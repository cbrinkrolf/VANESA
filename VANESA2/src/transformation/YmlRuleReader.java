package transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

public class YmlRuleReader {

	private YmlRule testrule = null;

	private Iterable<Object> readYmlRules() {
		Yaml yaml = new Yaml(new Constructor(YmlRule.class));
		File initialFile = new File("src/transformation/test2.yml");
		System.out.println("Path for yml file containing transformation rules: " + initialFile.getAbsolutePath());
		InputStream targetStream;
		try {
			targetStream = new FileInputStream(initialFile);
			return yaml.loadAll(targetStream);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Rule getRuleFromYmlRule(YmlRule rule) {
		if (testrule == null) {
			this.testrule = rule;
		}
		Rule r = new Rule();
		r.setRuleName(rule.getRuleName());
		for (int i = 0; i < rule.getBiologicalNodes().size(); i++) {
			r.addBiologicalNode(rule.getBiologicalNodes().get(i));
		}

		YmlEdge edge;
		RuleEdge e;
		for (int i = 0; i < rule.getBiologicalEdges().size(); i++) {
			edge = rule.getBiologicalEdges().get(i);
			// System.out.println("edge: " + edge.getFrom()+" -> "+edge.getTo());
			e = new RuleEdge(edge.getName(), edge.getType(), r.getBiologicaNode(edge.getFrom()),
					r.getBiologicaNode(edge.getTo()));
			r.addBiologicalEdge(e);
		}

		for (int i = 0; i < rule.getPetriNodes().size(); i++) {
			// System.out.println("node added: "+rule.getPetriNodes().get(i).getName());
			r.addPetriNode(rule.getPetriNodes().get(i));
		}
		for (int i = 0; i < rule.getPetriEdges().size(); i++) {
			edge = rule.getPetriEdges().get(i);
			// System.out.println("edge: " + edge.getFrom()+" -> "+edge.getTo());
			// System.out.println(r.getPetriNode(edge.getFrom()).getName());
			e = new RuleEdge(edge.getName(), edge.getType(), r.getPetriNode(edge.getFrom()),
					r.getPetriNode(edge.getTo()));
			r.addPetriEdge(e);
		}

		String m;
		String[] l;
		for (int i = 0; i < rule.getMappingBNToPN().size(); i++) {

			if (rule.getMappingBNToPN().get(i).get("from") != null
					&& rule.getMappingBNToPN().get(i).get("to") != null) {

				// System.out.println(l[0] + " -> "+ l[1]);
				r.addBNtoPNMapping(r.getBiologicaNode(rule.getMappingBNToPN().get(i).get("from")),
						r.getPetriNode(rule.getMappingBNToPN().get(i).get("to")));
			} else {
				System.out.println("Mapping error in rule: BN to PN");
			}
		}
		return r;
	}

	public List<Rule> getRules() {
		List<Rule> rules = new ArrayList<Rule>();
		Iterable<Object> ymlRules = this.readYmlRules();
		List<YmlRule> ymlList = new ArrayList<YmlRule>();
		if (ymlRules != null) {
			Iterator<Object> it = ymlRules.iterator();
			YmlRule r;
			Rule rule;
			while (it.hasNext()) {
				r = (YmlRule) it.next();
				ymlList.add(r);
				rule = this.getRuleFromYmlRule(r);
				rule.isConsistent();
				rules.add(rule);
			}
		}

		writeRules(ymlList);
		return rules;
	}

	public void writeRules(List<YmlRule> rules) {
		StringWriter writer = new StringWriter();
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
			fw = new FileWriter(new File("src/transformation/test2.yml"));
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
