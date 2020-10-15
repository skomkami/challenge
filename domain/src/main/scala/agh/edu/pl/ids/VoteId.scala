package agh.edu.pl.ids

import agh.edu.pl.models.models.{ EntityId, EntityIdCodec }
//import sangria.macros.derive.{ deriveInputObjectType, InputObjectTypeName }
//import sangria.schema.InputObjectType

case class VoteId(override val value: String) extends EntityId

object VoteId extends EntityIdCodec[VoteId] {
  override implicit def fromString(value: String): VoteId = VoteId(value)
//  implicit def EntityIdInput: InputObjectType[VoteId] =
//    deriveInputObjectType(InputObjectTypeName("voteId"))
}
