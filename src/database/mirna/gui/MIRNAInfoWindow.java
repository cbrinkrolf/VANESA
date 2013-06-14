package database.mirna.gui;

import gui.MainWindow;
import gui.MainWindowSingelton;

import javax.swing.JOptionPane;

public class MIRNAInfoWindow {

	public MIRNAInfoWindow() {
		String instructions =
	        "<html>"+
	        "<h3>The miRNA search window</h3>"+
	        "<ul>"+
	        
	        "<li>Through the miRNA search window you can access homosapien microRNA information<p>"+
	        "available in miRBase and TarBase.<p>"+
	        "MiRBase is a biological database that acts as an archive of microRNA sequences and<p>"+
	        "annotations and TarBase is a comprehensive database of experimentally supported animal<p>"+
	        "microRNA targets.<p>"+
	        "<li>The search window is a query mask that gives the user the possibilty to consult the miRNA<p>"+
	        "database for information of interest. <p>"+
	        "<li>By searching the database for one of the following attributes name, accession or sequence<p>"+ 
	        "the database will be checked for all pathways that meet the given demands.<p>"+
	        "As a result a list of possible pathways will be displayed to the user. In the following step the<p>"+
	        "user can choose either one or more pathways of interest.<p>"+	        
	        "</ul>"+
	        
	        "</ul>"+
	        "<h3>How to use the search form</h3>"+	
	        "<ul>"+
	        "<li>To search for one attribute simply type "+
	        "in the attribute of interest.  <p><font color=\"#000099\">Example: Homosapien </font><p>"+
	        "<li>To search for two attributes in one field use the ' & ' char "+
	        "to connect them.  <p><font color=\"#000099\">Example: 5.4.2.1 & 5.3.2.1 </font><p>"+
	        "<li>To search for either one or another attribute in one field "+
	        "use the ' | ' char to connect them. <p><font color=\"#000099\">Example: 5.4.2.1 | 5.3.2.1 </font><p>"+
	        "<li>To search for data where given attributes should not appear "+
	        "put an exclamation mark before that attribute. <p><font color=\"#000099\">Example: !homo </font><p>"+
	        ""+
	        "<ul>"+   
	        
	        
	        "</html>";
		
		MainWindow w = MainWindowSingelton.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"KEGG Information",1);
		
	}

}
