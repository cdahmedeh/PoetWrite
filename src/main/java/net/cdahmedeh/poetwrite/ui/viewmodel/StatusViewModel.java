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

package net.cdahmedeh.poetwrite.ui.viewmodel;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.async.AppTask;
import net.cdahmedeh.poetwrite.ui.async.TaskBusStatus;

import static java.lang.String.format;

/**
 * The component that displays the current task bus status lies here. It streams
 * directly from TaskBus.
 *
 * A bit confusing bit because the convention lifecycle isn't followed
 * properly.
 *
 * View -> ViewController -> TaskBus -> Event -> ViewModel -> View
 *
 * The issue is that the StatusViewModel is also listening to the TaskBus.
 *
 * TODO: Revisit lifecycle.
 * TODO: Consider if the Model can listen to taskbus directly.
 */
public class StatusViewModel extends ViewModel {
    private BehaviorSubject<TaskBusStatus> taskHandlerStatus = BehaviorSubject.createDefault(TaskBusStatus.empty());

    @AssistedInject
    public StatusViewModel(TaskBus taskBus) {
        super(taskBus);
    }

    @AssistedFactory
    public interface StatusViewModelFactory {
        StatusViewModel create();
    }

    @Override
    protected void listen(AppTask task, AppEvent event) {
    }

    /**
     * Called by the controller when the taskbus notifies the controller
     * which in turn is listening to the TaskBus stream.
     */
    public void setTaskHandlerStatus(TaskBusStatus taskHandlerStatus) {
        this.taskHandlerStatus.onNext(taskHandlerStatus);
    }

    /**
     * Just the subscriber/listener from when the status changes. The StatusView
     * listens to this.
     */
    public Observable<TaskBusStatus> stream() {
        return taskHandlerStatus.hide();
    }
}
