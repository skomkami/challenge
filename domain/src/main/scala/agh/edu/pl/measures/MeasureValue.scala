package agh.edu.pl.measures

import agh.edu.pl.entities.JsonSerializable
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

  implicit lazy val MeasureValueType: ObjectType[Unit, MeasureValue] =
    deriveObjectType[Unit, MeasureValue]()

  implicit lazy val MeasureValueInputType: InputObjectType[MeasureValue] =
    deriveInputObjectType[MeasureValue](
      InputObjectTypeName("MeasureValueInput")
    )
}
