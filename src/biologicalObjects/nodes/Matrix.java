package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Matrix extends BiologicalNodeAbstract{
	public Matrix(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.matrix);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
		setReference(false);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(vs.getEllipse(getVertex()));
	}
	
//	@SuppressWarnings("unchecked")
//	public void lookUpAtAllDatabases() {
//
//		String db = getDB();
//		addID(db, getLabel());
//		
//	}
//	
}
