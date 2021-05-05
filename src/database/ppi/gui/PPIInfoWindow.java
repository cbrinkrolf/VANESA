package database.ppi.gui;

import javax.swing.JOptionPane;

import gui.MainWindow;

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
		
		MainWindow w = MainWindow.getInstance();
		JOptionPane.showMessageDialog(w.getFrame(), instructions,"PPI Information",1);
	}
}
