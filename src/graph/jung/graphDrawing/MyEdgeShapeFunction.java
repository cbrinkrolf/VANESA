package graph.jung.graphDrawing;

import java.awt.Shape;

import org.apache.commons.collections15.Transformer;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EdgeShape.Line;
import edu.uci.ics.jung.visualization.decorators.EdgeShape.QuadCurve;

public class MyEdgeShapeFunction
		implements Transformer<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape> {

	private final QuadCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract> quadcurve = new EdgeShape.QuadCurve<>();
	private final Line<BiologicalNodeAbstract, BiologicalEdgeAbstract> line = new EdgeShape.Line<>();
	//private final BentLine<BiologicalNodeAbstract, BiologicalEdgeAbstract> bentline = new EdgeShape.BentLine<>();
	//private final CubicCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract> cubiccurve = new EdgeShape.CubicCurve<>();
	//private final Orthogonal<BiologicalNodeAbstract, BiologicalEdgeAbstract> orthogonal = new EdgeShape.Orthogonal<>();

	public MyEdgeShapeFunction() {

	}

	@Override
	public Shape transform(Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract> context) {

		// if(context.element instanceof AlignmentEdge)
		// return (new EdgeShape.Wedge<BiologicalNodeAbstract,
		// BiologicalEdgeAbstract>(6)).transform(context);

		BiologicalEdgeAbstract e = context.element;
		if (context.graph.findEdge(e.getTo(), e.getFrom()) != null) {
			return (quadcurve).transform(context);
		}
		return (line).transform(context);
	}

}
