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
import net.cdahmedeh.poetwrite.ui.app.PersistenceHandler;
import net.cdahmedeh.poetwrite.ui.async.AppTask;
import net.cdahmedeh.poetwrite.ui.event.*;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

public class MenuViewModel extends ViewModel {
    private BehaviorSubject<Boolean> confirmationNeeded = BehaviorSubject.createDefault(false);


    @AssistedInject
    public MenuViewModel(TaskBus taskBus) {
        super(taskBus);
    }

    public Observable<Boolean> streamConfirmationNeeded() {
        return confirmationNeeded.hide();
    }

    @AssistedFactory
    public interface MenuViewModelFactory {
        MenuViewModel create();
    }

    @Override
    protected void listen(AppTask task, AppEvent event) {
        if (event instanceof ContentChangedEvent contentChangedEvent) {
            boolean changed = contentChangedEvent.getStatus() == PersistenceHandler.FileStatus.CHANGED;
            this.confirmationNeeded.onNext(changed);
        }

        if (event instanceof SaveEvent saveEvent) {
            this.confirmationNeeded.onNext(false);
        }

        if (event instanceof NewFileEvent newFileEvent) {
            this.confirmationNeeded.onNext(false);
        }

        if (event instanceof FileOpenedEvent fileOpenedEvent) {
            this.confirmationNeeded.onNext(false);
        }
    }
}
