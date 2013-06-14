package gui;


import gui.algorithms.CenterWindow;
import gui.images.ImagePath;

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

public class IntroScreen extends JFrame {

	Toolkit toolkit = Toolkit.getDefaultToolkit();
	JWindow window = new JWindow();
	JLabel label;
	
	
	public IntroScreen() {
		ImagePath imagePath = ImagePath.getInstance();
		String markUp = "<html><font text-align = center>"
	      + "Launching Program ...</font></html>";
		
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

	public void setLoadingText(String text){
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		label.setText("<html><p align=\"left\">Loading Module: "+text+"</p></html>");
		
	}
	
	protected static ImageIcon createImageIcon(String path, String description) {
		
		File file = new File(path);
		if (file != null) {
			return new ImageIcon(path, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
}
