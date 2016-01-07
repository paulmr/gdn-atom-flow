package atomflow.store

import org.scalatest.FunSpec

class MemStoreTest extends FunSpec
    with StoreTest[Int, Store[Int]] {

  def getStore = new MemStore(Nil)

}
