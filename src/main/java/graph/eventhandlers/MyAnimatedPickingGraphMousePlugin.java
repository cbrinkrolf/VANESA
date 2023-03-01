package graph.eventhandlers;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import graph.GraphInstance;
import graph.jung.classes.MyVisualizationViewer;

public class MyAnimatedPickingGraphMousePlugin extends PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
    private final GraphInstance graphInstance = new GraphInstance();
    private boolean inWindow = false;

    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = graphInstance.getPathway().getGraph().getVisualizationViewer();
        vv.getPickedVertexState().getPicked().size();
        if (vv.getPickedVertexState().getPicked().size() == 1) {
            Layout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout = vv.getGraphLayout();
            Point2D q = layout.apply(vv.getPickedVertexState().getPicked().iterator().next());
            Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
            final double dx = (lvc.getX() - q.getX()) / 10;
            final double dy = (lvc.getY() - q.getY()) / 10;
            Runnable animator = () -> {
                for (int i = 0; i < 10; i++) {
                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                }
            };
            Thread thread = new Thread(animator);
            thread.start();
        }
    }

    public void mouseEntered(MouseEvent e) {
        inWindow = true;
    }

    public void mouseExited(MouseEvent e) {
        inWindow = false;
    }

    public void mouseMoved(MouseEvent e) {
        if (inWindow) {
            @SuppressWarnings("unchecked")
            final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv =
                    (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();
            vv.setMousePoint(vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
        }
    }
}
