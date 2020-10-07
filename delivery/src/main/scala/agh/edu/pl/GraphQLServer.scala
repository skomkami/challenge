package agh.edu.pl

import agh.edu.pl.context.Context
import agh.edu.pl.repository.Repository
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.optics.JsonPath._
import io.circe.parser._
import io.circe.{ Json, JsonObject }
import sangria.ast.Document
import sangria.execution.{ ErrorWithResolver, Executor, QueryAnalysisError }
import sangria.marshalling.circe.{
  CirceInputUnmarshaller,
  CirceResultMarshaller
}
import sangria.parser.QueryParser

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

case class GraphQLServer(repository: Repository) {
  def endpoint(requestJson: Json)(implicit ec: ExecutionContext): Route = {

    val query = root.query.string.getOption(requestJson).getOrElse("")
    val variables = root.variables.string.getOption(requestJson)
    val operationName = root.operationName.string.getOption(requestJson)

    QueryParser.parse(query) match {
      case Success(queryAst) =>
        parse(variables.getOrElse("{}")) match {
          case Right(vars) =>
            complete(executeGraphQLQuery(queryAst, operationName, vars))
          case Left(parsingFailure) =>
            complete(
              BadRequest,
              JsonObject("error" -> Json.fromString(parsingFailure.getMessage))
            )
        }
      case Failure(error) =>
        complete(
          BadRequest,
          JsonObject("error" -> Json.fromString(error.getMessage))
        )
    }

  }

  private def executeGraphQLQuery(
      query: Document,
      operation: Option[String],
      vars: Json
    )(implicit
      ec: ExecutionContext
    ): Future[(StatusCode, Json)] =
    Executor
      .execute(
        GraphQLSchema.SchemaDefinition,
        query,
        Context(repository),
        variables = vars,
        operationName = operation
      )
      .map(OK -> _)
      .recover {
        case error: QueryAnalysisError =>
          BadRequest -> error.resolveError
        case error: ErrorWithResolver =>
          InternalServerError -> error.resolveError
      }

}
