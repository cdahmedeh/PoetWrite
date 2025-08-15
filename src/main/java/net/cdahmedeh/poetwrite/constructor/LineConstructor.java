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

package net.cdahmedeh.poetwrite.constructor;

import net.cdahmedeh.poetwrite.parser.PoemLineLexer;
import net.cdahmedeh.poetwrite.parser.PoemLineParser;
import net.cdahmedeh.poetwrite.domain.Line;
import net.cdahmedeh.poetwrite.parser.PoemLineVisitor;
import net.cdahmedeh.poetwrite.parser.PoemLineExtendedVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class LineConstructor {
    public static Line fromText(String text) {
        PoemLineLexer lexer = new PoemLineLexer(CharStreams.fromString(text));
        CommonTokenStream stream = new CommonTokenStream(lexer);
        PoemLineParser parser = new PoemLineParser(stream);

        PoemLineParser.LineContext context = parser.line();

        PoemLineVisitor visitor = new PoemLineExtendedVisitor();

        return (Line) visitor.visit(context);
    }
}
