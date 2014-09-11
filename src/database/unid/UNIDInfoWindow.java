package database.unid;

import gui.MainWindow;
import gui.MainWindowSingleton;

import javax.swing.JOptionPane;

/**
 * 
 * @author mlewinsk
 * June 2014
 */
public class UNIDInfoWindow {

	public UNIDInfoWindow() {
		String instructions =
	        "<html>"+
	        "<h3>The UNID search window</h3>"+
	        "<ul>"+

	        "<li>UNID stands for: Unified Networks Interaction Database.<p>"+
	        
	        "<li>Through the UNID search window you can access various interaction data<p>"+
	        "available in a local Graph-Database.<p>"+
	        	        
	        "</ul>"+
	        	        
	        "</html>";
		
		MainWindow w = MainWindowSingleton.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"UNID Information",1);
		
	}

}
