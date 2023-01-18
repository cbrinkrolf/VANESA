package transformation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

public class YamlRuleWriter {
	
	public static String writeRules(OutputStream os, List<Rule> rules){
		List<YamlRule> yamlRules = new ArrayList<YamlRule>();
		for(int i = 0; i<rules.size(); i++){
			yamlRules.add(getYamlRuleFromRule(rules.get(i)));
		}
		
		String result = "";
		try {
			writeYamlRules(os, yamlRules);
		} catch (IOException e) {
			e.printStackTrace();
			result = "Error during Yaml rule Export!";
		}
		return result;
	}
	
	private static void writeYamlRules(OutputStream os, List<YamlRule> rules) throws IOException {
		// Yaml yaml = new Yaml(new Constructor(YmlRule.class));
		Yaml yaml = new Yaml();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rules.size(); i++) {
			sb.append(yaml.dumpAs(rules.get(i), Tag.MAP, null));
			if (i < rules.size() - 1) {
				sb.append("---" + System.lineSeparator());
			}
		}
		//System.out.println(sb);
		
		os.write(sb.toString().getBytes());
		os.close();
	}

	private static YamlRule getYamlRuleFromRule(Rule r){
		YamlRule rule = new YamlRule();
		rule.setName(r.getName());
		
		// for BN
		RuleEdge re;
		rule.setBiologicalNodes(r.getBiologicalNodes());
		List<YamlEdge> edges = new ArrayList<YamlEdge>();
		YamlEdge e;
		for(int i = 0; i<r.getBiologicalEdges().size(); i++){
			re = r.getBiologicalEdges().get(i);
			e = new YamlEdge();
			e.setName(re.getName());
			e.setType(re.getType());
			e.setFrom(re.getFrom().getName());
			e.setTo(re.getTo().getName());
			edges.add(e);
			
		}
		rule.setBiologicalEdges(edges);
		
		// for PN
		edges = new ArrayList<YamlEdge>();
		rule.setPetriNodes(r.getPetriNodes());
		for(int i = 0; i<r.getPetriEdges().size(); i++){
			re = r.getPetriEdges().get(i);
			e = new YamlEdge();
			e.setName(re.getName());
			e.setType(re.getType());
			e.setFrom(re.getFrom().getName());
			e.setTo(re.getTo().getName());
			edges.add(e);
		}
		rule.setPetriEdges(edges);
		
		//mapping
		Map<RuleNode, RuleNode> map = r.getBnToPnMapping();
		RuleNode rn;
		Iterator<RuleNode> it = map.keySet().iterator();
		List<Map<String, String>> yamlMapping = new ArrayList<>();
		HashMap<String, String> hm;
		while(it.hasNext()){
			rn = it.next();
			hm = new HashMap<String, String>();
			hm.put("from", rn.getName());
			hm.put("to", map.get(rn).getName());
			yamlMapping.add(hm);
		}
		
		rule.setMappingBNToPN(yamlMapping);
		
		for(int i = 0; i<r.getConsideredEdges().size(); i++){
			rule.getConsideredEdges().add(r.getConsideredEdges().get(i).getName());
		}
		
		return rule;
	}
}
