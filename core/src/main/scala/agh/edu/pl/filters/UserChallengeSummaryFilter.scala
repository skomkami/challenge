package agh.edu.pl.filters

import java.time.OffsetDateTime

import agh.edu.pl.entities.UserChallengeSummary
import agh.edu.pl.ids.{ ChallengeId, UserChallengeSummaryId, UserId }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType, OptionInputType }

case class UserChallengeSummariesFilter(
    id: Option[UserChallengeSummaryId],
    userId: Option[UserId],
    challengeId: Option[ChallengeId],
    summaryValue: Option[Double],
    lastActive: Option[OffsetDateTime]
  ) extends EntityFilter[UserChallengeSummary] {
  override def filters: Iterable[Filter] = {
    val idFilter = id.map(value => FilterEq("id", value.value))
    val userIdFilter = userId.map(value => FilterEq("userId", value.value))
    val challengeIdFilter =
      challengeId.map(value => FilterEq("challengeId", value.value))
    val summaryValueFilter =
      summaryValue.map(value => FilterEq("summaryValue", value.toString))
    val lastActiveFilter =
      lastActive.map(value => FilterEq("lastActive", value.toString))

    idFilter ++ userIdFilter ++ challengeIdFilter ++ summaryValueFilter ++ lastActiveFilter ++ Nil
  }
}

case object UserChallengeSummariesFilter
    extends EntityFilterSettings[UserChallengeSummariesFilter] {
  import sangria.marshalling.circe._

  lazy val FilterInputType: InputObjectType[UserChallengeSummariesFilter] =
    deriveInputObjectType[UserChallengeSummariesFilter]()

  override def FilterArgument: Argument[Option[UserChallengeSummariesFilter]] =
    Argument("filter", OptionInputType(FilterInputType))
}
