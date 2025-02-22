package gui;

import java.awt.*;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ToolBarSeparator extends JSeparator {
	private static final long serialVersionUID = 1L;

	public ToolBarSeparator() {
		super(SwingConstants.VERTICAL);
		setMaximumSize(new Dimension(2, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(2, 2));
		setBackground(Color.BLACK);
	}
}
