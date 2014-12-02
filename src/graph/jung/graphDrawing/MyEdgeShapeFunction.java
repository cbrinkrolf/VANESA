package graph.jung.graphDrawing;

import java.awt.Shape;

import org.apache.commons.collections15.Transformer;

import petriNet.PNEdge;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import graph.algorithms.alignment.AlignmentEdge;

public class MyEdgeShapeFunction implements Transformer<Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape> {

	public MyEdgeShapeFunction(){
		
	}

	@Override
	public Shape transform(
			Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract> context) {
		
		if(context.element instanceof AlignmentEdge){
			return (new EdgeShape.Wedge<BiologicalNodeAbstract, BiologicalEdgeAbstract>(6)).transform(context);
		}else if(context.element instanceof PNEdge){
			
			PNEdge e = (PNEdge) context.element;
			if(context.graph.findEdge(e.getTo(), e.getFrom()) != null){
				return (new EdgeShape.QuadCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>()).transform(context);
			}
			return (new EdgeShape.Line<BiologicalNodeAbstract, BiologicalEdgeAbstract>()).transform(context);
		}
		else{
			return (new EdgeShape.QuadCurve<BiologicalNodeAbstract, BiologicalEdgeAbstract>()).transform(context);
		}
	}

}
