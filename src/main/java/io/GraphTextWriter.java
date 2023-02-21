package io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Locale;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class GraphTextWriter {
    public String write(OutputStream os, Pathway pw) {
        HashSet<String> nodes = new HashSet<>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("#Nodes \n");
            for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
                if (!nodes.contains(bna.getLabel() + " # " + bna.getName())) {
                    sb.append(bna.getLabel()).append(" # ").append(bna.getName()).append(" \n");
                    nodes.add(bna.getLabel() + " # " + bna.getName());
                }
            }
            sb.append("#Edges \n");
            for (BiologicalEdgeAbstract bioEdge : pw.getAllEdges()) {
                String from = bioEdge.getFrom().getLabel();
                String to = bioEdge.getTo().getLabel();
                sb.append(from).append(";").append(to).append(";");
                sb.append(String.valueOf(bioEdge.isDirected()).toLowerCase(Locale.ROOT));
                sb.append(" \n");
            }
            sb.append("\n");
            // sb.append("#Sequences \n");
            // Iterator it3 = pw.getAllNodes().iterator();
            //
            // while (it3.hasNext()) {
            // Protein bna = (Protein) it3.next();
            // if (bna.getAaSequence().length() > 10) {
            // sb.append(">" + bna.getLabel() + " \n");
            // sb.append(bna.getAaSequence() + " \n");
            // sb.append("\n");
            // }
            // }
            // sb.append("\n");
            os.write(sb.toString().getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "an error occurred!";
        }
        return "";
    }
}
