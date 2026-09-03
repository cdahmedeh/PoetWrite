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

package net.cdahmedeh.poetwrite.ui.component;

import net.cdahmedeh.poetwrite.query.event.QueryPreviewedEvent;
import net.cdahmedeh.poetwrite.query.event.QueryStepExecutedEvent;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.ui.constant.EditorConstants;

import com.formdev.flatlaf.extras.FlatSVGIcon; //TODO: trick the classloader

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wizard-style chained autocomplete, driven entirely by the keyboard.
 *
 * <pre>
 *   Up / Down     move selection in the active (rightmost) pane
 *   Enter         commit the highlighted step; next pane appears,
 *                 or the query completes if the step is terminal
 *   Escape        pop the active pane (cancels the wizard at the root)
 *   Backspace     in an empty text field: pop the active pane
 *   typing        regenerates the active pane's options
 *   click         on an earlier pane rewinds the wizard back to it
 * </pre>
 *
 * The flow is described entirely by the QueryStep tree: a step's children are
 * the rows of the next pane, a step that declares a search gets a text field
 * above its rows, and a step with nothing below it ends the query.
 *
 * Knows nothing about the TaskBus. It asks for work through the Listener and
 * is handed results through deliver(..), which the View calls on the EDT.
 * Host it in a JWindow/Popup anchored at the caret; call focusActivePane()
 * after showing it, and re-pack the window from Listener.layoutChanged().
 */
public final class QueryWizard extends JPanel {

    public interface Listener {
        /** Ask for a step's column. Result comes back via deliver(..). */
        void execute(QueryStep step);

        /** Ask for a step's preview text. Result comes back via deliver(..). */
        void preview(QueryStep step);

        /** The user committed a terminal step. Its parameters are the full query. */
        void completed(QueryStep step);

        /** Escape at the root pane. Host should hide the popup. */
        void cancelled();

        /** A pane or preview was added/removed - host should re-pack its window. */
        default void layoutChanged() {}
    }

    private static final int PANE_WIDTH    = 160;
    private static final int PANE_HEIGHT   = 220;
    private static final int PREVIEW_WIDTH = 280;

    // Palette - pastel family of the editor's syntax colours (notes blue /
    // asides green). Point these at the app's central palette constants.
    private static final Color SELECTION_ACTIVE   = new Color(0xEA, 0xEE, 0xF2); // pastel blue
    private static final Color SELECTION_INACTIVE = new Color(0xEA, 0xEE, 0xF2); // muted, committed panes
    private static final Color PREVIEW_BG         = new Color(0xFFFFFD);

    /** Placeholder row while a column is still resolving. */
    private static final Object LOADING = new Object();

    private static final int ICON_SIZE = 16;
    private static final int ICON_GAP  = 6;
    private static final float ICON_OPACITY = 0.50f; // ~25% transparent

    // QueryStep stores an icon as a resource path so the query package stays
    // free of Swing. Resolving it is the wizard's job, and cached because the
    // renderer runs per row per repaint.
    private static final Map<String, Icon> ICON_CACHE = new HashMap<>();

    private static Icon icon(String path) {
        if (path == null) {
            return null;
        }
        return ICON_CACHE.computeIfAbsent(path, resource -> {
            String name = resource.startsWith("/") ? resource.substring(1) : resource;
            FlatSVGIcon svg = new FlatSVGIcon(name, ICON_SIZE, ICON_SIZE);
            svg.setColorFilter(new FlatSVGIcon.ColorFilter(color ->
                    new Color(color.getRed(), color.getGreen(), color.getBlue(),
                            Math.round(color.getAlpha() * ICON_OPACITY))));
            return svg;
        });
    }

    private final Listener listener;
    private final List<StepPane> panes = new ArrayList<>();

    private JComponent previewBox;      // currently attached preview, or null
    private QueryStep previewing;       // step we last asked a preview for

    private Timer previewDotsTimer;     // cycles the preview's "Loading..." dots
    private JLabel previewLoadingLabel; // non-null only while the preview is pending
    private int previewDotCount = 1;

