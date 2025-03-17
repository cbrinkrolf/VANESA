package graph.operations;

import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;

public abstract class GraphOperation<V extends GraphNode, E extends GraphEdge<V>> {
	public abstract void apply(final Graph<V, E> graph);
}
