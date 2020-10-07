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
        Field("id", IntType, resolve = _.value.id)
      )
    )

  val linkByUserRel: Relation[Link, Link, Int] =
    Relation[Link, Int]("postedByUser", l => Seq(l.postedBy))
  val voteByUserRel: Relation[Vote, Vote, Int] =
    Relation[Vote, Int]("votedByUser", v => Seq(v.userId))
  val voteByLinkRel: Relation[Vote, Vote, Int] =
    Relation[Vote, Int]("votesForLink", v => Seq(v.linkId))
//
//  val linksFetcher: Fetcher[MyContext, Link, Link, Int] = Fetcher.rel(
//    (ctx: MyContext, ids: Seq[Int]) => ctx.esRepository.getByIds[Link](ids),
//    (ctx: MyContext, ids: RelationIds[Link]) =>
//      ctx.esRepository.getByIds[Link](ids(linkByUserRel)),
//  )
//
//  val usersFetcher: Fetcher[MyContext, User, User, Int] = Fetcher(
//    (ctx: MyContext, ids: Seq[Int]) => ctx.esRepository.getByIds[User](ids)
//  )
//
//  val votesFetcher: Fetcher[MyContext, Vote, Vote, Int] = Fetcher.rel(
//    (ctx: MyContext, ids: Seq[Int]) => ctx.esRepository.getByIds[Vote](ids),
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
