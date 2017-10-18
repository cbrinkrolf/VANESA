package gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import gui.algorithms.CenterWindow;

public class LoadScreen extends JFrame {

	private static final long serialVersionUID = 1L;
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	JWindow window = new JWindow();
	JLabel label;

	public LoadScreen() {
		
		String markUp = "<html><font text-align = left>"
	      + "Loading Graph ... </font></html>";
		
		ImageIcon icon = createImageIcon(
		".." + File.separator + "Graph- Editor" + File.separator + "pictures" + File.separator + "Intro.gif", "intro");	
		
		label = new JLabel(markUp, icon, SwingConstants.CENTER);
		label.setBorder(BorderFactory.createRaisedBevelBorder());
		label.setVerticalTextPosition(SwingConstants.BOTTOM);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setBackground(Color.BLACK);
		
	}

	public void openWindow() {

		window.getContentPane().add(label, BorderLayout.CENTER);

		Dimension labelSize = label.getPreferredSize();
		int labelWidth = labelSize.width;
		int labelHeight = labelSize.height;

		CenterWindow center = new CenterWindow(window);
		center.centerWindow(labelWidth, labelHeight);

		window.setVisible(true);
		window.pack();
		window.setAlwaysOnTop(true);
	}

	public void closeWindow() {
		window.setVisible(false);
	}

	protected static ImageIcon createImageIcon(String path, String description) {
		
		File file = new File(path);
		if (file.exists()) {
			return new ImageIcon(path, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
