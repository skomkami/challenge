package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ gqlOffsetDateTime, LinkType, VoteType }
import agh.edu.pl.context.Context
import agh.edu.pl.filters.{ FilterEq, UsersFilter }
import agh.edu.pl.ids.UserId
import agh.edu.pl.models.{ Link, User, Vote }
import agh.edu.pl.mutations.CreateUser
import sangria.macros.derive.{ deriveObjectType, AddFields, ReplaceField }
import sangria.schema.{ Field, ListType, ObjectType }

case class GraphqlUser() extends GraphqlEntity[UserId, User] {
  override def createEntitySettings: CreateUser.type = CreateUser

  override lazy val filterSettings: UsersFilter.type = UsersFilter

  override def GraphQLOutputType: ObjectType[Context, User] =
    deriveObjectType[Context, User](
//      Interfaces(EntityType),
      ReplaceField(
        "createdAt",
        Field("createdAt", gqlOffsetDateTime, resolve = _.value.createdAt)
      ),
      AddFields(
        Field(
          "links",
          ListType(LinkType),
          resolve = c =>
            c.ctx
              .repository
              .getAll[Link](
                Some(FilterEq("postedBy", c.value.id.value) :: Nil)
              )
        ),
        Field(
          "votes",
          ListType(VoteType),
          resolve = c =>
            c.ctx
              .repository
              .getAll[Vote](Some(FilterEq("userId", c.value.id.value) :: Nil))
        )
      )
    )
}
