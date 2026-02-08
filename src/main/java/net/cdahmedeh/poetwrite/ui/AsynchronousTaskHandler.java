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

    @RequiredArgsConstructor
    public static class AsynchronousTaskHandlerStatus {
        @Getter private final AsynchronousTask current;
        @Getter private final boolean busy;
        @Getter private final int progress;
        @Getter private final int total;

        public static AsynchronousTaskHandlerStatus empty() {
            return new AsynchronousTaskHandlerStatus(AsynchronousTask.empty(), false, 0, 0);
        }
    }

    public static class TaskHandlerStartedEvent extends AppEvent {}

    private List<AsynchronousTask> tasks = new ArrayList<>();

    private BehaviorSubject<AsynchronousTaskHandlerStatus> status
            = BehaviorSubject.createDefault(AsynchronousTaskHandlerStatus.empty());

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    @Inject
    public AsynchronousTaskHandler() {
    }

    public Observable<AsynchronousTaskHandlerStatus> status() {
        return status.hide();
    }

    public void submit(String name, AppEvent event, Runnable run) {
        AsynchronousTask task = new AsynchronousTask(name, event, run);

        increase(task);

        this.pool.submit(() -> {
            try {
                task.task.run();
            } finally {
                decrease(task);
            }
        });
    }

    private void increase(AsynchronousTask task) {
        tasks.add(task);

        AsynchronousTaskHandlerStatus status = this.status.getValue();
        this.status.onNext(
                new AsynchronousTaskHandlerStatus(
                        task,
                        tasks.size() > 0,
                        status.progress,
                        Math.max(tasks.size(), status.total)
                        ));
    }

    public void decrease(AsynchronousTask task) {
        tasks.remove(task);

        AsynchronousTaskHandlerStatus status = this.status.getValue();
        this.status.onNext(
                new AsynchronousTaskHandlerStatus(
                        task,
                        tasks.size() > 0,
                        status.progress + 1,
                        Math.max(tasks.size(), status.total)
                ));
    }


}