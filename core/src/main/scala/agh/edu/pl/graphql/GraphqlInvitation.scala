package agh.edu.pl.graphql

import agh.edu.pl.schema.GraphQLSchema.{ ChallengeType, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, Invitation, User }
import agh.edu.pl.ids.InvitationId
import agh.edu.pl.mutations.{ MarkInvitationAsRead, SendInvitation }
import com.softwaremill.quicklens._
import sangria.macros.derive.{ deriveObjectType, AddFields }
import sangria.schema.{ Field, ObjectType }

case class GraphqlInvitation() extends GraphqlEntity[InvitationId, Invitation] {
  override def createEntitySettings: SendInvitation.type =
    SendInvitation

  override def createMutation: Field[Context, Unit] =
    super.createMutation.modify(_.name).setTo("sendInvitation")

  override def GraphQLOutputType: ObjectType[Context, Invitation] =
    deriveObjectType[Context, Invitation](
      AddFields(
        Field(
          name = "forUser",
          fieldType = UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.forUserId)
        ),
        Field(
          name = "fromUser",
          fieldType = UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.fromUserId)
        ),
        Field(
          name = "toChallenge",
          fieldType = ChallengeType,
          resolve =
            c => c.ctx.repository.getById[Challenge](c.value.toChallengeId)
        )
      )
    )

  def markAsReadMutation: Field[Context, Unit] = Field(
    name = firstLetterToLower(MarkInvitationAsRead.getClass),
    fieldType = GraphQLOutputType,
    arguments = MarkInvitationAsRead.CommandInput :: Nil,
    resolve = c => c.arg(MarkInvitationAsRead.CommandInput).updateEntity(c.ctx)
  )

  override def mutations: Seq[Field[Context, Unit]] =
    super.mutations :+ markAsReadMutation
}
