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

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.async.AsyncTask;
import net.cdahmedeh.poetwrite.ui.async.TaskBusStatus;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

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
    protected void listen(AsyncTask task, AppEvent event) {
    }

    public void setTaskHandlerStatus(TaskBusStatus taskHandlerStatus) {
        this.taskHandlerStatus.onNext(taskHandlerStatus);
    }

    public Observable<TaskBusStatus> stream() {
        return taskHandlerStatus.hide();
    }
}
