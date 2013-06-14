package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import graph.GraphInstance;

public class SatelliteWindow {
	
	private SatelliteVisualizationViewer vv;
	private JPanel p = new JPanel();
	private GraphInstance graphInstance;
	boolean emptyPane = true;
	
	public SatelliteWindow() {
	}
	
	
	public void revalidateSatelliteView(){
		graphInstance = new GraphInstance();
		vv = GraphInstance.getMyGraph().getSatelliteView();
		if(emptyPane){	
			p.add(vv,BorderLayout.CENTER);
			emptyPane = false;
		}else{
			p.removeAll();
			p.add(vv,BorderLayout.CENTER);
		}
		p.setVisible(true);
		p.repaint();
		
	}	
	
	public void removeAllElements(){
		emptyPane=true;
		p.removeAll();
		p.setVisible(false);
	}
	
	
	public JPanel getSatellitePane() {
		p.setVisible(false);
		return p;	
	}
}
