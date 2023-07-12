package graph.jung.graphDrawing;

import com.google.common.base.Function;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;

public class MyEdgeStringer implements Function<BiologicalEdgeAbstract, String> {
    @Override
    public String apply(BiologicalEdgeAbstract bea) {
        return bea.getNetworklabel();
    }
}
