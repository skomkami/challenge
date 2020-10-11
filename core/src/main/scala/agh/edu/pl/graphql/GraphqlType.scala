package agh.edu.pl.graphql

import agh.edu.pl.commands.CreateEntity
import agh.edu.pl.context.Context
import agh.edu.pl.models.models.Identifiable
import agh.edu.pl.mutations.CreateEntitySettings
import io.circe.{ Decoder, Encoder }
import sangria.schema.{
  Argument,
  Field,
  ListType,
  ObjectType,
  OptionType,
  StringType
}

import scala.reflect.ClassTag

abstract class GraphqlType[T <: Identifiable[T]: Encoder: Decoder: ClassTag] {

//  protected def customSettings: Seq[DeriveObjectSetting[Unit, T]] = Nil
//
//  val IdentifiableType: InterfaceType[Unit, Identifiable[T]] = InterfaceType(
//    "Identifiable",
//    fields[Unit, Identifiable[T]](Field("id", IntType, resolve = _.value.id))
//  )

//  def GraphQLOutputType: ObjectType[Unit, T] = deriveObjectType[Unit, T](
//    customSettings.+:(Interfaces(PossibleInterface.convert(IdentifiableType)))
//  ) // TODO can I do it using macros or something

  protected val typeName: String =
    getClass.getSimpleName.replaceFirst("Graphql", "")

  def createEntitySettings: CreateEntitySettings[_ <: CreateEntity[T]]

  def GraphQLOutputType: ObjectType[Context, T]

  def createMutation: Field[Context, Unit] = Field(
    name = s"create$typeName",
    fieldType = GraphQLOutputType,
    arguments = createEntitySettings.CreateEntityInput :: Nil,
    resolve =
      c => c.arg(createEntitySettings.CreateEntityInput).newEntity(c.ctx)
  )

  val Id = Argument("id", StringType)

  protected def getAllQuery: Field[Context, Unit] = Field(
    name = s"all${typeName}s",
    fieldType = ListType(GraphQLOutputType),
    resolve = c => c.ctx.repository.getAll[T]()
  )

  protected def getByIdQuery: Field[Context, Unit] = Field(
    name = s"${getClass.getSimpleName.toLowerCase}",
    fieldType = OptionType(GraphQLOutputType),
    arguments = Id :: Nil,
    resolve = c => c.ctx.repository.getById(c.arg(Id))
  )

  def queries: Seq[Field[Context, Unit]] =
    getAllQuery :: getByIdQuery :: Nil

}
