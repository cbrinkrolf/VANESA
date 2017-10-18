package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class Exon extends BiologicalNodeAbstract{
	
	public Exon(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.exon);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(shapes.getRegularStar(getVertex(), 6));
	}
}
