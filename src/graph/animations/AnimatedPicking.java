package graph.animations;

import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.visualization.Layout;
//import edu.uci.ics.jung.visualization.PickSupport;
//import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphInstance;

public class AnimatedPicking {

	protected VisualizationViewer vv;
	GraphElementAccessor pickSupport;
	PickedState pickedState;
	Layout layout;

	public AnimatedPicking() {
	}
	
	public void animatePicking(Vertex vertex, boolean animate){
		
		GraphInstance graphInstance = new GraphInstance();	
		vv = GraphInstance.getMyGraph().getVisualizationViewer();
		pickSupport = vv.getPickSupport();
		pickedState = vv.getPickedState();		
		if (pickSupport != null && pickedState != null) {
			if (vertex != null) {
				if (pickedState.isPicked(vertex) == false) {
					pickedState.clearPickedVertices();
					pickedState.pick(vertex, true);
				}
				if(animate){
					layout = vv.getGraphLayout();
					Point2D q = layout.getLocation(vertex);
					Point2D lvc = vv.inverseTransform(vv.getCenter());
					final double dx = (lvc.getX() - q.getX()) / 10;
					final double dy = (lvc.getY() - q.getY()) / 10;

					Runnable animator = new Runnable() {

						public void run() {
							for (int i = 0; i < 10; i++) {
								vv.getLayoutTransformer().translate(dx, dy);
								try {
									Thread.sleep(100);
								} catch (InterruptedException ex) {
								}
							}
						}
					};
					Thread thread = new Thread(animator);
					thread.start();
				}
			}
		}
		
	}
}
