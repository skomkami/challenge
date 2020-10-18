package agh.edu.pl.ids

import java.time.OffsetDateTime

import agh.edu.pl.models.{ EntityId, EntityIdSettings }

case class UserChallengeActivityId(override val value: String) extends EntityId

case object UserChallengeActivityId
    extends EntityIdSettings[UserChallengeActivityId] {
  override implicit def fromString(value: String): UserChallengeActivityId =
    UserChallengeActivityId(
      value
    )

  override type PK = DataToGenerateId
  case class DataToGenerateId(
      challengeId: ChallengeId,
      userId: UserId,
      date: OffsetDateTime
    )

}
