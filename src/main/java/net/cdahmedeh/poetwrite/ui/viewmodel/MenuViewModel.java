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
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.async.AppTask;
import net.cdahmedeh.poetwrite.ui.event.*;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

public class MenuViewModel extends ViewModel {
    // Determines if a confirmation dialog is needed to prompt the user if they
    // want to replace an existing file.
    private BehaviorSubject<Boolean> confirmationNeeded = BehaviorSubject.createDefault(false);
    public Observable<Boolean> confirmationNeeded() {return confirmationNeeded.hide();}

    @AssistedInject
    public MenuViewModel(TaskBus taskBus) {
        super(taskBus);
    }

    @AssistedFactory
    public interface MenuViewModelFactory {
        MenuViewModel create();
    }

    @Override
    protected void listen(AppTask task, AppEvent event) {
        // If the content changes, it means that confirmation dialogue is
        // needed.
        if (event instanceof ContentChangedEvent contentChangedEvent) {
            boolean changed = contentChangedEvent.getStatus() == PersistenceManager.FileStatus.CHANGED;
            this.confirmationNeeded.onNext(changed);
        }

        // TODO: Cleanup
        // TODO: Revisit
        // These Might be redundant right now because ContentChangedEvent gets
        // calls when anything changes.

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
