package agh.edu.pl.security

import java.util.UUID

import agh.edu.pl.auth.AuthService
import agh.edu.pl.entities.{ User => DomainUser }
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.fullfacing.keycloak4s.admin.client.{ Keycloak, KeycloakClient }
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import com.fullfacing.keycloak4s.core.models.User._

import scala.concurrent.Future

case class KeycloakService(
  )(implicit
    keycloakClient: KeycloakClient[Task, Source[ByteString, Any]]
  ) extends AuthService {
  override def pushUser(user: DomainUser): Future[Either[String, UUID]] = {

    val users = Keycloak.Users[Task, Source[ByteString, Any]]
    val userToCreate: Create = Create(
      username = user.email.address,
      enabled = true,
      email = Some(user.email.address),
      firstName = Some(user.name)
    )

    users.create(userToCreate).map(_.left.map(_.getMessage)).runToFuture
  }
}
