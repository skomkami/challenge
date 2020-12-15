package agh.edu.pl.measures

import enumeratum.{ CirceEnum, Enum, EnumEntry }
import sangria.macros.derive.deriveEnumType
import sangria.schema.EnumType

sealed trait ValueSummarization extends EnumEntry

object ValueSummarization
    extends Enum[ValueSummarization]
       with CirceEnum[ValueSummarization] {
  case object Summarize extends ValueSummarization
  case object Best extends ValueSummarization

  implicit val EnumType: EnumType[ValueSummarization] =
    deriveEnumType[ValueSummarization]()

  override def values: IndexedSeq[ValueSummarization] = findValues
}
