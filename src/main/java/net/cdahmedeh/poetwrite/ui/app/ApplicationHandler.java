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

package net.cdahmedeh.poetwrite.ui.app;

import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.ApplicationClosedEvent;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * For handling application-related actions. Currently just to exit the
 * application cleanly.
 */
@Singleton
public class ApplicationHandler extends LazyService {

    @Inject
    protected ApplicationHandler(TaskBus taskBus) {
        super(taskBus);
    }

    @Override
    public String name() {
        return "Application Session Handler";
    }

    @Override
    protected void init() {
    }

    /**
     * This ensures that all tasks in the task bus have been completed. Will
     * be useful in the future once we have file writes. For example, waiting
     * until the files are saved before quiting the app.
     */
    public void hold() {
        taskBus.monitor()
                .takeUntil(s -> s.getRemaining() == 0)
                .blockingSubscribe();
    }

    /**
     * Closes the application. It's done a seperate thread because TaskBus might
     * fight with it.
     *
     * TODO: Find a way to make it happen using TaskBus.
     */
    public void close() {
        new Thread(() -> {
            hold();
            System.exit(0);
        }).start();
    }
}
