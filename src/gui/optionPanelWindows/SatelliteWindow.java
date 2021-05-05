package gui.optionPanelWindows;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import graph.GraphInstance;

public class SatelliteWindow {
	
	private SatelliteVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;
	private JPanel p = new JPanel();
	boolean emptyPane = true;
	
	public SatelliteWindow() {
	}
	
	
	public void revalidateSatelliteView(){
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
