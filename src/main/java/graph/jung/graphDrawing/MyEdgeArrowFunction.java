package graph.jung.graphDrawing;

import java.awt.Shape;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.decorators.DirectionalEdgeArrowTransformer;

public class MyEdgeArrowFunction implements
		Function<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape> {
	@Override
	public Shape apply(Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract> context) {
		if (context.element.getBiologicalElement().equals(biologicalElements.Elementdeclerations.pnInhibitorArc)) {
			return VertexShapes.getPNInhibitorArrowHead();
		}
		if (context.element.getBiologicalElement().equals(biologicalElements.Elementdeclerations.inhibitionEdge)
				|| context.element.getBiologicalElement().equals(biologicalElements.Elementdeclerations.inhibitor)) {
			return VertexShapes.getInhibitorArrowHead();
		}
		return new DirectionalEdgeArrowTransformer<BiologicalNodeAbstract, BiologicalEdgeAbstract>(10, 8, 4)
				.apply(context);
	}
}