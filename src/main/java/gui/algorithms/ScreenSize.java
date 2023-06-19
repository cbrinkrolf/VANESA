package gui.algorithms;

import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenSize {
    public final int width;
    public final int height;

    public ScreenSize() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) d.getWidth();
        height = (int) d.getHeight();
    }
}
