package copy;

import java.util.HashSet;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class CopySelectionSingleton extends CopySelection {
    private static CopySelection instance = null;

    private CopySelectionSingleton(HashSet<BiologicalNodeAbstract> vertices, HashSet<BiologicalEdgeAbstract> edges) {
        super(vertices, edges);
    }

    public static CopySelection getInstance() {
        if (instance == null) {
            instance = new CopySelectionSingleton(new HashSet<>(), new HashSet<>());
        }
        return instance;
    }

    public static void setInstance(CopySelection submitted) {
        instance = submitted;
    }
}