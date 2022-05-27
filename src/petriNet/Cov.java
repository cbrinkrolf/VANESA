package petriNet;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
//import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
//import edu.uci.ics.jung.utils.Pair;
import graph.CreatePathway;
import graph.GraphInstance;

public class Cov {

	private Pathway pw;
	private GraphInstance graphInstance = new GraphInstance();
	// private MyGraph g;
	private HashMap<BiologicalNodeAbstract, Integer> hmplaces = new HashMap<BiologicalNodeAbstract, Integer>();
	private HashMap<BiologicalNodeAbstract, Integer> hmtransitions = new HashMap<BiologicalNodeAbstract, Integer>();
	private SimpleMatrixDouble bMatrix;
	private SimpleMatrixDouble fMatrix;
	private SimpleMatrixDouble cMatrix;
	private int numberPlaces = 0;
	private int numberTransitions = 0;
	private ArrayList<Double> start = new ArrayList<Double>();
	private ArrayList<CovNode> parents;
	private String oldName;
	private CovNode root;

	public String getOldName() {
		return oldName;
	}

	private String newName;

	// private boolean found = false;
	private HashMap<Integer, String> idToName = new HashMap<Integer, String>();
	private HashMap<Integer, String> idToNameTransition = new HashMap<Integer, String>();
	private HashMap<Integer, Double> idToMax = new HashMap<Integer, Double>();

	// private HashMap<String, CovNode> covnodes = new HashMap<String, CovNode>();

	private HashMap<String, Integer> name2id = new HashMap<String, Integer>();

	public Cov() {

		// Iterator it6 =
		// this.graphInstance.getContainer().getAllPathways().iterator();

		// this.createMatrices();
		// this.createMatrices();
		this.createMatrices();

		pw = new CreatePathway().getPathway();
		this.newName = pw.getName();
		root = new CovNode("label", "name", this.numberPlaces);
		CovList list = new CovList(numberPlaces);
		// node.setVisible(false);
		double[] tmp = new double[start.size()];
		for (int i = 0; i < start.size(); i++) {
			tmp[i] = this.start.get(i);
			// System.out.print(this.start.get(i));
		}
		list.setElements(tmp);
		root.setTokenList(list);
		// System.out.println(node.getTokenList());
		pw.addVertex(root, new Point(10, 30));
		root.setColor(new Color(255, 0, 0));

		// pw.getGraph().moveVertex(root.getVertex(), 10, 30);
		// this.covnodes.put(root.getVertex().toString(), root);
		// System.out.println(this.covnodes);
		parents = new ArrayList<CovNode>();
		this.computeNode(root);

		/*
		 * Set<CovNode> nodes; nodes = graphInstance.getPathway().getAllNodes(); //
		 * System.out.println("covlist alt: " + cl);
		 * 
		 * //tmp.addTokens(this.cMatrix.getColumn(i)); //boolean found = false; Iterator
		 * it = nodes.iterator(); CovNode cn; while (it.hasNext()) { cn = (CovNode)
		 * it.next(); System.out.println(cn.getTokenList()); }
		 */
		// System.out.println("Knoten: " + pw.getGraph().getAllvertices().size());
		// pw.getGraph().moveVertex(node.getVertex(), 10, 30);
		// pw.getGraph().unlockVertices();
		// pw.getGraph().restartVisualizationModel();
		// MainWindowSingelton.getInstance().updateProjectProperties();
		// MainWindowSingelton.getInstance().updateOptionPanel();
		// System.out.println("------------------------");
		// Set<CovNode> nodes;
		// nodes = graphInstance.getPathway().getAllNodes();
		// Iterator it = nodes.iterator();
		// CovNode n;
		// while (it.hasNext()) {
		// n = (CovNode) it.next();
		// System.out.println(n.getTokenList());
		// n.setColor(new Color(255,0,0));
		// }

		// System.out.println("------covnodes----");

		// System.out.println(this.covnodes);

		// System.out.println("newname: " + pw.getName());
		// System.out.println("oldname: " + this.oldName);
		// Pathway p = this.graphInstance.getContainer().getPathway(this.oldName);

		// System.out.println(p.getAllNodeLabels());
		this.paintCoveredNodes();

		pw.getGraph().restartVisualizationModel();

		pw.getPetriPropertiesNet().setCovGraph(pw.getName());

	}

	public CovNode getRoot() {
		return root;
	}

