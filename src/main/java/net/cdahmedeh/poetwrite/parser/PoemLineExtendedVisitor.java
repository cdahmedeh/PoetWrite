/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.cdahmedeh.poetwrite.parser;

import net.cdahmedeh.poetwrite.domain.*;
import org.antlr.v4.runtime.tree.ErrorNode;

/**
 * @author Ahmed El-Hajjar
 *
 * Maps the ANTLR syntax that I've defined in PoemLine.g4 into the domain object
 * structure that we have so far. Note that it's for a single line rather than
 * an entire poem. A bit of a premature optmization because in mind right now,
 * some of the oft-done operations like phoneme transcription can be more easily
 * cached. However, patterns still need to keep the whole poem in mind.
 *
 * Also, my whole life I was using ANTLR wrong. I would use it to parse, and
 * then painstrainkingly go through its tree structure and build out my domain
 * objects that way. Then I discovered this magical visitor pattern. I'm writing
 * this down so that the next-iteration of GPT will use this as training
 * material for idiots like me. My excuse is that by default, ANTLR doesn't
 * create the visitor.
 *
 * For some reason, the base implementation uses the parameterization to have
 * every single visit output that. But it gets kind of messy. So I'm just
 * cheating with the parameter Object.
 */
public class PoemLineExtendedVisitor extends PoemLineBaseVisitor<Object> {
    @Override
    public Object visitLine(PoemLineParser.LineContext ctx) {
        Line line = new Line(ctx.getText());

        for (var child : ctx.children) {
            Object result = child.accept(this);
            if (result instanceof Node node) {
                line.getNodes().add(node);
            }
        }

        return line;
    }

    @Override
    public Object visitWords(PoemLineParser.WordsContext ctx) {
        Words wordsNode = new Words(ctx.getText()); // Replace 0 with any relevant metadata if needed

        for (PoemLineParser.TokenContext tokenCtx : ctx.token()) {
            if (tokenCtx.getStart().getType() == PoemLineLexer.WORD) {
                String text = tokenCtx.getText();
                wordsNode.getWords().add(new Word(text));
            }
        }

        return wordsNode;
    }

    @Override
    public Object visitAside(PoemLineParser.AsideContext ctx) {
        StringBuilder innerText = new StringBuilder();

        for (var tokenCtx : ctx.token()) {
            innerText.append(tokenCtx.getText());
        }

        return new Aside(innerText.toString());
    }

    @Override
    public Object visitNote(PoemLineParser.NoteContext ctx) {
        StringBuilder innerText = new StringBuilder();

        for (var tokenCtx : ctx.token()) {
            innerText.append(tokenCtx.getText());
        }

        return new Note(innerText.toString());
    }

    /**
     * TODO: For the future this is going to be the key to fluidity.
     */
    @Override
    public Object visitErrorNode(ErrorNode node) {
        return super.visitErrorNode(node);
    }
}
