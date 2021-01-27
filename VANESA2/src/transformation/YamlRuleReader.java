package transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlRuleReader {

	private YamlRule testrule = null;
	
	private Iterable<Object> readYamlRules(File f) {
		Yaml yaml = new Yaml(new Constructor(YamlRule.class));
		//File initialFile = new File("src/transformation/test2.yml");
		System.out.println("Path for yml file containing transformation rules: " + f.getAbsolutePath());
		InputStream is;
		try {
			is = new FileInputStream(f);
			return yaml.loadAll(is);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Rule getRuleFromYamlRule(YamlRule rule) {
		if (testrule == null) {
			this.testrule = rule;
		}
		// for BN
		Rule r = new Rule();
		r.setName(rule.getName());
		for (int i = 0; i < rule.getBiologicalNodes().size(); i++) {
			r.addBiologicalNode(rule.getBiologicalNodes().get(i));
		}
		YamlEdge edge;
		RuleEdge e;
		Map<String, RuleEdge> edgeMap = new HashMap<String, RuleEdge>();
		for (int i = 0; i < rule.getBiologicalEdges().size(); i++) {
			edge = rule.getBiologicalEdges().get(i);
			e = new RuleEdge(edge.getName(), edge.getType(), r.getBiologicaNode(edge.getFrom()),
					r.getBiologicaNode(edge.getTo()));
			r.addBiologicalEdge(e);
			edgeMap.put(edge.getName(), e);
		}

		// for PN
		for (int i = 0; i < rule.getPetriNodes().size(); i++) {
			r.addPetriNode(rule.getPetriNodes().get(i));
		}
		for (int i = 0; i < rule.getPetriEdges().size(); i++) {
			edge = rule.getPetriEdges().get(i);
			e = new RuleEdge(edge.getName(), edge.getType(), r.getPetriNode(edge.getFrom()),
					r.getPetriNode(edge.getTo()));
			r.addPetriEdge(e);
		}

		// for Mapping
		for (int i = 0; i < rule.getMappingBNToPN().size(); i++) {

			if (rule.getMappingBNToPN().get(i).get("from") != null
					&& rule.getMappingBNToPN().get(i).get("to") != null) {
				r.addBNtoPNMapping(r.getBiologicaNode(rule.getMappingBNToPN().get(i).get("from")),
						r.getPetriNode(rule.getMappingBNToPN().get(i).get("to")));
			} else {
				System.out.println("Mapping error in rule: BN to PN");
			}
		}
		// for considered edges
		for(int i = 0; i<rule.getConsideredEdges().size(); i++){
			r.getConsideredEdges().add(edgeMap.get(rule.getConsideredEdges().get(i)));
		}
		return r;
	}

	public List<Rule> getRules(File f) {
		List<Rule> rules = new ArrayList<Rule>();
		Iterable<Object> ymlRules = this.readYamlRules(f);
		List<YamlRule> ymlList = new ArrayList<YamlRule>();
		if (ymlRules != null) {
			Iterator<Object> it = ymlRules.iterator();
			YamlRule r;
			Rule rule;
			while (it.hasNext()) {
				r = (YamlRule) it.next();
				ymlList.add(r);
				rule = this.getRuleFromYamlRule(r);
				rule.isConsistent();
				rules.add(rule);
			}
		}
		//writeRules(ymlList);
		return rules;
	}
}
