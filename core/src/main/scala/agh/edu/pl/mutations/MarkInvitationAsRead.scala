package agh.edu.pl.mutations

import agh.edu.pl.commands.UpdateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities.Invitation
import agh.edu.pl.ids.InvitationId
import agh.edu.pl.entities.EntityIdSettings
import sangria.schema.{ Argument, InputObjectType }
import sangria.macros.derive.deriveInputObjectType

import scala.concurrent.Future

case class MarkInvitationAsRead(id: InvitationId)
    extends UpdateEntity[Invitation](id) {
  override def update(ctx: Context, entity: Invitation): Future[Invitation] = {
    val markedAsRead = entity.copy(read = true)
    ctx.repository.update(markedAsRead)
  }
}

case object MarkInvitationAsRead
    extends EntityCommandSettings[Invitation, MarkInvitationAsRead] {
  import sangria.marshalling.circe._

  lazy val MarkInvitationAsReadInputType
      : InputObjectType[MarkInvitationAsRead] =
    deriveInputObjectType[MarkInvitationAsRead]()

  lazy val CommandInput: Argument[MarkInvitationAsRead] =
    Argument("input", MarkInvitationAsReadInputType)

  override def idSettings: EntityIdSettings[InvitationId] =
    InvitationId
}
