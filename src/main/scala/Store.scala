package atomflow.store

import scala.collection.mutable.ArrayBuffer

import atomflow.model._

trait Store[Id] {
  def putEvent(ev: Event): (Id, Store[Id])
  def getEvent(id: Id): Option[Event]
  // def updateEvent(id: Id, ev: Event): Store[Id]
}

class MemStore(data: List[Event]) extends Store[Int] {
  def putEvent(ev: Event) = {
    val id = data.length
    (id, new MemStore(data :+ ev))
  }

  def getEvent(id: Int): Option[Event] = {
    if(data.length <= id) None
    else Some(data(id))
  }
}
