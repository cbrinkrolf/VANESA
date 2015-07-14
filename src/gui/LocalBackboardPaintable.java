package gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphInstance;

public class LocalBackboardPaintable implements VisualizationViewer.Paintable {

	private HashSet<BiologicalNodeAbstract> bnas;
	private Color bgcolor;
	private int drawsize;
	private String shape;

	/**
	 * Background painter for BiologicalNodeAbstract(s).
	 * 
	 * 
	 * @param bnas
	 *            - Set of BiologicalNodeAbstract s that should be emphasized.
	 * @param bgcolor
	 *            - Shapecolor (AWT).
	 * @param drawsize
	 *            - size of the shape.
	 * @param shape
	 *            - rect, oval, roundrect, fadeoval (oval with fade to transparent)
	 */
	public LocalBackboardPaintable(HashSet<BiologicalNodeAbstract> bnas,
			Color bgcolor, int drawsize, String shape) {
		this.bnas = bnas;
		this.bgcolor = bgcolor;
		this.drawsize = drawsize;
		this.shape = shape;
	}

	@Override
	public void paint(Graphics g) {
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance
				.getMyGraph().getVisualizationViewer();
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform oldXform = g2d.getTransform();
		AffineTransform lat = vv.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).getTransform();
		AffineTransform vat = vv.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.VIEW).getTransform();
		AffineTransform at = new AffineTransform();
		at.concatenate(g2d.getTransform());
		at.concatenate(vat);
		at.concatenate(lat);
		g2d.setTransform(at);

		g.setColor(bgcolor);
		for (BiologicalNodeAbstract bna : bnas) {
			vv.getModel().getGraphLayout().getSize();
			Point2D p = GraphInstance.getMyGraph().getVertexLocation(bna);
			double px, py;
		
			
			px = p.getX();
			py = p.getY();

			switch (shape) {
			case "rect":
				g.fillRect((int) (px - (drawsize / 2)),
						(int) (py - (drawsize / 2)), drawsize, drawsize);			
				break;
			case "roundrect":
				g.fillRoundRect((int) (px - (drawsize / 2)),
						(int) (py - (drawsize / 2)), drawsize, drawsize, 10, 10);
				break;
			case "oval":
				g.fillOval((int) (px - (drawsize / 2)),
						(int) (py - (drawsize / 2)), drawsize, drawsize);
				break;		
			case "fadeoval":
				float[] fracs = {0.0f, 1.0f};
				Color[] colors = {bgcolor,new Color(bgcolor.getRed(),bgcolor.getGreen(),bgcolor.getBlue(),0)};
				RadialGradientPaint gp = new RadialGradientPaint(p, drawsize/2, fracs, colors);
				g2d.setPaint(gp);
				g2d.fill(new Ellipse2D.Double(px-(drawsize/2),py-(drawsize/2),drawsize,drawsize));
				break;			

			default:
				System.out.println("LocalBackboardPaintable: shapte not found, using OVAL.");
				g.fillOval((int) (px - (drawsize / 2)),
						(int) (py - (drawsize / 2)), drawsize, drawsize);
				break;
			}
		}

		g2d.setTransform(oldXform);
	}

	public boolean useTransform() {
		return false;
	}

}
