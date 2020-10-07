package agh.edu.pl.repository

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.models.models.Identifiable
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

  def create[E <: Identifiable[E]](
      createEntityInput: CreateEntity[E]
    )(implicit
      tag: ClassTag[E],
      encoder: Encoder[E]
    ): Future[E]

  def getByIds[E](
      ids: Seq[Int]
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[Seq[E]]

  def getById[E](
      id: Int
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[E]
}
