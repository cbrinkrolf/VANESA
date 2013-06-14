package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Fragment extends BiologicalNodeAbstract{
	public Fragment(String label, String name, Vertex vertex){		
		super(label,name,vertex);
		setBiologicalElement(Elementdeclerations.fragment);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse(vertex));
		setAbstract(false);
		setReference(false);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		setShape(vs.getEllipse(getVertex()));
	}
	
//	@SuppressWarnings("unchecked")
//	public void lookUpAtAllDatabases() {
//
//		String db = getDB();
//		addID(db, getLabel());
//		
//	}
	
}