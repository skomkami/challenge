package agh.edu.pl.mutations

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.graphql.CustomScalars
import agh.edu.pl.models.models.{ Entity, EntityId, EntityIdCodec }
import io.circe.Decoder
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.schema.Argument
import shapeless.Lazy

abstract class CreateEntitySettings[
    E <: Entity[_ <: EntityId],
    C <: CreateEntity[E]
  ] {
  implicit val gqlOffsetDateTime =
    CustomScalars.GraphQLOffsetDateTime

  implicit def jsonDecoder(implicit d: Lazy[DerivedDecoder[C]]): Decoder[C] =
    deriveDecoder[C]

  implicit def CreateEntityInput: Argument[C]

  def idCodec: EntityIdCodec[E#IdType]
}
