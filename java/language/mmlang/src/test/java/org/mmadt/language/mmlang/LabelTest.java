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

package org.mmadt.language.mmlang;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.jsr223.mmLangScriptEngine;
import org.mmadt.language.mmlang.util.ParserArgs;

import javax.script.ScriptEngine;
import java.util.List;
import java.util.stream.Stream;

import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;
import static org.mmadt.machine.object.impl.__.mult;
import static org.mmadt.machine.object.impl.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LabelTest {

    private final static ParserArgs[] LABELS = new ParserArgs[]{
            args(ints(1).label("x"), "1=>x"),
            args(ints(1).label("x"), List.of(ints(1).label("x")), "1=>x"),
            args(ints(1).label("y"), "1=>x=>y"),
            args(ints(1).label("y"), List.of(ints(1).label("x"), ints(1).label("y")), "1=>x=>y"),
            args(ints(3), "1=>x=>y=>[plus,2]"),
            args(ints(3).label("z"), "1=>x=>y=>[plus,2]=>z"),
            args(objs(), "1=>x=>y=>[plus,2]=>x"),
            args(ints(1).label("x"), "1=>x=>y=>x"),
            args(ints(1).label("y"), "1=>x=>y=>x=>y"),
            args(ints(1).label("y"), "1=>x=>y=>[plus,0]=>y"),
            args(ints(1).label("y"), "1=>x=>y=>[plus,10][minus,2][minus,8]=>y"),
            args(objs(), "1=>x=>y=>[plus,10][minus,2][minus,9]=>y"),

            /////////////////////////////////////////////////////

            args(ints().access(plus(2)), List.of(), "int=>int=>[plus,2]"),
            args(ints().access(plus(2)), List.of(ints().label("x")), "int=>int~x=>[plus,2]"),
            args(ints().access(plus(2).mult(mult(3))), List.of(ints().label("x"), ints().label("y").access(plus(2))), "int~x=>[plus,2]=>y=>[mult,3]"),
            args(ints().access(plus(10).mult(mult(20))), List.of(ints().label("y").access(plus(10))), "int=>[plus,10]=>y=>[mult,20]=>int"),
            args(ints().access(plus(10).mult(mult(20))), List.of(ints().label("y").access(plus(10))), "int=>[plus,10]=>int~y=>[mult,20]=>int"),
            args(ints().access(plus(10).mult(mult(20))), List.of(ints().label("y").access(plus(10))), "int=>[plus,10]=>y=>[mult,20]=>int"),
            args(ints().access(plus(10).mult(mult(20))), List.of(ints().label("y").access(plus(10)), ints().label("z").access(plus(10))), "int=>[plus,10]=>y=>z=>[mult,20]=>int"),
    };


    @TestFactory
    Stream<DynamicTest> testLabels() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(LABELS).map(query -> query.execute(engine));
    }
}
