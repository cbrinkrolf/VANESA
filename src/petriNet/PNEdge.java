package petriNet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.decorators.EdgeShape;

public class PNEdge extends BiologicalEdgeAbstract {

	// Wahrscheinlichkeit, dass diese Kante aktiviert wird
	private double activationProbability;
	// Anzahl an Tokens, die "wandern"
	// private double passingTokens;

	private double lowerBoundary;
	private double upperBoundary;

	// hier gibt es bestimmt einen besseren Datentyp. Dieser ist erstmal ein
	// Platzhalter!
	// TODO vllt besser boolean, inhibition true/false?
	private String condition;
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
				|| type.equals(biologicalElements.Elementdeclerations.inhibitor))
			setBiologicalElement(biologicalElements.Elementdeclerations.pnInhibitionEdge);
		else if (type
				.equals(biologicalElements.Elementdeclerations.pnDiscreteEdge)
				|| type.equals(biologicalElements.Elementdeclerations.pnContinuousEdge)
				|| type.equals(biologicalElements.Elementdeclerations.pnInhibitionEdge))
			setBiologicalElement(type);
		else
			setBiologicalElement(biologicalElements.Elementdeclerations.pnDiscreteEdge);

		this.function = edgeFunction;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
		super.setLabel(this.function);
		//this.validateFunction();
	}

	// public PNEdge(Edge edge, String label, String name, String type, double
	// passingTokens){
	// this(edge, label, name, type);
	// this.passingTokens = passingTokens;
	// }

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
		// return passingTokens;
		return fp.parse(this.function);
	}

	/*
	 * public void setPassingTokens(double passingTokens) { this.passingTokens =
	 * passingTokens; super.setLabel(passingTokens+""); }
	 */

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

	/*
	 * public String getCondition() { return condition; }
	 * 
	 * public void setCondition(String condition) { this.condition = condition;
	 * }
	 */

	public boolean isConditionFulfilled() {
		return true;
	}
}
