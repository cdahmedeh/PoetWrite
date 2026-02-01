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
    public static class AsynchronousTask {
        @Getter private final String name;
        @Getter private final Runnable task;
    }

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    private final AtomicInteger count = new AtomicInteger(0);

    private BehaviorSubject<Boolean> busy = BehaviorSubject.createDefault(false);

    private List<AsynchronousTask> tasks = new ArrayList<>();

    @Inject
    public AsynchronousTaskHandler() {}

    public void submit(String name, Runnable run) {
        AsynchronousTask task = new AsynchronousTask(name, run);

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
        System.out.println("Adding task: " + task.getName());
        System.out.println("Tasks: " + tasks.toString());
        this.count.incrementAndGet();
        tasks.add(task);
        busy.onNext(count.get() > 0);
    }

    public void decrease(AsynchronousTask task) {
        System.out.println("Removing task: " + task.getName());
        System.out.println("Tasks: " + tasks.toString());
        count.decrementAndGet();
        tasks.remove(task);
        busy.onNext(count.get() > 0);
    }

    public AsynchronousTask current() {
        return tasks.getLast();
    }

    public int count() {
        return tasks.size();
    }

    public Observable<Boolean> stream() {
        return this.busy.hide();
    }
}