package graph.groups;

import biologicalObjects.nodes.BiologicalNodeAbstract;

import java.util.ArrayList;
import java.util.Collection;

public class Group extends ArrayList<BiologicalNodeAbstract> {
    public Group(Collection<BiologicalNodeAbstract> nodes) {
        super(nodes);
    }
}
