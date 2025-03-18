package graph.operations.layout.heb;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.Graph;
import graph.layouts.HierarchicalCircleLayoutConfig;
import graph.layouts.hebLayout.HEBLayoutConfig;
import graph.operations.layout.hc.HierarchicalCircleLayoutOperation;

public class HEBLayoutOperation extends HierarchicalCircleLayoutOperation {
	@Override
	public void apply(final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph) {

	}

	@Override
	public HierarchicalCircleLayoutConfig getConfig() {
		return HEBLayoutConfig.getInstance();
	}
}
