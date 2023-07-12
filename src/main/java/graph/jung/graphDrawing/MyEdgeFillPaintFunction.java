package graph.jung.graphDrawing;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import com.google.common.base.Function;
import edu.uci.ics.jung.visualization.picking.PickedState;

import java.awt.*;

public class MyEdgeFillPaintFunction implements Function<BiologicalEdgeAbstract, Paint> {
    private final PickedState<BiologicalNodeAbstract> psV;
    private final PickedState<BiologicalEdgeAbstract> psE;
    private boolean graphTheory = false;

    public MyEdgeFillPaintFunction(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE) {
        this.psV = psV;
        this.psE = psE;
    }

    public void setGraphTheory(boolean graphTheory) {
        this.graphTheory = graphTheory;
    }

    @Override
    public Paint apply(BiologicalEdgeAbstract bea) {
        return null;
    }
}
