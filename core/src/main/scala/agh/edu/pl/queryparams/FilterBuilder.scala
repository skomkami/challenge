package agh.edu.pl.queryparams

import agh.edu.pl.filters.{ FieldFilter, Filter, FilterConds, StringQuery }
import agh.edu.pl.models.Email
import agh.edu.pl.registry.DomainRegistry
import io.circe.Json
import sangria.marshalling.circe.CirceResultMarshaller
import sangria.marshalling.{ FromInput, ResultMarshaller }
import sangria.schema._

import scala.reflect.ClassTag

case class EntityFilter[T](filters: List[Filter] = Nil)

case object EntityFilter {
  implicit def fromInput[T](
      implicit
      tag: ClassTag[T]
    ): FromInput[EntityFilter[T]] =
    new FromInput[EntityFilter[T]] {
      override val marshaller: ResultMarshaller =
        CirceResultMarshaller

      override def fromResult(node: marshaller.Node): EntityFilter[T] = {
        val mapBuilder = node.asInstanceOf[Json]
        val valuesMap = mapBuilder.asObject.map(_.toMap).getOrElse(Map.empty)
        val filters = valuesMap.map {
          case (fieldName, conditions) =>
            val declaredFields = tag.runtimeClass.getDeclaredFields
            if (
              declaredFields
                .find(_.getName == fieldName)
                .exists(_.getType.getSimpleName == "String")
            ) {
              StringQuery(fieldName, conditions.asString.getOrElse(""))
            }
            else {
              val map = conditions.asObject.map(_.toMap).getOrElse(Map.empty)
              val emptyConds = FilterConds()
              val filterConds = map.foldLeft(emptyConds) { (condsAcc, entry) =>
                entry match {
                  case ("eq", value) =>
                    condsAcc.copy(eq = value.asString)
                  case ("neq", value) =>
                    condsAcc.copy(neq = value.asString)
                  case ("lt", value) =>
                    condsAcc.copy(lt = value.asString)
                  case ("gt", value) =>
                    condsAcc.copy(gt = value.asString)
                  case _ => condsAcc
                }
              }
              FieldFilter(fieldName, filterConds)
            }
        }
        EntityFilter[T](filters.toList)
      }
    }
}

case object FilterBuilder {

  trait Condition[T]

  private val basicConditions = List("eq", "neq")
  private val extendedConditions = basicConditions ++ List("gt", "lt")

  private def conditionsInput[T](
      conditions: List[String],
      inputType: InputType[T]
    ): InputObjectType[_] = {
    val fields =
      conditions.map(cond => InputField(cond, OptionInputType(inputType)))

    val conditionsInputType = InputObjectType[Condition[T]](
      name = s"Conditions",
      fields = fields
    )
    inputType match {
      case ScalarAlias(ScalarType(name, _, _, _, _, _, _, _, _), _, _) =>
        conditionsInputType.copy(name = s"${name}Conditions")
      case ScalarType(name, _, _, _, _, _, _, _, _) =>
        conditionsInputType.copy(name = s"${name}Conditions")
      case EnumType(name, _, _, _, _) =>
        conditionsInputType.copy(name = s"${name}Conditions")
      case _ =>
        conditionsInputType.copy(name =
          s"${inputType.getClass.getSimpleName}Conditions"
        )
    }
  }

  def filterType[T](
      graphqlType: ObjectType[_, T]
    ): InputObjectType[EntityFilter[T]] = {
    val fields =
      graphqlType
        .fields
        .map { field =>
          field.name -> extractFromOptionType(field.fieldType)
        }
        .collect {
          case (name, fieldDef @ ScalarAlias(alias, _, _))
              if DomainRegistry
                .idsSettings
                .contains(alias.name) =>
            InputField(
              name,
              OptionInputType(
                OptionInputType(
                  conditionsInput(
                    basicConditions,
                    fieldDef
                  )
                )
              )
            )
          case (name, typeDef @ EnumType(_, _, _, _, _)) =>
            InputField(
              name,
              OptionInputType(conditionsInput(basicConditions, typeDef))
            )
          case (name, StringType | Email.scalarAlias) =>
            InputField(name, OptionInputType(StringType))
          case (name, typeDef @ ScalarType(_, _, _, _, _, _, _, _, _)) =>
            scribe.info(s"scalar type: $name")
            InputField(
              name,
              OptionInputType(conditionsInput(extendedConditions, typeDef))
            )
          case (name, typeDef @ ScalarAlias(_, _, _)) =>
            scribe.info(s"scalar alias: $name")
            InputField(
              name,
              OptionInputType(conditionsInput(extendedConditions, typeDef))
            )
        }

    InputObjectType[EntityFilter[T]](
      name = s"${graphqlType.name}Filter",
      fields = fields.toList
    )
  }
}
