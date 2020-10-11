package agh.edu.pl.context

import agh.edu.pl.repository.Repository

import scala.concurrent.ExecutionContext

case class Context(repository: Repository, ec: ExecutionContext) {}
