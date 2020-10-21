package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{
  gqlOffsetDateTime,
  UserChallengeActivityType,
  UserChallengeSummaryType
}
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ User, UserChallengeActivity, UserChallengeSummary }
import agh.edu.pl.filters.{ FilterEq, UsersFilter }
import agh.edu.pl.ids.UserId
import agh.edu.pl.mutations.CreateUser
import sangria.macros.derive.{ deriveObjectType, AddFields }
import sangria.schema.{ Field, ListType, ObjectType }

case class GraphqlUser() extends GraphqlEntity[UserId, User] {
  override def createEntitySettings: CreateUser.type = CreateUser

  override lazy val filterSettings: UsersFilter.type = UsersFilter

  override def GraphQLOutputType: ObjectType[Context, User] =
    deriveObjectType[Context, User](
//      Interfaces(EntityType),
      AddFields(
        Field(
          "activities",
          ListType(UserChallengeActivityType),
          resolve = c =>
            c.ctx
              .repository
              .getAll[UserChallengeActivity](
                Some(FilterEq("userId", c.value.id.value) :: Nil)
              )
        ),
        Field(
          "votes",
          ListType(UserChallengeSummaryType),
          resolve = c =>
            c.ctx
              .repository
              .getAll[UserChallengeSummary](
                Some(FilterEq("challengeId", c.value.id.value) :: Nil)
              )
        )
      )
    )
}
