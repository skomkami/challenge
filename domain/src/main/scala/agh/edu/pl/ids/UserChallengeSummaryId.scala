package agh.edu.pl.ids

import agh.edu.pl.models.{ EntityId, EntityIdSettings }

case class UserChallengeSummaryId(override val value: String) extends EntityId

case object UserChallengeSummaryId
    extends EntityIdSettings[UserChallengeSummaryId] {
  override implicit def fromString(value: String): UserChallengeSummaryId =
    UserChallengeSummaryId(value)

  override type PK = DataToGenerateId
  case class DataToGenerateId(
      challengeId: ChallengeId,
      userId: UserId
    )

}
