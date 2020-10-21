package agh.edu.pl.models

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import io.circe.{ Decoder, Encoder }
import io.circe.refined._
import sangria.schema.ScalarAlias

class Email(val address: String)

object Email extends JsonSerializable[Email] {
  implicit val scalarAlias: ScalarAlias[Email, String] =
    ScalarAlias[Email, String](
      sangria.schema.StringType,
      _.address,
      id => Right(new Email(id))
    ).rename("Email")

  implicit val decoder: Decoder[Email] = Decoder[ValidEmail].map(apply)

  implicit val encoder: Encoder[Email] = Encoder[String].contramap(_.address)

  type EmailPred = MatchesRegex[W.`"""([a-zA-z0-9\\.]+)@([a-zA-z0-9\\.]+)"""`.T]
  type ValidEmail = String Refined EmailPred
  def apply(address: ValidEmail): Email =
    new Email(address.value)

}
