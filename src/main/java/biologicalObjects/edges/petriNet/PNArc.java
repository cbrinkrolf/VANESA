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
public class PNArc extends BiologicalEdgeAbstract {

	// conflict sovling: priority that edge is active, 1=highest, n=lowest
	// priority
	@Setter(AccessLevel.NONE)
	private int priority = 1;

	// conflict sovling: probability that edge is active
	@Setter(AccessLevel.NONE)
	private double probability = 1.0;

	private boolean wasUndirected = false;

	public PNArc(BiologicalNodeAbstract from, BiologicalNodeAbstract to, String label, String name, String type,
			String edgeFunction) {
		super(edgeFunction, name, from, to);
		super.setDirected(true);
		if (type.equals(Elementdeclerations.inhibitionEdge) || type.equals(Elementdeclerations.inhibitor)
				|| type.equals(Elementdeclerations.pnInhibitorArc)) {
			setBiologicalElement(Elementdeclerations.pnInhibitorArc);
		} else if (type.equals(Elementdeclerations.pnTestArc)) {
			setBiologicalElement(Elementdeclerations.pnTestArc);
		} else {
			setBiologicalElement(Elementdeclerations.pnArc);
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

	public boolean isInhibitorArc(){
		return getBiologicalElement().equals(Elementdeclerations.pnInhibitorArc);
	}
	
	public boolean isTestArc(){
		return getBiologicalElement().equals(Elementdeclerations.pnTestArc);
	}
	
	public boolean isRegularArc(){
		return getBiologicalElement().equals(Elementdeclerations.pnArc);
	}
}
