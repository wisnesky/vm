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

package org.mmadt.language.obj

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.op.map._

trait Lst[A <: Obj] extends Obj
  with HeadOp[A]
  with TailOp
  with AppendOp[A]
  with GetOp[Int, A]
  with PlusOp[Lst[A]]
  with ZeroOp[Lst[A]] {
  val value: List[Obj]
}

object Lst {
  def checkIndex(alst: Lst[_], index: scala.Int): Unit = {
    if (index < 0) throw new LanguageException("lst index must be 0 or greater: " + index)
    if (alst.value.length < (index + 1)) throw new LanguageException("lst index is out of bounds: " + index)
  }
}