package agh.edu.pl

import agh.edu.pl.error.DomainError

package object commands {
  case class NotFoundEntity(entityType: String, id: String)
      extends DomainError(s"""Not found entity $entityType with id: "$id" """)
}
