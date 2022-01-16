package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import gui.algorithms.CenterWindow;
import gui.images.ImagePath;

public class IntroScreen {

	private JWindow window = new JWindow();
	private JLabel label;

	public IntroScreen() {
		ImagePath imagePath = ImagePath.getInstance();
		String markUp = "<html><font text-align = center>" + "Launching Program ...</font></html>";

		label = new JLabel(new ImageIcon(imagePath.getPath("intro.png")), SwingConstants.CENTER);
		label.setBorder(BorderFactory.createRaisedBevelBorder());
		label.setVerticalTextPosition(SwingConstants.BOTTOM);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setBackground(Color.BLACK);
		label.setText("<html><p align=\"left\">Loading Module: Main Application</p></html>");

	}

	public void openWindow() {

		window.getContentPane().add(label);
		Dimension labelSize = label.getPreferredSize();

		int labelWidth = labelSize.width;
		int labelHeight = labelSize.height;

		CenterWindow center = new CenterWindow(window);
		center.centerWindow(labelWidth, labelHeight);

		window.setVisible(true);
	}

	public void closeWindow() {
		window.setVisible(false);
	}

	public void setLoadingText(String text) {
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		label.setText("<html><p align=\"left\">Loading Module: " + text + "</p></html>");
	}
}
