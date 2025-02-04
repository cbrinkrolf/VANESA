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
import graph.CreatePathway;
import graph.GraphInstance;
import util.VanesaUtility;

public class Cov {
	private final Pathway pw;
	private final HashMap<BiologicalNodeAbstract, Integer> hmPlaces = new HashMap<>();
	private final HashMap<BiologicalNodeAbstract, Integer> hmTransitions = new HashMap<>();
	private SimpleMatrixDouble bMatrix;
	private SimpleMatrixDouble fMatrix;
	private SimpleMatrixDouble cMatrix;
	private int numberPlaces = 0;
	private int numberTransitions = 0;
	private final ArrayList<Double> start = new ArrayList<>();

	// pw.getGraph().moveVertex(root.getVertex(), 10, 30);
	// this.covnodes.put(root.getVertex().toString(), root);
	// System.out.println(this.covnodes);
	private final ArrayList<CovNode> parents = new ArrayList<>();
	private String oldName;
	private final CovNode root;

	public String getOldName() {
		return oldName;
	}

	private final String newName;

	// private boolean found = false;
	private final HashMap<Integer, String> idToName = new HashMap<>();
	private final HashMap<Integer, String> idToNameTransition = new HashMap<>();
	private final HashMap<Integer, Double> idToMax = new HashMap<>();
	// private final HashMap<String, CovNode> covnodes = new HashMap<>();
	private final HashMap<String, Integer> name2id = new HashMap<>();

	public Cov() {
		// Iterator it6 = this.graphInstance.getContainer().getAllPathways().iterator();
		createMatrices();
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
		paintCoveredNodes();
		pw.getGraph().restartVisualizationModel();
		pw.getPetriPropertiesNet().setCovGraph(pw.getName());
	}

	public CovNode getRoot() {
		return root;
	}

