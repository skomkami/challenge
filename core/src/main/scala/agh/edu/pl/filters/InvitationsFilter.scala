package agh.edu.pl.filters

import java.time.OffsetDateTime

import agh.edu.pl.entities.Invitation
import agh.edu.pl.ids.{ ChallengeId, UserId }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType, OptionInputType }

case class InvitationsFilter(
    forUser: Option[UserId],
    fromUser: Option[UserId],
    toChallenge: Option[ChallengeId],
    read: Option[Boolean],
    sendTime: Option[OffsetDateTime]
  ) extends EntityFilter[Invitation] {
  override def filters: Iterable[Filter] = {

    val forUserFilter =
      forUser.map(value => StringQuery("forUser", value.value))
    val fromUserFilter =
      fromUser.map(value => StringQuery("fromUser", value.value))
    val toChallengeFilter =
      toChallenge.map(value => StringQuery("toChallenge", value.value))
    val readFilter = read.map(value => FilterEq("read", value.toString))
    val sendTimeFilter =
      sendTime.map(value => FilterEq("sendTime", value.toString))

    forUserFilter ++ fromUserFilter ++ toChallengeFilter ++ readFilter ++ sendTimeFilter ++ Nil
  }
}

case object InvitationsFilter extends EntityFilterSettings[InvitationsFilter] {
  import sangria.marshalling.circe._

  lazy val FilterInputType: InputObjectType[InvitationsFilter] =
    deriveInputObjectType[InvitationsFilter]()

  override def FilterArgument: Argument[Option[InvitationsFilter]] =
    Argument("filter", OptionInputType(FilterInputType))
}
