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

package org.mmadt.machine.console;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@BuildParseTree
public class SimpleParser extends BaseParser<Object> {

    final Map<Integer, Rule> ARROWS = new HashMap<>() {{
        put(0, Terminal(">"));
        put(1, Terminal("->"));
        put(2, Terminal("-->"));
        put(3, Terminal("--->"));
    }};

    final Rule COLON = Terminal(Tokens.COLON);
    final Rule COMMA = Terminal(Tokens.COMMA);
    final Rule PERIOD = Terminal(Tokens.PERIOD);
    final Rule AND = Terminal(Tokens.AMPERSAND);
    final Rule OR = Terminal(Tokens.BAR);
    final Rule STAR = Terminal(Tokens.ASTERIX);
    final Rule PLUS = Terminal(Tokens.CROSS);
    final Rule QMARK = Terminal(Tokens.QUESTION);
    final Rule SUB = Terminal(Tokens.DASH);
    final Rule DIV = Terminal(Tokens.BACKSLASH);
    final Rule MAPSFROM = Terminal(Tokens.MAPSFROM);
    final Rule MAPSTO = Terminal(Tokens.MAPSTO);
    final Rule LBRACKET = Terminal(Tokens.LBRACKET);
    final Rule RBRACKET = Terminal(Tokens.RBRACKET);
    final Rule LCURL = Terminal(Tokens.LCURL);
    final Rule RCURL = Terminal(Tokens.RCURL);
    final Rule TRIPLE_QUOTE = Terminal(Tokens.DQUOTE + Tokens.DQUOTE + Tokens.DQUOTE);
    final Rule DOUBLE_QUOTE = Terminal(Tokens.DQUOTE);
    final Rule SINGLE_QUOTE = Terminal(Tokens.SQUOTE);
    final Rule TILDE = Terminal(Tokens.TILDE);
    final Rule LPAREN = Terminal(Tokens.LPAREN);
    final Rule RPAREN = Terminal(Tokens.RPAREN);
    final Rule EQUALS = Terminal(Tokens.EQUALS);
    final Rule GT = Terminal(Tokens.GT);
    final Rule LT = Terminal(Tokens.LT);
    final Rule NEQ = Terminal(Tokens.NEQ);
    final Rule SEMICOLON = Terminal(Tokens.SEMICOLON);
    final Rule TRUE = Terminal(Tokens.TRUE);
    final Rule FALSE = Terminal(Tokens.FALSE);

    /// built-int type symbols
    final Rule INT = Terminal(Tokens.INT);
    final Rule REAL = Terminal(Tokens.REAL);
    final Rule STR = Terminal(Tokens.STR);
    final Rule BOOL = Terminal(Tokens.BOOL);
    final Rule REC = Terminal(Tokens.REC);
    final Rule LST = Terminal(Tokens.LIST);
    final Rule INST = Terminal(Tokens.INST);

    final Rule OP_ID = Terminal(Tokens.ID);
    final Rule OP_DEFINE = Terminal(Tokens.DEFINE);
    final Rule OP_MODEL = Terminal(Tokens.MODEL);

    public Rule Source() {
        final Var<Obj> left = new Var<>();
        final Var<Obj> right = new Var<>();
        final Var<String> unary = new Var<>();
        final Var<String> operator = new Var<>();
        return Sequence(
                Optional(SUB, unary.set(Tokens.DASH)), Obj(left), ACTION(unary.isNotSet() || left.set(((WithMinus) left.getAndClear()).neg())), this.push(left.get()),
                ZeroOrMore(left.set((Obj) this.pop()),
                        BinaryOperator(operator),
                        Optional(SUB, unary.set(Tokens.DASH)),
                        Obj(right), ACTION(unary.isNotSet() || left.set(((WithMinus) left.getAndClear()).neg())), this.push(OperatorHelper.operation(operator.get(), left.get(), right.get()))));
    }

    ///////////////

    Rule Obj(final Var<Obj> obj) {
        final Var<Obj> access = new Var<>();
        final Var<Q> quantifier = new Var<>();
        return Sequence(
                FirstOf(Bool(obj),
                        Int(obj),
                        Real(obj),
                        Str(obj),
                        Lst(obj),
                        Rec(obj),
                        Inst(obj)),                                                                     // obj
                Optional(Quantifier(quantifier), obj.set(obj.get().q(quantifier.get()))),               // {quantifier}
                Optional(MAPSFROM, Inst(access), obj.set(obj.get().access((Inst) access.get()))));      // <= inst
    }

    Rule Lst(final Var<Obj> object) {
        final Var<PList<Obj>> list = new Var<>(new PList<>());
        return FirstOf(
                Sequence(LST, object.set(TLst.some())),
                Sequence(LBRACKET,
                        FirstOf(SEMICOLON, // empty list
                                Sequence(Entry(), ACTION(list.get().add((Obj) pop())),
                                        ZeroOrMore(SEMICOLON, Entry(), ACTION(list.get().add((Obj) pop()))))),
                        RBRACKET, object.set(TLst.of(list.get()))));
    }

