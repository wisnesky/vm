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

package org.mmadt.machine.obj.impl

import org.mmadt.machine.obj._
import org.mmadt.machine.obj.impl.VBool.boolF

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class VObj[J](jvm: J, quantifier: TQ) extends OObj(jvm, quantifier) with Value[J] {

  override def _jvm(): J = jvm

  override def eq(other: Obj): Bool = other match {
    case _: Type => boolF
    case x: Value[_] => new VBool(this.jvm == x._jvm())
  }

  override def q(): (VInt, VInt) = quantifier

  override def q(min: VInt, max: VInt): this.type = new VObj[J](jvm, (min, max)).asInstanceOf[this.type]

}