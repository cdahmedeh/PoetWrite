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
import net.cdahmedeh.poetwrite.ui.app.PersistenceHandler;
import net.cdahmedeh.poetwrite.ui.event.*;
import net.cdahmedeh.poetwrite.ui.app.ApplicationHandler;
import net.cdahmedeh.poetwrite.service.generator.TextGenerator;
import net.cdahmedeh.poetwrite.ui.model.MenuViewModel;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import java.io.File;
import java.util.Random;

public class MenuViewController extends ViewController<MenuViewModel> {
    private final TextGenerator textGenerator;
    private final ApplicationHandler applicationHandler;
    private final PersistenceHandler persistenceHandler;

    @AssistedInject
    protected MenuViewController(@Assisted MenuViewModel viewModel, TaskBus taskBus, TextGenerator textGenerator, ApplicationHandler applicationHandler, PersistenceHandler persistenceHandler) {
        super(viewModel, taskBus);
        this.textGenerator = textGenerator;
        this.applicationHandler = applicationHandler;
        this.persistenceHandler = persistenceHandler;
    }



    @AssistedFactory
    public interface MenuViewControllerFactory {
        MenuViewController create(MenuViewModel menuViewModel);
    }

    public void generateRandomText() {
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
    }

    public void save() {
        FileDialogNeededEvent event = new FileDialogNeededEvent();
        taskBus.submit("Checking If File Dialog Needed", event, () -> {
            boolean check = persistenceHandler.check();
            event.setNeeded(check);
        });
    }

    public void create() {
        NewFileEvent event = new NewFileEvent();
        taskBus.submit("Creating New Poem", event, () -> {
            persistenceHandler.create();
            event.setFile(persistenceHandler.getCurrentFile().getFileName().toString());
        });
    }

    public void open(File file) {
        FileOpenedEvent event = new FileOpenedEvent();
        taskBus.submit("Opening File", event, () -> {
            persistenceHandler.open(file);
            event.setContent(persistenceHandler.getContent());
            event.setFile(persistenceHandler.getCurrentFile().getFileName().toString());
        });
    }

    public void saveAs() {
        FileDialogNeededEvent event = new FileDialogNeededEvent();
        taskBus.submit("Checking If File Dialog Needed", event, () -> {
            boolean check = persistenceHandler.check();
            event.setNeeded(true);
        });
    }

    public void closeApp() {
        applicationHandler.close();
    }

}
