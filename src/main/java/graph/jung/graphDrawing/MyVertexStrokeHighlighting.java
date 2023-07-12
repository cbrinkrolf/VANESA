package graph.jung.graphDrawing;

import java.awt.BasicStroke;
import java.awt.Stroke;

import com.google.common.base.Function;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.Place;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyVertexStrokeHighlighting implements Function<BiologicalNodeAbstract, Stroke> {
    private final Stroke pn_heavy = new BasicStroke(5);
    private final Stroke pn_medium = new BasicStroke(4);
    private final Stroke pn_light = new BasicStroke(2);
    private final Stroke heavy = new BasicStroke(3);
    private final Stroke superHeavy = new BasicStroke(6);
    private final Stroke medium = new BasicStroke(2);
    private final Stroke light = new BasicStroke(1);
    private final PickedState<BiologicalNodeAbstract> psV;
    private final PickedState<BiologicalEdgeAbstract> psE;
    private boolean graphTheory = false;
    private final Pathway pw;
    private final BasicStroke basicStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.5f,
                                                            new float[]{5, 5}, 0);
    private final BasicStroke refStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5,
                                                          new float[]{6, 3}, 0);
    private final BasicStroke groupStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5,
                                                            new float[]{2, 2}, 0);

    public MyVertexStrokeHighlighting(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE,
                                      Pathway pw) {
        this.psV = psV;
        this.psE = psE;
        this.pw = pw;
    }

    public Stroke withGraphTheory(BiologicalNodeAbstract v) {
        if (pw.isPetriNet()) {
            if (psV.isPicked(v))
                return pn_heavy;
            return pn_light;
        }
        if (psV.isPicked(v)) {
            return heavy;
        }
        return light;
    }

    public Stroke withoutGraphTheory(BiologicalNodeAbstract bna) {
        boolean petriNet = pw.isPetriNet();
        boolean isContPlace = false;

        // mark Environment nodes in hierarchical Nodes.
        if (bna.isEnvironmentNodeOf(pw)) {
            return basicStroke;
        }

        if (((bna instanceof Place && !bna.isDiscrete()) || bna instanceof ContinuousTransition)) {
            isContPlace = true;
        }

        if (psV.getPicked().isEmpty()) {
            if (psE.getPicked().isEmpty()) {
                if (petriNet && !isContPlace) {
                    return pn_medium;
                }
                return medium;
            } else {
                for (BiologicalEdgeAbstract bea : psE.getPicked()) {
                    if (bna == bea.getFrom() || bna == bea.getTo()) {
                        if (petriNet && !isContPlace) {
                            return pn_medium;
                        }
                        return medium;
                    }
                }
                if (petriNet && !isContPlace) {
                    return pn_medium;
                }
                return light;
            }

        } else {
            if (psV.isPicked(bna)) {
                if (petriNet && !isContPlace) {
                    return pn_heavy;
                }
                if (bna.getGroups().size() > 0) {
                    return groupStroke;
                }
                return heavy;
            } else {
                for (BiologicalNodeAbstract w : pw.getGraph().getJungGraph().getNeighbors(bna)) {
                    if (psV.isPicked(w)) {
                        if (petriNet && !isContPlace) {
                            return pn_medium;
                        }
                        return medium;
                    }
                }

                for (BiologicalEdgeAbstract bea : psE.getPicked()) {
                    if (bna == bea.getFrom() || bna == bea.getTo()) {
                        if (petriNet && !isContPlace) {
                            return pn_medium;
                        }
                        return medium;
                    }
                }

                if (petriNet && !isContPlace) {
                    return pn_light;
                }
                return light;
            }
        }
    }

    public void setGraphTheory(boolean graphTheory) {
        this.graphTheory = graphTheory;
    }

    @Override
    public Stroke apply(BiologicalNodeAbstract bna) {
        if (bna.isLogical()) {
            return refStroke;
        }
        if (graphTheory) {
            return withGraphTheory(bna);
        }
        return withoutGraphTheory(bna);
    }
}
