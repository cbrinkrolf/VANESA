package gui;

import javax.swing.JOptionPane;

import util.VanesaUtility;

public class AboutWindow {
	public AboutWindow(){
		// Get current size of heap in bytes.
		long heapSize = Runtime.getRuntime().totalMemory();

		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.
		// Any attempt will result in an OutOfMemoryException.
		long heapMaxSize = Runtime.getRuntime().maxMemory();

		// Get amount of free memory within the heap in bytes. This size will 
		// increase after garbage collection and decrease as new objects are created.
		long heapFreeSize = Runtime.getRuntime().freeMemory();
				
		String instructions =
	        "<html>"+
	        "<h3>About</h3>"+
	        "VANESA is a network editor software to search, create and examine biological pathways.<p>"
	        +"It is developed at Bielefeld University (Germany).<p>"
	        +"For further details, please visit the VANESA GitHub website: https://github.com/cbrinkrolf/VANESA/<p>"+
	        "or get in touch with the Bioinformatics Department.<p><p>"
	        +"Memory overview<br>"
	        +"Size of current memory usage: "+VanesaUtility.formatSize(heapSize)+"<br>"
	        +"Size of maximum memory: " + VanesaUtility.formatSize(heapMaxSize)+"<br>"
	        +"Size of free memory: "+VanesaUtility.formatSize(heapFreeSize)+"<br>"
	        +"<p><p>Contact Details<p><p>"

	        +"Christoph Brinkrolf mailto:cbrinkro@gmail.com<p>"
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
