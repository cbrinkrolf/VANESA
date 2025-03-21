package graph.operations.layout;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.Graph;
import graph.annotations.VanesaAnnotation;

public class StaticLayoutOperation extends LayoutOperation {
	@Override
	public void apply(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract, VanesaAnnotation> graph) {
		// TODO: center all elements
		/*
		HashMap<BiologicalNodeAbstract, Point2D> map = new HashMap<>();
		for (BiologicalNodeAbstract bna : g.getVertices()) {
			Point2D p = getVertexLocation(bna);
			map.put(bna, p);
		}
		// shifting to 100,100
		GraphCenter gc = new GraphCenter(this);
		double minX = gc.getMinX();
		double minY = gc.getMinY();
		double offsetX = 0;
		double offsetY = 0;
		if (minX < 0) {
			offsetX = Math.abs(minX) + 100;
		} else if (minX < 100) {
			offsetX = 100 - minX;
		}

		if (minY < 0) {
			offsetY = Math.abs(minY) + 100;
		} else if (minY < 100) {
			offsetY = 100 - minY;
		}

		changeToLayout(new StaticLayout<>(g));
		for (BiologicalNodeAbstract bna : g.getVertices()) {
			moveVertex(bna, map.get(bna).getX() + offsetX, map.get(bna).getY() + offsetY);
		}
		annotationManager.moveAllAnnotation(offsetX, offsetY);
		*/
	}
}
