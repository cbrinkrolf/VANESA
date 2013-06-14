package graph.algorithms.alignment;

import cern.colt.matrix.DoubleMatrix2D;

public abstract class GraphAlignmentAlgorithms {

	protected AdjacencyMatrix graphA, graphB;
	
	protected DoubleMatrix2D solutionMatrix;
	
	protected SimilarityMatrix similarity;
	
	public abstract void run() throws Exception;
	
	public DoubleMatrix2D getSolutionMatrix(){
		return solutionMatrix;
	}

	public AdjacencyMatrix getGraphA() {
		return graphA;
	}

	public AdjacencyMatrix getGraphB() {
		return graphB;
	}
	
	public SimilarityMatrix getSimilarity() {
		return similarity;
	}
	
}
