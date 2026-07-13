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
import net.cdahmedeh.poetwrite.annotation.Duplicated;
import net.cdahmedeh.poetwrite.ui.services.ApplicationHandler;
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.ContentChangedEvent;
import net.cdahmedeh.poetwrite.ui.event.SaveRequestedEvent;
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

    @AssistedFactory
    public interface MainViewControllerFactory {
        MainViewController create(MainViewModel mainViewModel);
    }

    /**
     * Content in the text editor has changed. Notify the persistence manager.
     */
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

    /**
     * Request a save. Check if the save selection dialog is needed.
     */
    public void ask(File selectedFile) {
        SaveRequestedEvent event = new SaveRequestedEvent();
        taskBus.submit("Saving Poem", event, () -> {
            event.setDialogNeeded(true);
        });
    }

    /**
     * Save the loaded file onto disk.
     */
    public void save() {
        SaveEvent event = new SaveEvent();

        taskBus.submit("Saving Poem", event, () -> {
            persistenceManager.save();
            event.setFile(persistenceManager.getFile().getFileName().toString());
        });
    }

    /**
     * Save a selected file onto the disk.
     */
    public void save(File selectedFile) {
        SaveEvent event = new SaveEvent();

        taskBus.submit("Saving Poem", event, () -> {
            persistenceManager.save(selectedFile);
            event.setFile(persistenceManager.getFile().getFileName().toString());
        });
    }

    /**
     * Request to have the application closed. Application handler will kindly
     * wait until all taskbus tasks are done.
     */
    @Duplicated("MenuViewController.closeApp()")
    public void closeApp() {
        applicationHandler.close();
    }

}
