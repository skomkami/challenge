package agh.edu.pl.server

import agh.edu.pl.GraphQLServer
import agh.edu.pl.config.ServiceConfig
import agh.edu.pl.elasticsearch.EsRepository
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ ElasticClient, ElasticProperties }
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.Json
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContextExecutor

object Server extends App {

  val config: ServiceConfig = ConfigSource.default.load[ServiceConfig] match {
    case Right(value)   => value
    case Left(failures) => throw new Exception(failures.toString)
  }

  private val elasticProps: ElasticProperties = ElasticProperties(
    s"http://${config.es.host}:${config.es.port}"
  )
  private val esClient: ElasticClient = ElasticClient(JavaClient(elasticProps))
  private val esRepository =
    EsRepository(esClient)(scala.concurrent.ExecutionContext.global)

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val server = GraphQLServer(esRepository)

  val route: Route =
    (post & path("graphql")) {
      entity(as[Json]) { requestJson =>
        server.endpoint(requestJson)
      }
    } ~ (get & path("graphiql")) {
      getFromResource("index.html")
    }

  val bindingFuture =
    Http()
      .newServerAt(config.server.bindHost, config.server.bindPort)
      .bind(route)
  println(
    s"open a browser with URL: http://${config.server.bindHost}:${config.server.bindPort}/graphiql"
  )

  def shutdown(): Unit =
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
