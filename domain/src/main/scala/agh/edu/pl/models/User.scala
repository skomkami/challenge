package agh.edu.pl.models

import java.time.OffsetDateTime

import agh.edu.pl.ids.UserId
import agh.edu.pl.models.models._

case class User(
    override val id: UserId,
    name: String,
    email: String,
    password: String,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends Entity[UserId](id) {
  override type IdType = UserId
}

case object User extends EntitySettings[User]
