package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.models.Vote
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

case class CreateVote( //id: Option[Int] = None,
    createdAt: OffsetDateTime = OffsetDateTime.now,
    userId: Int,
    linkId: Int
  ) extends CreateEntity[Vote] {

  def toEntity: Vote =
    Vote(id = 0, createdAt = createdAt, userId = userId, linkId = linkId)
}

case object CreateVote extends CreateEntitySettings[CreateVote] {
  import sangria.marshalling.circe._

  lazy val CreateVoteInputType: InputObjectType[CreateVote] =
    deriveInputObjectType[CreateVote]()
  lazy val CreateEntityInput: Argument[CreateVote] =
    Argument("input", CreateVoteInputType)
}