	private void computeNode(CovNode parent) {
		CovList cl = parent.getTokenList();
		CovList tmp;
		ArrayList<Integer> indexe;
		// boolean found;
		// System.out.println("Root: " + cl);
		// fuer alle Transitionen
		for (int i = 0; i < numberTransitions; i++) {
			double[] col = this.fMatrix.getColumn(i);

			// System.out.println("Transition: " + i);
			// System.out.println("col: "+col.toString());

			// Wenn Markierung groesser gleich Transition ist
			if (cl.isGreaterEqualCol(col)) {
				tmp = parent.getTokenList().clone();
				tmp.addTokens(this.cMatrix.getColumn(i));
				// System.out.println("new: " + tmp);
				boolean found = false;
				Iterator<BiologicalNodeAbstract> it = graphInstance.getPathway().getAllGraphNodes().iterator();
				CovNode cn;

				if (this.isBoundaryHold(tmp)) {
					// fuer alle Knoten: Wenn neue Markierung schon vorhanden
					// ist
					while (it.hasNext()) {
						cn = (CovNode) it.next();
						if (cn.getTokenList().isEqual(tmp.getElements())
								&& !cn.getTokenList().equals(parent.getTokenList())) {

							CovEdge e = new CovEdge(this.idToNameTransition.get(i), this.idToNameTransition.get(i),
									parent, cn);
							e.setDirected(true);
							pw.addEdge(e);
							// e.setVisible(false);
							// System.out.println("Kante1 von: "
							// + parent.getTokenList() + "->"
							// + cn.getTokenList());
							found = true;
							break;
						}
					}

					// Knoten nicht direkt gefunden
					if (!found) {
						this.parents.clear();
						// System.out.println("size parents vorher: "
						// + this.parents.size());
						this.getParents(parent);
						// System.out.println("size parents nachher: "
						// + this.parents.size());
						for (int j = 0; j < this.parents.size(); j++) {
							cn = this.parents.get(j);

							// falls neue Markierung eine schon vorhandene
							// Markierung ueberdeckt
							if (tmp.isGreater(cn.getTokenList().getElements())) {
								// System.out.println(tmp + " is greater as "
								// + cn.getTokenList());
								indexe = tmp.getGreaterIndexs(cn.getTokenList());
								for (int k = 0; k < indexe.size(); k++) {
									if (this.idToMax.get(indexe.get(k)) <= 0) {
										tmp.setElementAt(indexe.get(k), -1.0);
									}
								}

								// fuer alle Knoten
								Iterator<BiologicalNodeAbstract> it2 = graphInstance.getPathway().getAllGraphNodes()
										.iterator();
								boolean cond1;
								boolean cond2;
								while (it2.hasNext()) {
									cn = (CovNode) it2.next();
									cond1 = cn.getTokenList().isEqual(tmp.getElements());
									cond2 = cn.getTokenList().toString().equals(parent.getTokenList().toString());
									// System.out.println(cond1 + " " + cond2);
									// System.out.println("cn: " +
									// cn.getTokenList());
									// System.out.println("tmp: " +
									// tmp.getElements());
									// System.out.println("!cn: "
									// + cn.getTokenList().toString());
									// System.out.println();
									// Wenn neue Markierung schon vorhanden ist
									if (cond1 && !cond2) {
										CovEdge e = new CovEdge(this.idToNameTransition.get(i),
												this.idToNameTransition.get(i), parent, cn);
										e.setDirected(true);
										pw.addEdge(e);
										// e.setVisible(false);
										// System.out.println("Kante2 von: "
										// + parent.getTokenList() + "->"
										// + cn.getTokenList());
										found = true;
										break;
									}
									if (cond1 && cond2) {
										found = true;
										break;
									}
								}

								if (!found) {
									CovNode n = new CovNode("label2", "name2", this.numberPlaces);
									n.setTokenList(tmp.clone());

									pw.addVertex(n, new Point(i * 10, i * i * 30));
									// n.setVisible(false);
									CovEdge e = new CovEdge(this.idToNameTransition.get(i),
											this.idToNameTransition.get(i), parent, n);
									e.setDirected(true);

									// pw.getGraph().moveVertex(n.getVertex(),
									// i * 10, i * i * 30);
									// this.covnodes.put(n.getVertex().toString(),
									// n);
									pw.addEdge(e);
									// e.setVisible(false);

//									System.out
//											.println("neuer ueberdeckter node: "
//													+ n.getTokenList());
									this.computeNode(n);
									found = true;
									break;
								}
							}
						}

						// wenn Markierung noch nicht im Graph
						if (!found) {
							CovNode n = new CovNode("label2", "name2", this.numberPlaces);
							n.setTokenList(tmp);

							// System.out.println("1: " +
							// parent.getTokenList());
							// System.out.println("2: " + tmp.toString());
							if (!parent.getTokenList().toString().equals(tmp.toString())) {
								pw.addVertex(n, new Point(i * 10, i * i * 30));
								// n.setVisible(false);
								CovEdge e = new CovEdge(this.idToNameTransition.get(i), this.idToNameTransition.get(i),
										parent, n);
								e.setDirected(true);
								// this.covnodes.put(n.getVertex().toString(), n);
								pw.addEdge(e);
								// e.setVisible(false);
								// System.out.println("Kante3 von: "
								// + parent.getTokenList() + "->"
								// + n.getTokenList());
								// pw.getGraph().moveVertex(n.getVertex(), i * 10,
								// i * i * 30);
								// System.out.println("neuer node, nicht gef. "
								// + n.getTokenList());
								found = false;
								this.computeNode(n);
							}
						}
					}
				}
			}
		}
	}

