package transformation;

import java.util.ArrayList;
import java.util.List;

public class YmlRule {

	private String ruleName;
	private List<RuleNode> biologicalNodes = new ArrayList<RuleNode>();
	private List<YmlEdge> biologicalEdges = new ArrayList<YmlEdge>();
	
	private List<RuleNode> petriNodes = new ArrayList<RuleNode>();
	private List<YmlEdge> petriEdges = new ArrayList<YmlEdge>();
	
	private List<String> mappingBNToPN = new ArrayList<String>();
	
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public List<RuleNode> getBiologicalNodes() {
		return biologicalNodes;
	}
	public void setBiologicalNodes(List<RuleNode> biologicalNodes) {
		this.biologicalNodes = biologicalNodes;
	}
	public List<YmlEdge> getBiologicalEdges() {
		return biologicalEdges;
	}
	public void setBiologicalEdges(List<YmlEdge> biologicalEdges) {
		this.biologicalEdges = biologicalEdges;
	}
	public List<RuleNode> getPetriNodes() {
		return petriNodes;
	}
	public void setPetriNodes(List<RuleNode> petriNodes) {
		this.petriNodes = petriNodes;
	}
	public List<YmlEdge> getPetriEdges() {
		return petriEdges;
	}
	public void setPetriEdges(List<YmlEdge> petriEdges) {
		this.petriEdges = petriEdges;
	}
	public List<String> getMappingBNToPN() {
		return mappingBNToPN;
	}
	public void setMappingBNToPN(List<String> mappingBNToPN) {
		this.mappingBNToPN = mappingBNToPN;
	}
	
	
	
	
	
}
