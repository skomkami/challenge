package agh.edu.pl.calculator

import agh.edu.pl.context.Context
import agh.edu.pl.entities.UserChallengeSummary
import agh.edu.pl.filters.FilterEq
import agh.edu.pl.ids.ChallengeId
import agh.edu.pl.models.EntityId

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

case class ChallengePositionsCalculator(challengeId: ChallengeId) {

  def process(ctx: Context): Unit = {
    implicit val ec = ctx.ec
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
