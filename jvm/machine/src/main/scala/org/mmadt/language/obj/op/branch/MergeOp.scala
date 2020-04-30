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

package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.{Brch, IntQ, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait MergeOp[A <: Obj] {
  this: Brch[A] =>
  def merge[B <: Obj]: B = MergeOp[A]().exec(this).asInstanceOf[B]
  final def >-[B <: Obj]: B = this.merge[B]
}

object MergeOp {
  def apply[A <: Obj](): MergeInst[A] = new MergeInst[A]()

  class MergeInst[A <: Obj](q: IntQ = qOne) extends VInst[Brch[A], A]((Tokens.merge, Nil), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new MergeInst[A](q).asInstanceOf[this.type]
    override def exec(start: Brch[A]): A = {
      if (start.isValue)
        strm(start.value.filter(x => x.alive())).asInstanceOf[A] // .map(x => x.q(multQ(start, x)))
      else
        BranchInstruction.brchType[A](start).clone(via = (start, this))
    }
  }

}