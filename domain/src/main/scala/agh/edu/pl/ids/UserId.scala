package agh.edu.pl.ids

import java.util.UUID

import agh.edu.pl.EntityId

case class UserId(value: UUID) extends EntityId {
  override def stringValue: String = value.toString
}
