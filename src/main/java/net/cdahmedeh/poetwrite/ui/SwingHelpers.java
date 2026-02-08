package net.cdahmedeh.poetwrite.ui;

import javax.swing.*;

public class SwingHelpers {
    public static void dispatch(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
}
