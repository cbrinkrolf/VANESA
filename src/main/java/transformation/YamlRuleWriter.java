package transformation;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.BaseWriter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

public class YamlRuleWriter extends BaseWriter<List<Rule>> {
	public YamlRuleWriter(File file) {
		super(file);
	}

	@Override
	protected void internalWrite(OutputStream outputStream, List<Rule> rules) throws Exception {
		List<YamlRule> yamlRules = new ArrayList<>();
		for (Rule rule : rules) {
			yamlRules.add(getYamlRuleFromRule(rule));
		}
		writeYamlRules(outputStream, yamlRules);
	}

	private static void writeYamlRules(OutputStream os, List<YamlRule> rules) throws IOException {
		Yaml yaml = new Yaml();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rules.size(); i++) {
			sb.append(yaml.dumpAs(rules.get(i), Tag.MAP, null));
			if (i < rules.size() - 1) {
				sb.append("---").append(System.lineSeparator());
			}
		}
		os.write(sb.toString().getBytes());
		os.close();
	}

	private static YamlRule getYamlRuleFromRule(Rule r) {
		YamlRule rule = new YamlRule();
		rule.setName(r.getName());
		rule.setActive(r.isActive());
		// for BN
		rule.setBiologicalNodes(r.getBiologicalNodes());
		List<YamlEdge> biologicalEdges = new ArrayList<>();
		for (int i = 0; i < r.getBiologicalEdges().size(); i++) {
			RuleEdge re = r.getBiologicalEdges().get(i);
			YamlEdge e = new YamlEdge();
			e.setName(re.getName());
			e.setType(re.getType());
			e.setFrom(re.getFrom().getName());
			e.setTo(re.getTo().getName());
			e.setDirected(re.isDirected());
			biologicalEdges.add(e);
		}
		rule.setBiologicalEdges(biologicalEdges);
		// for PN
		rule.setPetriNodes(r.getPetriNodes());
		List<YamlEdge> petriEdges = new ArrayList<>();
		for (int i = 0; i < r.getPetriEdges().size(); i++) {
			RuleEdge re = r.getPetriEdges().get(i);
			YamlEdge e = new YamlEdge();
			e.setName(re.getName());
			e.setType(re.getType());
			e.setFrom(re.getFrom().getName());
			e.setTo(re.getTo().getName());
			e.setParameterMap(re.getParameterMap());
			petriEdges.add(e);
		}
		rule.setPetriEdges(petriEdges);
		// mapping
		Map<RuleNode, RuleNode> map = r.getBnToPnMapping();
		List<Map<String, String>> yamlMapping = new ArrayList<>();
		for (RuleNode rn : map.keySet()) {
			Map<String, String> hm = new HashMap<>();
			hm.put("from", rn.getName());
			hm.put("to", map.get(rn).getName());
			yamlMapping.add(hm);
		}
		rule.setMappingBNToPN(yamlMapping);
		for (int i = 0; i < r.getConsideredEdges().size(); i++) {
			rule.getConsideredEdges().add(r.getConsideredEdges().get(i).getName());
		}
		return rule;
	}
}
