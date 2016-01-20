package biologicalObjects.nodes;

import graph.jung.graphDrawing.VertexShapes;
import biologicalElements.Elementdeclerations;


public class Collector extends BiologicalNodeAbstract{
	
	
	public Collector(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.collector);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(shapes.getRegularStar(getVertex(), 6));
	}
}
