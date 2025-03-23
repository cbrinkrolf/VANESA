package petriNet;

import biologicalObjects.edges.BiologicalEdgeAbstract;

public class CovEdge extends BiologicalEdgeAbstract {
	public CovEdge(final String label, final String name, final CovNode from, final CovNode to) {
		super(label, name, from, to, "");
	}
}
