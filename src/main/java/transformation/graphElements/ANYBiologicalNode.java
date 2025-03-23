package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ANYBiologicalNode extends BiologicalNodeAbstract {
	public ANYBiologicalNode(final String label, final String name) {
		super(label, name, Elementdeclerations.anyBNA);
		attributeSetter(getClass().getSimpleName(), this);
	}
}
