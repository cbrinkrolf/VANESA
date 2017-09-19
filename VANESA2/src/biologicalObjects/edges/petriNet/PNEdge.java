package biologicalObjects.edges.petriNet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import petriNet.FunctionParser;

public class PNEdge extends BiologicalEdgeAbstract {
	
	public static final int CONFLICTHANDLING_NONE = 0;
	public static final int CONFLICTHANDLING_PRIO = 1;
	public static final int CONFLICTHANDLING_PROB = 2;

	private int conflictStrategy = 0;
	
	// conflict sovling: priority that edge is active, 1=highest, n=lowest priority
	private int priority = 1;
	
	// conflict sovling: probability that edge is active
	private double probability = 1.0;

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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getConflictStrategy() {
		return conflictStrategy;
	}

	public void setConflictStrategy(int conflictStrategy) {
		this.conflictStrategy = conflictStrategy;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
}
