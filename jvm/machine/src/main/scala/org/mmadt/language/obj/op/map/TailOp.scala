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

import org.mmadt.language.obj.`type`.LstType
import org.mmadt.language.obj.value.LstValue
import org.mmadt.language.obj.{Brch, IntQ, Obj}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait TailOp {
  this: Obj =>
  def tail(): this.type = TailOp().exec(this)
}

object TailOp {
  def apply[O <: Obj](): TailInst[O] = new TailInst[O]

  class TailInst[O <: Obj](q: IntQ = qOne) extends VInst[O, O]((Tokens.tail, Nil), q) {
    override def q(q: IntQ): this.type = new TailInst(q).asInstanceOf[this.type]
    override def exec(start: O): O = (start match {
      case abrch: Brch[_] => if (abrch.value.isEmpty) throw new LanguageException("no tail on empty brch") else abrch.clone(value = abrch.value.tail)
      case alst: LstValue[_] => if (alst.value.isEmpty) throw new LanguageException("no tail on empty lst") else alst.clone(value = alst.value.tail)
      case alst: LstType[_] => if (alst.value.isEmpty) alst else alst.clone(value = alst.value.tail)
    }).asInstanceOf[O].via(start, this)

  }

}