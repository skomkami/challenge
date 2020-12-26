package agh.edu.pl.schema

import sangria.schema._
import agh.edu.pl.entities._
import agh.edu.pl.graphql._
import agh.edu.pl.context.Context
import com.softwaremill.quicklens._

object GraphQLSchema {

  val Size = Argument("size", OptionInputType(IntType))
  val Offset = Argument("offset", OptionInputType(IntType))

//  val EntityType: InterfaceType[Context, Entity[_ <: EntityId]] =
//    InterfaceType(
//      "Entity",
//      fields[Context, Entity[_ <: EntityId]](
//        Field(
//          "id",
//          StringType,
//          resolve = _.value.id.value
//        )
//      )
//    )

  val GraphQLUser: GraphqlUser = GraphqlUser()
  val GraphQLChallenge: GraphqlChallenge = GraphqlChallenge()
  val GraphQLUserChallengeSummary: GraphqlUserChallengeSummary =
    GraphqlUserChallengeSummary()
  val GraphQLUserChallengeActivity: GraphqlUserChallengeActivity =
    GraphqlUserChallengeActivity()
  val GraphQLInvitation: GraphqlInvitation = GraphqlInvitation()

  lazy val UserType: ObjectType[Context, User] = GraphQLUser.GraphQLOutputType
  lazy val ChallengeType: ObjectType[Context, Challenge] =
    GraphQLChallenge.GraphQLOutputType
  lazy val UserChallengeSummaryType: ObjectType[Context, UserChallengeSummary] =
    GraphQLUserChallengeSummary.GraphQLOutputType
  lazy val UserChallengeActivityType
      : ObjectType[Context, UserChallengeActivity] =
    GraphQLUserChallengeActivity.GraphQLOutputType
  lazy val InvitationType: ObjectType[Context, Invitation] =
    GraphQLInvitation.GraphQLOutputType

  val schemaProviders: List[GraphqlEntity[_, _]] =
    List(
      GraphQLUser,
      GraphQLChallenge,
      GraphQLUserChallengeSummary,
      GraphQLUserChallengeActivity,
      GraphQLInvitation
    )

  private lazy val Mutation: ObjectType[Context, Unit] = ObjectType(
    "Mutation",
    fields[Context, Unit](
      schemaProviders.flatMap(_.mutations): _*
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
