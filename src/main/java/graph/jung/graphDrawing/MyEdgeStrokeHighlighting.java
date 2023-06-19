package graph.jung.graphDrawing;

import java.awt.BasicStroke;
import java.awt.Stroke;

import com.google.common.base.Function;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyEdgeStrokeHighlighting implements Function<BiologicalEdgeAbstract, Stroke> {
    private static final Stroke basic = new BasicStroke(2);
    private static final Stroke heavy = new BasicStroke(4);
    private static final Stroke nogt = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
                                                       new float[]{2}, 0);
    private static final float log = (float) Math.log(1.5);
    private static final float pickedFactor = 1.4f;
    private static final Stroke dotted = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
                                                         new float[]{10}, 0);
    private static final Stroke dottedPicked = new BasicStroke(2 * pickedFactor, BasicStroke.CAP_BUTT,
                                                               BasicStroke.JOIN_MITER, 10, new float[]{10}, 0);

    private final PickedState<BiologicalNodeAbstract> psV;
    private final PickedState<BiologicalEdgeAbstract> psE;
    private final Pathway pw;
    private boolean graphTheory = false;

    public MyEdgeStrokeHighlighting(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE,
                                    Pathway pw) {
        this.psV = psV;
        this.psE = psE;
        this.pw = pw;
    }

    private Stroke getStrokeWithoutGraphTheory(BiologicalEdgeAbstract bea) {
        if (pw.isBNA()) {
            BiologicalNodeAbstract pathway = (BiologicalNodeAbstract) pw;
            if (pathway.getEnvironment().contains(bea.getFrom()) || pathway.getEnvironment().contains(bea.getTo())) {
                return nogt;
            }
        }
        float strength = (float) Math.log1p(pw.edgeGrade(bea)) / log;
        strength = Math.max(strength, 2);
        Stroke hierarchical = new BasicStroke(strength);
        Stroke picked = new BasicStroke(pickedFactor * strength);
        if (psV.getPicked().isEmpty()) {
            if (psE.getPicked().isEmpty()) {
                if (bea.getBiologicalElement().equals(Elementdeclerations.pnTestArc)) {
                    return dotted;
                }
                return hierarchical;
            } else {
                if (psE.isPicked(bea)) {
                    if (bea.getBiologicalElement().equals(Elementdeclerations.pnTestArc)) {
                        return dottedPicked;
                    }
                    return picked;
                }
                return dotted;
            }
        } else {
            if (psV.isPicked(bea.getFrom()) || psV.isPicked(bea.getTo())) {
                if (bea.getBiologicalElement().equals(Elementdeclerations.pnTestArc)) {
                    return dottedPicked;
                }
                return picked;
            }
            return dotted;
        }
    }

    private Stroke getStrokeWithGraphTheory(BiologicalEdgeAbstract bea) {
        if (psV.isPicked(bea.getFrom()) && psV.isPicked(bea.getTo())) {
            return heavy;
        }
        return dotted;
    }

    public void setGraphTheory(boolean graphTheory) {
        this.graphTheory = graphTheory;
    }

    @Override
    public Stroke apply(BiologicalEdgeAbstract bea) {
        if (graphTheory) {
            return getStrokeWithGraphTheory(bea);
        }
        return getStrokeWithoutGraphTheory(bea);
    }
}
