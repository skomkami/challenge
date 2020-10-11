package agh.edu.pl.commands

import agh.edu.pl.context.Context

import scala.concurrent.Future

abstract class CreateEntity[E]() {
  def toEntity: E
  def newEntity(ctx: Context): Future[E] = Future(toEntity)(ctx.ec)
}
