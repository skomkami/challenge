package agh.edu.pl.measures

import agh.edu.pl.measures.ValueOrder.{ BiggerWins, SmallerWins }
import agh.edu.pl.measures.ValueSummarization.Summarize
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.softwaremill.quicklens._

class MeasureOrderingTest extends AnyFlatSpec with Matchers {

  val values = List(
    MeasureValue(
      integerValue = Some(3),
      decimalValue = Some(3.3)
    ),
    MeasureValue(
      integerValue = Some(2),
      decimalValue = Some(2.2)
    ),
    MeasureValue(
      integerValue = Some(5),
      decimalValue = Some(5.5)
    ),
    MeasureValue(
      integerValue = Some(0),
      decimalValue = Some(0f)
    ),
    MeasureValue(
      integerValue = None,
      decimalValue = None
    )
  )

  val defaultMeasure = Measure(
    unitName = "meters",
    allowDecimal = false,
    valueSummarization = Summarize,
    valueOrder = BiggerWins
  )

  "BiggerWins and integerValue" should "have 5 on top" in {
    val measure = defaultMeasure

    val sorted = values.sorted(measure.ordering)
    sorted.head.integerValue shouldEqual Some(5)
    sorted.last.integerValue shouldEqual None
  }

  "BiggerWins and decimalValue" should "have 5.5 on top" in {
    val measure = modify(defaultMeasure)(_.allowDecimal).setTo(true)

    val sorted = values.sorted(measure.ordering)
    sorted.head.decimalValue shouldEqual Some(5.5)
    sorted.last.decimalValue shouldEqual None
  }

  "SmallerWins and integerValue" should "have 5 on top" in {
    val measure = modify(defaultMeasure)(_.valueOrder).setTo(SmallerWins)

    val sorted = values.sorted(measure.ordering)
    sorted.head.integerValue shouldEqual Some(0)
    sorted.last.integerValue shouldEqual None
  }

  "SmallerWins and decimalValue" should "have 5.5 on top" in {
    val measure = modify(defaultMeasure)(_.valueOrder)
      .setTo(SmallerWins)
      .modify(_.allowDecimal)
      .setTo(true)

    val sorted = values.sorted(measure.ordering)
    sorted.head.decimalValue shouldEqual Some(0)
    sorted.last.decimalValue shouldEqual None
  }

}
