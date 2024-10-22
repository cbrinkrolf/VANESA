package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Paint;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.GraphSettings;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyEdgeDrawPaintFunction implements Function<BiologicalEdgeAbstract, Paint> {
    private final PickedState<BiologicalNodeAbstract> psV;
    private final PickedState<BiologicalEdgeAbstract> psE;
    private static final Color dotted = Color.LIGHT_GRAY;
    private static final Color dotted_black = Color.BLACK.brighter();
    private final GraphSettings settings = GraphSettings.getInstance();
    private Color alphaEdge = new Color(dotted.darker().getRed(), dotted.darker().getGreen(), dotted.darker().getBlue(), settings.getEdgeOpacity());
    private boolean graphTheory = false;

    public MyEdgeDrawPaintFunction(PickedState<BiologicalNodeAbstract> psV, PickedState<BiologicalEdgeAbstract> psE) {
        this.psV = psV;
        this.psE = psE;
    }

    public Paint getDrawPaintWithGraphTheory(BiologicalEdgeAbstract bea) {
        if (psV.isPicked(bea.getFrom()) && psV.isPicked(bea.getTo())) {
            return Color.BLUE;
        }
        if (settings.isBackgroundColor()) {
            return dotted_black;
        }
        return dotted;
    }

    public Paint getDrawPaintWithoutGraphTheory(BiologicalEdgeAbstract bea) {
        // uncomment for edges
        if (settings.getDrawEdges()) {
            if (psV.getPicked().isEmpty()) {
                if (psE.getPicked().isEmpty()) {
                    if (settings.isBackgroundColor()) {
                        return dotted_black;
                    }
                    return bea.getColor();
                }
                if (psE.isPicked(bea)) {
                    return bea.getColor();
                }
                if (settings.isBackgroundColor()) {
                    return dotted_black;
                }
                return bea.getColor();
            }
            for (BiologicalNodeAbstract bna : psV.getPicked()) {
                if (bna == bea.getFrom() || bna == bea.getTo())
                    return bea.getColor();
            }
            if (psE.isPicked(bea)) {
                return bea.getColor();
            }
            if (settings.isBackgroundColor()) {
                return dotted_black;
            }
            return bea.getColor();
        }
        return null;
    }

    public void setGraphTheory(boolean graphTheory) {
        this.graphTheory = graphTheory;
    }

    @Override
    public Paint apply(BiologicalEdgeAbstract bea) {
        if (graphTheory) {
            return getDrawPaintWithGraphTheory(bea);
        }
        return getDrawPaintWithoutGraphTheory(bea);
    }

    public void updateEdgeAlphaValue() {
        alphaEdge = new Color(dotted.getRed(), dotted.getGreen(), dotted.getBlue(), settings.getEdgeOpacity());
    }
}
