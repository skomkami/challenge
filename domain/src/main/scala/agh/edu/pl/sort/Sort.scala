package agh.edu.pl.sort

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import sangria.macros.derive._
import sangria.schema.EnumType

case class Sort(field: String, order: SortOrder)

sealed trait SortOrder extends EnumEntry

object SortOrder extends Enum[SortOrder] with CirceEnum[SortOrder] {
  case object Desc extends SortOrder
  case object Asc extends SortOrder

  implicit val EnumType: EnumType[SortOrder] = deriveEnumType[SortOrder]()

  override def values: IndexedSeq[SortOrder] = findValues
}
