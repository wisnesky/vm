/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{LstType, Type, __}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{LstValue, RecValue, Value}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PlusOp[O <: Obj] {
  this: O =>
  def plus(anon: __): this.type = PlusOp(anon).exec(this)
  def plus(arg: O): this.type = PlusOp(arg).exec(this)
  final def +(anon: __): this.type = this.plus(anon)
  final def +(arg: O): this.type = this.plus(arg)
}

object PlusOp {
  def apply[O <: Obj](obj: Obj): PlusInst[O] = new PlusInst[O](obj)

  class PlusInst[O <: Obj](arg: Obj, q: IntQ = qOne) extends VInst[O, O]((Tokens.plus, List(arg)), q) {
    override def q(q: IntQ): this.type = new PlusInst[O](arg, q).asInstanceOf[this.type]
    override def exec(start: O): O = {
      val inst = new PlusInst(Inst.resolveArg(start, arg), q)
      (start match {
        case _: Strm[_] => start
        case _: Value[_] => start match {
          case aint: Int => start.clone(ground = aint.ground + inst.arg0[Int]().ground)
          case areal: Real => start.clone(ground = areal.ground + inst.arg0[Real]().ground)
          case astr: Str => start.clone(ground = astr.ground + inst.arg0[Str]().ground)
          case arec: RecValue[Value[Value[Obj]], Obj] => start.clone(ground = arec.ground ++ inst.arg0[RecValue[Value[Obj], Value[Obj]]]().ground)
          case arec: ORecType => start.clone(ground = arec.ground ++ inst.arg0[ORecType]().ground)
          case alst: LstValue[Value[Obj]] => start.clone(ground = alst.ground ++ inst.arg0[LstValue[Value[Obj]]]().ground)
          case alst: LstType[Obj] => start.clone(ground = alst.ground ++ inst.arg0[LstType[Obj]]().ground)
          //////// EXPERIMENTAL
          case prodA: Poly[O] if prodA.ground._1 == ";" => arg match {
            case prodB: Poly[O] if prodB.ground._1 == ";" => `|`(prodA, prodB)
            case coprodB: Poly[O] if coprodB.ground._1 == "|" => `|`(prodA, coprodB)
          }
          case coprodA: Poly[O] if coprodA.ground._1 == "|" => arg match {
            case prodB: Poly[O] if prodB.ground._1 == ";" => `|`(coprodA, prodB)
            case coprodB: Poly[O] if coprodB.ground._1 == "|" => `|`().clone(coprodA.ground._2 ++ coprodB.ground._2)
          }
        }
        case _: Type[_] => start
      }).via(start, inst).asInstanceOf[O]
    }
  }

}
