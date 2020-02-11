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

package org.mmadt.storage.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.op.StartOp
import org.mmadt.language.obj.value.RecValue
import org.mmadt.language.obj.{Obj, TQ}
import org.mmadt.storage.obj._
import org.mmadt.storage.obj.`type`.TRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRec[A <: Obj, B <: Obj](java: Map[A, B], quantifier: TQ) extends VObj(java, quantifier) with RecValue[A, B] {

  def this(java: Map[A, B]) = this(java, qOne)

  override def value(): Map[A, B] = java //
  override def start(): RecType[A, B] = new TRec(java, List((new TRec(java, Nil, qZero),  StartOp(this))), q()) //
  override def q(quantifier: TQ): this.type = new VRec(java, quantifier).asInstanceOf[this.type] //

}
