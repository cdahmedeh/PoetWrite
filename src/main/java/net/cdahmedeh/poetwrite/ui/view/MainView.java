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

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.ui.FlatNativeWindowsLibrary;
import com.formdev.flatlaf.util.SystemInfo;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.annotation.Helped;
import net.cdahmedeh.poetwrite.annotation.Draft;
import net.cdahmedeh.poetwrite.annotation.Duplicated;
import net.cdahmedeh.poetwrite.lib.analysis.PatternAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.PoemSyllablesAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.ui.component.*;
import net.cdahmedeh.poetwrite.ui.constant.*;
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.viewcontroller.MainViewController;
import net.cdahmedeh.poetwrite.ui.viewmodel.MainViewModel;
import org.fife.ui.rsyntaxtextarea.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
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
 *
 * TODO: So much have to go through the Swing EDT. Right now, it's running on
 *       whatever thread happens to call it. Usually the TaskBus. To be honest,
 *       it's not something I typically cared about.
 */
public class MainView extends View<MainViewModel, MainViewController, JFrame> {
    // File status for displaying saved status in the title bar. Eventually
    // comes from the PersistentManager.
    private PersistenceManager.FileStatus status = PersistenceManager.FileStatus.UNKNOWN;
    // Name of the file for display in the title bar. Only the name of the file.
    // Not absolute path.
    private String currentFile = "";

    // The window itself
    // TODO: Consider moving out of this view. Will be important when we start
    //       implementation multiple file editing.
    private JFrame frame;

    // The auto-complete wizard.
    @Helped("Claude - PoetWrite autocomplete wizard implementation")
    private JWindow wizardWindow;
    private QueryWizard wizard;

    // Where the poem is actually written. Including gutter.
    private PoemTextArea textArea;
    private JScrollPane textAreaScrollPane; // Text areas are always wrapped in Swing.
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

    // Where the window is actually created. Shortly after, the components
    // get put in.
    // TODO: To speed up start-up time, the window will be displayed as soon
    //       as it's done.
    private void initWindow() {
        frame = new JFrame(AppConstants.APP_NAME);
        frame.setSize(AppearanceConstants.DEFAULT_WINDOW_WIDTH, AppearanceConstants.DEFAULT_WINDOW_HEIGHT);
        frame.setIconImage(new ImageIcon(getClass().getResource(IconConstants.APP_ICON_PATH)).getImage());
        frame.setLayout(new BorderLayout());
    }

    // The close button is setup to do nothing. A seperate call on the button
    // listener actually does the work of calling the controller to start the
    // close operation. Which prompts the user to save their file if needed
    // and waits for all background tasks to be done
    private void setupClose() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    // Where the poem editor is actually setup.
    // Includes setting up the gutter, syntax highlighting, and some appearance
    // related stuff.
    @SneakyThrows
    private void setupEditor() {
        // For some reason, RSyntaxTextArea needs rows and columns setup, even
        // thought the actual size is determined by the size of the component
        // itself.
        textArea = new PoemTextArea(EditorConstants.DEFAULT_TEXT_AREA_ROWS, EditorConstants.DEFAULT_TEXT_AREA_COLUMNS);

        // Editor features like line wrap.
        // TODO: Code folding could be used to collapse stanzas in the future.
        textArea.setCodeFoldingEnabled(true);
        textArea.setLineWrap(true);

        // Sets the font colours.
        textArea.setForeground(new Color(EditorConstants.TEXT_EDITOR_FONT_COLOUR, EditorConstants.TEXT_EDITOR_FONT_COLOUR, EditorConstants.TEXT_EDITOR_FONT_COLOUR));
        textArea.setFont(new Font(EditorConstants.DEFAULT_EDITOR_FONT, Font.PLAIN, EditorConstants.DEFAULT_EDITOR_FONT_SIZE));

        // For current line highlight
        textArea.setCurrentLineHighlightColor(EditorConstants.CURRENT_LINE_HIGHLIGHT_COLOUR);
        textArea.setCaretColor(EditorConstants.CARET_COLOR);

        // Wrapping text area as usually done in Swing.
        textAreaScrollPane = new JScrollPane(textArea);
        frame.add(textAreaScrollPane, BorderLayout.CENTER);

        // The Poem Gutter is a custom component that allows display of
        // syllable count and rhyming pattern group.
        poemGutter = new PoemGutter(textArea);
        textAreaScrollPane.setColumnHeaderView(poemGutter.createTextHeader());
        textAreaScrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, poemGutter.createHeader());
        textAreaScrollPane.setRowHeaderView(poemGutter);

