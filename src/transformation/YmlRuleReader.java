package transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YmlRuleReader {

	
	private Iterable<Object> readYmlRules(){
		Yaml yaml = new Yaml(new Constructor(YmlRule.class));
		File initialFile = new File("src/transformation/test.yml");
		System.out.println("Path for yml file containing transformation rules: "+initialFile.getAbsolutePath());
	    InputStream targetStream;
		try {
			targetStream = new FileInputStream(initialFile);
			return  yaml.loadAll(targetStream);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Rule getRuleFromYmlRule(YmlRule rule){
		Rule r = new Rule();
		r.setRuleName(rule.getRuleName());
		for(int i = 0; i<rule.getBiologicalNodes().size(); i++){
			r.addBiologicalNode(rule.getBiologicalNodes().get(i));
		}
		
		YmlEdge edge;
		RuleEdge e;
		for(int i = 0; i<rule.getBiologicalEdges().size(); i++){
			edge = rule.getBiologicalEdges().get(i);
			//System.out.println("edge: " + edge.getFrom()+" -> "+edge.getTo());
			e = new RuleEdge(edge.getName(), edge.getType(), r.getBiologicaNode(edge.getFrom()), r.getBiologicaNode(edge.getTo()));
			r.addBiologicalEdge(e);
		}
		
		for(int i = 0; i<rule.getPetriNodes().size(); i++){
			//System.out.println("node added: "+rule.getPetriNodes().get(i).getName());
			r.addPetriNode(rule.getPetriNodes().get(i));
		}
		for(int i = 0; i<rule.getPetriEdges().size(); i++){
			edge = rule.getPetriEdges().get(i);
			//System.out.println("edge: " + edge.getFrom()+" -> "+edge.getTo());
			//System.out.println(r.getPetriNode(edge.getFrom()).getName());
			e = new RuleEdge(edge.getName(), edge.getType(), r.getPetriNode(edge.getFrom()), r.getPetriNode(edge.getTo()));
			r.addPetriEdge(e);
		}
		
		String m;
		String[] l;
		for(int i = 0; i<rule.getMappingBNToPN().size(); i++){
			m = rule.getMappingBNToPN().get(i);
			l = m.split("->");
			if(l.length == 2){
				//System.out.println(l[0] + " -> "+ l[1]);
				r.addBNtoPNMapping(r.getBiologicaNode(l[0]), r.getPetriNode(l[1]));
			}else{
				System.out.println("Split went wrong");
			}
		}
		return r;
	}
	
	
	public List<Rule> getRules(){
		List<Rule> rules = new ArrayList<Rule>();
		Iterable<Object> ymlRules = this.readYmlRules();
		
		if(ymlRules != null){
			Iterator<Object> it = ymlRules.iterator();
			YmlRule r;
			Rule rule;
			while(it.hasNext()){
				r = (YmlRule) it.next();
				rule = this.getRuleFromYmlRule(r);
				rule.isConsistent();
				rules.add(rule);
			}
		}
		return rules;
	}
}
