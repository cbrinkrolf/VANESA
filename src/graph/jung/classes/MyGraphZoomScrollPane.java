package graph.jung.classes;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;


public class MyGraphZoomScrollPane extends GraphZoomScrollPane{

	public MyGraphZoomScrollPane(VisualizationViewer vv) {
		super(vv);
		
	}

	public void setVisualizationViewer(VisualizationViewer vv){
		
		remove(vv);
		this.vv=vv;
		add(vv);

	}
}
