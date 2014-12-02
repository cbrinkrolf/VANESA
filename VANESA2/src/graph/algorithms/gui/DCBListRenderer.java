/**
 * 
 */
package graph.algorithms.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * @author Britta Niemann
 *
 */
public class DCBListRenderer extends JLabel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList arg0, Object arg1,
			int arg2, boolean isSelected, boolean cellHasFocus) {
		
		DCBClusterLabel value = (DCBClusterLabel) arg1;
		this.setText(value.getLabel());
	    
	    this.setOpaque(true);
	    
	    // Element aus der Liste ist markiert
	    if(isSelected){
	      // Schriftfarbe
	      // UIManager.getColor("List.selectionForeground") gibt die 
	      // Standard Schriftfarbe f�r ein markiertes Listen Element zur�ck
	      this.setForeground(value.getColor());
	      // Hintergrund
	      // UIManager.getColor("List.selectionBackground") gibt die 
	      // Standard Hintergrundfarbe f�r ein markiertes Listen Element zur�ck      
	      this.setBackground(UIManager.getColor("List.selectionBackground"));
	    }
	    // Element aus der Liste ist nicht markiert
	    else{
	      // Schriftfarbe
	      this.setForeground(UIManager.getColor("List.foreground"));
	      // Hintergrund
	      this.setBackground(UIManager.getColor("List.background"));
	    }
		
		return this;
	}
	
	
	
	
	
}
