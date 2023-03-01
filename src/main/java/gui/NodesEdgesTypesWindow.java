package gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.algorithms.NetworkProperties;

public class NodesEdgesTypesWindow {
    private final Pathway pw;

    public NodesEdgesTypesWindow(Pathway pw) {
        this.pw = pw;
        NetworkProperties np = new NetworkProperties();
        int nodes = np.getNodeCount();
        int edges = np.getEdgeCount();
        int logicalNodes = 0;
        for (BiologicalNodeAbstract bna : pw.getGraph().getAllVertices()) {
            if (bna.isLogical()) {
                logicalNodes++;
            }
        }
        StringBuilder instructions = new StringBuilder();
        instructions.append("<html>");
        instructions.append(
                "<table rules=\"rows\" style=\"border-collapse:separate; border-spacing:0; width:100%; border-top:1px solid #eaeaea;\">");
        writeLine(instructions, "", "all = ", "true +", "logical");
        writeLine(instructions, "Number of nodes:", nodes + "", nodes - logicalNodes + "", logicalNodes + "");
        // writeLine(instructions, "Number of (true) nodes:", nodes - logicalNodes + "");
        // writeLine(instructions, "Number of logical nodes:", logicalNodes + "");
        writeLine(instructions, "Number of edges:", edges + "");
        writeLine(instructions, "<hr>Nodes", "");

        Map<String, Integer> nodesMap = getNodeTypes();
        Map<String, Integer> nodesMapLogical = getNodeTypesLogical();

        for (String type : nodesMap.keySet()) {
            if (nodesMapLogical.containsKey(type)) {
                writeLine(instructions, type + ": ", nodesMap.get(type) + "",
                          nodesMap.get(type) - nodesMapLogical.get(type) + "", nodesMapLogical.get(type) + "");
            } else {
                writeLine(instructions, type + ": ", nodesMap.get(type) + "");
            }
        }
        Map<String, Integer> edgesMap = getEdgeTypes();
        writeLine(instructions, "<hr>Edges", "");
        for (String type : edgesMap.keySet()) {
            writeLine(instructions, type + ": ", edgesMap.get(type) + "");
        }
        instructions.append("</table>").append("</html>");
        JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), instructions.toString(),
                                      "Network Properties", JOptionPane.PLAIN_MESSAGE);
    }

    private Map<String, Integer> getNodeTypes() {
        Map<String, Integer> map = new HashMap<>();
        String type;
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            type = bna.getBiologicalElement();
            if (!map.containsKey(type)) {
                map.put(type, 0);
            }
            map.put(type, map.get(type) + 1);
        }
        return map;
    }

    private Map<String, Integer> getNodeTypesLogical() {
        Map<String, Integer> map = new HashMap<>();
        String type;
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            if (bna.isLogical()) {
                type = bna.getBiologicalElement();
                if (!map.containsKey(type)) {
                    map.put(type, 0);
                }
                map.put(type, map.get(type) + 1);
            }
        }
        return map;
    }

    private Map<String, Integer> getEdgeTypes() {
        Map<String, Integer> map = new HashMap<>();
        String type;
        for (BiologicalEdgeAbstract bea : pw.getAllEdges()) {
            type = bea.getBiologicalElement();
            if (!map.containsKey(type)) {
                map.put(type, 0);
            }
            map.put(type, map.get(type) + 1);
        }
        return map;
    }

    private void writeLine(StringBuilder sb, String description, String attribute1) {
        writeLine(sb, description, attribute1, "", "");
    }

    private void writeLine(StringBuilder sb, String description, String attribute1, String attribute2,
                           String attribute3) {
        sb.append("<tr>");
        sb.append("<th style=\"text-align: left;color:#666;text-transform:uppercase;\" scope=\"col\">");
        sb.append(description).append("</th>");
        sb.append("<td style=\"padding:5px;color:#888;\">").append(attribute1).append("</td>");
        sb.append("<td style=\"padding:5px;color:#888;\">").append(attribute2).append("</td>");
        sb.append("<td style=\"padding:5px;color:#888;\">").append(attribute3).append("</td>");
        sb.append("</tr>");
    }
}