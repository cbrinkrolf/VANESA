/*package graph.layouts;

import java.awt.geom.Point2D;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

public class ClusterLayout {

	SubLayoutDecorator layout;
	Point2D center;
	PickedState pickedState;
	SubLayout subLayout;
	int type;
	Graph g;
	boolean isSelectionEmpty = true;
	Set vertices;

	public ClusterLayout(PickedState pickedState, int type,
			SubLayoutDecorator layout) {
		this.pickedState = pickedState;
		this.layout = layout;
		this.type = type;
		this.g = layout.getGraph();
		layout();
	}

	private void doLayout(int type) {

		if (isSelectionEmpty) {
			vertices = g.getVertices();
		} else {
			vertices = pickedState.getPickedVertices();
		}

		int number = type;
		switch (number) {
		case 1:
			// subLayout = new SpringLayout(g);
			break;
		case 2:
			 
              //g.vv.setGraphLayout(l, false);
             
			// subLayout = new KKSubLayout(vertices,layout);
			break;
		case 3:
			// subLayout = new FRLayout(g);
			break;
		case 4:
			subLayout = new CircularSubLayout(vertices,layout);
			
			break;
		case 5:
			// subLayout = new ISOMLayout(g);
			break;
		default:
			break;
		}
		layout.addSubLayout(subLayout);
	}

	private void layout() {
		
		if (pickedState.getPickedVertices().isEmpty()) {			
			layout.removeAllSubLayouts();
			isSelectionEmpty = true;			
		} else {
			isSelectionEmpty = false;		
		}
		doLayout(type);
	}
}*/
