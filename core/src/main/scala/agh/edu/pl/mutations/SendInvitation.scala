package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, Invitation, User }
import agh.edu.pl.ids.{ ChallengeId, InvitationId, UserId }
import agh.edu.pl.models.EntityIdSettings
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }

case class SendInvitation(
    forUser: UserId,
    fromUser: UserId,
    toChallenge: ChallengeId
  ) extends CreateEntity[Invitation] {

  override def toEntity(newId: InvitationId): Invitation =
    Invitation(
      id = newId,
      forUser = forUser,
      fromUser = fromUser,
      toChallenge = toChallenge,
      read = false,
      sendTime = OffsetDateTime.now
    )

  override def createNewEntity(
      ctx: Context,
      newId: InvitationId
    ): Future[Invitation] = {
    implicit val ec: ExecutionContext = ctx.ec

    for {
      _ <- ctx.repository.getById[User](forUser)
      _ <- ctx.repository.getById[User](fromUser)
      challenge <- ctx.repository.getById[Challenge](toChallenge)
      created <- ctx.repository.create(toEntity(newId))
    } yield challenge.checkAvailabilityAndReturn(created)
  }

  override def updateEntity(
      ctx: Context,
      entity: Invitation
    ): Future[Invitation] =
    throw OperationNotSupported("Can't update existing invitation")

  override def generateId: InvitationId =
    InvitationId.generateId(
      InvitationId.DataToGenerateId(
        forUser,
        toChallenge
      )
    )

  override def id: Option[InvitationId] = None
}

case object SendInvitation
    extends EntityCommandSettings[Invitation, SendInvitation] {
  import sangria.marshalling.circe._

  lazy val SendInvitationInputType: InputObjectType[SendInvitation] =
    deriveInputObjectType[SendInvitation]()

  lazy val CommandInput: Argument[SendInvitation] =
    Argument("input", SendInvitationInputType)

  override def idSettings: EntityIdSettings[InvitationId] =
    InvitationId
}
