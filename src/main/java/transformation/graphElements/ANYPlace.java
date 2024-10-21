package transformation.graphElements;

import java.awt.Color;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.petriNet.Place;
import graph.jung.graphDrawing.VertexShapes;

public class ANYPlace extends Place {

	public ANYPlace(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.place);
		setDefaultShape(VertexShapes.makeCoarse(VertexShapes.getEllipse()));
		setDiscrete(true);
		attributeSetter(this.getClass().getSimpleName(), this);
		
		this.setDefaultNodesize(2);
		setDefaultColor(Color.WHITE);
	}
}
