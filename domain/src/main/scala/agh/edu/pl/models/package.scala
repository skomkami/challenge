package agh.edu.pl.models

import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.encoding.DerivedAsObjectEncoder
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }
import shapeless.Lazy

package object models {

  abstract class Identifiable[Self](val id: Int)
      extends Product
         with Serializable {
    def withId(newId: Int): Self
  }

  abstract class EntitySettings[E] {
    implicit def jsonDecoder(implicit d: Lazy[DerivedDecoder[E]]): Decoder[E] =
      deriveDecoder[E]

    implicit def jsonEncoder(
        implicit
        d: Lazy[DerivedAsObjectEncoder[E]]
      ): Encoder[E] =
      deriveEncoder[E]
  }

}
