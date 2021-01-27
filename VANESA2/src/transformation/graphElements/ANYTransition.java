package transformation.graphElements;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.petriNet.Transition;
import graph.jung.graphDrawing.VertexShapes;

public class ANYTransition extends Transition {

	public ANYTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.transition);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);

		AffineTransform transform2 = new AffineTransform();

		transform2.translate(1, 1);
		transform2.scale(1, 2);
		setDefaultShape(shapes.makeCoarse(transform2.createTransformedShape(shapes.getRectangle())));
		setDefaultColor(Color.white);
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
	}

	@Override
	public String getNetworklabel() {
		return "";
	}

}
