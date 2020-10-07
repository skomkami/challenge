package agh.edu.pl.ids

import java.util.UUID

import agh.edu.pl.EntityId

case class UserChallengeActivityId(value: UUID) extends EntityId {
  override def stringValue: String = value.toString
}
