package agh.edu.pl.commands

abstract class CreateEntity[E]() {
  def toEntity: E
}
