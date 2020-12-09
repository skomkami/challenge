package agh.edu.pl.measures

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import sangria.macros.derive.deriveEnumType
import sangria.schema.EnumType

sealed trait ValueOrder extends EnumEntry

object ValueOrder extends Enum[ValueOrder] with CirceEnum[ValueOrder] {
  case object BiggerWins extends ValueOrder
  case object SmallerWins extends ValueOrder

  implicit val GenderEnum: EnumType[ValueOrder] = deriveEnumType[ValueOrder]()

  override def values: IndexedSeq[ValueOrder] = findValues
}
