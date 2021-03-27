package agh.edu.pl.context

import agh.edu.pl.authservice.AuthService
import agh.edu.pl.calculator.UpdatePositionsProtocol
import agh.edu.pl.ids.UserId
import agh.edu.pl.repository.Repository
import akka.actor.typed.ActorSystem

import scala.concurrent.ExecutionContext

case class Context(
    repository: Repository,
    ec: ExecutionContext,
    authService: AuthService,
    userId: Option[UserId],
    system: ActorSystem[UpdatePositionsProtocol]
  ) {}
