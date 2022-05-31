package gui;

import javax.swing.JOptionPane;

public class AboutWindow {

	public AboutWindow(){
		
		String instructions =
	        "<html>"+
	        "<h3>About</h3>"+
		     
	        "The network editor is a software to search," 
	        +"to create and to examine biological pathways.<p>"
	        +"It is developed by the Univerity of Bielefeld (Germany).<p>"
	        +"For further details, please visit the VANESA GitHub website: https://github.com/cbrinkrolf/VANESA/<p>"+
	        "or get in touch with the Bioinformatics Department.<p><p>"
	        +"Contact Details<p><p>"
	        
	        +"Christoph Brinkrolf mailto:christoph.brinkrolfk@uni-bielefeld.de<p>"
	        +"Benjamin Kormeier mailto:bkormeie@techfak.uni-bielefeld.de<p><p>"
	        
	        +"Bielefeld University<p>"
	        +"Faculty of Technology<p>"
	        +"Bioinformatics Department<p>"
	        +"PO Box 10 01 31<p>"
	        +"D-33501 Bielefeld<p><p>"+
	        
	        "</html>";
		
		MainWindow w = MainWindow.getInstance();
		JOptionPane.showMessageDialog(w.getFrame(), instructions,"About",1);
		
	}
	
}
