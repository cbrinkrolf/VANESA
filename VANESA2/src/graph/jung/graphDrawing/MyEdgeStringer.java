package graph.jung.graphDrawing;

import org.apache.commons.collections15.Transformer;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;

/**
 * 
 * @author cbrinkrolf
 * 
 */
public class MyEdgeStringer implements
		Transformer<BiologicalEdgeAbstract, String> {

	public MyEdgeStringer(Pathway pw) {

	}

	@Override
	public String transform(BiologicalEdgeAbstract bea) {
		return bea.getNetworklabel();
	}
}
