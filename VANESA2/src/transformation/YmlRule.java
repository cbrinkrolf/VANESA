package transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YmlRule {

	private String ruleName;
	private List<RuleNode> biologicalNodes = new ArrayList<RuleNode>();
	private List<YmlEdge> biologicalEdges = new ArrayList<YmlEdge>();
	
	private List<RuleNode> petriNodes = new ArrayList<RuleNode>();
	private List<YmlEdge> petriEdges = new ArrayList<YmlEdge>();
	
	private List<Map<String, String>> mappingBNToPN = new ArrayList<>();
	
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
	public List<Map<String, String>> getMappingBNToPN() {
		return mappingBNToPN;
	}
	public void setMappingBNToPN(List<Map<String, String>> mappingBNToPN) {
		this.mappingBNToPN = mappingBNToPN;
	}
	
	
	
	
	
}
