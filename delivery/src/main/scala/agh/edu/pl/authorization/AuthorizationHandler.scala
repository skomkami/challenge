package agh.edu.pl.authorization

import java.math.BigInteger
import java.security.spec.RSAPublicKeySpec
import java.security.{ KeyFactory, PublicKey }
import java.util.Base64

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ AuthorizationFailedRejection, Directive1 }
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.semiauto._
import org.keycloak.TokenVerifier
import org.keycloak.adapters.{ KeycloakDeployment, KeycloakDeploymentBuilder }
import org.keycloak.jose.jws.AlgorithmType
import org.keycloak.representations.AccessToken
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Success

trait AuthorizationHandler {
  val keycloakDeployment: KeycloakDeployment =
    KeycloakDeploymentBuilder.build(
      getClass.getResourceAsStream("/keycloak.json")
    )

  implicit def executionContext: ExecutionContext
  implicit def materializer: Materializer
  implicit def system: ActorSystem

  def authorize: Directive1[Option[AccessToken]] =
    extractCredentials.flatMap {
      case Some(OAuth2BearerToken(token)) =>
        onComplete(verifyToken(token)).flatMap {
          case Success(Some(t)) =>
            provide(Some(t))
          case _ =>
            scribe.warn(s"token $token is not valid")
            reject(AuthorizationFailedRejection)
        }
      case _ =>
        scribe.warn("no token present in request")
        provide(None)
    }

  def verifyToken(token: String): Future[Option[AccessToken]] = {
    val tokenVerifier = TokenVerifier
      .create(token, classOf[AccessToken])

    for {
      publicKey <- publicKeys.map(_.get(tokenVerifier.getHeader.getKeyId))
    } yield publicKey match {
      case Some(publicKey) =>
        val token = tokenVerifier.publicKey(publicKey).verify().getToken
        Some(token)
      case None =>
        scribe.warn(
          s"no public key found for id ${tokenVerifier.getHeader.getKeyId}"
        )
        None
    }
  }

  case class Keys(keys: Seq[KeyData])
  case class KeyData(
      kid: String,
      n: String,
      e: String
    )

  implicit val keyDataFormat = deriveCodec[KeyData]
  implicit val keysFormat = deriveCodec[Keys]

  lazy val publicKeys: Future[Map[String, PublicKey]] =
    Http()
      .singleRequest(HttpRequest(uri = keycloakDeployment.getJwksUrl))
      .flatMap { response =>
        Unmarshal(response)
          .to[Keys]
          .map(_.keys.map(k => (k.kid, generateKey(k))).toMap)
      }

  private def generateKey(keyData: KeyData): PublicKey = {
    val keyFactory = KeyFactory.getInstance(AlgorithmType.RSA.toString)
    val urlDecoder = Base64.getUrlDecoder
    val modulus = new BigInteger(1, urlDecoder.decode(keyData.n))
    val publicExponent = new BigInteger(1, urlDecoder.decode(keyData.e))
    keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent))
  }
}
