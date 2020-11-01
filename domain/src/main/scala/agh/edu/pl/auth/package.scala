package agh.edu.pl

import agh.edu.pl.error.DomainError

package object auth {
  case class AuthServiceError(override val msg: String) extends DomainError(msg)
}
