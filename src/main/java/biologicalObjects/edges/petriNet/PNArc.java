package biologicalObjects.edges.petriNet;

import java.util.ArrayList;
import java.util.List;

import biologicalElements.ElementDeclarations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphEdgeLineStyle;
import graph.rendering.shapes.CircleTipShape;
import petriNet.FunctionParser;

public class PNArc extends BiologicalEdgeAbstract {
	// conflict solving: priority that edge is active, 1=highest, n=lowest priority
	private int priority = 1;
	// conflict solving: probability that edge is active
	private double probability = 1.0;

	public PNArc(BiologicalNodeAbstract from, BiologicalNodeAbstract to, String label, String name, String type,
			String edgeFunction) {
		super(edgeFunction, name, from, to,
				type.equals(ElementDeclarations.inhibitionEdge) || type.equals(ElementDeclarations.inhibitor)
						|| type.equals(ElementDeclarations.pnInhibitorArc)
						? ElementDeclarations.pnInhibitorArc
						: (type.equals(ElementDeclarations.pnTestArc)
								? ElementDeclarations.pnTestArc
								: ElementDeclarations.pnArc));
		super.setDirected(true);
		setFunction(edgeFunction);
		if (ElementDeclarations.pnInhibitorArc.equals(getBiologicalElement())) {
			setToTipShape(new CircleTipShape());
		} else if (ElementDeclarations.pnTestArc.equals(getBiologicalElement())) {
			setLineStyle(GraphEdgeLineStyle.DASHED);
		}
	}

	public double getPassingTokens() {
		return new FunctionParser().parse(getFunction());
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

	// defines parameters which are available in during transformation
	public List<String> getTransformationParameters() {
		List<String> list = new ArrayList<>();
		list.add("function");
		return list;
	}

	public boolean isInhibitorArc() {
		return getBiologicalElement().equals(ElementDeclarations.pnInhibitorArc);
	}

	public boolean isTestArc() {
		return getBiologicalElement().equals(ElementDeclarations.pnTestArc);
	}

	public boolean isRegularArc() {
		return getBiologicalElement().equals(ElementDeclarations.pnArc);
	}
}
