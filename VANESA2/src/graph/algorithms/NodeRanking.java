package graph.algorithms;

import edu.uci.ics.jung.algorithms.importance.AbstractRanker;
import edu.uci.ics.jung.algorithms.importance.DegreeDistributionRanker;
import edu.uci.ics.jung.graph.Graph;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;

public class NodeRanking {

	GraphInstance graphInstance = new GraphInstance();
	InternalGraphRepresentation graphRepresentation;
	
	Pathway pathway;
	
	public NodeRanking(){
		
		pathway = graphInstance.getPathway();
		
	}
	
	
	public AbstractRanker testRanking() {
		
		MyGraph myGraph = pathway.getGraph();
		Graph g = myGraph.getJungGraph();
		
		DegreeDistributionRanker ranker;
		ranker = new DegreeDistributionRanker(g);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();
		ranker.printRankings(true, true);
		
		return ranker;
		
	}
	
	
	
}
