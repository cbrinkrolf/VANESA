package biologicalObjects.edges.petriNet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import petriNet.FunctionParser;

public class PNEdge extends BiologicalEdgeAbstract {

	// Wahrscheinlichkeit, dass diese Kante aktiviert wird
	private double activationProbability = 1.0;
	private int activationPriority = 1;

	private double lowerBoundary;
	private double upperBoundary;

	// type could be "normal", test or inhibition arc
	private String type;
	private String function;

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
		this.type = type;
		if (type.equals(biologicalElements.Elementdeclerations.inhibitionEdge)
				|| type.equals(biologicalElements.Elementdeclerations.inhibitor) || type.equals(biologicalElements.Elementdeclerations.pnInhibitionEdge)) {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnInhibitionEdge);
		} else if(type.equals(biologicalElements.Elementdeclerations.pnTestEdge)) {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnTestEdge);
		} else {
			setBiologicalElement(biologicalElements.Elementdeclerations.pnEdge);
		}
		this.function = edgeFunction;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
		super.setLabel(this.function);
		// this.validateFunction();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getActivationProbability() {
		return activationProbability;
	}

	public void setActivationProbability(double activationProbability) {
		this.activationProbability = activationProbability;
	}

	public double getPassingTokens() {
		return fp.parse(this.function);
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

	public boolean isConditionFulfilled() {
		return true;
	}
}
