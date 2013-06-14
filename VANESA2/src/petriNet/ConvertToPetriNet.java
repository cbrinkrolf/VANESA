package petriNet;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphInstance;
import graph.animations.RegulationTabelModel;
import gui.MainWindowSingelton;
import gui.algorithms.ScreenSize;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class ConvertToPetriNet {
	private petriNetProperties prop;
	GraphInstance graphInstance = new GraphInstance();
	InternalGraphRepresentation graphRepresentation;
	ArrayList<Transition> transitions = new ArrayList<Transition>();
	Pathway pw;
	
private final int scaleFactor=2;
private final double initialTokens = 10;
	
	
	private static ConvertToPetriNet instance = null;

	/** synchronized needed for thread-safety */
	public static synchronized ConvertToPetriNet getInstance() {
		if (instance == null) {
			instance = new ConvertToPetriNet();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public ConvertToPetriNet() {
		int answer=JOptionPane.YES_NO_OPTION;
		if (!graphInstance.getPathway().getAllNodes().isEmpty()) 
		answer = JOptionPane.showOptionDialog(
				MainWindowSingelton.getInstance(),
				"What type of Petri Net do you want?", "Choose PN type...?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new String[] { "Discrete PN", "Continuous PN" },
				JOptionPane.CANCEL_OPTION);
		if (answer != JOptionPane.YES_OPTION && answer != JOptionPane.NO_OPTION) {
			ContainerSingelton.getInstance().setPetriView(false);
			return;
		}
		
		
		ContainerSingelton.getInstance().setPetriView(true);
		prop = new petriNetProperties();
		// System.out.println("alle knoten: "+GraphInstance.getMyGraph().getAllvertices());

		Set<BiologicalNodeAbstract> hsVertex = new HashSet<BiologicalNodeAbstract>();
		hsVertex = graphInstance.getPathway().getAllNodes();// GraphInstance.getMyGraph().getAllvertices();
		DefaultSettableVertexLocationFunction locations = graphInstance
				.getPathway().getGraph().getVertexLocations();
		Set<BiologicalEdgeAbstract> hsEdge = new HashSet<BiologicalEdgeAbstract>();
		hsEdge = graphInstance.getPathway().getAllEdges();

		pw = new CreatePathway().getPathway();
		pw.getGraph().lockVertices();
		pw.getGraph().stopVisualizationModel();
		pw.setPetriNet(true);
		Iterator<BiologicalNodeAbstract> it = hsVertex.iterator();
		BiologicalNodeAbstract bna;
		Place p;

		while (it.hasNext()) {
			bna = (BiologicalNodeAbstract) it.next();
			// bna =
			// System.out.println("Name: "+bna.getName());
			// System.out.println("Label: "+bna.getLabel());
			// System.out.println("V-name: "+bna.getVertex().toString());
			p = new Place(bna.getLabel(), bna.getVertex().toString(), pw
					.getGraph().createNewVertex(), this.initialTokens,
					answer == JOptionPane.YES_OPTION);
			p.setTokenStart(this.initialTokens);
			p.setTokenMax(1000);
			p = (Place) pw.addElement(p);
			p.setColor(bna.getColor());
			// System.out.println("Vertex: "+p.getName());
			// System.out.println("x: "+locations.getLocation(bna.getVertex()).getX());
			double x = locations.getLocation(bna.getVertex()).getX();
			double y = locations.getLocation(bna.getVertex()).getY();
			// System.out.println("x: "+x+" y: "+y);
			pw.getGraph().moveVertex(p.getVertex(), scaleFactor* x,scaleFactor* y);
			// this.graphRepresentation.getAllVertices();

		}

		BiologicalEdgeAbstract bea;
		double x1;
		double y1;
		double x2;
		double y2;

		Place p1;
		Place p2;
		Vertex v1;
		Vertex v2;

		DefaultSettableVertexLocationFunction locationsNew = pw.getGraph()
				.getVertexLocations();
		Iterator itEdge1 = hsEdge.iterator();
		// System.out.println("test");

		int countTrainsition = 0;
		Iterator itEdge = hsEdge.iterator();

		boolean isUnDirEdge = false;

		while (itEdge.hasNext()) {
			bea = (BiologicalEdgeAbstract) itEdge.next();

			/*
			 * if (bea instanceof ReactionEdge) {
			 * 
			 * } else { System.out.println(bea.getClass()); }
			 */
			// ReactionPairEdge rpe = bea.getReactionPairEdge();
			// System.out.println("first_1: "+bea.getEdge().getEndpoints().getFirst());
			// System.out.println("second: "+bea.getEdge().getEndpoints().getSecond());
			// v1 = (Vertex) bea.getEdge().getEndpoints().getFirst();
			p1 = (Place) pw.getNodeByName(bea.getEdge().getEndpoints()
					.getFirst().toString());
			v1 = p1.getVertex();
			p2 = (Place) pw.getNodeByName(bea.getEdge().getEndpoints()
					.getSecond().toString());
			v2 = p2.getVertex();
			Transition t;
			if (answer == JOptionPane.YES_OPTION)
				t = new DiscreteTransition(p1.getName() + "_" + p2.getName(),
						"t" + countTrainsition, pw.getGraph().createNewVertex());
			else
				t = new ContinuousTransition(p1.getName() + "_" + p2.getName(),
						"t" + countTrainsition, pw.getGraph().createNewVertex());

			t = (Transition) pw.addElement(t);
			PNEdge edge1 = new PNEdge(pw.getGraph().createEdge(v1,
					t.getVertex(), true), "label", "name", "discrete", "1");
			edge1.setDirected(true);
			// System.out.println(edge1.isDirected());
			// System.out.println(edge1.getEdge().getClass());
			PNEdge edge2 = new PNEdge(pw.getGraph().createEdge(t.getVertex(),
					v2, true), "label2", "name2", bea.getBiologicalElement(),
					"1");

			if (!bea.isDirected()) {
				isUnDirEdge = true;
				edge1.wasUndirected(true);
				edge2.wasUndirected(true);
			}

			pw.addElement(edge1);

			edge2.setDirected(true);
			pw.addElement(edge2);

			// p1orginal = (Place)
			// graphInstance.getPathway().getNodeByName(p1.getName());
			// System.out.println("Placename: "+p1orginal.getName());
			// v1orginal = p1orginal.getVertex();
			x1 = locationsNew.getLocation(p1.getVertex()).getX();
			y1 = locationsNew.getLocation(p1.getVertex()).getY();
			x2 = locationsNew.getLocation(p2.getVertex()).getX();
			y2 = locationsNew.getLocation(p2.getVertex()).getY();
			// System.out.println("x1: "+x1 +" x2: "+x2+" y1: "+y1+" y2");
			pw.getGraph().moveVertex(t.getVertex(), (x2 + x1) / 2,
					(y2 + y1) / 2);
			this.transitions.add(t);
			countTrainsition++;

		}

		// this.simulateSteps(10);

		HashSet allNodes = pw.getAllNodes();
		HashSet<Place> allPlaces = new HashSet<Place>();
		Iterator iter = allNodes.iterator();
		// BiologicalNodeAbstract bna;
		while (iter.hasNext()) {
			bna = (BiologicalNodeAbstract) iter.next();
			// System.out.println(bna.getClass().getName());
			if (bna.getClass().getName().equals("petriNet.Place")) {

				p = (Place) bna;
				allPlaces.add(p);
				// System.out.println("added");
			}
		}
//		int tokenMax = this.getMaxToken(allPlaces);
//		int color;
//		// System.out.println(allPlaces.size());
//		Iterator<Place> iterP = allPlaces.iterator();
//		while (iterP.hasNext()) {
//			p = (Place) iterP.next();
//			color = (int) 255 * (int) p.getToken() / tokenMax;
//			p.setRelativeColor(color, color, color);
//			// System.out.println("Color: "+color);
//		}
		pw.getGraph().unlockVertices();
		pw.getGraph().restartVisualizationModel();
		MainWindowSingelton.getInstance().updateProjectProperties();
		MainWindowSingelton.getInstance().updateOptionPanel();


		if (isUnDirEdge)
			JOptionPane
					.showMessageDialog(
							MainWindowSingelton.getInstance(),
							"There were some undirected Edges in your graph. For creating the PN the program had interpretated those randomly and marked them red. So you can change their direction if you want.",
							"Undirected Edges found...",
							JOptionPane.WARNING_MESSAGE);
		Iterator<BiologicalEdgeAbstract> ite = pw.getAllEdges().iterator();
		while (ite.hasNext()) {
			PNEdge e = (PNEdge) ite.next();
			if (e.wasUndirected()) {
				e.setColor(Color.red);
				e.setHidden(false);
				e.setReference(false);
				e.setVisible(true);
			}
		}
		if (pw.countNodes()>0) new PNTableDialog().setVisible(true);
	}

	public petriNetProperties getProp() {
		return prop;
	}

	public void setProp(petriNetProperties prop) {
		this.prop = prop;
	}

	@SuppressWarnings("unchecked")
	private void simulateStep() {
		Transition t;
		Set<Edge> inEdges = new HashSet<Edge>();
		Set<Edge> outEdges = new HashSet<Edge>();
		Iterator it;
		Edge e;
		Place p;

		ArrayList<Place> placesIn = new ArrayList<Place>();
		ArrayList<Place> placesOut = new ArrayList<Place>();
		boolean condition = true;
		for (int i = 0; i < this.transitions.size(); i++) {
			condition = true;
			placesIn.clear();
			// placesOut.clear();
			t = this.transitions.get(i);
			inEdges = t.getVertex().getInEdges();
			it = inEdges.iterator();
			while (it.hasNext() && condition) {
				e = (Edge) it.next();
				// Object o =
				// pw.getNodeByName(e.getEndpoints().getFirst().toString());
				// System.out.println("first: "+e.getEndpoints().getSecond());
				p = (Place) pw.getNodeByVertexID(e.getEndpoints().getFirst()
						.toString());
				placesIn.add(p);
				if (p.getToken() <= 0) {
					condition = false;
				}
				// System.out.println(p.getToken());
				// System.out.println("second: "+e.getEndpoints().getSecond());
			}
			if (condition) {
				outEdges = t.getVertex().getOutEdges();
				it = outEdges.iterator();
				while (it.hasNext()) {
					e = (Edge) it.next();
					p = (Place) pw.getNodeByVertexID(e.getEndpoints()
							.getSecond().toString());
					placesOut.add(p);
					p.consume(-1);
				}
				for (int j = 0; j < placesIn.size(); j++) {
					p = placesIn.get(j);
					p.consume(1);
				}
			}

		}
	}

	private void simulateSteps(int steps) {
		for (int i = 0; i < steps; i++) {
			this.simulateStep();
		}
	}

	private int getMaxToken(HashSet<Place> places) {
		Iterator<Place> it = places.iterator();
		Place p;
		int max = 0;
		while (it.hasNext()) {
			p = (Place) it.next();
			max = Math.max(max, (int) p.getToken());
		}
		return max;
	}
}
