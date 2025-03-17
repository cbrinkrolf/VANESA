package petriNet;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final Pathway sourcePathway;
	private final Pathway targetPathway;
	private final HashMap<BiologicalNodeAbstract, Integer> hmPlaces = new HashMap<>();
	private final HashMap<BiologicalNodeAbstract, Integer> hmTransitions = new HashMap<>();
	private SimpleMatrixDouble fMatrix;
	private SimpleMatrixDouble cMatrix;
	private int numberPlaces = 0;
	private int numberTransitions = 0;
	private final ArrayList<Double> start = new ArrayList<>();
	private final ArrayList<CovNode> parents = new ArrayList<>();
	private final CovNode root;
	private final Map<Integer, String> idToName = new HashMap<>();
	private final Map<Integer, String> idToNameTransition = new HashMap<>();
	private final Map<Integer, Double> idToMax = new HashMap<>();
	private final Map<String, Integer> name2id = new HashMap<>();

	public Cov() {
		sourcePathway = GraphInstance.getPathway();
		createMatrices();
		targetPathway = CreatePathway.create();
		root = new CovNode("label", "name", numberPlaces);
		double[] tmp = new double[start.size()];
		for (int i = 0; i < start.size(); i++) {
			tmp[i] = this.start.get(i);
		}
		final CovList list = new CovList(numberPlaces);
		list.setElements(tmp);
		root.setTokenList(list);
		targetPathway.addVertex(root, new Point(10, 30));
		root.setColor(new Color(255, 0, 0));
		computeNode(root);
		paintCoveredNodes();
		targetPathway.updateMyGraph();
		sourcePathway.getPetriPropertiesNet().setCovGraph(targetPathway.getName());
	}

	public CovNode getRoot() {
		return root;
	}

	private void computeNode(final CovNode parent) {
		final CovList cl = parent.getTokenList();
		for (int i = 0; i < numberTransitions; i++) {
			final double[] col = fMatrix.getColumn(i);
			// Wenn Markierung groesser gleich Transition ist
			if (cl.isGreaterEqualCol(col)) {
				final CovList tmp = parent.getTokenList().clone();
				tmp.addTokens(cMatrix.getColumn(i));
				boolean found = false;
				if (isBoundaryHold(tmp)) {
					// fuer alle Knoten: Wenn neue Markierung schon vorhanden ist
					for (final BiologicalNodeAbstract bna : targetPathway.getAllGraphNodes()) {
						final CovNode cn = (CovNode) bna;
						if (cn.getTokenList().isEqual(tmp.getElements()) && !cn.getTokenList().equals(
								parent.getTokenList())) {
							CovEdge e = new CovEdge(idToNameTransition.get(i), idToNameTransition.get(i), parent, cn);
							e.setDirected(true);
							targetPathway.addEdge(e);
							found = true;
							break;
						}
					}

					if (found) {
						continue;
					}
					// Knoten nicht direkt gefunden
					parents.clear();
					getParents(parent);
					for (CovNode cn : parents) {
						if (!tmp.isGreater(cn.getTokenList().getElements())) {
							continue;
						}
						// falls neue Markierung eine schon vorhandene Markierung ueberdeckt
						final List<Integer> indices = tmp.getGreaterIndices(cn.getTokenList());
						for (Integer index : indices) {
							if (idToMax.get(index) <= 0) {
								tmp.setElementAt(index, -1.0);
							}
						}
						for (final BiologicalNodeAbstract biologicalNodeAbstract : targetPathway.getAllGraphNodes()) {
							cn = (CovNode) biologicalNodeAbstract;
							boolean cond1 = cn.getTokenList().isEqual(tmp.getElements());
							boolean cond2 = cn.getTokenList().toString().equals(parent.getTokenList().toString());
							// Wenn neue Markierung schon vorhanden ist
							if (cond1 && !cond2) {
								final String edgeName = idToNameTransition.get(i);
								final CovEdge e = new CovEdge(edgeName, edgeName, parent, cn);
								e.setDirected(true);
								targetPathway.addEdge(e);
								found = true;
								break;
							}
							if (cond1 && cond2) {
								found = true;
								break;
							}
						}
						if (!found) {
							final CovNode n = new CovNode("label2", "name2", numberPlaces);
							n.setTokenList(tmp.clone());
							targetPathway.addVertex(n, new Point(i * 10, i * i * 30));
							final String edgeName = idToNameTransition.get(i);
							final CovEdge e = new CovEdge(edgeName, edgeName, parent, n);
							e.setDirected(true);
							targetPathway.addEdge(e);
							computeNode(n);
							found = true;
							break;
						}
					}

					// wenn Markierung noch nicht im Graph
					if (!found) {
						CovNode n = new CovNode("label2", "name2", numberPlaces);
						n.setTokenList(tmp);
						if (!parent.getTokenList().toString().equals(tmp.toString())) {
							targetPathway.addVertex(n, new Point(i * 10, i * i * 30));
							final String edgeName = idToNameTransition.get(i);
							final CovEdge e = new CovEdge(edgeName, edgeName, parent, n);
							e.setDirected(true);
							targetPathway.addEdge(e);
							computeNode(n);
						}
					}
				}
			}
		}
	}

	private void createMatrices() {
		for (final BiologicalNodeAbstract bna : sourcePathway.getAllGraphNodes()) {
			if (bna instanceof Transition) {
				final Transition t = (Transition) bna;
				hmTransitions.put(bna, numberTransitions);
				idToNameTransition.put(numberTransitions, t.getName());
				numberTransitions++;
			} else {
				final Place p = (Place) bna;
				idToName.put(numberPlaces, p.getName());
				name2id.put(p.getName(), numberPlaces);
				idToMax.put(numberPlaces, p.getTokenMax());
				hmPlaces.put(bna, numberPlaces);
				start.add(p.getTokenStart());
				numberPlaces++;
			}
		}
		final double[][] f = VanesaUtility.createMatrix(numberPlaces, numberTransitions);
		final double[][] b = VanesaUtility.createMatrix(numberPlaces, numberTransitions);
		// incoming edges (backward matrix)
		for (final BiologicalEdgeAbstract biologicalEdgeAbstract : sourcePathway.getAllEdges()) {
			final PNArc edge = (PNArc) biologicalEdgeAbstract;
			if (this.hmPlaces.containsKey(edge.getTo())) {
				// T->P
				int i = hmPlaces.get(edge.getTo());
				int j = hmTransitions.get(edge.getFrom());
				b[i][j] += edge.getPassingTokens();
			} else {
				// P->T
				int i = hmPlaces.get(edge.getFrom());
				int j = hmTransitions.get(edge.getTo());
				f[i][j] -= edge.getPassingTokens();
			}
		}
		final SimpleMatrixDouble bMatrix = new SimpleMatrixDouble(b);
		fMatrix = new SimpleMatrixDouble(f);
		cMatrix = new SimpleMatrixDouble(VanesaUtility.createMatrix(numberPlaces, numberTransitions));
		cMatrix.add(bMatrix);
		cMatrix.add(fMatrix);
	}

	public Map<String, Integer> getName2id() {
		return name2id;
	}

	private void getParents(CovNode node) {
		if (!parents.contains(node)) {
			parents.add(node);
		}
		for (final BiologicalEdgeAbstract biologicalEdgeAbstract : targetPathway.getGraph2().getInEdges(node)) {
			final CovEdge e = (CovEdge) biologicalEdgeAbstract;
			final CovNode n = (CovNode) e.getFrom();
			if (!parents.contains(n)) {
				parents.add(n);
				getParents(n);
			}
		}
	}

	private void paintCoveredNodes() {
		for (BiologicalNodeAbstract biologicalNodeAbstract : targetPathway.getAllGraphNodes()) {
			CovNode n = (CovNode) biologicalNodeAbstract;
			CovList l = n.getTokenList();
			double[] tokens = l.getElements();
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i] == -1.0) {
					for (final BiologicalNodeAbstract bna : sourcePathway.getAllGraphNodes()) {
						if (bna.getName().equals(idToName.get(i))) {
							bna.setColor(new Color(255, 0, 0));
						}
					}
				}
			}
		}
		sourcePathway.updateMyGraph();
	}

	private boolean isBoundaryHold(CovList list) {
		for (int i = 0; i < list.getSize(); i++) {
			if (list.getElementAt(i) > idToMax.get(i) && idToMax.get(i) > 0) {
				return false;
			}
		}
		return true;
	}

	public Pathway getPathway() {
		return targetPathway;
	}
}