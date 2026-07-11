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

package net.cdahmedeh.poetwrite.ui.viewcontroller;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import net.cdahmedeh.poetwrite.ui.app.ApplicationHandler;
import net.cdahmedeh.poetwrite.ui.app.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.ContentChangedEvent;
import net.cdahmedeh.poetwrite.ui.event.FileDialogNeededEvent;
import net.cdahmedeh.poetwrite.ui.event.SaveEvent;
import net.cdahmedeh.poetwrite.ui.viewmodel.MainViewModel;

import java.io.File;


public class MainViewController extends ViewController<MainViewModel> {
    private final ApplicationHandler applicationHandler;
    private final PersistenceManager persistenceManager;


    @AssistedInject
    public MainViewController(@Assisted MainViewModel viewModel, TaskBus taskBus, ApplicationHandler applicationHandler, PersistenceManager persistenceManager) {
        super(viewModel, taskBus);
        this.applicationHandler = applicationHandler;
        this.persistenceManager = persistenceManager;
    }

    public void update(String content) {
        ContentChangedEvent event = new ContentChangedEvent();
        taskBus.submit("Updating Content", event, new Runnable() {
            @Override
            public void run() {
                persistenceManager.update(content);
                event.setStatus(persistenceManager.status());
            }
        });
    }

    public void ask(File selectedFile) {
        FileDialogNeededEvent event = new FileDialogNeededEvent();
        taskBus.submit("Saving Poem", event, () -> {
//            boolean check = persistenceHandler.check();
            event.setNeeded(true);
        });
    }

    @AssistedFactory
    public interface MainViewControllerFactory {
        MainViewController create(MainViewModel mainViewModel);
    }

    public void save() {
        SaveEvent event = new SaveEvent();

        taskBus.submit("Saving Poem", event, () -> {
            persistenceManager.save();
            event.setFile(persistenceManager.getCurrentFile().getFileName().toString());
        });
    }

    public void save(File selectedFile) {
        SaveEvent event = new SaveEvent();

        taskBus.submit("Saving Poem", event, () -> {
            persistenceManager.save(selectedFile);
            event.setFile(persistenceManager.getCurrentFile().getFileName().toString());
        });
    }

    public void closeApp() {
        applicationHandler.close();
    }

}
