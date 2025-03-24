package transformation.graphElements;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ANYBiologicalNode extends BiologicalNodeAbstract {
	public ANYBiologicalNode(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.anyBNA, parent);
		attributeSetter();
	}
}