	private void computeNode(CovNode parent) {
		CovList cl = parent.getTokenList();
		// boolean found;
		// System.out.println("Root: " + cl);
		// fuer alle Transitionen
		for (int i = 0; i < numberTransitions; i++) {
			double[] col = this.fMatrix.getColumn(i);

			// System.out.println("Transition: " + i);
			// System.out.println("col: "+col.toString());

			// Wenn Markierung groesser gleich Transition ist
			if (cl.isGreaterEqualCol(col)) {
				CovList tmp = parent.getTokenList().clone();
				tmp.addTokens(this.cMatrix.getColumn(i));
				// System.out.println("new: " + tmp);
				boolean found = false;
				if (this.isBoundaryHold(tmp)) {
					// fuer alle Knoten: Wenn neue Markierung schon vorhanden ist
					for (BiologicalNodeAbstract bna : GraphInstance.getPathway().getAllGraphNodes()) {
						CovNode cn = (CovNode) bna;
						if (cn.getTokenList().isEqual(tmp.getElements()) && !cn.getTokenList().equals(
								parent.getTokenList())) {
							CovEdge e = new CovEdge(this.idToNameTransition.get(i), this.idToNameTransition.get(i),
													parent, cn);
							e.setDirected(true);
							pw.addEdge(e);
							// e.setVisible(false);
							// System.out.println("Kante1 von: " + parent.getTokenList() + "->" + cn.getTokenList());
							found = true;
							break;
						}
					}

					// Knoten nicht direkt gefunden
					if (!found) {
						this.parents.clear();
						// System.out.println("size parents vorher: " + this.parents.size());
						this.getParents(parent);
						// System.out.println("size parents nachher: " + this.parents.size());
						for (int j = 0; j < this.parents.size(); j++) {
							CovNode cn = this.parents.get(j);
							// falls neue Markierung eine schon vorhandene Markierung ueberdeckt
							if (tmp.isGreater(cn.getTokenList().getElements())) {
								// System.out.println(tmp + " is greater as " + cn.getTokenList());
								ArrayList<Integer> indices = tmp.getGreaterIndices(cn.getTokenList());
								for (Integer index : indices) {
									if (idToMax.get(index) <= 0) {
										tmp.setElementAt(index, -1.0);
									}
								}

								// fuer alle Knoten
								Iterator<BiologicalNodeAbstract> it2 = GraphInstance.getPathway().getAllGraphNodes()
																					.iterator();
								while (it2.hasNext()) {
									cn = (CovNode) it2.next();
									boolean cond1 = cn.getTokenList().isEqual(tmp.getElements());
									boolean cond2 = cn.getTokenList().toString().equals(
											parent.getTokenList().toString());
									// System.out.println(cond1 + " " + cond2);
									// System.out.println("cn: " + cn.getTokenList());
									// System.out.println("tmp: " + tmp.getElements());
									// System.out.println("!cn: " + cn.getTokenList().toString());
									// System.out.println();
									// Wenn neue Markierung schon vorhanden ist
									if (cond1 && !cond2) {
										CovEdge e = new CovEdge(this.idToNameTransition.get(i),
																this.idToNameTransition.get(i), parent, cn);
										e.setDirected(true);
										pw.addEdge(e);
										// e.setVisible(false);
										// System.out.println("Kante2 von: " + parent.getTokenList() + "->" + cn.getTokenList());
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
									CovEdge e = new CovEdge(idToNameTransition.get(i), idToNameTransition.get(i),
															parent, n);
									e.setDirected(true);

									// pw.getGraph().moveVertex(n.getVertex(), i * 10, i * i * 30);
									// this.covnodes.put(n.getVertex().toString(), n);
									pw.addEdge(e);
									// e.setVisible(false);

									//									System.out.println("neuer ueberdeckter node: " + n.getTokenList());
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
							// System.out.println("1: " + parent.getTokenList());
							// System.out.println("2: " + tmp.toString());
							if (!parent.getTokenList().toString().equals(tmp.toString())) {
								pw.addVertex(n, new Point(i * 10, i * i * 30));
								// n.setVisible(false);
								CovEdge e = new CovEdge(idToNameTransition.get(i), idToNameTransition.get(i), parent,
														n);
								e.setDirected(true);
								// this.covnodes.put(n.getVertex().toString(), n);
								pw.addEdge(e);
								// e.setVisible(false);
								// System.out.println("Kante3 von: " + parent.getTokenList() + "->" + n.getTokenList());
								// pw.getGraph().moveVertex(n.getVertex(), i * 10, i * i * 30);
								// System.out.println("neuer node, nicht gef. " + n.getTokenList());
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
		this.oldName = GraphInstance.getPathway().getName();
		// Hashmaps fuer places und transitions
		Iterator<BiologicalNodeAbstract> hsit = GraphInstance.getPathway().getAllGraphNodes().iterator();
		// System.out.println("vertices:");
		// numberTransitions = 0;
		// numberPlaces = 0;
		while (hsit.hasNext()) {
			BiologicalNodeAbstract bna = hsit.next();
			// System.out.println(bna.getVertex());
			// System.out.println(bna.getClass());
			if (bna instanceof Transition) {
				Transition t = (Transition) bna;
				hmTransitions.put(bna, numberTransitions);
				idToNameTransition.put(numberTransitions, t.getName());
				// System.out.println("name: "+t.getName());
				numberTransitions++;
			} else {
				Place p = (Place) bna;
				idToName.put(numberPlaces, p.getName());
				name2id.put(p.getName(), numberPlaces);
				idToMax.put(numberPlaces, p.getTokenMax());
				// System.out.println("id: " + numberPlaces + " tokenMax: " + p.getTokenMax());
				// System.out.println("id: " + numberPlaces + " name: " + p.getName());
				hmPlaces.put(bna, numberPlaces);
				start.add(p.getTokenStart());
				numberPlaces++;
			}
		}

		double[][] f = VanesaUtility.createMatrix(numberPlaces, numberTransitions);
		double[][] b = VanesaUtility.createMatrix(numberPlaces, numberTransitions);
		// double[][] c = this.initArray(numberPlace, numberTransition);

		// einkommende Kanten (backward matrix)
		Iterator<BiologicalEdgeAbstract> edgeit = GraphInstance.getPathway().getAllEdges().iterator();
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
			if (this.hmPlaces.containsKey(edge.getTo())) {
				int i = hmPlaces.get(edge.getTo());
				int j = hmTransitions.get(edge.getFrom());
				// double eintrag = b[i][j];
				// eintrag+=
				// System.out.println(edge.getPassingTokens());
				b[i][j] += edge.getPassingTokens();
			}
			// P->T
			else {
				// System.out.println(pair.getFirst().toString());
				int i = hmPlaces.get(edge.getFrom());
				int j = hmTransitions.get(edge.getTo());
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
		this.cMatrix = new SimpleMatrixDouble(VanesaUtility.createMatrix(this.numberPlaces, this.numberTransitions));
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

	private void getParents(CovNode node) {
		if (!this.containsParent(node)) {
			this.parents.add(node);
		}
		// System.out.println("Vertex: " + node.getVertex().toString());
		Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph().getInEdges(node).iterator();
		// System.out.println("setsize: " + set.size());
		while (it.hasNext()) {
			CovEdge e = (CovEdge) it.next();
			//			n = this.covnodes.get(e.getEndpoints().getFirst().toString());
			CovNode n = (CovNode) e.getFrom();
			// System.out.println("parent: " + e.getEndpoints().getFirst().toString() + " " + n.getLabel());
			if (!this.containsParent(n)) {
				// System.out.println("enthaelt nicht: " + n.getTokenList().toString());
			} else {
				// System.out.println("enthaelt schon: " + n.getTokenList().toString());
			}
			if (!this.containsParent(n)) {
				this.parents.add(n);
				this.getParents(n);
			}
			// System.out.println(this.parents);
		}
	}

	private boolean containsParent(CovNode n) {
		for (CovNode tmp : this.parents) {
			if (tmp.equals(n)) {
				return true;
			}
		}
		return false;
	}

	private void paintCoveredNodes() {
		Pathway pwold = GraphInstance.getContainer().getPathway(this.oldName);
		// System.out.println("oldname: " + pwold.getName());
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		// this.graphInstance.getContainer().
		// Iterator it2 = oldNodes.iterator();
		/*
		 * while(it2.hasNext()){ bna = (BiologicalNodeAbstract) it2.next();
		 * bna.setColor(new Color(255,0,0));
		 * System.out.println("old label: "+bna.getLabel()); }
		 */
		while (it.hasNext()) {
			CovNode n = (CovNode) it.next();
			// n.setColor(new Color(255,0,0));
			CovList l = n.getTokenList();
			double[] tokens = l.getElements();
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i] == -1.0) {
					for (final BiologicalNodeAbstract bna : pwold.getAllGraphNodes()) {
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
				// System.out.println("neu: "+list.getElementAt(i)+" alt: "+this.idToMax.get(i));
				return false;
			}
		}
		return true;
	}

	public String getNewName() {
		return newName;
	}
}