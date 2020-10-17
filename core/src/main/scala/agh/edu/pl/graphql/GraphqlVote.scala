package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ gqlOffsetDateTime, LinkType, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.filters.VotesFilter
import agh.edu.pl.ids.VoteId
import agh.edu.pl.models.{ Link, User, Vote }
import agh.edu.pl.mutations.CreateVote
import sangria.macros.derive._
import sangria.schema.{ Field, ObjectType }

case class GraphqlVote() extends GraphqlEntity[VoteId, Vote] {

  override def createEntitySettings: CreateVote.type = CreateVote
  override lazy val filterSettings: VotesFilter.type = VotesFilter

  override def GraphQLOutputType: ObjectType[Context, Vote] =
    deriveObjectType[Context, Vote](
//      Interfaces(EntityType),
      ReplaceField(
        "createdAt",
        Field("createdAt", gqlOffsetDateTime, resolve = _.value.createdAt)
      ),
      ExcludeFields("userId", "linkId"),
      AddFields(
        Field(
          "user",
          UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.userId)
        ),
        Field(
          "link",
          LinkType,
          resolve = c => c.ctx.repository.getById[Link](c.value.linkId)
        )
      )
    )
}
