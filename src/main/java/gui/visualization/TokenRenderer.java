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

	private Pathway pathway = null;
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
		if (pathway == null) {
			// System.out.println("pw null");
			return;
		}
		// System.out.println(graphInstance.getPathway().getAllGraphNodes());
		if (pathway.getAllGraphNodes().isEmpty()) {
			// System.out.println("empty");
			return;
		}
		if(vv == null){
			vv = pathway.getGraph().getVisualizationViewer();
		}

		g.setColor(Color.BLACK);
		double scaleL = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
		double scaleV = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
		double scale;
		if (scaleV < 1) {
			scale = scaleV;
		} else {
			scale = scaleL;
		}
		scale = ((double) ((int) (scale * 100)) / 100);

		int fontSize = (int) (14.0 * scaleV);
		g.setFont(new Font("Arial", Font.PLAIN, fontSize));

		Place p;
		int x1;
		int xpos;
		Point2D p1inv;
		int y;
		boolean discrete;
		String tokens;
		for (BiologicalNodeAbstract bna : pathway.getAllGraphNodes()) {
			if (bna instanceof Place) {
				p = (Place) bna;
				x1 = (int) (p.getShape().getBounds2D().getMaxX() - p.getShape().getBounds2D().getMinX());
				// int y1 = (int) (p.getShape().getBounds2D().getMaxY()
				// - p.getShape()
				// .getBounds2D().getMinY());

				// double x1 =
				// c.getBounds().getMaxX()-c.getBounds().getMinX();
				// double y1 =
				// c.getBounds().getMaxY()-c.getBounds().getMinY();

				discrete = false;

				tokens = p.getToken() + "";

				if (pathway.isHeadless()) {
					tokens = "";
				} else {
					if (p.isDiscrete()) {
						tokens = (int) p.getToken() + "";
						discrete = true;
					}

					if (p.isLogical() && p.getLogicalReference() instanceof Place) {
						tokens = ((Place) p.getLogicalReference()).getToken() + "";
						if (((Place) p.getLogicalReference()).isDiscrete()) {
							tokens = (int) ((Place) p.getLogicalReference()).getToken() + "";
							discrete = true;
						}
					}
				}

				// System.out.println(pathway.isHeadless());
				// System.out.println(pathway.getGraph().getVisualizationViewer().getPathway().isHeadless());

				

				// Point2D point = pw.getGraph().getVertexLocation(p);//
				// pw.getGraph().getVisualizationViewer().getGraphLayout().transform(bna);

				// g2d.drawString("b",(int)((p.getX())), (int)((p.getY())*scale));
				p1inv = vv.getRenderContext().getMultiLayerTransformer().transform(vv.getGraphLayout().apply(p));

				if (discrete) {
					xpos = Double.valueOf(p1inv.getX() - x1 + (19 - 5 * (double) tokens.length() / 2)).intValue();
				} else {
					xpos = Double.valueOf(p1inv.getX() - x1 + (21 - 5 * (double) tokens.length() / 2)).intValue();
				}

				y = (int) p1inv.getY();
				// g2.draw(AffineTransform.getScaleInstance(p.getNodesize(),
				// p.getNodesize()).createTransformedShape(s));

				// g.getFont()

				// g.setFont(new Font);
				// g2.draw(s);
				g.drawString(tokens, xpos, (int) (y + 7 * scaleV));
			}
		}
	}
}
