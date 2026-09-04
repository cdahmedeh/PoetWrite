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
import net.cdahmedeh.poetwrite.annotation.Helped;
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
 * - Handling of the word that is being highlighted.
 */
public class PoemTextArea extends RSyntaxTextArea {
    private float lineSpacingFactor = EditorConstants.DEFAULT_LINE_SPACING;

    // The word that the user is hovering over.
    // TODO: I don't like that this part of the component, should be the
    //       responsibility of the view or even the model to do this.
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

    /**
     * Nudges Swing into asking the tooltip supplier again.
     *
     * Used when a hover analysis comes back from the TaskBus after the tooltip
     * is already on screen showing its loading message. There is no public way
     * to poke text into a tooltip that is already up, so instead we replay the
     * last mouse move at the ToolTipManager. It re-asks the supplier, gets the
     * finished text this time, and swaps the popup.
     *
     * Deliberately NOT a custom tooltip component. This is still Swing's own
     * JToolTip with the post-it styling from the UIManager, we are just asking
     * it a second time.
     *
     * The timing does not change: the tooltip is already visible when this
     * runs, so ToolTipManager re-shows it immediately rather than restarting
     * its delay.
     */
    @Helped("I couldn't figure out this quirk at all because I didn't know how" +
            "for the tooltip manager to update. Again, I didn't want to use " +
            "a custom component.")
    public void refreshToolTip(MouseEvent event) {
        if (event == null) {
            return;
        }

        // The pointer may have left the editor entirely while the task was
        // running. Leaving does not fire a move, so the view still thinks that
        // word is hovered. Replaying the move here would pop a tooltip up over
        // an editor nobody is pointing at.
        if (getMousePosition() == null) {
            return;
        }

        ToolTipManager.sharedInstance().mouseMoved(event);
    }
}