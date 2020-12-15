package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ ChallengeType, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, User, UserChallengeSummary }
import agh.edu.pl.ids.UserChallengeSummaryId
import agh.edu.pl.mutations.JoinChallenge
import com.softwaremill.quicklens._
import sangria.macros.derive.{ deriveObjectType, ReplaceField }
import sangria.schema.{ Field, ObjectType }

case class GraphqlUserChallengeSummary()
    extends GraphqlEntity[UserChallengeSummaryId, UserChallengeSummary] {
  override def createEntitySettings: JoinChallenge.type =
    JoinChallenge

  override def createMutation: Field[Context, Unit] =
    super.createMutation.modify(_.name).setTo("joinChallenge")

  override def GraphQLOutputType: ObjectType[Context, UserChallengeSummary] =
    deriveObjectType[Context, UserChallengeSummary](
      ReplaceField(
        "userId",
        Field(
          "user",
          UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.userId)
        )
      ),
      ReplaceField(
        "challengeId",
        Field(
          "challenge",
          ChallengeType,
          resolve =
            c => c.ctx.repository.getById[Challenge](c.value.challengeId)
        )
      )
    )
}
