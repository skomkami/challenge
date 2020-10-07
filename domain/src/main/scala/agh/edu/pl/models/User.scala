package agh.edu.pl.models

import java.time.OffsetDateTime

import agh.edu.pl.models.models.{ EntitySettings, Identifiable }

case class User(
    override val id: Int,
    name: String,
    email: String,
    password: String,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends Identifiable[User](id) {
  override def withId(newId: Int): User = this.copy(id = newId)
}

case object User extends EntitySettings[User]
