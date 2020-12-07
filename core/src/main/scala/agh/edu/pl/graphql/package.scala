package agh.edu.pl

import java.time.OffsetDateTime

import agh.edu.pl.context.Context
import agh.edu.pl.response.SearchResponse
import sangria.ast.StringValue
import sangria.schema._
import sangria.validation.Violation

import scala.util.{ Failure, Success, Try }

package object graphql {
  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error during parsing DateTime"
  }

  implicit val GraphQLOffsetDateTime: ScalarType[OffsetDateTime] =
    ScalarType[OffsetDateTime](
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

  def searchResponse[T](
      entityOutput: ObjectType[Context, T]
    ): ObjectType[Context, SearchResponse[T]] =
    ObjectType[Context, SearchResponse[T]](
      name = s"${entityOutput.name}sResponse",
      fields = fields[Context, SearchResponse[T]](
        Field("total", LongType, resolve = _.value.total),
        Field("hasNextPage", BooleanType, resolve = _.value.hasNextPage),
        Field("results", ListType(entityOutput), resolve = _.value.results)
      )
    )
}
