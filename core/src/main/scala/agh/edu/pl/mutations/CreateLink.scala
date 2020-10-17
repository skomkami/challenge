package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.ids.{ LinkId, UserId }
import agh.edu.pl.models.{ EntityIdSettings, Link, User }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType }

import scala.concurrent.{ ExecutionContext, Future }

case class CreateLink(
    id: Option[LinkId] = None,
    url: String,
    description: String,
    postedBy: UserId,
    createdAt: OffsetDateTime = OffsetDateTime.now
  ) extends CreateEntity[Link] {

  override def toEntity(newId: LinkId): Link =
    Link(
      id = newId,
      url = url,
      description = description,
      postedBy = postedBy,
      createdAt = createdAt
    )

  override def newEntity(ctx: Context, newId: LinkId): Future[Link] = {
    implicit val ec: ExecutionContext = ctx.ec
    for {
      _ <- ctx.repository.getById[User](postedBy)
      created <- ctx.repository.create[Link](toEntity(newId))
    } yield created
  }

  override def generateId: LinkId =
    LinkId.generateId(LinkId.DataToGenerateId(postedBy, url))

}

case object CreateLink extends CreateEntitySettings[Link, CreateLink] {
  import sangria.marshalling.circe._

  lazy val CreateLinkInputType: InputObjectType[CreateLink] =
    deriveInputObjectType[CreateLink]()

  lazy val CreateEntityInput: Argument[CreateLink] =
    Argument("input", CreateLinkInputType)

  override def idCodec: EntityIdSettings[LinkId] = LinkId
}
