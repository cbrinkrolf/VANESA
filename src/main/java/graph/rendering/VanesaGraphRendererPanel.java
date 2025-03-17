package graph.rendering;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.VanesaGraph;

public class VanesaGraphRendererPanel extends GraphRendererPanel<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
	public VanesaGraphRendererPanel(final VanesaGraph graph) {
		super(graph);
	}
}
