package agh.edu.pl.commands

import agh.edu.pl.context.Context
import agh.edu.pl.entities.{ Entity, EntityId }
import io.circe.{ Decoder, Encoder }

import scala.concurrent.Future
import scala.reflect.ClassTag

abstract class CreateEntity[
    E <: Entity[_ <: EntityId]: Decoder: Encoder: ClassTag
  ] extends Command[E] {
  def id: Option[E#IdType]
  def generateId: E#IdType
  def toEntity(newId: E#IdType): E

  final def finalId: E#IdType = id.getOrElse(generateId)

  def createNewEntity(ctx: Context, newId: E#IdType): Future[E] =
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
        case None           => createNewEntity(ctx, finalId)
      }(ctx.ec)

}
