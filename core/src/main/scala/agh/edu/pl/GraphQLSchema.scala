package agh.edu.pl

import agh.edu.pl.context.Context
import agh.edu.pl.graphql.{ GraphqlLink, _ }
import agh.edu.pl.models._
import agh.edu.pl.models.models.Identifiable
import sangria.execution.deferred.Relation
import sangria.schema.{ Field, ObjectType, _ }

object GraphQLSchema {

  implicit lazy val gqlOffsetDateTime =
    CustomScalars.GraphQLOffsetDateTime

  val IdentifiableType: InterfaceType[Context, Identifiable[_]] =
    InterfaceType(
      "Identifiable",
      fields[Context, Identifiable[_]](
        Field("id", StringType, resolve = _.value.id)
      )
    )

  val linkByUserRel: Relation[Link, Link, String] =
    Relation[Link, String]("postedByUser", l => Seq(l.postedBy))
  val voteByUserRel: Relation[Vote, Vote, String] =
    Relation[Vote, String]("votedByUser", v => Seq(v.userId))
  val voteByLinkRel: Relation[Vote, Vote, String] =
    Relation[Vote, String]("votesForLink", v => Seq(v.linkId))
//
//  val linksFetcher: Fetcher[MyContext, Link, Link, String] = Fetcher.rel(
//    (ctx: MyContext, ids: Seq[String]) => ctx.esRepository.getByIds[Link](ids),
//    (ctx: MyContext, ids: RelationIds[Link]) =>
//      ctx.esRepository.getByIds[Link](ids(linkByUserRel)),
//  )
//
//  val usersFetcher: Fetcher[MyContext, User, User, String] = Fetcher(
//    (ctx: MyContext, ids: Seq[String]) => ctx.esRepository.getByIds[User](ids)
//  )
//
//  val votesFetcher: Fetcher[MyContext, Vote, Vote, String] = Fetcher.rel(
//    (ctx: MyContext, ids: Seq[String]) => ctx.esRepository.getByIds[Vote](ids),
//    (ctx: MyContext, ids: RelationIds[Vote]) =>
//      ctx.dao.getVotesByRelationIds(ids)
//  )

  val GraphQLLink = GraphqlLink()
  val GraphQLUser = GraphqlUser()
  val GraphQLVote = GraphqlVote()

  lazy val LinkType: ObjectType[Context, Link] = GraphQLLink.GraphQLOutputType
  lazy val UserType: ObjectType[Context, User] = GraphQLUser.GraphQLOutputType
  lazy val VoteType: ObjectType[Context, Vote] = GraphQLVote.GraphQLOutputType

  val schemaProviders: List[GraphqlType[_]] =
    List(GraphQLLink, GraphQLUser, GraphQLVote)

  lazy val Mutation: ObjectType[Context, Unit] = ObjectType(
    "Mutation",
    fields[Context, Unit](schemaProviders.map(_.createMutation): _*)
  )

  val QueryType: ObjectType[Context, Unit] = ObjectType(
    "Query",
    fields[Context, Unit](schemaProviders.flatMap(_.queries): _*)
  )
  val SchemaDefinition = Schema(QueryType, Some(Mutation))
}
