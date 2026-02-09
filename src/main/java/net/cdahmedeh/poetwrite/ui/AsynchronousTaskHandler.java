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

package net.cdahmedeh.poetwrite.ui;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class AsynchronousTaskHandler {
    @RequiredArgsConstructor
    public static class AsynchronousTask<E extends AppEvent> {
        @Getter private final String name;
        @Getter private final E event;
        @Getter private final Runnable task;

        public static <E extends AppEvent> AsynchronousTask<E> empty() {
            return new AsynchronousTask<E>(
                    "Task Handler Starting",
                    (E) new TaskHandlerStartedEvent(),
                    () -> {});
        }
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class AsynchronousTaskHandlerStatus {
        @Getter private AsynchronousTask current;
        @Getter private boolean busy;
        @Getter private int progress;
        @Getter private int total;

        public static AsynchronousTaskHandlerStatus empty() {
            return new AsynchronousTaskHandlerStatus(AsynchronousTask.empty(), false, 0, 0);
        }
    }

    public static class TaskHandlerStartedEvent extends AppEvent {}

    private List<AsynchronousTask> tasks = new ArrayList<>();

    private BehaviorSubject<AsynchronousTaskHandlerStatus> status
            = BehaviorSubject.createDefault(AsynchronousTaskHandlerStatus.empty());

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    private BehaviorSubject<AsynchronousTask> publish = BehaviorSubject.createDefault(AsynchronousTask.empty());

    @Inject
    public AsynchronousTaskHandler() {
    }

    public Observable<AsynchronousTaskHandlerStatus> status() {
        return status.hide();
    }

    public Observable<AsynchronousTask> stream() {
        return publish.hide();
    }

    public void submit(String name, AppEvent event, Runnable run) {
        AsynchronousTask task = new AsynchronousTask(name, event, run);

        increase(task);

        this.pool.submit(() -> {
            try {
                System.out.println("Running task: " + task.name);
                set(task);
                task.task.run();
            } finally {
                System.out.println("Finished task: " + task.name);
                this.publish.onNext(task);
                decrease(task);
            }
        });
    }

    private void set(AsynchronousTask task) {
        AsynchronousTaskHandlerStatus status = this.status.getValue();
        status.current = task;

        this.status.onNext(status);
    }

    private void increase(AsynchronousTask task) {
        tasks.add(task);

        AsynchronousTaskHandlerStatus status = this.status.getValue();
        status.busy = tasks.size() > 0 ? true : false;
        status.total = status.busy ? status.total + 1 : 0;

        this.status.onNext(status);
    }

    public void decrease(AsynchronousTask task) {
        tasks.remove(task);

        AsynchronousTaskHandlerStatus status = this.status.getValue();
        status.busy = tasks.size() > 0 ? true : false;
        status.progress = status.progress + 1;
        status.total = status.progress != status.total ? status.total : 0;
        status.progress = status.progress < status.total ? status.progress : 0;

        this.status.onNext(status);
    }
}