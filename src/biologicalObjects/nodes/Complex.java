package biologicalObjects.nodes;

import java.util.Vector;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;


public class Complex extends BiologicalNodeAbstract{
	
	Vector elements = new Vector();
	
	public Complex(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.complex);
		shapes = new VertexShapes();	
		setShape(shapes.getRegularStar(6));
		setAbstract(false);
	}
	
	public void addElement(Object element){
		elements.add(element);
	}

	public Vector getAllElements(){
		return elements;
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(shapes.getRegularStar(getVertex(), 6));
	}
}
