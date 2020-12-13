package agh.edu.pl.filters

import java.time.OffsetDateTime

import agh.edu.pl.entities.UserChallengeActivity
import agh.edu.pl.ids.{ ChallengeId, UserId }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType, OptionInputType }

case class UserChallengeActivitiesFilter(
    userId: Option[UserId],
    challengeId: Option[ChallengeId],
    value: Option[Double],
    date: Option[OffsetDateTime]
  ) extends EntityFilter[UserChallengeActivity] {
  override def filters: Iterable[Filter] = {
    val userIdFilter = userId.map(value => FilterEq("userId", value.value))
    val challengeIdFilter =
      challengeId.map(value => FilterEq("challengeId", value.value))
    val valueFilter = value.map(value => FilterEq("value", value.toString))
    val dateFilter = date.map(value => FilterEq("date", value.toString))

    userIdFilter ++ challengeIdFilter ++ valueFilter ++ dateFilter ++ Nil
  }
}

case object UserChallengeActivitiesFilter
    extends EntityFilterSettings[UserChallengeActivitiesFilter] {
  import sangria.marshalling.circe._

  lazy val FilterInputType: InputObjectType[UserChallengeActivitiesFilter] =
    deriveInputObjectType[UserChallengeActivitiesFilter]()

  override def FilterArgument: Argument[Option[UserChallengeActivitiesFilter]] =
    Argument("filter", OptionInputType(FilterInputType))
}
