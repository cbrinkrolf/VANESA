package graph.layouts.hebLayout;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public interface HierarchyListComparator {
	/**
	 * Returns the node that contains the input node.
	 * @param n1 The node to check..
	 * @return The bna the two nodes contain to. Returns null, if no node was found.
	 */
	public BiologicalNodeAbstract findGroup(BiologicalNodeAbstract n);
}
