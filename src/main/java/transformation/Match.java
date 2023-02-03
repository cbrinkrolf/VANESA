package transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class Match {

	private Rule rule;

	private Map<RuleNode, BiologicalNodeAbstract> nodeMapping;
	private Map<RuleEdge, BiologicalEdgeAbstract> edgeMapping;

	private Set<BiologicalNodeAbstract> nodes;
	private Set<BiologicalEdgeAbstract> edges;

	public Match(Rule rule) {
		this.rule = rule;

		nodeMapping = new HashMap<>();
		edgeMapping = new HashMap<>();
		nodes = new HashSet<>();
		edges = new HashSet<>();

	}

	public void setRule(Rule rule) {
		this.rule = rule;
		nodeMapping.clear();
		edgeMapping.clear();
		nodes.clear();
		edges.clear();
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

	public Set<BiologicalNodeAbstract> getMappedNodes() {
		return this.nodes;
	}

	public Set<BiologicalEdgeAbstract> getMappedEdges() {
		return this.edges;
	}

	public void clearMappings() {
		nodeMapping.clear();
		edgeMapping.clear();
		nodes.clear();
		edges.clear();
	}
}
