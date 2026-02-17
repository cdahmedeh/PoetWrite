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

@Singleton
public class TaskBus {
    private static final long STATUS_DELAY_MILLIS = 80;

    private boolean testMode = false;

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    private BehaviorSubject<TaskBusStatus> stream =
            BehaviorSubject.createDefault(TaskBusStatus.empty());

    private BehaviorSubject<TaskBusStatus> monitor
            = BehaviorSubject.createDefault(TaskBusStatus.empty());

    @Inject
    public TaskBus() {
    }

    public void enableTestMode() {
        this.testMode = true;
    }

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
                .observeOn(Schedulers.computation())
                .concatMap(s -> Observable.just(s).delay(STATUS_DELAY_MILLIS, java.util.concurrent.TimeUnit.MILLISECONDS))
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