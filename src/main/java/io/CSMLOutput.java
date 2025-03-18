package io;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import biologicalElements.ElementDeclarations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;

/**
 * CSML 3.0
 *
 * @author Rafael, cbrinkro
 */
public class CSMLOutput extends BaseWriter<Pathway> {
    private int connector = 0;
    private double xmin = 1000, xmax = -1000, ymin = 1000, ymax = -1000;
    private final double scale = 1;
    private final Map<BiologicalNodeAbstract, String> edgesString = new HashMap<>();
    private final Map<BiologicalNodeAbstract, Point2D> placePositions = new HashMap<>();
    private final Map<BiologicalNodeAbstract, Point2D> transitionPositions = new HashMap<>();

    public CSMLOutput(File file) {
        super(file);
    }

    @Override
    protected void internalWrite(OutputStream outputStream, Pathway pw) throws Exception {
        prepare(pw);
        buildConnections(pw);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n");
        sb.append("<csml:project xmlns:csml=\"http://www.csml.org/csml/version3\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" majorVersion=\"3\" minorVersion=\"0\" projectID=\"local\" projectVersionID=\"undef\">\n");
        sb.append("  <csml:model modelID=\"undef\" modelVersionID=\"undef\">\n");
        sb.append("    <csml:entitySet>\n");
        for (BiologicalNodeAbstract bna : placePositions.keySet()) {
            sb.append(getPlaceString((Place) bna));
        }
        sb.append("    </csml:entitySet>\n");
        sb.append("    <csml:processSet>\n");
        for (BiologicalNodeAbstract bna : transitionPositions.keySet()) {
            appendTransition(sb, bna);
        }
        sb.append("    </csml:processSet>\n");
        sb.append("  </csml:model>\n");
        sb.append("  <csml:viewSet>\n");
        sb.append("    <csml:view name=\"Default View\" refAnimationID=\"default\" refModelID=\"undef\" refPositionID=\"default\" refShapeID=\"default\" viewID=\"default\">\n");
        sb.append("    </csml:view>\n");
        sb.append("  </csml:viewSet>\n");
        sb.append("</csml:project>\n");
        outputStream.write(sb.toString().getBytes());
    }

