package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ gqlOffsetDateTime, ChallengeType, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, UserChallengeActivity }
import agh.edu.pl.filters.UserChallengeActivitiesFilter
import agh.edu.pl.ids.UserChallengeActivityId
import agh.edu.pl.models.User
import agh.edu.pl.mutations.LogActivity
import com.softwaremill.quicklens._
import sangria.macros.derive.{ deriveObjectType, ReplaceField }
import sangria.schema.{ Field, ObjectType }

case class GraphqlUserChallengeActivity()
    extends GraphqlEntity[UserChallengeActivityId, UserChallengeActivity] {
  override def createEntitySettings: LogActivity.type =
    LogActivity

  override def createMutation: Field[Context, Unit] =
    super.createMutation.modify(_.name).setTo("logActivity")

  override lazy val filterSettings: UserChallengeActivitiesFilter.type =
    UserChallengeActivitiesFilter

  override def GraphQLOutputType: ObjectType[Context, UserChallengeActivity] =
    deriveObjectType[Context, UserChallengeActivity](
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
