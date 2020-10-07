package agh.edu.pl.mutations

import agh.edu.pl.graphql.CustomScalars
import io.circe.Decoder
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.semiauto.deriveDecoder
import sangria.schema.Argument
import shapeless.Lazy

abstract class CreateEntitySettings[E] {
  implicit val gqlOffsetDateTime =
    CustomScalars.GraphQLOffsetDateTime

  implicit def jsonDecoder(implicit d: Lazy[DerivedDecoder[E]]): Decoder[E] =
    deriveDecoder[E]

  implicit def CreateEntityInput: Argument[E]
}
