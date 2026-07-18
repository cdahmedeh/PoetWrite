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
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

/**
 * DISCLOSURE: some of the code in here was written with the help of Claude.
 *
 * The custom-drawn gutter that PoemTextArea's docs promised.
 *
 * RSyntaxTextArea's built-in Gutter can't render anything beyond the plain
 * line number when line wrap is enabled: its wrapped painting path
 * (LineNumberList.paintWrappedLineNumbers) calls Integer.toString() directly
 * and never consults the LineNumberFormatter, even though getMaxLength() is
 * still used for sizing. Since PoetWrite always wraps, the formatter route
 * only ever produced a wider-but-empty gutter. It also limits us to one
 * string in one colour, which rules out rhyme scheme indicators later.
 *
 * So this component replaces the RSTA gutter entirely. It's installed as
 * the row header of a plain JScrollPane. Row headers share the vertical
 * scroll position with the main viewport and use the same coordinate
 * system as the text area, so each line's label is positioned by asking
 * the text area where that line is (modelToView2D). That also makes line
 * wrap free: a wrapped line gets its label on its first visual row only,
 * matching what RSTA's gutter did.
 *
 * Columns, from left: line number (right-aligned), rhyme pattern letter
 * (left-aligned, coloured per rhyme group in the editor's pastel
 * palette), syllable count (right-aligned). A fixed header strip above the
 * gutter labels the columns (sigma for syllables, a note for rhyme
 * groups); see createHeader() and createTextHeader() for how it plugs
 * into the scroll pane.
 */
public class PoemGutter extends JComponent {
    // Space on the outer edges of the gutter, and between the columns.
    private static final int HORIZONTAL_PADDING = 8;
    private static final int COLUMN_GAP = 12;

    // Breathing room between the divider line and the editor text. Kept
    // inside this component (the divider just moves left) rather than as
    // a margin on the text area, so the current line highlight still runs
    // unbroken across it.
    private static final int DIVIDER_TEXT_GAP = 4;

    // Minimum digits each column is sized for, so the gutter width is
    // stable from startup: the syllable column stays reserved before any
    // analysis has run, and the line number column doesn't shift when the
    // poem crosses 10 lines. Both still grow beyond this if needed.
    private static final int MIN_LINE_NUMBER_DIGITS = 2;
    private static final int MIN_SYLLABLE_DIGITS = 2;

    // Minimum letters the pattern column is sized for. Schemes past 'Z'
    // produce two-letter groups (AA, AB...), which the column grows for.
    private static final int MIN_PATTERN_LETTERS = 1;

    // Header labels: a sum over the syllable column, a musical note over
    // the rhyme column. Kept as strings so font fallback is easy to check.
    private static final String SYLLABLE_HEADER_SYMBOL = "\u03a3";
    private static final String PATTERN_HEADER_SYMBOL = "\u266a";

    private final RSyntaxTextArea textArea;

    // Syllable count per line. Index 0 = line 1. Only read/written on the
    // EDT.
    private List<Integer> syllableCounts = List.of();

    // Rhyme scheme letter per line ("" for lines without one). Index 0 =
    // line 1. Only read/written on the EDT.
    private List<String> pattern = List.of();

    // The column-label strip from createHeader(), if one was made. Kept
    // so refresh() can repaint it when column positions move.
    private JComponent header;

