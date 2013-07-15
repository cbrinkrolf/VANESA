package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import edu.uci.ics.jung.graph.Vertex;
import graph.jung.graphDrawing.VertexShapes;

public class Location extends BiologicalNodeAbstract{
	public Location(String label, String name, Vertex vertex) {
		super(label, name, vertex);
		setBiologicalElement(Elementdeclerations.location);
		shapes = new VertexShapes();	
		setShape(shapes.getRectangle(vertex));
	}
	
	@Override
	public void rebuildShape(VertexShapes vs){
		setShape(vs.getRectangle(getVertex()));
	}
}
