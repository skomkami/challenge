package agh.edu.pl.authservice

import java.util.UUID

import agh.edu.pl.entities.User

import scala.concurrent.Future

trait AuthService {
  def createUser(user: User): Future[Either[String, UUID]]
}
