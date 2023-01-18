package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ANYBiologicalNode extends BiologicalNodeAbstract {

	public ANYBiologicalNode(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.anyBNA);
		attributeSetter(this.getClass().getSimpleName(), this);
	}
}
