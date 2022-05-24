package graph.jung.classes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.AffineTransformer;
import edu.uci.ics.jung.visualization.transform.LensTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import graph.GraphContainer;
import graph.Compartment.Compartment;
import graph.layouts.GraphCenter;

public class MyVisualizationViewer<V, E> extends VisualizationViewer<V, E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Pathway pw;

	private HashMap<String, Area> areas = new HashMap<String, Area>();

	private Point2D mousePoint = new Point2D.Double(0, 0);

	private boolean drawCompartments = false;
	private boolean experimental = false;

	public MyVisualizationViewer(VisualizationModel<V, E> arg0, Dimension arg2, Pathway pw) {
		super(arg0, arg2);
		this.pw = pw;

	}

	private long lastTimeCompartmentRendered = 0;
	private double scaleLastCompartmentRendered = 0;

	private Graphics2D g2d = null;

	// private long[] relaxTimes = new long[5];

	// private long[] paintTimes = new long[5];

	// private int relaxIndex = 0;

	// private int paintIndex = 0;

	// private double paintfps, relaxfps;

	/**
	 * a collection of user-implementable functions to render under the topology
	 * (before the graph is rendered)
	 */
	// protected List<Paintable> preRenderers = new ArrayList<Paintable>();

	/**
	 * a collection of user-implementable functions to render over the topology
	 * (after the graph is rendered)
	 */
	// protected List<Paintable> postRenderers = new ArrayList<Paintable>();

	protected void renderGraph(Graphics2D g2d) {

		if (drawCompartments) {
			boolean scaleChanged = false;
			if (this.getScale() != scaleLastCompartmentRendered) {
				scaleChanged = true;
			}
			// if (scaleChanged || (System.nanoTime() - lastTimeCompartmentRendered) /
			// 1000000 > 1000) {

			if (experimental) {
				super.renderGraph(g2d);

				this.drawCompartemntsExperimental(g2d);
			} else {
				this.prepareCompartments();
				super.renderGraph(g2d);

				this.drawCompartemnts(g2d);
			}

			lastTimeCompartmentRendered = System.nanoTime();
			scaleLastCompartmentRendered = this.getScale();
			// } else {
			// super.renderGraph(g2d);
			// this.drawCompartemnts(g2d);
			// }
		} else {
			super.renderGraph(g2d);
			// this.drawCompartemnts(g2d);
		}

		g2d.setFont(new Font("default", Font.BOLD, 12));
		g2d.setColor(Color.red);

		if (GraphContainer.getInstance().isPetriView()) {
			g2d.drawString("P: " + pw.getPlaceCount() + " T: " + pw.getTransitionCount() + " Edges: "
					+ pw.getGraph().getAllEdges().size(), 1, 11);
		} else {

			g2d.drawString(
					"Nodes: " + pw.getGraph().getAllVertices().size() + " Edges: " + pw.getGraph().getAllEdges().size(),
					1, 11);
		}
		g2d.drawString("Picked nodes: " + this.getPickedVertexState().getPicked().size(), 1, 23);

		double scale;
		scale = ((double) ((int) (this.getScale() * 100)) / 100);
		g2d.drawString("Zoom: " + scale + "x", this.getWidth() - 75, 11);

		drawMousePoint(g2d);

		// g2d.drawString("x", 580, 533);
		// System.out.println(this.getWidth());
		// ContainerSingelton.getInstance().setPetriView(true);
		// System.out.println(getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));

	}

	public List<VisualizationServer.Paintable> getPreRenderers() {
		return super.preRenderers;
	}

	public Pathway getPathway() {
		return pw;
	}

	private void prepareCompartments() {

		areas = new HashMap<String, Area>();
		HashMap<String, HashSet<BiologicalNodeAbstract>> compToNodes = new HashMap<String, HashSet<BiologicalNodeAbstract>>();
		HashMap<String, HashSet<BiologicalEdgeAbstract>> compToEdges = new HashMap<String, HashSet<BiologicalEdgeAbstract>>();

		Iterator<Compartment> comp = pw.getCompartmentManager().getAllCompartmentsAlphabetically().iterator();
		Compartment c;
		while (comp.hasNext()) {
			c = comp.next();
			this.areas.put(c.getName(), new Area());
			compToNodes.put(c.getName(), new HashSet<BiologicalNodeAbstract>());
			compToEdges.put(c.getName(), new HashSet<BiologicalEdgeAbstract>());
		}

		long l1 = System.nanoTime();

		Iterator<BiologicalNodeAbstract> itNodes = pw.getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		Area a;
		while (itNodes.hasNext()) {
			bna = itNodes.next();

			if (pw.getCompartmentManager().getCompartment(bna).length() == 0) {
				continue;
			}
			a = areas.get(pw.getCompartmentManager().getCompartment(bna));

			if (!compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).contains(bna)) {
				this.addNodetoArea(bna, a);
				compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).add(bna);
			}
			for (BiologicalEdgeAbstract bea : pw.getGraph().getJungGraph().getInEdges(bna)) {
				if (!compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea)) {
					this.addEdgeToArea(bea, a);
					compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).add(bea);
				}
				if (!compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea.getFrom())) {
					this.addNodetoArea(bea.getFrom(), a);
					compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).add(bea.getFrom());
				}
			}
			for (BiologicalEdgeAbstract bea : pw.getGraph().getJungGraph().getOutEdges(bna)) {
				if (!compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea)) {
					this.addEdgeToArea(bea, a);
					compToEdges.get(pw.getCompartmentManager().getCompartment(bna)).add(bea);
				}
				if (!compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).contains(bea.getTo())) {
					this.addNodetoArea(bea.getTo(), a);
					compToNodes.get(pw.getCompartmentManager().getCompartment(bna)).add(bea.getTo());
				}
			}
		}

		long l2 = System.nanoTime();

		// System.out.println("adding: " + (l2 - l1) / 1000);
		// }

	}

	private void addEdgeToArea(BiologicalEdgeAbstract bea, Area a) {
		BiologicalNodeAbstract bna1;
		BiologicalNodeAbstract bna2;

		Point2D p1;
		Point2D p2;
		Point2D p1inv;
		Point2D p2inv;
		Polygon poly1;
		Polygon poly2;

		bna1 = bea.getFrom();
		bna2 = bea.getTo();

		// if(bna1.getCompartment().equals(bna2.getCompartment())){
		// System.out.println(bna.getName());
		// System.out.println(pw.getGraph().getVertexLocation(bna));
		p1 = pw.getGraph().getVertexLocation(bna1);// pw.getGraph().getVisualizationViewer().getGraphLayout().transform(bna);
		p2 = pw.getGraph().getVertexLocation(bna2);

		// g2d.drawString("b",(int)((p.getX())), (int)((p.getY())*scale));
		p1inv = this.getRenderContext().getMultiLayerTransformer().transform(p1);
		p2inv = this.getRenderContext().getMultiLayerTransformer().transform(p2);

		double width = 12;
		if (this.getScale() < 1) {
			width = width * this.getScale();
		}

		poly1 = new Polygon();
		poly1.addPoint((int) (p1inv.getX() + width), (int) (p1inv.getY() + width));
		poly1.addPoint((int) (p1inv.getX() - width), (int) (p1inv.getY() - width));
		poly1.addPoint((int) (p2inv.getX() - width), (int) (p2inv.getY() - width));
		poly1.addPoint((int) (p2inv.getX() + width), (int) (p2inv.getY() + width));

		poly2 = new Polygon();
		poly2.addPoint((int) (p1inv.getX() + width), (int) (p1inv.getY() - width));
		poly2.addPoint((int) (p1inv.getX() - width), (int) (p1inv.getY() + width));
		poly2.addPoint((int) (p2inv.getX() - width), (int) (p2inv.getY() + width));
		poly2.addPoint((int) (p2inv.getX() + width), (int) (p2inv.getY() - width));

		// System.out.println(bna1.getName()+" "+bna2.getName());
		a.add(new Area(poly1));
		a.add(new Area(poly2));
	}

	private void addNodetoArea(BiologicalNodeAbstract bna, Area a) {
		Point2D p1;
		Point2D p1inv;
		Shape s1;
		int h1;
		RoundRectangle2D r1;

		p1 = pw.getGraph().getVertexLocation(bna);

		// g2d.drawString("b",(int)((p.getX())), (int)((p.getY())*scale));
		p1inv = this.getRenderContext().getMultiLayerTransformer().transform(p1);

		s1 = this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)
				.transform(bna.getShape().getBounds2D());
		// System.out.println(p1);
		// System.out.println(p1inv);
		// Shape s3 = this.getRenderContext().getMultiLayerTransformer()
		// .getTransformer(Layer.VIEW).transform(bea.getShape().getBounds2D());
		// System.out.println(loc);
		if (experimental) {
			p1inv = p1;
			s1 = bna.getShape().getBounds2D();
		}

		h1 = (int) s1.getBounds2D().getHeight();
		h1 = h1 / 2;

		r1 = new RoundRectangle2D.Double((int) (p1inv.getX() - h1 * 1.5), (int) (p1inv.getY() - h1 * 1.5), 3 * h1,
				3 * h1, 15, 15);
		a.add(new Area(r1));
	}

	private void drawCompartemnts(Graphics2D g2d) {

		long l1 = System.nanoTime();
		Iterator<String> it = this.areas.keySet().iterator();
		String comp;
		while (it.hasNext()) {
			comp = it.next();

			g2d.setColor(pw.getCompartmentManager().getCompartment(comp).getColor());
			g2d.fill(areas.get(comp));
		}
		// g2d.setColor(new Color(255, 0, 0, 30));
		// a.transform(new AffineTransform(0.5, 0.5,0.5,0.5,0.5,0.5));
		// System.out.println("painting " + (System.nanoTime() - l1) / 1000);
	}

	private void drawCompartemntsExperimental(Graphics2D g2d) {

		long l1 = System.nanoTime();
		Iterator<String> it = this.areas.keySet().iterator();
		String comp;
		while (it.hasNext()) {
			comp = it.next();

			double scale;
			MutableTransformer mt;
			double scaleV = this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
			if (scaleV < 1) {
				// mt =
				// this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
				mt = this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
				// System.out.println("smaller");
			} else {
				// mt=
				mt = this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
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
			Point2D lvc = getRenderContext().getMultiLayerTransformer().inverseTransform(getCenter());
			Point2D q1 = getRenderContext().getMultiLayerTransformer().inverseTransform(q);
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

	public double getScale() {
		double scaleV = this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
		double scaleL = this.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
		double scale;
		if (scaleV < 1) {
			scale = scaleV;
		} else {
			scale = scaleL;
		}
		return scale;
	}

	public void setDrawCompartments(boolean drawCompartments) {
		this.drawCompartments = drawCompartments;
		this.repaint();
	}

	public void setEsperimentalCompartments(boolean experimental) {
		this.experimental = experimental;
		if (experimental) {
			prepareCompartments();
		}
		this.repaint();
	}

	public void setMousePoint(Point2D point) {
		this.mousePoint = point;

		// System.out.println("draw");
		// drawMousePoint(g2d);
		this.repaint(this.getWidth() - 75, 0, 75, 40);
	}

	private void drawMousePoint(Graphics2D g2d) {

		g2d.setFont(new Font("default", Font.PLAIN, 12));
		g2d.setColor(Color.black);

		/*
		 * if (GraphContainer.getInstance().isPetriView()) { g2d.drawString("P: " +
		 * pw.getPlaceCount() + " T: " + pw.getTransitionCount() + " Edges: " +
		 * pw.getGraph().getAllEdges().size(), 1, 11); } else {
		 * 
		 * g2d.drawString( "Nodes: " + pw.getGraph().getAllVertices().size() +
		 * " Edges: " + pw.getGraph().getAllEdges().size(), 1, 11); }
		 * g2d.drawString("Picked nodes: " +
		 * this.getPickedVertexState().getPicked().size(), 1, 23);
		 */
		// this.;
		g2d.drawString("x: " + String.format("%.3f", mousePoint.getX()), this.getWidth() - 75, 22);
		g2d.drawString("y: " + String.format("%.3f", mousePoint.getY()), this.getWidth() - 75, 35);

		// System.out.println(this.getMousePosition());

	}
}
