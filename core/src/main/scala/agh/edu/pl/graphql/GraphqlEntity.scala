package agh.edu.pl.graphql

import agh.edu.pl.GraphQLSchema.{ Offset, Size }
import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.models.{ plural, Entity, EntityId }
import agh.edu.pl.mutations.EntityCommandSettings
import agh.edu.pl.queryparams.{
  EntityFilter,
  EntitySort,
  FilterBuilder,
  SortBuilder
}
import agh.edu.pl.repository.SearchResponse
import io.circe.{ Decoder, Encoder }
import sangria.schema._

import scala.reflect.ClassTag

abstract class GraphqlEntity[Id <: EntityId, T <: Entity[Id]: Encoder: Decoder](
    implicit
    tag: ClassTag[T]
  ) {
  protected val typeName: String = tag.runtimeClass.getSimpleName

  def createEntitySettings: EntityCommandSettings[T, _ <: CreateEntity[T]]

  def GraphQLOutputType: ObjectType[Context, T]

  def GraphQLSearchResponse: OutputType[SearchResponse[T]] = searchResponse(
    GraphQLOutputType
  )

  private lazy val filterInput: InputObjectType[EntityFilter[T]] =
    FilterBuilder.filterType

  lazy val filterArg: Argument[Option[EntityFilter[T]]] =
    Argument("filter", OptionInputType(filterInput))

  private lazy val sortInput: InputObjectType[EntitySort[T]] =
    SortBuilder.sortType(GraphQLOutputType)

  lazy val sortArg: Argument[Option[EntitySort[T]]] =
    Argument("orderBy", OptionInputType(sortInput))

  def createMutation: Field[Context, Unit] = Field(
    name = firstLetterToLower(createEntitySettings.getClass),
    fieldType = GraphQLOutputType,
    arguments = createEntitySettings.CommandInput :: Nil,
    resolve = c => c.arg(createEntitySettings.CommandInput).createEntity(c.ctx)
  )

  private val Id = Argument("id", createEntitySettings.idSettings.scalarAlias)

  protected def getAllQuery: Field[Context, Unit] = Field(
    name = s"all${plural(typeName)}",
    fieldType = GraphQLSearchResponse,
    arguments = Size :: Offset :: filterArg :: sortArg :: Nil,
    resolve = c =>
      c.ctx
        .repository
        .getAll[T](
          filters = c.arg(filterArg).map(_.filters),
          sorts = c.arg(sortArg).map(_.sorts),
          size = c.arg(Size),
          from = c.arg(Offset)
        )
  )

  protected def getByIdQuery: Field[Context, Unit] = Field(
    name = s"${typeName.toLowerCase}",
    fieldType = OptionType(GraphQLOutputType),
    arguments = Id :: Nil,
    resolve = c =>
      c.ctx
        .repository
        .getById[T](c.arg(Id))
  )

  def queries: Seq[Field[Context, Unit]] =
    getAllQuery :: getByIdQuery :: Nil

  def mutations: Seq[Field[Context, Unit]] =
    createMutation :: Nil

}
