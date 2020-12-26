package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.ids.UserId
import agh.edu.pl.models.{ Email, Gender }

case class User(
    override val id: UserId,
    name: String,
    email: Email,
    gender: Gender,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends Entity[UserId](id) {
  override type IdType = UserId
}

case object User extends JsonSerializable[User]
