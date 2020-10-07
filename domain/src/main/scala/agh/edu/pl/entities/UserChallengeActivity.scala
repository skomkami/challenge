package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.Entity
import agh.edu.pl.measures.Measure

case class UserChallengeActivity[UserActivityId, UserId, ChallengeId, U](
    override val id: UserActivityId,
    userId: UserId,
    challengeId: ChallengeId,
    value: Measure[U],
    date: OffsetDateTime
  ) extends Entity[UserActivityId](id) {}
