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

package net.cdahmedeh.poetwrite.ui.app;

import com.formdev.flatlaf.FlatLightLaf;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MenuViewController;
import net.cdahmedeh.poetwrite.ui.viewcontroller.StatusViewController;
import net.cdahmedeh.poetwrite.ui.event.ServiceStartingEvent;
import net.cdahmedeh.poetwrite.ui.viewmodel.MainViewModel;
import net.cdahmedeh.poetwrite.ui.viewmodel.MenuViewModel;
import net.cdahmedeh.poetwrite.ui.viewmodel.StatusViewModel;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MainViewController;
import net.cdahmedeh.poetwrite.ui.view.MainView;
import net.cdahmedeh.poetwrite.ui.view.MenuView;
import net.cdahmedeh.poetwrite.ui.view.StatusView;

import javax.swing.*;
import java.awt.*;

/**
 * Once we get all the business logic stuff working, this will be the starter of
 * all the magic. I promise I won't make this complicated like starting a Spring
 * Boot app or even worse JavaFX.
 *
 * TODO: Take a deep breath Ahmed, and take it easy.
 * TODO: I'm still holding it.
 * TODO: The generation of the MVVM stuff is getting quite ugly. Dagger really
 *       makes dependency injection a nightmare if there's some cyclical
 *       dependencies.
 * TODO: In the future logging will probably go here.
 *
 */

public class MainApp {
    public void build() {
        AppComponent appComponent = DaggerAppComponent.create();

        TaskBus asynchronousTaskHandler = appComponent.taskBus();
        asynchronousTaskHandler.submit("Welcome to PoetWrite!", new ServiceStartingEvent(), () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        setupLookAndFeel();

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

    private static void setupLookAndFeel() {
        FlatLightLaf.setup();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        UIManager.put("TitlePane.buttonSize", new Dimension(44, 34));
        UIManager.put("MenuItem.margin", new Insets(6, 8, 6, 8));
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the world of PoetWrite");

        new MainApp().build();
    }
}