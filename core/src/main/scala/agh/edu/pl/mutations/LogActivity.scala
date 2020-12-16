package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.calculator.ChallengePositionsCalculator
import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{
  Challenge,
  UserChallengeActivity,
  UserChallengeSummary
}
import agh.edu.pl.error.DomainError
import agh.edu.pl.ids.{
  ChallengeId,
  UserChallengeActivityId,
  UserChallengeSummaryId,
  UserId
}
import agh.edu.pl.measures.{ Measure, MeasureValue }
import agh.edu.pl.models.EntityIdSettings
import agh.edu.pl.repository.Repository
import com.softwaremill.quicklens._
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }

case class LogActivity(
    userId: UserId,
    challengeId: ChallengeId,
    value: MeasureValue,
    date: Option[OffsetDateTime] = None
  ) extends CreateEntity[UserChallengeActivity] {

  override def toEntity(newId: UserChallengeActivityId): UserChallengeActivity =
    UserChallengeActivity(
      id = newId,
      userId = userId,
      challengeId = challengeId,
      value = value,
      date = date.getOrElse(OffsetDateTime.now)
    )

  override def createNewEntity(
      ctx: Context,
      newId: UserChallengeActivityId
    ): Future[UserChallengeActivity] = {
    implicit val ec: ExecutionContext = ctx.ec

    val summaryId = UserChallengeSummaryId.generateId(
      UserChallengeSummaryId.DataToGenerateId(challengeId, userId)
    )

    val createActivity =
      for {
        challenge <- ctx.repository.getById[Challenge](challengeId)
        summary <- ctx.repository.getById[UserChallengeSummary](summaryId)
        _ <- updateSummary(summary, challenge.measure, ctx.repository)
        created <- ctx
          .repository
          .create[UserChallengeActivity](toEntity(newId))
      } yield challenge.checkAvailabilityAndReturn(created, Some(created.date))

    createActivity.onComplete(
      ChallengePositionsCalculator(challengeId).processWhenSuccess(ctx)
    )
    createActivity
  }

  case class MeasureMismatch(missingValue: String)
      extends DomainError(
        s"Cannot log activity without $missingValue parameter"
      )

  private def updateSummary(
      existingSummary: UserChallengeSummary,
      measure: Measure,
      repository: Repository
    ): Future[UserChallengeSummary] = {

    if (
      measure.allowDecimal && value
        .decimalValue
        .isEmpty || !measure.allowDecimal && value.integerValue.isEmpty
    ) {
      val missingValue =
        if (measure.allowDecimal) "value.decimalValue" else "value.integerValue"
      throw MeasureMismatch(missingValue)
    }

    val updatedSummary =
      modify(existingSummary)(_.summaryValue)
        .using(measure.updateSummaryValueFn(value))
        .modify(_.lastActive)
        .setTo(date.orElse(Some(OffsetDateTime.now)))
    repository.update(updatedSummary)
  }

  override def updateEntity(
      ctx: Context,
      entity: UserChallengeActivity
    ): Future[UserChallengeActivity] =
    throw OperationNotSupported("Can't update existing activity")

  override def generateId: UserChallengeActivityId =
    UserChallengeActivityId.generateId(
      UserChallengeActivityId.DataToGenerateId(
        challengeId,
        userId,
        date.getOrElse(OffsetDateTime.now)
      )
    )

  override def id: Option[UserChallengeActivityId] = None
}

case object LogActivity
    extends EntityCommandSettings[UserChallengeActivity, LogActivity] {
  import sangria.marshalling.circe._

  lazy val LogActivityInputType: InputObjectType[LogActivity] =
    deriveInputObjectType[LogActivity]()

  lazy val CommandInput: Argument[LogActivity] =
    Argument("input", LogActivityInputType)

  override def idSettings: EntityIdSettings[UserChallengeActivityId] =
    UserChallengeActivityId
}
