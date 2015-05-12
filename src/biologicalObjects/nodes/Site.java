package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Site extends BiologicalNodeAbstract{
	public Site(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.site);
		shapes = new VertexShapes();	
		attributeSetter(this.getClass().getSimpleName(), this);
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
