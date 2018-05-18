package transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import gui.visualization.VisualizationConfigBeans.Bean;

public class Transformator {

	private Pathway pw;

	private List<Rule> rules = new ArrayList<Rule>();

	private List<BiologicalNodeAbstract> nodeList = new ArrayList<BiologicalNodeAbstract>();
	private List<BiologicalEdgeAbstract> edgeList = new ArrayList<BiologicalEdgeAbstract>();

	private HashMap<Integer, BiologicalNodeAbstract> id2bna = new HashMap<>();

	private HashMap<String, ArrayList<BiologicalNodeAbstract>> nodeType2bna = new HashMap<String, ArrayList<BiologicalNodeAbstract>>();
	private HashMap<String, ArrayList<BiologicalEdgeAbstract>> edgeType2bea = new HashMap<String, ArrayList<BiologicalEdgeAbstract>>();

	public void transform(Pathway pw) {
		this.pw = pw;

		BiologicalNodeAbstract bna;
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			id2bna.put(bna.getID(), bna);
		}
		createRules();
		applyRules();
	}

	private void applyRules() {
		for (int i = 0; i < rules.size(); i++) {
			this.applyRule(rules.get(i));
		}
	}

	private void applyRule(Rule r) {

		List<String> nodeNames = r.getNodeNames();

		this.createBuckets();

		ArrayList<Collection<Integer>> list = new ArrayList<Collection<Integer>>();
		ArrayList<Integer> l;
		BiologicalNodeAbstract bna;
		for (int i = 0; i < r.getNodeTypes().size(); i++) {

			String type = r.getNodeTypes().get(i);

			Iterator<BiologicalNodeAbstract> it = nodeType2bna.get(type).iterator();
			l = new ArrayList<Integer>();

			while (it.hasNext()) {
				bna = it.next();
				l.add(bna.getID());
			}
			list.add(l);
		}

		Collection<List<Integer>> permutations = Permutator.permutations(list);

		// test each permutation
		Iterator<List<Integer>> itList = permutations.iterator();
		List<Integer> perm;

		BiologicalEdgeAbstract bea;
		while (itList.hasNext()) {
			perm = itList.next();

			for (int i = 0; i < r.getEdgeFrom().size(); i++) {
				Iterator<BiologicalEdgeAbstract> it = pw.getAllEdges().iterator();
				while (it.hasNext()) {
					bea = it.next();
					int id1 = perm.get(r.getNodeNames().indexOf(r.getEdgeFrom().get(i)));
					
					int id2 = perm.get(r.getNodeNames().indexOf(r.getEdgeTo().get(i)));
					BiologicalNodeAbstract n1 = id2bna.get(id1);
					BiologicalNodeAbstract n2 = id2bna.get(id2);
					if(bea.getFrom() == n1 && bea.getTo() == n2){
						System.out.println(r.getNodeNames().indexOf(r.getEdgeFrom().get(i)));
						
						System.out.println(n1.getLabel() + "->" + n2.getLabel());
						System.out.println(perm);
					}

				}

			}

		}

		// System.out.println(permutations.size());
	}

	private void createRules() {

		Rule r = new Rule();
		/*
		 * N1: SmallMolecule N2: Enzyme N3: SmallMolecule E1: BEA N1 -> N2 E2:
		 * BEA N2 -> N3
		 * 
		 * 
		 */
		r.getNodeNames().add("N1");
		r.getNodeNames().add("N2");
		r.getNodeNames().add("N3");

		r.getNodeTypes().add("SmallMolecule");
		r.getNodeTypes().add("Enzyme");
		r.getNodeTypes().add("SmallMolecule");

		r.getEdgeFrom().add("N1");
		r.getEdgeFrom().add("N2");

		r.getEdgeTo().add("N2");
		r.getEdgeTo().add("N3");

		r.getEdgeTypes().add("BiologicalEdgeAbstract");
		r.getEdgeTypes().add("BiologicalEdgeAbstract");

		rules.add(r);

	}

	private void createBuckets() {
		nodeType2bna.clear();
		edgeType2bea.clear();

		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		String name;
		Class c;
		// all nodes
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		while (it.hasNext()) {
			bna = it.next();
			c = bna.getClass();
			name = bna.getClass().getSimpleName();

			while (!name.equals("Object")) {
				// System.out.println(name);

				if (!nodeType2bna.containsKey(name)) {
					nodeType2bna.put(name, new ArrayList<BiologicalNodeAbstract>());
				}
				nodeType2bna.get(name).add(bna);

				c = c.getSuperclass();
				name = c.getSimpleName();
			}
		}
		// all edges
		Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdges().iterator();
		while (it2.hasNext()) {
			bea = it2.next();
			c = bea.getClass();
			name = bea.getClass().getSimpleName();

			while (!name.equals("Object")) {
				// System.out.println(name);

				if (!edgeType2bea.containsKey(name)) {
					edgeType2bea.put(name, new ArrayList<BiologicalEdgeAbstract>());
				}
				edgeType2bea.get(name).add(bea);

				c = c.getSuperclass();
				name = c.getSimpleName();
			}
		}
	}

}
