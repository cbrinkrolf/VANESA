package gui;

import java.awt.Dimension;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ToolBarSeparator extends JSeparator {
    public ToolBarSeparator() {
        super(SwingConstants.HORIZONTAL);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    }
}
