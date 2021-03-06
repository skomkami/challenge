package agh.edu.pl

import agh.edu.pl.error.DomainError

package object authservice {
  case class AuthServiceError(override val msg: String) extends DomainError(msg)
}
