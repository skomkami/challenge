package agh.edu.pl.filters

import agh.edu.pl.entities.User
import agh.edu.pl.ids.UserId
import agh.edu.pl.models.Gender
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.{ Argument, InputObjectType, OptionInputType }

case class UsersFilter(
    id: Option[UserId],
    name: Option[String],
    email: Option[String],
    gender: Option[Gender]
  ) extends EntityFilter[User] {
  override def filters: Iterable[Filter] = {
    val idFilter = id.map(value => FilterEq("id", value.value))
    val nameFilter = name.map(value => StringQuery("name", value))
    val emailFilter = email.map(value => StringQuery("email", value))
    val genderFilter = gender.map(value => FilterEq("gender", value.entryName))
    idFilter ++ nameFilter ++ emailFilter ++ genderFilter ++ Nil
  }
}

case object UsersFilter extends EntityFilterSettings[UsersFilter] {
  import sangria.marshalling.circe._

  lazy val FilterInputType: InputObjectType[UsersFilter] =
    deriveInputObjectType[UsersFilter]()

  override def FilterArgument: Argument[Option[UsersFilter]] =
    Argument("filter", OptionInputType(FilterInputType))
}
