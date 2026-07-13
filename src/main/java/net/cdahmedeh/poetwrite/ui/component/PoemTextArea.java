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

import net.cdahmedeh.poetwrite.ui.constant.UIConstants;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

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
    private float lineSpacingFactor = UIConstants.DEFAULT_LINE_SPACING;

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
}
