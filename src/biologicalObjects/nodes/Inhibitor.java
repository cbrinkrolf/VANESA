package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Inhibitor extends BiologicalNodeAbstract {
	public Inhibitor(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.inhibitor);
		shapes = new VertexShapes();
		setShape(shapes.getEllipse());
		setAbstract(false);
		setReference(false);
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
		// setShape(vs.getEllipse(getVertex()));
	}

	// @SuppressWarnings("unchecked")
	// public void lookUpAtAllDatabases() {
	//
	// String db = getDB();
	// addID(db, getLabel());
	//
	// }
}
