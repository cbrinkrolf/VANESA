package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.jung.graphDrawing.VertexShapes;

public class ANYBiologicalNode extends BiologicalNodeAbstract {

	public ANYBiologicalNode(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.anyBNA);
		shapes = new VertexShapes();
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
//		setShape(vs.getRegularStar(getVertex(), 8));
	}

}
