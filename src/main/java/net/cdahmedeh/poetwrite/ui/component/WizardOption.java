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

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

import com.formdev.flatlaf.extras.FlatSVGIcon; //TODO: trick the classloader

/**
 * One selectable row in a wizard pane.
 *
 * <ul>
 *   <li>{@code label}   — what the list shows.</li>
 *   <li>{@code value}   — the clause this option contributes to the {@link WizardChain}.</li>
 *   <li>{@code next}    — factory for the step that follows once this option is
 *                         committed; {@code null} means terminal (chain complete).</li>
 *   <li>{@code preview} — optional supplier of a component shown to the right of
 *                         the active pane while this option is highlighted
 *                         (line text, dictionary entry, …). Called lazily, on the EDT.</li>
 * </ul>
 */
public final class WizardOption {

    /** Creates the step that follows this option. Receives the chain including this option. */
    @FunctionalInterface
    public interface NextStepFactory {
        WizardStep create(WizardChain chainSoFar);
    }

    private final String label;
    private final Object value;
    private final NextStepFactory next;
    private final Supplier<JComponent> preview;
    private final boolean separator;
    private final boolean loading;
    private Icon icon;

    private WizardOption(String label, Object value, NextStepFactory next,
                         Supplier<JComponent> preview, boolean separator, boolean loading) {
        this.label = label;
        this.value = value;
        this.next = next;
        this.preview = preview;
        this.separator = separator;
        this.loading = loading;
    }

    /** Non-selectable divider row (the "----" in the first pane of the mockup). */
    public static WizardOption separator() {
        return new WizardOption("", null, null, null, true, false);
    }

    /** Non-selectable "Searching" row shown while an async step's result is pending. */
    public static WizardOption loading() {
        return new WizardOption("Searching", null, null, null, false, true);
    }

    /** Option that advances the wizard to another step. */
    public static WizardOption next(String label, Object value, NextStepFactory next) {
        return new WizardOption(label, value, next, null, false, false);
    }

    public static WizardOption next(String label, Object value,
                                    Supplier<JComponent> preview, NextStepFactory next) {
        return new WizardOption(label, value, next, preview, false, false);
    }

    /** Option that completes the chain when committed. */
    public static WizardOption terminal(String label, Object value) {
        return new WizardOption(label, value, null, null, false, false);
    }

    public static WizardOption terminal(String label, Object value, Supplier<JComponent> preview) {
        return new WizardOption(label, value, null, preview, false, false);
    }

    private static final float ICON_OPACITY = 0.50f; // ~25% transparent

    public WizardOption icon(String svgResourcePath) {
        String name = svgResourcePath.startsWith("/")
                ? svgResourcePath.substring(1)
                : svgResourcePath;
        FlatSVGIcon svg = new FlatSVGIcon(name, 16, 16);
        svg.setColorFilter(new FlatSVGIcon.ColorFilter(color ->
                new Color(color.getRed(), color.getGreen(), color.getBlue(),
                        Math.round(color.getAlpha() * ICON_OPACITY))));
        this.icon = svg;
        return this;
    }

    public WizardOption icon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public Icon icon() {
        return icon;
    }

    public String label()                 { return label; }
    public Object value()                 { return value; }
    public boolean isSeparator()          { return separator; }
    public boolean isLoading()            { return loading; }
    /** False for separators and the loading placeholder — the only rows a user can pick. */
    public boolean isSelectable()         { return !separator && !loading; }
    public boolean isTerminal()           { return next == null; }
    public Supplier<JComponent> preview() { return preview; }

    WizardStep createNext(WizardChain chainIncludingThis) {
        return next == null ? null : next.create(chainIncludingThis);
    }

    @Override
    public String toString() {
        return label;
    }
}