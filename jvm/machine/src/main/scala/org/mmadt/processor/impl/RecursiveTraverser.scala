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

package org.mmadt.processor.impl

import org.mmadt.language.Tokens
import org.mmadt.language.model.{Model, SimpleModel}
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RecursiveTraverser[S <: Obj](obj: S, state: Map[StrValue, Obj], model: Model) extends Traverser[S] {

  def this(obj: S) = this(obj, Map[StrValue, Obj](), new SimpleModel())

  def this(obj: S, state: Map[StrValue, Obj]) = this(obj, state, new SimpleModel())

  override def obj(): S = obj //
  override def split[E <: Obj](obj: E): Traverser[E] = new RecursiveTraverser(obj, this.state, model) //

  override def apply[E <: Obj](t: E with Type[_]): Traverser[E] = {
    if (t.insts().isEmpty) {
      TypeChecker.checkType(this.obj(), t)
      this.asInstanceOf[Traverser[E]]
    } else {
      this.obj match {
        case tobj: Type[_] if !model.get(tobj.pure(), t).toString.equals(t.toString) => this.apply(model.get(tobj.pure(), t).asInstanceOf[E with Type[_]])
        case _ =>
          (t.insts().head._2 match {
            // traverser instructions
            case toInst: Inst if toInst.op().equals(Tokens.to) => new RecursiveTraverser[S](obj, Map[StrValue, Obj](toInst.arg[StrValue]() -> obj) ++ this.state, model) //
            case fromInst: Inst if fromInst.op().equals(Tokens.from) => new RecursiveTraverser[E](this.state(fromInst.arg[StrValue]()).asInstanceOf[E], this.state, model) //
            case modelInst: Inst if modelInst.op().equals(Tokens.model) => new RecursiveTraverser[E](obj.asInstanceOf[E], this.state, new SimpleModel().put(int, int.mult(2), int.plus(int)))
            // branch instructions
            // storage instructions
            case storeInst: Inst => this.split(storeInst.inst(storeInst.op(), storeInst.args().map {
              case typeArg: Type[_] => this.split(this.obj() match {
                case tt: Type[_] => tt.pure()
                case _ => this.obj()
              }).apply(typeArg).obj()
              case valueArg: Value[_] => valueArg
            }).apply(this.obj))
          }).apply(t.pop().asInstanceOf[E with Type[_]])
      }
    }
  }

  override def state(): Map[StrValue, Obj] = state //
  override def model(): Model = model
}
