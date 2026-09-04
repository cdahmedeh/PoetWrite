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

package net.cdahmedeh.poetwrite.service.indexer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.lib.domain.Word;

/**
 * The tooltip contains a bunch of information about the word. But keep in mind
 * some anlayses depends on different things.
 *
 * For example, for a word, it only needs to be aware of the word itself. But
 * however, the part-of-speech is fully depending on the line.
 *
 * It is built during the parsing process. Which basically maps a character
 * position to a certain word.
 *
 * We're doing something like
 * NavigableMap<Integer, HoverContext> accross the board.
 */
@RequiredArgsConstructor
public class HoverContext {
    @Getter
    private final Poem poem;

    @Getter
    private final Line line;

    @Getter
    private final Word word;
}
