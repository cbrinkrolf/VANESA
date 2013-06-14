package gui.algorithms;

import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenSize {

	private double height;
	private double width;
	
	public double getheight(){
		return height;
	}
	
	public double getwidth(){
		return width;
	}
	
	public ScreenSize(){
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		height = d.getHeight();
		width = d.getWidth();
	}
}
