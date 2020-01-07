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

package org.mmadt.machine.object.model.ext.algebra;

import org.mmadt.machine.object.impl.composite.inst.map.GetInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface WithProduct<K extends Obj, V extends Obj> extends Obj {

    public WithProduct<K, V> put(final K key, final V value);

    public WithProduct<K, V> drop(final K key);

    public V get(final K key);

    public default V get(final K key, final V type) {
        return GetInst.compute(this, key, type);
    }

    public default V get(final Object key, final V type) {
        return GetInst.compute(this, ObjectHelper.create(this, key), type);
    }

    ///

    public default WithProduct<K, V> put(final Object key, final Object value) {
        return this.put(ObjectHelper.create(this, key), ObjectHelper.create(this, value));
    }
}
