/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2026 Ahmed El-Hajjar
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

package net.cdahmedeh.poetwrite.lib.domain;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @author Ahmed El-Hajjar
 *
 * This is the main root of the domain structure of a poem. Everything rests on
 * being able to prune through the poem effectively. So this is just your
 * run-of-the-mill silly OOP pattern.
 *
 * You can find how this is all designed in the following documentation.
 * Poem Syntax and Domain Structure - /docs/poem-syntax-and-domain-structure.md
 *
 * TODO: Right now, the Poem contains the text, but there's probably a way to
 *       clean this up a bit.
 */
public class Poem implements Entity {
    @Getter
    private final String text;

    @Getter
    private final List<Line> lines = Lists.newArrayList();

    public Poem() {
        this.text = "";
    }

    public Poem(String text) {
        this.text = text;
    }

    // TODO: We're relying on two different kinds of equality here. The poem,
    //       which is directly from the editor, for a fast comparison. And then
    //       use the line equality as well. As there are cases where two
    //       identical poems are the same with different whitespace or
    //       arrangment.
    @Override
    public int hashCode() {
        return Objects.hash(text, lines);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Poem other) {
            return Objects.equals(text, other.text) && lines.equals(other.lines);
        }
        return false;
    }
}
