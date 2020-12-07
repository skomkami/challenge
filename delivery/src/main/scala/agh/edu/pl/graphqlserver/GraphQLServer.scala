package agh.edu.pl.graphqlserver

import agh.edu.pl.context.Context
import agh.edu.pl.exception.Handler
import agh.edu.pl.repository.Repository
import agh.edu.pl.GraphQLSchema
import agh.edu.pl.auth.AuthService
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.optics.JsonPath._
import io.circe.{ Json, JsonObject }
import org.keycloak.representations.AccessToken
import sangria.ast.Document
import sangria.execution.{ ErrorWithResolver, Executor, QueryAnalysisError }
import sangria.marshalling.circe.{
  CirceInputUnmarshaller,
  CirceResultMarshaller
}
import sangria.parser.QueryParser

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

case class GraphQLServer(repository: Repository, authService: AuthService) {
  def endpoint(
      requestJson: Json,
      token: Option[AccessToken]
    )(implicit
      ec: ExecutionContext
    ): Route = {

    val query = root.query.string.getOption(requestJson).getOrElse("")
    val variables = root.variables.obj.getOption(requestJson)
    val operationName = root.operationName.string.getOption(requestJson)

    QueryParser.parse(query) match {
      case Success(queryAst) =>
        val vars =
          variables
            .map(Json.fromJsonObject)
            .getOrElse(Json.fromJsonObject(JsonObject.empty))
        complete(
          executeGraphQLQuery(
            queryAst,
            operationName,
            vars,
            token
          )
        )

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
      vars: Json,
      token: Option[AccessToken]
    )(implicit
      ec: ExecutionContext
    ): Future[(StatusCode, Json)] = {

    val schema = token match {
      case Some(_) =>
        GraphQLSchema.SchemaDefinition
      case None => GraphQLSchema.SchemaDefinition
    }

    Executor
      .execute(
        schema = schema,
        queryAst = query,
        userContext = Context(repository, ec, authService),
        variables = vars,
        operationName = operation,
        exceptionHandler = Handler.exceptionHandler
      )
      .map(OK -> _)
      .recover {
        case error: QueryAnalysisError =>
          BadRequest -> error.resolveError
        case error: ErrorWithResolver =>
          InternalServerError -> error.resolveError
      }
  }
}
