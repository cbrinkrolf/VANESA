package graph.jung.classes;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;


public class MyGraphZoomScrollPane extends GraphZoomScrollPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyGraphZoomScrollPane(VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv) {
		super(vv);
		
	}

	public void setVisualizationViewer(VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv){
		
		remove(vv);
		this.vv=vv;
		add(vv);

	}
}
