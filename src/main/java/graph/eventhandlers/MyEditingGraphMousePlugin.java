/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 */
package graph.eventhandlers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.gui.EdgeDialog;
import graph.gui.PetriNetVertexDialog;
import graph.gui.VertexDialog;
import graph.jung.classes.MyVisualizationViewer;
import gui.MainWindow;
import gui.PopUpDialog;

/**
 * A plugin that can create vertices, undirected edges, and directed edges using mouse gestures.
 *
 * @author Tom Nelson - RABA Technologies
 */
public class MyEditingGraphMousePlugin extends AbstractGraphMousePlugin implements MouseListener, MouseMotionListener {
	private BiologicalNodeAbstract startVertex;
	private final CubicCurve2D rawEdge = new CubicCurve2D.Float();
	private Shape edgeShape;
	private final Shape rawArrowShape;
	private Shape arrowShape;
	private final Paintable edgePaintable;
	private final Paintable arrowPaintable;
	private boolean edgeIsDirected;
	private final GraphContainer con = GraphContainer.getInstance();

	private Pathway pw = null;
	private MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = null;

	private int lastVertexTypeIdx = -1;
	private int lastEdgeTypeIdx = -1;
	private boolean lastDirected = true;

	private boolean inWindow = false;

	public MyEditingGraphMousePlugin() {
		this(InputEvent.BUTTON1_DOWN_MASK);
	}