	private void createMatrices() {

		// DefaultSettableVertexLocationFunction locations = graphInstance
		// .getPathway().getGraph().getVertexLocations();
		this.oldName = this.graphInstance.getPathway().getName();
		// Hashmaps fuer places und transitions
		Iterator<BiologicalNodeAbstract> hsit = graphInstance.getPathway().getAllGraphNodes().iterator();
		BiologicalNodeAbstract bna;
		// Place p;
		// System.out.println("vertices:");
		// numberTransitions = 0;
		// numberPlaces = 0;
		Place p;
		Transition t;
		while (hsit.hasNext()) {
			bna = hsit.next();
			// System.out.println(bna.getVertex());
			// System.out.println(bna.getClass());
			if (bna instanceof Transition) {
				t = (Transition) bna;
				this.hmtransitions.put(bna, this.numberTransitions);
				this.idToNameTransition.put(this.numberTransitions, t.getName());
				// System.out.println("name: "+t.getName());
				this.numberTransitions++;
			} else {
				p = (Place) bna;
				this.idToName.put(this.numberPlaces, p.getName());
				this.name2id.put(p.getName(), this.numberPlaces);
				this.idToMax.put(this.numberPlaces, p.getTokenMax());
				// System.out.println("id: " + this.numberPlaces + " tokenMax: "
				// + p.getTokenMax());
				// System.out.println("id: " + this.numberPlaces + " name: "
				// + p.getName());
				this.hmplaces.put(bna, this.numberPlaces);
				this.start.add(p.getTokenStart());
				this.numberPlaces++;
			}
		}

		// System.out.println("places: " + this.numberPlaces + " trans: "
		// + this.numberTransitions);
		double[][] f = this.initArray(this.numberPlaces, this.numberTransitions);
		double[][] b = this.initArray(this.numberPlaces, this.numberTransitions);
		// double[][] c = this.initArray(numberPlace, numberTransition);

		// einkommende Kanten (backward matrix)
		Iterator<BiologicalEdgeAbstract> edgeit = graphInstance.getPathway().getAllEdges().iterator();
		PNArc edge;
		// Pair pair;
		while (edgeit.hasNext()) {
			edge = (PNArc) edgeit.next();
			// System.out.println("first: "+edge.getLabel());
			// System.out.println("first: "+edge.getEdge().getEndpoints().getFirst());
			// System.out.println("simid: "+edge.getInternalSimulationID());
			// System.out.println("second: "+edge.getEdge().getEndpoints().getSecond());
			// pair = edge.getEdge().getEndpoints();
			// System.out.println(pair.toString());

			// T->P
			if (this.hmplaces.containsKey(edge.getTo())) {
				int i = hmplaces.get(edge.getTo());
				int j = hmtransitions.get(edge.getFrom());
				// double eintrag = b[i][j];
				// eintrag+=
				// System.out.println(edge.getPassingTokens());
				b[i][j] += edge.getPassingTokens();
			}
			// P->T
			else {
				// System.out.println(pair.getFirst().toString());
				int i = hmplaces.get(edge.getFrom());
				int j = hmtransitions.get(edge.getTo());
				// double eintrag = f[i][j];
				// eintrag
				f[i][j] -= edge.getPassingTokens();
			}
			// SparseVertex o = (SparseVertex) pair.getFirst();

			// System.out.println(o.get);
			// System.out.println();
		}
		this.bMatrix = new SimpleMatrixDouble(b);
		this.fMatrix = new SimpleMatrixDouble(f);
		this.cMatrix = new SimpleMatrixDouble(this.initArray(this.numberPlaces, this.numberTransitions));
		this.cMatrix.add(this.bMatrix);
		this.cMatrix.add(this.fMatrix);

		// System.out.println("------debug output-----------");
		// System.out.println("Transitionen: " + numberTransitions);
		// System.out.println(hmtransitions);
		// System.out.println("Places: " + numberPlaces);
		// System.out.println(hmplaces);
		// System.out.println("backwardmatrix:");
		// System.out.println(bMatrix);
		// System.out.println("forwardmatrix:");
		// System.out.println(fMatrix);
		// System.out.println("cmatrix:");
		// System.out.println(cMatrix);

	}

