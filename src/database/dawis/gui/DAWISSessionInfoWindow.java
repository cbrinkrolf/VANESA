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
 * The DAWISInfoWindow that pops up by clicking on the 'i'-icon
 */
public class DAWISSessionInfoWindow {

	public DAWISSessionInfoWindow(){
		
		String instructions =
		    
			 "<html>"+
		        "<h3>The DAWIS Session Load window</h3>"+
		        
		        "<li>Here you can create a network from elements, you have choosen in DAWIS-M.D." +
		        
		        "</ul>"+
		        "<h3>What is DAWIS-M.D.?</h3>"+	
		        "<ul>"+
		       		        
		        "<li>DAWIS-M.D. is a datawarehouse that contains entries of multiple databases:<p>"+
		        "Transpath, Transfac, KEGG, Brenda, OMIM, Gene Ontology, UniProt, ENZYME, HPRD and EMBL<p>"+
		        "It is maintained and developed at the Bioinformatics Department  <p>"+
		        "at the Bielefeld University and is reachable at :<p>"+
		        
		        "<li> http://agbi.techfac.uni-bielefeld.de/DAWISMD/"+
		       
		        "</ul>"+
		        "<h3>How can I create networks from DAWIS-M.D.?</h3>"+	
		        "<ul>"+
		        
		        "<li>Go to the website and choose element of interest.<p>"+
		        
		        "<li>On the right site you'll see a hyperlink <font color=\"#000099\"> VANESA </font><p>"+
		        "It unfolds by clicking on it.<p>"+
		        
		        "<li>For loading elements in VANESA you have to choose some.<p>"+
		        "You can load all elements of the website in one step by clicking on <p>"+
		        "<font color=\"#000099\"> Add Node </font>, or choose some of them under the <font color=\"#000099\"> Expert </font> view <p>"+
		         
		        "<li>Click on the <font color=\"#000099\"> Create Graph </font> Button.<p>"+
		        
		        "<li>Now the data of your interest is loaded into the database and a network<p>"+
		        "can be created. Copy the <font color=\"#000099\"> Session ID </font> of your session<p>"+
		        "(you find it in the same VANESA-window) and paste it into this search form."+
		        
		        ""+
		        "<ul>"+   
		     "</html>";
		
		MainWindow w = MainWindowSingelton.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"DAWIS Session-ID Informations",1);
		
	}
}
