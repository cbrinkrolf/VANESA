package graph.hierarchies;

import java.util.HashMap;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class AutoCoarse {
    public static void coarseSeparatedSubGraphs(Pathway pw) {
        // Build Graph Analysis Tree
        GraphAnalysisTree gat = new GraphAnalysisTree(pw);
        gat.build();
        // Get the autoCoarse mapping for coarsing splitting node subpathways.
        HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> map = gat.getSplittingNodesMapping();
        // Comparator using the mapping.
        class HLC implements HierarchyListComparator<Integer> {
            public HLC() {
            }

            public Integer getValue(BiologicalNodeAbstract n) {
                if (map.containsKey(n)) {
                    return map.get(n).getID();
                }
                return getSubValue(n);
            }

            public Integer getSubValue(BiologicalNodeAbstract n) {
                return n.getID();
            }
        }
        // automated reconstruction
        HierarchyList<Integer> l = new HierarchyList<>();
        l.addAll(pw.getAllGraphNodes());
        l.sort(new HLC());
        l.coarse();
    }
}
