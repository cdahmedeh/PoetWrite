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

package net.cdahmedeh.poetwrite.ui.view;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import net.cdahmedeh.poetwrite.ui.controller.MainViewController;
import net.cdahmedeh.poetwrite.ui.model.MainViewModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * TODO: This is a huge mess. For whoever is reading this, please don't judge
 *       me.
 * TODO: Fix Mess
 * TODO: Hate that Swing doesn't have some kind of Markup. Even WPF has XAML.
 */
public class MainView extends View<MainViewModel, MainViewController, JFrame> {
    private static JMenu help;
    private static JMenuItem generateRandomText;
    private JFrame frame;
    private JTextField textAreaField;
    private JButton generateRandomTextButton;
    private RSyntaxTextArea textArea;

    public MainView(MainViewModel viewModel, MainViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        frame = new JFrame("PoetWrite");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1280, 800);

        frame.setIconImage(new ImageIcon(getClass().getResource("/icons/appicon.png")).getImage());

        frame.setLayout(new BorderLayout());
//        textAreaField = new JTextField();
//        frame.add(textAreaField, BorderLayout.CENTER);
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Aptos", Font.PLAIN, 15));
        RTextScrollPane sp = new RTextScrollPane(textArea);
        frame.add(sp, BorderLayout.CENTER);

//        generateRandomTextButton = new JButton("Generate Random Text");
//        frame.add(generateRandomTextButton, BorderLayout.NORTH);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                viewController.closeApp();
            }
        });


    }

    public void attachStatusBar(JPanel pane) {
        frame.add(pane, BorderLayout.SOUTH);
    }

    public void attachMenu(JMenuBar menuBar) {
        frame.setJMenuBar(menuBar);
    }

    @Override
    public JFrame root() {
        return frame;
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {
        Disposable textSubscriber = viewModel.streamText()
                .subscribe(text -> {
                    SwingUtilities.invokeLater(() -> textArea.setText(text));
//                    textArea.setText(text);
                });

        disposable.add(textSubscriber);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.show());
    }

}
