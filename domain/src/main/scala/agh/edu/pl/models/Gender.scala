package agh.edu.pl.models

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import sangria.macros.derive._
import sangria.schema.EnumType

sealed trait Gender extends EnumEntry

object Gender extends Enum[Gender] with CirceEnum[Gender] {
  case object Male extends Gender
  case object Female extends Gender

  implicit val EnumType: EnumType[Gender] = deriveEnumType[Gender]()

  override def values: IndexedSeq[Gender] = findValues
}