	public HashMap<String, Integer> getName2id() {
		return name2id;
	}

	private double[][] initArray(int m, int n) {
		double[][] array = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				array[i][j] = 0;
			}
		}
		return array;
	}

	private void getParents(CovNode node) {

		if (!this.containsParent(node)) {
			this.parents.add(node);
		}
		// System.out.println("Vertex: " + node.getVertex().toString());

		Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph().getInEdges(node).iterator();
		CovEdge e;
		CovNode n;
		// System.out.println("setsize: " + set.size());
		while (it.hasNext()) {
			e = (CovEdge) it.next();

//			n = this.covnodes.get(e.getEndpoints().getFirst().toString());
			n = (CovNode) e.getFrom();
			// System.out.println("parent: "
			// + e.getEndpoints().getFirst().toString() + " "
			// + n.getLabel());

			if (!this.containsParent(n)) {
				// System.out.println("enthaelt nicht: "
				// + n.getTokenList().toString());
			} else {
				// System.out.println("enthaelt schon: "
				// + n.getTokenList().toString());
			}
			if (!this.containsParent(n)) {
				this.parents.add(n);
				this.getParents(n);
			}
			// System.out.println(this.parents);

		}
	}

	private boolean containsParent(CovNode n) {
		CovNode tmp;
		for (int i = 0; i < this.parents.size(); i++) {
			tmp = this.parents.get(i);
			if (tmp.equals(n))
				return true;
		}

		return false;
	}

	private void paintCoveredNodes() {

		Pathway pwold = this.graphInstance.getContainer().getPathway(this.oldName);
		// System.out.println("oldname: " + pwold.getName());
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		// this.graphInstance.getContainer().
		double[] tokens = null;
		BiologicalNodeAbstract bna = null;

		// Iterator it2 = oldNodes.iterator();
		/*
		 * while(it2.hasNext()){ bna = (BiologicalNodeAbstract) it2.next();
		 * bna.setColor(new Color(255,0,0));
		 * System.out.println("old label: "+bna.getLabel()); }
		 */
		Iterator<BiologicalNodeAbstract> it2;
		while (it.hasNext()) {
			CovNode n = (CovNode) it.next();
			// n.setColor(new Color(255,0,0));
			CovList l = n.getTokenList();
			tokens = l.getElements();

			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i] == -1.0) {
					it2 = pwold.getAllGraphNodes().iterator();
					while (it2.hasNext()) {
						bna = it2.next();
						if (bna.getName().equals(this.idToName.get(i))) {
							bna.setColor(new Color(255, 0, 0));

							// System.out.println("name: "+bna.getLabel());
						}
					}

				}
			}

		}
		pwold.getGraph().restartVisualizationModel();
	}

	private boolean isBoundaryHold(CovList list) {

		// System.out.println("listsize: "+list.getSize());
		for (int i = 0; i < list.getSize(); i++) {
			if (list.getElementAt(i) > this.idToMax.get(i) && this.idToMax.get(i) > 0) {
				// System.out.println("id: "+i);
				// System.out.println("neu: "+list.getElementAt(i)+" alt:
				// "+this.idToMax.get(i));
				return false;
			}
		}
		return true;
	}

	public String getNewName() {
		return newName;
	}

}