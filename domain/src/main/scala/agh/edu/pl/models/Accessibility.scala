package agh.edu.pl.models

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import sangria.macros.derive._
import sangria.schema.EnumType

sealed trait Accessibility extends EnumEntry

object Accessibility extends Enum[Accessibility] with CirceEnum[Accessibility] {

  case object Public extends Accessibility
  case object Private extends Accessibility

  implicit val EnumType: EnumType[Accessibility] =
    deriveEnumType[Accessibility]()

  override def values: IndexedSeq[Accessibility] = findValues
}
