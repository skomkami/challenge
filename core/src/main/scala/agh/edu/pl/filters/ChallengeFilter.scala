package agh.edu.pl.filters

import java.time.OffsetDateTime

import agh.edu.pl.entities.Challenge
import agh.edu.pl.ids.{ ChallengeId, UserId }
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType, OptionInputType }

case class ChallengesFilter(
    id: Option[ChallengeId],
    name: Option[String],
    createdBy: Option[UserId],
    createdOn: Option[OffsetDateTime],
    finishesOn: Option[OffsetDateTime]
  ) extends EntityFilter[Challenge] {
  override def filters: Iterable[Filter] = {
    val nameFilter = name.map(value => FilterEq("name", value))
    val createdByFilter =
      createdBy.map(value => FilterEq("createdBy", value.value))
    val createdOnFilter =
      createdOn.map(value => FilterEq("createdOn", value.toString))
    val finishesOnFilter =
      finishesOn.map(value => FilterEq("finishesOn", value.toString))
    nameFilter ++
      createdByFilter ++
      createdOnFilter ++
      finishesOnFilter ++ Nil
  }
}

case object ChallengesFilter extends EntityFilterSettings[ChallengesFilter] {
  import sangria.marshalling.circe._
  lazy val FilterInputType: InputObjectType[ChallengesFilter] =
    deriveInputObjectType[ChallengesFilter]()

  override def FilterArgument: Argument[Option[ChallengesFilter]] =
    Argument("filter", OptionInputType(FilterInputType))
}
