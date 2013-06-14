package database.dawis.gui;

import gui.MainWindow;
import gui.MainWindowSingelton;

import javax.swing.JOptionPane;

/**
 * 
 * @author Olga
 *
 */

/*
 * The DAWIS-M.D.InfoWindow that pops up by clicking on the 'i'-icon
 */
public class DAWISInfoWindow {

	public DAWISInfoWindow(){
		
		String instructions =
		    
			 "<html>"+
		        "<h3>The DAWIS-M.D. search window</h3>"+
		        "<ul>"+
		        
			
	        "<li>DAWIS-M.D. is a datawarehouse that contains entries of multiple databases:<p>"+
	        "Transpath, Transfac, KEGG, Brenda, OMIM, Gene Ontology, UniProt, HPRD, ENZYME and EMBL<p>"+
	        "It is maintained and developed at the Bioinformatics Department  <p>"+
	        "at the Bielefeld University. <p>"+
	        
	        "<li>The search window is a query mask that gives the user the<p>"+
	        "possibilty to consult the DAWIS-M.D. datawarehouse for information of interest. <p>"+
	       
	        "<li>By searching the datawarehouse for one of the following attributes object,<p>"+ 
	        "name, id or organism the datawarehouse will be checked <p>"+
	        "for all information that meet the given demands. As a result a list of possible elements <p>"+
	        "will be displayed to the user. In the following step the user can choose either one or more <p>"+
	        "elements of interest he would like to examine in detail.<p>"+
	        
	        "<li>Additionally the software will try to calculate a possible network with a given search depth.<p>"+
	        "The calculation is an iterative procedure in which possible connections to other informatoins<p>" +
	        "will be checked<p>"+
	     
	        "</ul>"+
	        "<h3>How to use the search form</h3>"+	
	        "<ul>"+
	        "<li>To search for one attribute simply choose "+
	        "the object of interest and type in its element-id or name.  <p><font color=\"#000099\">Example: Object Enzyme  </font><p>"+
	        "<font color=\"#000099\"> Element-id 1.5.1.34 </font><p>"+
	        "<li>To search for one special organism activate the radio button. <p>"+
	        "You can also type an organism into the search field too. "+
	        "<p><font color=\"#000099\">Example: Organism Homo sapiens </font><p>"+
	        "<li>To search for all elements, that are related to the element  of interest choose. "+
	        "<p><font color=\"#000099\"> Basic mode </font><p>"+    
	        "<li>To search for special objects, that are related to the element  of interest choose. "+
	        "<p><font color=\"#000099\"> Expert mode </font><p>"+    
	       
	        ""+
	        "<ul>"+        
	        "</html>";
		
		MainWindow w = MainWindowSingelton.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"DAWIS-M.D. Informations",1);
		
	}
}
