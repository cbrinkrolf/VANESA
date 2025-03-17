package gui.visualization;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.AffineTransformer;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import graph.compartment.Compartment;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.GraphCenter;

public class CompartmentRenderer implements VisualizationViewer.Paintable {
	private Pathway pw;
	private MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;
	private HashMap<String, Area> areas = new HashMap<>();
	private boolean experimental = false;

	public CompartmentRenderer(final Pathway pw) {
		this.pw = pw;
	}

	public void setPathway(final Pathway pw) {
		this.pw = pw;
	}

	@Override
	public void paint(final Graphics g) {
		prepareCompartments();
		drawCompartments((Graphics2D) g);

	}

	private void prepareCompartments() {
		areas = new HashMap<>();
		HashMap<String, HashSet<BiologicalNodeAbstract>> compToNodes = new HashMap<>();
		HashMap<String, HashSet<BiologicalEdgeAbstract>> compToEdges = new HashMap<>();
		for (final Compartment c : pw.getCompartmentManager().getAllCompartmentsAlphabetically()) {
			areas.put(c.getName(), new Area());
			compToNodes.put(c.getName(), new HashSet<>());
			compToEdges.put(c.getName(), new HashSet<>());
		}
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (pw.getCompartmentManager().getCompartment(bna).isEmpty()) {
				continue;
			}
			final Area a = areas.get(pw.getCompartmentManager().getCompartment(bna));

			if (!compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).contains(bna)) {
				this.addNodeToArea(bna, a);
				compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).add(bna);
			}
			for (BiologicalEdgeAbstract bea : pw.getGraph2().getInEdges(bna)) {
				if (!compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea)) {
					this.addEdgeToArea(bea, a);
					compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).add(bea);
				}
				if (!compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea.getFrom())) {
					this.addNodeToArea(bea.getFrom(), a);
					compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).add(bea.getFrom());
				}
			}
			for (BiologicalEdgeAbstract bea : pw.getGraph2().getOutEdges(bna)) {
				if (!compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea)) {
					this.addEdgeToArea(bea, a);
					compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).add(bea);
				}
				if (!compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea.getTo())) {
					this.addNodeToArea(bea.getTo(), a);
					compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).add(bea.getTo());
				}
			}
		}
	}

	private void addEdgeToArea(BiologicalEdgeAbstract bea, Area a) {
		final Point2D p1 = pw.getGraph().getVertexLocation(bea.getFrom());
		final Point2D p2 = pw.getGraph().getVertexLocation(bea.getTo());
		final Point2D p1inv = vv.getRenderContext().getMultiLayerTransformer().transform(p1);
		final Point2D p2inv = vv.getRenderContext().getMultiLayerTransformer().transform(p2);
		double width = 12;
		if (pw.getGraph().getVisualizationViewer().getScale() < 1) {
			width *= pw.getGraph().getVisualizationViewer().getScale();
		}

		final Polygon poly1 = new Polygon();
		poly1.addPoint((int) (p1inv.getX() + width), (int) (p1inv.getY() + width));
		poly1.addPoint((int) (p1inv.getX() - width), (int) (p1inv.getY() - width));
		poly1.addPoint((int) (p2inv.getX() - width), (int) (p2inv.getY() - width));
		poly1.addPoint((int) (p2inv.getX() + width), (int) (p2inv.getY() + width));

		final Polygon poly2 = new Polygon();
		poly2.addPoint((int) (p1inv.getX() + width), (int) (p1inv.getY() - width));
		poly2.addPoint((int) (p1inv.getX() - width), (int) (p1inv.getY() + width));
		poly2.addPoint((int) (p2inv.getX() - width), (int) (p2inv.getY() + width));
		poly2.addPoint((int) (p2inv.getX() + width), (int) (p2inv.getY() - width));
		a.add(new Area(poly1));
		a.add(new Area(poly2));
	}

	private void addNodeToArea(BiologicalNodeAbstract bna, Area a) {
		final Point2D p1 = pw.getGraph().getVertexLocation(bna);
		Point2D p1inv = vv.getRenderContext().getMultiLayerTransformer().transform(p1);
		Shape s1 = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).transform(
				bna.getShape().getBounds2D());
		if (experimental) {
			p1inv = p1;
			s1 = bna.getShape().getBounds2D();
		}
		final int h1 = (int) s1.getBounds2D().getHeight() / 2;
		RoundRectangle2D r1 = new RoundRectangle2D.Double((int) (p1inv.getX() - h1 * 1.5),
				(int) (p1inv.getY() - h1 * 1.5), 3 * h1, 3 * h1, 15, 15);
		a.add(new Area(r1));
	}

	private void drawCompartments(final Graphics2D g2d) {
		for (final String comp : areas.keySet()) {
			g2d.setColor(pw.getCompartmentManager().getCompartment(comp).getColor());
			g2d.fill(areas.get(comp));
		}
	}

	@Override
	public boolean useTransform() {
		return false;
	}

	// CHRIS WIP for an more efficient painting of compartments
	private void drawCompartemntsExperimental(Graphics2D g2d) {

		long l1 = System.nanoTime();
		Iterator<String> it = this.areas.keySet().iterator();
		String comp;
		while (it.hasNext()) {
			comp = it.next();

			double scale;
			MutableTransformer mt;
			double scaleV = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
			if (scaleV < 1) {
				// mt =
				// this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
				mt = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
				// System.out.println("smaller");
			} else {
				// mt=
				mt = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
				// mt =
				// this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
			}

			AffineTransformer transformer = null;

			if (mt instanceof AffineTransformer) {
				transformer = (AffineTransformer) mt;
				// System.out.println("aff");
			} else if (mt instanceof LensTransformer) {
				// System.out.println("lens");
				transformer = (AffineTransformer) ((LensTransformer) mt).getDelegate();
			}

			// this.getRenderContext()

			// AffineTransformer at =
			// (AffineTransformer)((LensTransformer)mt).getDelegate();
			Area a = areas.get(comp);

			// at.setrot
			// mt.get
			// a.createTransformedArea(mt.getTransform());
			// System.out.println(mt.transform(new Point2D.Double(mt.getTranslateX(),
			// mt.getTranslateY())));
			GraphCenter graphCenter = new GraphCenter(pw.getGraph());

			// System.out.println("drin");
			// Layout layout = viewer.getGraphLayout();
			Point2D q = graphCenter.getCenter();
			// Point2D q = viewer.getCenter();
			// System.out.println("r: "+r);
			// System.out.println("q: "+q);
			Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
			Point2D q1 = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(q);
			// System.out.println("q: "+viewer.getCenter());
			// System.out.println("lvc: " + lvc);
			// System.out.println(lvc);
			final double dx = (lvc.getX() - q.getX());
			final double dy = (lvc.getY() - q.getY());
			// System.out.println(viewer.getClass()+" "+dx+" "+dy);

			// System.out.println(" " + dx + " " + dy);
			// System.out.println("center:" + q);
			// System.out.println("center2:" + getCenter());
			// System.out.println("center3:" + q1);
			// viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx,
			// dy);
			AffineTransform at = new AffineTransform();
			// at.translate(-dx, 0);
			// at.translate((lvc.getX()), (lvc.getY()));

			at.scale(dx, dy);
			// mt.setTranslate(tX, tY);
			// mt.setTranslate(mt.getTranslateX(), mt.getTranslateY());
			// Area area = a.createTransformedArea(mt.getTransform());
			Area area = a.createTransformedArea(transformer.getTransform());

			area.transform(at);
//Area area = a.createTransformedArea(new AffineTransform(dx, dy,0,0,0,0));

			// System.out.println(getRenderContext().getMultiLayerTransformer().);
			g2d.setColor(pw.getCompartmentManager().getCompartment(comp).getColor());
			// g2d.fill(areas.get(comp));
			g2d.fill(area);
		}
	}

}
