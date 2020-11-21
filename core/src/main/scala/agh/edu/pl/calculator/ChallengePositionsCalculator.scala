package agh.edu.pl.calculator

import agh.edu.pl.context.Context
import agh.edu.pl.entities.UserChallengeSummary
import agh.edu.pl.filters.FilterEq
import agh.edu.pl.ids.ChallengeId
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
    ): Future[Seq[EntityId]] =
    ctx
      .repository
      .getAll[UserChallengeSummary](filter =
        Some(FilterEq("challengeId", challengeId.value) :: Nil)
      )
      .map(calculatePositions)
      .flatMap { sorted =>
        ctx.repository.updateMany(sorted)
      }

  private def calculatePositions(
      summaries: Seq[UserChallengeSummary]
    ): Seq[UserChallengeSummary] =
    summaries
      .sortBy(_.summaryValue)(Ordering[Double].reverse)
      .zipWithIndex
      .map {
        case (summary, position) => summary.copy(position = Some(position + 1))
      }
}
