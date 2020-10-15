package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.ids.UserId
import agh.edu.pl.models.{ models, Sex, User }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.Future

case class CreateUser(
    id: Option[UserId] = None,
    name: String,
    email: String,
    sex: Sex,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends CreateEntity[User] {

  override def toEntity(newId: UserId): User =
    User(
      id = newId,
      name = name,
      email = email,
      sex = sex,
      createdAt = createdAt
    )

  override def updateEntity(ctx: Context, entity: User): Future[User] =
    ctx.repository.update(toEntity(finalId))

  override def generateId: UserId = UserId.generateId
}

case object CreateUser extends CreateEntitySettings[User, CreateUser] {
  import sangria.marshalling.circe._

  private lazy val CreateUserInputType: InputObjectType[CreateUser] =
    deriveInputObjectType[CreateUser]()

  lazy val CreateEntityInput: Argument[CreateUser] =
    Argument("input", CreateUserInputType)

  override def idCodec: models.EntityIdCodec[UserId] = UserId
}
