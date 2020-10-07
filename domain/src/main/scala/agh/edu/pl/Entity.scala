package agh.edu.pl

abstract class Entity[+EntityId](val id: EntityId)
    extends Product
       with Serializable {
  protected type SelfType <: Entity[EntityId]
  protected type Id <: EntityId
}
