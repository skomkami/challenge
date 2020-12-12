package agh.edu.pl.calculator

import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, UserChallengeSummary }
import agh.edu.pl.filters.FilterEq
import agh.edu.pl.ids.ChallengeId
import agh.edu.pl.measures.Measure
import agh.edu.pl.models.EntityId

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

case class ChallengePositionsCalculator(challengeId: ChallengeId) {

  def processWhenSuccess[E](ctx: Context)(prevCommand: Try[E]): Unit =
    prevCommand match {
      case Success(_) =>
        process(ctx)
      case Failure(exception) =>
        scribe.error(
          s"Sorting skipped because of error: ${exception.getMessage}"
        )
    }

  protected def process(ctx: Context): Unit = {
    implicit val ec = ctx.ec
    val _ = ctx
      .repository
      //force refresh before position calculator
      //TODO check refresh wait_for in documentation
      .forceRefresh[UserChallengeSummary]
      .andThen { _ =>
        sortSummaries(ctx)
          .onComplete {
            case Success(_) =>
              scribe.info(
                s"Successfully updated positions for challenge: $challengeId"
              )
            case Failure(exception) =>
              scribe.error(
                s"There was an error updating positions for challenge: $challengeId. Msg: ${exception.getMessage}"
              )
          }
      }
  }

  private def sortSummaries(
      ctx: Context
    )(implicit
      ec: ExecutionContext
    ): Future[Seq[EntityId]] = {
    val summariesFuture = for {
      allCount <- ctx.repository.allCount[UserChallengeSummary]
      summariesResponse <- ctx
        .repository
        .getAll[UserChallengeSummary](
          filter = Some(FilterEq("challengeId", challengeId.value) :: Nil),
          size = Some(allCount.toInt)
        )
    } yield summariesResponse.results

    for {
      challenge <- ctx.repository.getById[Challenge](challengeId)
      summaries <- summariesFuture
      sortedSummaries = calculatePositions(challenge.measure)(summaries)
      sortedIds <- ctx.repository.updateMany(sortedSummaries)
    } yield sortedIds
  }

  private def calculatePositions(
      measure: Measure
    )(
      summaries: Seq[UserChallengeSummary]
    ): Seq[UserChallengeSummary] =
    summaries
      .sortBy(_.summaryValue)(measure.ordering)
      .zipWithIndex
      .map {
        case (summary, position) => summary.copy(position = Some(position + 1))
      }
}
