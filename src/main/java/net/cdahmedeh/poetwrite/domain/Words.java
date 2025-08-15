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

import java.util.List;

/**
 * @author Ahmed El-Hajjar
 *
 * Lazy way to make an array of words. I just did this so I keep the Word object
 * as is until I have a better idea of all of this will be tied together.
 *
 * We can see as the 'default' node type for a line.
 *
 * This is still an early design. See the comments in Line.
 *
 * See ./docs/poem-syntax-and-entity-structure.md for a more complete view.
 */
public class Words extends Node {
    @Getter
    private final List<Word> words = Lists.newArrayList();

    public Words(String text) {
        super(text);
    }
}
