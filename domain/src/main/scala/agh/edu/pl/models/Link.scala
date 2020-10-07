package agh.edu.pl.models

import java.time.OffsetDateTime

import agh.edu.pl.models.models.{ EntitySettings, Identifiable }

case class Link(
    override val id: Int,
    url: String,
    description: String,
    postedBy: Int,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends Identifiable[Link](id) {
  override def withId(newId: Int): Link = this.copy(id = newId)
}

case object Link extends EntitySettings[Link]
