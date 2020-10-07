package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{
  gqlOffsetDateTime,
  IdentifiableType,
  LinkType,
  VoteType
}
import agh.edu.pl.context.Context
import agh.edu.pl.models.{ Link, User, Vote }
import agh.edu.pl.mutations.CreateUser
import agh.edu.pl.repository.Filter
import sangria.macros.derive.{
  deriveObjectType,
  AddFields,
  Interfaces,
  ReplaceField
}
import sangria.schema.{ Field, ListType, ObjectType }

case class GraphqlUser() extends GraphqlType[User] {
  override def createEntitySettings: CreateUser.type = CreateUser

  override def GraphQLOutputType: ObjectType[Context, User] =
    deriveObjectType[Context, User](
      Interfaces(IdentifiableType),
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
                Some(Filter("postedBy", c.value.id.toString))
              )
        ),
        Field(
          "votes",
          ListType(VoteType),
          resolve = c =>
            c.ctx
              .repository
              .getAll[Vote](Some(Filter("userId", c.value.id.toString)))
        )
      )
    )

}
