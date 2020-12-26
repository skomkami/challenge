package agh.edu.pl.exceptionhandler

import agh.edu.pl.error.DomainError
import sangria.execution.{ ExceptionHandler, HandledException }

case object Handler {
  val exceptionHandler = ExceptionHandler {
    case (_, error: DomainError) =>
      HandledException(error.getMessage)
  }
}
