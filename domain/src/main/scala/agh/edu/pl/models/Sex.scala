package agh.edu.pl.models

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import sangria.macros.derive._
import sangria.schema.EnumType

sealed trait Sex extends EnumEntry

object Sex extends Enum[Sex] with CirceEnum[Sex] {
  case object Male extends Sex
  case object Female extends Sex

  implicit val SexEnum: EnumType[Sex] = deriveEnumType[Sex]()

  override def values: IndexedSeq[Sex] = findValues
}
