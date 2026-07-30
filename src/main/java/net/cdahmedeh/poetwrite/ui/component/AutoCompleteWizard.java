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

import net.cdahmedeh.poetwrite.ui.constant.EditorConstants;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wizard-style chained autocomplete, driven entirely by the keyboard.
 *
 * <pre>
 *   Up / Down     move selection in the active (rightmost) pane
 *   Enter         commit the highlighted option; next pane appears,
 *                 or the chain completes if the option is terminal
 *   Escape        pop the active pane (cancels the wizard at the root)
 *   Backspace     in an empty text field: pop the active pane
 *   typing        refilters / regenerates the active pane's options
 *   click         on an earlier pane rewinds the wizard back to it
 * </pre>
 *
 * The component knows nothing about poems, rhymes, or dictionaries: the whole
 * flow is described by a graph of {@link WizardStep}s and {@link WizardOption}s,
 * and the outcome is a {@link WizardChain} of clause values. Host it in a
 * JWindow/Popup anchored at the caret; call {@link #focusActivePane()} after
 * showing it, and re-pack the window from {@link Listener#layoutChanged()}.
 */
public final class AutoCompleteWizard extends JPanel {

    public interface Listener {
        /** The user committed a terminal option. The chain is the full query. */
        void completed(WizardChain chain);

        /** Escape at the root pane. Host should hide the popup. */
        void cancelled();

        /** A pane or preview was added/removed — host should re-pack its window. */
        default void layoutChanged() {}
    }

    private static final int PANE_WIDTH    = 160;
    private static final int PANE_HEIGHT   = 220;
    private static final int PREVIEW_WIDTH = 280;

    // Palette — pastel family of the editor's syntax colours (notes blue /
    // asides green). Point these at the app's central palette constants.
    private static final Color SELECTION_ACTIVE   = new Color(0xEA, 0xEE, 0xF2); // pastel blue
    private static final Color SELECTION_INACTIVE = new Color(0xEA, 0xEE, 0xF2); // muted, committed panes
    private static final Color FIELD_UNDERLINE    = new Color(0x7F, 0xB3, 0xD9); // stronger blue accent
    //    private static final Color PREVIEW_BG         = new Color(0xFAF8F4); // faint green wash
    private static final Color PREVIEW_BG         = new Color(0xFFFFFD); // faint green wash

    private final Listener listener;
    private final List<StepPane> panes = new ArrayList<>();
    private JComponent previewBox; // currently attached preview, or null

    public AutoCompleteWizard(WizardStep root, Listener listener) {
        this.listener = listener;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createLineBorder(borderColor())); // single outer border;
        setBackground(UIManager.getColor("List.background"));     // children only draw 1px dividers
        setOpaque(true);
        setFocusable(true); // receives keys when the active pane has no text field
        installKeyBindings();
        push(root);
    }

    /** Call once after the host popup becomes visible. */
    public void focusActivePane() {
        activePane().takeFocus();
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
        refreshPreview(); // rebuild so preview content picks the font up too
    }

    /** The chain of committed selections so far (excludes the active pane). */
    public WizardChain chain() {
        WizardChain chain = WizardChain.EMPTY;
        for (StepPane pane : panes) {
            if (pane.committed != null) {
                chain = chain.plus(pane.committed);
            }
        }
        return chain;
    }

    // ------------------------------------------------------------------ flow

    private StepPane activePane() {
        return panes.get(panes.size() - 1);
    }

    private void push(WizardStep step) {
        if (!panes.isEmpty()) {
            activePane().setActive(false);
        }
        StepPane pane = new StepPane(step, chain());
        panes.add(pane);
        pane.setBorder(dividerBorder(panes.size() == 1, 0));
        detachPreview();
        add(pane);
        pane.refilter();
        pane.takeFocus();
        refreshPreview();
        revalidate();
        repaint();
        listener.layoutChanged();
    }

    private void commit() {
        StepPane active = activePane();
        WizardOption chosen = active.selectedOption();
        if (chosen == null || !chosen.isSelectable()) {
            return;
        }
        active.committed = chosen;
        WizardChain chain = chain(); // now includes the chosen option
        if (chosen.isTerminal()) {
            listener.completed(chain);
            return;
        }
        WizardStep next = chosen.createNext(chain);
        if (next == null) { // factory may decide there's nothing further
            listener.completed(chain);
        } else {
            push(next);
        }
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
        refreshPreview();
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
//        Color c = UIManager.getColor("Component.borderColor");
//        return c != null ? c : Color.GRAY;
        return new Color(0xEC, 0xEC, 0xE0);
    }

    // --------------------------------------------------------------- preview

    private void detachPreview() {
        if (previewBox != null) {
            remove(previewBox);
            previewBox = null;
        }
    }

    private void refreshPreview() {
        detachPreview();
        WizardOption selected = activePane().selectedOption();
        if (selected != null && selected.preview() != null) {
            JComponent content = selected.preview().get();
            if (content != null) {
                previewBox = wrapPreview(content);
                add(previewBox);
            }
        }
        revalidate();
        repaint();
        listener.layoutChanged();
    }

    private JComponent wrapPreview(JComponent content) {
        content.setFont(getFont());
        JPanel box = new JPanel(new BorderLayout());
        box.setBorder(dividerBorder(false, 8));
        box.setBackground(PREVIEW_BG);
        box.setOpaque(true);
        content.setOpaque(false);
        box.add(content, BorderLayout.CENTER);
        Dimension size = new Dimension(PREVIEW_WIDTH, PANE_HEIGHT);
        box.setPreferredSize(size);
        box.setMinimumSize(size);
        box.setMaximumSize(size);
        box.setAlignmentY(TOP_ALIGNMENT);
        return box;
    }

    // ------------------------------------------------------------------ keys

    private void installKeyBindings() {
        // ANCESTOR map: fires whether focus sits on this panel or on a pane's
        // text field — except where the field itself binds the key (Enter,
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

        final WizardStep step;
        final WizardChain chainBefore;
        final JTextField field; // null when the step has no text input
        final DefaultListModel<WizardOption> model = new DefaultListModel<>();
        final JList<WizardOption> list = new JList<>(model);
        final AtomicInteger requestGeneration = new AtomicInteger();
        WizardOption committed; // set once this pane's choice is locked in

        Timer dotsTimer;          // cycles the "Searching…" row's dot count
        int loadingDotCount = 1;

        StepPane(WizardStep step, WizardChain chainBefore) {
            super(new BorderLayout());
            this.step = step;
            this.chainBefore = chainBefore;
            setBackground(UIManager.getColor("List.background"));
            setOpaque(true);

            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setFocusable(false); // keys stay on the field / wizard panel
            list.setCellRenderer(new OptionRenderer());
            list.setSelectionBackground(SELECTION_ACTIVE);
            list.setSelectionForeground(UIManager.getColor("List.foreground"));
            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && isActivePane()) {
                    refreshPreview();
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

            if (step.hasTextField()) {
                field = new JTextField();
                field.setBackground(new Color(0xFEFEFE));
//                field.setBackground(new Color(0xFDFDF9));
                field.putClientProperty("JTextField.placeholderText", step.placeholder());
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

            applyFont(AutoCompleteWizard.this.getFont());

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
//            list.setBackground(new Color(0xFBFBF9)); //TODO: Shouldn't be here
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

        void refilter() {
            String typed = field == null ? "" : field.getText();
            int generation = requestGeneration.incrementAndGet();

            // stillWithinCall is true only while optionsAsync() is still executing
            // on this (EDT) call stack — a callback firing before it returns is
            // synchronous and safe to apply directly; one firing later, from any
            // thread, must hop via invokeLater. resolved separately tracks whether
            // the callback has fired at all yet, purely to decide whether to show
            // the "Searching…" placeholder — it must NOT be used for the threading
            // decision, since by the time an async callback fires this thread has
            // long since left the call stack.
            AtomicBoolean stillWithinCall = new AtomicBoolean(true);
            AtomicBoolean resolved = new AtomicBoolean(false);
            step.optionsAsync(chainBefore, typed, options -> {
                resolved.set(true);
                if (stillWithinCall.get()) {
                    applyOptions(generation, options);
                } else {
                    SwingUtilities.invokeLater(() -> applyOptions(generation, options));
                }
            });
            stillWithinCall.set(false);

            if (!resolved.get()) {
                showLoadingPlaceholder(generation);
            }
        }

        /** Shows a non-selectable "Searching…" row, but only if the pane is currently
         *  empty — if stale results from a previous keystroke are still showing,
         *  leave them visible instead of covering them with the placeholder. */
        private void showLoadingPlaceholder(int generation) {
            if (generation != requestGeneration.get() || !model.isEmpty()) {
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

            model.addElement(WizardOption.loading());
            selectFirstSelectable(); // finds nothing selectable — clears selection
            if (isActivePane()) {
                refreshPreview();
            }
        }

        /** Stops the dots timer. Safe to call even if never started. */
        void stopLoadingAnimation() {
            if (dotsTimer != null) {
                dotsTimer.stop();
            }
        }

        /** Applies a batch of options unless a later keystroke has already superseded it. */
        private void applyOptions(int generation, List<WizardOption> options) {
            if (generation != requestGeneration.get()) {
                return; // stale — a newer request is already in flight or applied
            }

            stopLoadingAnimation();
            model.clear();
            for (WizardOption option : options) {
                model.addElement(option);
            }

            boolean hasIcons = false;
            for (int i = 0; i < model.size(); i++) {
                if (model.get(i).icon() != null) {
                    hasIcons = true;
                    break;
                }
            }
            list.putClientProperty("pw.hasIcons", hasIcons);

            selectFirstSelectable();
            if (isActivePane()) {
                refreshPreview();
            }
        }

        private void selectFirstSelectable() {
            for (int i = 0; i < model.size(); i++) {
                if (model.get(i).isSelectable()) {
                    list.setSelectedIndex(i);
                    return;
                }
            }
            list.clearSelection();
        }

        WizardOption selectedOption() {
            return list.getSelectedValue();
        }

        void moveSelection(int direction) {
            int size = model.size();
            if (size == 0) {
                return;
            }
            int index = list.getSelectedIndex();
            for (int i = index + direction; i >= 0 && i < size; i += direction) {
                if (model.get(i).isSelectable()) {
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
                AutoCompleteWizard.this.requestFocusInWindow();
            }
        }
    }

    // -------------------------------------------------------------- renderer

    private static final class OptionRenderer extends DefaultListCellRenderer {
        private static final int ICON_SIZE = 16;
        private static final int ICON_GAP  = 6;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean selected, boolean focused) {
            WizardOption option = (WizardOption) value;
            if (option.isSeparator()) {
                JSeparator sep = new JSeparator();
                sep.setPreferredSize(new Dimension(1, 2));
                sep.setForeground(borderColor());
                return sep;
            }
            if (option.isLoading()) {
                Object dots = list.getClientProperty("pw.loadingDots");
                int dotCount = dots instanceof Integer ? (Integer) dots : 1;

                JLabel c = (JLabel) super.getListCellRendererComponent(
                        list, option.label() + ".".repeat(dotCount), index, false, false);
                c.setIcon(null);
                c.setBorder(BorderFactory.createEmptyBorder(4, 9, 4, 9));
                c.setForeground(new Color(150, 150, 150)); // quieter than normal rows — it's a status, not a choice
                c.setOpaque(true);
                return c;
            }

            JLabel c = (JLabel) super.getListCellRendererComponent(
                    list, option.label(), index, selected, false);
            c.setIcon(option.icon());
            c.setIconTextGap(ICON_GAP);

            // if this pane has any icons, pad icon-less rows so text lines up
            boolean paneHasIcons = Boolean.TRUE.equals(list.getClientProperty("pw.hasIcons"));
            int indent = (paneHasIcons && option.icon() == null) ? ICON_SIZE + ICON_GAP : 0;

            c.setBorder(BorderFactory.createEmptyBorder(4, 9 + indent, 4, 9));
            c.setForeground(new Color(EditorConstants.TEXT_EDITOR_FONT_COLOUR,
                    EditorConstants.TEXT_EDITOR_FONT_COLOUR, EditorConstants.TEXT_EDITOR_FONT_COLOUR));
            c.setOpaque(true);
            return c;
        }
    }
}