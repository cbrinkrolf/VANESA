/**
 * 
 */
package graph.algorithms.gui;

import java.awt.Color;

/**
 * @author Britta Niemann
 *
 */
public class DCBClusterLabel {
	String label;
	Color color;
	public DCBClusterLabel(String label, Color color){
		this.label = label;
		this.color = color;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	
}
