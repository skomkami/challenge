package agh.edu.pl.filters

import java.time.OffsetDateTime

import agh.edu.pl.ids.{ LinkId, UserId }
import agh.edu.pl.models.Link
import sangria.macros.derive._
import sangria.schema.{ Argument, InputObjectType, OptionInputType }

case class LinksFilter(
    id: Option[LinkId],
    url: Option[String],
    description: Option[String],
    postedBy: Option[UserId],
    createdAt: Option[OffsetDateTime]
  ) extends EntityFilter[Link] {
  override def filters: Iterable[Filter] = {
    val idFilter = id.map(value => FilterEq("id", value.value))
    val urlFilter = url.map(value => FilterEq("url", value))
    val descriptionFilter =
      description.map(value => FilterEq("description", value))
    val postedByFilter =
      postedBy.map(value => FilterEq("postedBy", value.value))
    val createdAtFilter =
      createdAt.map(value => FilterEq("createdAt", value.toString))
    idFilter ++ urlFilter ++ descriptionFilter ++ postedByFilter ++ createdAtFilter ++ Nil
  }
}

case object LinksFilter extends EntityFilterSettings[LinksFilter] {
  import sangria.marshalling.circe._
  lazy val FilterInputType: InputObjectType[LinksFilter] =
    deriveInputObjectType[LinksFilter]()

  override def FilterArgument: Argument[Option[LinksFilter]] =
    Argument("filter", OptionInputType(FilterInputType))
}
