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

import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;
import net.cdahmedeh.poetwrite.ui.event.ServiceStartingEvent;
import net.cdahmedeh.poetwrite.ui.event.WelcomeEvent;

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

    /**
     * Just puts a welcome message in the cue. Simply as a thing to add a bit
     * of soul to the application while it starts. It doesn't actually do
     * anything.
     *
     * TODO: I can see it in the future to be used to hold off intiting the UI
     *       so that the window shows up as soon as possible.
     */
    public void sendWelcomeMessage() {
        taskBus.submit(WelcomeEvent.TASK_WELCOME_MESSAGE, new WelcomeEvent(), () -> {
            SleepTools.safeSleep(2000);
        });
    }
}
