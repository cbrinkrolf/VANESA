package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class CompoundNode extends BiologicalNodeAbstract {
	
	public CompoundNode(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.compound);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);
		setReference(false);
	}

//	public void lookUpAtAllDatabases() {
//
//		String db = getDB();
//		addID(db, getLabel());
//
//	}

}
