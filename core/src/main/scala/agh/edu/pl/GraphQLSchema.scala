package agh.edu.pl

import agh.edu.pl.context.Context
import agh.edu.pl.graphql.{ GraphqlLink, _ }
import agh.edu.pl.models._
import agh.edu.pl.models.models.{ Entity, EntityId }
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

  lazy val LinkType: ObjectType[Context, Link] = GraphQLLink.GraphQLOutputType
  lazy val UserType: ObjectType[Context, User] = GraphQLUser.GraphQLOutputType
  lazy val VoteType: ObjectType[Context, Vote] = GraphQLVote.GraphQLOutputType

  val schemaProviders: List[GraphqlEntity[_, _]] =
    List(GraphQLLink, GraphQLUser, GraphQLVote)

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
