package biologicalObjects.edges;

import biologicalElements.ElementDeclarations;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.rendering.shapes.InhibitionTipShape;

public class Inhibition extends BiologicalEdgeAbstract {
	private boolean absoluteInhibition = true;

	public Inhibition(final String label, final String name, final BiologicalNodeAbstract from,
			final BiologicalNodeAbstract to) {
		super(label, name, from, to, ElementDeclarations.inhibitionEdge);
		setToTipShape(new InhibitionTipShape());
	}

	public boolean isAbsoluteInhibition() {
		return absoluteInhibition;
	}

	public void setAbsoluteInhibition(boolean absoluteInhibition) {
		this.absoluteInhibition = absoluteInhibition;
	}
}
