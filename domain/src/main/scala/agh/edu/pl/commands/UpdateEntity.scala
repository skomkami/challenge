package agh.edu.pl.commands

import agh.edu.pl.context.Context
import agh.edu.pl.models.{ Entity, EntityId }
import io.circe.{ Decoder, Encoder }

import scala.concurrent.Future
import scala.reflect.ClassTag

abstract class UpdateEntity[E <: Entity[_ <: EntityId]: Decoder: Encoder](
    id: E#IdType
  )(implicit
    tag: ClassTag[E]
  ) extends Command[E] {
  def update(ctx: Context, entity: E): Future[E]

  final def updateEntity(ctx: Context): Future[E] =
    ctx
      .repository
      .getByIdOpt[E](id)
      .flatMap {
        case Some(existing) => update(ctx, existing)
        case None =>
          throw NotFoundEntity(tag.runtimeClass.getSimpleName, id.value)
      }(ctx.ec)

}
