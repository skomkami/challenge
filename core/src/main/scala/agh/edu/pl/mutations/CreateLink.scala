package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.models.{ Link, User }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.Future

case class CreateLink( //id: Option[String] = None,
    url: String,
    description: String,
    postedBy: String,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends CreateEntity[Link] {

  def toEntity: Link =
    Link(
      id = "0",
      url = url,
      description = description,
      postedBy = postedBy,
      createdAt = createdAt
    )

  override def newEntity(ctx: Context): Future[Link] = {
    implicit val ec = ctx.ec
    for {
      _ <- ctx.repository.getById[User](postedBy)
      created <- ctx.repository.create[Link](this)
    } yield created
  }
}

case object CreateLink extends CreateEntitySettings[CreateLink] {
  import sangria.marshalling.circe._

  lazy val CreateLinkInputType: InputObjectType[CreateLink] =
    deriveInputObjectType[CreateLink]()
  lazy val CreateEntityInput = Argument("input", CreateLinkInputType)
}
