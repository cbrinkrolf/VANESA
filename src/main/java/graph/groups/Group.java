package graph.groups;

import biologicalObjects.nodes.BiologicalNodeAbstract;

import java.util.ArrayList;
import java.util.Collection;

public class Group extends ArrayList<BiologicalNodeAbstract> {
	private static final long serialVersionUID = 1L;

	public Group(Collection<BiologicalNodeAbstract> nodes) {
		super(nodes);
	}
}
