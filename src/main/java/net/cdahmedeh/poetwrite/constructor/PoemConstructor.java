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

import net.cdahmedeh.poetwrite.domain.Poem;
import net.cdahmedeh.poetwrite.parser.PoemLexer;
import net.cdahmedeh.poetwrite.parser.PoemParser;
import net.cdahmedeh.poetwrite.parser.PoemVisitor;
import net.cdahmedeh.poetwrite.parser.PoemExtendedVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class PoemConstructor {
    public static Poem fromText(String text) {
        PoemLexer lexer = new PoemLexer(CharStreams.fromString(text));
        CommonTokenStream stream = new CommonTokenStream(lexer);
        PoemParser parser = new PoemParser(stream);

        PoemParser.PoemContext context = parser.poem();

        PoemVisitor visitor = new PoemExtendedVisitor();

        return (Poem) visitor.visit(context);
    }
}
