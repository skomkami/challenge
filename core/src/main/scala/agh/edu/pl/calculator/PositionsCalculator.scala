package agh.edu.pl.calculator

import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, EntityId, UserChallengeSummary }
import agh.edu.pl.filters.FilterEq
import agh.edu.pl.ids.ChallengeId
import agh.edu.pl.measures.Measure
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

case object PositionsCalculator {
  val behavior: Behavior[UpdatePositionsProtocol] = Behaviors.receive {
    (context, message) =>
      message match {
        case RunPositionCalculation(ctx, updatedSummary) =>
          context.pipeToSelf(updatedSummary) {
            case Success(summary) =>
              RefreshSummariesIndex(ctx, summary.challengeId)
            case Failure(exception) =>
              LogFailure(exception)
          }
          Behaviors.same
        case RefreshSummariesIndex(ctx, challengeId) =>
          val refreshResult = ctx
            .repository
            //force refresh before position calculator
            .forceRefresh[UserChallengeSummary]

          context.pipeToSelf(refreshResult) {
            case Success(_) =>
              CalculatePositions(ctx, challengeId)
            case Failure(exception) =>
              LogFailure(exception)
          }
          Behaviors.same
        case CalculatePositions(ctx, challengeId) =>
          sortSummaries(challengeId, ctx)(ctx.ec)
          Behaviors.same
        case LogFailure(ex) =>
          scribe.warn(ex.getMessage)
          Behaviors.same
      }
  }

  private def sortSummaries(
      challengeId: ChallengeId,
      ctx: Context
    )(implicit
      ec: ExecutionContext
    ): Future[Seq[EntityId]] = {
    val summariesFuture = for {
      allCount <- ctx.repository.allCount[UserChallengeSummary]
      summariesResponse <- ctx
        .repository
        .getAll[UserChallengeSummary](
          filters = Some(FilterEq("challengeId", challengeId.value) :: Nil),
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
