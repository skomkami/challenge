package agh.edu.pl

import sangria.schema.{ OptionType, OutputType }

import scala.annotation.tailrec

package object queryparams {
  @tailrec
  private[queryparams] def extractFromOptionType(
      fieldType: OutputType[_]
    ): OutputType[_] =
    fieldType match {
      case OptionType(underlyingType) => extractFromOptionType(underlyingType)
      case baseType                   => baseType
    }
}
