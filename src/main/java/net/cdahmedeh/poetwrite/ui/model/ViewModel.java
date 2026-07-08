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
import net.cdahmedeh.poetwrite.ui.async.AppTask;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.async.TaskBusStatus;

/**
 * See ./docs/ui-architecture.md for design overview.
 *
 * The ViewModel contains whatever the view wants to display.
 *
 * Implementation Guide:
 * - All data/variables should be in a BehaviourSubject observable. So that the
 *   View can later listen to them for changes.
 * - Extend listen(..) to determine how to respond to results that come from
 *   AppEvent(s).
 * - Never refer to the view directly.
 *
 * IMPORTANT NOTE:
 * Normally, models would be waiting for an event from the TaskBus. But we can't
 * do this because we'd have an infinite loop of events coming in. So instead,
 * the model is listening directly from TaskBus itself.
 */
public abstract class ViewModel {
    private TaskBus taskBus;
    private BehaviorSubject<TaskBusStatus> taskCurrent = BehaviorSubject.createDefault(TaskBusStatus.empty());

    public ViewModel(TaskBus taskBus) {
        this.taskBus = taskBus;

        taskBus.stream().subscribe(status -> {
            listen(status.getTask(), status.getTask().getEvent());
        });
    }

    protected abstract void listen(AppTask task, AppEvent event);
}
