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
 * Columns, from left: line number (right-aligned), syllable count
 * (right-aligned). Rhyme scheme indicators can become a third column here.
 */
public class PoemGutter extends JComponent {
    // Space on the outer edges of the gutter, and between the two columns.
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
    private final RSyntaxTextArea textArea;

    // Syllable count per line. Index 0 = line 1. Only read/written on the
    // EDT.
    private List<Integer> syllableCounts = List.of();

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

    private void refresh() {
        revalidate();
        repaint();
    }

    /**
     * Width fits the widest line number plus the widest syllable count.
     * Height mirrors the text area so the shared viewport scrolling covers
     * the whole document.
     */
    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(getFont());

        int width = HORIZONTAL_PADDING
                + lineNumberColumnWidth(metrics)
                + syllableColumnWidth(metrics)
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

        // Right edge of each column; both columns are right-aligned.
        int numberColumnRight = HORIZONTAL_PADDING + lineNumberColumnWidth(metrics);
        int syllableColumnRight = numberColumnRight + syllableColumnWidth(metrics);

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
        }
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

    private int widestDigit(FontMetrics metrics) {
        int widest = 0;
        for (char digit = '0'; digit <= '9'; digit++) {
            widest = Math.max(widest, metrics.charWidth(digit));
        }
        return widest;
    }
}
