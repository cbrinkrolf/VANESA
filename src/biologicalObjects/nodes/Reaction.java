package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Reaction extends BiologicalNodeAbstract{
	public Reaction(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.reaction);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
		setReference(false);
	}
	
//	public void lookUpAtAllDatabases() {
//		String db = getDB();
//		addID(db, getLabel());
//	}

}
