package agh.edu.pl

import agh.edu.pl.context.Context
import agh.edu.pl.entities.{
  Challenge,
  UserChallengeActivity,
  UserChallengeSummary
}
import agh.edu.pl.graphql.{ GraphqlLink, _ }
import agh.edu.pl.models._
import agh.edu.pl.models.{ Entity, EntityId }
import sangria.schema.{ Field, ObjectType, _ }

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

//  val linkByUserRel: Relation[Link, Link, String] =
//    Relation[Link, String]("postedByUser", l => Seq(l.postedBy.value))
//  val voteByUserRel: Relation[Vote, Vote, String] =
//    Relation[Vote, String]("votedByUser", v => Seq(v.userId.value))
//  val voteByLinkRel: Relation[Vote, Vote, String] =
//    Relation[Vote, String]("votesForLink", v => Seq(v.linkId.value))

  val GraphQLLink = GraphqlLink()
  val GraphQLUser = GraphqlUser()
  val GraphQLVote = GraphqlVote()
  val GraphQLChallenge = GraphqlChallenge()
  val GraphQLUserChallengeSummary = GraphqlUserChallengeSummary()
  val GraphQLUserChallengeActivity = GraphqlUserChallengeActivity()

  lazy val LinkType: ObjectType[Context, Link] = GraphQLLink.GraphQLOutputType
  lazy val UserType: ObjectType[Context, User] = GraphQLUser.GraphQLOutputType
  lazy val VoteType: ObjectType[Context, Vote] = GraphQLVote.GraphQLOutputType
  lazy val ChallengeType: ObjectType[Context, Challenge] =
    GraphQLChallenge.GraphQLOutputType
  lazy val UserChallengeSummaryType: ObjectType[Context, UserChallengeSummary] =
    GraphQLUserChallengeSummary.GraphQLOutputType
  lazy val UserChallengeActivityType
      : ObjectType[Context, UserChallengeActivity] =
    GraphQLUserChallengeActivity.GraphQLOutputType

  val schemaProviders: List[GraphqlEntity[_, _]] =
    List(
      GraphQLLink,
      GraphQLUser,
      GraphQLVote,
      GraphQLChallenge,
      GraphQLUserChallengeSummary,
      GraphQLUserChallengeActivity
    )

  lazy val Mutation: ObjectType[Context, Unit] = ObjectType(
    "Mutation",
    fields[Context, Unit](
      schemaProviders.map(_.createMutation): _*
    )
  )

  val QueryType: ObjectType[Context, Unit] = ObjectType(
    "Query",
    fields[Context, Unit](schemaProviders.flatMap(_.queries): _*)
  )
  val SchemaDefinition = Schema(QueryType, Some(Mutation))
}
