package agh.edu.pl.ids

import agh.edu.pl.entities.{ EntityId, EntityIdSettings }

case class InvitationId(override val value: String) extends EntityId

object InvitationId extends EntityIdSettings[InvitationId] {
  override def fromString(value: String): InvitationId = InvitationId(
    value
  )

  override type PK = DataToGenerateId
  case class DataToGenerateId(
      forUser: UserId,
      toChallenge: ChallengeId
    )
}
