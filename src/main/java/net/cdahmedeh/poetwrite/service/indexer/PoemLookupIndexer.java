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

    public NavigableMap<Integer, Word> index(Poem poem) {
        NavigableMap<Integer, Word> index = new TreeMap<>();
        for (Line line : poem.getLines()) {
            for (Word word : line.getAllWords()) {
                index.put(word.getStart(), word);
            }
        }
        return index;
    }
}
