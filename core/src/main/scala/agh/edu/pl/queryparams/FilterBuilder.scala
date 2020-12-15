package agh.edu.pl.queryparams

import agh.edu.pl.filters.{ Filter, FilterEq, StringQuery }
import agh.edu.pl.measures.{ ValueOrder, ValueSummarization }
import agh.edu.pl.models.{ Email, Gender }
import agh.edu.pl.registry.DomainRegistry
import sangria.marshalling.{
  CoercedScalaResultMarshaller,
  FromInput,
  ResultMarshaller
}
import sangria.schema._

import scala.collection.immutable.ListMap
import scala.reflect.ClassTag

case class EntityFilter[T](filters: List[Filter] = Nil)

case object EntityFilter {
  implicit def fromInput[T](
      implicit
      tag: ClassTag[T]
    ): FromInput[EntityFilter[T]] =
    new FromInput[EntityFilter[T]] {
      override val marshaller: ResultMarshaller =
        CoercedScalaResultMarshaller.default

      override def fromResult(node: marshaller.Node): EntityFilter[T] = {
        val filters = node.asInstanceOf[ListMap[_, Option[_]]].collect {
          case (name, Some(value)) =>
            val strName = name.toString
            val strValue = value.toString
            tag
              .runtimeClass
              .getDeclaredField(strName)
              .getType
              .getSimpleName match {
              case "String" =>
                StringQuery(strName, strValue)
              case _ =>
                FilterEq(name.toString, value.toString)
            }

        }
        EntityFilter[T](filters.toList)
      }
    }
}

case object FilterBuilder {

  def filterType[T](
      implicit
      tag: ClassTag[T]
    ): InputObjectType[EntityFilter[T]] = {
    val fields =
      tag
        .runtimeClass
        .getDeclaredFields
        .map { field =>
          val inputType = field.getType.getSimpleName match {
            case "String"             => Some(StringType)
            case "Email"              => Some(Email.scalarAlias)
            case "Int"                => Some(IntType)
            case "boolean"            => Some(BooleanType)
            case "Gender"             => Some(Gender.EnumType)
            case "ValueOrder"         => Some(ValueOrder.EnumType)
            case "ValueSummarization" => Some(ValueSummarization.EnumType)
            case "OffsetDateTime" =>
              Some(agh.edu.pl.graphql.GraphQLOffsetDateTime)
            case _ => None
          }
          inputType
            .orElse {
              DomainRegistry
                .idsSettings
                .get(field.getType.getSimpleName)
                .map(_.scalarAlias)
            }
            .map(OptionInputType.apply)
            .map(InputField(field.getName, _))
        }
        .flatMap(_.toList)

    InputObjectType[EntityFilter[T]](
      name = s"${tag.runtimeClass.getSimpleName}Filter",
      fields = fields.toList
    )
  }
}