package graph.rendering;

import biologicalElements.Pathway;
import biologicalElements.PathwayType;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.VanesaGraph;
import graph.annotations.VanesaAnnotation;

import java.awt.*;

public class VanesaGraphRendererPanel
		extends GraphRendererPanel<BiologicalNodeAbstract, BiologicalEdgeAbstract, VanesaAnnotation> {
	private static final Font INFO_FONT = new Font("Arial", Font.BOLD, 12);

	public VanesaGraphRendererPanel(final VanesaGraph graph) {
		super(graph);
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final var g2d = (Graphics2D) g;
		final var viewportBounds = getBounds();
		// Render info texts
		g2d.setFont(INFO_FONT);
		g2d.setColor(Color.RED);
		g2d.drawString(String.format("Zoom %.3fx", getZoom()), (float) viewportBounds.getWidth() - 74, 12);
		g2d.drawString(String.format("FPS %3d", getLastFps()), (float) viewportBounds.getWidth() - 74, 26);
		// if (mousePosition != null)
		// 	g2d.drawString(mousePosition.x + "," + mousePosition.y, (float) viewportBounds.getWidth() - 74, 40);
		if (graph.getContext() instanceof Pathway) {
			final Pathway pathway = (Pathway) graph.getContext();
			if (pathway.getType() == PathwayType.PetriNet) {
				g2d.drawString(
						String.format("P: %s, T: %s, Arcs: %s", pathway.getPlaceCount(), pathway.getTransitionCount(),
								graph.getEdgeCount()), 4, 12);
			} else {
				g2d.drawString(String.format("Nodes: %s, Edges: %s", graph.getNodeCount(), graph.getEdgeCount()), 4,
						12);
			}
		} else {
			g2d.drawString(String.format("Nodes: %s, Edges: %s", graph.getNodeCount(), graph.getEdgeCount()), 4, 12);
		}
	}
}
