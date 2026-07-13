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


package net.cdahmedeh.poetwrite.ui.async;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.event.TaskBusStartedEvent;

/**
 * See ./docs/async-design.md for design overview.
 *
 * Wrapper for Tasks submitted to TaskBus.
 *
 */
@RequiredArgsConstructor
public class AppTask {
        // The friendly name for the task. This is for display in the UI
        // progress status bar.
        //
        // Not a constant or method in the event because some tasks use the same
        // kind of event. Such as creating a new file or saving. So each have
        // a different message.
        @Getter
        private final String name;

        // The event. This just contains the results of the computations when
        // a Task is executed. Keep in mind that both the ViewController and
        // the service have a say on what's in here.
        @Getter
        private final AppEvent event;

        // The async task itself as a runnable. This is what the thread pool
        // in TaskBus uses.
        @Getter
        private final Runnable task;

        /**
         * This is the placeholder task when none are running. Really only for
         * the UI.
         *
         * TODO: Remove the need for a status item for when nothing is running.
         *       Just a bit of clean up, but will do for now.
         */
        public static <E extends AppEvent> AppTask empty() {
            return new AppTask(
                    "",
                    new TaskBusStartedEvent(),
                    () -> {
                    });
        }
}
