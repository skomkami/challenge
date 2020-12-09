package agh.edu.pl.measures

import agh.edu.pl.context.Context
import agh.edu.pl.measures.ValueOrder.{ BiggerWins, SmallerWins }
import agh.edu.pl.measures.ValueSummarization.{ Best, Summarize }
import agh.edu.pl.models.JsonSerializable
import cats.implicits._
import sangria.macros.derive.{
  deriveInputObjectType,
  deriveObjectType,
  InputObjectTypeName
}
import sangria.schema.{ InputObjectType, ObjectType }
case class Measure(
    unitName: String,
    allowDecimal: Boolean,
    valueSummarization: ValueSummarization,
    valueOrder: ValueOrder
  ) {
  def updateSummaryValueFn(a: MeasureValue)(b: MeasureValue): MeasureValue =
    (valueOrder, valueSummarization) match {
      case (BiggerWins, Best) =>
        MeasureValue(
          integerValue = (a.integerValue ++ b.integerValue).maxOption,
          decimalValue = (a.decimalValue ++ b.decimalValue).maxOption
        )
      case (BiggerWins, Summarize) =>
        MeasureValue(
          integerValue = a.integerValue |+| b.integerValue,
          decimalValue = a.decimalValue |+| b.decimalValue
        )
      case (SmallerWins, Best) =>
        MeasureValue(
          integerValue = (a.integerValue ++ b.integerValue).minOption,
          decimalValue = (a.decimalValue ++ b.decimalValue).minOption
        )
      case _ => a //TODO should never happen so raise an error
    }

  def ordering: Ordering[MeasureValue] = (x: MeasureValue, y: MeasureValue) => {
    val compareResult =
      if (allowDecimal) {
        x.decimalValue.compare(y.decimalValue)
      }
      else {
        x.integerValue.compare(y.integerValue)
      }

    if (
      allowDecimal && x.decimalValue.isEmpty || !allowDecimal && x
        .integerValue
        .isEmpty
    ) {
      1 // if None should be last
    }
    else {
      valueOrder match {
        case BiggerWins  => -compareResult
        case SmallerWins => compareResult
      }
    }
  }
}

case object Measure extends JsonSerializable[Measure] {

  implicit lazy val MeasureType: ObjectType[Context, Measure] =
    deriveObjectType[Context, Measure]()

  implicit lazy val MeasureInputType: InputObjectType[Measure] =
    deriveInputObjectType[Measure](InputObjectTypeName("MeasureInput"))
}
