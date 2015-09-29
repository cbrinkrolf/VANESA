package database.ppi.gui;

import gui.MainWindow;
import gui.MainWindowSingleton;

import javax.swing.JOptionPane;

public class PPIInfoWindow {

	public PPIInfoWindow() {
		String instructions =
	        "<html>"+
	        "<h3>The PPI search window</h3>"+
	        "<ul>"+

	        "<li>Supports search in HPRD, Mint, IntAct.<p>"+
	        
	        "<li>Select a database and search for a name, alias or AC number.<p>"+
	        	        
	        "</ul>"+
	        	        
	        "</html>";
		
		MainWindow w = MainWindowSingleton.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"PPI Information",1);
	}
}
