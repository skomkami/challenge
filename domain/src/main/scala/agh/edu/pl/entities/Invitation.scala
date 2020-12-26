package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ ChallengeId, InvitationId, UserId }

final case class Invitation(
    override val id: InvitationId,
    forUserId: UserId,
    fromUserId: UserId,
    toChallengeId: ChallengeId,
    read: Boolean,
    sendTime: OffsetDateTime
  ) extends Entity[InvitationId](id) {
  override type IdType = InvitationId
}

case object Invitation extends JsonSerializable[Invitation]
