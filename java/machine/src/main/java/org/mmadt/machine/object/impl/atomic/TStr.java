/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.object.impl.atomic;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.GteInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.LteInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TStr extends TObj implements Str {

    private static final Str SOME = new TStr(null);
    private static final Str NONE = new TStr(null).q(0);
    private static final Str ALL = new TStr(null).q(0, Integer.MAX_VALUE);
    private static final Str ZERO = new TStr("");
    private static final Str MAX = new TStr("zzzzzzzzzzzz");

    private TStr(final Object value) {
        super(value);
    }

    public static Str all() {
        return ALL;
    }

    public static Str none() {
        return NONE;
    }

    public static Str some() {
        return SOME;
    }

    public static Str of(final Object... objects) {
        return ObjectHelper.make(TStr::new, objects);
    }

    @Override
    public Bool gt(final Str str) {
        return (this.isInstance() && str.isInstance()) ?
                TBool.from(this).set(this.java().compareTo(str.java()) > 0) :
                TBool.from(this).mapTo(GtInst.create(str));
    }

    @Override
    public Bool gte(final Str str) {
        return (this.isInstance() && str.isInstance()) ?
                TBool.from(this).set(this.java().compareTo(str.java()) >= 0) :
                TBool.from(this).mapTo(GteInst.create(str));
    }


    @Override
    public Bool eq(final Obj obj) {
        return this.isInstance() ?
                TBool.from(this).set(obj instanceof Str && this.java().equals(((Str) obj).java())) :
                TBool.from(this).mapTo(EqInst.create(obj));
    }

    @Override
    public Bool lt(final Str str) {
        return (this.isInstance() && str.isInstance()) ?
                TBool.from(this).set(this.java().compareTo(str.java()) < 0).q(this.q()) :
                TBool.from(this).mapTo(LtInst.create(str));
    }

    @Override
    public Bool lte(final Str str) {
        return (this.isInstance() && str.isInstance()) ?
                TBool.from(this).set(this.java().compareTo(str.java()) <= 0) :
                TBool.from(this).mapTo(LteInst.create(str));
    }

    @Override
    public Str plus(final Str str) {
        return (this.isInstance() && str.isInstance()) ?
                this.set(this.java().concat(str.java())) :
                this.mapTo(PlusInst.create(str));
    }

    @Override
    public Str zero() {
        return this.q().constant() ? this.set(Tokens.EMPTY) : this.mapTo(ZeroInst.create());
    }

    @Override
    public Str max() {
        return MAX;
    }

    @Override
    public Str min() {
        return ZERO;
    }

    @Override
    public Bool regex(final Str pattern) {
        return TBool.of(this.java().matches(pattern.java()));
    } // TODO: [regex,...]

    @Override
    public String toString() {
        return StringFactory.string(this);
    }

}
