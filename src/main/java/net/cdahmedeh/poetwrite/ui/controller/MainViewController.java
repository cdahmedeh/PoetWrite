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

package net.cdahmedeh.poetwrite.ui.controller;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import net.cdahmedeh.poetwrite.ui.app.ApplicationHandler;
import net.cdahmedeh.poetwrite.ui.app.PersistenceHandler;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.ContentUpdateEvent;
import net.cdahmedeh.poetwrite.ui.model.MainViewModel;


public class MainViewController extends ViewController<MainViewModel> {
    private final ApplicationHandler applicationHandler;
    private final PersistenceHandler persistenceHandler;


    @AssistedInject
    public MainViewController(@Assisted MainViewModel viewModel, TaskBus taskBus, ApplicationHandler applicationHandler, PersistenceHandler persistenceHandler) {
        super(viewModel, taskBus);
        this.applicationHandler = applicationHandler;
        this.persistenceHandler = persistenceHandler;
    }

    public void update(String content) {
        ContentUpdateEvent event = new ContentUpdateEvent();
        taskBus.submit("Updating Content", event, new Runnable() {
            @Override
            public void run() {
                persistenceHandler.update(content);
            }
        });
    }

    @AssistedFactory
    public interface MainViewControllerFactory {
        MainViewController create(MainViewModel mainViewModel);
    }

    public void closeApp() {
        applicationHandler.close();
    }

}
