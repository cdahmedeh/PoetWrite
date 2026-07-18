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
import com.formdev.flatlaf.ui.FlatLineBorder;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.annotation.Duplicated;
import net.cdahmedeh.poetwrite.lib.analysis.PatternAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.PoemSyllablesAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.ui.component.PoemGutter;
import net.cdahmedeh.poetwrite.ui.constant.*;
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.component.PoemTextArea;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MainViewController;
import net.cdahmedeh.poetwrite.ui.viewmodel.MainViewModel;
import org.fife.ui.rsyntaxtextarea.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * This is the core part of PoetWrite, the text editor.
 *
 * TODO: This is a huge mess. For whoever is reading this, please don't judge
 *       me. Views are changing a lot, so I can't tempt myself to do premature
 *       refactoring.
 *
 * TODO: Hate that Swing doesn't have some kind of Markup. Even WPF has XAML.
 */
public class MainView extends View<MainViewModel, MainViewController, JFrame> {
    private JFrame frame;
    private RSyntaxTextArea textArea;

    private String currentFile = "";
    private PersistenceManager.FileStatus status = PersistenceManager.FileStatus.UNKNOWN;
    private JScrollPane textAreaScrollPane;

    private PoemGutter poemGutter;

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


        frame = new JFrame(AppConstants.APP_NAME);
        frame.setSize(AppearanceConstants.DEFAULT_WINDOW_WIDTH, AppearanceConstants.DEFAULT_WINDOW_HEIGHT);
        frame.setIconImage(new ImageIcon(getClass().getResource(IconConstants.APP_ICON_PATH)).getImage());
        frame.setLayout(new BorderLayout());
    }

    private void setupClose() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }

    @SneakyThrows
    private void setupEditor() {
        textArea = new PoemTextArea(20, 60);
//        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setForeground(new Color(EditorConstants.TEXT_EDITOR_FONT_COLOUR, EditorConstants.TEXT_EDITOR_FONT_COLOUR, EditorConstants.TEXT_EDITOR_FONT_COLOUR));
        textArea.setLineWrap(true);
        textArea.setFont(new Font(EditorConstants.DEFAULT_EDITOR_FONT, Font.PLAIN, EditorConstants.DEFAULT_EDITOR_FONT_SIZE));

        textArea.setCurrentLineHighlightColor(EditorConstants.CURRENT_LINE_HIGHLIGHT_COLOUR);
        textArea.setCaretColor(EditorConstants.CARET_COLOR);

        // Plain JScrollPane instead of RTextScrollPane: RSTA's Gutter
        // ignores LineNumberFormatter when line wrap is on, so PoemGutter
        // paints the whole gutter (numbers + syllables) itself as the
        // scroll pane's row header.
        textAreaScrollPane = new JScrollPane(textArea);
        poemGutter = new PoemGutter(textArea);
        textAreaScrollPane.setRowHeaderView(poemGutter);
        frame.add(textAreaScrollPane, BorderLayout.CENTER);

        textAreaScrollPane.setColumnHeaderView(poemGutter.createTextHeader());
        textAreaScrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, poemGutter.createHeader());

        textAreaScrollPane.putClientProperty("FlatLaf.style", "focusWidth: 0");
        textAreaScrollPane.putClientProperty("FlatLaf.style",
                "focusWidth: 0; focusColor: $ScrollPane.borderColor");
        textAreaScrollPane.setBorder(BorderFactory.createLineBorder(
                UIManager.getColor("Component.borderColor"), 0));

        textArea.setAnimateBracketMatching(false);
        textArea.setBracketMatchingEnabled(false);

        textAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        setupSyntaxHighlighting();
    }

    private void setupSyntaxHighlighting() {
        AbstractTokenMakerFactory factory =
                (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        factory.putMapping("text/poem", "net.cdahmedeh.poetwrite.ui.syntax.PoemTokenMaker");

        Font base = textArea.getFont();
        SyntaxScheme scheme = textArea.getSyntaxScheme();
        Style note = scheme.getStyle(Token.COMMENT_MULTILINE);
        note.foreground = EditorConstants.SYNTAX_NOTE_COLOUR ;
        Map<TextAttribute, Object> attrs = new HashMap<>(base.getAttributes());
        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        note.font = base.deriveFont(attrs);

        scheme.getStyle(Token.OPERATOR).foreground = EditorConstants.SYNTAX_PUNCTUATION_COLOUR;

        Style aside = scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE);
        aside.foreground = EditorConstants.SYNTAX_ASIDE_COLOUR;

        scheme.getStyle(Token.SEPARATOR).foreground = EditorConstants.SYNTAX_BRACKET_COLOUR;

        textArea.repaint();
        textArea.revalidate();
        textArea.repaint();

        textArea.setSyntaxEditingStyle("text/poem");
    }

    private void setupGutter(PoemSyllablesAnalysis analysis) {
        List<Integer> counts = new ArrayList<>();
        for (Integer syllableCount: analysis.getSyllables()) {
            counts.add(syllableCount);
        }
        // Poem events arrive on TaskBus threads; PoemGutter is a Swing
        // component, so hop to the EDT.
        SwingUtilities.invokeLater(() -> poemGutter.setSyllableCounts(counts));
    }

    private void setupGutter(PatternAnalysis patternAnalysis) {
        List<String> pattern = new ArrayList<>(patternAnalysis.getPattern());

        // Poem events arrive on TaskBus threads; PoemGutter is a Swing
        // component, so hop to the EDT.
        SwingUtilities.invokeLater(() -> poemGutter.setPattern(pattern));
    }

    private void setupHover(NavigableMap<Integer, Word> index) {
        textArea.setUseFocusableTips(false);
        // Colour + rounded border (arc = 8 rounds the corners of the border paint)
        UIManager.put("ToolTip.background", new Color(0xFAF8F4));   // warm off-white; tune to your palette
        UIManager.put("ToolTip.foreground", new Color(0x3A3A3A));
        UIManager.put("ToolTip.border", new FlatLineBorder(
                new Insets(6, 10, 6, 10), new Color(0xD6D2C9), 1, 8));

        // Noto Sans
        UIManager.put("ToolTip.font", new Font("Noto Sans", Font.PLAIN, 13));

        // Native rounded window corners + OS drop shadow (Windows 11 / macOS)
        UIManager.put("Popup.borderCornerRadius", 8);
        UIManager.put("Popup.forceHeavyWeight", true);

        textArea.setToolTipSupplier((rsta, e) -> {
            int offset = rsta.viewToModel2D(e.getPoint());
            Map.Entry<Integer, Word> entry = index.floorEntry(offset);
            if (entry == null || !entry.getValue().contains(offset)) {
                ((PoemTextArea) rsta).setHoveredWord(null);
                return null;
            }
            Word w = entry.getValue();
            ((PoemTextArea) rsta).setHoveredWord(w);
            Font ef = rsta.getFont();
            return "<html><b>" + w.getWord() + "</b><br>Random stuff for now</html>";
        });
        ToolTipManager.sharedInstance().registerComponent(textArea);

        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(true);
    }

    @Override
    protected void listen() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                viewController.update(textArea.getText());
                viewController.parse(textArea.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                viewController.update(textArea.getText());
                viewController.parse(textArea.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (status == PersistenceManager.FileStatus.CHANGED) {
                    FlatSVGIcon exitIcon = new FlatSVGIcon(getClass().getResource(IconConstants.EXIT_ICON_PATH));
                    int confirm = JOptionPane.showConfirmDialog(frame, PromptConstants.PROMPT_UNSAVED_CHANGED_FOR_QUIT, PromptConstants.TITLE_QUIT, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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

    @Duplicated("MenuView")
    @Override
    protected void subscribe(CompositeDisposable disposable) {
        Disposable textSubscriber = viewModel.editorContent()
                .subscribe(text -> {
                    SwingUtilities.invokeLater(() -> textArea.setText(text));
                });

        disposable.add(textSubscriber);

        Disposable poemSubscriber = viewModel.poem().subscribe(
                poem -> {
                    viewController.analyzeSyllables(poem);
                    viewController.analyzePattern(poem);
                    viewController.indexPoem(poem);
                }
        );
        disposable.add(poemSubscriber);

        Disposable poemSyllableAnalysisSubscriber = viewModel.poemSyllablesAnalysis().subscribe(
                poemSyllableAnalysis -> {
                    setupGutter(poemSyllableAnalysis);
                }
        );
        disposable.add(poemSyllableAnalysisSubscriber);

        Disposable patternAnalysisSubscriber = viewModel.patternAnalysis().subscribe(
                patternAnalysis -> {
                    setupGutter(patternAnalysis);
                }
        );
        disposable.add(patternAnalysisSubscriber);

        Disposable poemIndexSubscriber = viewModel.poemIndex().subscribe(
                poemIndex -> {
                    setupHover(poemIndex);
                }
               );
        disposable.add(poemIndexSubscriber);

        Disposable dialogNeededSubscriber =  viewModel.dialogNeeded()
                .subscribe(dialogNeeded -> {
                    requestSave(dialogNeeded);
                });

        disposable.add(dialogNeededSubscriber);

        Disposable fileChangedDisposable = viewModel.fileStatus()
                .subscribe(fileChanged -> {
                    status = fileChanged;
                    String changedText = status == PersistenceManager.FileStatus.CHANGED ? " (unsaved changes)" : "";
                    frame.setTitle(AppConstants.APP_NAME+  " - " + currentFile + changedText);
                    if (currentFile == "") {
                        frame.setTitle(AppConstants.APP_NAME);
                    }
                });
        disposable.add(fileChangedDisposable);

        Disposable fileNameDisposable = viewModel.fileName()
                .subscribe(fileName -> {
                    currentFile = fileName;
                    String changedText = status == PersistenceManager.FileStatus.CHANGED ? " (unsaved changes)" : "";
                    frame.setTitle(AppConstants.APP_NAME+  " - " + currentFile + changedText);
                    if (currentFile == "") {
                        frame.setTitle(AppConstants.APP_NAME);
                    }
                });
        disposable.add(fileNameDisposable);
    }



    private void requestSave(Boolean dialogNeeded) {
        if (dialogNeeded) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Poem Files (*.poem)", "poem"));

            while (true) {
                if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
                    return;
                }

                File selectedFile = chooser.getSelectedFile();

                if (selectedFile.exists()) {
                    int confirm = JOptionPane.showConfirmDialog(frame, String.format(PromptConstants.PROMPT_MESSAGE_OVERWRITE, selectedFile.getName()), PromptConstants.TITLE_OVERWRITE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.show());
    }
}
