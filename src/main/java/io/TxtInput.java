package io;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Protein;
import graph.CreatePathway;
import gui.MainWindow;

public class TxtInput {
    private enum ReadingState {
        initial,
        nodes,
        edges,
        sequences
    }

    private final Hashtable<String, BiologicalNodeAbstract> mapping = new Hashtable<>();
    private final Pathway pw = new CreatePathway().getPathway();

    public TxtInput(InputStream is, File file) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            pw.setFile(file);
            ReadingState state = ReadingState.initial;
            String line;
            while ((line = in.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.equals("#Edges")) {
                    state = ReadingState.edges;
                    continue;
                }
                if (trimmedLine.equals("#Nodes")) {
                    state = ReadingState.nodes;
                    continue;
                }
                if (trimmedLine.equals("#Sequences")) {
                    state = ReadingState.sequences;
                    continue;
                }
                switch (state) {
                    case edges:
                        manageEdge(line);
                        break;
                    case nodes:
                        manageNode(line);
                        break;
                    case sequences:
                        manageSequence(line, in);
                        break;
                }
            }
        }
        is.close();
        pw.getGraph().changeToCircleLayout();
        pw.getGraph().restartVisualizationModel();
        pw.getGraph().normalCentering();
        MainWindow.getInstance().updateProjectProperties();
    }

    /**
     * separates label and name from an input String with a "#" delimiter
     *
     * @param token form a StringTokenizer or a line from an input file
     * @return String[] with the label and the name
     */
    private String[] getLabelName(String token) {
        String[] labeling = new String[2];
        StringTokenizer st = new StringTokenizer(token.trim(), "#");
        if (st.countTokens() == 2) {
            while (st.hasMoreTokens()) {
                labeling[0] = st.nextToken().trim();
                labeling[1] = st.nextToken().trim();
            }
        }
        if (st.countTokens() == 1) {
            while (st.hasMoreTokens()) {
                labeling[0] = st.nextToken().trim();
                labeling[1] = labeling[0];
            }
        }
        return labeling;
    }

    /**
     * manage the input from the "#Nodes" lines
     */
    private void manageNode(String line) {
        if (line.trim().indexOf(';') > 0 || line.trim().indexOf('#') > 0) {
            //label and name or label and FC or label, name and FC
            if (line.trim().indexOf(';') > 0) { // line with FC
                StringTokenizer st = new StringTokenizer(line.trim(), ";");
                while (st.hasMoreTokens()) {
                    String first = st.nextToken().trim();
                    //String next = st.nextToken().trim();
					/*int nodeRanking = 0;
					if (st.countTokens() == 3) {
						nodeRanking = Integer.valueOf(st.nextToken().trim());
					}*/
                    String label = first;
                    String name = first;
                    if (first.trim().indexOf("#") > 0) {
                        label = getLabelName(first)[0];
                        name = getLabelName(first)[1];
                    }
                    Protein p = new Protein(label, name);
					/*MicroArrayAttributes microArray = new MicroArrayAttributes();
					Double foldchange = Double.valueOf(next);
					microArray.setFoldschange(foldchange);
					microArray.setRanking(nodeRanking);
					p.setMicroarrayAttributes(microArray);
					int red = 0;
					int green = 0;
					int blue = 0;
					if (foldchange >= 0.8 && foldchange <= 1.2) {
						//do nothing, the nodes will be black, which means there is no change in FC
						//(black is the corresponding colour to the heatmap colour coding of a middel expression)
					} else if (foldchange > 1.2) {
						//color coding from 1.2 to 3 from dark red to light red
						red = (int) (85 * foldchange);
						if (red > 255)
							red = 255;
					} else if (foldchange < 0.8) {
						//color coding from 0.8 to 0.333 from dark green to light green
						green = (int) ((1/foldchange) * 85);
						if (green > 255)
							green = 255;
					}
					Color colour = new Color(red, green, blue);
					p.setColor(colour);*/
                    pw.addVertex(p, new Point2D.Double(10, 10));
                    //pw.getGraph().moveVertex(p.getVertex(), 10, 10);
                    mapping.put(p.getLabel(), p);
                }
            }
            if (line.trim().indexOf(';') < 0 && line.trim().indexOf('#') > 0) {
                //no microarray FC data
                String label = getLabelName(line)[0];
                String name = getLabelName(line)[1];
                Protein p = new Protein(label, name);
                pw.addVertex(p, new Point2D.Double(10, 10));
                //pw.getGraph().moveVertex(p.getVertex(), 10, 10);
                mapping.put(p.getLabel(), p);
            }
        } else {
            Protein p = new Protein(line.trim(), line.trim());
            pw.addVertex(p, new Point2D.Double(10, 10));
            //pw.getGraph().moveVertex(p.getVertex(), 10, 10);
            mapping.put(p.getLabel(), p);
        }
    }

    /**
     * manage the input from the "#Edges" lines
     */
    private void manageEdge(String line) {
        if (line.trim().indexOf(";") > 0) {
            StringTokenizer st = new StringTokenizer(line.trim(), ";");
            boolean isDir = false;
            while (st.hasMoreTokens()) {
                String first = st.nextToken().trim();
                String next = st.nextToken().trim();
                if (st.countTokens() > 0) {
                    // "boolean String" whether the edge is or is not directed
                    String isdirected = st.nextToken().trim();
                    isDir = isdirected.equalsIgnoreCase("true");
                }
                if (mapping.containsKey(first) && mapping.containsKey(next)) {
                    ReactionEdge reactionedge = new ReactionEdge("", "", mapping.get(first), mapping.get(next));
                    if (isDir) {
                        reactionedge.setDirected(true); //otherwise you will not get a directed edge in the vaml file
                    }
                    pw.addEdge(reactionedge);
                }
            }
        }
    }

    /**
     * manage the input from the "#Sequences" lines
     */
    private void manageSequence(String line, BufferedReader in) throws IOException {
        if (!line.trim().isEmpty()) {
            Protein bna = null;
            if (line.trim().contains(">")) {
                //map the name to the protein
                StringTokenizer st = new StringTokenizer(line.trim(), ">");
                while (st.hasMoreTokens()) {
                    bna = (Protein) pw.getNodeByLabel(st.nextToken().trim());
                }
            }
            line = in.readLine();
            //add the sequence to the protein
            StringTokenizer seq = new StringTokenizer(line.trim());
            while (seq.hasMoreTokens()) {
                bna.setAaSequence(seq.nextToken().trim());
            }
        }
    }
}
