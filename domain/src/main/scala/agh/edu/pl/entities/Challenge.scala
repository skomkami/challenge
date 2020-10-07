package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.Entity
import agh.edu.pl.ids.UserId
import agh.edu.pl.measures.Measure

/**
  * @tparam ChallengeId - id type
  * @tparam U - type representing measure unit
  */

final case class Challenge[ChallengeId, U](
    override val id: ChallengeId,
    createdBy: UserId,
    createdOn: OffsetDateTime,
    finishesOn: OffsetDateTime,
    measure: Measure[U]
  ) extends Entity[ChallengeId](id) {
  override protected type SelfType = Challenge[ChallengeId, U]
  override protected type Id = ChallengeId

}
