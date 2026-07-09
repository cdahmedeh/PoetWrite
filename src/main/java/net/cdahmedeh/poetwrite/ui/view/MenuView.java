package net.cdahmedeh.poetwrite.ui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import net.cdahmedeh.poetwrite.ui.constant.UIConstants;
import net.cdahmedeh.poetwrite.ui.controller.MenuViewController;
import net.cdahmedeh.poetwrite.ui.model.MenuViewModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * TODO: This is a huge mess. For whoever is reading this, please don't judge
 * TODO: Hate that Swing doesn't have some kind of Markup. Even WPF has XAML.
 */
public class MenuView extends View<MenuViewModel, MenuViewController, JMenuBar> {
    private JMenuBar menuBar;

    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem saveAsMenuItem;

    private JMenuItem exitMenuItem;

    private JMenuItem generateRandomTextMenuItem;
    private File selectedFile;

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

        // New
        newMenuItem = new JMenuItem(UIConstants.STRING_NEW);
        fileMenu.add(newMenuItem);

        FlatSVGIcon newIcon = new FlatSVGIcon(getClass().getResource(UIConstants.NEW_ICON_PATH));
        fileMenu.add(newMenuItem);

        newMenuItem.setIcon(newIcon);

        // Open
        openMenuItem = new JMenuItem(UIConstants.STRING_OPEN);
        fileMenu.add(openMenuItem);

        FlatSVGIcon openIcon = new FlatSVGIcon(getClass().getResource(UIConstants.OPEN_ICON_PATH));
        fileMenu.add(openMenuItem);

        openMenuItem.setIcon(openIcon);

        // Save
        saveMenuItem = new JMenuItem(UIConstants.STRING_SAVE);
        fileMenu.add(saveMenuItem);

        FlatSVGIcon saveIcon = new FlatSVGIcon(getClass().getResource(UIConstants.SAVE_ICON_PATH));
        fileMenu.add(saveMenuItem);

        saveMenuItem.setIcon(saveIcon);

        // Save As
        saveAsMenuItem = new JMenuItem(UIConstants.STRING_SAVE_AS);
        fileMenu.add(saveAsMenuItem);

        FlatSVGIcon saveAsIcon = new FlatSVGIcon(getClass().getResource(UIConstants.SAVE_AS_ICON_PATH));
        fileMenu.add(saveAsMenuItem);

        saveAsMenuItem.setIcon(saveAsIcon);

        // Separator
        fileMenu.addSeparator();

        // Exit
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
        FileNameExtensionFilter poemFilter = new FileNameExtensionFilter("Poem Files (*.poem)", "poem");

        newMenuItem.addActionListener(e -> viewController.create());

        openMenuItem.addActionListener(e-> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(poemFilter);
            if (chooser.showOpenDialog(menuBar) == JFileChooser.APPROVE_OPTION) {
                viewController.open(chooser.getSelectedFile());
            }
        });

        saveMenuItem.addActionListener(e -> viewController.save());

        saveAsMenuItem.addActionListener(e-> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(poemFilter);
            while (true) {
                if (chooser.showSaveDialog(menuBar) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File selectedFile = chooser.getSelectedFile();

                if (selectedFile.exists()) {
                    int confirm = JOptionPane.showConfirmDialog(menuBar, UIConstants.MESSAGE_OVERWRITE_PROMPT);
                    if (confirm == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    if (confirm != JOptionPane.YES_OPTION) {
                        continue;
                    }
                }

                viewController.saveAs(selectedFile);
                return;
            }
        });

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
