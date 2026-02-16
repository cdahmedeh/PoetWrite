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

package net.cdahmedeh.poetwrite.ui.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import net.cdahmedeh.poetwrite.ui.async.BusTask;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.async.TaskBusStatus;

public abstract class ViewModel {
    private TaskBus taskBus;
    private BehaviorSubject<TaskBusStatus> taskCurrent = BehaviorSubject.createDefault(TaskBusStatus.empty());
    protected Observable<TaskBusStatus> taskStatus = taskCurrent.hide();

    public ViewModel(TaskBus taskBus) {
        this.taskBus = new TaskBus();

        taskBus.stream().subscribe(status -> {
            listen(status.getTask(), status.getTask().getEvent());
        });
    }

    protected abstract void listen(BusTask task, AppEvent event);
}
