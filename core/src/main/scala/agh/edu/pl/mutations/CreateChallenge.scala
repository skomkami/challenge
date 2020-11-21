package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Challenge, User }
import agh.edu.pl.ids.{ ChallengeId, UserId }
import agh.edu.pl.models.EntityIdSettings
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }

case class CreateChallenge(
    override val id: Option[ChallengeId],
    name: String,
    createdBy: UserId,
    createdOn: OffsetDateTime = OffsetDateTime.now,
    finishesOn: OffsetDateTime
  ) extends CreateEntity[Challenge] {

  override def toEntity(newId: ChallengeId): Challenge =
    Challenge(
      id = newId,
      name = name,
      createdBy = createdBy,
      createdOn = createdOn,
      finishesOn = finishesOn
    )

  override def newEntity(
      ctx: Context,
      newId: ChallengeId
    ): Future[Challenge] = {
    implicit val ec: ExecutionContext = ctx.ec
    for {
      _ <- ctx.repository.getById[User](createdBy)
      created <- ctx.repository.create[Challenge](toEntity(newId))
    } yield created
  }

  override def generateId: ChallengeId =
    ChallengeId.generateId(ChallengeId.DataToGenerateId(name))

}

case object CreateChallenge
    extends CreateEntitySettings[Challenge, CreateChallenge] {
  import sangria.marshalling.circe._

  lazy val CreateChallengeInputType: InputObjectType[CreateChallenge] =
    deriveInputObjectType[CreateChallenge]()

  lazy val CreateEntityInput: Argument[CreateChallenge] =
    Argument("input", CreateChallengeInputType)

  override def idSettings: EntityIdSettings[ChallengeId] = ChallengeId
}
