package atomflow.store

import scala.collection.mutable.ArrayBuffer

import atomflow.model._

trait Store[Id] {
  def putEvent(ev: Event): Id
  def getEvent(id: Id): Option[Event]

  def tail(max: Int): List[Event]

  // def updateEvent(id: Id, ev: Event): Store[Id]
}

class MemStore extends Store[Int] {
  val data = ArrayBuffer.empty[Event]

  def putEvent(ev: Event) = data.synchronized {
    val id = data.length
    data += ev
    id
  }

  def getEvent(id: Int): Option[Event] =
    data.synchronized {
      if(data.length <= id) None
      else Some(data(id))
    }

  def tail(max: Int) = data.synchronized {
    data.toArray.sorted.toList.reverse.take(max)
  }
}
