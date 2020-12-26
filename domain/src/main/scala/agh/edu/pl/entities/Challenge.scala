package agh.edu.pl.entities

import java.time.OffsetDateTime

import agh.edu.pl.error.DomainError
import agh.edu.pl.ids.{ ChallengeId, UserId }
import agh.edu.pl.measures.Measure

final case class Challenge(
    override val id: ChallengeId,
    name: String,
    description: String,
    createdById: UserId,
    createdOn: OffsetDateTime,
    finishesOn: OffsetDateTime,
    measure: Measure
  ) extends Entity[ChallengeId](id) {
  override type IdType = ChallengeId

  def checkAvailabilityAndReturn[R](
      returnValue: R,
      validationDate: Option[OffsetDateTime] = None
    ): R = {
    val date = validationDate.getOrElse(OffsetDateTime.now)
    if (finishesOn.isBefore(date)) {
      throw ChallengeInactive(name)
    }
    else returnValue
  }

  case class ChallengeInactive(challengeName: String)
      extends DomainError(
        s"Challenge $challengeName has finished."
      )
}

case object Challenge extends JsonSerializable[Challenge]
