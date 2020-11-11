package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ ChallengeId, UserChallengeSummaryId, UserId }
import agh.edu.pl.models.{ Entity, JsonSerializable }

case class UserChallengeSummary(
    override val id: UserChallengeSummaryId,
    userId: UserId,
    challengeId: ChallengeId,
    summaryValue: Double,
    // None indicates that no calculation occurred
    position: Option[Int],
    lastActive: Option[OffsetDateTime] = None
  ) extends Entity[UserChallengeSummaryId](id) {
  override type IdType = UserChallengeSummaryId
}

case object UserChallengeSummary extends JsonSerializable[UserChallengeSummary]
