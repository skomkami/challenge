package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ ChallengeId, InvitationId, UserId }
import agh.edu.pl.models.{ Entity, JsonSerializable }

final case class Invitation(
    override val id: InvitationId,
    forUser: UserId,
    fromUser: UserId,
    toChallenge: ChallengeId,
    read: Boolean,
    sendTime: OffsetDateTime
  ) extends Entity[InvitationId](id) {
  override type IdType = InvitationId
}

case object Invitation extends JsonSerializable[Invitation]
