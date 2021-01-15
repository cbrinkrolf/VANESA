package transformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RuleManager {

	private static RuleManager instance = null;
	
	private List<Rule> rules = new ArrayList<Rule>();

	public static synchronized RuleManager getInstance() {
		if (RuleManager.instance == null) {
			RuleManager.instance = new RuleManager();
			instance.init();
		}
		return RuleManager.instance;
	}
	
	private void init(){
		//File f = new File("src/transformation/test2.yaml");
		File f = new File("src/transformation/savedRules.yaml");
		this.rules = new YamlRuleReader().getRules(f);
	}
	
	public void importRulesFromFile(File f){
		rules.addAll(new YamlRuleReader().getRules(f));
	}
	
	public void clearAllRules(){
		rules.clear();
	}
	
	public List<Rule> getRules(){
		return this.rules;
	}

}
