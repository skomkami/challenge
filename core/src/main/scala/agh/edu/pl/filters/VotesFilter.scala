package agh.edu.pl.filters

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ LinkId, UserId, VoteId }
import agh.edu.pl.models.Vote
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType, OptionInputType }

case class VotesFilter(
    id: Option[VoteId],
    createdAt: Option[OffsetDateTime],
    userId: Option[UserId],
    linkId: Option[LinkId]
  ) extends EntityFilter[Vote] {
  override def filters: Iterable[Filter] = {
    val idFilter = id.map(value => FilterEq("id", value.value))
    val createdAtFilter =
      createdAt.map(value => FilterEq("createdAt", value.toString))
    val userIdFilter = userId.map(value => FilterEq("userId", value.value))
    val linkIdFilter = linkId.map(value => FilterEq("linkId", value.value))
    idFilter ++ createdAtFilter ++ userIdFilter ++ linkIdFilter ++ Nil
  }
}

case object VotesFilter extends EntityFilterSettings[VotesFilter] {
  import sangria.marshalling.circe._

  lazy val FilterInputType: InputObjectType[VotesFilter] =
    deriveInputObjectType[VotesFilter]()

  override def FilterArgument: Argument[Option[VotesFilter]] =
    Argument("filter", OptionInputType(FilterInputType))
}
