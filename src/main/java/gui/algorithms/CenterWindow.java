package gui.algorithms;

import javax.swing.JFrame;
import javax.swing.JWindow;

public class CenterWindow {
    private JWindow window;
    private JFrame frame;

    public CenterWindow(JWindow window) {
        this.window = window;
    }

    public CenterWindow(JFrame frame) {
        this.frame = frame;
    }

    public void centerWindow(int width, int height) {
        final ScreenSize screen = new ScreenSize();
        if (frame != null) {
            frame.setLocation((screen.width / 2) - width / 2, (screen.height / 2) - height / 2);
        } else if (window != null) {
            window.setLocation((screen.width / 2) - width / 2, (screen.height / 2) - height / 2);
            window.pack();
        }
    }
}
