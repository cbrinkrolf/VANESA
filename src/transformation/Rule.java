package transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rule {

	private String ruleName = "";

	private List<RuleNode> bNodes = new ArrayList<>();
	private List<RuleEdge> bEdges = new ArrayList<>();

	private List<RuleNode> pNodes = new ArrayList<>();
	private List<RuleEdge> pEdges = new ArrayList<>();

	private HashMap<RuleNode, RuleNode> bn2Pn = new HashMap<RuleNode, RuleNode>();
	private HashMap<RuleNode, RuleNode> pn2Bn = new HashMap<RuleNode, RuleNode>();

	public Rule() {

	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public void addBiologicalEdge(RuleEdge e) {
		for (int i = 0; i < bEdges.size(); i++) {
			if (bEdges.get(i).getName().equals(e.getName())) {
				System.out.println("Edge exists already");
				return;
			}
		}
		if (this.getBiologicaNode(e.getFrom().getName()) == null) {
			this.addBiologicalNode(e.getFrom());

			System.out.println("From node did not get added, yet");
		}
		if (this.getBiologicaNode(e.getTo().getName()) == null) {
			this.addBiologicalNode(e.getTo());

			System.out.println("To node did not get added, yet");
		}
		bEdges.add(e);
	}

	public void addBiologicalNode(RuleNode n) {
		for (int i = 0; i < bNodes.size(); i++) {
			if (n.getName().equals(bNodes.get(i).getName())) {
				System.out.println("Node exists already");
				return;
			}
		}
		bNodes.add(n);
	}

	public RuleNode getBiologicaNode(String name) {
		RuleNode n = null;
		for (int i = 0; i < bNodes.size(); i++) {
			if (name.equals(bNodes.get(i).getName())) {
				return bNodes.get(i);
			}
		}
		return n;
	}

	public RuleEdge getBiologicalEdge(String name) {
		RuleEdge e = null;
		for (int i = 0; i < bEdges.size(); i++) {
			if (name.equals(bEdges.get(i).getName())) {
				return bEdges.get(i);
			}
		}
		return e;
	}

	public List<RuleNode> getAllBiologicalNodes() {
		return this.bNodes;
	}

	public List<RuleEdge> getAllBiologicalEdges() {
		return this.bEdges;
	}

	public void addPetriEdge(RuleEdge e) {
		for (int i = 0; i < pEdges.size(); i++) {
			if (pEdges.get(i).getName().equals(e.getName())) {
				System.out.println("Edge exists already");
				return;
			}
		}
		if (this.getPetriNode(e.getFrom().getName()) == null) {
			this.addPetriNode(e.getFrom());

			System.out.println("From node did not get added, yet");
		}
		if (this.getPetriNode(e.getTo().getName()) == null) {
			this.addPetriNode(e.getTo());

			System.out.println("To node did not get added, yet");
		}
		pEdges.add(e);
	}

	public void addPetriNode(RuleNode n) {
		for (int i = 0; i < pNodes.size(); i++) {
			if (n.getName().equals(pNodes.get(i).getName())) {
				System.out.println("Node exists already");
				return;
			}
		}
		pNodes.add(n);
	}

	public RuleNode getPetriNode(String name) {
		RuleNode n = null;
		for (int i = 0; i < pNodes.size(); i++) {
			if (name.equals(pNodes.get(i).getName())) {
				return pNodes.get(i);
			}
		}
		return n;
	}

	public RuleEdge getPetriEdge(String name) {
		RuleEdge e = null;
		for (int i = 0; i < pEdges.size(); i++) {
			if (name.equals(pEdges.get(i).getName())) {
				return pEdges.get(i);
			}
		}
		return e;
	}

	public List<RuleEdge> getAllPetriEdges() {
		return this.pEdges;
	}

	public List<RuleNode> getAllPetriNodes() {
		return this.pNodes;
	}

	public void addBNtoPNMapping(RuleNode bNode, RuleNode pNode) {
		this.bn2Pn.put(bNode, pNode);
		this.pn2Bn.put(pNode, bNode);
	}

	public RuleNode getMappedBnode(RuleNode pNode) {
		if (pn2Bn.containsKey(pNode)) {
			return pn2Bn.get(pNode);
		}
		return null;
	}

	public RuleNode getMappedPnode(RuleNode bNode) {
		if (bn2Pn.containsKey(bNode)) {
			return bn2Pn.get(bNode);
		}
		return null;
	}

	public boolean isConsistent() {
		RuleNode node;
		for (int i = 0; i < this.getAllPetriNodes().size(); i++) {
			node = this.getAllPetriNodes().get(i);
			// check if PN node type is matching
			if (!(Transformator.places.contains(node.getType())
					|| Transformator.transitions.contains(node.getType()))) {
				System.out.println("Error in rule: " + this.ruleName);
				System.out.println("Petri net node " + node.getName()
						+ " does not match Petri net node type. Given node type: " + node.getType());
				return false;
			}
		}

		RuleEdge e;
		RuleNode from;
		RuleNode to;
		for (int i = 0; i < this.getAllPetriEdges().size(); i++) {
			e = this.getAllPetriEdges().get(i);
			from = e.getFrom();
			to = e.getTo();
			if (Transformator.places.contains(from.getType()) && Transformator.places.contains(to.getType())) {
				System.out.println("Error in rule: " + this.ruleName);
				System.out.println("Nodes of arc " + e.getName() + " have the same type (both are places)!");
				return false;
			}
			if (Transformator.transitions.contains(from.getType())
					&& Transformator.transitions.contains(to.getType())) {
				System.out.println("Error in rule: " + this.ruleName);
				System.out.println("Nodes of arc " + e.getName() + " have the same type (both are transitions)!");
				return false;
			}
			if (e.getType().equals(Transformator.pnInhibitoryArc) || e.getType().equals(Transformator.pnTestArc)) {
				if (Transformator.transitions.contains(from.getType())) {
					System.out.println("Error in rule: " + this.ruleName);
					System.out.println(
							"Inhibitory and test arcs always have to connect a place with a transtionen (not vice versa)!");
					return false;
				}
			}
		}

		return true;
	}

}
