package transformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import gui.MyPopUp;

public class YamlRuleWriter {
	
	public void writeRules(List<Rule> rules){
		List<YamlRule> yamlRules = new ArrayList<YamlRule>();
		for(int i = 0; i<rules.size(); i++){
			yamlRules.add(this.getYamlRuleFromRule(rules.get(i)));
		}
		
		this.writeYamlRules(yamlRules);
	}
	
	private void writeYamlRules(List<YamlRule> rules) {
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
		FileWriter fw;
		try {
			fw = new FileWriter(new File("src/transformation/savedRules.yaml"));
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MyPopUp.getInstance().show("YAML Rules", rules.size()+" rules were written to file!");
	}

	private YamlRule getYamlRuleFromRule(Rule r){
		YamlRule rule = new YamlRule();
		rule.setName(r.getName());
		
		// for BN
		RuleEdge re;
		rule.setBiologicalNodes(r.getAllBiologicalNodes());
		List<YamlEdge> edges = new ArrayList<YamlEdge>();
		YamlEdge e;
		for(int i = 0; i<r.getAllBiologicalEdges().size(); i++){
			re = r.getAllBiologicalEdges().get(i);
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
		rule.setPetriNodes(r.getAllPetriNodes());
		for(int i = 0; i<r.getAllPetriEdges().size(); i++){
			re = r.getAllPetriEdges().get(i);
			e = new YamlEdge();
			e.setName(re.getName());
			e.setType(re.getType());
			e.setFrom(re.getFrom().getName());
			e.setTo(re.getTo().getName());
			edges.add(e);
		}
		rule.setPetriEdges(edges);
		
		//mapping
		Map<RuleNode, RuleNode> map = r.getBNtoPNMapping();
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
