package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ ChallengeType, UserType }
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, User, UserChallengeActivity }
import agh.edu.pl.ids.UserChallengeActivityId
import agh.edu.pl.mutations.LogActivity
import com.softwaremill.quicklens._
import sangria.macros.derive.{ deriveObjectType, AddFields }
import sangria.schema.{ Field, ObjectType }

case class GraphqlUserChallengeActivity()
    extends GraphqlEntity[UserChallengeActivityId, UserChallengeActivity] {
  override def createEntitySettings: LogActivity.type =
    LogActivity

  override def createMutation: Field[Context, Unit] =
    super.createMutation.modify(_.name).setTo("logActivity")

  override def GraphQLOutputType: ObjectType[Context, UserChallengeActivity] =
    deriveObjectType[Context, UserChallengeActivity](
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
