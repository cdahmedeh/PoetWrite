package net.cdahmedeh.poetwrite.ui.component;

import javax.swing.*;
import java.awt.*;

public class SpinningIcon implements Icon {
    private final Icon delegate;
    private double angle = 0;
    private final Timer timer;

    public SpinningIcon(Icon delegate, JComponent host) {
        this.delegate = delegate;
        this.timer = new Timer(100, e -> {
            angle = (angle + 20) % 360; // degrees per tick
            host.repaint();
        });
    }

    public void start() { timer.start(); }
    public void stop()  { timer.stop(); angle = 0; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            int cx = x + getIconWidth() / 2;
            int cy = y + getIconHeight() / 2;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.rotate(Math.toRadians(angle), cx, cy);
            delegate.paintIcon(c, g2, x, y);
        } finally {
            g2.dispose();
        }
    }

    @Override public int getIconWidth()  { return delegate.getIconWidth(); }
    @Override public int getIconHeight() { return delegate.getIconHeight(); }
}
