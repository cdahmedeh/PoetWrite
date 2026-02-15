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
import net.cdahmedeh.poetwrite.ui.event.TextUpdateEvent;
import net.cdahmedeh.poetwrite.ui.app.ApplicationHandler;
import net.cdahmedeh.poetwrite.service.generator.TextGenerator;
import net.cdahmedeh.poetwrite.ui.model.MenuViewModel;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import java.util.Random;

public class MenuViewController extends ViewController<MenuViewModel> {
    private final TextGenerator textGenerator;
    private final ApplicationHandler applicationHandler;

    @AssistedInject
    protected MenuViewController(@Assisted MenuViewModel viewModel, TaskBus taskBus, TextGenerator textGenerator, ApplicationHandler applicationHandler) {
        super(viewModel, taskBus);
        this.textGenerator = textGenerator;
        this.applicationHandler = applicationHandler;
    }

    @AssistedFactory
    public interface MenuViewControllerFactory {
        MenuViewController create(MenuViewModel menuViewModel);
    }

    public void generateRandomText() {
        for (int i = 0; i < 100; i++) {
            TextUpdateEvent event = new TextUpdateEvent();
            taskBus.submit("Generating Random Text " + new Random().nextDouble(), event, () -> {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String text = textGenerator.generate();
                event.setText(text);
            });
        }
//        TextUpdateEvent event1 = new TextUpdateEvent();
//        taskHandler.submit("Generating Random Text " + new Random().nextDouble(), event1, () -> {
//            String text = textGenerator.make(2000, "1 - Should Be First");
//            event1.setText(text);
//        });
//
//        TextUpdateEvent event2 = new TextUpdateEvent();
//        taskHandler.submit("Generating Random Text " + new Random().nextDouble(), event2, () -> {
//            try {
//                Thread.sleep(30);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            String text = textGenerator.make(1000, "2 - Should Be Second");
//            event2.setText(text);
//        });

    }

    public void closeApp() {
        applicationHandler.close();
    }

}
