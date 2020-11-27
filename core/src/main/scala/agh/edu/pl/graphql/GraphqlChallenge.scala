package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{
  gqlOffsetDateTime,
  Offset,
  Size,
  UserChallengeSummaryType,
  UserType
}
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, User, UserChallengeSummary }
import agh.edu.pl.filters.{ ChallengesFilter, FilterEq }
import agh.edu.pl.ids.ChallengeId
import agh.edu.pl.mutations.CreateChallenge
import sangria.macros.derive.{ deriveObjectType, AddFields, ReplaceField }
import sangria.schema.{ Field, ListType, ObjectType }

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
      ),
      AddFields(
        Field(
          name = "summaries",
          fieldType = ListType(UserChallengeSummaryType),
          arguments = List(Size, Offset),
          resolve = c =>
            c.ctx
              .repository
              .getAll[UserChallengeSummary](
                filter = Some(FilterEq("challengeId", c.value.id.value) :: Nil),
                size = c.arg(Size),
                from = c.arg(Offset)
              )
        )
      )
    )
}
