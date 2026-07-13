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

import net.cdahmedeh.poetwrite.ui.constant.AppearanceConstants;

import javax.swing.*;
import java.awt.*;

/**
 * A custom icon that spins like a throbber. Quick and dirty code. Basically
 *  a throwaway.
 *
 * In a previous prototype, the spinner was just a GIF. But it's not scalable,
 * and have no control on how it behaves.
 *
 * Support FlatSVGIcon so we can use SVG as we're in the days of HiDPI displays.
 *
 * Used for the loading throbber in the status bar.
 */
public class SpinningIcon implements Icon {
    private final Icon delegate;
    private double angle = AppearanceConstants.SPINNER_STARTING_ANGLE;
    private final Timer timer;

    public SpinningIcon(Icon delegate, JComponent host) {
        this.delegate = delegate;
        this.timer = new Timer(AppearanceConstants.SPINNER_ANIMATION_INTERVAL, e -> {
            angle = (angle + AppearanceConstants.SPINNER_ROTATE_INCREMENTS) % 360;
            host.repaint();
        });
    }

    public void start() { timer.start(); }
    public void stop()  { timer.stop(); angle = AppearanceConstants.SPINNER_STARTING_ANGLE; }

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
