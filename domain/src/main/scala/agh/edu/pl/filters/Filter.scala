package agh.edu.pl.filters

sealed trait Filter

case class FilterEq(field: String, value: String) extends Filter
case class FilterConds(
    eq: Option[String] = None,
    neq: Option[String] = None,
    lt: Option[String] = None,
    gt: Option[String] = None
  )

case class FieldFilter(field: String, value: FilterConds) extends Filter

case class StringQuery(field: String, value: String) extends Filter
