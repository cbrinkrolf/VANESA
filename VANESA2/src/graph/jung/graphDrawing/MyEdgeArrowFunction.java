package graph.jung.graphDrawing;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.decorators.DirectionalEdgeArrowTransformer;

/**
 * 
 * 
 * @author cbrinkrolf
 * 
 */
public class MyEdgeArrowFunction
		implements
		Transformer<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape> {

	@Override
	public Shape transform(
			Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract> context) {
		//System.out.println(context.element.getBiologicalElement());
		if ((context.element.getBiologicalElement().equals(
				biologicalElements.Elementdeclerations.pnInhibitionEdge)
				|| context.element.getBiologicalElement().equals(
						biologicalElements.Elementdeclerations.inhibitionEdge) || context.element
				.getBiologicalElement().equals(
						biologicalElements.Elementdeclerations.inhibitor))) {
			return new Ellipse2D.Double(-10, -5, 10, 10);
		}
		return new DirectionalEdgeArrowTransformer<BiologicalNodeAbstract, BiologicalEdgeAbstract>(
				10, 8, 4).transform(context);
	}
}