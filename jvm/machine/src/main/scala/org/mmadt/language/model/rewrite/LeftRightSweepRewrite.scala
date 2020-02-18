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

package org.mmadt.language.model.rewrite

import org.mmadt.language.model.Model
import org.mmadt.language.obj.{OType, Obj, TType}
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.C1Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object LeftRightSweepRewrite {
  def rewrite[E <: Obj](model:Model,startType:OType,endType:TType[E]):Traverser[E] ={
    var mutatingTraverser:Traverser[E] = new C1Traverser[E](startType.asInstanceOf[E])
    var previousTraverser:Traverser[E] = new C1Traverser[E](endType)
    while (previousTraverser != mutatingTraverser) {
      mutatingTraverser = previousTraverser
      previousTraverser = recursiveRewrite(model,mutatingTraverser.obj().asInstanceOf[OType],startType,new C1Traverser(startType.asInstanceOf[E]))
    }
    mutatingTraverser
  }

  @scala.annotation.tailrec
  private def recursiveRewrite[E <: Obj](model:Model,atype:OType,btype:OType,traverser:Traverser[E]):Traverser[E] ={
    if (atype.insts().nonEmpty) {
      model.get(atype) match {
        case Some(right:OType) => recursiveRewrite(model,right,btype,traverser)
        case None => recursiveRewrite(model,
          atype.rinvert(),
          atype.insts().last._2.apply(atype.range(),atype.insts().last._2.args()).asInstanceOf[OType].compose(btype),
          traverser)
      }
    } else if (btype.insts().nonEmpty) recursiveRewrite(model,btype.linvert(),btype.linvert().domain(),traverser.apply(btype).asInstanceOf[Traverser[E]])
    else traverser
  }
}