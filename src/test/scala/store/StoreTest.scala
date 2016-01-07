package atomflow.store

import atomflow.model._
import org.scalatest.{ FunSpecLike, Matchers }
import java.util.Date

trait StoreTest[Id, S <: Store[Id]] extends Matchers {
  self: FunSpecLike =>

  def getStore(): S

  def testEvent(
    atomId: String = "xyzzy",
    eventType: String = "update",
    startDate: Date = new Date(),
    status: EventStatus.Value = EventStatus.OPEN
  ) = Event(atomId, eventType, startDate, status)

  it("should put and retrieve an event") {
    val s = getStore()
    val ev = testEvent()
    val (id, newStore) = s.putEvent(ev)
    val actual = newStore.getEvent(id)
    actual should equal(Some(ev))
  }

  it("should give None for non-existant events") {
    val s = getStore()
    val ev = testEvent()
    val (id, newStore) = s.putEvent(ev)
    newStore.getEvent(id) should not be (None)
    s.getEvent(id) should be (None)
  }

}
