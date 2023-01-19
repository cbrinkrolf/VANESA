package gui;

import javax.swing.JOptionPane;

public class AboutWindow {
	public AboutWindow(){
		String instructions =
	        "<html>"+
	        "<h3>About</h3>"+
	        "VANESA is a network editor software to search, create and examine biological pathways.<p>"
	        +"It is developed at Bielefeld University (Germany).<p>"
	        +"For further details, please visit the VANESA GitHub website: https://github.com/cbrinkrolf/VANESA/<p>"+
	        "or get in touch with the Bioinformatics Department.<p><p>"
	        +"Contact Details<p><p>"

	        +"Christoph Brinkrolf mailto:christoph.brinkrolf@uni-bielefeld.de<p>"
	        +"Benjamin Kormeier mailto:bkormeie@techfak.uni-bielefeld.de<p><p>"

	        +"Bielefeld University<p>"
	        +"Faculty of Technology<p>"
	        +"Bioinformatics Department<p>"
	        +"PO Box 10 01 31<p>"
	        +"D-33501 Bielefeld<p><p>"+

	        "</html>";
		MainWindow w = MainWindow.getInstance();
		JOptionPane.showMessageDialog(w.getFrame(), instructions, "About", JOptionPane.INFORMATION_MESSAGE);
	}
}
