/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.ext.algebra.WithMinus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class MinusInst<S extends WithMinus<S>> extends TInst<S, S> implements MapInstruction<S, S> {

    private MinusInst(final Object arg) {
        super(PList.of(Tokens.MINUS, arg));
    }

    public S apply(final S obj) {
        return this.quantifyRange(obj.minus(this.<S>argument(0).mapArg(obj)));
    }

    public static <S extends WithMinus<S>> MinusInst<S> create(final Object arg) {
        return new MinusInst<>(arg);
    }

}
