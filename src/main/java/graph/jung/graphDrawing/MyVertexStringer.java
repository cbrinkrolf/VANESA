package graph.jung.graphDrawing;

import com.google.common.base.Function;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class MyVertexStringer implements Function<BiologicalNodeAbstract, String> {
    @Override
    public String apply(BiologicalNodeAbstract bna) {
        //if(bna instanceof Place){
        //	return ((Place)bna).getToken() + " | " +bna.getNetworklabel();
        //}
        if (bna.isLogical()) {
            return bna.getLogicalReference().getNetworklabel();
        }
        return bna.getNetworklabel();
    }
}
