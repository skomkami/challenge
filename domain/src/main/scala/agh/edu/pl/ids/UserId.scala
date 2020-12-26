package agh.edu.pl.ids

import agh.edu.pl.entities.{ EntityId, EntityIdSettings }
import agh.edu.pl.models.Email

case class UserId(override val value: String) extends EntityId

object UserId extends EntityIdSettings[UserId] {
  override def fromString(value: String): UserId = UserId(value)

  override type PK = DeterministicId
  case class DeterministicId(email: Email)

  def fromEmail(email: String): UserId =
    generateId(DeterministicId(Email.fromString(email)))
}
