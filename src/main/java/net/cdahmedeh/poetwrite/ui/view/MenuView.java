package net.cdahmedeh.poetwrite.ui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import net.cdahmedeh.poetwrite.annotation.Duplicated;
import net.cdahmedeh.poetwrite.ui.constant.UIConstants;
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MenuViewController;
import net.cdahmedeh.poetwrite.ui.viewmodel.MenuViewModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * This is the main menu bar which has menu actions, and displays the
 * application name along with the open file and changed status.
 *
 * It attaches to the MainView
 *
 * TODO: This is a huge mess. For whoever is reading this, please don't judge
 *       me. Views are changing a lot, so I can't tempt myself to do premature
 *       refactoring.
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

    // If the user needs to get a prompt if they want to lose their changes.
    private boolean confirmationNeeded;

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

        FlatSVGIcon fileMenuIcon = new FlatSVGIcon(getClass().getResource(UIConstants.FILE_ICON_PATH));
        fileMenu.setIcon(fileMenuIcon);

        // New
        newMenuItem = new JMenuItem(UIConstants.STRING_NEW);
        fileMenu.add(newMenuItem);

        FlatSVGIcon newIcon = new FlatSVGIcon(getClass().getResource(UIConstants.NEW_ICON_PATH));
        fileMenu.add(newMenuItem);

        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        newMenuItem.setIcon(newIcon);

        // Open
        openMenuItem = new JMenuItem(UIConstants.STRING_OPEN);
        fileMenu.add(openMenuItem);

        FlatSVGIcon openIcon = new FlatSVGIcon(getClass().getResource(UIConstants.OPEN_ICON_PATH));
        fileMenu.add(openMenuItem);

        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        openMenuItem.setIcon(openIcon);

        // Save
        saveMenuItem = new JMenuItem(UIConstants.STRING_SAVE);
        fileMenu.add(saveMenuItem);

        FlatSVGIcon saveIcon = new FlatSVGIcon(getClass().getResource(UIConstants.SAVE_ICON_PATH));
        fileMenu.add(saveMenuItem);

        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        saveMenuItem.setIcon(saveIcon);

        // Save As
        saveAsMenuItem = new JMenuItem(UIConstants.STRING_SAVE_AS);
        fileMenu.add(saveAsMenuItem);

        FlatSVGIcon saveAsIcon = new FlatSVGIcon(getClass().getResource(UIConstants.SAVE_AS_ICON_PATH));
        fileMenu.add(saveAsMenuItem);

        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK));

        saveAsMenuItem.setIcon(saveAsIcon);

        // Separator
        fileMenu.addSeparator();

        // Exit
        exitMenuItem = new JMenuItem(UIConstants.STRING_EXIT);
        fileMenu.add(exitMenuItem);

        FlatSVGIcon exitIcon = new FlatSVGIcon(getClass().getResource(UIConstants.EXIT_ICON_PATH));
        fileMenu.add(exitMenuItem);

        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.ALT_DOWN_MASK));

        exitMenuItem.setIcon(exitIcon);

        menuBar.add(fileMenu);
    }

    private void setupToolsMenu() {
        JMenu toolsMenu = new JMenu(UIConstants.STRING_TOOLS);

        FlatSVGIcon toolsMenuIcon = new FlatSVGIcon(getClass().getResource(UIConstants.TOOLS_ICON_PATH));
        toolsMenu.setIcon(toolsMenuIcon);

        generateRandomTextMenuItem = new JMenuItem(UIConstants.STRING_GENERATE);
        toolsMenu.add(generateRandomTextMenuItem);

        generateRandomTextMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.ALT_DOWN_MASK));

        FlatSVGIcon generateRandomTextIcon = new FlatSVGIcon(getClass().getResource(UIConstants.GENERATE_ICON_PATH));
        generateRandomTextMenuItem.setIcon(generateRandomTextIcon);

        menuBar.add(toolsMenu);
    }

    @Duplicated("MainView")
    @Override
    protected void listen() {
        FileNameExtensionFilter poemFilter = new FileNameExtensionFilter("Poem Files (*.poem)", PersistenceManager.DEFAULT_FILE_EXTENSION);

        newMenuItem.addActionListener(e -> {
            if (confirmationNeeded) {
                int confirm = JOptionPane.showConfirmDialog(menuBar.getParent(), UIConstants.PROMPT_UNSAVED_CHANGES_FOR_NEW, UIConstants.UNSAVED_CHANGES, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            viewController.create();
        });

        openMenuItem.addActionListener(e-> {
            if (confirmationNeeded) {
                int confirm = JOptionPane.showConfirmDialog(menuBar.getParent(), UIConstants.PROMPT_UNSAVED_CHANGED_FOR_OPEN, UIConstants.UNSAVED_CHANGES, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(poemFilter);
            if (chooser.showOpenDialog(menuBar) == JFileChooser.APPROVE_OPTION) {
                viewController.open(chooser.getSelectedFile());
            }
        });

        saveMenuItem.addActionListener(e -> viewController.save());

        saveAsMenuItem.addActionListener(e-> {
                viewController.saveAs();
        });

        exitMenuItem.addActionListener(e -> viewController.closeApp());

//        generateRandomTextMenuItem.addActionListener(e -> viewController.generateRandomText());
    }



    @Override
    protected void subscribe(CompositeDisposable disposable) {
        Disposable confirmationNeededSubscriber = viewModel.confirmationNeeded()
                .subscribe(confirmation -> {
            confirmationNeeded = confirmation;
        });

        disposable.add(confirmationNeededSubscriber);
    }

    @Override
    public JMenuBar root() {
        return menuBar;
    }
}
