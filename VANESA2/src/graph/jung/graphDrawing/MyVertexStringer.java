package graph.jung.graphDrawing;

import org.apache.commons.collections15.Transformer;

import petriNet.Place;

import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * 
 * @author cbrinkrolf
 * 
 */
public class MyVertexStringer implements
		Transformer<BiologicalNodeAbstract, String> {

	public MyVertexStringer() {
	}

	@Override
	public String transform(BiologicalNodeAbstract bna) {
		
		if(bna instanceof Place){
			return ((Place)bna).getToken() + " | " +bna.getNetworklabel();
		}
		
		return bna.getNetworklabel();
	}
}
