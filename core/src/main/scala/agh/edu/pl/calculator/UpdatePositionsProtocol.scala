package agh.edu.pl.calculator

import agh.edu.pl.context.Context
import agh.edu.pl.entities.UserChallengeSummary
import agh.edu.pl.ids.ChallengeId

import scala.concurrent.Future

sealed trait UpdatePositionsProtocol

case class RunPositionCalculation(
    ctx: Context,
    updatedSummary: Future[UserChallengeSummary]
  ) extends UpdatePositionsProtocol

private[calculator] case class RefreshSummariesIndex(
    ctx: Context,
    challengeId: ChallengeId
  ) extends UpdatePositionsProtocol

private[calculator] case class CalculatePositions(
    ctx: Context,
    challengeId: ChallengeId
  ) extends UpdatePositionsProtocol

private[calculator] case class LogFailure(
    reason: Throwable
  ) extends UpdatePositionsProtocol
