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

package org.mmadt.language.mmlang

import org.mmadt.VmException
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.branch.BranchOp.BranchInst
import org.mmadt.language.obj.op.branch.ChooseOp.ChooseInst
import org.mmadt.language.obj.op.branch.{BranchOp,ChooseOp}
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.op.map.GetOp.GetInst
import org.mmadt.language.obj.op.model.AsOp
import org.mmadt.language.obj.op.traverser.FromOp.FromInst
import org.mmadt.language.obj.op.traverser.ToOp.ToInst
import org.mmadt.language.obj.op.traverser.{FromOp,ToOp}
import org.mmadt.language.obj.value.strm._
import org.mmadt.language.obj.value.{strm => _,_}
import org.mmadt.language.{LanguageException,Tokens}
import org.mmadt.storage.StorageFactory.{strm => estrm,_}

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangParser(val model:Model) extends JavaTokenParsers {

  override val whiteSpace:Regex = """[\s\n]+""".r
  override def decimalNumber:Parser[String] = """-?\d+\.\d+""".r

  // all mm-ADT languages must be able to accept a string representation of an expression in the language and return an Obj
  private def parse[O <: Obj](input:String):O ={
    this.parseAll(expr | emptySpace,input.trim).get.asInstanceOf[O]
  }
  private def emptySpace[O <: Obj]:Parser[O] = (Tokens.empty | whiteSpace) ^^ (_ => estrm[O])

  // specific to mmlang execution
  lazy val expr       :Parser[Obj] = compilation | evaluation | anonType
  lazy val compilation:Parser[Obj] = dType ^^ (x => (x.domain() ==> (x,this.model)))
  lazy val evaluation :Parser[Obj] = (strm | objValue) ~ opt(anonType | dType) ^^ (x =>
    x._1 ==>
    ((Type.resolve(x._1,x._2.getOrElse(asType[Obj](x._1))).domain() ==>
      (x._2.getOrElse(asType[Obj](x._1)),this.model)).asInstanceOf[Type[Obj]],this.model))

  /////////////////////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////////////////////

  // mmlang's language structure
  lazy val obj:Parser[Obj] = objValue | objType

  // type parsing
  lazy val objType  :Parser[Type[Obj]]    = anonType | dType
  lazy val boolType :Parser[BoolType]     = Tokens.bool ^^ (_ => bool)
  lazy val intType  :Parser[IntType]      = Tokens.int ^^ (_ => int)
  lazy val realType :Parser[RealType]     = Tokens.real ^^ (_ => real)
  lazy val strType  :Parser[StrType]      = Tokens.str ^^ (_ => str)
  lazy val recType  :Parser[ORecType]     = (Tokens.rec ~> opt(recStruct)) ^^ (x => trec(value = x.getOrElse(Map.empty)))
  lazy val recStruct:Parser[Map[Obj,Obj]] = (LBRACKET ~> repsep((obj <~ (Tokens.:-> | Tokens.::)) ~ obj,(COMMA | PIPE)) <~ RBRACKET) ^^ (x => x.map(o => (o._1,o._2)).toMap)
  lazy val namedType:Parser[Type[Obj]]    = ("^(?!(" + instOp + "))([a-zA-Z]+)").r <~ not(":") ^^ (x => this.model.get(tobj(x)) match {
    case Some(atype) => atype
    case None => tobj(x)
  })
  lazy val cType    :Parser[Type[Obj]]    = (boolType | realType | intType | strType | recType | namedType) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val dType    :Parser[Type[Obj]]    = opt(cType <~ Tokens.:<=) ~ cType ~ rep[Inst[Obj,Obj]](inst | cType ^^ (t => AsOp(t))) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y.exec(x).asInstanceOf[Type[Obj]]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => y.exec(x).asInstanceOf[Type[Obj]])
  }
  lazy val anonType :Parser[__]           = inst ~ rep[Inst[Obj,Obj]](inst | cType ^^ (t => AsOp(t))) ^^ (x => __(x._1 :: x._2))
  lazy val instOp   :String               = Tokens.reserved.foldRight(EMPTY)((a,b) => b + PIPE + a).drop(1)

  // value parsing
  lazy val objValue :Parser[Value[Obj]] = (boolValue | realValue | intValue | strValue | recValue) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1))
  lazy val boolValue:Parser[BoolValue]  = opt(valueType) ~ (Tokens.btrue | Tokens.bfalse) ^^ (x => vbool(x._1.getOrElse(Tokens.bool),x._2.toBoolean,qOne))
  lazy val intValue :Parser[IntValue]   = opt(valueType) ~ wholeNumber ^^ (x => vint(x._1.getOrElse(Tokens.int),x._2.toLong,qOne))
  lazy val realValue:Parser[RealValue]  = opt(valueType) ~ decimalNumber ^^ (x => vreal(x._1.getOrElse(Tokens.real),x._2.toDouble,qOne))
  lazy val strValue :Parser[StrValue]   = opt(valueType) ~ ("""'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""").r ^^ (x => vstr(x._1.getOrElse(Tokens.str),x._2.subSequence(1,x._2.length - 1).toString,qOne))
  lazy val recValue :Parser[ORecValue]  = opt(valueType) ~ (LBRACKET ~> repsep((objValue <~ (Tokens.:-> | Tokens.::)) ~ objValue,COMMA) <~ RBRACKET) ^^ (x => vrec(x._1.getOrElse(Tokens.rec),x._2.map(o => (o._1,o._2)).toMap,qOne))
  lazy val valueType:Parser[String]     = "[a-zA-Z]+".r <~ ":"
  lazy val strm     :Parser[Strm[Obj]]  = boolStrm | realStrm | intStrm | strStrm | recStrm
  lazy val boolStrm :Parser[BoolStrm]   = (boolValue <~ COMMA) ~ rep1sep(boolValue,COMMA) ^^ (x => bool(x._1,x._2.head,x._2.tail:_*))
  lazy val intStrm  :Parser[IntStrm]    = (intValue <~ COMMA) ~ rep1sep(intValue,COMMA) ^^ (x => int(x._1,x._2.head,x._2.tail:_*))
  lazy val realStrm :Parser[RealStrm]   = (realValue <~ COMMA) ~ rep1sep(realValue,COMMA) ^^ (x => real(x._1,x._2.head,x._2.tail:_*))
  lazy val strStrm  :Parser[StrStrm]    = (strValue <~ COMMA) ~ rep1sep(strValue,COMMA) ^^ (x => str(x._1,x._2.head,x._2.tail:_*))
  lazy val recStrm  :Parser[ORecStrm]   = (recValue <~ COMMA) ~ rep1sep(recValue,COMMA) ^^ (x => vrec(x._1,x._2.head,x._2.tail:_*))

  // instruction parsing
  lazy val inst         :Parser[Inst[Obj,Obj]]       = (sugarlessInst | fromSugar | toSugar | infixSugar | getSugar | chooseSugar | branchSugar) ~ opt(quantifier) ^^ (x => x._2.map(q => x._1.q(q)).getOrElse(x._1).asInstanceOf[Inst[Obj,Obj]])
  lazy val infixSugar   :Parser[Inst[Obj,Obj]]       = (Tokens.plus_op | Tokens.mult_op | Tokens.gte_op | Tokens.lte_op | Tokens.gt_op | Tokens.lt_op | Tokens.eqs_op | Tokens.a_op | Tokens.is) ~ obj ^^ (x => OpInstResolver.resolve(x._1,List(x._2)))
  lazy val chooseSugar  :Parser[ChooseInst[Obj,Obj]] = (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,PIPE)) <~ RBRACKET ^^ (x => ChooseOp(trec(value = x.map(o => (o._1,o._2)).toMap)))
  lazy val branchSugar  :Parser[BranchInst[Obj,Obj]] = (LBRACKET ~> repsep((obj <~ Tokens.:->) ~ obj,AMPERSAND)) <~ RBRACKET ^^ (x => BranchOp(trec(value = x.map(o => (o._1,o._2)).toMap)))
  lazy val getSugar     :Parser[GetInst[Obj,Obj]]    = Tokens.get_op ~> "[a-zA-Z]+".r ^^ (x => GetOp[Obj,Obj](str(x)))
  lazy val toSugar      :Parser[ToInst[Obj]]         = LANGLE ~> "[a-zA-z]+".r <~ RANGLE ^^ (x => ToOp(x))
  lazy val fromSugar    :Parser[FromInst[Obj]]       = LANGLE ~> PERIOD ~ "[a-zA-z]+".r <~ RANGLE ^^ (x => FromOp(x._2))
  lazy val sugarlessInst:Parser[Inst[Obj,Obj]]       = LBRACKET ~> ("""=?[a-z]+""".r <~ opt(COMMA)) ~ repsep(obj,COMMA) <~ RBRACKET ^^ (x => OpInstResolver.resolve(x._1,x._2))

  // quantifier parsing
  lazy val quantifier    :Parser[IntQ] = (LCURL ~> quantifierType <~ RCURL) | (LCURL ~> intValue ~ opt(COMMA ~> intValue) <~ RCURL) ^^ (x => (x._1,x._2.getOrElse(x._1)))
  lazy val quantifierType:Parser[IntQ] = (Tokens.q_star | Tokens.q_mark | Tokens.q_plus) ^^ {
    case Tokens.q_star => qStar
    case Tokens.q_mark => qMark
    case Tokens.q_plus => qPlus
  }
}

object mmlangParser {
  def parse[O <: Obj](script:String,model:Model):O = try {new mmlangParser(model).parse[O](script)} catch {
    case e:VmException => throw e
    case e:Exception => {
       e.printStackTrace()
      throw new LanguageException(e.getMessage)
    }
  }
}