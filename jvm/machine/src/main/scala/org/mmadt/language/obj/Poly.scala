package org.mmadt.language.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.MergeOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm

trait Poly[A <: Obj] extends Obj
  with MergeOp[A] {
  def gsep: String
  def glist: Seq[A]
  def isSerial: Boolean = this.gsep == Tokens.`;`
  def isParallel: Boolean = this.gsep == Tokens.`,`
  def isChoice: Boolean = this.gsep == Tokens.|
  def isPlus: Boolean = this.isParallel | this.isChoice
  def isEmpty: Boolean = this.glist.isEmpty

  def isValue: Boolean = this.isInstanceOf[Strm[_]] || (!this.glist.exists(x => x.alive && ((x.isInstanceOf[Type[_]] && !x.isInstanceOf[Poly[_]]) || (x.isInstanceOf[Poly[_]] && !x.asInstanceOf[Poly[_]].isValue))))
  def isType: Boolean = !this.glist.exists(x => x.alive && ((x.isInstanceOf[Value[_]] && !x.isInstanceOf[Poly[_]]) || (x.isInstanceOf[Poly[_]] && !x.asInstanceOf[Poly[_]].isType)))
}
object Poly {
  def resolveSlots[A <: Obj](start: A, apoly: Poly[A], inst: Inst[A, Poly[A]]): Poly[A] = {
    apoly match {
      case arec: Rec[Obj, A] => Rec.resolveSlots(start, arec, inst.asInstanceOf[Inst[Obj, Rec[Obj, A]]])
      case alst: Lst[A] => Lst.resolveSlots(start, alst, inst.asInstanceOf[Inst[A, Lst[A]]])
    }
  }
  def keepFirst[A <: Obj](start: Obj, inst: Inst[Obj, Obj], apoly: Poly[A]): Poly[A] = {
    apoly match {
      case arec: Rec[Obj, A] => Rec.keepFirst(start,inst,arec)
      case alst: Lst[A] => Lst.keepFirst(alst)
    }
  }
  def sameSep(apoly: Poly[_], bpoly: Poly[_]): Boolean = (apoly.glist.isEmpty || bpoly.glist.isEmpty) ||
    (apoly.isChoice == bpoly.isChoice && apoly.isParallel == bpoly.isParallel && apoly.isSerial == bpoly.isSerial)
}
