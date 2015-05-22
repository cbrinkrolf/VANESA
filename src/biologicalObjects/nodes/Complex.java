package biologicalObjects.nodes;

import graph.jung.graphDrawing.VertexShapes;
import biologicalElements.Elementdeclerations;


public class Complex extends BiologicalNodeAbstract{
	
	
	public Complex(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.complex);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);
		System.out.println("asd");
		System.out.println(label);
		System.out.println(name);
	}
	

	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(shapes.getRegularStar(getVertex(), 6));
	}
}
