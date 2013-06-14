package database.brenda.gui;

import gui.MainWindow;
import gui.MainWindowSingelton;

import javax.swing.JOptionPane;

public class BrendaInfoWindow {

	public BrendaInfoWindow(){
		
		String instructions =
		    
			 "<html>"+
		        "<h3>The BRENDA search window</h3>"+
		        "<ul>"+
		        
			
	        "<li>BRENDA is the comprehensive enzyme information database.<p>"+
	        "It is maintained and developed at the Institute of Biochemistry <p>"+
	        "at the University of Cologne. Data on enzyme functions are extracted <p>"+
	        "directly from the primary literature and stored in this database.<p>"+
	        "<li>The search window is a query mask that gives the user the<p>"+
	        "possibilty to consult the BRENDA database for information of interest. <p>"+
	        "<li>By searching the database for one of the following attributes EC-Number,<p>"+ 
	        "name, substrate, product or organism the database will be checked <p>"+
	        "for all enzymes that meet the given demands. As a result a list of possible enzymes <p>"+
	        "will be displayed to the user. In the following step the user can choose either one or more <p>"+
	        "enzymes of interest he would like to examine in detail.<p>"+
	        
	        "<li>Additionally the software will try to calculate a possible pathway with a given search depth.<p>"+
	        "The calculation is an iterative procedure in which possible connections to other enzymes<p>" +
	        "will be checked<p>"+
	     
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
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"BRENDA Informations",1);
		
	}
	
}

