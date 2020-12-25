package agh.edu.pl

import sangria.schema._

import scala.annotation.tailrec
import scala.reflect.ClassTag

package object queryparams {
  @tailrec
  private[queryparams] def extractFromOptionType(
      fieldType: OutputType[_]
    ): OutputType[_] =
    fieldType match {
      case OptionType(underlyingType) => extractFromOptionType(underlyingType)
      case baseType                   => baseType
    }

  private[queryparams] def extractBaseTypes[T](
      graphqlType: ObjectType[_, T]
    )(implicit
      tag: ClassTag[T]
    ): Seq[(String, OutputType[_])] = {
    val classFields = tag.runtimeClass.getDeclaredFields.map(_.getName)
    val graphqlBaseClassFields =
      graphqlType.fields.filter(field => classFields.contains(field.name))
    val idField = graphqlType.fields.find(_.name == "id").toVector
    extractBaseTypes(idField ++ graphqlBaseClassFields, "")
  }

  private[queryparams] def extractBaseTypes(
      fields: Vector[Field[_, _]],
      prefix: String
    ): Seq[(String, OutputType[_])] =
    fields
      .map { field =>
        field.name -> field.fieldType
      }
      .map {
        case (fieldName, fieldType) =>
          fieldName -> extractFromOptionType(fieldType)
      }
      .flatMap {
        case (fieldName, obj @ ObjectType(_, _, _, _, _, _, _)) =>
          extractBaseTypes(obj.fields, s"$prefix${fieldName}_")
        case (filedName, fieldType) =>
          Seq(s"$prefix$filedName" -> fieldType)
      }
}
