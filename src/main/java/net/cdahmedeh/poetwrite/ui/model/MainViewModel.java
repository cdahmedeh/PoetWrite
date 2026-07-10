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
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.*;
import net.cdahmedeh.poetwrite.ui.async.AppTask;

public class MainViewModel extends ViewModel {
    private BehaviorSubject<String> text = BehaviorSubject.createDefault("");

    private BehaviorSubject<Boolean> dialogNeeded = BehaviorSubject.createDefault(false);

    private BehaviorSubject<String> fileName = BehaviorSubject.createDefault("");

    private BehaviorSubject<Boolean> fileChanged = BehaviorSubject.createDefault(true);

    @AssistedInject
    public MainViewModel(TaskBus taskBus) {
        super(taskBus);
    }

    @AssistedFactory
    public interface MainViewModelFactory {
        MainViewModel create();
    }

    @Override
    protected void listen(AppTask task, AppEvent event) {
        if (event instanceof ContentChangedEvent contentChangedEvent) {
            String text = contentChangedEvent.getContent();
            this.fileChanged.onNext(contentChangedEvent.isChanged());
        }

        if (event instanceof SaveEvent saveEvent) {
            this.fileChanged.onNext(false);
        }

        if (event instanceof NewFileEvent newFileEvent) {
            this.fileName.onNext(newFileEvent.getFile());
            this.text.onNext("");
            this.fileChanged.onNext(true);
        }

        if (event instanceof FileOpenedEvent fileOpenedEvent) {
            this.fileName.onNext(fileOpenedEvent.getFile());
            this.text.onNext(fileOpenedEvent.getContent());
        }

        if (event instanceof FileDialogNeededEvent dialogNeededEvent) {
            this.dialogNeeded.onNext(dialogNeededEvent.isNeeded());
        }

        if (event instanceof FileEvent) {
            this.fileName.onNext(((FileEvent) event).getFile());
        }
    }

    public Observable<String> streamText() {
        return this.text.hide();
    }

    public Observable<Boolean> streamDialogNeeded() {
        return this.dialogNeeded.hide();
    }

    public Observable<Boolean> streamFileChanged() {
        return this.fileChanged.hide();
    }

    public Observable<String> streamFileName() {
        return this.fileName.hide();
    }
}
