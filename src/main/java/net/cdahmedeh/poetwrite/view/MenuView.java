package net.cdahmedeh.poetwrite.view;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import net.cdahmedeh.poetwrite.controller.MenuViewController;
import net.cdahmedeh.poetwrite.model.MenuViewModel;

import javax.swing.*;

public class MenuView extends View<MenuViewModel, MenuViewController, JMenuBar> {
    private JMenuItem generateRandomText;
    private JMenuBar mb;


    public MenuView(MenuViewModel viewModel, MenuViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        mb = new JMenuBar();
        JMenu help;

        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Exit"));

        help = new JMenu("Tools");
        generateRandomText = new JMenuItem("Generate Random Text");
        help.add(generateRandomText);

        mb.add(file);
        mb.add(help);

        generateRandomText.addActionListener(e -> viewController.generateRandomText());
    }

    @Override
    public JMenuBar root() {
        return mb;
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {

    }
}
