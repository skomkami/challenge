package agh.edu.pl.ids

import agh.edu.pl.models.models.{ EntityId, EntityIdCodec }
//import sangria.macros.derive.{ deriveInputObjectType, InputObjectTypeName }
//import sangria.schema.InputObjectType

case class UserId(override val value: String) extends EntityId

object UserId extends EntityIdCodec[UserId] {
  override implicit def fromString(value: String): UserId = UserId(value)
}
