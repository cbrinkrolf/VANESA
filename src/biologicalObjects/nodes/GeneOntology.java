package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class GeneOntology extends BiologicalNodeAbstract{
	public GeneOntology(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.go);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
		setAbstract(false);
		setReference(false);
	}
	
//	public void lookUpAtAllDatabases() {
//		String db = getDB();
//		addID(db, getLabel());
//	}
	
}
