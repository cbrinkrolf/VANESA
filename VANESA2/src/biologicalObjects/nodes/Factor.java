package biologicalObjects.nodes;

import java.awt.Color;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Factor extends BiologicalNodeAbstract{
	public Factor(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.factor);
		shapes = new VertexShapes();	
		setDefaultShape(shapes.getEllipse());
		setDefaultColor(Color.cyan);
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
}
