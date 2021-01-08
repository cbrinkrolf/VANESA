package petriNet;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;

public class ConvertToPetriNet {
	private petriNetProperties prop;
	private GraphInstance graphInstance = new GraphInstance();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private Pathway pw;

	private final int scaleFactor = 2;
	private final double initialTokens = 10;

	private static ConvertToPetriNet instance = null;

	/** synchronized needed for thread-safety */
	public static synchronized ConvertToPetriNet getInstance() {
		if (instance == null) {
			instance = new ConvertToPetriNet();
		}
		return instance;
	}

	public ConvertToPetriNet() {
		int answer = JOptionPane.YES_NO_OPTION;
		if (!graphInstance.getPathway().getAllGraphNodes().isEmpty())
			answer = JOptionPane.showOptionDialog(
					MainWindow.getInstance(),
					"What type of Petri Net do you want?",
					"Choose PN type...?", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] {
							"Discrete PN", "Continuous PN" },
					JOptionPane.CANCEL_OPTION);
		if (answer != JOptionPane.YES_OPTION && answer != JOptionPane.NO_OPTION) {
			GraphContainer.getInstance().setPetriView(false);
			return;
		}

		GraphContainer.getInstance().setPetriView(true);
		prop = new petriNetProperties();
		// System.out.println("alle knoten: "+GraphInstance.getMyGraph().getAllvertices());

		HashMap<BiologicalNodeAbstract, Place> node2place = new HashMap<BiologicalNodeAbstract, Place>();

		// Set<BiologicalNodeAbstract> hsVertex = new
		// HashSet<BiologicalNodeAbstract>();
		// HashMap locations = graphInstance
		// .getPathway().getGraph().getVertexLocations();

		Pathway pwOld = graphInstance.getPathway();

		pw = new CreatePathway().getPathway();
		pw.setPetriNet(true);
		Iterator<BiologicalNodeAbstract> it = pwOld.getGraph().getAllVertices()
				.iterator();
		BiologicalNodeAbstract bna;
		Place p;
		while (it.hasNext()) {

			bna = it.next();
			// bna =
			// System.out.println("Name: "+bna.getName());
			// System.out.println("Label: "+bna.getLabel());
			// System.out.println("V-name: "+bna.getVertex().toString());
			if(answer == JOptionPane.YES_OPTION){
				p = new DiscretePlace(bna.getLabel(), bna.getName());
			}else{
				p = new ContinuousPlace(bna.getLabel(), bna.getName());
			}
			
			p.setTokenStart(this.initialTokens);
			p.setTokenMax(1000);

			p.setColor(bna.getColor());
			// System.out.println("Vertex: "+p.getName());
			// System.out.println("x: "+locations.getLocation(bna.getVertex()).getX());
			double x = pwOld.getGraph().getVertexLocation(bna).getX();// locations.getLocation(bna.getVertex()).getX();
			double y = pwOld.getGraph().getVertexLocation(bna).getY();// locations.getLocation(bna.getVertex()).getY();
			// System.out.println("x: "+x+" y: "+y);
			// pw.getGraph().moveVertex(p.getVertex(), scaleFactor*
			// x,scaleFactor* y);
			pw.addVertex(p,
					new Point2D.Double(scaleFactor * x, scaleFactor * y));
			// this.graphRepresentation.getAllVertices();
			node2place.put(bna, p);

		}

		BiologicalEdgeAbstract bea;
		double x1;
		double y1;
		double x2;
		double y2;

		Place p1;
		Place p2;
		// Vertex v1;
		// Vertex v2;

		// DefaultSettableVertexLocationFunction locationsNew = pw.getGraph()
		// .getVertexLocations();
		// Iterator itEdge1 = hsEdge.iterator();
		// System.out.println("test");

		int countTrainsition = 0;
		Iterator<BiologicalEdgeAbstract> itEdge = pwOld.getAllEdges()
				.iterator();

		boolean isUnDirEdge = false;
		while (itEdge.hasNext()) {
			bea = itEdge.next();

			/*
			 * if (bea instanceof ReactionEdge) {
			 * 
			 * } else { System.out.println(bea.getClass()); }
			 */
			// ReactionPairEdge rpe = bea.getReactionPairEdge();
			// System.out.println("first_1: "+bea.getEdge().getEndpoints().getFirst());
			// System.out.println("second: "+bea.getEdge().getEndpoints().getSecond());
			// v1 = (Vertex) bea.getEdge().getEndpoints().getFirst();
			p1 = node2place.get(bea.getFrom());
			// v1 = p1.getVertex();
			p2 = node2place.get(bea.getTo());
			// v2 = p2.getVertex();
			Transition t;
			if (answer == JOptionPane.YES_OPTION)
				/*
				 * t = new DiscreteTransition(p1.getName() + "_" + p2.getName(),
				 * "t" + countTrainsition);
				 */
				t = new DiscreteTransition("t" + countTrainsition, "t"
						+ countTrainsition);
			else
				/*
				 * t = new ContinuousTransition(p1.getName() + "_" +
				 * p2.getName(), "t" + countTrainsition);
				 */
				t = new ContinuousTransition("t" + countTrainsition, "t"
						+ countTrainsition);

			x1 = pw.getGraph().getVertexLocation(p1).getX();// locationsNew.getLocation(p1.getVertex()).getX();
			y1 = pw.getGraph().getVertexLocation(p1).getY();// locationsNew.getLocation(p1.getVertex()).getY();
			x2 = pw.getGraph().getVertexLocation(p2).getX();// locationsNew.getLocation(p2.getVertex()).getX();
			y2 = pw.getGraph().getVertexLocation(p2).getY();// locationsNew.getLocation(p2.getVertex()).getY();

			pw.addVertex(t, new Point2D.Double((x2 + x1) / 2, (y2 + y1) / 2));

			PNEdge edge1 = new PNEdge(p1, t, "label", "name", "discrete", "1");
			edge1.setDirected(true);
			// System.out.println(edge1.isDirected());
			// System.out.println(edge1.getEdge().getClass());
			PNEdge edge2 = new PNEdge(t, p2, "label2", "name2",
					bea.getBiologicalElement(), "1");

			if (!bea.isDirected()) {
				isUnDirEdge = true;
				edge1.wasUndirected(true);
				edge2.wasUndirected(true);
			}

			pw.addEdge(edge1);

			edge2.setDirected(true);
			pw.addEdge(edge2);

			// p1orginal = (Place)
			// graphInstance.getPathway().getNodeByName(p1.getName());
			// System.out.println("Placename: "+p1orginal.getName());
			// v1orginal = p1orginal.getVertex();
			// System.out.println("x1: "+x1 +" x2: "+x2+" y1: "+y1+" y2");
			this.transitions.add(t);
			countTrainsition++;

		}

