package agh.edu.pl.ids

import agh.edu.pl.models.{ Email, EntityId, EntityIdSettings }

case class UserId(override val value: String) extends EntityId

object UserId extends EntityIdSettings[UserId] {
  override implicit def fromString(value: String): UserId = UserId(value)

  override type PK = DeterministicId
  case class DeterministicId(email: Email)
}
