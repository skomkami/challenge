package agh.edu.pl.registry

import agh.edu.pl.ids._
import agh.edu.pl.models.EntityIdSettings

case object DomainRegistry {
  lazy val idsSettings: Map[String, EntityIdSettings[_]] = Map(
    "UserId" -> UserId,
    "ChallengeId" -> ChallengeId,
    "InvitationId" -> InvitationId,
    "UserChallengeSummaryId" -> UserChallengeSummaryId,
    "UserChallengeActivityId" -> UserChallengeActivityId
  )
}
