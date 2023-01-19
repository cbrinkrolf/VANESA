package transformation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class RuleManager {

	private static RuleManager instance = null;

	@Getter
	private List<Rule> rules = new ArrayList<>();

	public static synchronized RuleManager getInstance() {
		if (RuleManager.instance == null) {
			RuleManager.instance = new RuleManager();
			instance.init();
		}
		return RuleManager.instance;
	}

	private void init() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("rules/savedRules.yaml")) {
			rules = new YamlRuleReader().getRules(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addRules(List<Rule> rules) {
		this.rules.addAll(rules);
	}

	public void clearAllRules() {
		rules.clear();
	}

	public List<Rule> getActiveRules() {
		List<Rule> activeRules = new ArrayList<>();

		for (int i = 0; i < this.rules.size(); i++) {
			if (rules.get(i).isActive()) {
				activeRules.add(this.rules.get(i));
			}
		}
		return activeRules;
	}
}
