package agh.edu.pl.graphql

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.filters.{ EntityFilter, EntityFilterSettings }
import agh.edu.pl.models.{ Entity, EntityId }
import agh.edu.pl.mutations.CreateEntitySettings
import io.circe.{ Decoder, Encoder }
import sangria.schema._

import scala.reflect.ClassTag

abstract class GraphqlEntity[Id <: EntityId, T <: Entity[Id]: Encoder: Decoder](
    implicit
    tag: ClassTag[T]
  ) {
  protected val typeName: String = tag.runtimeClass.getSimpleName

  def createEntitySettings: CreateEntitySettings[T, _ <: CreateEntity[T]]

  def GraphQLOutputType: ObjectType[Context, T]

  def filterSettings: EntityFilterSettings[_ <: EntityFilter[T]]

  def createMutation: Field[Context, Unit] = Field(
    name = s"create$typeName",
    fieldType = GraphQLOutputType,
    arguments = createEntitySettings.CreateEntityInput :: Nil,
    resolve =
      c => c.arg(createEntitySettings.CreateEntityInput).createEntity(c.ctx)
  )

  private val Id = Argument("id", StringType)
  private val Size = Argument("size", OptionInputType(IntType))
  private val Offset = Argument("offset", OptionInputType(IntType))

  protected def getAllQuery: Field[Context, Unit] = Field(
    name = s"all${typeName}s",
    fieldType = ListType(GraphQLOutputType),
    arguments = Size :: Offset :: filterSettings.FilterArgument :: Nil,
    resolve = c =>
      c.ctx
        .repository
        .getAll[T](
          filter = c.arg(filterSettings.FilterArgument).map(_.filters.toList),
          size = c.arg(Size),
          from = c.arg(Offset)
        )
  )

  protected def getByIdQuery: Field[Context, Unit] = Field(
    name = s"${getClass.getSimpleName.toLowerCase}",
    fieldType = OptionType(GraphQLOutputType),
    arguments = Id :: Nil,
    resolve = c =>
      c.ctx
        .repository
        .getById[T](createEntitySettings.idCodec.fromString(c.arg(Id)))
  )

  def queries: Seq[Field[Context, Unit]] =
    getAllQuery :: getByIdQuery :: Nil

}
