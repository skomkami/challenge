package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.models.User
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

case class CreateUser( //id: Option[String] = None,
    name: String,
    email: String,
    password: String,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends CreateEntity[User] {

  def toEntity: User =
    User(
      id = "",
      name = name,
      email = email,
      password = password,
      createdAt = createdAt
    )
}

case object CreateUser extends CreateEntitySettings[CreateUser] {
  import sangria.marshalling.circe._

  val entityName: String = "abc"

  private lazy val CreateUserInputType: InputObjectType[CreateUser] =
    deriveInputObjectType[CreateUser]()
  lazy val CreateEntityInput: Argument[CreateUser] =
    Argument("input", CreateUserInputType)
}
