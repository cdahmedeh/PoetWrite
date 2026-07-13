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

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.component.PoemTextArea;
import net.cdahmedeh.poetwrite.ui.constant.UIConstants;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MainViewController;
import net.cdahmedeh.poetwrite.ui.viewmodel.MainViewModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * TODO: This is a huge mess. For whoever is reading this, please don't judge
 * TODO: Hate that Swing doesn't have some kind of Markup. Even WPF has XAML.
 */
public class MainView extends View<MainViewModel, MainViewController, JFrame> {
    private JFrame frame;
    private RSyntaxTextArea textArea;

    private String currentFile = "none";
    private PersistenceManager.FileStatus status = PersistenceManager.FileStatus.UNKNOWN;

    public MainView(MainViewModel viewModel, MainViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        initWindow();
        setupClose();
        setupEditor();
    }

    private void initWindow() {


        frame = new JFrame(UIConstants.APP_NAME);
        frame.setSize(UIConstants.DEFAULT_WINDOW_WIDTH, UIConstants.DEFAULT_WINDOW_HEIGHT);
        frame.setIconImage(new ImageIcon(getClass().getResource(UIConstants.APP_ICON_PATH)).getImage());
        frame.setLayout(new BorderLayout());
    }

    private void setupClose() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }

    private void setupEditor() {
        textArea = new PoemTextArea(20, 60);
//        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setForeground(new Color(65, 65, 65));
        textArea.setLineWrap(true);
        textArea.setFont(new Font(UIConstants.DEFAULT_EDITOR_FONT, Font.PLAIN, UIConstants.DEFAULT_EDITOR_FONT_SIZE));



        textArea.setCurrentLineHighlightColor(new Color(250, 245, 250));
        textArea.setCaretColor(new Color(80, 80, 80));

        RTextScrollPane textAresScrollPane = new RTextScrollPane(textArea);
        frame.add(textAresScrollPane, BorderLayout.CENTER);

        textAresScrollPane.putClientProperty("FlatLaf.style", "focusWidth: 0");
        textAresScrollPane.putClientProperty("FlatLaf.style",
                "focusWidth: 0; focusColor: $ScrollPane.borderColor");
        textAresScrollPane.setBorder(BorderFactory.createLineBorder(
                UIManager.getColor("Component.borderColor"), 0));

        Gutter gutter = textAresScrollPane.getGutter();
        gutter.setLineNumberFont(new Font(UIConstants.DEFAULT_EDITOR_FONT, Font.PLAIN, UIConstants.DEFAULT_EDITOR_FONT_SIZE));
    }

    @Override
    protected void listen() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                viewController.update(textArea.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                viewController.update(textArea.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (status == PersistenceManager.FileStatus.CHANGED) {
                    FlatSVGIcon exitIcon = new FlatSVGIcon(getClass().getResource(UIConstants.EXIT_ICON_PATH));
                    int confirm = JOptionPane.showConfirmDialog(frame, "Your poem has some unsaved changes. Are you sure you want to quit PoetWrite and lose your changes?", "Quit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (confirm == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

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
                });

        disposable.add(textSubscriber);



        Disposable dialogNeededSubscriber =  viewModel.streamDialogNeeded()
                .subscribe(dialogNeeded -> {
                    System.out.println("Dialog Needed Called");

                    if (dialogNeeded) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new FileNameExtensionFilter("Poem Files (*.poem)", "poem"));



                        while (true) {
                            if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
                                return;
                            }

                            File selectedFile = chooser.getSelectedFile();

                            if (selectedFile.exists()) {
                                int confirm = JOptionPane.showConfirmDialog(frame, String.format(UIConstants.MESSAGE_OVERWRITE_PROMPT, selectedFile.getName()), "Overwrite Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if (confirm == JOptionPane.NO_OPTION) {
                                    viewController.ask(selectedFile);
                                    return;
                                }
                                if (confirm == JOptionPane.CANCEL_OPTION) {
                                    return;
                                }
                                if (confirm != JOptionPane.YES_OPTION) {
                                    continue;
                                }
                            }

                            viewController.save(selectedFile);
                            return;
                        }
                    } else {
                        viewController.save();
                        return;
                    }
                });

        disposable.add(dialogNeededSubscriber);

        Disposable fileChangedDisposable = viewModel.streamFileStatus()
                .subscribe(fileChanged -> {
                    status = fileChanged;
                    String changedText = status == PersistenceManager.FileStatus.CHANGED ? " (unsaved changes)" : "";
                    System.out.println(status);
                    frame.setTitle("PoetWrite - " + currentFile + changedText);
                });
        disposable.add(fileChangedDisposable);

        Disposable fileNameDisposable = viewModel.streamFileName()
                .subscribe(fileName -> {
                    currentFile = fileName;
                    String changedText = status == PersistenceManager.FileStatus.CHANGED ? " (unsaved changes)" : "";
                    System.out.println(status);
                    frame.setTitle("PoetWrite - " + currentFile + " " + changedText);
                });
        disposable.add(fileNameDisposable);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.show());
    }
}
