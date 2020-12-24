package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.auth.AuthServiceError
import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities
import agh.edu.pl.entities.User
import agh.edu.pl.ids.UserId
import agh.edu.pl.models.{ Email, EntityIdSettings, Gender }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }

case class CreateUser(
    id: Option[UserId] = None,
    name: String,
    email: Email,
    gender: Gender,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends CreateEntity[User] {

  override def toEntity(newId: UserId): User =
    entities.User(
      id = newId,
      name = name,
      email = email,
      gender = gender,
      createdAt = createdAt
    )

  override def createNewEntity(ctx: Context, newId: UserId): Future[User] = {
    implicit val ec: ExecutionContext = ctx.ec
    val entity = toEntity(newId)

    ctx
      .authService
      .createUser(entity)
      .flatMap {
        case Right(_)    => ctx.repository.create(entity)
        case Left(error) => throw AuthServiceError(error)
      }
  }

  override def updateEntity(ctx: Context, entity: User): Future[User] =
    throw EntityAlreadyExists(
      s"User: ${entity.id} already exists"
    )

  override def generateId: UserId =
    UserId.generateId(UserId.DeterministicId(email))
}

case object CreateUser extends EntityCommandSettings[User, CreateUser] {
  import sangria.marshalling.circe._

  private lazy val CreateUserInputType: InputObjectType[CreateUser] =
    deriveInputObjectType[CreateUser]()

  lazy val CommandInput: Argument[CreateUser] =
    Argument("input", CreateUserInputType)

  override def idSettings: EntityIdSettings[UserId] = UserId
}
