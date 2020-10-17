package agh.edu.pl.mutations

import java.time.OffsetDateTime

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.graphql.CustomScalars
import agh.edu.pl.models.{ Entity, EntityId, EntityIdSettings }
import io.circe.Decoder
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.schema.{ Argument, ScalarType }
import shapeless.Lazy

abstract class CreateEntitySettings[
    E <: Entity[_ <: EntityId],
    C <: CreateEntity[E]
  ] {
  implicit val gqlOffsetDateTime: ScalarType[OffsetDateTime] =
    CustomScalars.GraphQLOffsetDateTime

  implicit def jsonDecoder(implicit d: Lazy[DerivedDecoder[C]]): Decoder[C] =
    deriveDecoder[C]

  implicit def CreateEntityInput: Argument[C]

  def idCodec: EntityIdSettings[E#IdType]
}
