package agh.edu.pl.ids

import agh.edu.pl.entities.{ EntityId, EntityIdSettings }

case class ChallengeId(override val value: String) extends EntityId

object ChallengeId extends EntityIdSettings[ChallengeId] {
  override implicit def fromString(value: String): ChallengeId = ChallengeId(
    value
  )

  override type PK = DataToGenerateId
  case class DataToGenerateId(name: String)
}
