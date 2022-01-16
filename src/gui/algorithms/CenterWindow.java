package gui.algorithms;


import javax.swing.JFrame;
import javax.swing.JWindow;

public class CenterWindow {
	
	private JWindow window; 
	private JFrame frame; 
	
	
	public CenterWindow(JWindow window){		
		this.window = window;
	}
	
	public CenterWindow(JFrame frame){		
		this.frame = frame;
	}
	

	public void centerWindow(int width, int height){
		
		ScreenSize screen = new ScreenSize();
		int screenHeight = (int)screen.getheight();
		int screenWidth = (int)screen.getwidth();
		
		if(frame != null){
			frame.setLocation((screenWidth/2) - width/2, (screenHeight/2) -height/2);
			
		} else if (window != null){
			window.setLocation((screenWidth/2) - width/2, (screenHeight/2) -height/2);
			window.pack();
		}
	}
}
