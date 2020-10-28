package agh.edu.pl

import agh.edu.pl.context.Context
import agh.edu.pl.entities.{
  Challenge,
  User,
  UserChallengeActivity,
  UserChallengeSummary
}
import agh.edu.pl.graphql._
import agh.edu.pl.models.{ Entity, EntityId }
import sangria.schema.{ Field, ObjectType, _ }
import com.softwaremill.quicklens._

object GraphQLSchema {

  implicit lazy val gqlOffsetDateTime =
    CustomScalars.GraphQLOffsetDateTime

  val EntityType: InterfaceType[Context, Entity[_ <: EntityId]] =
    InterfaceType(
      "Entity",
      fields[Context, Entity[_ <: EntityId]](
        Field(
          "id",
          StringType,
          resolve = _.value.id.value
        )
      )
    )

  private val GraphQLUser = GraphqlUser()
  private val GraphQLChallenge = GraphqlChallenge()
  private val GraphQLUserChallengeSummary = GraphqlUserChallengeSummary()
  private val GraphQLUserChallengeActivity = GraphqlUserChallengeActivity()

  lazy val UserType: ObjectType[Context, User] = GraphQLUser.GraphQLOutputType
  lazy val ChallengeType: ObjectType[Context, Challenge] =
    GraphQLChallenge.GraphQLOutputType
  lazy val UserChallengeSummaryType: ObjectType[Context, UserChallengeSummary] =
    GraphQLUserChallengeSummary.GraphQLOutputType
  lazy val UserChallengeActivityType
      : ObjectType[Context, UserChallengeActivity] =
    GraphQLUserChallengeActivity.GraphQLOutputType

  val schemaProviders: List[GraphqlEntity[_, _]] =
    List(
      GraphQLUser,
      GraphQLChallenge,
      GraphQLUserChallengeSummary,
      GraphQLUserChallengeActivity
    )

  private lazy val Mutation: ObjectType[Context, Unit] = ObjectType(
    "Mutation",
    fields[Context, Unit](
      schemaProviders.map(_.createMutation): _*
    )
  )

  private val QueryType: ObjectType[Context, Unit] = ObjectType(
    "Query",
    fields[Context, Unit](schemaProviders.flatMap(_.queries): _*)
  )

  val SchemaDefinition: Schema[Context, Unit] =
    Schema(QueryType, Some(Mutation))

  val PublicSchemaDefinition: Schema[Context, Unit] =
    SchemaDefinition
      .modify(_.query.fieldsFn)
      .setTo(() => GraphQLUser.queries.toList)
      .modify(_.mutation.each.fieldsFn)
      .setTo(() => GraphQLUser.createMutation :: Nil)
}
