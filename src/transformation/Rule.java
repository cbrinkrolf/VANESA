package transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rule {

	private String name = "";
	private boolean active = true;

	private List<RuleNode> biologicalNodes = new ArrayList<RuleNode>();
	private List<RuleEdge> biologicalEdges = new ArrayList<RuleEdge>();

	private List<RuleNode> petriNodes = new ArrayList<RuleNode>();
	private List<RuleEdge> petriEdges = new ArrayList<RuleEdge>();

	private HashMap<RuleNode, RuleNode> bnToPnMapping = new HashMap<RuleNode, RuleNode>();
	private HashMap<RuleNode, RuleNode> pnToBnMapping = new HashMap<RuleNode, RuleNode>();

	@Getter
	@Setter
	private List<RuleEdge> consideredEdges = new ArrayList<RuleEdge>();

	public void addBiologicalEdge(RuleEdge e) {
		for (int i = 0; i < biologicalEdges.size(); i++) {
			if (biologicalEdges.get(i).getName().equals(e.getName())) {
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
		biologicalEdges.add(e);
	}

	public void addBiologicalNode(RuleNode n) {
		for (int i = 0; i < biologicalNodes.size(); i++) {
			if (n.getName().equals(biologicalNodes.get(i).getName())) {
				System.out.println("Node exists already");
				return;
			}
		}
		biologicalNodes.add(n);
	}

	public RuleNode getBiologicaNode(String name) {
		RuleNode n = null;
		for (int i = 0; i < biologicalNodes.size(); i++) {
			if (name.equals(biologicalNodes.get(i).getName())) {
				return biologicalNodes.get(i);
			}
		}
		return n;
	}

	public RuleEdge getBiologicalEdge(String name) {
		RuleEdge e = null;
		for (int i = 0; i < biologicalEdges.size(); i++) {
			if (name.equals(biologicalEdges.get(i).getName())) {
				return biologicalEdges.get(i);
			}
		}
		return e;
	}

	public void addPetriEdge(RuleEdge e) {
		for (int i = 0; i < petriEdges.size(); i++) {
			if (petriEdges.get(i).getName().equals(e.getName())) {
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
		petriEdges.add(e);
	}

	public void addPetriNode(RuleNode n) {
		for (int i = 0; i < petriNodes.size(); i++) {
			if (n.getName().equals(petriNodes.get(i).getName())) {
				System.out.println("Node exists already");
				return;
			}
		}
		petriNodes.add(n);
	}

	public RuleNode getPetriNode(String name) {
		RuleNode n = null;
		for (int i = 0; i < petriNodes.size(); i++) {
			if (name.equals(petriNodes.get(i).getName())) {
				return petriNodes.get(i);
			}
		}
		return n;
	}

	public RuleEdge getPetriEdge(String name) {
		RuleEdge e = null;
		for (int i = 0; i < petriEdges.size(); i++) {
			if (name.equals(petriEdges.get(i).getName())) {
				return petriEdges.get(i);
			}
		}
		return e;
	}

	public void addBNtoPNMapping(RuleNode bNode, RuleNode pNode) {
		this.bnToPnMapping.put(bNode, pNode);
		this.pnToBnMapping.put(pNode, bNode);
	}

	public RuleNode getMappedBnode(RuleNode pNode) {
		if (pnToBnMapping.containsKey(pNode)) {
			return pnToBnMapping.get(pNode);
		}
		return null;
	}

	public RuleNode getMappedPnode(RuleNode bNode) {
		if (bnToPnMapping.containsKey(bNode)) {
			return bnToPnMapping.get(bNode);
		}
		return null;
	}

	public boolean isConsistent() {
		RuleNode node;
		for (int i = 0; i < this.getPetriNodes().size(); i++) {
			node = this.getPetriNodes().get(i);
			// check if PN node type is matching
			if (!(Transformator.places.contains(node.getType())
					|| Transformator.transitions.contains(node.getType()))) {
				System.out.println("Error in rule: " + this.name);
				System.out.println("Petri net node " + node.getName()
						+ " does not match Petri net node type. Given node type: " + node.getType());
				return false;
			}
		}

		RuleEdge e;
		RuleNode from;
		RuleNode to;
		for (int i = 0; i < this.getPetriEdges().size(); i++) {
			e = this.getPetriEdges().get(i);
			from = e.getFrom();
			to = e.getTo();
			if (Transformator.places.contains(from.getType()) && Transformator.places.contains(to.getType())) {
				System.out.println("Error in rule: " + this.name);
				System.out.println("Nodes of arc " + e.getName() + " have the same type (both are places)!");
				return false;
			}
			if (Transformator.transitions.contains(from.getType())
					&& Transformator.transitions.contains(to.getType())) {
				System.out.println("Error in rule: " + this.name);
				System.out.println("Nodes of arc " + e.getName() + " have the same type (both are transitions)!");
				return false;
			}
			if (e.getType().equals(Transformator.pnInhibitoryArc) || e.getType().equals(Transformator.pnTestArc)) {
				if (Transformator.transitions.contains(from.getType())) {
					System.out.println("Error in rule: " + this.name);
					System.out.println(
							"Inhibitory and test arcs always have to connect a place with a transtionen (not vice versa)!");
					return false;
				}
			}
		}
		return true;
	}
}
