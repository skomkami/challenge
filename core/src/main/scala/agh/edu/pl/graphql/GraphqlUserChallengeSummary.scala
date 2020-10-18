package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ gqlOffsetDateTime, ChallengeType, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, UserChallengeSummary }
import agh.edu.pl.filters.UserChallengeSummariesFilter
import agh.edu.pl.ids.UserChallengeSummaryId
import agh.edu.pl.models.User
import agh.edu.pl.mutations.JoinChallenge
import sangria.macros.derive.{ deriveObjectType, ReplaceField }
import sangria.schema.{ Field, ObjectType }
import com.softwaremill.quicklens._

case class GraphqlUserChallengeSummary()
    extends GraphqlEntity[UserChallengeSummaryId, UserChallengeSummary] {
  override def createEntitySettings: JoinChallenge.type =
    JoinChallenge

  override def createMutation: Field[Context, Unit] =
    super.createMutation.modify(_.name).setTo("joinChallenge")

  override lazy val filterSettings: UserChallengeSummariesFilter.type =
    UserChallengeSummariesFilter

  override def GraphQLOutputType: ObjectType[Context, UserChallengeSummary] =
    deriveObjectType[Context, UserChallengeSummary](
      ReplaceField(
        "userId",
        Field(
          "userId",
          UserType,
          resolve = c => c.ctx.repository.getById[User](c.value.userId)
        )
      ),
      ReplaceField(
        "challengeId",
        Field(
          "challengeId",
          ChallengeType,
          resolve =
            c => c.ctx.repository.getById[Challenge](c.value.challengeId)
        )
      )
    )
}