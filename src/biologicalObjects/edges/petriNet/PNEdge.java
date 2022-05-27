package biologicalObjects.edges.petriNet;

import java.util.ArrayList;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import petriNet.FunctionParser;

@Getter
@Setter
public class PNEdge extends BiologicalEdgeAbstract {

	// conflict sovling: priority that edge is active, 1=highest, n=lowest
	// priority
	@Setter(AccessLevel.NONE)
	private int priority = 1;

	// conflict sovling: probability that edge is active
	@Setter(AccessLevel.NONE)
	private double probability = 1.0;

	private boolean wasUndirected = false;

	public PNEdge(BiologicalNodeAbstract from, BiologicalNodeAbstract to, String label, String name, String type,
			String edgeFunction) {
		super(edgeFunction, name, from, to);
		super.setDirected(true);
		if (type.equals(Elementdeclerations.inhibitionEdge) || type.equals(Elementdeclerations.inhibitor)
				|| type.equals(Elementdeclerations.pnInhibitionEdge)) {
			setBiologicalElement(Elementdeclerations.pnInhibitionEdge);
		} else if (type.equals(Elementdeclerations.pnTestEdge)) {
			setBiologicalElement(Elementdeclerations.pnTestEdge);
		} else {
			setBiologicalElement(Elementdeclerations.pnEdge);
		}
		setFunction(edgeFunction);
	}

	public double getPassingTokens() {
		return new FunctionParser().parse(getFunction());
	}

	public void setPriority(int priority) {
		if (priority > 0) {
			this.priority = priority;
		}
	}

	public void setProbability(double probability) {
		if (probability >= 0) {
			this.probability = probability;
		}
	}

	// defines parameters which are available in during transformation
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<String>();
		list.add("function");
		return list;
	}

	public boolean isInhibitoryArc(){
		return getBiologicalElement().equals(Elementdeclerations.pnInhibitionEdge);
	}
	
	public boolean isTestArc(){
		return getBiologicalElement().equals(Elementdeclerations.pnTestEdge);
	}
}
