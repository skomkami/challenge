package agh.edu.pl.measures

import agh.edu.pl.entities.UserChallengeSummary
import agh.edu.pl.ids.{ ChallengeId, UserChallengeSummaryId, UserId }
import agh.edu.pl.measures.ValueOrder.{ BiggerWins, SmallerWins }
import agh.edu.pl.measures.ValueSummarization.{ Best, Summarize }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.softwaremill.quicklens._

class MeasureSummarizationTest extends AnyFlatSpec with Matchers {
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
    )
  )
  val defaultMeasure = Measure(
    unitName = "meters",
    allowDecimal = false,
    valueSummarization = Summarize,
    valueOrder = BiggerWins
  )

  val emptySummary = UserChallengeSummary(
    id = UserChallengeSummaryId("test"),
    userId = UserId("test"),
    challengeId = ChallengeId("test"),
    summaryValue = MeasureValue.empty,
    position = None
  )

  def calculateWithMeasure(measure: Measure): UserChallengeSummary = {
    val result = values.foldLeft(emptySummary) { (summary, value) =>
      modify(summary)(_.summaryValue).using(measure.updateSummaryValueFn(value))
    }
    result
  }

  "BiggerWins, Summarize value" should "equal 10" in {
    val measure = defaultMeasure

    val result = calculateWithMeasure(measure)
    result.summaryValue.integerValue shouldEqual Some(10)
  }

  "BiggerWins, Best value" should "equal 5" in {
    val measure = modify(defaultMeasure)(_.valueSummarization).setTo(Best)

    val result = calculateWithMeasure(measure)
    result.summaryValue.integerValue shouldEqual Some(5)
  }

  "SmallerWins, Best value" should "equal 2" in {
    val measure = modify(defaultMeasure)(_.valueOrder)
      .setTo(SmallerWins)
      .modify(_.valueSummarization)
      .setTo(Best)

    val result = calculateWithMeasure(measure)
    result.summaryValue.integerValue shouldEqual Some(2)
  }

}
