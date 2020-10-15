package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.ids.{ LinkId, UserId, VoteId }
import agh.edu.pl.models.{ models, Link, User, Vote }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.Future

case class CreateVote(
    id: Option[VoteId] = None,
    createdAt: OffsetDateTime = OffsetDateTime.now,
    userId: UserId,
    linkId: LinkId
  ) extends CreateEntity[Vote] {

  def toEntity(newId: VoteId): Vote =
    Vote(
      id = newId,
      createdAt = createdAt,
      userId = userId,
      linkId = linkId
    )

  override def newEntity(ctx: Context, newId: VoteId): Future[Vote] = {
    implicit val ec = ctx.ec
    for {
      _ <- ctx.repository.getById[User](userId)
      _ <- ctx.repository.getById[Link](linkId)
      newVote <- ctx.repository.create(toEntity(newId))
    } yield newVote
  }

  override def generateId: VoteId = VoteId.generateId
}

case object CreateVote extends CreateEntitySettings[Vote, CreateVote] {
  import sangria.marshalling.circe._

  lazy val CreateVoteInputType: InputObjectType[CreateVote] =
    deriveInputObjectType[CreateVote]()

  lazy val CreateEntityInput: Argument[CreateVote] =
    Argument("input", CreateVoteInputType)

  override def idCodec: models.EntityIdCodec[VoteId] = VoteId
}
