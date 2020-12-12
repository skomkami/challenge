package agh.edu.pl.elasticsearch

import agh.edu.pl.error.DomainError
import agh.edu.pl.filters.{ Filter, FilterEq, StringQuery }
import agh.edu.pl.models.{ plural, Entity, EntityId }
import agh.edu.pl.repository.Repository
import agh.edu.pl.response.SearchResponse
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery
import com.sksamuel.elastic4s.requests.searches.queries.{
  BoolQuery,
  Query,
  RegexQuery
}
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
    tag.runtimeClass.getSimpleName.toLowerCase.pipe(plural)

  override def getAll[E](
      filters: Option[List[Filter]] = None,
      size: Option[Int] = None,
      offset: Option[Int] = None
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[SearchResponse[E]] = {
    val pageSize = size.getOrElse(10)
    val from = offset.getOrElse(0)
    elasticClient
      .execute {
        search(INDEX_NAME)
          .bool(buildQuery(filters))
          .size(pageSize)
          .from(from)
      }
      .map { resp =>
        val hits = resp.result.hits.hits
        val total = resp.result.totalHits
        val entities =
          hits.toIndexedSeq.map(_.sourceAsString).map(decodeSource(_)(decoder))
        val hasNextPage = total - from - pageSize > 0

        SearchResponse[E](total, hasNextPage, entities.toList)
      }
  }

  private def buildQuery(queryFilter: Option[List[Filter]]): BoolQuery = {
    val qMusts = mutable.ListBuffer[Query]()
    qMusts += matchAllQuery()

    queryFilter.getOrElse(Nil).collect {
      case StringQuery(field, value) =>
        qMusts += RegexQuery(field, value)
    }

    val qFilters = queryFilter.getOrElse(Nil).collect {
      case FilterEq(field, value) => MatchQuery(field, value)
    }

    must(qMusts.toSeq).filter(qFilters)
  }

  override def create[E <: Entity[_ <: EntityId]](
      entity: E
    )(implicit
      tag: ClassTag[E],
      encoder: Encoder[E]
    ): Future[E] =
    elasticClient
      .execute {
        indexInto(INDEX_NAME)
          .id(entity.id.value)
          .source(entity.asJson.noSpaces)
      }
      .map(_ => entity)

  def getById[E <: Entity[_]](
      id: E#IdType
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[E] =
    getByIdOpt(id).map { entityOpt =>
      entityOpt.getOrElse {
        val className = tag.runtimeClass.getSimpleName
        throw NotFoundEntity(className, id.value)
      }
    }

  def getByIdOpt[E <: Entity[_]](
      id: E#IdType
    )(implicit
      tag: ClassTag[E],
      decoder: Decoder[E]
    ): Future[Option[E]] =
    elasticClient
      .execute {
        get(INDEX_NAME, id.value)
      }
      .map { resp =>
        if (!resp.result.found)
          None
        else
          Some(
            resp
              .result
              .sourceAsString
              .pipe(decodeSource(_)(decoder))
          )
      }

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

  case class NotFoundEntity(entityType: String, id: String)
      extends DomainError(s"""Not found entity $entityType with id: "$id" """)

  override def update[E <: Entity[_ <: EntityId]](
      entity: E
    )(implicit
      tag: ClassTag[E],
      encoder: Encoder[E]
    ): Future[E] = elasticClient
    .execute {
      updateById(INDEX_NAME, entity.id.value)
        .docAsUpsert(entity.asJson.noSpaces)
    }
    .map(_ => entity)

  override def delete[E <: Entity[_]](
      entityId: E#IdType
    )(implicit
      tag: ClassTag[E]
    ): Future[E#IdType] = elasticClient
    .execute {
      deleteById(INDEX_NAME, entityId.value)
    }
    .map { resp =>
      if (resp.result.result == "not_found") {
        val className = tag.runtimeClass.getSimpleName
        throw NotFoundEntity(className, entityId.value)
      }
      else entityId
    }

  override def updateMany[E <: Entity[_ <: EntityId]](
      entities: Seq[E]
    )(implicit
      tag: ClassTag[E],
      encoder: Encoder[E]
    ): Future[Seq[EntityId]] = elasticClient
    .execute {
      val operations = entities.map { entity =>
        updateById(INDEX_NAME, entity.id.value)
          .docAsUpsert(entity.asJson.noSpaces)
      }
      bulk(operations)
    }
    .map { resp =>
      if (resp.result.hasFailures) {
        throw ???
      }
      else {
        entities.map(_.id)
      }
    }

  case class NotRefreshedIndex(index: String)
      extends DomainError(
        s"""Something went wrong while refreshing index: $index """
      )

  override def forceRefresh[E <: Entity[_]](
      implicit
      tag: ClassTag[E]
    ): Future[Unit] = elasticClient
    .execute {
      refreshIndex(INDEX_NAME)
    }
    .map { resp =>
      if (resp.isError) {
        throw NotRefreshedIndex(INDEX_NAME)
      }
      else ()
    }

  override def allCount[E <: Entity[_]](
      implicit
      tag: ClassTag[E]
    ): Future[Long] = elasticClient
    .execute {
      count(INDEX_NAME)
    }
    .map(_.result.count)
}
