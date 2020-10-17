package agh.edu.pl.ids

import agh.edu.pl.models.{ EntityId, EntityIdSettings }

case class LinkId(override val value: String) extends EntityId

object LinkId extends EntityIdSettings[LinkId] {
  override implicit def fromString(value: String): LinkId = LinkId(value)

  override type PK = DataToGenerateId
  case class DataToGenerateId(userId: UserId, url: String)
}
