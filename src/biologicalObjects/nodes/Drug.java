package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Drug extends BiologicalNodeAbstract{
	public Drug(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.drug);
		VertexShapes shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
		setAbstract(false);
		setReference(false);
	}
	
//	public void lookUpAtAllDatabases() {
//		String db = getDB();
//		addID(db, getLabel());	
//	}
}