		// this.simulateSteps(10);

		HashSet<Place> allPlaces = new HashSet<Place>();
		Iterator<BiologicalNodeAbstract> iter = pw.getAllGraphNodes().iterator();
		// BiologicalNodeAbstract bna;
		while (iter.hasNext()) {
			bna = iter.next();
			// System.out.println(bna.getClass().getName());
			if (bna.getClass().getName().equals("petriNet.Place")) {

				p = (Place) bna;
				allPlaces.add(p);
				// System.out.println("added");
			}
		}
		// int tokenMax = this.getMaxToken(allPlaces);
		// int color;
		// // System.out.println(allPlaces.size());
		// Iterator<Place> iterP = allPlaces.iterator();
		// while (iterP.hasNext()) {
		// p = (Place) iterP.next();
		// color = (int) 255 * (int) p.getToken() / tokenMax;
		// p.setRelativeColor(color, color, color);
		// // System.out.println("Color: "+color);
		// }
		pw.getGraph().restartVisualizationModel();
		MainWindow.getInstance().updateProjectProperties();
		MainWindow.getInstance().updateOptionPanel();

		if (isUnDirEdge)
			JOptionPane
					.showMessageDialog(
							MainWindow.getInstance(),
							"There were some undirected Edges in your graph. For creating the PN the program had interpretated those randomly and marked them red. So you can change their direction if you want.",
							"Undirected Edges found...",
							JOptionPane.WARNING_MESSAGE);
		Iterator<BiologicalEdgeAbstract> ite = pw.getAllEdges().iterator();
		while (ite.hasNext()) {
			PNEdge e = (PNEdge) ite.next();
			if (e.wasUndirected()) {
				e.setColor(Color.red);
				e.setVisible(true);
			}
		}
		if (pw.countNodes() > 0)
			new PNTableDialog().setVisible(true);
	}

	public petriNetProperties getProp() {
		return prop;
	}

	public void setProp(petriNetProperties prop) {
		this.prop = prop;
	}

	/*
	 * private void simulateStep() { Transition t; Set<Edge> inEdges = new
	 * HashSet<Edge>(); Set<Edge> outEdges = new HashSet<Edge>(); Iterator it;
	 * Edge e; Place p;
	 * 
	 * ArrayList<Place> placesIn = new ArrayList<Place>(); ArrayList<Place>
	 * placesOut = new ArrayList<Place>(); boolean condition = true; for (int i
	 * = 0; i < this.transitions.size(); i++) { condition = true;
	 * placesIn.clear(); // placesOut.clear(); t = this.transitions.get(i);
	 * inEdges = t.getVertex().getInEdges(); it = inEdges.iterator(); while
	 * (it.hasNext() && condition) { e = (Edge) it.next(); // Object o = //
	 * pw.getNodeByName(e.getEndpoints().getFirst().toString()); //
	 * System.out.println("first: "+e.getEndpoints().getSecond()); p = (Place)
	 * pw.getNodeByVertexID(e.getEndpoints().getFirst() .toString());
	 * placesIn.add(p); if (p.getToken() <= 0) { condition = false; } //
	 * System.out.println(p.getToken()); //
	 * System.out.println("second: "+e.getEndpoints().getSecond()); } if
	 * (condition) { outEdges = t.getVertex().getOutEdges(); it =
	 * outEdges.iterator(); while (it.hasNext()) { e = (Edge) it.next(); p =
	 * (Place) pw.getNodeByVertexID(e.getEndpoints() .getSecond().toString());
	 * placesOut.add(p); p.consume(-1); } for (int j = 0; j < placesIn.size();
	 * j++) { p = placesIn.get(j); p.consume(1); } }
	 * 
	 * } }
	 * 
	 * private void simulateSteps(int steps) { for (int i = 0; i < steps; i++) {
	 * this.simulateStep(); } }
	 */

	/*
	 * private int getMaxToken(HashSet<Place> places) { Iterator<Place> it =
	 * places.iterator(); Place p; int max = 0; while (it.hasNext()) { p =
	 * (Place) it.next(); max = Math.max(max, (int) p.getToken()); } return max;
	 * }
	 */
}
