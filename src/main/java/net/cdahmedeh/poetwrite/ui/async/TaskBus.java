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

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
 * See ./docs/async-design.md for design overview.
 *
 * PoetWrite's main system for handling tasks. Virtually everything has to go
 * through here from initializing services to doing computations.
 *
 * Basically a fancy wrapper around a thread-pool. It just contains certain QoL
 * features like knowing the status of the TaskBus, like how much tasks are
 * running.
 *
 * Contains observables that can be used by the UI to display the currently
 * running tasks, and how many are left. The UI observable has an artificial
 * delay so that the user can see all the tasks that are being run.
 *
 * TODO: Single-threaded only.
 * TODO: (Very badly) Explain the really complicated notification system from
 *       the following methods:
 *          queue()
 *          set()
 *          progress()
 *          publish()
 *          announce()
 */

@Singleton
public class TaskBus {
    // Artificial delay to keep short-running tasks visible in the UI for a bit.
    private static final long STATUS_DELAY_MILLIS = 80;

    // Force everything to run on the main thread and keeps everything linear
    // and instant. Used for unit tests.
    private boolean testMode = false;

    // The pool that holds the tasks. Very simple, one task at a time. FIFO.
    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    // Instantaneous status of the TaskBus. Mainly used to determine if no
    // tasks are running when needing to quit the application.
    private BehaviorSubject<TaskBusStatus> stream =
            BehaviorSubject.createDefault(TaskBusStatus.empty());

    // UI friendly status monitor that introduces an artificial delay so that
    // it is visble to the user a little bit.
    private BehaviorSubject<TaskBusStatus> monitor
            = BehaviorSubject.createDefault(TaskBusStatus.empty());

    @Inject
    public TaskBus() {
    }

    public void enableTestMode() {
        this.testMode = true;
    }

    /**
     * Submit a task to the TaskBus. It basically takes a Runnable and puts it
     * into the thread-pool.
     *
     * It wraps the Runnable along with a friendly name that is visible on the
     * UI and the event that it is expected to throw after the task is
     * completed.
     */
    @SneakyThrows
    public void submit(String name, AppEvent event, Runnable run) {
        AppTask task = new AppTask(name, event, run);

        queue(task);

        Runnable runnable = () -> {
            try {
                set(task);
                run.run();
            } finally {
                publish(task);
                progress(task);
            }
        };

        Future<?> future = this.pool.submit(runnable);
        if (testMode) {
            future.get();
        }
    }

    public Observable<TaskBusStatus> stream() {
        return stream;
    }

    public Observable<TaskBusStatus> monitor() {
        return monitor
                .map(TaskBusStatus::snapshot)
                .concatMap(s -> Observable.just(s)
                        .delay(STATUS_DELAY_MILLIS, TimeUnit.MILLISECONDS))
                .replay(1)
                .refCount();
    }

    private void queue(AppTask task) {
        System.out.println(task.getName() + " queued");

        TaskBusStatus status = this.monitor.getValue();
        status.queue();

        announce(status);
    }

    private void set(AppTask task) {
        TaskBusStatus status = this.monitor.getValue();
        status.setTask(task);

        announce(status);
    }

    private void progress(AppTask task) {
        TaskBusStatus status = this.monitor.getValue();

        status.forward();

        if (status.isBusy() == false) {
            status.reset();
        }

        announce(status);
    }

    private void publish(AppTask task) {
        TaskBusStatus status = this.monitor.getValue();
        status.setTask(task);
        this.stream.onNext(status);
    }

    private void announce(TaskBusStatus status) {
        this.monitor.onNext(status);
    }
}