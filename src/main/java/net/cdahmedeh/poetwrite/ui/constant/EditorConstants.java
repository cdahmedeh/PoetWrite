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

package net.cdahmedeh.poetwrite.ui.constant;

import java.awt.*;

/**
 * For text editor
 */
public class EditorConstants {
    public static final String DEFAULT_EDITOR_FONT = "Noto Sans";
    public static final int DEFAULT_EDITOR_FONT_SIZE = 14;
    public static final float DEFAULT_LINE_SPACING =  1.10f;

    public static final int TEXT_EDITOR_FONT_COLOUR = 65;
    public static final Color CURRENT_LINE_HIGHLIGHT_COLOUR = new Color(250, 245, 250);
    public static final Color CARET_COLOR = new Color(80, 80, 80);

    public static final Color SYNTAX_PUNCTUATION_COLOUR = new Color(0x856161);
    public static final Color SYNTAX_NOTE_COLOUR = new Color(0x7F8C99);
    public static final Color SYNTAX_ASIDE_COLOUR = new Color(0x5d7a5e);
    public static final Color SYNTAX_BRACKET_COLOUR = new Color(0x9E9E9E);

    public static final Color GUTTER_LINE_NUMBER_COLOUR = new Color(0xE0E0E0);
    public static final Color GUTTER_CURRENT_LINE_NUMBER_COLOUR = new Color(0x909090);
    public static final Color GUTTER_SYLLABLE_COLOUR = new Color(0x8A8A8A);

    public static final Color GUTTER_HEADER_COLOUR =new Color(0x8A8A8A);

    public static final int TOOLTIP_OFFSET_X = 6;
    public static final int TOOLTIP_OFFSET_Y = 6;
    public static final int TOOLTIP_HTML_FUDGE_X = 2;
    public static final float TOOLTIP_LINE_GAP_FACTOR = 0.15f;

    public static final Color[] GUTTER_PATTERN_COLOURS = {
            new Color(0xa599b8),   // lavender
            new Color(0xb6a78a),   // sand
            new Color(0x759595),   // teal
            new Color(0xb68aa7),   // mauve
            new Color(0x93a377),   // moss
            new Color(0x8295b0),   // slate blue
            new Color(0xb78a8e),   // dusty rose
            new Color(0xafa679),   // olive gold
            new Color(0x799f8d),   // sea green
            new Color(0xb89989),   // terracotta
            new Color(0xa78eb3),   // plum
            new Color(0x7fa2a9),   // steel cyan
    };


}
