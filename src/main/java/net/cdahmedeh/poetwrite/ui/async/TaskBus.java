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

    public Observable<TaskBusStatus> stream() {
        return stream;
    }

    public Observable<TaskBusStatus> monitor() {
        return monitor
                .map(s -> TaskBusStatus.snapshot(s))
                .observeOn(Schedulers.computation())
                .concatMap(s -> Observable.just(s).delay(100, java.util.concurrent.TimeUnit.MILLISECONDS))
                .replay(1)
                .refCount();
    }

    public void submit(String name, AppEvent event, Runnable run) {
        AsyncTask task = new AsyncTask(name, event, run);

        queue(task);

        this.pool.submit(() -> {
            try {
                set(task);
                run.run();
            } finally {
                publish(task);
                progress(task);
            }
        });
    }

    private void queue(AsyncTask task) {
        TaskBusStatus status = this.monitor.getValue();
        status.queue();

        this.monitor.onNext(status);
    }

    private void set(AsyncTask task) {
        TaskBusStatus status = this.monitor.getValue();
        status.setTask(task);

        this.monitor.onNext(status);
    }

    private void progress(AsyncTask task) {
        TaskBusStatus status = this.monitor.getValue();
        status.forward();

        if (status.isBusy() == false) {
            status.reset();
        }

        monitor.onNext(status);
    }

    public void publish(AsyncTask task) {
        TaskBusStatus status = this.monitor.getValue();
        this.stream.onNext(TaskBusStatus.update(status, task));
    }

    public void announce(TaskBusStatus status) {
        this.monitor.onNext(TaskBusStatus.snapshot(status));
    }
}