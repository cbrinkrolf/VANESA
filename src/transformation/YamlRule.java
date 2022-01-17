package transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YamlRule {

	private String name;
	private List<RuleNode> biologicalNodes = new ArrayList<RuleNode>();
	private List<YamlEdge> biologicalEdges = new ArrayList<YamlEdge>();
	
	private List<RuleNode> petriNodes = new ArrayList<RuleNode>();
	private List<YamlEdge> petriEdges = new ArrayList<YamlEdge>();
	
	private List<Map<String, String>> mappingBNToPN = new ArrayList<>();
	
	private List<String> consideredEdges = new ArrayList<String>();
	
}
