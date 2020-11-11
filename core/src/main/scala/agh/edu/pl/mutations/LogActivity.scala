package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.calculator.ChallengePositionsCalculator
import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ UserChallengeActivity, UserChallengeSummary }
import agh.edu.pl.ids.{
  ChallengeId,
  UserChallengeActivityId,
  UserChallengeSummaryId,
  UserId
}
import agh.edu.pl.models.EntityIdSettings
import agh.edu.pl.repository.Repository
import com.softwaremill.quicklens._
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

case class LogActivity(
    userId: UserId,
    challengeId: ChallengeId,
    value: Double,
    date: OffsetDateTime = OffsetDateTime.now
  ) extends CreateEntity[UserChallengeActivity] {

  override def toEntity(newId: UserChallengeActivityId): UserChallengeActivity =
    UserChallengeActivity(
      id = newId,
      userId = userId,
      challengeId = challengeId,
      value = value,
      date = date
    )

  override def newEntity(
      ctx: Context,
      newId: UserChallengeActivityId
    ): Future[UserChallengeActivity] = {
    implicit val ec: ExecutionContext = ctx.ec

    val summaryId = UserChallengeSummaryId.generateId(
      UserChallengeSummaryId.DataToGenerateId(challengeId, userId)
    )

    val createActivity =
      for {
        summary <- ctx.repository.getById[UserChallengeSummary](summaryId)
        _ <- updateSummary(summary, ctx.repository)
        created <- ctx
          .repository
          .create[UserChallengeActivity](toEntity(newId))
      } yield created

    createActivity.onComplete {
      case Success(_) =>
        //force refresh before position calculator
        //TODO check refresh wait_for in documentation
        ctx.repository.forceRefresh[UserChallengeSummary].map { _ =>
          ChallengePositionsCalculator(challengeId).process(ctx)
        }
      case Failure(exception) =>
        scribe.error(
          s"Sorting skipped because of error: ${exception.getMessage}"
        )
    }
    createActivity
  }

  private def updateSummary(
      existingSummary: UserChallengeSummary,
      repository: Repository
    ): Future[UserChallengeSummary] = {
    val updatedSummary =
      modify(existingSummary)(_.summaryValue)
        .using(_ + value)
        .modify(_.lastActive)
        .setTo(Some(date))
    repository.update(updatedSummary)
  }

  override def updateEntity(
      ctx: Context,
      entity: UserChallengeActivity
    ): Future[UserChallengeActivity] =
    throw OperationNotSupported("Can't update existing activity")

  override def generateId: UserChallengeActivityId =
    UserChallengeActivityId.generateId(
      UserChallengeActivityId.DataToGenerateId(challengeId, userId, date)
    )

  override def id: Option[UserChallengeActivityId] = None
}

case object LogActivity
    extends CreateEntitySettings[UserChallengeActivity, LogActivity] {
  import sangria.marshalling.circe._

  lazy val LogActivityInputType: InputObjectType[LogActivity] =
    deriveInputObjectType[LogActivity]()

  lazy val CreateEntityInput: Argument[LogActivity] =
    Argument("input", LogActivityInputType)

  override def idCodec: EntityIdSettings[UserChallengeActivityId] =
    UserChallengeActivityId
}
