package graph.layouts.hebLayout;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public interface HierarchyListComparator<E> {
	
	public E getSubValue(BiologicalNodeAbstract n);
	public E getValue(BiologicalNodeAbstract n);
}
