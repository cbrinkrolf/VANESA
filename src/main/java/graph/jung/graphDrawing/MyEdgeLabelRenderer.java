package graph.jung.graphDrawing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.GraphSettings;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * Edge label renderer based on a JLabel.
 */
public class MyEdgeLabelRenderer extends BasicEdgeLabelRenderer<BiologicalNodeAbstract, BiologicalEdgeAbstract>
		implements Renderer.EdgeLabel<BiologicalNodeAbstract, BiologicalEdgeAbstract>, Serializable {
	private static final long serialVersionUID = 6360860594551716099L;

	private final GraphSettings settings = GraphSettings.getInstance();
	private final Color pickedEdgeLabelColor;
	private boolean rotateEdgeLabels;
	private boolean disabled;

	public MyEdgeLabelRenderer(final Color pickedEdgeLabelColor) {
		this(pickedEdgeLabelColor, true);
	}

	public MyEdgeLabelRenderer(final Color pickedEdgeLabelColor, final boolean rotateEdgeLabels) {
		super();
		this.pickedEdgeLabelColor = pickedEdgeLabelColor;
		this.rotateEdgeLabels = rotateEdgeLabels;

	}

	@Override
	public void labelEdge(RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> rc,
			Layout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout, BiologicalEdgeAbstract e, String label) {
		if (disabled) {
			return;
		}
		if (label == null || label.length() == 0)
			return;

		Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph = layout.getGraph();
		// don't draw edge if either incident vertex is not drawn
		Pair<BiologicalNodeAbstract> endpoints = graph.getEndpoints(e);
		BiologicalNodeAbstract v1 = endpoints.getFirst();
		BiologicalNodeAbstract v2 = endpoints.getSecond();
		if (!rc.getEdgeIncludePredicate().apply(Context
				.<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>getInstance(graph, e)))
			return;

		if (!rc.getVertexIncludePredicate().apply(Context
				.<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalNodeAbstract>getInstance(graph, v1))
				&& !rc.getVertexIncludePredicate().apply(Context
						.<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalNodeAbstract>getInstance(
								graph, v2)))
			return;

		Point2D p1 = layout.apply(v1);
		Point2D p2 = layout.apply(v2);
		p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
		p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();

		GraphicsDecorator g = rc.getGraphicsContext();
		float distX = x2 - x1;
		float distY = y2 - y1;
		double totalLength = Math.sqrt(distX * distX + distY * distY);

		double closeness = rc.getEdgeLabelClosenessTransformer().apply(Context
				.<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>getInstance(graph, e))
				.doubleValue();

		int posX = (int) (x1 + (closeness) * distX);
		int posY = (int) (y1 + (closeness) * distY);

		int xDisplacement = (int) (rc.getLabelOffset() * (distY / totalLength));
		int yDisplacement = (int) (rc.getLabelOffset() * (-distX / totalLength));

		Component component = prepareRenderer(rc, rc.getEdgeLabelRenderer(), label, rc.getPickedEdgeState().isPicked(e),
				e);

		Dimension d = component.getPreferredSize();

		Shape edgeShape = rc.getEdgeShapeTransformer().apply(e);

		double parallelOffset = 1;

		parallelOffset += rc.getParallelEdgeIndexFunction().getIndex(graph, e);

		parallelOffset *= d.height;
		if (edgeShape instanceof Ellipse2D) {
			parallelOffset += edgeShape.getBounds().getHeight();
			parallelOffset = -parallelOffset;
		}

		AffineTransform old = g.getTransform();
		AffineTransform xform = new AffineTransform(old);
		xform.translate(posX + xDisplacement, posY + yDisplacement);
		double dx = x2 - x1;
		double dy = y2 - y1;
		if (rotateEdgeLabels) {
			double theta = Math.atan2(dy, dx);
			if (dx < 0) {
				theta += Math.PI;
			}
			xform.rotate(theta);
		}
		if (dx < 0) {
			parallelOffset = -parallelOffset;
		}

		if (rc.getPickedEdgeState().isPicked(e)) {
			component.setForeground(pickedEdgeLabelColor);
		} else if (settings.isBackgroundColor()) {
			component.setForeground(Color.WHITE);
		}

		xform.translate(-d.width / 2, -(d.height / 2 - parallelOffset));
		g.setTransform(xform);
		g.draw(component, rc.getRendererPane(), 0, 0, d.width, d.height, true);

		g.setTransform(old);
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}
}
