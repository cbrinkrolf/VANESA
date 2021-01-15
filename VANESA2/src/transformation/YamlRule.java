package transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlRule {

	private String name;
	private List<RuleNode> biologicalNodes = new ArrayList<RuleNode>();
	private List<YamlEdge> biologicalEdges = new ArrayList<YamlEdge>();
	
	private List<RuleNode> petriNodes = new ArrayList<RuleNode>();
	private List<YamlEdge> petriEdges = new ArrayList<YamlEdge>();
	
	private List<Map<String, String>> mappingBNToPN = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RuleNode> getBiologicalNodes() {
		return biologicalNodes;
	}
	public void setBiologicalNodes(List<RuleNode> biologicalNodes) {
		this.biologicalNodes = biologicalNodes;
	}
	public List<YamlEdge> getBiologicalEdges() {
		return biologicalEdges;
	}
	public void setBiologicalEdges(List<YamlEdge> biologicalEdges) {
		this.biologicalEdges = biologicalEdges;
	}
	public List<RuleNode> getPetriNodes() {
		return petriNodes;
	}
	public void setPetriNodes(List<RuleNode> petriNodes) {
		this.petriNodes = petriNodes;
	}
	public List<YamlEdge> getPetriEdges() {
		return petriEdges;
	}
	public void setPetriEdges(List<YamlEdge> petriEdges) {
		this.petriEdges = petriEdges;
	}
	public List<Map<String, String>> getMappingBNToPN() {
		return mappingBNToPN;
	}
	public void setMappingBNToPN(List<Map<String, String>> mappingBNToPN) {
		this.mappingBNToPN = mappingBNToPN;
	}
	
}
