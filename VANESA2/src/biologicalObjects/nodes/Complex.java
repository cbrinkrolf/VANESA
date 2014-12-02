package biologicalObjects.nodes;

//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;
import biologicalElements.Elementdeclerations;


public class Complex extends BiologicalNodeAbstract{
	
	
	public Complex(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.complex);
		shapes = new VertexShapes();	
		setDefaultShape(shapes.getRegularStar(6));
	}
	

	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(shapes.getRegularStar(getVertex(), 6));
	}
}
