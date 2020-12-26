package agh.edu.pl.models

import agh.edu.pl.entities.JsonSerializable
import agh.edu.pl.error.DomainError
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string._
import io.circe.refined._
import io.circe.{ Decoder, Encoder }
import sangria.schema.ScalarAlias
import sangria.validation.Violation

class Email(val address: String) {
  override def toString: String = address
}

object Email extends JsonSerializable[Email] {

  case object EmailViolation extends Violation {
    override def errorMessage: String = "Email address did not pass validation"
  }

  implicit val scalarAlias: ScalarAlias[Email, String] =
    ScalarAlias[Email, String](
      sangria.schema.StringType,
      _.address,
      email =>
        refineV[EmailPred](email)
          .left
          .map(_ => EmailViolation)
          .map(apply)
    ).rename("Email")

  implicit val decoder: Decoder[Email] = Decoder[ValidEmail].map(apply)

  implicit val encoder: Encoder[Email] = Encoder[String].contramap(_.address)
  type EmailPred = MatchesRegex[W.`"""([a-zA-z0-9\\.]+)@([a-zA-z0-9\\.]+)"""`.T]

  type ValidEmail = String Refined EmailPred
  def apply(address: ValidEmail): Email =
    new Email(address.value)
  def fromString(address: String): Email =
    refineV[EmailPred](address)
      .map(apply)
      .getOrElse(throw new DomainError(s"Invalid email: $address"))
}
