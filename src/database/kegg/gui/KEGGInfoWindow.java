package database.kegg.gui;

import javax.swing.JOptionPane;

import gui.MainWindow;

public class KEGGInfoWindow {

	public KEGGInfoWindow(){
		
		String instructions =
	        "<html>"+
	        "<h3>The KEGG search window</h3>"+
	        "<ul>"+
	        
	        "<li>KEGG (Kyoto Encyclopedia of Genes and Genomes) is a collection of online databases<p>"+
	        "dealing with genomes, enzymatic pathways, and biological chemicals. <p>"+
	        "The PATHWAY database records networks of molecular interactions in the cells,<p>"+
	        "and variants of them specific to particular organisms.<p>"+
	        "<li>The search window is a query mask that gives the user the<p>"+
	        "possibilty to consult the KEGG database for information of interest. <p>"+
	        "<li>By searching the database for one of the following attributes pathway,<p>"+ 
	        "organism, enzyme, gene or compound the database will be checked <p>"+
	        "for all pathways that meet the given demands. As a result a list of possible pathways <p>"+
	        "will be displayed to the user. In the following step the user can choose either one or more <p>"+
	        "pathways of interest.<p>"+
	        
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
		
		MainWindow w = MainWindow.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"KEGG Information",1);
		
	}
	
}
