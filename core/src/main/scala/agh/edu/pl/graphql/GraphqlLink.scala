package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ gqlOffsetDateTime, UserType, VoteType }
import agh.edu.pl.context.Context
import agh.edu.pl.filters.{ FilterEq, LinksFilter }
import agh.edu.pl.ids.LinkId
import agh.edu.pl.models.{ Link, User, Vote }
import agh.edu.pl.mutations.CreateLink
import sangria.macros.derive.{ deriveObjectType, AddFields, ReplaceField }
import sangria.schema.{ Field, ListType, ObjectType }

case class GraphqlLink() extends GraphqlEntity[LinkId, Link] {
  override def createEntitySettings: CreateLink.type = CreateLink

  override lazy val filterSettings: LinksFilter.type = LinksFilter

  override def GraphQLOutputType: ObjectType[Context, Link] =
    deriveObjectType[Context, Link](
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
              .getAll[Vote](Some(FilterEq("linkId", c.value.id.value) :: Nil))
        )
      )
    )
}