	/**
	 * create instance and prepare shapes for visual effects
	 */
	public MyEditingGraphMousePlugin(int modifiers) {
		super(modifiers);
		rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50, 1.0f, 0.0f);
		rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
		edgePaintable = new EdgePaintable();
		arrowPaintable = new ArrowPaintable();
	}

	/**
	 * overrided to be more flexible, and pass events with key combinations. The default responds to both ButtonOne and
	 * ButtonOne+Shift
	 */
	@Override
	public boolean checkModifiers(MouseEvent e) {
		return (e.getModifiersEx() & modifiers) != 0;
	}

	/**
	 * If the mouse is pressed in an empty area, create a new vertex there. If the mouse is pressed on an existing
	 * vertex, prepare to create an edge from that vertex to another
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (checkModifiers(e)) {
			setPathway(e);
			final Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
			GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv.getPickSupport();
			BiologicalNodeAbstract vertex = pickSupport.getVertex(vv.getGraphLayout(), e.getPoint().getX(),
					e.getPoint().getY());
			if (vertex != null) { // get ready to make an edge
				startVertex = vertex;
				super.down = e.getPoint();
				transformEdgeShape(down, down);
				vv.addPostRenderPaintable(edgePaintable);
				if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {
					edgeIsDirected = true;
					transformArrowShape(down, e.getPoint());
					vv.addPostRenderPaintable(arrowPaintable);
				}
			} else { // make a new vertex
				Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph = vv.getGraphLayout().getGraph();
				Layout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout = vv.getGraphLayout();
				for (BiologicalNodeAbstract biologicalNodeAbstract : graph.getVertices()) {
					layout.lock(biologicalNodeAbstract, true);
				}
				if (pw.isPetriNet()) {
					PetriNetVertexDialog dialog = new PetriNetVertexDialog(con.getPetriNetEditingMode(), pw);
					BiologicalNodeAbstract bna = dialog.getAnswer(p, vv);
					if (bna != null) {
						if (pw instanceof BiologicalNodeAbstract) {
							bna.setParentNode((BiologicalNodeAbstract) pw);
						}
						if (GraphInstance.getPathway() != null) {
							MainWindow.getInstance().updateElementTree();
							MainWindow.getInstance().updatePathwayTree();
						}
					}
				} else {
					VertexDialog dialog = new VertexDialog(pw, this.lastVertexTypeIdx);
					Map<String, String> answers = dialog.getAnswer(vv);
					lastVertexTypeIdx = dialog.getLastTypeIdx();
					if (answers != null) {
						String name = answers.get("name");
						String label = answers.get("name");
						String element = answers.get("elementType");
						String compartment = answers.get("compartment");
						BiologicalNodeAbstract newVertex = pw.addVertex(name, label, element, compartment, p);
						if (pw instanceof BiologicalNodeAbstract) {
							newVertex.setParentNode((BiologicalNodeAbstract) pw);
						}
						if (GraphInstance.getPathway() != null) {
							MainWindow.getInstance().updateElementTree();
						}
						for (final BiologicalNodeAbstract biologicalNodeAbstract : graph.getVertices()) {
							layout.lock(biologicalNodeAbstract, false);
						}
					}
				}
			}
			vv.repaint();
		}
	}

	/**
	 * If startVertex is non-null, and the mouse is released over an existing vertex, create an undirected edge from
	 * startVertex to the vertex under the mouse pointer. If shift was also pressed, create a directed edge instead.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		setPathway(e);
		// final Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
		// int v = vv.getPickedVertexState().getPicked().size();
		// int edge = vv.getPickedEdgeState().getPicked().size();
		GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv.getPickSupport();
		// if (v > 0) {
		final BiologicalNodeAbstract vertex = pickSupport.getVertex(vv.getGraphLayout(), e.getPoint().getX(),
				e.getPoint().getY());
		if (vertex != null && startVertex != null) {
			if (pw.isPetriNet() && !((startVertex instanceof Place && vertex instanceof Transition) || (
					startVertex instanceof Transition && vertex instanceof Place))) {
				PopUpDialog.getInstance().show("Operation not allowed",
						"In a Petri net only Transition->Place and Place->Transition arcs are allowed!");
			} else {
				EdgeDialog dialog = new EdgeDialog(startVertex, vertex, pw, lastEdgeTypeIdx, lastDirected);
				Pair<Map<String, String>, BiologicalNodeAbstract[]> answer = dialog.getAnswer(vv);
				this.lastEdgeTypeIdx = dialog.getLastTypeIdx();
				this.lastDirected = dialog.isLastDirected();
				Map<String, String> details = answer.getLeft();
				BiologicalNodeAbstract[] nodes = answer.getRight();

				if (details != null) {
					if (details.get("element") != null && pw.isPetriNet() && (details.get("element").toLowerCase()
							.contains("inhibi") || details.get("element").toLowerCase().contains("test")) && !(
							startVertex instanceof Place && vertex instanceof Transition)) {
						PopUpDialog.getInstance().show("Operation not allowed",
								"Inhibitory / Test arcs are only possible from Place to Transition!");
					} else {
						final String name = details.get("name");
						final String label = details.get("name");
						final String element = details.get("element");
						final String function = details.get("function");
						final boolean directed = details.get("directed").equals("true");
						BiologicalEdgeAbstract bea = pw.addEdge(label, name, nodes[0], nodes[1], element, directed);
						bea.setFunction(function);
						if (nodes[0] == startVertex && nodes[1] == vertex) {
							pw.addEdgeToView(bea, false);
						} else {
							BiologicalEdgeAbstract clone = bea.clone();
							clone.setFrom(startVertex);
							clone.setTo(vertex);
							pw.addEdgeToView(clone, false);
						}
					}
				}
			}
		}
		startVertex = null;
		down = null;
		edgeIsDirected = false;
		vv.removePostRenderPaintable(edgePaintable);
		vv.removePostRenderPaintable(arrowPaintable);
		vv.repaint();
	}

	/**
	 * If startVertex is non-null, stretch an edge shape between startVertex and the mouse pointer to simulate edge
	 * creation
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (checkModifiers(e)) {
			if (startVertex != null) {
				transformEdgeShape(down, e.getPoint());
				if (edgeIsDirected) {
					transformArrowShape(down, e.getPoint());
				}
			}
			setPathway(e);
			vv.repaint();
		}
	}

	/**
	 * code lifted from PluggableRenderer to move an edge shape into an arbitrary position
	 */
	private void transformEdgeShape(Point2D down, Point2D out) {
		float x1 = (float) down.getX();
		float y1 = (float) down.getY();
		float x2 = (float) out.getX();
		float y2 = (float) out.getY();

		AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

		float dx = x2 - x1;
		float dy = y2 - y1;
		float thetaRadians = (float) Math.atan2(dy, dx);
		xform.rotate(thetaRadians);
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		xform.scale(dist / rawEdge.getBounds().getWidth(), 1.0);
		edgeShape = xform.createTransformedShape(rawEdge);
	}

	private void transformArrowShape(Point2D down, Point2D out) {
		float x1 = (float) down.getX();
		float y1 = (float) down.getY();
		float x2 = (float) out.getX();
		float y2 = (float) out.getY();

		AffineTransform xform = AffineTransform.getTranslateInstance(x2, y2);

		float dx = x2 - x1;
		float dy = y2 - y1;
		float thetaRadians = (float) Math.atan2(dy, dx);
		xform.rotate(thetaRadians);
		arrowShape = xform.createTransformedShape(rawArrowShape);
	}

	/**
	 * Used for the edge creation visual effect during mouse drag
	 */
	class EdgePaintable implements Paintable {
		@Override
		public void paint(Graphics g) {
			if (edgeShape != null) {
				Color oldColor = g.getColor();
				g.setColor(Color.black);
				((Graphics2D) g).draw(edgeShape);
				g.setColor(oldColor);
			}
		}

		@Override
		public boolean useTransform() {
			return false;
		}
	}

	/**
	 * Used for the directed edge creation visual effect during mouse drag
	 */
	class ArrowPaintable implements Paintable {
		@Override
		public void paint(Graphics g) {
			if (arrowShape != null) {
				Color oldColor = g.getColor();
				g.setColor(Color.black);
				((Graphics2D) g).fill(arrowShape);
				g.setColor(oldColor);
			}
		}

		public boolean useTransform() {
			return false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		inWindow = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		inWindow = false;
	}

	@Override
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
			pw = vv.getPathway();
		}
	}
}
