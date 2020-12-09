package agh.edu.pl.measures

import agh.edu.pl.context.Context
import agh.edu.pl.models.JsonSerializable
import sangria.macros.derive.{
  deriveInputObjectType,
  deriveObjectType,
  InputObjectTypeName
}
import sangria.schema.{ InputObjectType, ObjectType }

case class MeasureValue(
    integerValue: Option[Int],
    decimalValue: Option[Double]
  )
case object MeasureValue extends JsonSerializable[MeasureValue] {
  def empty: MeasureValue = MeasureValue(None, None)

  implicit lazy val MeasureValueType: ObjectType[Context, MeasureValue] =
    deriveObjectType[Context, MeasureValue]()

  implicit lazy val MeasureValueInputType: InputObjectType[MeasureValue] =
    deriveInputObjectType[MeasureValue](
      InputObjectTypeName("MeasureValueInput")
    )
}
