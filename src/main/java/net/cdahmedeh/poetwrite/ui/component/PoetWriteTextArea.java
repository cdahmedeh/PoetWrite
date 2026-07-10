package net.cdahmedeh.poetwrite.ui.component;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class PoetWriteTextArea extends RSyntaxTextArea {
    private float lineSpacingFactor = 1.10f;

    public PoetWriteTextArea(int rows, int cols) {
        super(rows, cols);
    }

    public void setLineSpacingFactor(float factor) {
        this.lineSpacingFactor = factor;
        firePropertyChange("font", null, getFont());
        revalidate();
        repaint();
    }

    @Override
    public int getLineHeight() {
        return Math.round(super.getLineHeight() * lineSpacingFactor);
    }
}
