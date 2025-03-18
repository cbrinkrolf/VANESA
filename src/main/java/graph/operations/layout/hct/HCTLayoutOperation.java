package graph.operations.layout.hct;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.Graph;
import graph.layouts.HierarchicalCircleLayoutConfig;
import graph.layouts.hctLayout.HCTLayoutConfig;
import graph.operations.layout.hc.HierarchicalCircleLayoutOperation;

public class HCTLayoutOperation extends HierarchicalCircleLayoutOperation {
	@Override
	public void apply(final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph) {

	}

	@Override
	public HierarchicalCircleLayoutConfig getConfig() {
		return HCTLayoutConfig.getInstance();
	}
}
