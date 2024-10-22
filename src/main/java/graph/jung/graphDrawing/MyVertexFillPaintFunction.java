package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;

import com.google.common.base.Function;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyVertexFillPaintFunction implements Function<BiologicalNodeAbstract, Paint> {

    protected PickedState<BiologicalNodeAbstract> psV;
    protected PickedState<BiologicalEdgeAbstract> psE;

    private Pathway pw;
    protected boolean graphTheory = false;

    public MyVertexFillPaintFunction(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE,
                                     Pathway pw) {
        this.psV = psV;
        this.psE = psE;
        this.pw = pw;
    }

    private Paint getFillPaintWithoutGraphTheory(BiologicalNodeAbstract v) {
        if (pw.getRootNode() == v) {
            if (psV.getPicked().isEmpty() || psV.getPicked().contains(v)) {
                return Color.RED;
            }
        }

        // mark Environment nodes in hierarchical Nodes.
        if (pw instanceof BiologicalNodeAbstract) {
            BiologicalNodeAbstract bna = (BiologicalNodeAbstract) pw;
            if (bna.getEnvironment().contains(v)) {
                return v.getColor().brighter();
            }
        }

        if (psV.getPicked().isEmpty()) {
            if (psE.getPicked().isEmpty()) {
                return v.getColor();
            } else {
                for (BiologicalEdgeAbstract bea : psE.getPicked()) {
                    if (v == bea.getFrom() || v == bea.getTo()) {
                        return v.getColor().brighter().brighter();
                    }
                }
                return Color.LIGHT_GRAY.brighter();
            }
        } else {
            if (psV.isPicked(v))
                return v.getColor();
            else {
                for (BiologicalNodeAbstract w : pw.getGraph().getJungGraph().getNeighbors(v)) {
                    if (psV.isPicked(w))
                        return v.getColor().brighter().brighter();
                }
                for (BiologicalEdgeAbstract bea : psE.getPicked()) {
                    if (v == bea.getFrom() || v == bea.getTo())
                        return v.getColor().brighter().brighter();
                }
                return Color.LIGHT_GRAY.brighter();
            }
        }
    }

    private Paint getFillPaintWithGraphTheory(BiologicalNodeAbstract v) {
        if (psV.isPicked(v)) {
            return Color.YELLOW;
        } else {
            return Color.white;
        }

    }

    public void setGraphTheory(boolean graphTheory) {
        this.graphTheory = graphTheory;
    }

    @Override
    public Paint apply(BiologicalNodeAbstract bna) {
        if (graphTheory) {
            return getFillPaintWithGraphTheory(bna);
        }
        return getFillPaintWithoutGraphTheory(bna);
    }
}
