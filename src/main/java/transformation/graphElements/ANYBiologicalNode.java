package transformation.graphElements;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ANYBiologicalNode extends BiologicalNodeAbstract {
	public ANYBiologicalNode(final String label, final String name, final Pathway pathway) {
		super(label, name, ElementDeclarations.anyBNA, pathway);
		attributeSetter();
	}
}
