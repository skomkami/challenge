package agh.edu.pl.filters

sealed trait Filter

case class FilterEq(field: String, value: String) extends Filter
case class StringQuery(field: String, value: String) extends Filter

//case object FilterEq extends JsonSerializable[FilterEq] {
//  lazy val FilterInputType: InputObjectType[FilterEq] =
//    deriveInputObjectType[FilterEq]()
//}
