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
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.event.*;
import net.cdahmedeh.poetwrite.ui.services.ApplicationHandler;
import net.cdahmedeh.poetwrite.service.generator.TextGenerator;
import net.cdahmedeh.poetwrite.ui.viewmodel.MenuViewModel;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import java.io.File;

public class MenuViewController extends ViewController<MenuViewModel> {
    private final TextGenerator textGenerator;
    private final ApplicationHandler applicationHandler;
    private final PersistenceManager persistenceManager;

    @AssistedInject
    protected MenuViewController(@Assisted MenuViewModel viewModel, TaskBus taskBus, TextGenerator textGenerator, ApplicationHandler applicationHandler, PersistenceManager persistenceManager) {
        super(viewModel, taskBus);
        this.textGenerator = textGenerator;
        this.applicationHandler = applicationHandler;
        this.persistenceManager = persistenceManager;
    }



    @AssistedFactory
    public interface MenuViewControllerFactory {
        MenuViewController create(MenuViewModel menuViewModel);
    }



    /**
     * Request a save to see the file dialog is needed. Such as for a new file
     * or save as function.
     */
    public void save() {
        SaveRequestedEvent event = new SaveRequestedEvent();
        taskBus.submit("Saving Poem", event, () -> {
            boolean check = persistenceManager.getStatus() == PersistenceManager.FileStatus.NEW;
            event.setDialogNeeded(check);
        });
    }

    /**
     * Create new blank poem.
     */
    public void create() {
        NewFileEvent event = new NewFileEvent();
        taskBus.submit("Creating New Poem", event, () -> {
            persistenceManager.create();
            event.setFile(persistenceManager.getFile().getFileName().toString());
        });
    }

    /**
     * Open a file. The user is prompted for what file to pick with a file
     * chooser.
     */
    public void open(File file) {
        FileOpenedEvent event = new FileOpenedEvent();
        taskBus.submit("Opening File", event, () -> {
            persistenceManager.open(file);
            event.setContent(persistenceManager.getContent());
            event.setFile(persistenceManager.getFile().getFileName().toString());
        });
    }

    /**
     * Just like save(), but this one guarantees to show a dialog as the user
     * selected Save As.
     */
    public void saveAs() {
        SaveRequestedEvent event = new SaveRequestedEvent();
        taskBus.submit("Saving Poem", event, () -> {
            event.setDialogNeeded(true);
        });
    }

    /**
     * Request to have the application closed. Application handler will kindly
     * wait until all taskbus tasks are done.
     */
    @Duplicated("MainViewController.closeApp")
    public void closeApp() {
        applicationHandler.close();
    }

//    public void generateRandomText() {
//        for (int i = 0; i < 10; i++) {
//            TextUpdateEvent event = new TextUpdateEvent();
//            taskBus.submit("Generating Random Text " + new Random().nextDouble(), event, () -> {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                String text = textGenerator.generate();
//                event.setText(text);
//            });
//        }
//    }
}
