package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphInstance;

public class LocalBackboardPaintable implements VisualizationViewer.Paintable {
	private final HashSet<BiologicalNodeAbstract> bnas;
	private Color bgcolor;
	private int drawsize;
	private String shape;
	private boolean active = true;
	private String name;

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
	 *            - rect, oval, roundrect, fadeoval (oval with fade to
	 *            transparent)
	 */
	public LocalBackboardPaintable(HashSet<BiologicalNodeAbstract> bnas, Color bgcolor, int drawsize, String shape,
								   String name) {
		this.bnas = bnas;
		this.bgcolor = bgcolor;
		this.drawsize = drawsize;
		this.shape = shape;
		this.name = name;
	}

	@Override
	public void paint(Graphics g) {
		if (active) {
			VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance
					.getPathway().getGraph().getVisualizationViewer();
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform oldXform = g2d.getTransform();
			AffineTransform lat = vv.getRenderContext()
					.getMultiLayerTransformer().getTransformer(Layer.LAYOUT)
					.getTransform();
			AffineTransform vat = vv.getRenderContext()
					.getMultiLayerTransformer().getTransformer(Layer.VIEW)
					.getTransform();
			AffineTransform at = new AffineTransform();
			at.concatenate(g2d.getTransform());
			at.concatenate(vat);
			at.concatenate(lat);
			g2d.setTransform(at);
			g2d.setColor(bgcolor);
			for (BiologicalNodeAbstract bna : bnas) {
				vv.getModel().getGraphLayout().getSize();
				Point2D p = GraphInstance.getGraph().getNodePosition(bna);
				double px = p.getX();
				double py = p.getY();
				int drawSizeHalf = drawsize / 2;
				switch (shape) {
					case "rect":
						g2d.fill(new Rectangle2D.Double(px - drawSizeHalf, py - drawSizeHalf, drawsize, drawsize));
						break;
					case "roundrect":
						g2d.fill(new RoundRectangle2D.Double(px - drawSizeHalf, py - drawSizeHalf, drawsize, drawsize,
															 10.0d, 10.0d));
						break;
					case "fadeoval":
						float[] fracs = { 0.0f, 1.0f };
						Color[] colors = { bgcolor, new Color(bgcolor.getRed(), bgcolor.getGreen(), bgcolor.getBlue(), 0) };
						RadialGradientPaint gp = new RadialGradientPaint(p, drawSizeHalf, fracs, colors);
						g2d.setPaint(gp);
						g2d.fill(new Ellipse2D.Double(px - drawSizeHalf, py - drawSizeHalf, drawsize, drawsize));
						break;
					case "oval":
					default:
						g2d.fill(new Ellipse2D.Double(px - drawSizeHalf, py - drawSizeHalf, drawsize, drawsize));
						break;
				}
			}
			g2d.setTransform(oldXform);
		}
	}

	public Color getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(Color bgcolor) {
		this.bgcolor = bgcolor;
	}

	public int getDrawsize() {
		return drawsize;
	}

	public void setDrawsize(int drawsize) {
		this.drawsize = drawsize;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean useTransform() {
		return false;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
}
