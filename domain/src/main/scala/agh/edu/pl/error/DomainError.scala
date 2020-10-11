package agh.edu.pl.error

case class DomainError(msg: String) extends Throwable(msg)
