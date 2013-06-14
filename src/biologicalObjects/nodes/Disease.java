package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Disease extends BiologicalNodeAbstract {
	public final String Object = "Disease";
	public Disease(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.disease);
		shapes = new VertexShapes();	
		setShape(shapes.getRegularStar(8));
		setAbstract(false);
		setReference(false);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
//		setShape(vs.getRegularStar(getVertex(), 8));
	}

//	public void lookUpAtAllDatabases() {
//		String db = getDB();
//		addID(db, getLabel());
//	}
	
}
