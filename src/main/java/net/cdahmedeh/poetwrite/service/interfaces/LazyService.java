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

/**
 * See ./docs/async-design.md for design overview.
 *
 * A LazyService is service that is intended to be loaded asynchronously,
 * ideally outside of a UI thread. It uses the TaskBus and publishes the
 * initialization code there.
 *
 * It abuses how Dagger handles dependency resolution. Since it can generate
 * a graph of the dependency, you're guaranteed to have the services initialized
 * in the right order.
 *
 * TODO: What happens when this becomes truly multithreaded?
 * TODO: What happens when services are so expensive that they block frequent
 *       tasks like parsing?
 */
public abstract class LazyService {
    // If the service has been inited.
    // It is volatile because there's the possibiliy that multiple threads might
    // be trying to do the init, and would run into issues if each thread saw
    // different true/false values.
    private volatile boolean initialized = false;

    protected final TaskBus taskBus;

    protected LazyService(TaskBus taskBus) {
        this.taskBus = taskBus;
        ensure();
    }

    /**
     * The friendly name of the service. Really only for the UI progress bar.
     */
    public abstract String name();

    /**
     * Called automatically upon construction. This submits the initialization
     * code on the TaskBus.
     */
    public void ensure() {
        if (initialized) return;

        taskBus.submit(String.format("Starting %s",name()), new ServiceStartingEvent(), () -> {
            init();
            initialized = true;
        });
    };

    /**
     * Your initialization code for the service. For example, loading a
     * dictionary file or starting some external service.
     */
    protected abstract void init();
}
