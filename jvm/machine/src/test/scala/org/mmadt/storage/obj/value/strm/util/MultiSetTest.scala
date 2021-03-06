package org.mmadt.storage.obj.value.strm.util

import org.mmadt.language.obj.IntQ
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class MultiSetTest extends FunSuite {

  def qmaker(a: Int, b: Int): IntQ = (a, b)
  def qmaker(a: Int): IntQ = (a, a)

  test("multiset put") {
    assertResult(1L)(MultiSet.put(int(2)).objSize)
    assertResult(qOne)(MultiSet.put(int(2)).qSize)
    //
    assertResult(1L)(MultiSet.put(int(2)).put(int(2)).objSize)
    assertResult(qmaker(2))(MultiSet.put(int(2)).put(int(2)).qSize)
    //
    assertResult(1L)(MultiSet.put(int(2)).put(int(2)).put(int(2)).objSize)
    assertResult(qmaker(3))(MultiSet.put(int(2)).put(int(2)).put(int(2)).qSize)
    //
    assertResult(1L)(MultiSet.put(int(2)).put(int(2)).put(int(2).q(1, 2)).objSize)
    assertResult(qmaker(3, 4))(MultiSet.put(int(2)).put(int(2)).put(int(2).q(1, 2)).qSize)
    //
    assertResult(2L)(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).objSize)
    assertResult(qmaker(12, 13))(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).qSize)
    //
    assertResult(2L)(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).put(btrue.q(20)).objSize)
    assertResult(qmaker(32, 33))(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).put(btrue.q(20)).qSize)
  }

  test("multiset seq") {
    assertResult(2L)(MultiSet.put(int(2), int(3)).objSize)
    assertResult(qmaker(2))(MultiSet.put(int(2), int(3)).qSize)
    //
    assertResult(2L)(MultiSet.put(int(2), int(3), int(3).q(10)).objSize)
    assertResult(qmaker(12))(MultiSet.put(int(2), int(3), int(3).q(10)).qSize)
  }

  test("multiset w/ inst") {
    println(int(1, 1, 1).q(20))
    assertResult(int(int(12).q(40), int(13).q(40), int(14).q(40)))(int(1, 2, 3).q(20).plus(10).q(2).plus(1))
    assertResult(int(int(12).q(80), int(13).q(40)))(int(1, 1, 2).q(20).plus(10).q(2).plus(1))
  }
}
