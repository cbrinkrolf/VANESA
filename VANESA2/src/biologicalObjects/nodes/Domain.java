package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class Domain extends BiologicalNodeAbstract{
	
	public Domain(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.domain);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(shapes.getRegularStar(getVertex(), 6));
	}
}
