package biologicalObjects.edges.petriNet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import petriNet.FunctionParser;

public class PNEdge extends BiologicalEdgeAbstract {

	// probability that edge is active
	private double activationProbability = 1.0;
	// priority that edge is active, 1=highest, n=lowest priority
	private int activationPriority = 1;

	private double lowerBoundary;
	private double upperBoundary;

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
				|| type.equals(biologicalElements.Elementdeclerations.inhibitor) || type.equals(biologicalElements.Elementdeclerations.pnInhibitionEdge)) {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnInhibitionEdge);
		} else if(type.equals(biologicalElements.Elementdeclerations.pnTestEdge)) {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnTestEdge);
		} else {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnEdge);
		}
		setFunction(edgeFunction);
	}

	public double getActivationProbability() {
		return activationProbability;
	}

	public void setActivationProbability(double activationProbability) {
		this.activationProbability = activationProbability;
	}

	public double getPassingTokens() {
		return fp.parse(getFunction());
	}

	public double getLowerBoundary() {
		return lowerBoundary;
	}

	public void setLowerBoundary(double lowerBoundary) {
		this.lowerBoundary = lowerBoundary;
	}

	public double getUpperBoundary() {
		return upperBoundary;
	}

	public void setUpperBoundary(double upperBoundary) {
		this.upperBoundary = upperBoundary;
	}

	public int getActivationPriority() {
		return activationPriority;
	}

	public void setActivationPriority(int activationPriority) {
		this.activationPriority = activationPriority;
	}
}
