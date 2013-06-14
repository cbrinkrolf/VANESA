package graph.algorithms;

import java.util.HashMap;

import edu.uci.ics.jung.graph.Graph;

public class ResultGraph{
	public Graph graph;
	public HashMap mapping;
	
	public ResultGraph(Graph g, HashMap m){
		this.graph = g;
		this.mapping = m;
	}
}