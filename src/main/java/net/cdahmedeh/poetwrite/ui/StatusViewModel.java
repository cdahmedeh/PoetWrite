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

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import net.cdahmedeh.poetwrite.ui.AsynchronousTaskHandler.AsynchronousTask;
import net.cdahmedeh.poetwrite.ui.AsynchronousTaskHandler.AsynchronousTaskHandlerStatus;

import javax.inject.Singleton;

import static java.lang.String.format;

public class StatusViewModel extends ViewModel {
    private BehaviorSubject<AsynchronousTaskHandlerStatus> taskHandlerStatus = BehaviorSubject.createDefault(AsynchronousTaskHandlerStatus.empty());

    @AssistedInject
    public StatusViewModel(AsynchronousTaskHandler taskHandler) {
        super(taskHandler);
    }

    @AssistedFactory
    public interface StatusViewModelFactory {
        StatusViewModel create();
    }

    @Override
    protected void listen(AsynchronousTask task, AppEvent event) {
    }

    public void setTaskHandlerStatus(AsynchronousTaskHandlerStatus taskHandlerStatus) {
        this.taskHandlerStatus.onNext(taskHandlerStatus);
    }

    public Observable<AsynchronousTaskHandlerStatus> stream() {
        return taskHandlerStatus.hide();
    }
}
