package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ ChallengeId, UserId }
import agh.edu.pl.measures.Measure
import agh.edu.pl.models._

final case class Challenge(
    override val id: ChallengeId,
    name: String,
    description: String,
    createdBy: UserId,
    createdOn: OffsetDateTime,
    finishesOn: OffsetDateTime,
    measure: Measure
  ) extends Entity[ChallengeId](id) {
  override type IdType = ChallengeId
}

case object Challenge extends JsonSerializable[Challenge]
