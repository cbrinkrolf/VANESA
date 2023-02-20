package database.mirna;

import api.payloads.Response;
import api.payloads.dbMirna.DBMirnaMature;
import api.payloads.dbMirna.DBMirnaTargetGene;
import api.payloads.dbMirna.GenesTargetedByMatureResponsePayload;
import api.payloads.dbMirna.MaturesTargetingGeneResponsePayload;
import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.MIRNA;
import configurations.Wrapper;
import database.mirna.gui.MirnaQueryMask;
import graph.layouts.Circle;
import gui.MyPopUp;
import pojos.DBColumn;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MirnaStatistics {
    private final Pathway pw;

    public MirnaStatistics(Pathway pw) {
        this.pw = pw;
    }

    public void enrichGenes(boolean sources, boolean targets, boolean hsaOnly) {
        if (sources) {
            enrichGeneSources(hsaOnly);
        }
        if (targets) {
            enrichGeneTargets(hsaOnly);
        }
    }

    public void enrichMirnas(boolean sources, boolean targets, boolean hsaOnly) {
        if (sources) {
            enrichMiRNASources(hsaOnly);
        }
        if (targets) {
            enrichMiRNATargets(hsaOnly);
        }
    }

    private void enrichGeneSources(boolean hsaOnly) {
        HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<>();
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            bnas.put(bna.getLabel(), bna);
        }
        Collection<BiologicalNodeAbstract> nodes;
        if (pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().size() > 0) {
            nodes = pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked();
        } else {
            nodes = pw.getAllGraphNodes();
        }
        HashMap<BiologicalNodeAbstract, DBMirnaMature[]> data = new HashMap<>();
        for (BiologicalNodeAbstract bna : nodes) {
            Response<MaturesTargetingGeneResponsePayload> response =
                    MirnaQueryMask.retrieveMaturesTargetingGene(hsaOnly, bna.getLabel());
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                data.put(bna, response.payload.results);
            }
        }
        int counterNodes = 0;
        int counterEdges = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            DBMirnaMature[] resultsDBSearch = data.get(bna);
            for (DBMirnaMature mature : resultsDBSearch) {
                BiologicalNodeAbstract tmp;
                if (bnas.containsKey(mature.name)) {
                    tmp = bnas.get(mature.name);
                } else {
                    tmp = new MIRNA(mature.name, mature.name);
                    Point2D p = Circle.getPointOnCircle(pw.getGraph().getVertexLocation(bna), 20, 2.0 * (Math.random() % Math.PI));
                    pw.addVertex(tmp, p);
                    bnas.put(mature.name, tmp);
                    counterNodes++;
                }
                if (!pw.existEdge(tmp, bna)) {
                    Expression exp = new Expression("", "", tmp, bna); // TODO: This is not an expression???
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateGraph();
        pw.getGraph().updateLayout();
        MyPopUp.getInstance().show("miRNA target enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }

    private void enrichGeneTargets(boolean hsaOnly) {
        HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<>();
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            bnas.put(bna.getLabel(), bna);
        }
        Collection<BiologicalNodeAbstract> nodes;
        if (pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().size() > 0) {
            nodes = pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked();
        } else {
            nodes = pw.getAllGraphNodes();
        }
        HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>> data = new HashMap<>();
        for (BiologicalNodeAbstract bna : nodes) {
            String finalQueryString = miRNAqueries.miRNA_get_SourcingMirnas.replaceFirst("\\?", "'" + bna.getLabel() + "'");
            if (hsaOnly) {
                finalQueryString = finalQueryString.substring(0, finalQueryString.length() - 2);
                finalQueryString += "AND Hairpins.SpeciesID=54;";
            }
            ArrayList<DBColumn> resultsDBSearch = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
            if (resultsDBSearch.size() > 0) {
                data.put(bna, resultsDBSearch);
            }
        }
        int counterEdges = 0;
        int counterNodes = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            ArrayList<DBColumn> resultsDBSearch = data.get(bna);
            for (DBColumn dbSearch : resultsDBSearch) {
                String[] column = dbSearch.getColumn();
                BiologicalNodeAbstract tmp;
                if (bnas.containsKey(column[0])) {
                    tmp = bnas.get(column[0]);
                } else {
                    tmp = new MIRNA(column[0], column[0]);
                    Point2D p = Circle.getPointOnCircle(pw.getGraph().getVertexLocation(bna), 20, 2.0 * (Math.random() % Math.PI));
                    pw.addVertex(tmp, p);
                    bnas.put(column[0], tmp);
                    counterNodes++;
                }
                if (!pw.existEdge(bna, tmp)) {
                    Expression exp = new Expression("", "", bna, tmp);
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateGraph();
        pw.getGraph().updateLayout();
        MyPopUp.getInstance().show("miRNA source enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }

    private void enrichMiRNASources(boolean hsaOnly) {
        HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<>();
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            bnas.put(bna.getLabel(), bna);
        }
        Collection<BiologicalNodeAbstract> nodes;
        if (pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().size() > 0) {
            nodes = pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked();
        } else {
            nodes = pw.getAllGraphNodes();
        }
        HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>> data = new HashMap<>();
        for (BiologicalNodeAbstract bna : nodes) {
            String finalQueryString = miRNAqueries.miRNA_get_SourceGenes.replaceFirst("\\?", "'" + bna.getLabel() + "'");
            if (hsaOnly) {
                finalQueryString = finalQueryString.substring(0, finalQueryString.length() - 2);
                finalQueryString += "AND Hairpins.SpeciesID=54;";
            }
            ArrayList<DBColumn> resultsDBSearch = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
            if (resultsDBSearch.size() > 0) {
                data.put(bna, resultsDBSearch);
            }
        }
        int counterEdges = 0;
        int counterNodes = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            ArrayList<DBColumn> resultsDBSearch = data.get(bna);
            for (DBColumn dbSearch : resultsDBSearch) {
                String[] column = dbSearch.getColumn();
                BiologicalNodeAbstract tmp;
                if (bnas.containsKey(column[0])) {
                    tmp = bnas.get(column[0]);
                } else {
                    tmp = new DNA(column[0], column[0]);
                    Point2D p = Circle.getPointOnCircle(pw.getGraph().getVertexLocation(bna), 20, 2.0 * (Math.random() % Math.PI));
                    pw.addVertex(tmp, p);
                    bnas.put(column[0], tmp);
                    counterNodes++;
                }
                if (!pw.existEdge(tmp, bna)) {
                    Expression exp = new Expression("", "", tmp, bna);
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateGraph();
        pw.getGraph().updateLayout();
        MyPopUp.getInstance().show("miRNA target enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }

    private void enrichMiRNATargets(boolean hsaOnly) {
        HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<>();
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            bnas.put(bna.getLabel(), bna);
        }
        Collection<BiologicalNodeAbstract> nodes;
        if (pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().size() > 0) {
            nodes = pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked();
        } else {
            nodes = pw.getAllGraphNodes();
        }
        HashMap<BiologicalNodeAbstract, DBMirnaTargetGene[]> data = new HashMap<>();
        for (BiologicalNodeAbstract bna : nodes) {
            Response<GenesTargetedByMatureResponsePayload> response =
                    MirnaQueryMask.retrieveGenesTargetedByMature(hsaOnly, bna.getLabel());
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                data.put(bna, response.payload.results);
            }
        }
        int counterEdges = 0;
        int counterNodes = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            for (DBMirnaTargetGene targetGene : data.get(bna)) {
                BiologicalNodeAbstract tmp;
                if (bnas.containsKey(targetGene.accession)) {
                    tmp = bnas.get(targetGene.accession);
                } else {
                    tmp = new DNA(targetGene.accession, targetGene.accession);
                    Point2D p = Circle.getPointOnCircle(pw.getGraph().getVertexLocation(bna), 20, 2.0 * (Math.random() % Math.PI));
                    pw.addVertex(tmp, p);
                    bnas.put(targetGene.accession, tmp);
                    counterNodes++;
                }
                if (!pw.existEdge(bna, tmp)) {
                    Expression exp = new Expression("", "", bna, tmp); // TODO: This is not an expression???
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateGraph();
        pw.getGraph().updateLayout();
        MyPopUp.getInstance().show("miRNA target enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }
}
