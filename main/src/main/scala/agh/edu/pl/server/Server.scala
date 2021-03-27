package agh.edu.pl.server

import agh.edu.pl.authorization.AuthorizationHandler
import agh.edu.pl.backends.AkkaHttpBackendL
import agh.edu.pl.calculator.{ PositionsCalculator, UpdatePositionsProtocol }
import agh.edu.pl.config.ServiceConfig
import agh.edu.pl.elasticsearch.EsRepository
import agh.edu.pl.graphqlserver.GraphQLServer
import agh.edu.pl.security.KeycloakService
import akka.actor.ActorSystem
import akka.actor.typed.{ ActorSystem => TypedSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{
  AuthorizationFailedRejection,
  RejectionHandler,
  Route
}
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.fullfacing.keycloak4s.admin.client.KeycloakClient
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ ElasticClient, ElasticProperties }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.Json
import monix.eval.Task
import org.keycloak.adapters.KeycloakDeploymentBuilder
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import sttp.client.akkahttp.AkkaHttpBackend

import scala.concurrent.ExecutionContextExecutor

object Server extends App with CorsSupport with AuthorizationHandler {

  val config: ServiceConfig = ConfigSource.default.load[ServiceConfig] match {
    case Right(value)   => value
    case Left(failures) => throw new Exception(failures.toString)
  }

  override lazy val keycloakDeployment =
    KeycloakDeploymentBuilder.build(config.keycloak.buildAdapterConfig)

  val challengeActorSystem: TypedSystem[UpdatePositionsProtocol] =
    TypedSystem(PositionsCalculator.behavior, "challengeActorSystem")
  implicit val system: ActorSystem = challengeActorSystem.classicSystem
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val elasticProps: ElasticProperties = ElasticProperties(
    s"http://${config.es.host}:${config.es.port}"
  )
  private val esClient: ElasticClient = ElasticClient(JavaClient(elasticProps))
  private val esRepository =
    EsRepository(esClient)(scala.concurrent.ExecutionContext.global)

  val kcClient: KeycloakClient[Task, Source[ByteString, Any]] = {
    implicit val backend: AkkaHttpBackendL =
      new AkkaHttpBackendL(
        AkkaHttpBackend()
      )
    new KeycloakClient[Task, Source[ByteString, Any]](
      config.keycloak.toConfigWithAuth
    )
  }

  private val kcService = KeycloakService()(kcClient)

  implicit def rejectionHandler: RejectionHandler = RejectionHandler
    .newBuilder()
    .handle {
      case AuthorizationFailedRejection =>
        complete(StatusCodes.Unauthorized -> None)
    }
    .result()
    .mapRejectionResponse(addCORSHeaders)

  val server = GraphQLServer(esRepository, kcService, challengeActorSystem)

  val route: Route =
    (post & path("graphql")) {
      entity(as[Json]) { requestJson =>
        authorize { token =>
          server.endpoint(requestJson, token)
        }
      }
    } ~ (get & path("graphiql")) {
      getFromResource("index.html")
    }

  val bindingFuture =
    Http()
      .newServerAt(config.server.bindHost, config.server.bindPort)
      .bind(corsHandler(route))
  scribe.info(
    s"open a browser with URL: http://${config.server.bindHost}:${config.server.bindPort}/graphiql"
  )

  def shutdown(): Unit =
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
