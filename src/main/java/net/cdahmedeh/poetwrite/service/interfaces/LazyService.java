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

package net.cdahmedeh.poetwrite.service.interfaces;

import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.ServiceStartingEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LazyService {
    private volatile boolean initialized = false;

    protected final TaskBus taskBus;

    protected LazyService(TaskBus taskBus) {
        this.taskBus = taskBus;
        ensure();
    }

    public abstract String name();

    public void ensure() {
        if (initialized) return;

        taskBus.submit(String.format("Starting %s ",name()), new ServiceStartingEvent(), () -> {
            init();
            initialized = true;
        });
    };

    protected abstract void init();
}
