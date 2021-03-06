package agh.edu.pl.measures

import agh.edu.pl.measures.ValueOrder.{ BiggerWins, SmallerWins }
import agh.edu.pl.measures.ValueSummarization.{ Best, Summarize }
import agh.edu.pl.entities.JsonSerializable
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
    val a: Option[Double] =
      if (allowDecimal) x.decimalValue else x.integerValue.map(_.doubleValue)
    val b: Option[Double] =
      if (allowDecimal) y.decimalValue else y.integerValue.map(_.doubleValue)
    (a, b) match {
      case (None, None)    => 0
      case (Some(_), None) => -1
      case (None, Some(_)) => 1
      case (Some(a1), Some(b1)) =>
        val compareResult = Ordering[Double].compare(a1, b1)
        valueOrder match {
          case BiggerWins  => -compareResult
          case SmallerWins => compareResult
        }
    }
  }
}

case object Measure extends JsonSerializable[Measure] {

  implicit lazy val MeasureType: ObjectType[Unit, Measure] =
    deriveObjectType[Unit, Measure]()

  implicit lazy val MeasureInputType: InputObjectType[Measure] =
    deriveInputObjectType[Measure](InputObjectTypeName("MeasureInput"))
}
