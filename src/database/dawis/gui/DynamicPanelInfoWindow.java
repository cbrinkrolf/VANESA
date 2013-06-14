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
 * The DynamicPanelInfoWindow that pops up by clicking on the 'i'-icon
 */
public class DynamicPanelInfoWindow {

	public DynamicPanelInfoWindow(){
		
		String instructions =
		    
			 "<html>"+
		        "<h3>Use of the table</h3>"+
		        "<ul>"+
		        
			
	        "<li>The table shows you the relations between elements.<p>"+
	        "They are dependent on the database, where they are stored.<p>"+
	        "Contingent on the type of the start element there are 2, 3 or 4 depth layers.<p>"+
	        "The elements of the depth 1 are always reachable.<p>"+
	        "For the elements from deeper layer, you have to choose at least one<p>"+
	        "<font color = \"#990000\">related element</font> from previos layer.<p>"+
	        "Which elements are related to the element of your interest <p>"+
	        "shows you the program.<p><p><p>"+
	        
	        
	        "<font color=\"#000099\">Example</font><p><p>"+
	        
	        "You want to see a special protein and its related enzymes as well.<p><p>"+
	        
	        "<li>Protein is your start element. So you needn't to choose Protein in the table.<p>"+
	        "<li>Simply choose Enzyme and click on OK-Button<p>"+
	        "<li>You'l see a small message with an afford to choose a gene or a gene ontology element,<p>"+
	        "because this elements are directly related to proteins accordingly the database<p>"+
	        "<li>Now, that you know, which elements are related to proteins, <p>"+
	        "choose one of them and click on the OK-Button one more time.<p>"+    
	        "The program does the rest.<p>"+ 
	       
	        ""+
	        "<ul>"+        
	        "</html>";
		
		MainWindow w = MainWindowSingelton.getInstance();
		JOptionPane.showMessageDialog(w.returnFrame(), instructions,"Use of depth",1);
		
	}
}
