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

package org.mmadt.language

import org.mmadt.language.mmlang.mmlangPrinter
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Printable[O] {
  def format(value:O):String
}

object Printable {
  def format[O](input:O)(implicit p:Printable[O]):String =
    p.format(input)

  implicit val valuePrintable:Printable[Value[Obj]] = new Printable[Value[Obj]] {
    def format(input:Value[Obj]):String = mmlangPrinter.valueString(input)
  }

  implicit val typePrintable:Printable[Type[Obj]] = new Printable[Type[Obj]] {
    def format(input:Type[Obj]):String = mmlangPrinter.typeString(input)
  }

  implicit val instPrintable:Printable[Inst] = new Printable[Inst] {
    def format(input:Inst):String = mmlangPrinter.instString(input)
  }

  implicit val traverserPrintable:Printable[Traverser[Obj]] = new Printable[Traverser[Obj]] {
    def format(input:Traverser[Obj]):String = mmlangPrinter.traverserString(input)
  }

}