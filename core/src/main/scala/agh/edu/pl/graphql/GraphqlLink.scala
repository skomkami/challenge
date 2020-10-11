package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{
  gqlOffsetDateTime,
  IdentifiableType,
  UserType,
  VoteType
}
import agh.edu.pl.context.Context
import agh.edu.pl.models.{ Link, User, Vote }
import agh.edu.pl.mutations.CreateLink
import agh.edu.pl.repository.Filter
import sangria.macros.derive.{
  deriveObjectType,
  AddFields,
  Interfaces,
  ReplaceField
}
import sangria.schema.{ Field, ListType, ObjectType }

case class GraphqlLink() extends GraphqlType[Link] {
  override def createEntitySettings: CreateLink.type = CreateLink

  override def GraphQLOutputType: ObjectType[Context, Link] =
    deriveObjectType[Context, Link](
      Interfaces(IdentifiableType),
      ReplaceField(
        "createdAt",
        Field("createdAt", gqlOffsetDateTime, resolve = _.value.createdAt)
      ),
      ReplaceField(
        "postedBy",
        Field(
          "postedBy",
          UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.postedBy)
        )
      ),
      AddFields(
        Field(
          "votes",
          ListType(VoteType),
          resolve = c =>
            c.ctx
              .repository
              .getAll[Vote](Some(Filter("linkId", c.value.id)))
        )
      )
    )
}
