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

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Ahmed El-Hajjar
 *
 * This is the root node of the tree structure that represents a line of poetry.
 *
 * Right now, the syntax of a line is really simple.
 *
 * Take this example:
 * We are going to (really) sing this song. [A Comment Here]
 *
 * It turns into this
 *
 * Line -> Node -> Words "We are going to" + Aside "really" + Words "sing this song" + Note "A Comment Here".
 *
 * Really hate the concept of having to design your domain objects first. There
 * is still some brainwashing left when I was introduced to OOP.
 *
 * My plan is to eventually put the results of the analyses into this structure
 * as some kind of optimization. These don't have persistence so I'm not going
 * to worry about making it ORM compliant.
 *
 * See ./docs/poem-syntax-and-entity-structure.md for a more complete view.
 */
@RequiredArgsConstructor
public class Line {
    @Getter
    private final String text;

    @Getter
    private final List<Node> nodes = Lists.newArrayList();
}
