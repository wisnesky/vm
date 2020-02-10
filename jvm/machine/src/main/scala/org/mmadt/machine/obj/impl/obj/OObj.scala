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

package org.mmadt.machine.obj.impl.obj

import org.mmadt.language.Tokens
import org.mmadt.machine.obj._
import org.mmadt.machine.obj.impl.obj.value._
import org.mmadt.machine.obj.impl.obj.value.inst._
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value._
import org.mmadt.machine.obj.theory.obj.{Bool, Inst, Obj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class OObj(val quantifier: TQ) extends Obj {

  override def q(): TQ = quantifier //

  override def inst(op: String, args: List[Obj]): Inst = op match {
    case Tokens.and => new VAndInst(args.head.asInstanceOf[Bool])
    case Tokens.choose => new VChooseInst(args.head.asInstanceOf[RecValue[Type[_], Type[_]]])
    case Tokens.or => new VOrInst(args.head.asInstanceOf[Bool])
    case Tokens.plus => new VPlusInst(args.head)
    case Tokens.map => new VMapInst(args.head)
    case Tokens.mult => new VMultInst(args.head)
    case Tokens.model => new VModelInst(args.head)
    case Tokens.is => new VIsInst(args.head.asInstanceOf[Bool])
    case Tokens.gt => new VGtInst(args.head)
    case Tokens.get => new VGetInst(args.head)
    case Tokens.to => new VToInst(args.head.asInstanceOf[StrValue])
    case Tokens.from => new VFromInst(args.head.asInstanceOf[StrValue])
    // INSTRUCTION IMPLEMENTATIONS NEEDED
    case Tokens.start => new VInst((Tokens.start, args)) // TODO: will be first flatmap
  }
}
