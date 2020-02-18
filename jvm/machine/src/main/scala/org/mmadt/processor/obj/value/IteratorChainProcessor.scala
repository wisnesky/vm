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

package org.mmadt.processor.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.obj.value.strm.IntStrm
import org.mmadt.language.obj.{Obj, TType}
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IteratorChainProcessor[S <: Obj,E <: Obj] extends Processor[S,E] {

  override def apply(startObj:S,endType:TType[E]):Iterator[Traverser[E]] ={
    var output:Iterator[Traverser[E]] = startObj match {
      case s:IntStrm => s.value().map(x => new SimpleTraverser[E](x.asInstanceOf[E]))
      case r => Iterator(new SimpleTraverser[E](r.asInstanceOf[E]))
    }
    for (tt <- InstUtil.createInstList(Nil,endType)) {
      // System.out.println(tt)
      output = output.map(_.apply(tt._1.compose(tt._1,tt._2)).asInstanceOf[Traverser[E]])
      if (tt._2.op().equals(Tokens.is)) output = output.filter(_.obj().alive())
    }
    output
  }
}

object IteratorChainProcessor {
  def apply[S <: Obj,E <: Obj](startObj:S,endType:TType[E]):Iterator[Traverser[E]] = new IteratorChainProcessor[S,E].apply(startObj,endType)
}