    Rule Rec(final Var<Obj> object) {
        final Var<PMap<Obj, Obj>> rec = new Var<>(new PMap<>());
        return FirstOf(
                Sequence(REC, object.set(TRec.some())),
                Sequence(LBRACKET,
                        FirstOf(COLON, // empty record
                                Sequence(Field(), ACTION(rec.get().put((Obj) pop(), (Obj) pop()) instanceof Obj),
                                        ZeroOrMore(COMMA, ACTION(rec.get().put((Obj) pop(), (Obj) pop()) instanceof Obj)))),
                        RBRACKET, object.set(TRec.of(rec.get()))));
    }

    @SuppressSubnodes
    Rule Real(final Var<Obj> object) {
        return FirstOf(
                Sequence(REAL, object.set(TReal.of())),
                Sequence(OneOrMore(Digit()), PERIOD, OneOrMore(Digit()), object.set(TReal.of(Float.valueOf(match())))));
    }

    @SuppressSubnodes
    Rule Int(final Var<Obj> object) {
        return FirstOf(
                Sequence(INT, object.set(TInt.some())),
                Sequence(OneOrMore(Digit()), object.set(TInt.of(Integer.valueOf(match())))));
    }

    @SuppressSubnodes
    Rule Str(final Var<Obj> object) {
        return FirstOf(
                Sequence(STR, object.set(TStr.some())),
                Sequence(TRIPLE_QUOTE, ZeroOrMore(Sequence(TestNot(TRIPLE_QUOTE), ANY)), object.set(TStr.of(match())), TRIPLE_QUOTE),
                Sequence(SINGLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\\'")), ANY)), object.set(TStr.of(match())), SINGLE_QUOTE),
                Sequence(DOUBLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\"")), ANY)), object.set(TStr.of(match())), DOUBLE_QUOTE));
    }

    @SuppressSubnodes
    Rule Bool(final Var<Obj> object) {
        return FirstOf(
                Sequence(BOOL, object.set(TBool.some())),
                Sequence(TRUE, object.set(TBool.of(Boolean.valueOf(match())))),
                Sequence(FALSE, object.set(TBool.of(Boolean.valueOf(match())))));
    }

    Rule Inst(final Var<Obj> object) {
        return FirstOf(
                Sequence(INST, object.set(TInst.some())),
                Sequence(object.set(TInst.ids()), OneOrMore(Single_Inst(), object.set(((Inst) object.getAndClear()).mult((Inst) this.pop())))));
    }

    @SuppressSubnodes
    Rule Single_Inst() {
        final Var<String> opcode = new Var<>();
        final Var<Obj> value = new Var<>();
        final Var<PList<Obj>> args = new Var<>(new PList<>());
        return Sequence(
                LBRACKET,
                VarSym(), opcode.set(match()),                                         // opcode
                ZeroOrMore(COMMA, Obj(value), args.get().add(value.getAndClear())),    // arguments
                RBRACKET, this.push(TInst.of(opcode.get(), args.get())));
    }

    @SuppressSubnodes
    Rule Field() {
        final Var<Obj> key = new Var<>();
        final Var<Obj> value = new Var<>();
        return Sequence(Obj(key), COLON, Obj(value), this.push(key.get()), this.push(value.get()), swap());
    }

    @SuppressSubnodes
    Rule Entry() {
        final Var<Obj> value = new Var<>();
        return Sequence(Obj(value), this.push(value.get()));
    }

    @SuppressSubnodes
    Rule VarSym() {
        return Sequence(Char(), ZeroOrMore(FirstOf(Char(), Digit())));
    }

    @SuppressSubnodes
    Rule BinaryOperator(final Var<String> operator) {
        return Sequence(FirstOf(STAR, PLUS, DIV, SUB, AND, OR), operator.set(this.match().trim()));
    }

    @SuppressNode
    Rule Terminal(final String string) {
        return Sequence(Spacing(), string, Spacing());
    }

    @SuppressNode
    Rule Spacing() {
        return ZeroOrMore(FirstOf(
                OneOrMore(AnyOf(" \t\r\n\f")),                                                               // whitespace
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),                                        // block comment
                Sequence("//", ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), FirstOf("\r\n", '\r', '\n', EOI)))); // line comment
    }

    @SuppressNode
    Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressNode
    Rule Char() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }

    @SuppressSubnodes
    Rule Quantifier(final Var<Q> quantifier) {
        return Sequence(Q(), quantifier.set((Q) this.pop()));
    }

    @SuppressSubnodes
    Rule Q() {
        final Var<WithOrderedRing> low = new Var<>();
        final Var<WithOrderedRing> high = new Var<>();
        return Sequence(
                LCURL,  // TODO: the *, +, ? shorthands assume Int ring. (this will need to change)
                FirstOf(Sequence(STAR, this.push(new TQ<>(0, Integer.MAX_VALUE))),                                    // {*}
                        Sequence(PLUS, this.push(new TQ<>(1, Integer.MAX_VALUE))),                                    // {+}
                        Sequence(QMARK, this.push(new TQ<>(0, 1))),                                                   // {?}
                        Sequence(COMMA, Obj((Var) high), this.push(new TQ<>(high.get().min(), high.get()))),          // {,10}
                        Sequence(Obj((Var) low),
                                FirstOf(Sequence(COMMA, Obj((Var) high), this.push(new TQ<>(low.get(), high.get()))), // {1,10}
                                        Sequence(COMMA, this.push(new TQ<>(low.get(), low.get().max()))),
                                        this.push(new TQ<>(low.get(), low.get()))))),                                 // {1}
                RCURL);
    }
}
