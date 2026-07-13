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

import com.formdev.flatlaf.FlatLightLaf;
import net.cdahmedeh.poetwrite.ui.constant.AppearanceConstants;
import net.cdahmedeh.poetwrite.ui.constant.LogConstants;
import net.cdahmedeh.poetwrite.ui.services.ApplicationHandler;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MenuViewController;
import net.cdahmedeh.poetwrite.ui.viewcontroller.StatusViewController;
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

 * TODO: In the future logging will probably go here.
 *
 * TODO:
 *
 * NOTE: The generation of the MVVM stuff is getting quite ugly. I have to do
 *       things like, if for example, all the views will have a reference to
 *       TaskBus. There's an @AssistedInject annotation in the injected services
 *       that have these implicit imports where you can see this being done.
 *       So AppComponent is the kind of kludge we needed. As I just want to have
 *       to pass on the MVVM pieces to each other.
 */

public class MainApp {
    public static void main(String[] args) {
        System.out.println(LogConstants.LOG_WELCOME);

        new MainApp().build();
    }

    /**
     * Bringing up the UI. Right now it:
     * - Gets the application handler to load first.
     * - Does the UI LaF stuff.
     * - Setups views and their VC and VM.
     *
     * TODO: Make setupViews(..) async.
     * TODO: Consider moving AppComponent to a field. Instead of passing it to
     *       setupViews.
     */
    public void build() {
        AppComponent appComponent = DaggerAppComponent.create();

        ApplicationHandler applicationHandler = appComponent.applicationHandler();
        applicationHandler.sendWelcomeMessage();

        setupLookAndFeel();

        eetupViews(appComponent);
    }

    /**
     * Everything that involves instantiating the views and their viewmodels
     * and viewcontrollers. Uses AppComponents so that we only need to worry
     * about injecting the models/controllers.
     *
     * As remember, things like TaskBus, well, I don't want them to be created
     * here.
     */
    private static void eetupViews(AppComponent appComponent) {
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

    /**
     * Application appearance.
     */
    private static void setupLookAndFeel() {
        // Using the FlatLaf look and feel. Much prettier compared to the
        // default Swing theme, or its native emulation.
        //
        // TODO: Maybe in the future, we could have OS-specific themes or even
        //       a theme selector.
        FlatLightLaf.setup();

        // Use client rendering for the title bar. Allows the menu to be in
        // there for a more minimalist look.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        // The titlebar is just way too thin by default. Makes it taller.
        UIManager.put("TitlePane.buttonSize",
                new Dimension(
                        AppearanceConstants.UI_TITLE_PANE_BUTTON_SIZE_WIDTH, AppearanceConstants.UI_TITLE_PANE_BUTTON_SIZE_HEIGHT));

        // Menu items are also too small. This adds some panning. Keep in mind
        // this doesn't change the icon size of the menu items.
        UIManager.put("MenuItem.margin",
                new Insets(
                        AppearanceConstants.UI_MENU_ITEM_MARGIN_VERTICAL, AppearanceConstants.UI_MENU_ITEM_MARGIN_HORIZONTAL,
                        AppearanceConstants.UI_MENU_ITEM_MARGIN_VERTICAL, AppearanceConstants.UI_MENU_ITEM_MARGIN_HORIZONTAL));
    }


}