    public PoemGutter(RSyntaxTextArea textArea) {
        this.textArea = textArea;

        setFont(new Font(EditorConstants.DEFAULT_EDITOR_FONT, Font.PLAIN, EditorConstants.DEFAULT_EDITOR_FONT_SIZE));

        // Typing changes line positions, line count (gutter width when
        // crossing 9 -> 10 lines) and total height.
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        // The current line's number is drawn darker, so caret moves need a
        // repaint.
        textArea.addCaretListener(e -> repaint());

        // Resizing the window moves the wrap points, which shifts line
        // positions and the text area's total height without any document
        // change.
        textArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refresh();
            }
        });
    }

    /**
     * Replaces the syllable counts and refreshes. Must be called on the
     * EDT.
     */
    public void setSyllableCounts(List<Integer> syllableCounts) {
        this.syllableCounts = syllableCounts;
        refresh();
    }

    /**
     * Replaces the rhyme pattern letters and refreshes. Must be called on
     * the EDT.
     */
    public void setPattern(List<String> pattern) {
        this.pattern = pattern;
        refresh();
    }

    private void refresh() {
        revalidate();
        repaint();

        // Column positions may have moved even when this component's
        // total width didn't, which wouldn't trigger a corner repaint on
        // its own.
        if (header != null) {
            header.repaint();
        }
    }

    /**
     * Width fits the widest line number, syllable count and pattern
     * letter. Height mirrors the text area so the shared viewport
     * scrolling covers the whole document.
     */
    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(getFont());

        int width = HORIZONTAL_PADDING
                + lineNumberColumnWidth(metrics)
                + syllableColumnWidth(metrics)
                + patternColumnWidth(metrics)
                + HORIZONTAL_PADDING
                + DIVIDER_TEXT_GAP;

        return new Dimension(width, textArea.getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle clip = g2.getClipBounds();

        g2.setColor(textArea.getBackground());
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);

        // Extend the editor's current line highlight across the gutter so
        // the bar reads as one continuous stripe. The row is located from
        // the caret position itself (not the logical line start) so that
        // on a wrapped line the gutter highlights the same visual row the
        // editor does. Like RTextArea, the highlight is suppressed while
        // there's a selection.
        if (textArea.getHighlightCurrentLine()
                && textArea.getSelectionStart() == textArea.getSelectionEnd()) {
            try {
                Rectangle2D caretRect = textArea.modelToView2D(textArea.getCaretPosition());
                if (caretRect != null) {
                    g2.setColor(textArea.getCurrentLineHighlightColor());
                    g2.fillRect(0, (int) caretRect.getY(), getWidth(), (int) caretRect.getHeight());
                }
            } catch (BadLocationException e) {
                // No highlight this paint; the next repaint catches up.
            }
        }

        // Same divider RSTA's gutter drew between numbers and text. It
        // sits DIVIDER_TEXT_GAP short of this component's right edge; the
        // strip past it is the whitespace before the editor text.
        int dividerX = getWidth() - 1 - DIVIDER_TEXT_GAP;
        g2.setColor(new Color(221, 221, 221));
        g2.drawLine(dividerX, clip.y, dividerX, clip.y + clip.height);

        // Match the desktop's font smoothing so the gutter text renders
        // like the editor's.
        Map<?, ?> desktopHints = (Map<?, ?>)
                Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (desktopHints != null) {
            g2.addRenderingHints(desktopHints);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        g2.setFont(getFont());
        FontMetrics metrics = g2.getFontMetrics();

        // The pattern column sits between the line numbers and the
        // syllable counts. It is left-aligned at patternColumnLeft:
        // letters of a rhyme group should line up vertically down the
        // poem, which right-alignment would break once two-letter groups
        // (AA...) appear. The other two columns stay right-aligned at
        // their respective ...Right coordinates.
        int numberColumnRight = HORIZONTAL_PADDING + lineNumberColumnWidth(metrics);
        int patternColumnLeft = numberColumnRight + COLUMN_GAP;
        int syllableColumnRight = numberColumnRight + patternColumnWidth(metrics) + syllableColumnWidth(metrics);

        Element root = textArea.getDocument().getDefaultRootElement();
        int caretLine = textArea.getCaretLineNumber();

        // Skip straight to the first line visible in the clip instead of
        // walking the whole document on every repaint.
        int firstLine = root.getElementIndex(
                textArea.viewToModel2D(new Point(0, clip.y)));

        for (int line = Math.max(0, firstLine); line < root.getElementCount(); line++) {
            Rectangle2D rect;
            try {
                rect = textArea.modelToView2D(root.getElement(line).getStartOffset());
            } catch (BadLocationException e) {
                break;
            }

            if (rect == null) {
                break;
            }
            if (rect.getY() > clip.y + clip.height) {
                break;
            }

            int baseline = (int) rect.getY() + metrics.getAscent();

            String number = String.valueOf(line + 1);
            g2.setColor(line == caretLine
                    ? EditorConstants.GUTTER_CURRENT_LINE_NUMBER_COLOUR
                    : EditorConstants.GUTTER_LINE_NUMBER_COLOUR);
            g2.drawString(number, numberColumnRight - metrics.stringWidth(number), baseline);

            // Empty lines and lines with only notes/asides have zero
            // syllables; painting "0" there is just noise.
            if (line < syllableCounts.size() && syllableCounts.get(line) > 0) {
                String syllables = String.valueOf(syllableCounts.get(line));
                g2.setColor(EditorConstants.GUTTER_SYLLABLE_COLOUR);
                g2.drawString(syllables, syllableColumnRight - metrics.stringWidth(syllables), baseline);
            }

            // Rhyme scheme letter, tinted per group so matching lines can
            // be spotted by colour alone.
            if (line < pattern.size() && pattern.get(line).isEmpty() == false) {
                String letter = pattern.get(line);
                g2.setColor(patternColour(letter));
                g2.drawString(letter, patternColumnLeft, baseline);
            }
        }
    }

    /**
     * The fixed strip above the gutter labelling the columns: a sigma
     * over the syllable counts and a note over the rhyme groups. Install
     * as the scroll pane's UPPER_LEFT_CORNER. The corner inherits its
     * width from the row header and its height from the column header,
     * so createTextHeader() must be installed too - JScrollPane only
     * shows a corner when both neighbouring headers exist.
     */
    public JComponent createHeader() {
        header = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, headerHeight());
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setColor(textArea.getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Continue the gutter's divider through the header.
                g2.setColor(new Color(221, 221, 221));
                int dividerX = getWidth() - 1 - DIVIDER_TEXT_GAP;
                g2.drawLine(dividerX, 0, dividerX, getHeight());

                Map<?, ?> desktopHints = (Map<?, ?>)
                        Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
                if (desktopHints != null) {
                    g2.addRenderingHints(desktopHints);
                } else {
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }

                // Same geometry the rows use, so the labels track their
                // columns as widths change.
                FontMetrics metrics = getFontMetrics(PoemGutter.this.getFont());
                int numberColumnRight = HORIZONTAL_PADDING + lineNumberColumnWidth(metrics);
                int patternColumnLeft = numberColumnRight + COLUMN_GAP;
                int syllableColumnRight = numberColumnRight + patternColumnWidth(metrics) + syllableColumnWidth(metrics);

                g2.setColor(EditorConstants.GUTTER_HEADER_COLOUR);
                drawHeaderSymbol(g2, SYLLABLE_HEADER_SYMBOL, syllableColumnRight, true, getHeight());
                drawHeaderSymbol(g2, PATTERN_HEADER_SYMBOL, patternColumnLeft, false, getHeight());
            }
        };

        return header;
    }

    /**
     * The empty strip above the editor text. It pushes the first line of
     * the poem down by the same blank line as the header, and its
     * presence is what makes JScrollPane display the corner at all.
     */
    public JComponent createTextHeader() {
        return new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, headerHeight());
            }

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(textArea.getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    // One blank editor line, so the poem starts exactly one row lower.
    private int headerHeight() {
        return textArea.getLineHeight();
    }

    /**
     * Draws a header symbol aligned to its column (right-aligned symbols
     * get x as their right edge, left-aligned as their left), vertically
     * centred. Noto Sans is missing some symbol glyphs (the musical note
     * in particular), so fall back to the logical Dialog font - which has
     * a proper fallback chain - when the gutter font can't display one.
     */
    private void drawHeaderSymbol(Graphics2D g2, String symbol, int x, boolean rightAligned, int height) {
        Font gutterFont = getFont();
        Font symbolFont = gutterFont.canDisplayUpTo(symbol) == -1
                ? gutterFont
                : new Font(Font.DIALOG, Font.PLAIN, gutterFont.getSize());

        g2.setFont(symbolFont);
        FontMetrics metrics = g2.getFontMetrics();

        int drawX = rightAligned ? x - metrics.stringWidth(symbol) : x;
        int baseline = (height + metrics.getAscent() - metrics.getDescent()) / 2;

        g2.drawString(symbol, drawX, baseline);
    }

    /**
     * The colour for a rhyme group's letter: the editor's pastel palette,
     * cycled by group index. Derived from the letter itself (A=0, B=1...,
     * AA=26...) rather than list position, so a group keeps its colour
     * even when earlier groups change.
     */
    private Color patternColour(String letter) {
        int index = 0;
        for (int i = 0; i < letter.length(); i++) {
            index = index * 26 + (letter.charAt(i) - 'A' + 1);
        }
        index--;

        Color[] palette = EditorConstants.GUTTER_PATTERN_COLOURS;
        return palette[Math.floorMod(index, palette.length)];
    }

    private int lineNumberColumnWidth(FontMetrics metrics) {
        int digits = String.valueOf(Math.max(1, textArea.getLineCount())).length();
        return Math.max(MIN_LINE_NUMBER_DIGITS, digits) * widestDigit(metrics);
    }

    /**
     * Always reserved, even before any counts exist, so the gutter width
     * doesn't jump once analysis catches up.
     */
    private int syllableColumnWidth(FontMetrics metrics) {
        int maxSyllables = 0;
        for (Integer count : syllableCounts) {
            maxSyllables = Math.max(maxSyllables, count);
        }

        int digits = Math.max(MIN_SYLLABLE_DIGITS, String.valueOf(maxSyllables).length());

        return COLUMN_GAP + digits * widestDigit(metrics);
    }

    /**
     * Sized for the longest letter currently in the scheme, and always
     * reserved so the gutter width is stable before analysis lands.
     */
    private int patternColumnWidth(FontMetrics metrics) {
        int letters = MIN_PATTERN_LETTERS;
        for (String group : pattern) {
            letters = Math.max(letters, group.length());
        }

        return COLUMN_GAP + letters * widestLetter(metrics);
    }

    private int widestDigit(FontMetrics metrics) {
        int widest = 0;
        for (char digit = '0'; digit <= '9'; digit++) {
            widest = Math.max(widest, metrics.charWidth(digit));
        }
        return widest;
    }

    private int widestLetter(FontMetrics metrics) {
        int widest = 0;
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            widest = Math.max(widest, metrics.charWidth(letter));
        }
        return widest;
    }
}
