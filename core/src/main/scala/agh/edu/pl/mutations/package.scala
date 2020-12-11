package agh.edu.pl

import agh.edu.pl.error.DomainError

package object mutations {
  case class EntityAlreadyExists(reason: String)
      extends DomainError(s"""Entity already exists. $reason """)

  case class OperationNotSupported(reason: String)
      extends DomainError(s"""Operation not supported. $reason """)

  case object UnsupportedChallengeType
      extends DomainError(
        "Unsupported challenge type for SmallerWins and Summarization"
      )

  case class ChallengeInactive(challengeName: String)
      extends DomainError(
        s"Challenge $challengeName has finished."
      )
}
