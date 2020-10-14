//package agh.edu.pl.entities
//
//import java.time.OffsetDateTime
//
//import agh.edu.pl.Entity
//import agh.edu.pl.measures.Measure
//
//case class UserChallengeSummary[UserChallengeSummaryId, UserId, ChallengeId, U](
//    override val id: UserChallengeSummaryId,
//    userId: UserId,
//    challengeId: ChallengeId,
//    summaryValue: Measure[U],
//    lastActive: OffsetDateTime
//  ) extends Entity[UserChallengeSummaryId](id) {}
