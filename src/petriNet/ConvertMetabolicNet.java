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
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.SmallMolecule;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.Place;
import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphInstance;
import gui.MainWindow;

public class ConvertMetabolicNet {
	private petriNetProperties prop;
	private GraphInstance graphInstance = new GraphInstance();
	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	private Pathway pw;

	private final int scaleFactor = 1;
	private final double initialTokens = 10;

	private static ConvertMetabolicNet instance = null;

	/** synchronized needed for thread-safety */
	public static synchronized ConvertMetabolicNet getInstance() {
		if (instance == null) {
			instance = new ConvertMetabolicNet();
		}
		return instance;
	}

	public ConvertMetabolicNet() {
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
			ContainerSingelton.getInstance().setPetriView(false);
			return;
		}

		ContainerSingelton.getInstance().setPetriView(true);
		prop = new petriNetProperties();
		// System.out.println("alle knoten: "+GraphInstance.getMyGraph().getAllvertices());

		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> node2place = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();

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
		BiologicalNodeAbstract bna2;
		Place p;
		Transition t;
		while (it.hasNext()) {

			bna = it.next();
			if(bna instanceof SmallMolecule){
			// bna =
			// System.out.println("Name: "+bna.getName());
			// System.out.println("Label: "+bna.getLabel());
			// System.out.println("V-name: "+bna.getVertex().toString());
			p = new Place(bna.getLabel(), bna.getName(), this.initialTokens,
					answer == JOptionPane.YES_OPTION);
			p.setTokenStart(this.initialTokens);
			//p.setTokenMax(1000);

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
			}else if(bna instanceof Enzyme){
				t = new ContinuousTransition(bna.getLabel(), bna.getName());
				t.setColor(bna.getColor());
				// System.out.println("Vertex: "+p.getName());
				// System.out.println("x: "+locations.getLocation(bna.getVertex()).getX());
				double x = pwOld.getGraph().getVertexLocation(bna).getX();// locations.getLocation(bna.getVertex()).getX();
				double y = pwOld.getGraph().getVertexLocation(bna).getY();// locations.getLocation(bna.getVertex()).getY();
				// System.out.println("x: "+x+" y: "+y);
				// pw.getGraph().moveVertex(p.getVertex(), scaleFactor*
				// x,scaleFactor* y);
				pw.addVertex(t,
						new Point2D.Double(scaleFactor * x, scaleFactor * y));
				node2place.put(bna, t);
			}

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
			bna = node2place.get(bea.getFrom());
			// v1 = p1.getVertex();
			bna2 = node2place.get(bea.getTo());
			// v2 = p2.getVertex();
			//Transition t;
			
			

			PNEdge edge1 = new PNEdge(bna, bna2, bea.getLabel(), bea.getName(), "discrete", bea.getLabel());
			edge1.setDirected(true);
			// System.out.println(edge1.isDirected());
			// System.out.println(edge1.getEdge().getClass());
			
			edge1.setDirected(true);

			pw.addEdge(edge1);

			// p1orginal = (Place)
			// graphInstance.getPathway().getNodeByName(p1.getName());
			// System.out.println("Placename: "+p1orginal.getName());
			// v1orginal = p1orginal.getVertex();
			// System.out.println("x1: "+x1 +" x2: "+x2+" y1: "+y1+" y2");
			//this.transitions.add(t);
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
		if (pw.countNodes() > 0){
			new PNTableDialog().setVisible(true);
		}
		pw.getGraph().normalCentering();
		
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
