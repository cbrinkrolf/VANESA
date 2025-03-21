package graph.operations.layout;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.Graph;
import graph.annotations.VanesaAnnotation;

public class CircleLayoutOperation extends LayoutOperation {
	private double radius = 400;

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Override
	public void apply(final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract, VanesaAnnotation> graph) {
		final double angleStep = Math.PI * 2 / graph.getNodeCount();
		int i = 0;
		for (final BiologicalNodeAbstract node : graph.getNodes()) {
			graph.setNodePosition(node, Math.cos(angleStep * i) * radius, Math.sin(angleStep * i) * radius);
			i++;
		}
	}
}
