package graph.jung.classes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphContainer;

public class MyVisualizationViewer<V, E> extends VisualizationViewer<V, E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Pathway pw;

	private HashMap<String, Area> areas = new HashMap<String, Area>();

	public MyVisualizationViewer(VisualizationModel<V, E> arg0, Dimension arg2,
			Pathway pw) {
		super(arg0, arg2);
		this.pw = pw;
		areas.put("Cytoplasma", new Area());
		areas.put("Nucleus", new Area());
		areas.put("Membrane", new Area());
		areas.put("InsideTheCell", new Area());

		// this.prepareCompartments();
	}

	private long[] relaxTimes = new long[5];

	private long[] paintTimes = new long[5];

	private int relaxIndex = 0;

	private int paintIndex = 0;

	private double paintfps, relaxfps;

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
		//this.prepareCompartments();

		super.renderGraph(g2d);
		//this.drawCompartemnts(g2d);
		g2d.setFont(new Font("default", Font.BOLD, 12));
		g2d.setColor(Color.red);

		if (GraphContainer.getInstance().isPetriView()) {
			g2d.drawString("P: " + pw.getPetriNet().getPlaces() + " T: "
					+ pw.getPetriNet().getTransitions() + " Edges: "
					+ pw.getGraph().getAllEdges().size(), 1, 11);
		} else {

			g2d.drawString("Nodes: " + pw.getGraph().getAllVertices().size()
					+ " Edges: " + pw.getGraph().getAllEdges().size(), 1, 11);
		}
		g2d.drawString("Picked nodes: "
				+ this.getPickedVertexState().getPicked().size(), 1, 23);
		double scaleV = this.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.VIEW).getScale();
		double scaleL = this.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).getScale();
		double scale;
		if (scaleV < 1) {
			scale = scaleV;
		} else {
			scale = scaleL;
		}
		scale = ((double) ((int) (scale * 100)) / 100);
		g2d.drawString("Zoom: " + scale + "x", this.getWidth() - 75, 11);

		// g2d.drawString("x", 580, 533);
		// System.out.println(this.getWidth());
		// ContainerSingelton.getInstance().setPetriView(true);

	}

	public List<VisualizationServer.Paintable> getPreRenderers() {
		return super.preRenderers;
	}

	public Pathway getPathway(){
		return pw;
	}
	
	private void prepareCompartments() {
		
		Iterator<String> comp = this.areas.keySet().iterator();
		
		while(comp.hasNext()){
			this.areas.put(comp.next(), new Area());
		}
		
		Iterator<BiologicalEdgeAbstract> it = pw.getAllEdges().iterator();

		long l1 = System.nanoTime();

		BiologicalNodeAbstract bna1;
		BiologicalNodeAbstract bna2;
		BiologicalEdgeAbstract bea;

		while (it.hasNext()) {
			Area a;
			bea = it.next();
			bna1 = bea.getFrom();
			bna2 = bea.getTo();

			// if(bna1.getCompartment().equals(bna2.getCompartment())){
			// System.out.println(bna.getName());
			// System.out.println(pw.getGraph().getVertexLocation(bna));
			Point2D p1 = pw.getGraph().getVertexLocation(bna1);// pw.getGraph().getVisualizationViewer().getGraphLayout().transform(bna);
			Point2D p2 = pw.getGraph().getVertexLocation(bna2);

			// g2d.drawString("b",(int)((p.getX())), (int)((p.getY())*scale));
			Point2D p1inv = this.getRenderContext().getMultiLayerTransformer()
					.transform(p1);
			Point2D p2inv = this.getRenderContext().getMultiLayerTransformer()
					.transform(p2);

			Shape s1 = this.getRenderContext().getMultiLayerTransformer()
					.getTransformer(Layer.VIEW)
					.transform(bna1.getShape().getBounds2D());
			Shape s2 = this.getRenderContext().getMultiLayerTransformer()
					.getTransformer(Layer.VIEW)
					.transform(bna2.getShape().getBounds2D());

			// Shape s3 = this.getRenderContext().getMultiLayerTransformer()
			// .getTransformer(Layer.VIEW).transform(bea.getShape().getBounds2D());
			// System.out.println(loc);
			int h1 = (int) s1.getBounds2D().getHeight();
			int h2 = (int) s2.getBounds2D().getHeight();

			if (bna1 instanceof Transition) {
				h1 = h1 / 2;
				//System.out.println(bna1.getCompartment());
				a = areas.get(bna1.getCompartment());
			} else {
				h2 = h2 / 2;
				a = areas.get(bna2.getCompartment());
			}

			int c1x = (int) s1.getBounds2D().getMaxX();
			int c1y = (int) s1.getBounds2D().getMaxY();
			// System.out.println(s.getBounds2D().getHeight());
			// System.out.println(this.getRenderContext().);
			// poly.addPoint(0, 0);

			double m = (p2inv.getX() - p1inv.getX())
					/ (p2inv.getY() - p1inv.getY());
			// System.out.println(m);
			double dist = Math.sqrt(Math.pow((p2inv.getX() - p1inv.getX()), 2)
					+ Math.pow((p2inv.getY() - p1inv.getY()), 2));

			Point2D pp1 = new Point2D.Double(s1.getBounds2D().getMaxX(), s1
					.getBounds2D().getMaxY());
			Point2D pp1inv = getRenderContext().getMultiLayerTransformer()
					.transform(pp1);

			double width = 12;
			Polygon poly1 = new Polygon();
			poly1.addPoint((int) (p1inv.getX() + width),
					(int) (p1inv.getY() + width));
			poly1.addPoint((int) (p1inv.getX() - width),
					(int) (p1inv.getY() - width));
			poly1.addPoint((int) (p2inv.getX() - width),
					(int) (p2inv.getY() - width));
			poly1.addPoint((int) (p2inv.getX() + width),
					(int) (p2inv.getY() + width));

			Polygon poly2 = new Polygon();
			poly2.addPoint((int) (p1inv.getX() + width),
					(int) (p1inv.getY() - width));
			poly2.addPoint((int) (p1inv.getX() - width),
					(int) (p1inv.getY() + width));
			poly2.addPoint((int) (p2inv.getX() - width),
					(int) (p2inv.getY() + width));
			poly2.addPoint((int) (p2inv.getX() + width),
					(int) (p2inv.getY() - width));

			// poly.addPoint((int)(s1.getBounds2D().getMinX()),
			// (int)(s1.getBounds2D().getMinY()));
			// poly.addPoint((int)(s2.getBounds2D().getMaxX()),
			// (int)(s2.getBounds2D().getMaxY()));
			// poly.addPoint((int)(s2.getBounds2D().getMinX()),
			// (int)(s2.getBounds2D().getMinY()));

			// poly.addPoint((int)(p1inv.getX()+h1), (int)(p1inv.getY()-h1));
			// poly.addPoint((int)(p2inv.getX()-h2), (int)(p2inv.getY()-h2));
			// poly.addPoint((int)(p2inv.getX()-h2), (int)(p2inv.getY()+h2));
			// poly.addPoint((int)(p1inv.getX()+h1), (int)(p1inv.getY()+h1));

			Line2D.Double l = new Line2D.Double(p1inv.getX(), p1inv.getY(),
					p2inv.getX(), p2inv.getY());

			Arc2D.Double arc = new Arc2D.Double(100, 100, 200, 200, 10, 90,
					Arc2D.OPEN);

			// g2d.draw(l);
			// g2d.drawPolygon(poly1);
			// g2d.drawPolygon(poly2);
			// g2d.fillPolygon(poly);
			// RoundRectangle2D r1 = new

			RoundRectangle2D r1 = new RoundRectangle2D.Double(
					(int) (p1inv.getX() - h1 * 1.5),
					(int) (p1inv.getY() - h1 * 1.5), 3 * h1, 3 * h1, 15, 15);
			Area a1 = new Area(r1);
			RoundRectangle2D r2 = new RoundRectangle2D.Double(
					(int) (p2inv.getX() - h2 * 1.5),
					(int) (p2inv.getY() - h2 * 1.5), 3 * h2, 3 * h2, 15, 15);
			Area a2 = new Area(r2);

			Area a3 = new Area(poly1);
			Area a4 = new Area(poly2);

			//System.out.println(bna1.getName()+" "+bna2.getName());
			a.add(a3);
			a.add(a1);
			a.add(a2);
			a.add(a4);

			// a3.add(new Area(arc));
			// a3.add(new Area(l));
			// a1.add(a2);
			// a1.add(a3);
			// g2d.fill(arc);

			// g2d.fill(r1);
			// g2d.fill(r2);
			// g2d.draw(a3);
			// g2d.draw(arc);
			// g2d.fill(r1);
			// g2d.fill(r2);
			// g2d.fill(a3);

			// g2d.fillRoundRect((int)(p1inv.getX()-h1*1.5),
			// (int)(p1inv.getY()-h1*1.5), 3*h1, 3*h1, 15, 15);
			// g2d.fillRoundRect((int)(p2inv.getX()-h2*1.5),
			// (int)(p2inv.getY()-h2*1.5), 3*h2, 3*h2, 15, 15);
			// g2d.drawRect((int)(p1inv.getX()-20), (int)(p1inv.getY()-20),
			// (int)(p2inv.getX()-p1inv.getX()+20),
			// (int)(p2inv.getY()-p1inv.getY())+20);
		}
		long l2 = System.nanoTime();

		//System.out.println("adding: " + (l2 - l1) / 1000);
		// }

		//System.out.println("painting " + (System.nanoTime() - l2) / 1000);
	}

	private void drawCompartemnts(Graphics2D g2d) {

		Iterator<String> it = this.areas.keySet().iterator();
		String comp;
		while (it.hasNext()) {
			comp = it.next();

			switch (comp) {
			case "Cytoplasma":
				g2d.setColor(new Color(0, 255, 0, 50));
				g2d.fill(areas.get(comp));
				break;

			case "Nucleus":
				g2d.setColor(new Color(0, 0, 255, 50));
				g2d.fill(areas.get(comp));
				break;

			case "Membrane":
				g2d.setColor(new Color(255, 128, 0, 50));
				g2d.fill(areas.get(comp));
				break;

			case "InsideTheCell":
				g2d.setColor(new Color(127, 0, 255, 50));
				g2d.fill(areas.get(comp));
				break;
			}
			
			
		}
		//g2d.setColor(new Color(255, 0, 0, 30));
		// a.transform(new AffineTransform(0.5, 0.5,0.5,0.5,0.5,0.5));
		
	}

	/*
	 * @Override protected void renderGraph(Graphics2D g2d) {
	 * System.out.println("render");
	 * 
	 * if(renderContext.getGraphicsContext() == null) {
	 * renderContext.setGraphicsContext(new GraphicsDecorator(g2d)); } else {
	 * renderContext.getGraphicsContext().setDelegate(g2d); }
	 * renderContext.setScreenDevice(this); Layout layout =
	 * model.getGraphLayout();
	 * 
	 * g2d.setRenderingHints(renderingHints);
	 * 
	 * // the size of the VisualizationViewer Dimension d = getSize();
	 * 
	 * // clear the offscreen image g2d.setColor(getBackground());
	 * g2d.fillRect(0,0,d.width,d.height);
	 * 
	 * AffineTransform oldXform = g2d.getTransform(); AffineTransform newXform =
	 * new AffineTransform(oldXform); newXform.concatenate(
	 * renderContext.getMultiLayerTransformer
	 * ().getTransformer(Layer.VIEW).getTransform()); //
	 * viewTransformer.getTransform());
	 * 
	 * g2d.setTransform(newXform);
	 * 
	 * // if there are preRenderers set, paint them for(Paintable paintable :
	 * preRenderers) {
	 * 
	 * if(paintable.useTransform()) { paintable.paint(g2d); } else {
	 * g2d.setTransform(oldXform); paintable.paint(g2d);
	 * g2d.setTransform(newXform); } }
	 * 
	 * if(layout instanceof Caching) { ((Caching)layout).clear(); }
	 * 
	 * renderer.render(renderContext, layout);
	 * 
	 * 
	 * 
	 * // if there are postRenderers set, do it for(Paintable paintable :
	 * postRenderers) {
	 * 
	 * if(paintable.useTransform()) { paintable.paint(g2d); } else {
	 * g2d.setTransform(oldXform); paintable.paint(g2d);
	 * g2d.setTransform(newXform); } } g2d.setTransform(oldXform); /*Layout
	 * layout = model.getGraphLayout();
	 * 
	 * g2d.setRenderingHints(renderingHints); long start =
	 * System.currentTimeMillis(); // the size of the VisualizationViewer
	 * Dimension d = getSize();
	 * 
	 * // clear the offscreen image g2d.setColor(getBackground());
	 * g2d.fillRect(0, 0, d.width, d.height);
	 * 
	 * AffineTransform oldXform = g2d.getTransform(); AffineTransform newXform =
	 * new AffineTransform(oldXform); //
	 * newXform.concatenate(viewTransformer.getTransform());
	 * 
	 * 
	 * JScrollBar bar; g2d.setTransform(newXform);
	 * 
	 * // if there are preRenderers set, paint them for (Iterator iterator =
	 * preRenderers.iterator(); iterator.hasNext();) { Paintable paintable =
	 * (Paintable) iterator.next(); if (paintable.useTransform()) {
	 * paintable.paint(g2d); } else { g2d.setTransform(oldXform);
	 * paintable.paint(g2d); g2d.setTransform(newXform); } }
	 * 
	 * locationMap.clear();
	 * 
	 * // paint all the edges try { for (Iterator iter =
	 * layout.getGraph().getEdges().iterator(); iter .hasNext();) { Edge e =
	 * (Edge) iter.next();
	 * 
	 * Vertex v1 = (Vertex) e.getEndpoints().getFirst(); Vertex v2 = (Vertex)
	 * e.getEndpoints().getSecond();
	 * 
	 * if (pw.containsElement(e)) {
	 * 
	 * GraphElementAbstract gea = (GraphElementAbstract) pw .getElement(e);
	 * 
	 * if (gea.isVisible()) { Point2D p = (Point2D) locationMap.get(v1); if (p
	 * == null) {
	 * 
	 * p = layout.getLocation(v1); p = layoutTransformer.transform(p);
	 * locationMap.put(v1, p); } Point2D q = (Point2D) locationMap.get(v2); if
	 * (q == null) { q = layout.getLocation(v2); q =
	 * layoutTransformer.transform(q); locationMap.put(v2, q); }
	 * 
	 * if (p != null && q != null) { renderer.paintEdge(g2d, e, (int) p.getX(),
	 * (int) p .getY(), (int) q.getX(), (int) q.getY());
	 * 
	 * } }
	 * 
	 * } else {
	 * 
	 * Point2D p = (Point2D) locationMap.get(v1); if (p == null) {
	 * 
	 * p = layout.getLocation(v1); p = layoutTransformer.transform(p);
	 * locationMap.put(v1, p); } Point2D q = (Point2D) locationMap.get(v2); if
	 * (q == null) { q = layout.getLocation(v2); q =
	 * layoutTransformer.transform(q); locationMap.put(v2, q); }
	 * 
	 * if (p != null && q != null) { renderer.paintEdge(g2d, e, (int) p.getX(),
	 * (int) p .getY(), (int) q.getX(), (int) q.getY());
	 * 
	 * }
	 * 
	 * } }
	 * 
	 * } catch (ConcurrentModificationException cme) { repaint(); }
	 * 
	 * // paint all the vertices try { for (Iterator iter =
	 * layout.getGraph().getVertices().iterator(); iter .hasNext();) {
	 * 
	 * BiologicalNodeAbstract v = (BiologicalNodeAbstract) iter.next();
	 * 
	 * if (pw.containsElement(v)){ GraphElementAbstract gea =
	 * (GraphElementAbstract) pw .getElement(v); if (gea.isVisible()) { Point2D
	 * p = (Point2D) locationMap.get(v); if (p == null) { p =
	 * layout.getLocation(v); p = layoutTransformer.transform(p);
	 * locationMap.put(v, p); } if (p != null) { renderer.paintVertex(g2d, v,
	 * (int) p.getX(), (int) p .getY()); } }
	 * 
	 * }else{ Point2D p = (Point2D) locationMap.get(v); if (p == null) { p =
	 * layout.getLocation(v); p = layoutTransformer.transform(p);
	 * locationMap.put(v, p); } if (p != null) { renderer.paintVertex(g2d, v,
	 * (int) p.getX(), (int) p .getY()); } } } } catch
	 * (ConcurrentModificationException cme) { repaint(); }
	 * 
	 * long delta = System.currentTimeMillis() - start; paintTimes[paintIndex++]
	 * = delta; paintIndex = paintIndex % paintTimes.length; paintfps =
	 * average(paintTimes);
	 * 
	 * // if there are postRenderers set, do it for (Iterator iterator =
	 * postRenderers.iterator(); iterator.hasNext();) { Paintable paintable =
	 * (Paintable) iterator.next(); if (paintable.useTransform()) {
	 * paintable.paint(g2d); } else { g2d.setTransform(oldXform);
	 * paintable.paint(g2d); g2d.setTransform(newXform); } }
	 * g2d.setTransform(oldXform);
	 */
	// }

	public double getScale() {
		double scaleV = this.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.VIEW).getScale();
		double scaleL = this.getRenderContext().getMultiLayerTransformer()
				.getTransformer(Layer.LAYOUT).getScale();
		double scale;
		if (scaleV < 1) {
			scale = scaleV;
		} else {
			scale = scaleL;
		}
		return scale;
	}

}
