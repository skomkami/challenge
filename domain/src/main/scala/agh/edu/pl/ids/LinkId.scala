package agh.edu.pl.ids

import agh.edu.pl.models.models.{ EntityId, EntityIdCodec }
//import sangria.macros.derive.{ deriveInputObjectType, InputObjectTypeName }
//import sangria.schema.InputObjectType

case class LinkId(override val value: String) extends EntityId

object LinkId extends EntityIdCodec[LinkId] {
  override implicit def fromString(value: String): LinkId = LinkId(value)

//  implicit def EntityIdInput: InputObjectType[LinkId] =
//    deriveInputObjectType(InputObjectTypeName("linkId"))

}
