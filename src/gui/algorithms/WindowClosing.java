package gui.algorithms;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowClosing extends WindowAdapter {
	
private boolean exit;
	
	public WindowClosing(boolean exit){
		this.exit =exit;
	}
	
	@Override
	public void windowClosing(WindowEvent event){
		event.getWindow().setVisible(false);
		event.getWindow().dispose();
		if(exit==true){
			System.exit(0);
		}
	}
}
