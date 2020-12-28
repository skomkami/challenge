package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.error.DomainError
import agh.edu.pl.ids.{ ChallengeId, InvitationId, UserId }
import agh.edu.pl.measures.Measure
import agh.edu.pl.models.Accessibility
import agh.edu.pl.models.Accessibility.Public
import agh.edu.pl.repository.Repository

import scala.concurrent.{ ExecutionContext, Future }

final case class Challenge(
    override val id: ChallengeId,
    name: String,
    description: String,
    createdById: UserId,
    createdOn: OffsetDateTime,
    finishesOn: OffsetDateTime,
    measure: Measure,
    accessibility: Accessibility
  ) extends Entity[ChallengeId](id) {
  override type IdType = ChallengeId

  def checkAvailabilityAndReturn[R](
      returnValue: R,
      validationDate: Option[OffsetDateTime] = None
    ): R = {
    val date = validationDate.getOrElse(OffsetDateTime.now)
    if (finishesOn.isBefore(date)) {
      throw ChallengeInactive(name)
    }
    else returnValue
  }

  def userHasAccess(
      repository: Repository,
      userId: UserId
    )(implicit
      ec: ExecutionContext
    ): Future[Boolean] =
    if (accessibility == Public || createdById == userId) {
      Future.successful(true)
    }
    else {
      val invitationId = InvitationId.generateId(
        InvitationId.DataToGenerateId(userId, id)
      )
      repository.getByIdOpt[Invitation](invitationId).map(_.isDefined).flatMap {
        result =>
          if (!result)
            Future
              .failed(throw NoPermissionsToChallenge(userId, name))
          else Future.successful(true)
      }
    }

  case class ChallengeInactive(challengeName: String)
      extends DomainError(
        s"Challenge $challengeName has finished."
      )

  case class NoPermissionsToChallenge(userId: UserId, challenge: String)
      extends DomainError(
        s"User with id: ${userId.value} doesn't have permissions to access challenge: $challenge"
      )
}

case object Challenge extends JsonSerializable[Challenge]
