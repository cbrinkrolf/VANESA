package graph.jung.graphDrawing;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

public class MyEdgeShapeFunction implements Function<BiologicalEdgeAbstract, Shape> {
	private final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;

	public MyEdgeShapeFunction(final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph) {
		this.graph = graph;
	}

	@Override
	public Shape apply(final BiologicalEdgeAbstract bea) {
		final Collection<BiologicalEdgeAbstract> forwardEdges = graph.findEdgeSet(bea.getFrom(), bea.getTo());
		final Collection<BiologicalEdgeAbstract> backwardEdges = graph.findEdgeSet(bea.getTo(), bea.getFrom());
		final List<BiologicalEdgeAbstract> edges = new ArrayList<>(forwardEdges);
		edges.addAll(backwardEdges);
		if (edges.size() > 1) {
			final var shape = EdgeShape.quadCurve(graph);
			shape.setEdgeIndexFunction(new EdgeIndexFunction<>() {
				@Override
				public int getIndex(final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph,
						final BiologicalEdgeAbstract bea) {
					return edges.indexOf(bea);
				}

				@Override
				public void reset(final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g,
						final BiologicalEdgeAbstract bea) {
				}

				@Override
				public void reset() {
				}
			});
			return shape.apply(bea);
		}
		return EdgeShape.line(graph).apply(bea);
	}
}
