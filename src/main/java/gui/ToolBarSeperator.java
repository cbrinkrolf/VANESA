package gui;

import java.awt.Dimension;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ToolBarSeperator extends JSeparator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ToolBarSeperator(){
		super(SwingConstants.HORIZONTAL);
		this.setMaximumSize( new Dimension(Integer.MAX_VALUE, 1) );
	}

}
