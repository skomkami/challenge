package agh.edu.pl.measures

abstract class Measure[T: Ordering] {
  def name: String
  def value: T
  def winsOver(other: Measure[T]): Boolean
}

case object Measure {
  def winsOver[T](fst: Measure[T], snd: Measure[T]): Boolean = fst.winsOver(snd)
}
