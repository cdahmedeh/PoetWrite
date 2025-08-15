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

package net.cdahmedeh.poetwrite.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Ahmed El-Hajjar
 *
 * A node is anything that is within a Line. In the simplest of cases, it's just
 * one Words node that has the entire text. But if we put notes or asides, then
 * they join in as other nodes.
 *
 * This is still an early design. See the comments in Line.
 *
 * See ./docs/poem-syntax-and-entity-structure.md for a more complete view.
 */
public class Node {
    @Getter
    private final String text;

    public Node(String text) {
        this.text = text;
    }
}
