package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.models.Link
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

case class CreateLink( //id: Option[Int] = None,
    url: String,
    description: String,
    postedBy: Int,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends CreateEntity[Link] {

  def toEntity: Link =
    Link(
      id = 0,
      url = url,
      description = description,
      postedBy = postedBy,
      createdAt = createdAt
    )
}

case object CreateLink extends CreateEntitySettings[CreateLink] {
  import sangria.marshalling.circe._

  lazy val CreateLinkInputType: InputObjectType[CreateLink] =
    deriveInputObjectType[CreateLink]()
  lazy val CreateEntityInput = Argument("input", CreateLinkInputType)
}
