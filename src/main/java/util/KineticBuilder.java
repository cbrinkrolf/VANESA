package util;

import biologicalElements.Pathway;
import biologicalObjects.edges.Activation;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;
import graph.gui.Parameter;
import gui.PopUpDialog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KineticBuilder {
    // TODO consider only directed edges
    public static String createConvenienceKinetic(BiologicalNodeAbstract bna) {
        StringBuilder sb = new StringBuilder();
        Pathway pw = GraphInstance.getPathway();
        Collection<BiologicalEdgeAbstract> inEdges = pw.getGraph().getJungGraph().getInEdges(bna);
        Collection<BiologicalEdgeAbstract> outEdges = pw.getGraph().getJungGraph().getOutEdges(bna);

        Set<BiologicalEdgeAbstract> substrateEdges = new HashSet<>();
        Set<BiologicalEdgeAbstract> activatorEdges = new HashSet<>();
        Set<BiologicalEdgeAbstract> inhibRelativeEdges = new HashSet<>();
        Set<BiologicalEdgeAbstract> productEdges = new HashSet<>();

        // manage inEdges
        if (inEdges != null) {
            for (BiologicalEdgeAbstract bea : inEdges) {
                if (bea instanceof Activation) {
                    activatorEdges.add(bea);
                } else if (bea instanceof Inhibition) {
                    if (!((Inhibition) bea).isAbsoluteInhibition()) {
                        inhibRelativeEdges.add(bea);
                    }
                } else {
                    substrateEdges.add(bea);
                }
            }
        }
        // manage outEdges
        if (outEdges != null) {
            productEdges.addAll(outEdges);
        }
        // reaction concentration;
        sb.append(bna.getName()).append(" ");
        // activators
        for (BiologicalEdgeAbstract bea : activatorEdges) {
            sb.append("* (1 + (").append(bea.getFrom().getName()).append(" / kA_").append(bea.getFrom().getName());
            sb.append("))");
            addParameter(bna, "kA_" + bea.getFrom().getName(), 1, "mmol/l");
        }
        // relative inhibitors
        for (BiologicalEdgeAbstract bea : inhibRelativeEdges) {
            sb.append("* (kI_").append(bea.getFrom().getName()).append(" / (kI_").append(bea.getFrom().getName());
            sb.append(" + ").append(bea.getFrom().getName()).append("))");
            addParameter(bna, "kI_" + bea.getFrom().getName(), 1, "mmol/l");
        }
        // numerator
        sb.append(" * ( v_f");
        addParameter(bna, "v_f", 1, "1/s");
        int substrates = 0;
        int products = 0;
        for (BiologicalEdgeAbstract bea : substrateEdges) {
            int weight = 1;
            substrates++;
            if (bea.getLabel().length() > 0) {
                try {
                    weight = Integer.parseInt(bea.getLabel());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sb.append(" * ");
            if (weight > 1) {
                sb.append("(");
            }
            sb.append(bea.getFrom().getName()).append(" / km_").append(bea.getFrom().getName());
            addParameter(bna, "km_" + bea.getFrom().getName(), 1, "mmol/l");
            if (weight > 1) {
                sb.append(")^").append(weight);
            }
        }
        sb.append(" - v_r ");
        addParameter(bna, "v_r", 0.1, "1/s");
        for (BiologicalEdgeAbstract bea : productEdges) {
            int weight = 1;
            products++;
            if (bea.getLabel().length() > 0) {
                try {
                    weight = Integer.parseInt(bea.getLabel());
                } catch (Exception e) {
                    e.printStackTrace();
                    PopUpDialog.getInstance().show("Parsing Error",
                                                   "This label cannot be parsed as an integer: " + bea.getLabel() +
                                                   "\r\n");
                }
            }
            sb.append(" * ");
            if (weight > 1) {
                sb.append("(");
            }
            sb.append(bea.getTo().getName()).append(" / km_").append(bea.getTo().getName());
            addParameter(bna, "km_" + bea.getTo().getName(), 1, "mmol/l");
            if (weight > 1) {
                sb.append(")^").append(weight);
            }
        }
        sb.append(") ");
        if (substrates > 0 || products > 0) {
            sb.append("/ (");
            // dominator
            boolean isFirstSubstrateEdge = true;
            for (BiologicalEdgeAbstract bea : substrateEdges) {
                if (!isFirstSubstrateEdge) {
                    sb.append(" * ");
                }
                isFirstSubstrateEdge = false;
                int weight = 1;
                if (bea.getLabel().length() > 0) {
                    try {
                        weight = Integer.parseInt(bea.getLabel());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sb.append("(");
                for (int i = 0; i <= weight; i++) {
                    if (i == 0) {
                        sb.append(" 1 ");
                    } else if (i == 1) {
                        sb.append(" + ").append(bea.getFrom().getName()).append(" / km_");
                        sb.append(bea.getFrom().getName());
                    } else {
                        sb.append(" + (").append(bea.getFrom().getName()).append(" / km_");
                        sb.append(bea.getFrom().getName()).append(")^").append(weight);
                    }
                }
                sb.append(")");
            }
            if (substrates > 0 && products > 0) {
                sb.append(" + ");
            }
            boolean isFirstProductEdge = true;
            for (BiologicalEdgeAbstract bea : productEdges) {
                if (!isFirstProductEdge) {
                    sb.append(" * ");
                }
                isFirstProductEdge = false;
                int weight = 1;
                if (bea.getLabel().length() > 0) {
                    try {
                        weight = Integer.parseInt(bea.getLabel());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sb.append("(");
                for (int i = 0; i <= weight; i++) {
                    if (i == 0) {
                        sb.append(" 1 ");
                    } else if (i == 1) {
                        sb.append(" + ").append(bea.getTo().getName()).append(" / km_").append(bea.getTo().getName());
                    } else {
                        sb.append(" + (").append(bea.getTo().getName()).append(" / km_").append(bea.getTo().getName());
                        sb.append(")^").append(weight);
                    }
                }
                sb.append(")");
            }
            sb.append(" - 1)");
        }
        return sb.toString();
    }

    private static void addParameter(BiologicalNodeAbstract bna, String name, double value, String unit) {
        if (bna.getParameter(name) == null) {
            bna.getParameters().add(new Parameter(name, value, unit));
        }
    }
}
