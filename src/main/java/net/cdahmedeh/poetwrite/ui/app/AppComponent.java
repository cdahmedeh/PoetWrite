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

package net.cdahmedeh.poetwrite.ui.app;

import dagger.Component;
import net.cdahmedeh.poetwrite.ui.services.ApplicationHandler;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MainViewController;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MenuViewController;
import net.cdahmedeh.poetwrite.ui.viewcontroller.StatusViewController;
import net.cdahmedeh.poetwrite.ui.viewmodel.MainViewModel;
import net.cdahmedeh.poetwrite.ui.viewmodel.MenuViewModel;
import net.cdahmedeh.poetwrite.ui.viewmodel.StatusViewModel;

import javax.inject.Singleton;

@Singleton
@Component
public interface AppComponent {
    ApplicationHandler applicationHandler();

    MainViewModel.MainViewModelFactory mainViewModelFactory();
    MainViewController.MainViewControllerFactory mainViewControllerFactory();

    StatusViewModel.StatusViewModelFactory statusViewModelFactory();
    StatusViewController.StatusViewControllerFactory statusViewControllerFactory();

    MenuViewModel.MenuViewModelFactory menuViewModelFactory();
    MenuViewController.MenuViewControllerFactory menuViewControllerFactory();
}