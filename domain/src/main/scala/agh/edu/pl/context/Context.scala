package agh.edu.pl.context

import agh.edu.pl.auth.AuthService
import agh.edu.pl.ids.UserId
import agh.edu.pl.repository.Repository

import scala.concurrent.ExecutionContext

case class Context(
    repository: Repository,
    ec: ExecutionContext,
    authService: AuthService,
    userId: Option[UserId]
  ) {}
