package agh.edu.pl.queryparams

import agh.edu.pl.sort.{ Sort, SortOrder }
import sangria.marshalling.{
  CoercedScalaResultMarshaller,
  FromInput,
  ResultMarshaller
}
import sangria.schema.{ ObjectType, _ }

import scala.annotation.tailrec
import scala.collection.immutable.ListMap

case class EntitySort[T](sorts: List[Sort] = Nil)

case object EntitySort {
  implicit def fromInput[T]: FromInput[EntitySort[T]] =
    new FromInput[EntitySort[T]] {
      override val marshaller: ResultMarshaller =
        CoercedScalaResultMarshaller.default

      override def fromResult(node: marshaller.Node): EntitySort[T] = {
        val sorts = node.asInstanceOf[ListMap[_, Option[_]]].collect {
          case (name, Some(value)) =>
            Sort(name.toString, SortOrder.namesToValuesMap(value.toString))
        }
        EntitySort[T](sorts.toList)
      }
    }
}

case object SortBuilder {
  @tailrec
  private def extractFromOptionType(fieldType: OutputType[_]): OutputType[_] =
    fieldType match {
      case OptionType(underlyingType) => extractFromOptionType(underlyingType)
      case baseType                   => baseType
    }

  def sortType[T](
      graphqlType: ObjectType[_, T]
    ): InputObjectType[EntitySort[T]] = {
    val fields = graphqlType
      .fields
      .map { field =>
        field.name -> extractFromOptionType(field.fieldType)
      }
      .collect {
//      case (name, StringType) => name
//      case (name, IntType) => name
//      case (name, GraphQLOffsetDateTime) => name
        case (name, ScalarType(_, _, _, _, _, _, _, _, _)) => name
        case (name, ScalarAlias(_, _, _))                  => name
      }
      .map { sortFieldName =>
        InputField(sortFieldName, OptionInputType(SortOrder.EnumType))
      }

    InputObjectType[EntitySort[T]](
      name = s"${graphqlType.name}Sort",
      fields = fields.toList
    )
  }
}
