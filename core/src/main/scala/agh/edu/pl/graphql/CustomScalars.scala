package agh.edu.pl.graphql

import java.time.OffsetDateTime

import sangria.ast.StringValue
import sangria.schema.ScalarType
import sangria.validation.Violation

import scala.util.{ Failure, Success, Try }

case object CustomScalars {

  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing DateTime"
  }

  val GraphQLOffsetDateTime = ScalarType[OffsetDateTime](
    "OffsetDateTime",
    coerceOutput = (odt, _) => odt.toString,
    coerceInput = {
      case StringValue(odt, _, _, _, _) => offsetDateTimeFromStr(odt)
      case _                            => Left(DateTimeCoerceViolation)
    },
    coerceUserInput = { //5
      case s: String => offsetDateTimeFromStr(s)
      case _         => Left(DateTimeCoerceViolation)
    }
  )

  private def offsetDateTimeFromStr(
      stringValue: String
    ): Either[Violation, OffsetDateTime] =
    Try(OffsetDateTime.parse(stringValue)) match {
      case Success(value) => Right(value)
      case Failure(_)     => Left(DateTimeCoerceViolation)
    }
}
