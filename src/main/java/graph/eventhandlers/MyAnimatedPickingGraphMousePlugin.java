package graph.eventhandlers;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import graph.jung.classes.MyVisualizationViewer;

public class MyAnimatedPickingGraphMousePlugin
		extends PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
	private boolean inWindow = false;
	private Pathway pw = null;
	private MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = null;

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
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
			setPathway(e);
			vv.setMousePoint(vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
		}
	}

	@SuppressWarnings("unchecked")
	private void setPathway(MouseEvent e) {
		// do not use GraphInstance.getPathway because graphs for transformation rules
		// also need mouse control
		if (this.pw == null) {
			vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();
		}
	}
}
