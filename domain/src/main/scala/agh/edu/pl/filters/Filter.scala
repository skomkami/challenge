package agh.edu.pl.filters

sealed trait Filter

case class FilterEq(field: String, value: String) extends Filter
case class StringQuery(field: String, value: String) extends Filter
