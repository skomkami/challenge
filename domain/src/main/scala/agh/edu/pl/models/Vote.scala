package agh.edu.pl.models

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ LinkId, UserId, VoteId }

case class Vote(
    override val id: VoteId,
    createdAt: OffsetDateTime = OffsetDateTime.now,
    userId: UserId,
    linkId: LinkId
  ) extends Entity[VoteId](id) {
  override type IdType = VoteId
}

case object Vote extends JsonSerializable[Vote]
