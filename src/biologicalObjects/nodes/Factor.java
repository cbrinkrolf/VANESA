package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import graph.jung.graphDrawing.VertexShapes;

public class Factor extends BiologicalNodeAbstract{
	public Factor(String label, String name){		
		super(label,name);
		setBiologicalElement(Elementdeclerations.factor);
		shapes = new VertexShapes();	
		attributeSetter(this.getClass().getSimpleName(), this);
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(vs.getEllipse(getVertex()));
	}
	
}
