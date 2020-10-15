package agh.edu.pl.models

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ LinkId, UserId }
import agh.edu.pl.models.models.{ Entity, JsonSerializable }

case class Link(
    override val id: LinkId,
    url: String,
    description: String,
    postedBy: UserId,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends Entity[LinkId](id) {
  override type IdType = LinkId
}

case object Link extends JsonSerializable[Link]
