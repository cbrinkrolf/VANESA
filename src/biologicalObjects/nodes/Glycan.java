package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Glycan extends BiologicalNodeAbstract{

	public Glycan(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.glycan);
		shapes = new VertexShapes();	
		setDefaultShape(shapes.getRegularPolygon(8));
		setReference(false);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(vs.getRegularPolygon(getVertex(), 8));
	}

//	public void lookUpAtAllDatabases() {
//		String db = getDB();
//		addID(db, getLabel());
//		
//	}
	
}
