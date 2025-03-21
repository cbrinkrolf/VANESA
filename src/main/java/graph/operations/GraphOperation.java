package graph.operations;

import graph.Graph;
import graph.GraphAnnotation;
import graph.GraphEdge;
import graph.GraphNode;

public abstract class GraphOperation<V extends GraphNode, E extends GraphEdge<V>, A extends GraphAnnotation> {
	public abstract void apply(final Graph<V, E, A> graph);
}
