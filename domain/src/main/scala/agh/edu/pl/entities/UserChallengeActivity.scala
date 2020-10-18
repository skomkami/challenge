package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ ChallengeId, UserChallengeActivityId, UserId }
import agh.edu.pl.models.{ Entity, JsonSerializable }

final case class UserChallengeActivity(
    override val id: UserChallengeActivityId,
    userId: UserId,
    challengeId: ChallengeId,
    value: Double,
    date: OffsetDateTime
  ) extends Entity[UserChallengeActivityId](id) {
  override type IdType = UserChallengeActivityId
}
case object UserChallengeActivity
    extends JsonSerializable[UserChallengeActivity]
