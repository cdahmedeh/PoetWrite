package net.cdahmedeh.poetwrite.ui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import net.cdahmedeh.poetwrite.ui.constant.UIConstants;
import net.cdahmedeh.poetwrite.ui.controller.MenuViewController;
import net.cdahmedeh.poetwrite.ui.model.MenuViewModel;

import javax.swing.*;

/**
 * TODO: This is a huge mess. For whoever is reading this, please don't judge
 * TODO: Hate that Swing doesn't have some kind of Markup. Even WPF has XAML.
 */
public class MenuView extends View<MenuViewModel, MenuViewController, JMenuBar> {
    private JMenuItem generateRandomTextMenuItem;
    private JMenuBar menuBar;

    private JMenuItem exitMenuItem;

    public MenuView(MenuViewModel viewModel, MenuViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        menuBar = new JMenuBar();

        setupFileMenu();
        setupToolsMenu();
    }

    private void setupFileMenu() {
        JMenu fileMenu = new JMenu(UIConstants.STRING_FILE);

        exitMenuItem = new JMenuItem(UIConstants.STRING_EXIT);
        fileMenu.add(exitMenuItem);

        FlatSVGIcon exitIcon = new FlatSVGIcon(getClass().getResource(UIConstants.EXIT_ICON_PATH));
        fileMenu.add(exitMenuItem);

        exitMenuItem.setIcon(exitIcon);

        menuBar.add(fileMenu);
    }

    private void setupToolsMenu() {
        JMenu toolsMenu;

        toolsMenu = new JMenu(UIConstants.STRING_TOOLS);
        generateRandomTextMenuItem = new JMenuItem(UIConstants.STRING_GENERATE);
        toolsMenu.add(generateRandomTextMenuItem);

        FlatSVGIcon generateRandomTextIcon = new FlatSVGIcon(getClass().getResource(UIConstants.GENERATE_ICON_PATH));
        generateRandomTextMenuItem.setIcon(generateRandomTextIcon);

        menuBar.add(toolsMenu);
    }

    @Override
    protected void listen() {
        exitMenuItem.addActionListener(e -> viewController.closeApp());
        generateRandomTextMenuItem.addActionListener(e -> viewController.generateRandomText());
    }

    @Override
    public JMenuBar root() {
        return menuBar;
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {

    }
}
