package agh.edu.pl.mutations

import agh.edu.pl.calculator.ChallengePositionsCalculator
import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{
  Challenge,
  EntityIdSettings,
  User,
  UserChallengeSummary
}
import agh.edu.pl.ids.{ ChallengeId, UserChallengeSummaryId, UserId }
import agh.edu.pl.measures.MeasureValue
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }

case class JoinChallenge(
    userId: UserId,
    challengeId: ChallengeId
  ) extends CreateEntity[UserChallengeSummary] {

  override def toEntity(
      newId: UserChallengeSummaryId
    ): UserChallengeSummary =
    UserChallengeSummary(
      id = newId,
      userId = userId,
      challengeId = challengeId,
      summaryValue = MeasureValue.empty,
      position = None,
      lastActive = None
    )

  override def createNewEntity(
      ctx: Context,
      newId: UserChallengeSummaryId
    ): Future[UserChallengeSummary] = {
    implicit val ec: ExecutionContext = ctx.ec
    val newSummary = for {
      _ <- ctx.repository.getById[User](userId)
      challenge <- ctx.repository.getById[Challenge](challengeId)
      _ <- challenge.userHasAccess(ctx.repository, userId)
      created <- ctx
        .repository
        .create[UserChallengeSummary](toEntity(newId))
    } yield challenge.checkAvailabilityAndReturn(created)

    newSummary.onComplete(
      ChallengePositionsCalculator(challengeId).processWhenSuccess(ctx)
    )
    newSummary
  }

  override def updateEntity(
      ctx: Context,
      entity: UserChallengeSummary
    ): Future[UserChallengeSummary] =
    throw EntityAlreadyExists(
      s"User: $userId already attends the challenge: $challengeId"
    )

  override def generateId: UserChallengeSummaryId =
    UserChallengeSummaryId.generateId(
      UserChallengeSummaryId.DataToGenerateId(challengeId, userId)
    )

  override def id: Option[UserChallengeSummaryId] = None
}

case object JoinChallenge
    extends EntityCommandSettings[UserChallengeSummary, JoinChallenge] {
  import sangria.marshalling.circe._

  lazy val JoinChallengeInputType: InputObjectType[JoinChallenge] =
    deriveInputObjectType[JoinChallenge]()

  lazy val CommandInput: Argument[JoinChallenge] =
    Argument("input", JoinChallengeInputType)

  override def idSettings: EntityIdSettings[UserChallengeSummaryId] =
    UserChallengeSummaryId
}
