package agh.edu.pl.models

import java.util.UUID

import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.encoding.DerivedAsObjectEncoder
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }
import sangria.ast.StringValue
import sangria.schema.ScalarType
import sangria.validation.Violation
import shapeless.Lazy

import scala.reflect.ClassTag

package object models {

  abstract class Identifiable[Self](val id: String)
      extends Product
         with Serializable {
    def withId(newId: String): Self
  }

  abstract class Entity[Id <: EntityId](val id: Id) {
    type IdType <: EntityId
  }

  abstract class JsonSerializable[E] {
    implicit def jsonDecoder(implicit d: Lazy[DerivedDecoder[E]]): Decoder[E] =
      deriveDecoder[E]

    implicit def jsonEncoder(
        implicit
        d: Lazy[DerivedAsObjectEncoder[E]]
      ): Encoder[E] =
      deriveEncoder[E]
  }

  abstract class EntityId {
    def value: String
  }

  abstract class EntityIdCodec[Id <: EntityId](implicit tag: ClassTag[Id]) {
    implicit def fromString(value: String): Id
    implicit val decoder: Decoder[Id] =
      Decoder[String].map(fromString)
    implicit val encoder: Encoder[Id] = Encoder[String].contramap(_.value)

    def generateId: Id = fromString(UUID.randomUUID.toString)

//    implicit val scalarAlias: ScalarAlias[Id, String] =
//      ScalarAlias[Id, String](
//        sangria.schema.StringType,
//        _.value,
//        id => Right(fromString(id))
//      ).rename(tag.runtimeClass.getSimpleName)
    protected val idTypeName = tag.runtimeClass.getSimpleName

    implicit val GraphQLIdType = ScalarType[Id](
      name = idTypeName,
      coerceOutput = (id, _) => id.value,
      coerceInput = {
        case StringValue(odt, _, _, _, _) => Right(fromString(odt))
        case _                            => Left(IdCoerceViolation)
      },
      coerceUserInput = { //5
        case s: String => Right(fromString(s))
        case _         => Left(IdCoerceViolation)
      }
    )

    case object IdCoerceViolation extends Violation {
      override def errorMessage: String = "Error during parsing Id"
    }
  }
}
