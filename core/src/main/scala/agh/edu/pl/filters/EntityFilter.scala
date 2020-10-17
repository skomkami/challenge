package agh.edu.pl.filters

import java.time.OffsetDateTime

import agh.edu.pl.models._
import sangria.schema.{ Argument, InputObjectType, ScalarType }

trait EntityFilter[E] {
  def filters: Iterable[Filter]
}

abstract class EntityFilterSettings[F <: EntityFilter[_ <: Entity[_]]]
    extends JsonSerializable[F] {
  implicit val GraphQLOffsetDateTime: ScalarType[OffsetDateTime] =
    agh.edu.pl.graphql.CustomScalars.GraphQLOffsetDateTime
  def FilterInputType: InputObjectType[F]
  def FilterArgument: Argument[Option[F]]
}
