package transformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Match {

	private Rule rule;

	private Map<RuleNode, BiologicalNodeAbstract> nodeMapping;
	private Map<RuleEdge, BiologicalEdgeAbstract> edgeMapping;


	public Match(Rule rule) {
		this.rule = rule;
		nodeMapping = new HashMap<>();
		edgeMapping = new HashMap<>();
	}

	public void setRule(Rule rule) {
		this.rule = rule;
		nodeMapping.clear();
		edgeMapping.clear();
	}

	public Rule getRule() {
		return this.rule;
	}

	public void addMapping(RuleNode rn, BiologicalNodeAbstract bna) {
		nodeMapping.put(rn, bna);
	}

	public void addMapping(RuleEdge re, BiologicalEdgeAbstract bea) {
		edgeMapping.put(re, bea);
	}

	public BiologicalNodeAbstract getMapping(RuleNode re) {
		return nodeMapping.get(re);
	}

	public BiologicalEdgeAbstract getMapping(RuleEdge re) {
		return edgeMapping.get(re);
	}

	public Collection<BiologicalNodeAbstract> getMappedNodes() {
		return nodeMapping.values();
	}

	public Collection<BiologicalEdgeAbstract> getMappedEdges() {
		return edgeMapping.values();
	}

	public void clearEdgeMapping() {
		edgeMapping.clear();
	}
}
