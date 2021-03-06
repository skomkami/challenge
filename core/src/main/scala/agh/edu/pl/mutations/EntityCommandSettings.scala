package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.Command
import agh.edu.pl.graphql.GraphQLOffsetDateTime
import agh.edu.pl.entities.{ Entity, EntityId, EntityIdSettings }
import io.circe.Decoder
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.schema.{ Argument, ScalarType }
import shapeless.Lazy

abstract class EntityCommandSettings[
    E <: Entity[_ <: EntityId],
    C <: Command[E]
  ] {
  implicit val gqlOffsetDateTime: ScalarType[OffsetDateTime] =
    GraphQLOffsetDateTime

  implicit def jsonDecoder(implicit d: Lazy[DerivedDecoder[C]]): Decoder[C] =
    deriveDecoder[C]

  implicit def CommandInput: Argument[C]

  def idSettings: EntityIdSettings[E#IdType]
}
