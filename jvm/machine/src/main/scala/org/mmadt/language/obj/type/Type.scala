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

package org.mmadt.language.obj.`type`

import org.mmadt.language.obj.op.{AsOp, FromOp, MapOp, ModelOp}
import org.mmadt.language.obj.value.{RecValue, StrValue, Value}
import org.mmadt.language.obj.{Bool, Inst, Obj, Rec, Str, TQ}
import org.mmadt.language.{Stringer, Tokens, obj}
import org.mmadt.processor.obj.`type`.CompilingProcessor

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[T <: Type[T]] extends Obj
  with ModelOp {

  def canonical(): this.type = this.range().q(1) //
  def range(): this.type = this.insts() match {
    case List() => this
    case _ => this.linvert().range()
  }

  def domain[TT <: Type[TT]](): TT = (this.insts() match {
    case Nil => this
    case i: List[(Type[_], Inst)] => i.head._1
  }).asInstanceOf[TT]

  def insts(): List[(Type[_], Inst)] //
  def linvert(): this.type //
  def rinvert[TT <: Type[TT]](): TT =
    (this.insts().dropRight(1).lastOption match {
      case Some(prev) => prev._2.apply(prev._1, prev._2.args())
      case None => this.insts().head._1
    }).asInstanceOf[TT]

  def compose[TT <: Type[TT]](btype: TT): TT = {
    var b: TT = btype
    var a: this.type = this
    for (i <- b.insts()) {
      a = a.compose(i._1, i._2)
    }
    a.asInstanceOf[TT]
  }

  def compose(inst: Inst): T //
  def compose[TT <: Type[TT]](t2: Obj, inst: Inst): TT = (t2 match {
    case _: Bool => bool(inst)
    case _: obj.Int => int(inst)
    case _: Str => str(inst)
    case _: Rec[_, _] => rec[Obj, Obj](Map.empty[Obj, Obj], inst)
  }).asInstanceOf[TT]

  def int(inst: Inst, q: TQ = this.q()): IntType //
  def bool(inst: Inst, q: TQ = this.q()): BoolType //
  def str(inst: Inst, q: TQ = this.q()): StrType //
  def rec[K <: Obj, V <: Obj](tvalue: Map[K, V], inst: Inst, q: TQ = this.q()): RecType[K, V] //

  final def <=[TT <: Type[TT]](mapFrom: TT with Type[TT]): TT = mapFrom.q(this.q()) //
  def ==>[TT <: Type[TT]](t: TT with Type[TT]): TT = new CompilingProcessor().apply(this, t).next().obj()

  override def map[O <: Obj](other: O): O = this.compose(other, MapOp(other)) //
  override def model(model: StrValue): this.type = this.compose(ModelOp(model)).asInstanceOf[this.type] //
  override def from[O <: Obj](label: StrValue): O = this.compose(FromOp(label)).asInstanceOf[O] //
  override def as[O <: Obj](name: String): O = this.compose(AsOp(name)).asInstanceOf[O] //
  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case argValue: Value[_] => TypeChecker.matchesTV(this, argValue)
    case argType: Type[_] => TypeChecker.matchesTT(this, argType)
  }

  override def equals(other: Any): Boolean = other match {
    case t: Type[T] => t.insts().map(_._2) == this.insts().map(_._2) && this.range().toString == t.range().toString
    case _ => false
  }

  // standard Java implementations
  override def hashCode(): scala.Int = this.range().toString.hashCode // TODO: using toString()
  override def toString: String = Stringer.typeString(this) //

  def |[O <: Type[O]](other: O): O = { // TODO: ghetto union type construction
    if (this.insts().nonEmpty && this.insts().head._2.op().equals(Tokens.choose))
      this.linvert().choose(Map(other -> other) ++ this.insts().head._2.arg[RecValue[O, O]]().value())
    else
      this.choose(other -> other, this.asInstanceOf[O] -> this.asInstanceOf[O])
  }
}