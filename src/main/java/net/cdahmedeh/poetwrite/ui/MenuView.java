package net.cdahmedeh.poetwrite.ui;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.swing.*;

public class MenuView extends View<MenuViewModel, MenuViewController, JMenuBar> {
    private JMenuItem generateRandomText;


    public MenuView(MenuViewModel viewModel, MenuViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        JMenuBar mb = new JMenuBar();
        JMenu help;

        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Exit"));

        help = new JMenu("Tools");
        generateRandomText = new JMenuItem("Generate Random Text");
        help.add(generateRandomText);

        mb.add(file);
        mb.add(help);
    }

    @Override
    public JMenuBar root() {
        return null;
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {

    }
}
