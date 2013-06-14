package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Other extends BiologicalNodeAbstract {

	public Other(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.others);
		shapes = new VertexShapes();	
		setShape(shapes.getEllipse());
		setAbstract(false);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(vs.getEllipse(getVertex()));
	}
	
}
