/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
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

package net.cdahmedeh.poetwrite.app;

import com.formdev.flatlaf.FlatLightLaf;
import net.cdahmedeh.poetwrite.async.AsynchronousTaskHandler;
import net.cdahmedeh.poetwrite.controller.MenuViewController;
import net.cdahmedeh.poetwrite.controller.StatusViewController;
import net.cdahmedeh.poetwrite.event.ServiceStartingEvent;
import net.cdahmedeh.poetwrite.model.MainViewModel;
import net.cdahmedeh.poetwrite.model.MenuViewModel;
import net.cdahmedeh.poetwrite.model.StatusViewModel;
import net.cdahmedeh.poetwrite.ui.*;
import net.cdahmedeh.poetwrite.controller.MainViewController;
import net.cdahmedeh.poetwrite.view.MainView;
import net.cdahmedeh.poetwrite.view.MenuView;
import net.cdahmedeh.poetwrite.view.StatusView;

import javax.swing.*;

/**
 * Once we get all the business logic stuff working, this will be the starter of
 * all the magic. I promise I won't make this complicated like starting a Spring
 * Boot app or even worse JavaFX.
 *
 * TODO: Take a deep breath Ahmed, and take it easy.
 */

public class MainApp {
    public void build() {
        AppComponent appComponent = DaggerAppComponent.create();
//        AsynchronousTaskHandler asynchronousTaskHandler = appComponent.taskHandler();
//        asynchronousTaskHandler.submit("Welcome to PoetWrite", () -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });

        AsynchronousTaskHandler asynchronousTaskHandler = appComponent.taskHandler();
        asynchronousTaskHandler.submit("Welcome to PoetWrite!", new ServiceStartingEvent(), () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        FlatLightLaf.setup();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        MainViewModel mainViewModel = appComponent.mainViewModelFactory().create();
        MainViewController mainViewController = appComponent.mainViewControllerFactory().create(mainViewModel);
        MainView mainView = new MainView(mainViewModel, mainViewController);

        StatusViewModel statusViewModel = appComponent.statusViewModelFactory().create();
        StatusViewController statusViewController = appComponent.statusViewControllerFactory().create(statusViewModel);
        StatusView statusView = new StatusView(statusViewModel, statusViewController);
        mainView.attachStatusBar(statusView.root());

        MenuViewModel menuViewModel = appComponent.menuViewModelFactory().create();
        MenuViewController menuViewController = appComponent.menuViewControllerFactory().create(menuViewModel);
        MenuView menuView = new MenuView(menuViewModel, menuViewController);
        mainView.attachMenu(menuView.root());

        mainView.show();
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the world of PoetWrite");

        new MainApp().build();
    }
}