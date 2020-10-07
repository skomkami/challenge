package agh.edu.pl.models

import java.time.OffsetDateTime

import agh.edu.pl.models.models.{ EntitySettings, Identifiable }

case class Vote(
    override val id: Int,
    createdAt: OffsetDateTime = OffsetDateTime.now,
    userId: Int,
    linkId: Int
  ) extends Identifiable[Vote](id) {
  override def withId(newId: Int): Vote = this.copy(id = newId)
}

case object Vote extends EntitySettings[Vote]
