package agh.edu.pl.graphqlserver

import agh.edu.pl.context.Context
import agh.edu.pl.exceptionhandler.Handler
import agh.edu.pl.repository.Repository
import agh.edu.pl.authservice.AuthService
import agh.edu.pl.ids.UserId
import agh.edu.pl.schema.GraphQLSchema
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.optics.JsonPath._
import io.circe.{ Json, JsonObject }
import org.keycloak.representations.AccessToken
import sangria.ast.Document
import sangria.execution.{
  ErrorWithResolver,
  Executor,
  QueryAnalysisError,
  QueryReducer
}
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

    val userId: Option[UserId] = token.map(_.getEmail).map(UserId.fromEmail)

    Executor
      .execute(
        schema = schema,
        queryAst = query,
        userContext = Context(repository, ec, authService, userId),
        variables = vars,
        operationName = operation,
        exceptionHandler = Handler.exceptionHandler,
        queryReducers = List(
          QueryReducer.rejectMaxDepth[Context](GraphQLSchema.maxQueryDepth),
          QueryReducer.rejectComplexQueries[Context](
            GraphQLSchema.maxQueryComplexity,
            (_, _) => TooComplexQuery()
          )
        )
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
