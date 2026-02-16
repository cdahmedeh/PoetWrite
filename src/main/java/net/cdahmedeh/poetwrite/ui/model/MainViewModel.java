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
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.event.TextUpdateEvent;
import net.cdahmedeh.poetwrite.ui.async.AppTask;

public class MainViewModel extends ViewModel {
    private BehaviorSubject<String> text = BehaviorSubject.createDefault("");

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
        if (event instanceof TextUpdateEvent textUpdateEvent) {
            String text = textUpdateEvent.getText();
            this.text.onNext(text);
        }
    }

    public Observable<String> streamText() {
        return this.text.hide();
    }
}
