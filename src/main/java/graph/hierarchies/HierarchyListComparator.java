package graph.hierarchies;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public interface HierarchyListComparator<E> {
    E getSubValue(BiologicalNodeAbstract n);

    E getValue(BiologicalNodeAbstract n);
}
