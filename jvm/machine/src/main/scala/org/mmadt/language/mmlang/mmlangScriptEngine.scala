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

import java.io.Reader

import javax.script._
import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngine(factory: mmlangScriptEngineFactory) extends AbstractScriptEngine with mmADTScriptEngine {
  override def eval(script: String): Obj = super.eval(script)
  override def eval(reader: Reader): Obj = super.eval(reader)
  override def eval(script: String, context: ScriptContext): Obj = super.eval(script, context)
  override def eval(script: String, bindings: Bindings): Obj = mmlangParser.parse[Obj](script, getModel(bindings))
  override def getFactory: ScriptEngineFactory = factory
  private def getModel(bindings: Bindings): Model = if (bindings.containsKey(Tokens.model)) bindings.get(Tokens.model).asInstanceOf[Model] else Model.id
}

