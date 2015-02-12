package gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

public class ToolBarPanel extends JPanel implements ComponentListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ToolBarPanel(){
		super();
		this.addComponentListener(this);
		this.setProperties();
	}
	
	private void setProperties(){
		this.setMaximumSize(this.getPreferredSize());
		this.revalidate();
		//this.repaint();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		setProperties();
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		setProperties();
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		setProperties();
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		setProperties();
		
	}
	
	

}