    /**
     * Opens with a single pending pane, so the popup can be shown the instant
     * the user asks for it. Call setRoot(..) once the tree has been built.
     */
    public QueryWizard(Listener listener) {
        this.listener = listener;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createLineBorder(borderColor())); // single outer border;
        setBackground(UIManager.getColor("List.background"));     // children only draw 1px dividers
        setOpaque(true);
        setFocusable(true); // receives keys when the active pane has no text field
        installKeyBindings();
        push(null); // pending pane: shows the loading row, asks for nothing
    }

    /** Called on the EDT by the View once the query tree is available. */
    public void setRoot(QueryStep root) {
        if (!panes.isEmpty() && panes.get(0).step == null) {
            StepPane pending = panes.remove(0);
            remove(pending);
            pending.stopLoadingAnimation();
        }
        push(root);
    }

    /** Call once after the host popup becomes visible. */
    public void focusActivePane() {
        activePane().takeFocus();
    }

    /**
     * Stops every animation. Call from the host when the popup closes,
     * otherwise the dot timers keep firing against a detached component.
     */
    public void dispose() {
        stopPreviewLoading();
        for (StepPane pane : panes) {
            pane.stopLoadingAnimation();
        }
    }

    /** Propagates the font (use the editor's) to all panes and the preview. */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (panes == null || panes.isEmpty()) {
            return; // called during superclass construction
        }
        for (StepPane pane : panes) {
            pane.applyFont(font);
        }
        if (previewBox != null) {
            previewBox.setFont(font);
        }
    }

    /** The steps committed so far, in order. Excludes the active pane. */
    public List<QueryStep> chain() {
        List<QueryStep> chain = new ArrayList<>();
        for (StepPane pane : panes) {
            if (pane.committed != null) {
                chain.add(pane.committed);
            }
        }
        return chain;
    }

    // -------------------------------------------------------------- incoming

    /** Called on the EDT by the View when a column is ready. */
    public void deliver(QueryStepExecutedEvent event) {
        for (StepPane pane : panes) {
            if (pane.step == event.getStep()) {
                pane.applySteps(event.getSteps());
                return;
            }
        }
        // Belongs to a pane that has since been popped.
    }

    /** Called on the EDT by the View when a preview is ready. */
    public void deliver(QueryPreviewedEvent event) {
        if (event.getStep() != previewing) {
            return; // the highlight has already moved on
        }
        attachPreview(event.getText());
    }

    // ------------------------------------------------------------------ flow

    private StepPane activePane() {
        return panes.get(panes.size() - 1);
    }

    private void push(QueryStep step) {
        if (!panes.isEmpty()) {
            activePane().setActive(false);
        }
        StepPane pane = new StepPane(step);
        panes.add(pane);
        pane.setBorder(dividerBorder(panes.size() == 1, 0));
        detachPreview();
        add(pane);
        pane.refilter();
        pane.takeFocus();
        revalidate();
        repaint();
        listener.layoutChanged();
    }

    private void commit() {
        StepPane active = activePane();
        QueryStep chosen = active.selectedStep();
        if (chosen == null) {
            return;
        }
        active.committed = chosen;
        if (terminal(chosen)) {
            listener.completed(chosen);
        } else {
            push(chosen);
        }
    }

    /** Nothing declared below it, so committing it finishes the query. */
    private static boolean terminal(QueryStep step) {
        return !step.hasSteps() && !step.hasCommand() && !step.hasSearch();
    }

    private void back() {
        if (panes.size() == 1) {
            listener.cancelled();
            return;
        }
        StepPane removed = panes.remove(panes.size() - 1);
        remove(removed);
        removed.stopLoadingAnimation();
        reactivateLast();
    }

    private void rewindTo(StepPane target) {
        while (panes.size() > 1 && activePane() != target) {
            StepPane removed = panes.remove(panes.size() - 1);
            remove(removed);
            removed.stopLoadingAnimation();
        }
        reactivateLast();
    }

    private void reactivateLast() {
        StepPane active = activePane();
        active.committed = null;
        active.setActive(true);
        active.takeFocus();
        active.requestPreview();
        revalidate();
        repaint();
        listener.layoutChanged();
    }

    // --------------------------------------------------------------- borders

    /** 1px divider on the left edge only, so adjacent boxes share one line. */
    private javax.swing.border.Border dividerBorder(boolean first, int padding) {
        javax.swing.border.Border edge = first
                ? BorderFactory.createEmptyBorder()
                : BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor());
        if (padding == 0) {
            return edge;
        }
        return BorderFactory.createCompoundBorder(edge,
                BorderFactory.createEmptyBorder(padding, padding + 2, padding, padding + 2));
    }

    private static Color borderColor() {
        return new Color(0xEC, 0xEC, 0xE0);
    }

    // --------------------------------------------------------------- preview

    private void detachPreview() {
        stopPreviewLoading();
        if (previewBox != null) {
            remove(previewBox);
            previewBox = null;
        }
    }

    private void stopPreviewLoading() {
        if (previewDotsTimer != null) {
            previewDotsTimer.stop();
        }
        previewLoadingLabel = null;
    }

    /**
     * Puts the preview box up straight away, holding a loading row, so it does
     * not pop into existence once the TaskBus is done. Same shape and size as
     * the finished preview, so the packed window does not jump.
     */
    private void attachPreviewLoading() {
        detachPreview();

        previewDotCount = 1;
        previewLoadingLabel = new JLabel("Loading.");
        previewLoadingLabel.setVerticalAlignment(JLabel.TOP);
        previewLoadingLabel.setForeground(new Color(150, 150, 150)); // matches the "Searching..." row

        if (previewDotsTimer == null) {
            previewDotsTimer = new Timer(350, e -> {
                previewDotCount = (previewDotCount % 3) + 1;
                if (previewLoadingLabel != null) {
                    previewLoadingLabel.setText("Loading" + ".".repeat(previewDotCount));
                }
            });
        }
        previewDotsTimer.start();

        previewBox = wrapPreview(previewLoadingLabel);
        add(previewBox);
        revalidate();
        repaint();
        listener.layoutChanged();
    }

    /**
     * The second half of the old refreshPreview(): the ask happens in
     * StepPane.requestPreview(), and the text lands here when the TaskBus is
     * done with it.
     */
    private void attachPreview(String text) {
        detachPreview();
        if (text != null && !text.isBlank()) {
            previewBox = wrapPreview(previewLabel(text));
            add(previewBox);
        }
        revalidate();
        repaint();
        listener.layoutChanged();
    }

    private JComponent previewLabel(String text) {
        // The explicit width is what makes long lines wrap -- a plain <html>
        // label lays out on one line and gets clipped by the box.
        JLabel label = new JLabel("<html><div style='width:" + (PREVIEW_WIDTH - 26) + "px'>"
                + text.replace("\n", "<br>") + "</div></html>");
        label.setVerticalAlignment(JLabel.TOP);
        return label;
    }

    private JComponent wrapPreview(JComponent content) {
        content.setFont(getFont());
        JPanel box = new JPanel(new BorderLayout());
        box.setBorder(dividerBorder(false, 8));
        box.setBackground(PREVIEW_BG);
        box.setOpaque(true);
        content.setOpaque(false);
        box.add(content, BorderLayout.CENTER);

        // Never shorter than a pane, but allowed to grow so a rich preview
        // (definition, synonyms, ARPAbet) is not cut off at the bottom.
        int height = Math.max(PANE_HEIGHT, content.getPreferredSize().height + 16);
        Dimension size = new Dimension(PREVIEW_WIDTH, height);
        box.setPreferredSize(size);
        box.setMinimumSize(size);
        box.setMaximumSize(size);
        box.setAlignmentY(TOP_ALIGNMENT);
        return box;
    }

    // ------------------------------------------------------------------ keys

    private void installKeyBindings() {
        // ANCESTOR map: fires whether focus sits on this panel or on a pane's
        // text field - except where the field itself binds the key (Enter,
        // Backspace, Left), which the field handles and routes back to us.
        InputMap im = getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();
        bind(im, am, "DOWN",       "pw-down",   () -> activePane().moveSelection(+1));
        bind(im, am, "UP",         "pw-up",     () -> activePane().moveSelection(-1));
        bind(im, am, "ENTER",      "pw-commit", this::commit);
        bind(im, am, "ESCAPE",     "pw-back",   this::back);
        // LEFT and BACK_SPACE only reach this map when focus is NOT in a text
        // field (fields bind both, taking precedence; the field's own
        // Backspace wrapper deletes text first and calls back() once empty).
        bind(im, am, "LEFT",       "pw-left",   this::back);
        bind(im, am, "BACK_SPACE", "pw-bs",     this::back);
    }

    private void bind(InputMap im, ActionMap am, String key, String name, Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), name);
        am.put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    // ------------------------------------------------------------------ pane

    private final class StepPane extends JPanel {

        final QueryStep step;
        final JTextField field; // null when the step has no search
        final DefaultListModel<Object> model = new DefaultListModel<>();
        final JList<Object> list = new JList<>(model);
        QueryStep committed;    // set once this pane's choice is locked in

        Timer dotsTimer;        // cycles the "Searching..." row's dot count
        int loadingDotCount = 1;

        // Executes asked for and not yet delivered. Typing queues one per
        // keystroke, and applying the intermediate ones would reset the
        // highlight under the user's fingers, so only the last is applied.
        int pending;

        StepPane(QueryStep step) {
            super(new BorderLayout());
            this.step = step;
            setBackground(UIManager.getColor("List.background"));
            setOpaque(true);

            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setFocusable(false); // keys stay on the field / wizard panel
            list.setCellRenderer(new StepRenderer());
            list.setSelectionBackground(SELECTION_ACTIVE);
            list.setSelectionForeground(UIManager.getColor("List.foreground"));
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && isActivePane()) {
                    requestPreview();
                }
            });
            list.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (!isActivePane()) {
                        rewindTo(StepPane.this);
                    }
                }
                @Override public void mouseClicked(MouseEvent e) {
                    if (isActivePane() && e.getClickCount() == 2) {
                        commit();
                    }
                }
            });

            if (step != null && step.hasSearch()) {
                field = new JTextField();
                field.setBackground(new Color(0xFEFEFE));
                field.putClientProperty("JTextField.placeholderText", "word...");
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor()),
                        BorderFactory.createEmptyBorder(6, 9, 5, 6)));
                field.getDocument().addDocumentListener(new DocumentListener() {
                    @Override public void insertUpdate(DocumentEvent e)  { refilter(); }
                    @Override public void removeUpdate(DocumentEvent e)  { refilter(); }
                    @Override public void changedUpdate(DocumentEvent e) { refilter(); }
                });
                field.addActionListener(e -> commit()); // Enter inside the field
                installEmptyBackspace(field);
                add(field, BorderLayout.NORTH);
            } else {
                field = null;
            }

            JScrollPane scroll = new JScrollPane(list);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            add(scroll, BorderLayout.CENTER);

            applyFont(QueryWizard.this.getFont());

            Dimension size = new Dimension(PANE_WIDTH, PANE_HEIGHT);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setAlignmentY(TOP_ALIGNMENT);
        }

        void applyFont(Font font) {
            if (font == null) {
                return;
            }
            list.setFont(font);
            if (field != null) {
                field.setFont(font);
            }
            list.setBackground(new Color(0xFFFFFF)); //TODO: Shouldn't be here
        }

        /** Backspace deletes text; once the field is empty, the next one pops the pane. */
        private void installEmptyBackspace(JTextField field) {
            KeyStroke bs = KeyStroke.getKeyStroke("BACK_SPACE");
            Object originalKey = field.getInputMap().get(bs);
            javax.swing.Action original = originalKey == null
                    ? null : field.getActionMap().get(originalKey);
            field.getInputMap().put(bs, "pw-backspace");
            field.getActionMap().put("pw-backspace", new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    if (field.getText().isEmpty()) {
                        back();
                    } else if (original != null) {
                        original.actionPerformed(e);
                    }
                }
            });
        }

        boolean isActivePane() {
            return activePane() == this;
        }

        /**
         * Hands the typed text to the step and asks for its column.
         *
         * The TaskBus is single-threaded FIFO and the View marshals with
         * invokeLater, so replies arrive in the order they were asked for.
         * That means counting outstanding requests is enough to spot a stale
         * one -- no generation token on the event needed.
         */
        void refilter() {
            if (step == null) {
                showLoadingPlaceholder();   // pending pane: nothing to ask for yet
                return;
            }
            if (field != null) {
                step.getSearch().setText(field.getText());
            }
            pending++;
            showLoadingPlaceholder();
            listener.execute(step);
        }

        /** Shows a non-selectable "Searching..." row, but only if the pane is currently
         *  empty - if stale results from a previous keystroke are still showing,
         *  leave them visible instead of covering them with the placeholder. */
        private void showLoadingPlaceholder() {
            if (!model.isEmpty()) {
                return;
            }
            if (dotsTimer == null) {
                dotsTimer = new Timer(350, e -> {
                    loadingDotCount = (loadingDotCount % 3) + 1;
                    list.putClientProperty("pw.loadingDots", loadingDotCount);
                    list.repaint();
                });
            }
            loadingDotCount = 1;
            list.putClientProperty("pw.loadingDots", loadingDotCount);
            dotsTimer.start();

            model.addElement(LOADING);
            // Select it so it renders as a full row, the way a real first
            // entry does. It is still not committable -- selectedStep()
            // returns null for it, so Enter is a no-op.
            list.setSelectedIndex(0);
            if (isActivePane()) {
                requestPreview();
            }
        }

        /** Stops the dots timer. Safe to call even if never started. */
        void stopLoadingAnimation() {
            if (dotsTimer != null) {
                dotsTimer.stop();
            }
        }

        void applySteps(List<QueryStep> steps) {
            if (pending > 0) {
                pending--;
            }
            if (pending > 0) {
                return; // superseded by a later keystroke -- leave the list alone
            }

            stopLoadingAnimation();
            model.clear();
            boolean hasIcons = false;
            for (QueryStep step : steps) {
                model.addElement(step);
                hasIcons |= step.hasIcon();
            }
            // Rows without an icon get padded to match, so text lines up.
            list.putClientProperty("pw.hasIcons", hasIcons);

            selectFirst();
            if (isActivePane()) {
                requestPreview();
            }
            listener.layoutChanged();
        }

        private void selectFirst() {
            if (model.isEmpty()) {
                list.clearSelection();
            } else {
                list.setSelectedIndex(0);
            }
        }

        QueryStep selectedStep() {
            Object selected = list.getSelectedValue();
            return selected instanceof QueryStep queryStep ? queryStep : null;
        }

        /** Asks for the highlighted step's preview, or clears it if there is none. */
        void requestPreview() {
            QueryStep selected = selectedStep();
            previewing = selected;

            if (selected == null || !selected.hasPreview()) {
                detachPreview();
                revalidate();
                repaint();
                listener.layoutChanged();
                return;
            }

            attachPreviewLoading();
            listener.preview(selected);
        }

        void moveSelection(int direction) {
            int size = model.size();
            if (size == 0) {
                return;
            }
            int index = list.getSelectedIndex();
            for (int i = index + direction; i >= 0 && i < size; i += direction) {
                if (model.get(i) instanceof QueryStep) {
                    list.setSelectedIndex(i);
                    list.ensureIndexIsVisible(i);
                    return;
                }
            }
        }

        void setActive(boolean active) {
            if (field != null) {
                field.setEditable(active);
                field.setFocusable(active);
            }
            // committed panes keep their choice visible, but muted
            list.setSelectionBackground(active ? SELECTION_ACTIVE : SELECTION_INACTIVE);
            list.repaint();
        }

        void takeFocus() {
            if (field != null) {
                field.requestFocusInWindow();
            } else {
                QueryWizard.this.requestFocusInWindow();
            }
        }
    }

    // -------------------------------------------------------------- renderer

    private static final class StepRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean selected, boolean focused) {
            if (value == LOADING) {
                Object dots = list.getClientProperty("pw.loadingDots");
                int dotCount = dots instanceof Integer ? (Integer) dots : 1;

                JLabel c = (JLabel) super.getListCellRendererComponent(
                        list, "Searching" + ".".repeat(dotCount), index, selected, false);
                c.setIcon(null);
                c.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));
                c.setForeground(new Color(150, 150, 150)); // quieter than normal rows - it's a status, not a choice
                c.setOpaque(true);
                return c;
            }

            QueryStep step = (QueryStep) value;

            JLabel c = (JLabel) super.getListCellRendererComponent(
                    list, step.getName(), index, selected, false);
            c.setIcon(icon(step.getIcon()));
            c.setIconTextGap(ICON_GAP);

            // if this pane has any icons, pad icon-less rows so text lines up
            boolean paneHasIcons = Boolean.TRUE.equals(list.getClientProperty("pw.hasIcons"));
            int indent = (paneHasIcons && !step.hasIcon()) ? ICON_SIZE + ICON_GAP : 0;

            c.setBorder(BorderFactory.createEmptyBorder(4, 9 + indent, 4, 9));
            c.setForeground(new Color(EditorConstants.TEXT_EDITOR_FONT_COLOUR,
                    EditorConstants.TEXT_EDITOR_FONT_COLOUR, EditorConstants.TEXT_EDITOR_FONT_COLOUR));
            c.setOpaque(true);
            return c;
        }
    }
}