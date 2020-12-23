package biologicalObjects.edges.petriNet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import petriNet.FunctionParser;

public class PNEdge extends BiologicalEdgeAbstract {

	// conflict sovling: priority that edge is active, 1=highest, n=lowest
	// priority
	private int priority = 1;

	// conflict sovling: probability that edge is active
	private double probability = 1.0;

	private boolean wasUndirected = false;

	public boolean wasUndirected() {
		return wasUndirected;
	}

	public void wasUndirected(boolean wasUndirected) {
		this.wasUndirected = wasUndirected;
	}

	private FunctionParser fp = new FunctionParser();

	public PNEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to, String label, String name, String type,
			String edgeFunction) {
		super(edgeFunction, name, from, to);
		super.setDirected(true);
		if (type.equals(biologicalElements.Elementdeclerations.inhibitionEdge)
				|| type.equals(biologicalElements.Elementdeclerations.inhibitor)
				|| type.equals(biologicalElements.Elementdeclerations.pnInhibitionEdge)) {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnInhibitionEdge);
		} else if (type.equals(biologicalElements.Elementdeclerations.pnTestEdge)) {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnTestEdge);
		} else {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnEdge);
		}
		setFunction(edgeFunction);
	}

	public double getPassingTokens() {
		return fp.parse(getFunction());
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		if (priority > 0) {
			this.priority = priority;
		}
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		if (probability >= 0) {
			this.probability = probability;
		}
	}
}
