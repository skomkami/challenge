package agh.edu.pl

import agh.edu.pl.error.DomainError

package object graphqlserver {
  case class TooComplexQuery(override val msg: String = "Query is too complex.")
      extends DomainError(msg)
}
