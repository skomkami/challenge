package agh.edu.pl.ids

import agh.edu.pl.models.{ EntityId, EntityIdSettings }

case class VoteId(override val value: String) extends EntityId

object VoteId extends EntityIdSettings[VoteId] {
  override implicit def fromString(value: String): VoteId = VoteId(value)

  override type PK = DeterministicID
  case class DeterministicID(userId: UserId, linkId: LinkId)
}
