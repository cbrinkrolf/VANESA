package biologicalObjects.edges;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Inhibition extends BiologicalEdgeAbstract {
	private boolean absoluteInhibition = true;

	public Inhibition(final String label, final String name, final BiologicalNodeAbstract from,
			final BiologicalNodeAbstract to) {
		super(label, name, from, to, Elementdeclerations.inhibitionEdge);
	}

	public boolean isAbsoluteInhibition() {
		return absoluteInhibition;
	}

	public void setAbsoluteInhibition(boolean absoluteInhibition) {
		this.absoluteInhibition = absoluteInhibition;
	}
}
