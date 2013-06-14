package gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class CoverPanel extends JPanel {

	private Color color;

	public CoverPanel(Color color) {
		   this.color = color;
	}

	@Override
	public void paintComponent(Graphics g) {
		   g.setColor(color);	
	}	
}
