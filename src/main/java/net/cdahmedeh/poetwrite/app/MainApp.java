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
import net.cdahmedeh.poetwrite.ui.*;
import net.cdahmedeh.poetwrite.ui.MainViewController;

/**
 * Once we get all the business logic stuff working, this will be the starter of
 * all the magic. I promise I won't make this complicated like starting a Spring
 * Boot app or even worse JavaFX.
 *
 * TODO: Take a deep breath Ahmed, and take it easy.
 */

public class MainApp {
    public void build() {
        FlatLightLaf.setup();

        AppComponent appComponent = DaggerAppComponent.create();
        MainViewModel mainViewModel = new MainViewModel();
        MainViewController mainViewController = appComponent.mainViewControllerFactory().create(mainViewModel);
        MainView mainView = new MainView(mainViewModel, mainViewController);

        StatusViewModel statusViewModel = new StatusViewModel();
        StatusViewController statusViewController = appComponent.statusViewControllerFactory().create(statusViewModel);
        StatusView statusView = new StatusView(statusViewModel, statusViewController);
        mainView.attach(statusView.root());

        mainView.show();
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the world of PoetWrite");

        new MainApp().build();
    }
}