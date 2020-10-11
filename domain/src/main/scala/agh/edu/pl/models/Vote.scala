package agh.edu.pl.models

import java.time.OffsetDateTime

import agh.edu.pl.models.models.{ EntitySettings, Identifiable }

case class Vote(
    override val id: String,
    createdAt: OffsetDateTime = OffsetDateTime.now,
    userId: String,
    linkId: String
  ) extends Identifiable[Vote](id) {
  override def withId(newId: String): Vote = this.copy(id = newId)
}

case object Vote extends EntitySettings[Vote]
