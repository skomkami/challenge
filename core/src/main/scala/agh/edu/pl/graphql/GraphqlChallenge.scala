package agh.edu.pl.graphql

import agh.edu.pl.schema.GraphQLSchema.{
  GraphQLUserChallengeSummary,
  Offset,
  Size,
  UserChallengeSummaryType,
  UserType
}
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, User, UserChallengeSummary }
import agh.edu.pl.filters.FilterEq
import agh.edu.pl.ids.ChallengeId
import agh.edu.pl.mutations.CreateChallenge
import cats.implicits._
import sangria.macros.derive.{ deriveObjectType, AddFields }
import sangria.schema.{ Field, ObjectType, OptionType }

case class GraphqlChallenge() extends GraphqlEntity[ChallengeId, Challenge] {
  override def createEntitySettings: CreateChallenge.type = CreateChallenge

  override def GraphQLOutputType: ObjectType[Context, Challenge] =
    deriveObjectType[Context, Challenge](
      AddFields(
        Field(
          name = "createdBy",
          fieldType = UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.createdById)
        ),
        Field(
          name = "summaries",
          fieldType = searchResponse(UserChallengeSummaryType),
          arguments = List(Size, Offset, GraphQLUserChallengeSummary.sortArg),
          resolve = c =>
            c.ctx
              .repository
              .getAll[UserChallengeSummary](
                filters =
                  Some(FilterEq("challengeId", c.value.id.value) :: Nil),
                sorts = c.arg(GraphQLUserChallengeSummary.sortArg).map(_.sorts),
                size = c.arg(Size),
                from = c.arg(Offset)
              )
        )
      ),
      AddFields(
        Field(
          name = "leader",
          fieldType = OptionType(UserType),
          resolve = c => {
            implicit val ec = c.ctx.ec
            val repository = c.ctx.repository

            repository
              .getAll[UserChallengeSummary](
                filters = Some(
                  FilterEq("challengeId", c.value.id.value) :: FilterEq(
                    "position",
                    "1"
                  ) :: Nil
                )
              )
              .map(_.results.headOption.map(_.userId))
              .flatMap(_.traverse(repository.getById[User]))
          }
        )
      )
    )
}
