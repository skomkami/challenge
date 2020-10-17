package agh.edu.pl.commands

import agh.edu.pl.context.Context
import agh.edu.pl.models.{ Entity, EntityId }
import io.circe.{ Decoder, Encoder }

import scala.concurrent.Future
import scala.reflect.ClassTag

abstract class CreateEntity[
    E <: Entity[_ <: EntityId]: Decoder: Encoder: ClassTag
  ](
  ) {
  def id: Option[E#IdType]
  def generateId: E#IdType
  def toEntity(newId: E#IdType): E

  final def finalId: E#IdType = id.getOrElse(generateId)

  def newEntity(ctx: Context, newId: E#IdType): Future[E] =
    ctx.repository.create[E](toEntity(newId))

  // by default don't update
  def updateEntity(ctx: Context, entity: E): Future[E] =
    Future(entity)(ctx.ec)

  final def createEntity(ctx: Context): Future[E] =
    ctx
      .repository
      .getByIdOpt[E](finalId)
      .flatMap {
        case Some(existing) => updateEntity(ctx, existing)
        case None           => newEntity(ctx, finalId)
      }(ctx.ec)

}
