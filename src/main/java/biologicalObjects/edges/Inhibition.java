package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Inhibition extends BiologicalEdgeAbstract {
	private boolean absoluteInhibition = true;

	public Inhibition(String label, String name, BiologicalNodeAbstract from, BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.inhibitionEdge);
	}

	public boolean isAbsoluteInhibition() {
		return absoluteInhibition;
	}

	public void setAbsoluteInhibition(boolean absoluteInhibition) {
		this.absoluteInhibition = absoluteInhibition;
	}
}
