package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import gui.algorithms.CenterWindow;

public class IntroScreen {
	private final JWindow window = new JWindow();
	private final JLabel label;

	public IntroScreen() {
		label = new JLabel(ImagePath.getInstance().getImageIcon("intro.png"), SwingConstants.CENTER);
		label.setBorder(BorderFactory.createRaisedBevelBorder());
		label.setVerticalTextPosition(SwingConstants.BOTTOM);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setBackground(Color.BLACK);
		label.setText("<html><p align=\"left\">Loading Module: Main Application</p></html>");
	}

	public void openWindow() {
		window.getContentPane().add(label);
		final Dimension labelSize = label.getPreferredSize();
		final CenterWindow center = new CenterWindow(window);
		center.centerWindow(labelSize.width, labelSize.height);
		window.setVisible(true);
	}

	public void closeWindow() {
		window.setVisible(false);
	}

	public void setLoadingText(final String text) {
		try {
			Thread.sleep(0);
		} catch (InterruptedException ignored) {
		}
		label.setText("<html><p align=\"left\">Loading Module: " + text + "</p></html>");
	}
}
