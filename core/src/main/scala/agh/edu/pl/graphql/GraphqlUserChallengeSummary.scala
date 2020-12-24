package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ ChallengeType, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, User, UserChallengeSummary }
import agh.edu.pl.ids.UserChallengeSummaryId
import agh.edu.pl.mutations.JoinChallenge
import sangria.macros.derive.{ deriveObjectType, AddFields }
import sangria.schema.{ Field, ObjectType }

case class GraphqlUserChallengeSummary()
    extends GraphqlEntity[UserChallengeSummaryId, UserChallengeSummary] {
  override def createEntitySettings: JoinChallenge.type =
    JoinChallenge

  override def GraphQLOutputType: ObjectType[Context, UserChallengeSummary] =
    deriveObjectType[Context, UserChallengeSummary](
      AddFields(
        Field(
          name = "user",
          fieldType = UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.userId)
        ),
        Field(
          name = "challenge",
          fieldType = ChallengeType,
          resolve =
            c => c.ctx.repository.getById[Challenge](c.value.challengeId)
        )
      )
    )
}
