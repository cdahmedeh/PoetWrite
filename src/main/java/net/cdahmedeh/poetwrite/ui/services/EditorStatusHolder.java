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

package net.cdahmedeh.poetwrite.ui.services;

import lombok.Getter;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Keeps track of what is being done in the editor, such as the current line
 * that the user is selecting based on cursor location.
 *
 * Mostly used by the assisted queries. For example, for rhyming, switching
 * between the previous and next line.
 */
@Singleton
public class EditorStatusHolder extends LazyService {
    private static final Logger LOG = LoggerFactory.getLogger(EditorStatusHolder.class);

    // The parsed Poem object structure.
    @Getter
    private Poem poem;

    // The line selected by the cursor.
    // NOTE: INDEX STARTS AS 1 FOR FIRST LINE.
    @Getter
    private int currentLine = -1;

    @Inject
    protected EditorStatusHolder(TaskBus taskBus) {
        super(taskBus);
    }

    @Override
    public String name() {
        return "Editor Status";
    }

    @Override
    protected void init() {

    }

    /**
     * Called after the Poem has been parsed.
     */
    public void poem(Poem poem) {
        this.poem = poem;
    }

    /**
     * Called as soon as the user moves the cursor.
     */
    public void update(int line) {
        currentLine = line;
        LOG.debug("Editor Line Changed: {}", line);

        if (poem == null) {
            LOG.error("Poem is null");
        }
    }
}
