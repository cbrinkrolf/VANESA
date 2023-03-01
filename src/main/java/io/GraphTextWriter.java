package io;

import java.io.File;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Locale;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class GraphTextWriter extends BaseWriter<Pathway> {
    public GraphTextWriter(File file) {
        super(file);
    }

    @Override
    protected void internalWrite(OutputStream outputStream, Pathway pw) throws Exception {
        HashSet<String> nodes = new HashSet<>();
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
        outputStream.write(sb.toString().getBytes());
    }
}
