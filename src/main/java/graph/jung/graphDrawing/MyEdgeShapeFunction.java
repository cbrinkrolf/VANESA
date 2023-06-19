package graph.jung.graphDrawing;

import java.awt.Shape;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

public class MyEdgeShapeFunction implements Function<BiologicalEdgeAbstract, Shape> {
    // private final QuadCurve quadcurve = EdgeShape.quadCurve(Graph<V,E>);

    // private final Line line = new EdgeShape.Line();
    // private final BentLine<BiologicalNodeAbstract, BiologicalEdgeAbstract>
    // bentline = new EdgeShape.BentLine<>();
    // private final CubicCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>
    // cubiccurve = new EdgeShape.CubicCurve<>();
    // private final Orthogonal<BiologicalNodeAbstract, BiologicalEdgeAbstract>
    // orthogonal = new EdgeShape.Orthogonal<>();

    private final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;

    public MyEdgeShapeFunction(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph) {
        this.graph = graph;
    }

    @Override
    public Shape apply(BiologicalEdgeAbstract bea) {
        // if(context.element instanceof AlignmentEdge)
        // return (new EdgeShape.Wedge<BiologicalNodeAbstract,
        // BiologicalEdgeAbstract>(6)).transform(context);
        if (graph.findEdge(bea.getTo(), bea.getFrom()) != null) {
            return EdgeShape.quadCurve(graph).apply(bea);
            // return (quadcurve).transform(context);
        }
        return EdgeShape.line(graph).apply(bea);
        // return (line).transform(context);
    }

}
