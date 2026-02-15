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
import net.cdahmedeh.poetwrite.ui.event.AppEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class TaskBus {

    private List<AsyncTask> tasks = new ArrayList<>();

    private BehaviorSubject<TaskBusStatus> stream =
            BehaviorSubject.createDefault(TaskBusStatus.empty());

    private BehaviorSubject<TaskBusStatus> monitor
            = BehaviorSubject.createDefault(TaskBusStatus.empty());

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    @Inject
    public TaskBus() {
    }

    public Observable<TaskBusStatus> monitor() {
        return monitor.hide();
    }

    public Observable<TaskBusStatus> stream() {
        return stream.hide();
    }

    public void submit(String name, AppEvent event, Runnable run) {
        AsyncTask task = new AsyncTask(name, event, run);

        increase(task);

        this.pool.submit(() -> {
            try {
                set(task);
                task.getTask().run();
            } finally {
                publish(task);
                decrease(task);
            }
        });
    }

    private void set(AsyncTask task) {
        TaskBusStatus status = this.monitor.getValue();
        status.setCurrent(task);

        announce(status);
    }

    private void increase(AsyncTask task) {
        boolean busy = !tasks.isEmpty();
        tasks.add(task);

        TaskBusStatus status = this.monitor.getValue();

        if (!busy) {
            status.setProgress(0);
            status.setTotal(0);
        }

        status.setBusy(true);
        status.setTotal(status.getTotal() + 1);

        announce(status);
    }

    public void decrease(AsyncTask task) {
        tasks.remove(task);

        TaskBusStatus status = this.monitor.getValue();

        status.setProgress(status.getProgress() + 1);

        boolean busy = !tasks.isEmpty();
        status.setBusy(busy);

        if (!busy) {
            status.setProgress(0);
            status.setTotal(0);
        }

        announce(status);
    }

    public void publish(AsyncTask task) {
        TaskBusStatus status = this.stream.getValue();
        this.stream.onNext(TaskBusStatus.update(status, task));
    }

    public void announce(TaskBusStatus status) {
        this.monitor.onNext(TaskBusStatus.snapshot(status));
    }

}