    private void prepare(Pathway pw) {
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            Point2D p = pw.getGraph2().getNodePosition(bna);
            if (bna instanceof Transition) {
                transitionPositions.put(bna, p);
            } else {
                placePositions.put(bna, p);
            }
            xmin = Math.min(xmin, p.getX());
            xmax = Math.max(xmax, p.getX());
            ymin = Math.min(ymin, p.getY());
            ymax = Math.max(ymax, p.getY());
        }
        xmin -= 20;
        ymin -= 20;
    }

    private void buildConnections(Pathway pw) {
        for (BiologicalEdgeAbstract bea : pw.getAllEdges()) {
            // get name and position and type
            BiologicalNodeAbstract from = bea.getFrom();
            BiologicalNodeAbstract to = bea.getTo();
            String first = from.getBiologicalElement();
            if (first.contains("Place")) {
                edge(to, from, "InputProcessBiological");
            } else if (first.contains("Transition")) {
                edge(from, to, "OutputProcessBiological");
            }
        }
    }

    private void edge(BiologicalNodeAbstract transition, BiologicalNodeAbstract place, String type) {
        String connection = getConnectionString(place, type);
        if (!edgesString.containsKey(transition)) {
            edgesString.put(transition, connection);
        } else {
            edgesString.put(transition, edgesString.get(transition) + connection);
        }
    }

    private String getPlaceString(Place place) {
        Point2D p = placePositions.get(place);
        String type = "";
        if (place.getBiologicalElement().equals(ElementDeclarations.discretePlace)) {
            type = "Integer";
        } else if (place.getBiologicalElement().equals(ElementDeclarations.continuousPlace)) {
            type = "Double";
        }
        String start = String.valueOf(place.getTokenStart());
        String min = String.valueOf(place.getTokenMin());
        String max = place.getTokenMax() < 0 ? "infinite" : String.valueOf(place.getTokenMax());
        return "  <csml:entity id=\""
                + place.getID()
                + "\" name=\""
                + place.getName()
                + "\" type=\"cso:-\">\n"
                + "<csml:entitySimulationProperty>\n"
                + " <csml:variable type = \"csml-variable:" + type + "\" variableID=\""
                + place.getLabel()
                + "\">\n"
                + "  <csml:parameter key=\"csml-variable:parameter:initialValue\" value=\"" + start + "\">\n"
                + "  </csml:parameter>\n"
                + "  <csml:parameter key=\"csml-variable:parameter:maximumValue\" value=\"" + max + "\">\n"
                + "  </csml:parameter>\n"
                + "  <csml:parameter key=\"csml-variable:parameter:minimumValue\" value=\"" + min + "\">\n"
                + "  </csml:parameter>\n"
                + "  <csml:parameter key=\"csml-variable:parameter:unit\" value=\"unit\">\n"
                + "  </csml:parameter>\n"
                + "  <csml:parameter key=\"csml-variable:parameter:global\" value=\"false\">\n"
                + "  </csml:parameter>\n"
                + "  <csml:parameter key=\"csml-variable:parameter:evaluateScriptOnce\" value=\"true\">\n"
                + "  </csml:parameter>\n"
                + " </csml:variable>\n"
                + "</csml:entitySimulationProperty>\n"
                + "<csml:viewProperty>\n"
                + " <csml:position position=\"rotation:0.0\" positionID=\"default\" x=\""
                + Math.floor(scale * (p.getX() - xmin)) + "\" y=\""
                + Math.floor(scale * (p.getY() - ymin)) + "\">\n"
                + " </csml:position>\n"
                + " <csml:shape shapeID=\"default\" visible=\"true\">\n"
                + " </csml:shape>\n" + "</csml:viewProperty>\n"
                + "<csml:biologicalProperty refCellComponentID=\"-\">"
                + " <csml:property key=\"extension\" value=\"\">"
                + " </csml:property>"
                + " <csml:property key=\"accession\" value=\"\">"
                + " </csml:property>"
                + " <csml:property key=\"probeID\" value=\"\">"
                + " </csml:property>" + "</csml:biologicalProperty>"
                + "<csml:comments>" + "<csml:comment type=\"text\">"
                + "</csml:comment>" + "</csml:comments>" +
                "  </csml:entity>\n";
    }

    private void appendTransition(StringBuilder sb, BiologicalNodeAbstract bna) {
        sb.append(getTransitionHeader(bna));
        String edgeString = edgesString.get(bna);
        if (edgeString != null) {
            sb.append(edgeString);
        }
        // outEdgesString.get(name)==null?"":outEdgesString.get(name))
        appendTransitionTail(sb, transitionPositions.get(bna));
    }

    private String getTransitionHeader(BiologicalNodeAbstract bna) {
        String type = "";
        if (bna instanceof DiscreteTransition) {
            type = "Discrete";
        } else if (bna instanceof ContinuousTransition) {
            type = "Continuous";
        } else if (bna instanceof StochasticTransition) {
            type = "Stochastic";
        }
        return "  <csml:process id=\"" + bna.getID() + "\" name=\""
                + (bna.getLabel().equals("") ? " " : bna.getLabel())
                //CHRIS Transitiontype Discrete vs Continuous
                + "\" type=\"" + type + "\">\n";
    }

    private String getConnectionString(BiologicalNodeAbstract place, String type) {
        //CHRIS no kinetic information
        return "<csml:connector id=\"c"
                + (++connector)
                + "\" name=\"c"
                + connector
                + "\" refID=\""
                + place.getID()
                + "\" type=\""
                + type
                + "\">\n"
                + "<csml:connectorSimulationProperty>\n"
                + " <csml:connectorFiring connectorFiringStyle=\"csml-connectorFiringStyle:threshold\" value=\"5\">\n"
                + " </csml:connectorFiring>\n"
                + " <csml:connectorKinetic>\n"
                + "  <csml:parameter key=\"stoichiometry\" value=\"3.0\">\n"
                + "  </csml:parameter>\n"
                + "  <csml:parameter key=\"custom\" value=\"2.0\">\n"
                + "  </csml:parameter>\n"
                + " </csml:connectorKinetic>\n"
                + "</csml:connectorSimulationProperty>\n"
                + "<csml:comments>" + " <csml:comment type=\"text\">\n"
                + " </csml:comment>\n" + "</csml:comments>\n"
                + "</csml:connector>\n";
    }

    private void appendTransitionTail(StringBuilder sb, Point2D p) {
        sb.append("      <csml:processSimulationProperty>\n");
        sb.append("        <csml:priority value=\"0\"/>\n");
        sb.append("        <csml:firing firingOnce=\"false\" firingStyle=\"csml-firingStyle:and\" type=\"csml-variable:Boolean\" value=\"true\"/>\n");
        sb.append("        <csml:delay delayStyle=\"nodelay\" value=\"0.0\"/>\n");
        sb.append("        <csml:processKinetic calcStyle=\"csml-calcStyle:speed\" fast=\"false\" kineticStyle=\"csml-kineticStyle:custom\">\n");
        sb.append("          <csml:parameter key=\"custom\" value=\"1.0\">\n");
        sb.append("          </csml:parameter>\n");
        sb.append("        </csml:processKinetic>\n");
        sb.append("      </csml:processSimulationProperty>\n");
        sb.append("      <csml:viewProperty>\n");
        sb.append("        <csml:position position=\"rotation:0.0\" positionID=\"default\"");
        sb.append(" x=\"").append(Math.floor(scale * (p.getX() - xmin))).append('"');
        sb.append(" y=\"").append(Math.floor(scale * (p.getY() - ymin))).append('"');
        sb.append(">\n");
        sb.append("        </csml:position>\n");
        sb.append("        <csml:shape shapeID=\"default\" visible=\"true\">\n");
        sb.append("        </csml:shape>\n");
        sb.append("      </csml:viewProperty>\n");
        sb.append("      <csml:biologicalProperty>\n");
        sb.append("      </csml:biologicalProperty>\n");
        sb.append("      <csml:comments>\n");
        sb.append("        <csml:comment type=\"text\">\n");
        sb.append("        </csml:comment>\n");
        sb.append("      </csml:comments>\n");
        sb.append("    </csml:process>\n");
    }
}
