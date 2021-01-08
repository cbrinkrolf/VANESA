package transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rule {

	private List<RuleNode> bNodes = new ArrayList<>();
	private List<RuleEdge> bEdges = new ArrayList<>();

	private List<RuleNode> pNodes = new ArrayList<>();
	private List<RuleEdge> pEdges = new ArrayList<>();

	private HashMap<RuleNode, RuleNode> bn2Pn = new HashMap<RuleNode, RuleNode>();
	private HashMap<RuleNode, RuleNode> pn2Bn = new HashMap<RuleNode, RuleNode>();

	public Rule() {

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
	
	public List<RuleEdge> getAllPetriEdges(){
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

	/*
	 * 
	 * // biological node/edge related private List<String> bNodeNames = new
	 * ArrayList<String>(); private HashMap<String, String> bNodeToType = new
	 * HashMap<String, String>();
	 * 
	 * private List<String> bEdgeNames = new ArrayList<String>(); private
	 * HashMap<String, String> bEdgeToType = new HashMap<String, String>(); private
	 * HashMap<String, String> bEdgeToFrom = new HashMap<String, String>(); private
	 * HashMap<String, String> bEdgeToTo = new HashMap<String, String>();
	 * 
	 * // Petri net node/edge related private List<String> pNodeNames = new
	 * ArrayList<String>(); private HashMap<String, String> pNodeToType = new
	 * HashMap<String, String>();
	 * 
	 * private List<String> pEdgeNames = new ArrayList<String>(); private
	 * HashMap<String, String> pNEdgeToType = new HashMap<String, String>(); private
	 * HashMap<String, String> pNEdgeToFrom = new HashMap<String, String>(); private
	 * HashMap<String, String> pNEdgeToTo = new HashMap<String, String>();
	 * 
	 * private HashMap<String, String> bnToPNnode = new HashMap<String, String>();
	 * private HashMap<String, String> pNToBNnode = new HashMap<String, String>();
	 * 
	 * public List<String> getBNodeNames() { return bNodeNames; }
	 * 
	 * public HashMap<String, String> getBNodeToType() { return bNodeToType; }
	 * 
	 * public List<String> getBEdgeNames() { return bEdgeNames; }
	 * 
	 * public HashMap<String, String> getBEdgeToType() { return bEdgeToType; }
	 * 
	 * public HashMap<String, String> getBEdgeToFrom() { return bEdgeToFrom; }
	 * 
	 * public HashMap<String, String> getBEdgeToTo() { return bEdgeToTo; }
	 * 
	 * public HashMap<String, String> getBnToPNNode() { return bnToPNnode; }
	 * 
	 * public HashMap<String, String> getPnToBNNode() { return pNToBNnode; }
	 * 
	 * public List<String> getPNodeNames() { return pNodeNames; }
	 * 
	 * public HashMap<String, String> getpNodeToType() { return pNodeToType; }
	 * 
	 * public List<String> getPEdgeNames() { return pEdgeNames; }
	 * 
	 * public HashMap<String, String> getPNEdgeToType() { return pNEdgeToType; }
	 * 
	 * public HashMap<String, String> getPNEdgeToFrom() { return pNEdgeToFrom; }
	 * 
	 * public HashMap<String, String> getPNEdgeToTo() { return pNEdgeToTo; }
	 * 
	 */
}
