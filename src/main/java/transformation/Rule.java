package transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rule {
	private String name = "";
	private boolean active = true;

	private List<RuleNode> biologicalNodes = new ArrayList<>();
	private List<RuleEdge> biologicalEdges = new ArrayList<>();

	private List<RuleNode> petriNodes = new ArrayList<>();
	private List<RuleEdge> petriEdges = new ArrayList<>();

	private HashMap<RuleNode, RuleNode> bnToPnMapping = new HashMap<>();
	private HashMap<RuleNode, RuleNode> pnToBnMapping = new HashMap<>();

	private List<RuleEdge> consideredEdges = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<RuleNode> getBiologicalNodes() {
		return biologicalNodes;
	}

	public void setBiologicalNodes(List<RuleNode> biologicalNodes) {
		this.biologicalNodes = biologicalNodes;
	}

	public List<RuleEdge> getBiologicalEdges() {
		return biologicalEdges;
	}

	public void setBiologicalEdges(List<RuleEdge> biologicalEdges) {
		this.biologicalEdges = biologicalEdges;
	}

	public List<RuleNode> getPetriNodes() {
		return petriNodes;
	}

	public void setPetriNodes(List<RuleNode> petriNodes) {
		this.petriNodes = petriNodes;
	}

	public List<RuleEdge> getPetriEdges() {
		return petriEdges;
	}

	public void setPetriEdges(List<RuleEdge> petriEdges) {
		this.petriEdges = petriEdges;
	}

	public HashMap<RuleNode, RuleNode> getBnToPnMapping() {
		return bnToPnMapping;
	}

	public void setBnToPnMapping(HashMap<RuleNode, RuleNode> bnToPnMapping) {
		this.bnToPnMapping = bnToPnMapping;
	}

	public HashMap<RuleNode, RuleNode> getPnToBnMapping() {
		return pnToBnMapping;
	}

	public void setPnToBnMapping(HashMap<RuleNode, RuleNode> pnToBnMapping) {
		this.pnToBnMapping = pnToBnMapping;
	}

	public List<RuleEdge> getConsideredEdges() {
		return consideredEdges;
	}

	public void setConsideredEdges(List<RuleEdge> consideredEdges) {
		this.consideredEdges = consideredEdges;
	}

	public void addBiologicalEdge(RuleEdge e) {
		for (RuleEdge biologicalEdge : biologicalEdges) {
			if (biologicalEdge.getName().equals(e.getName())) {
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
		for (RuleNode biologicalNode : biologicalNodes) {
			if (n.getName().equals(biologicalNode.getName())) {
				System.out.println("Node exists already");
				return;
			}
		}
		biologicalNodes.add(n);
	}

	public RuleNode getBiologicaNode(String name) {
		RuleNode n = null;
		for (RuleNode biologicalNode : biologicalNodes) {
			if (name.equals(biologicalNode.getName())) {
				return biologicalNode;
			}
		}
		return n;
	}

	public RuleEdge getBiologicalEdge(String name) {
		RuleEdge e = null;
		for (RuleEdge biologicalEdge : biologicalEdges) {
			if (name.equals(biologicalEdge.getName())) {
				return biologicalEdge;
			}
		}
		return e;
	}

	public void addPetriEdge(RuleEdge e) {
		for (RuleEdge petriEdge : petriEdges) {
			if (petriEdge.getName().equals(e.getName())) {
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
		for (RuleNode petriNode : petriNodes) {
			if (n.getName().equals(petriNode.getName())) {
				System.out.println("Node exists already");
				return;
			}
		}
		petriNodes.add(n);
	}

	public RuleNode getPetriNode(String name) {
		RuleNode n = null;
		for (RuleNode petriNode : petriNodes) {
			if (name.equals(petriNode.getName())) {
				return petriNode;
			}
		}
		return n;
	}

	public RuleEdge getPetriEdge(String name) {
		RuleEdge e = null;
		for (RuleEdge petriEdge : petriEdges) {
			if (name.equals(petriEdge.getName())) {
				return petriEdge;
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
		for (RuleNode node : getPetriNodes()) {
			// check if PN node type is matching
			if (!(Transformator.places.contains(node.getType())
					|| Transformator.transitions.contains(node.getType()))) {
				System.out.println("Error in rule: " + this.name);
				System.out.println("Petri net node " + node.getName()
						+ " does not match Petri net node type. Given node type: " + node.getType());
				return false;
			}
		}
		for (RuleEdge e : getPetriEdges()) {
			RuleNode from = e.getFrom();
			RuleNode to = e.getTo();
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
			if (e.getType().equals(Transformator.pnInhibitorArc) || e.getType().equals(Transformator.pnTestArc)) {
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

	public int getIncomingDirectedEdgeCount(RuleNode rn) {
		int count = 0;
		if (biologicalNodes.contains(rn)) {
			for (RuleEdge re : biologicalEdges) {
				if (rn == re.getTo() && re.isDirected()) {
					count++;
				}
			}
		} else if (petriNodes.contains(rn)) {
			for (RuleEdge re : petriEdges) {
				if (rn == re.getTo()) {
					count++;
				}
			}
		} else {
			System.err.println("RuleNode " + rn.getName() + " not found! Called method: getIncomingEdgeCount");
		}
		return count;
	}

	public int getOutgoingDirectedEdgeCount(RuleNode rn) {
		int count = 0;
		if (biologicalNodes.contains(rn)) {
			for (RuleEdge re : biologicalEdges) {
				if (rn == re.getFrom() && re.isDirected()) {
					count++;
				}
			}
		} else if (petriNodes.contains(rn)) {
			for (RuleEdge re : petriEdges) {
				if (rn == re.getFrom()) {
					count++;
				}
			}
		} else {
			System.err.println("RuleNode " + rn.getName() + " not found! Called method: getOutgoingEdgeCount");
		}
		return count;
	}
	
	public int getUndirectedEdgeCount(RuleNode rn){
		int count = 0;
		if (biologicalNodes.contains(rn)) {
			for (RuleEdge re : biologicalEdges) {
				if ((rn == re.getFrom() || rn == re.getTo()) && !re.isDirected()) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean isBNEmpty() {
		return biologicalNodes.isEmpty();
	}

	public boolean isPNEmpty() {
		return petriNodes.isEmpty();
	}
}
