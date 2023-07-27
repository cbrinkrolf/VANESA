package transformation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleManager {
	private static RuleManager instance = null;

	private List<Rule> rules = new ArrayList<>();

	public static synchronized RuleManager getInstance() {
		if (RuleManager.instance == null) {
			RuleManager.instance = new RuleManager();
		}
		return RuleManager.instance;
	}

	private RuleManager() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("rules/savedRules.yaml")) {
			rules = new YamlRuleReader().getRules(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void addRules(List<Rule> rules) {
		this.rules.addAll(rules);
	}

	public void clearAllRules() {
		rules.clear();
	}

	public List<Rule> getActiveRules() {
		return rules.stream().filter(Rule::isActive).collect(Collectors.toList());
	}
}