        // Some styling tweaks to get rid of the borders
        textAreaScrollPane.putClientProperty("FlatLaf.style", "focusWidth: 0");
        textAreaScrollPane.putClientProperty("FlatLaf.style",
                "focusWidth: 0; focusColor: $ScrollPane.borderColor");
        textAreaScrollPane.setBorder(BorderFactory.createLineBorder(
                UIManager.getColor("Component.borderColor"), 0));
        textAreaScrollPane.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, EditorConstants.GUTTER_DIVIDER_COLOUR));

        // We're not writing code here, so bracket pairs shouldn't be
        // highlighted.
        textArea.setAnimateBracketMatching(false);
        textArea.setBracketMatchingEnabled(false);

        // Make the scrollbars only appear if text doesn't fit. Just for a
        // cleaner look.
        textAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        setupSyntaxHighlighting();
    }

    // Setting up the syntax highlighting.
    // Includes font, colouring of fonts, background appearance.
    private void setupSyntaxHighlighting() {
        // Load up the syntax highlighter tokenizer PoemTokenMaker
        // Based on the ANTLR grammar, but a bit more forgiving.
        AbstractTokenMakerFactory factory =
                (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        factory.putMapping("text/poem", "net.cdahmedeh.poetwrite.ui.syntax.PoemTokenMaker");
        textArea.setSyntaxEditingStyle("text/poem");

        // Setup syntax highlighting colouring.
        SyntaxScheme scheme = textArea.getSyntaxScheme();
        scheme.getStyle(Token.OPERATOR).foreground = EditorConstants.SYNTAX_PUNCTUATION_COLOUR;
        scheme.getStyle(Token.SEPARATOR).foreground = EditorConstants.SYNTAX_BRACKET_COLOUR;
        Style note = scheme.getStyle(Token.COMMENT_MULTILINE);
        note.foreground = EditorConstants.SYNTAX_NOTE_COLOUR ;
        Style aside = scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE);
        aside.foreground = EditorConstants.SYNTAX_ASIDE_COLOUR;

        // Setup font weights
        Font base = textArea.getFont();
        Map<TextAttribute, Object> attrs = new HashMap<>(base.getAttributes());
        attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD);
        note.font = base.deriveFont(attrs);

        // Force repaint to display syntax-highlighting scheme.
        textArea.repaint();
        textArea.revalidate();
        textArea.repaint();
    }

    // Prepare the gutter. This is where the syllable counts and pattern groups
    // are shown. Called everytime the text changes.
    // TODO: No debouncing yet.

    // Syllables counts
    private void setupGutter(PoemSyllablesAnalysis analysis) {
        List<Integer> counts = new ArrayList<>();
        for (Integer syllableCount: analysis.getSyllables()) {
            counts.add(syllableCount);
        }
        // Poem events arrive on TaskBus threads; PoemGutter is a Swing
        // component, so hop to the EDT.
        SwingUtilities.invokeLater(() -> poemGutter.setSyllableCounts(counts));
    }

    // Pattern group display.
    private void setupGutter(PatternAnalysis patternAnalysis) {
        List<String> pattern = new ArrayList<>(patternAnalysis.getPattern());

        // Poem events arrive on TaskBus threads; PoemGutter is a Swing
        // component, so hop to the EDT.
        SwingUtilities.invokeLater(() -> poemGutter.setPattern(pattern));
    }

    // Setting up what shows when user hovers over a word.
    // TODO: Right now it's placeholder text. Eventually it will show
    //       stuff from the dictionary, parts of speech and metering.
    // It takes in a map with the word character offset and the associated word
    // to know what word is in question is being hovered over.
    private void setupHover(NavigableMap<Integer, Word> index) {
        // Allows the tooltips to be hovered over and keep them displayed. Done
        // mostly to keep them in view so the user doesn't have keep the pointer
        // over the word to read it's details.
        // TODO: Doesn't work very well now because of the hover changing right
        //       away as the pointer moves.
        textArea.setUseFocusableTips(false);

        // Tooltip Appearance.
        // Post-It Note Style. Yellow background. A hint of grey. And rounded
        // edges.

        // Colours
        UIManager.put("ToolTip.background", new Color(EditorConstants.TOOLTIP_BACKGROUND_COLOUR));   // warm off-white; tune to your palette
        UIManager.put("ToolTip.foreground", new Color(EditorConstants.TOOLTIP_FONT_COLOUR));
        UIManager.put("ToolTip.border", new FlatLineBorder(
                new Insets(6, 10, 6, 10), new Color(EditorConstants.TOOLTIP_BORDER_COLOUR), 1, 8));

        // Native rounded window corners + OS drop shadow
        // Seems to work on Windows 11, macOS + Linux not tested yet.
        UIManager.put("Popup.borderCornerRadius", 8);
        UIManager.put("Popup.forceHeavyWeight", true);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(true);

        // Tooltip Text Font
        UIManager.put("ToolTip.font", new Font(EditorConstants.TOOLTIP_FONT, EditorConstants.TOOLTIP_FONT_WEIGHT, EditorConstants.TOOLTIP_FONT_SIZE));

        // This is the part that does the magic of finding the actual word
        // entity based on where the cursor is hovering over the text area.
        //
        // TODO: I don't like the idea that the hovered word is part of the
        //       text area component. It should live in the view or maybe even
        //       the model.
        //
        // 1. It gets the position of the actual character that is being
        //    hovered over. So from pixel coordinates like 303x203 to character
        //    at position 12th letter.
        // 2. The index map comes from the parser PoemExtended visitor. It maps
        //    a text position to the associated Word object. Keep in mind, the
        //    map doesn't know when the text ends. A NavigableMap is used
        //    because it can find the key with the closest character position.
        // 3. TODO: Right now, the actual word is just shown as a placeholder.
        //          Eventually analysis of part-of-word and definitions and
        //          stuff will be here.
        // TODO: The tooltip text should be a template.
        // TODO: The logic for the null check could be simplified.
        textArea.setToolTipSupplier((textArea, event) -> {
            int offset = textArea.viewToModel2D(event.getPoint());
            Map.Entry<Integer, Word> entry = index.floorEntry(offset);
            if (entry == null || !entry.getValue().contains(offset)) {
                ((PoemTextArea) textArea).setHoveredWord(null);
                return null;
            }
            Word word = entry.getValue();
            ((PoemTextArea) textArea).setHoveredWord(word);
            return "<html><b>" + word.getWord() + "</b><br>The word you're highlighting is " + word.getWord() + ".</html>";
        });

        // Make Swing aware that tooltips will need to be shown for the editor
        // area.
        ToolTipManager.sharedInstance().registerComponent(textArea);
    }

    @Override
    protected void listen() {
        // Most important listener in all of PoetWrite. When the text is
        // updated.
        // It updates the text in the persistence handler.
        // It causes for the poem to be parsed for use by everything else.
        //
        // TODO: Obviously it needs debounce. But not everything should have it.
        //       Something like counting syllables or detecting rhyming pattern
        //       should be instant.
        //       While something like a dictionary lookup should take time.
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
                // This isn't actually called when the text edited unlike the
                // name of the method would suggest, such as changes in the
                // font or appearance in the text.
            }
        });

        // Remember how the close operationg wsa set to DO_NOTHING_ON_CLOSE?
        // This is where the actual closing flow is done.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Prompt the user to save if there are unsaved changes in the
                // poem.
                if (status == PersistenceManager.FileStatus.CHANGED) {
                    int confirm = JOptionPane.showConfirmDialog(frame, PromptConstants.PROMPT_UNSAVED_CHANGED_FOR_QUIT, PromptConstants.TITLE_QUIT, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (confirm == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                // Close the application. The closeApp() does the cleanup and
                // waits for the TaskBus to finish doing all its tasks.
                viewController.closeApp();
            }
        });

        // To trigger autocomplete upon pressing control + space. This is done
        // indirectly by sending an event all the way back down to TaskBus.
        // This view listens for the response to bring up the auto-complete
        // window.
        // Done as an event as the auto-complete features are planned to grow
        // and we need this to be non-blocking.
        textArea.getInputMap().put(
                KeyStroke.getKeyStroke("control SPACE"), "autocomplete");
        textArea.getActionMap().put("autocomplete", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                viewController.requestAutoComplete();
            }
        });
    }

    // Is fed the status bar to display current task status.
    // TODO: Currently unused as I decide if status display is useful, or
    //       if it just clutters the interface.
    public void attachStatusBar(JPanel pane) {
        frame.add(pane, BorderLayout.SOUTH);
    }

    // To feed the main application menu.
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
        // ---------------------------------------------------------------------
        // Text change handling.
        Disposable textSubscriber = viewModel.editorContent()
                .subscribe(text -> {
                    SwingUtilities.invokeLater(() -> textArea.setText(text));
                });
        disposable.add(textSubscriber);

        // ---------------------------------------------------------------------
        // Analyses events

        // Basic analyses.
        Disposable poemSubscriber = viewModel.poem().subscribe(
                poem -> {
                    viewController.analyzeSyllables(poem);
                    viewController.analyzePattern(poem);
                    viewController.indexPoem(poem);
                }
        );
        disposable.add(poemSubscriber);

        // Counting syllables per line.
        Disposable poemSyllableAnalysisSubscriber = viewModel.poemSyllablesAnalysis().subscribe(
                poemSyllableAnalysis -> {
                    setupGutter(poemSyllableAnalysis);
                }
        );
        disposable.add(poemSyllableAnalysisSubscriber);

        // Calculate rhyming pattern and groups.
        Disposable patternAnalysisSubscriber = viewModel.patternAnalysis().subscribe(
                patternAnalysis -> {
                    setupGutter(patternAnalysis);
                }
        );
        disposable.add(patternAnalysisSubscriber);

        // Setup what will be shown in hover over words.
        Disposable poemIndexSubscriber = viewModel.poemIndex().subscribe(
                poemIndex -> {
                    setupHover(poemIndex);
                }
        );

        disposable.add(poemIndexSubscriber);



        // ---------------------------------------------------------------------
        // File status related.

        // When creating a file, saving or opening, the logic to determine
        // if a confirmation to save dialogue will need to be presented.
        Disposable dialogNeededSubscriber =  viewModel.dialogNeeded()
                .subscribe(dialogNeeded -> {
                    requestSave(dialogNeeded);
                });

        disposable.add(dialogNeededSubscriber);

        // Show file status in the title bar.
        // TODO: Clean-up and extract common code into method.
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

        // Show file name in the title bar.
        // TODO: Clean-up and extract common code into method.
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

        // ---------------------------------------------------------------------
        // Auto-completed related stuff.
        // See Controller and Model comments for information on these.

        // Query Tree created. Only the tree as needed.
        Disposable queryTreeSubscriber = viewModel.queryTreeBuilt().subscribe(
                root -> SwingUtilities.invokeLater(() -> {
                    if (wizard != null) {
                        wizard.setRoot(root);
                    }
                }));
        disposable.add(queryTreeSubscriber);

        // User asks for the auto complete dialog to show up with ctrl+space.
        Disposable autoCompleteRequestedSubscriber = viewModel.autoCompleteRequested().subscribe(
                root -> SwingUtilities.invokeLater(() -> showAutoComplete()));
        disposable.add(autoCompleteRequestedSubscriber);

        // After the next steps are requested when the user makes the selection.
        Disposable queryStepSubscriber = viewModel.queryStepExecuted().subscribe(
                event -> SwingUtilities.invokeLater(() -> {
                    if (wizard != null) {
                        wizard.deliver(event);
                    }
                }));
        disposable.add(queryStepSubscriber);

        // After the preview for the current step was made.
        Disposable queryPreviewSubscriber = viewModel.queryPreviewed().subscribe(
                event -> SwingUtilities.invokeLater(() -> {
                    if (wizard != null) {
                        wizard.deliver(event);
                    }
                }));
        disposable.add(queryPreviewSubscriber);
    }

    // Shows the auto-completed wizard. The request to display come from the
    // TaskBus loop after pressing ctrl+space.
    @Draft("Displays the autocomplete wizard")
    @Helped("The feature set is huge, and as mentioned, custom UI components are throwaway")
    private void showAutoComplete() {
        if (wizardWindow != null) {
            wizardWindow.dispose();
            wizardWindow = null;
            wizard = null;
        }

        Rectangle caret;
        try {
            caret = textArea.modelToView2D(textArea.getCaretPosition()).getBounds();
        } catch (BadLocationException e) {
            return;
        }

        JWindow window = new JWindow(SwingUtilities.getWindowAncestor(textArea));
        window.setType(Window.Type.POPUP);      // BEFORE anything can pack the window
        window.setFocusableWindowState(true);
        wizardWindow = window;

        wizard = new QueryWizard(new QueryWizard.Listener() {
            @Override public void execute(QueryStep step) {
                viewController.executeQueryStep(step);
            }

            @Override public void preview(QueryStep step) {
                viewController.previewQueryStep(step);
            }

            @Override public void completed(QueryStep step) {
                System.out.println("QUERY: " + step.getParameters().all());
                close();
            }

            @Override public void cancelled() {
                close();
            }

            @Override public void layoutChanged() {
                if (window.isShowing()) {   // ignore events fired during construction
                    window.pack();
                }
            }

            private void close() {
                window.dispose();
                wizardWindow = null;
                wizard = null;
                textArea.requestFocusInWindow();
            }
        });

        wizard.setFont(new Font(EditorConstants.DEFAULT_EDITOR_FONT, Font.PLAIN,
                EditorConstants.DEFAULT_EDITOR_FONT_SIZE - 2));

        window.add(wizard);
        window.pack();

        if (SystemInfo.isWindows_11_orLater && FlatNativeWindowsLibrary.isLoaded()) {
            long hwnd = FlatNativeWindowsLibrary.getHWND(window);
            FlatNativeWindowsLibrary.setWindowCornerPreference(hwnd, 3);
        }

        Point location = new Point(caret.x, caret.y + caret.height + 2);
        SwingUtilities.convertPointToScreen(location, textArea);
        window.setLocation(location);

        window.setVisible(true);
        wizard.focusActivePane();

        viewController.buildAutoCompleteTree();
    }

    // Called when a user is trying to save a file, either directly with the
    // save or save as function, or when an application has unsaved changes.
    //
    // dialogNeeded means a file selector dialog is needed so the user can
    // pick their new file will go.
    @Draft
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

    // TODO: This will eventually be called as early as possible. So that the
    //       window shows up before any other loading is done. To make the start
    //       feel snappy.
    public void show() {
        SwingUtilities.invokeLater(() -> frame.show());
    }
}
