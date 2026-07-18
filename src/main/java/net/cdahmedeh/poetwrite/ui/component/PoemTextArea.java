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

import lombok.Setter;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.ui.constant.EditorConstants;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Extension of RSyntaxTextArea because we're going to be overriding quite a fwe
 * of the rendering it does that it does not provide via it's externally facing
 * API.
 *
 * Some examples
 * - Increasing the line spacing between lines.
 * - Eventually having a custom drawn gutter for showing more than just a single
 *   string with line number of syllable count. But rhyming pattern as well and
 *   a few things.
 */
public class PoemTextArea extends RSyntaxTextArea {
    private float lineSpacingFactor = EditorConstants.DEFAULT_LINE_SPACING;

    // Set by the tooltip supplier on each hover; null when nothing matched.
    @Setter
    private Word hoveredWord;

    /**
     * RSyntaxTextArea asks for the number of rows and columns of the text area.
     * But I'm not sure if it actually does anything because the window can be
     * resized.
     *
     * TODO: Figure out what rows and cols do.
     */
    public PoemTextArea(int rows, int cols) {
        super(rows, cols);
    }

    /**
     * Increases line height.
     */
    @Override
    public int getLineHeight() {
        return Math.round(super.getLineHeight() * lineSpacingFactor);
    }

    /**
     * Positions the hover tooltip so that it is horizontally aligned with the
     * hovered word, and sits one line (plus a small gap) below the line the
     * word is on.
     */
    @Override
    public Point getToolTipLocation(MouseEvent e) {
        if (hoveredWord == null) {
            return null; // default placement
        }
        try {
            Border b = UIManager.getBorder("ToolTip.border");
            Insets in = (b != null)
                    ? b.getBorderInsets(this)
                    : new Insets(0, 0, 0, 0);
            Rectangle2D r = modelToView2D(hoveredWord.getStart());

            int lineHeight = getLineHeight(); // includes lineSpacingFactor

            int x = (int) Math.round(r.getX())
                    - in.left
                    - EditorConstants.TOOLTIP_HTML_FUDGE_X;
            int y = (int) Math.round(r.getY())
                    + lineHeight
                    + Math.round(lineHeight * EditorConstants.TOOLTIP_LINE_GAP_FACTOR);

            return new Point(x, y);
        } catch (BadLocationException ex) {
            return null;
        }
    }
}