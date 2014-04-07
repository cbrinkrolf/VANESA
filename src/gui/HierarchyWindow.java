package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.eventhandlers.ToolBarListener;

public class HierarchyWindow {
	
	private VisualizationViewer vv;
	private JPanel p = new JPanel();
	boolean emptyPane = true;
	
	public HierarchyWindow() {
	}
	
	
	public void revalidateHierarchyView(){
		try{
		BiologicalNodeAbstract node = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().iterator().next();
		vv = node.getGraph().getVisualizationViewer();
		for(BiologicalNodeAbstract n : node.getGraph().getAllVertices()){
			if(node.getBorder().contains(n)){
				n.setColor(Color.RED);
			}
		}
		vv.setSize(200, 200);
		vv.setMaximumSize(new Dimension(200, 200));
		} catch(Exception ex){
			vv = null;
		}
		if(emptyPane){	
			if(vv!=null){
				p.add(vv,BorderLayout.CENTER);
				vv.repaint();
			}
			emptyPane = false;
		}else{
			p.removeAll();
			if(vv!=null){
				p.add(vv,BorderLayout.CENTER);
				vv.repaint();
			}
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
