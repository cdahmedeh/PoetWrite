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

package net.cdahmedeh.poetwrite.app;

import dagger.Component;
import net.cdahmedeh.poetwrite.async.AsynchronousTaskHandler;
import net.cdahmedeh.poetwrite.controller.MainViewController;
import net.cdahmedeh.poetwrite.controller.MenuViewController;
import net.cdahmedeh.poetwrite.controller.StatusViewController;
import net.cdahmedeh.poetwrite.model.MainViewModel;
import net.cdahmedeh.poetwrite.model.MenuViewModel;
import net.cdahmedeh.poetwrite.model.StatusViewModel;

import javax.inject.Singleton;

@Singleton
@Component
public interface AppComponent {
    AsynchronousTaskHandler taskHandler();

    MainViewModel.MainViewModelFactory mainViewModelFactory();
    MainViewController.MainViewControllerFactory mainViewControllerFactory();

    StatusViewModel.StatusViewModelFactory statusViewModelFactory();
    StatusViewController.StatusViewControllerFactory statusViewControllerFactory();

    MenuViewModel.MenuViewModelFactory menuViewModelFactory();
    MenuViewController.MenuViewControllerFactory menuViewControllerFactory();
}