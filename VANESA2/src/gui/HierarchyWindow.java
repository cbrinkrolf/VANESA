package gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import graph.GraphInstance;

public class HierarchyWindow {
	
	private VisualizationViewer vv;
	private JPanel p = new JPanel();
	private JButton enterNodeButton = new JButton("Enter Node");
	private GraphInstance graphInstance;
	boolean emptyPane = true;
	
	public HierarchyWindow() {
	}
	
	
	public void revalidateHierarchyView(){
		graphInstance = new GraphInstance();
		vv = GraphInstance.getMyGraph().getVisualizationViewer();
		if(emptyPane){	
			//p.add(vv,BorderLayout.CENTER);
			p.add(enterNodeButton);
			emptyPane = false;
		}else{
			p.removeAll();
			//p.add(vv,BorderLayout.CENTER);
			p.add(enterNodeButton);
		}
		p.setVisible(true);
		p.repaint();
		
	}	
	
	public void removeAllElements(){
		emptyPane=true;
		p.removeAll();
		p.setVisible(false);
	}
	
	
	public JPanel getHierarchyPane() {
		p.setVisible(false);
		return p;	
	}
}
