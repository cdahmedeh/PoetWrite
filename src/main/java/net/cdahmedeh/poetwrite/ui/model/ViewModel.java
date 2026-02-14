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
import net.cdahmedeh.poetwrite.ui.async.AsynchronousTaskHandler;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.async.AsynchronousTaskHandler.AsynchronousTaskHandlerStatus;

public abstract class ViewModel {
    private AsynchronousTaskHandler taskHandler;
    private BehaviorSubject<AsynchronousTaskHandlerStatus> taskCurrent = BehaviorSubject.createDefault(AsynchronousTaskHandlerStatus.empty());
    protected Observable<AsynchronousTaskHandlerStatus> taskStatus = taskCurrent.hide();

    public ViewModel(AsynchronousTaskHandler taskHandler) {
        this.taskHandler = new AsynchronousTaskHandler();

        taskHandler.stream().subscribe(task -> {
            listen(task, task.getEvent());
        });
    }

    protected abstract void listen(AsynchronousTaskHandler.AsynchronousTask task, AppEvent event);
}
