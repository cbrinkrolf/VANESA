package graph.jung.graphDrawing;

import com.google.common.base.Function;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;

/**
 * 
 * @author cbrinkrolf
 * 
 */
public class MyEdgeStringer implements
		Function<BiologicalEdgeAbstract, String> {

	public MyEdgeStringer(Pathway pw) {

	}

	@Override
	public String apply(BiologicalEdgeAbstract bea) {
		return bea.getNetworklabel();
	}
}
