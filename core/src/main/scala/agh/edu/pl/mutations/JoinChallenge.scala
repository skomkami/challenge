package agh.edu.pl.mutations

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, UserChallengeSummary }
import agh.edu.pl.ids.{ ChallengeId, UserChallengeSummaryId, UserId }
import agh.edu.pl.models.{ EntityIdSettings, User }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }

case class JoinChallenge(
    userId: UserId,
    challengeId: ChallengeId
  ) extends CreateEntity[UserChallengeSummary] {

  override def toEntity(newId: UserChallengeSummaryId): UserChallengeSummary =
    UserChallengeSummary(
      id = newId,
      userId = userId,
      challengeId = challengeId,
      summaryValue = 0,
      lastActive = None
    )

  override def newEntity(
      ctx: Context,
      newId: UserChallengeSummaryId
    ): Future[UserChallengeSummary] = {
    implicit val ec: ExecutionContext = ctx.ec
    for {
      _ <- ctx.repository.getById[User](userId)
      _ <- ctx.repository.getById[Challenge](challengeId)
      created <- ctx.repository.create[UserChallengeSummary](toEntity(newId))
    } yield created
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
    extends CreateEntitySettings[UserChallengeSummary, JoinChallenge] {
  import sangria.marshalling.circe._

  lazy val JoinChallengeInputType: InputObjectType[JoinChallenge] =
    deriveInputObjectType[JoinChallenge]()

  lazy val CreateEntityInput: Argument[JoinChallenge] =
    Argument("input", JoinChallengeInputType)

  override def idCodec: EntityIdSettings[UserChallengeSummaryId] =
    UserChallengeSummaryId
}
