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

package net.cdahmedeh.poetwrite.service.indexer;

import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * This is used to build an index that maps character positions to actual
 * entities in Poem that were found during the parsing process.
 *
 * This allows finding the original entity when selecting a piece of text. Or
 * in PoetWrite's case, hovering the cursor over it.
 *
 * This was an issue I discussed in the "Poem Analysis Implementation and Cache
 * Design" document.
 */
@Singleton
public class PoemLookupIndexer extends LazyService {
    @Inject
    /* package */ PoemLookupIndexer(TaskBus taskBus) {
        super(taskBus);
    }

    @Override
    public String name() {
        return "Poem Text Position Indexer";
    }

    @Override
    protected void init() {

    }

    /**
     * Maps a character position to the poem, line and word found there.
     *
     * Used to hold just the Word. It holds a HoverContext now because the
     * hover analyses need more than the word: a definition needs the word, a
     * part of speech or the meter needs the line, a rhyme group needs the
     * poem. This walk already has all three in hand, so handing them over
     * costs nothing here and saves searching for them later. Line has no
     * offsets of its own, so working it out afterwards would be miserable.
     */
    public NavigableMap<Integer, HoverContext> index(Poem poem) {
        NavigableMap<Integer, HoverContext> index = new TreeMap<>();
        for (Line line : poem.getLines()) {
            for (Word word : line.getAllWords()) {
                index.put(word.getStart(), new HoverContext(poem, line, word));
            }
        }
        return index;
    }
}
