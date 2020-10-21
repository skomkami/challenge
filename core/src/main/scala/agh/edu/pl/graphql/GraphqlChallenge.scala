package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ gqlOffsetDateTime, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, User }
import agh.edu.pl.filters.ChallengesFilter
import agh.edu.pl.ids.ChallengeId
import agh.edu.pl.mutations.CreateChallenge
import sangria.macros.derive.{ deriveObjectType, ReplaceField }
import sangria.schema.{ Field, ObjectType }

case class GraphqlChallenge() extends GraphqlEntity[ChallengeId, Challenge] {
  override def createEntitySettings: CreateChallenge.type = CreateChallenge

  override lazy val filterSettings: ChallengesFilter.type = ChallengesFilter

  override def GraphQLOutputType: ObjectType[Context, Challenge] =
    deriveObjectType[Context, Challenge](
      ReplaceField(
        "createdBy",
        Field(
          "createdBy",
          UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.createdBy)
        )
      )
    )
}
