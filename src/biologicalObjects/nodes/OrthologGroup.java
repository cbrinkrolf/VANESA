package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class OrthologGroup extends Complex{

	public OrthologGroup(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.orthologGroup);
		shapes = new VertexShapes();	
		attributeSetter(this.getClass().getSimpleName(), this);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
//		setShape(vs.getRegularStar(getVertex(), 10));
	}

}
