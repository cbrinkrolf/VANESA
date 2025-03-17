package gui.visualization;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.jung.classes.MyVisualizationViewer;

public class TokenRenderer implements VisualizationViewer.Paintable {
	private final Pathway pathway;
	private MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = null;

	public TokenRenderer(Pathway pw) {
		pathway = pw;
	}

	@Override
	public boolean useTransform() {
		return false;
	}

	@Override
	public void paint(Graphics g) {
		if (pathway == null || pathway.getAllGraphNodes().isEmpty()) {
			return;
		}
		if (vv == null) {
			vv = pathway.getGraph().getVisualizationViewer();
		}

		g.setColor(Color.BLACK);
		double scaleL = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
		double scaleV = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
		double scale = scaleV < 1 ? scaleV : scaleL;
		scale = ((double) ((int) (scale * 100)) / 100);

		int fontSize = (int) (14.0 * scaleV);
		g.setFont(new Font("Arial", Font.PLAIN, fontSize));

		for (BiologicalNodeAbstract bna : pathway.getAllGraphNodes()) {
			if (bna instanceof Place) {
				Place p = (Place) bna;
				int x1 = (int) (p.getShape().getBounds2D().getMaxX() - p.getShape().getBounds2D().getMinX());
				boolean discrete = false;
				String tokens = p.getToken() + "";
				if (pathway.isHeadless()) {
					tokens = "";
				} else {
					if (p.isDiscrete()) {
						tokens = (int) p.getToken() + "";
						discrete = true;
					}
					if (p.isLogical() && p.getLogicalReference() instanceof Place) {
						tokens = ((Place) p.getLogicalReference()).getToken() + "";
						if (p.getLogicalReference().isDiscrete()) {
							tokens = (int) ((Place) p.getLogicalReference()).getToken() + "";
							discrete = true;
						}
					}
				}
				Point2D p1inv = vv.getRenderContext().getMultiLayerTransformer().transform(
						vv.getGraphLayout().apply(p));
				int xpos = (int)(p1inv.getX() - x1 + ((discrete ? 19 : 21) - 5 * (double) tokens.length() / 2));
				int y = (int) p1inv.getY();
				g.drawString(tokens, xpos, (int) (y + 7 * scaleV));
			}
		}
	}
}
