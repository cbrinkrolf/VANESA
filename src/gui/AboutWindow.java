package gui;

import javax.swing.JOptionPane;

public class AboutWindow {

	public AboutWindow(){
		
		String instructions =
	        "<html>"+
	        "<h3>About</h3>"+
		     
	        "The network editor is a software to search," 
	        +"to create and to examine biological pathways.<p>"
	        +"It was developed by the Univerity of Bielefeld (Germany).<p>"
	        +"For futher details please visit the website http://vanesa.sourceforge.net/<p>"+
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
		
		MainWindow w = MainWindowSingleton.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"About",1);
		
	}
	
}
