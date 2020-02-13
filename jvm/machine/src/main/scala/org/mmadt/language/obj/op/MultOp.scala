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

package org.mmadt.language.obj.op

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MultOp[O <: Obj with MultOp[O, V, T], V <: Value[V], T <: Type[T]] {
  this: O =>

  def mult(other: T): T //
  def mult(other: V): O //
  final def *(other: T): T = this.mult(other) //
  final def *(other: V): O = this.mult(other) //
}

object MultOp {
  def apply[O <: Obj with MultOp[O, V, T], V <: Value[V], T <: Type[T]](other: V): Inst = new VInst((Tokens.mult, List(other)), qOne, ((a: O, b: List[Obj]) => a.mult(other)).asInstanceOf[(Obj, List[Obj]) => Obj]) //
  def apply[O <: Obj with MultOp[O, V, T], V <: Value[V], T <: Type[T]](other: T): Inst = new VInst((Tokens.mult, List(other)), qOne, ((a: O, b: List[Obj]) => b.head match {
    case v: Value[V] with V => a.mult(v)
    case t: Type[T] with T => a.mult(t)
  }).asInstanceOf[(Obj, List[Obj]) => Obj])
}
