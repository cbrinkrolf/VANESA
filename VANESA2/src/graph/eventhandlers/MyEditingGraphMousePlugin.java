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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.gui.EdgeDialog;
import graph.gui.PetriNetVertexDialog;
import graph.gui.VertexDialog;
import gui.MainWindow;
import gui.MyPopUp;

/**
 * A plugin that can create vertices, undirected edges, and directed edges using
 * mouse gestures.
 * 
 * @author Tom Nelson - RABA Technologies
 * 
 */
public class MyEditingGraphMousePlugin extends AbstractGraphMousePlugin
		implements MouseListener, MouseMotionListener {

	// HashMap vertexLocations;
	private BiologicalNodeAbstract startVertex;
	//private Point2D down;

	private CubicCurve2D rawEdge = new CubicCurve2D.Float();
	private Shape edgeShape;
	private Shape rawArrowShape;
	private Shape arrowShape;
	private Paintable edgePaintable;
	private Paintable arrowPaintable;
	private boolean edgeIsDirected;
	private GraphContainer con = GraphContainer.getInstance();

	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw;

	public MyEditingGraphMousePlugin() {
		this(InputEvent.BUTTON1_MASK);
	}

	/**
	 * create instance and prepare shapes for visual effects
	 * 
	 * @param modifiers
	 */
	public MyEditingGraphMousePlugin(int modifiers) {
		super(modifiers);
		rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50, 1.0f, 0.0f);
		rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
		edgePaintable = new EdgePaintable();
		arrowPaintable = new ArrowPaintable();
	}

	/**
	 * sets the vertex locations. Needed to place new vertices
	 * 
	 * @param vertexLocations
	 */
	// public void setVertexLocations(HashMap vertexLocations) {
	// this.vertexLocations = vertexLocations;
	// }

	/**
	 * overrided to be more flexible, and pass events with key combinations. The
	 * default responds to both ButtonOne and ButtonOne+Shift
	 */
	@Override
	public boolean checkModifiers(MouseEvent e) {
		return (e.getModifiers() & modifiers) != 0;
	}

	/**
	 * If the mouse is pressed in an empty area, create a new vertex there. If
	 * the mouse is pressed on an existing vertex, prepare to create an edge
	 * from that vertex to another
	 */
	public void mousePressed(MouseEvent e) {
		pw = graphInstance.getPathway();
		if (checkModifiers(e)) {
			
			final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph().getVisualizationViewer();
			// final Point2D p = vv.inverseViewTransform(e.getPoint());
			// System.out.println("Points: "+e.getPoint().getX()+", "+e.getPoint().getY());
			final Point2D p = vv.getRenderContext().getMultiLayerTransformer()
					.inverseTransform(e.getPoint());
			// System.out.println(e.getPoint()+ " "+p);
			// System.out.println("Points: "+p.getX()+", "+p.getY());
			// final Point2D p = e.getPoint();
			GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv
					.getPickSupport();
			// System.out.println("Click: "+p);
			// System.out.println("regul: "+e.getPoint());

			Iterator<BiologicalNodeAbstract> it = pw.getGraph()
					.getAllVertices().iterator();
			// while(it.hasNext()){
			// System.out.println(pw.getGraph().getVertexLocation(it.next()));
			// }
			// System.out.println(pw.getGraph().getAllEdges().size());
			// System.out.println(pickSupport.g);

			BiologicalNodeAbstract vertex = null;

			vertex = (BiologicalNodeAbstract) pickSupport
					.getVertex(vv.getGraphLayout(), e.getPoint().getX(), e
							.getPoint().getY());
			// System.out.println(vertex);

			if (vertex != null) { // get ready to make an edge
				// System.out.println(vertex);
				startVertex = vertex;
				super.down = e.getPoint();
				transformEdgeShape(down, down);
				vv.addPostRenderPaintable(edgePaintable);
				if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
					edgeIsDirected = true;
					transformArrowShape(down, e.getPoint());
					vv.addPostRenderPaintable(arrowPaintable);
				}
			} else { // make a new vertex
				Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph = vv
						.getGraphLayout().getGraph();
//				BiologicalNodeAbstract newVertex = new BiologicalNodeAbstract(
//						"label", "name");
				// vertexLocations.put(newVertex, p);

				Layout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout = vv
						.getGraphLayout();

				// System.out.println("size V: "+layout.getGraph().getVertices().size());
				// System.out.println("size E: "+layout.getGraph().getEdges().size());

				// graph.addVertex(newVertex);
				/*
				 * Object key = (((AggregateLayout)
				 * layout).getDelegate()).getBaseKey(); Object datum = new
				 * Coordinates(vv.inverseTransform( e.getPoint()).getX(),
				 * vv.inverseTransform( e.getPoint()).getY());
				 * newVertex.setUserDatum(key, datum, new CopyAction.Clone());
				 */

				for (Iterator<BiologicalNodeAbstract> iterator = graph
						.getVertices().iterator(); iterator.hasNext();) {
					layout.lock(iterator.next(), true);

				}
								
				if (con.isPetriView()) {
					// System.out.println("is petri");

					PetriNetVertexDialog dialog = new PetriNetVertexDialog(
							con.getPetriNetEditingMode());
					BiologicalNodeAbstract bna = dialog.getAnswer(p);
					// System.out.println();
					if (bna != null) {
						// BiologicalNodeAbstract ba = new
						// BiologicalNodeAbstract(
						// answers[0], "", newVertex);
						// ba.setBiologicalElement(answers[1]);
						// ba.setCompartment(answers[2]);
						// graphInstance.getPathway().addElement(ba);
						// graph.addVertex(newVertex);

						// vv.getModel().restart();
						//System.out.println("update");
						if(pw instanceof BiologicalNodeAbstract){
							bna.setParentNode((BiologicalNodeAbstract) pw); 
						}
						MainWindow.getInstance().updateElementTree();
						MainWindow.getInstance().updatePathwayTree();
						//MainWindowSingelton.getInstance().updateAllGuiElements();
						//MainWindowSingelton.getInstance().updateOptionPanel();
						// MainWindowSingelton.getInstance()
						// .updateTheoryProperties();

						// Pathway pw = graphInstance.getPathway();

					}

				} else {
					// System.out.println("not petri");
					VertexDialog dialog = new VertexDialog();
					String[] answers = dialog.getAnswer();
					if (answers != null) {

						// BiologicalNodeAbstract ba = new
						// BiologicalNodeAbstract(
						// answers[0], "");
						String name = answers[0];
						String label = answers[0];
						String element = answers[1];
						String compartment = answers[2];
//						newVertex.setBiologicalElement(answers[1]);
//						newVertex.setCompartment(answers[2]);
						// graphInstance.getPathway().addElement(newVertex);
						// graph.addVertex(newVertex);
						
						BiologicalNodeAbstract newVertex = pw.addVertex(name, label, element, compartment, p);
						if(pw instanceof BiologicalNodeAbstract){
							newVertex.setParentNode((BiologicalNodeAbstract) pw);
						}
						//pw.addVertex(newVertex, p);
						if (graph.getVertices().size() > 1) {
							// System.exit(0);
						}

						// pw.getGraph().setVertexLocation(newVertex, p);
						// layout.setLocation(newVertex, p);
						// vv.getModel().restart();
						MainWindow.getInstance().updateElementTree();
						// MainWindowSingelton.getInstance()
						// .updateTheoryProperties();

						for (Iterator<BiologicalNodeAbstract> iterator = graph
								.getVertices().iterator(); iterator.hasNext();) {
							layout.lock(iterator.next(), false);
						}

					}

				}
				if(pw instanceof BiologicalNodeAbstract){
					
				}
			}
			vv.repaint();
		}
	}

	/**
	 * If startVertex is non-null, and the mouse is released over an existing
	 * vertex, create an undirected edge from startVertex to the vertex under
	 * the mouse pointer. If shift was also pressed, create a directed edge
	 * instead.
	 */
	public void mouseReleased(MouseEvent e) {
		pw = graphInstance.getPathway();
		if (checkModifiers(e)) {
			final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph().getVisualizationViewer();
			//final Point2D p = vv.getRenderContext().getMultiLayerTransformer()
			//		.inverseTransform(e.getPoint());
			//int v = vv.getPickedVertexState().getPicked().size();
			//int edge = vv.getPickedEdgeState().getPicked().size();
			GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv
					.getPickSupport();
			// if (v > 0) {
			// System.out.println("release");
			final BiologicalNodeAbstract vertex = (BiologicalNodeAbstract) pickSupport
					.getVertex(vv.getGraphLayout(), e.getPoint().getX(), e
							.getPoint().getY());
			if (vertex != null && startVertex != null) {

				// Pathway pw = graphInstance.getPathway();

				BiologicalNodeAbstract start = startVertex;// (BiologicalNodeAbstract)
															// pw
				// .getNodeByVertexID(startVertex.toString());
				BiologicalNodeAbstract end = vertex;// (BiologicalNodeAbstract)
													// pw
				// .getNodeByVertexID(vertex.toString());

				if (pw.isPetriNet()
						&& !((start instanceof Place && end instanceof Transition) || (start instanceof Transition && end instanceof Place))){
					MyPopUp.getInstance().show("Operation not allowed", "In a petri net only Transition->Place and Place->Transition Relations are allowed!");
				} else {
					// Graph graph = vv.getGraphLayout().getGraph();
					EdgeDialog dialog = new EdgeDialog(startVertex, vertex);
					Pair<String[], BiologicalNodeAbstract[]> answer = dialog.getAnswer();
					String[] answers = answer.getLeft();
					BiologicalNodeAbstract[] nodes = answer.getRight();

					if (answers != null) {
						if (answers[2] != null && pw.isPetriNet()
								&& (answers[2].toLowerCase().contains("inhibi") || answers[2].toLowerCase().contains("test"))
								&& !(startVertex instanceof Place && vertex instanceof Transition)){
							MyPopUp.getInstance().show("Operation not allowed", "Inhibitory / Test Edges are only possible from Place to Transition!");
						} else {
							String name = answers[0];
							String label = answers[0];
							String element = answers[2];
							
							boolean directed = false;
							if (answers[1].equals("directed_edge")) {
								directed = true;
							}

							Set<BiologicalNodeAbstract> parentBNAs = new HashSet<BiologicalNodeAbstract>();
							parentBNAs.addAll(nodes[0].getAllParentNodes());
							parentBNAs.addAll(nodes[1].getAllParentNodes());
							BiologicalEdgeAbstract bea = pw.addEdge(label, name, nodes[0], nodes[1], element, directed);
							if(bea instanceof PNEdge){
								((PNEdge)bea).setFunction(label);
							}
							if(nodes[0]==startVertex && nodes[1]==vertex){
								pw.addEdgeToView(bea, false);
							}
							else{
								BiologicalEdgeAbstract clone = bea.clone();
								clone.setFrom(startVertex);
								clone.setTo(vertex);
								pw.addEdgeToView(clone, false);
							}
						}
					}
				}
			}
			vv.repaint();
			startVertex = null;
			down = null;
			edgeIsDirected = false;
			vv.removePostRenderPaintable(edgePaintable);
			vv.removePostRenderPaintable(arrowPaintable);
		}
	}

	/**
	 * If startVertex is non-null, stretch an edge shape between startVertex and
	 * the mouse pointer to simulate edge creation
	 */
	public void mouseDragged(MouseEvent e) {
		if (checkModifiers(e)) {
			if (startVertex != null) {
				transformEdgeShape(down, e.getPoint());
				if (edgeIsDirected) {
					transformArrowShape(down, e.getPoint());
				}
			}
			VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph().getVisualizationViewer();
			vv.repaint();
		}
	}

	/**
	 * code lifted from PluggableRenderer to move an edge shape into an
	 * arbitrary position
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

		public void paint(Graphics g) {
			if (edgeShape != null) {
				Color oldColor = g.getColor();
				g.setColor(Color.black);
				((Graphics2D) g).draw(edgeShape);
				g.setColor(oldColor);
			}
		}

		public boolean useTransform() {
			return false;
		}
	}

	/**
	 * Used for the directed edge creation visual effect during mouse drag
	 */
	class ArrowPaintable implements Paintable {

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

	public void mouseClicked(MouseEvent e) {
		// System.out.println("click: ");
		// System.out.println(e.getClickCount());
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}
