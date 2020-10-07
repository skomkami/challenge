package agh.edu.pl.entities

import agh.edu.pl.Entity

final case class User[UserId](
    override val id: UserId,
    userName: String,
    email: String
  ) extends Entity[UserId](id) {
  override protected type SelfType = User[UserId]
  override protected type Id = UserId
}
