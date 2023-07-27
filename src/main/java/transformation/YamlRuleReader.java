package transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlRuleReader {
	private YamlRule testRule = null;

	private Iterable<Object> readYamlRules(InputStream is) {
		Yaml yaml = new Yaml(new Constructor(YamlRule.class, new LoaderOptions()));
		return yaml.loadAll(is);
	}

	private Rule getRuleFromYamlRule(YamlRule rule) {
		if (testRule == null) {
			testRule = rule;
		}
		// for BN
		Rule r = new Rule();
		r.setName(rule.getName());
		r.setActive(rule.isActive());
		for (RuleNode node : rule.getBiologicalNodes()) {
			r.addBiologicalNode(node);
		}
		Map<String, RuleEdge> edgeMap = new HashMap<>();
		for (YamlEdge edge : rule.getBiologicalEdges()) {
			RuleEdge e = new RuleEdge(edge.getName(), edge.getType(), r.getBiologicaNode(edge.getFrom()),
					r.getBiologicaNode(edge.getTo()));
			e.setDirected(edge.isDirected());
			r.addBiologicalEdge(e);
			edgeMap.put(edge.getName(), e);
		}

		// for PN
		for (RuleNode node : rule.getPetriNodes()) {
			r.addPetriNode(node);
		}
		for (YamlEdge edge : rule.getPetriEdges()) {
			RuleEdge e = new RuleEdge(edge.getName(), edge.getType(), r.getPetriNode(edge.getFrom()),
					r.getPetriNode(edge.getTo()));
			e.setParameterMap(edge.getParameterMap());
			r.addPetriEdge(e);
		}

		// for Mapping
		for (Map<String, String> map : rule.getMappingBNToPN()) {
			if (map.get("from") != null && map.get("to") != null) {
				r.addBNtoPNMapping(r.getBiologicaNode(map.get("from")), r.getPetriNode(map.get("to")));
			} else {
				System.out.println("Mapping error in rule: BN to PN");
			}
		}
		// for considered edges
		for (String s : rule.getConsideredEdges()) {
			r.getConsideredEdges().add(edgeMap.get(s));
		}
		return r;
	}

	public List<Rule> getRules(File f) {
		try {
			return getRules(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<Rule> getRules(InputStream is) {
		List<Rule> rules = new ArrayList<>();
		Iterable<Object> ymlRules = readYamlRules(is);
		if (ymlRules != null) {
			for (Object ymlRule : ymlRules) {
				YamlRule r = (YamlRule) ymlRule;
				Rule rule = this.getRuleFromYamlRule(r);
				rule.isConsistent();
				rules.add(rule);
			}
		}
		return rules;
	}
}
