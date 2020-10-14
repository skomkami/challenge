package agh.edu.pl.repository

import agh.edu.pl.models.models.{ Entity, EntityId }
import io.circe.{ Decoder, Encoder }

import scala.concurrent.Future
import scala.reflect.ClassTag

trait Repository {
  def getAll[E](
      filter: Option[Filter] = None
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[Seq[E]]

  def create[E <: Entity[_ <: EntityId]](
      entity: E
    )(implicit
      tag: ClassTag[E],
      encoder: Encoder[E]
    ): Future[E]

  def getById[E <: Entity[_]](
      id: E#IdType
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[E]

  def getByIdOpt[E <: Entity[_]](
      id: E#IdType
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[Option[E]]

  def update[E <: Entity[_ <: EntityId]](
      entity: E
    )(implicit
      tag: ClassTag[E],
      encoder: Encoder[E]
    ): Future[E]
}
