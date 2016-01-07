package atomflow.store

import atomflow.model._
import org.scalatest.{ FunSpecLike, Matchers }
import java.util.Date
import org.scalatest.FunSpec

trait StoreTest[Id] extends Matchers {
  self: FunSpecLike =>

  def getEmptyStore(): Store[Id]

  def testEvent(
    atomId: String = "xyzzy",
    eventType: String = "update",
    startDate: Date = new Date(),
    status: EventStatus.Value = EventStatus.OPEN
  ) = Event(atomId, eventType, startDate, status)

  it("should put and retrieve an event") {
    val s = getEmptyStore()
    val ev = testEvent()
    val id = s.putEvent(ev)
    val actual = s.getEvent(id)
    actual should equal(Some(ev))
  }

  it("should give None for non-existant events") {
    val s1 = getEmptyStore()
    val s2 = getEmptyStore()
    val ev = testEvent()
    val id = s1.putEvent(ev)
    s1.getEvent(id) should not be (None)
    s2.getEvent(id) should be (None)
  }

}

class MemStoreTest extends FunSpec
    with StoreTest[Int] {

  def getEmptyStore = new MemStore()

}
