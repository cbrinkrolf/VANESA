package petriNet;

import biologicalObjects.edges.BiologicalEdgeAbstract;

public class CovEdge extends BiologicalEdgeAbstract {

	public CovEdge(String label, String name, CovNode from, CovNode to) {
		super(label, name, from, to);
	}
}
