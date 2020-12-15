package agh.edu.pl

import agh.edu.pl.hash.MD5Hash
import io.circe.generic.decoding.DerivedDecoder
import io.circe.generic.encoding.DerivedAsObjectEncoder
import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }
import sangria.schema.ScalarAlias
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

  trait EntityId {
    def value: String
  }

  abstract class EntityIdSettings[Id <: EntityId](implicit tag: ClassTag[Id]) {
    type PK

    implicit def fromString(value: String): Id

    implicit val decoder: Decoder[Id] =
      Decoder[String].map(fromString)
    implicit val encoder: Encoder[Id] = Encoder[String].contramap(_.value)

    def generateId(arg: PK): Id = fromString(MD5Hash.generateHash(arg))

    implicit val scalarAlias: ScalarAlias[Id, String] =
      ScalarAlias[Id, String](
        sangria.schema.StringType,
        _.value,
        id => Right(fromString(id))
      ).rename(tag.runtimeClass.getSimpleName)

    protected val idTypeName: String = tag.runtimeClass.getSimpleName
  }

  def plural(entityName: String): String =
    if (entityName.last == 'y') {
      s"${entityName.dropRight(1)}ies"
    }
    else {
      s"${entityName}s"
    }
}
