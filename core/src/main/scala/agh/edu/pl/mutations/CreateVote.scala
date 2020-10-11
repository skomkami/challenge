package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.models.{ Link, User, Vote }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.Future

case class CreateVote( //id: Option[String] = None,
    createdAt: OffsetDateTime = OffsetDateTime.now,
    userId: String,
    linkId: String
  ) extends CreateEntity[Vote] {

  def toEntity: Vote =
    Vote(id = "", createdAt = createdAt, userId = userId, linkId = linkId)

  override def newEntity(ctx: Context): Future[Vote] = {
    implicit val ec = ctx.ec
    for {
      _ <- ctx.repository.getById[User](userId)
      _ <- ctx.repository.getById[Link](linkId)
      created <- ctx.repository.create(this)
    } yield created

  }
}

case object CreateVote extends CreateEntitySettings[CreateVote] {
  import sangria.marshalling.circe._

  lazy val CreateVoteInputType: InputObjectType[CreateVote] =
    deriveInputObjectType[CreateVote]()
  lazy val CreateEntityInput: Argument[CreateVote] =
    Argument("input", CreateVoteInputType)
}
