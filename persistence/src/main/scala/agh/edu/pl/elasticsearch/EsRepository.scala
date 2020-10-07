package agh.edu.pl.elasticsearch

import java.util.concurrent.atomic.AtomicInteger

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.models.models.Identifiable
import agh.edu.pl.repository.{ Filter, Repository }
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery
import com.sksamuel.elastic4s.requests.searches.queries.{ BoolQuery, Query }
import io.circe.parser._
import io.circe.syntax._
import io.circe.{ Decoder, Encoder }

import scala.collection.mutable
import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.ClassTag
import scala.util.chaining._

case class EsRepository(
    elasticClient: ElasticClient
  )(implicit
    ec: ExecutionContext
  ) extends Repository {

  private def INDEX_NAME[E](implicit tag: ClassTag[E]): String =
    s"${tag.runtimeClass.getSimpleName.toLowerCase}s"

  override def getAll[E](
      filter: Option[Filter]
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[Seq[E]] =
    elasticClient
      .execute {
        search(INDEX_NAME).bool(buildQuery(filter))
      }
      .map(resp => resp.result.hits.hits)
      .map { hits =>
        hits.toIndexedSeq.map(_.sourceAsString).map(decodeSource(_)(decoder))
      }

  def buildQuery(queryFilter: Option[Filter]): BoolQuery = {
    val qMusts = mutable.ListBuffer[Query]()
    val qFilters = mutable.ListBuffer[Query]()

    qMusts += matchAllQuery()
    queryFilter.foreach {
      case Filter(field, value) => qFilters += MatchQuery(field, value)
    }

    must(qMusts.toSeq).filter(qFilters)
  }

  override def create[E <: Identifiable[E]](
      createEntityInput: CreateEntity[E]
    )(implicit
      tag: ClassTag[E],
      encoder: Encoder[E]
    ): Future[E] = {
//    val newId: String = UUID.randomUUID().toString
    val newId: Int = EsRepository.idGenerator.getAndIncrement()
    val newEntity = createEntityInput.toEntity.withId(newId)

    elasticClient
      .execute {
        indexInto(INDEX_NAME)
          .id(newId.toString)
          .source(newEntity.asJson.noSpaces)
      }
      .map(_ => newEntity)
  }

//  https://www.elastic.co/guide/en/elasticsearch/reference/6.8/query-dsl-ids-query.html
  override def getByIds[E](
      ids: Seq[Int]
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[Seq[E]] =
    elasticClient
      .execute {
        search(INDEX_NAME).query(idsQuery(ids.map(_.toString)))
      }
      .map(resp => resp.result.hits.hits)
      .map { hits =>
        hits.toIndexedSeq.map(_.sourceAsString).map(decodeSource(_)(decoder))
      }

  override def getById[E](
      id: Int
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[E] =
    elasticClient
      .execute {
        get(INDEX_NAME, id.toString)
      }
      .map(resp => resp.result.sourceAsString)
      .map(decodeSource(_)(decoder))

  private def decodeSource[E](
      source: String
    )(implicit
      decoder: Decoder[E]
    ): E =
    source.pipe(decode(_)) match {
      case Left(parsingError) =>
        throw new Exception(parsingError.getMessage)
      case Right(value) => value
    }
}

object EsRepository {
  val idGenerator: AtomicInteger = new AtomicInteger()
}
