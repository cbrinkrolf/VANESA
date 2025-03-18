package database.mirna;

import api.VanesaApi;
import api.payloads.Response;
import api.payloads.dbMirna.*;
import biologicalElements.Pathway;
import biologicalObjects.edges.Expression;
import biologicalObjects.edges.PhysicalInteraction;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.MIRNA;
import com.fasterxml.jackson.core.type.TypeReference;
import graph.layouts.Circle;
import gui.PopUpDialog;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;

public class MirnaSearch {
    private MirnaSearch() {
    }

    public static Response<MatureSearchResponsePayload> searchMatures(boolean hsaOnly, String matureName,
                                                                      String accession, String sequence) {
        MatureSearchRequestPayload payload = new MatureSearchRequestPayload();
        payload.hsaOnly = hsaOnly;
        payload.name = matureName;
        payload.accession = accession;
        payload.sequence = sequence;
        return VanesaApi.postSync("/db_mirna/mature/search", payload, new TypeReference<>() {
        });
    }

    public static Response<MatureSourceGenesResponsePayload> retrieveMatureSourceGenes(boolean hsaOnly,
                                                                                       String matureName) {
        MatureSourceGenesRequestPayload requestPayload = new MatureSourceGenesRequestPayload();
        requestPayload.hsaOnly = hsaOnly;
        requestPayload.name = matureName;
        return VanesaApi.postSync("/db_mirna/mature/source_genes", requestPayload, new TypeReference<>() {
        });
    }

    public static Response<MatureTargetGenesResponsePayload> retrieveMatureTargetGenes(boolean hsaOnly,
                                                                                       String matureName) {
        MatureTargetGenesRequestPayload requestPayload = new MatureTargetGenesRequestPayload();
        requestPayload.hsaOnly = hsaOnly;
        requestPayload.name = matureName;
        return VanesaApi.postSync("/db_mirna/mature/target_genes", requestPayload, new TypeReference<>() {
        });
    }

    public static Response<TargetGeneSearchResponsePayload> searchTargetGenes(boolean hsaOnly, String geneName) {
        TargetGeneSearchRequestPayload payload = new TargetGeneSearchRequestPayload();
        payload.hsaOnly = hsaOnly;
        payload.accession = geneName;
        return VanesaApi.postSync("/db_mirna/target_gene/search", payload, new TypeReference<>() {
        });
    }

    public static Response<SourceGeneMaturesResponsePayload> retrieveSourceGeneMatures(boolean hsaOnly,
                                                                                       String geneName) {
        SourceGeneMaturesRequestPayload requestPayload = new SourceGeneMaturesRequestPayload();
        requestPayload.hsaOnly = hsaOnly;
        requestPayload.name = geneName;
        return VanesaApi.postSync("/db_mirna/source_gene/matures", requestPayload, new TypeReference<>() {
        });
    }

    public static Response<TargetGeneMaturesResponsePayload> retrieveTargetGeneMatures(boolean hsaOnly,
                                                                                       String geneName) {
        TargetGeneMaturesRequestPayload requestPayload = new TargetGeneMaturesRequestPayload();
        requestPayload.hsaOnly = hsaOnly;
        requestPayload.name = geneName;
        return VanesaApi.postSync("/db_mirna/target_gene/matures", requestPayload, new TypeReference<>() {
        });
    }

    public static void enrichGenes(Pathway pw, boolean sources, boolean targets, boolean hsaOnly) {
        if (sources) {
            enrichTargetGeneMatures(pw, hsaOnly);
        }
        if (targets) {
            enrichSourceGeneMatures(pw, hsaOnly);
        }
    }

    public static void enrichMirnas(Pathway pw, boolean sources, boolean targets, boolean hsaOnly) {
        if (sources) {
            enrichMiRNASourceGenes(pw, hsaOnly);
        }
        if (targets) {
            enrichMiRNATargetGenes(pw, hsaOnly);
        }
    }

    private static void enrichTargetGeneMatures(Pathway pw, boolean hsaOnly) {
        HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<>();
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            bnas.put(bna.getLabel(), bna);
        }
        Collection<BiologicalNodeAbstract> nodes = pw.getGraph2().getSelectedNodes();
        if (nodes.isEmpty()) {
            nodes = pw.getAllGraphNodes();
        }
        HashMap<BiologicalNodeAbstract, DBMirnaMature[]> data = new HashMap<>();
        for (BiologicalNodeAbstract bna : nodes) {
            Response<TargetGeneMaturesResponsePayload> response = retrieveTargetGeneMatures(hsaOnly, bna.getLabel());
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                data.put(bna, response.payload.results);
            }
        }
        int counterNodes = 0;
        int counterEdges = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            for (DBMirnaMature mature : data.get(bna)) {
                BiologicalNodeAbstract tmp;
                if (bnas.containsKey(mature.name)) {
                    tmp = bnas.get(mature.name);
                } else {
                    tmp = new MIRNA(mature.name, mature.name, pw);
                    addVertexOnCircle(pw, bna, tmp);
                    bnas.put(mature.name, tmp);
                    counterNodes++;
                }
                if (!pw.containsEdge(tmp, bna)) {
                    PhysicalInteraction exp = new PhysicalInteraction("", "", tmp, bna);
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateLayout();
        PopUpDialog.getInstance().show("Target Gene Enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }

    private static void addVertexOnCircle(Pathway pw, BiologicalNodeAbstract parent, BiologicalNodeAbstract child) {
        Point2D p = Circle.getPointOnCircle(pw.getGraph2().getNodePosition(parent), 20,
                2 * Math.random() * Math.PI);
        pw.addVertex(child, p);
    }

    private static void enrichSourceGeneMatures(Pathway pw, boolean hsaOnly) {
        HashMap<String, BiologicalNodeAbstract> bnas = new HashMap<>();
        for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
            bnas.put(bna.getLabel(), bna);
        }
        Collection<BiologicalNodeAbstract> nodes = pw.getGraph2().getSelectedNodes();
        if (nodes.isEmpty()) {
            nodes = pw.getAllGraphNodes();
        }
        HashMap<BiologicalNodeAbstract, DBMirnaMature[]> data = new HashMap<>();
        for (BiologicalNodeAbstract bna : nodes) {
            Response<SourceGeneMaturesResponsePayload> response = retrieveSourceGeneMatures(hsaOnly, bna.getLabel());
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                data.put(bna, response.payload.results);
            }
        }
        int counterEdges = 0;
        int counterNodes = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            for (DBMirnaMature mature : data.get(bna)) {
                BiologicalNodeAbstract tmp;
                if (bnas.containsKey(mature.name)) {
                    tmp = bnas.get(mature.name);
                } else {
                    tmp = new MIRNA(mature.name, mature.name, pw);
                    addVertexOnCircle(pw, bna, tmp);
                    bnas.put(mature.name, tmp);
                    counterNodes++;
                }
                if (!pw.containsEdge(bna, tmp)) {
                    Expression exp = new Expression("", "", bna, tmp);
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateLayout();
        PopUpDialog.getInstance().show("Source Gene Enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }

    private static void enrichMiRNASourceGenes(Pathway pw, boolean hsaOnly) {
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
        HashMap<BiologicalNodeAbstract, DBMirnaSourceGene[]> data = new HashMap<>();
        for (BiologicalNodeAbstract bna : nodes) {
            Response<MatureSourceGenesResponsePayload> response = retrieveMatureSourceGenes(hsaOnly, bna.getLabel());
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                data.put(bna, response.payload.results);
            }
        }
        int counterEdges = 0;
        int counterNodes = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            for (DBMirnaSourceGene sourceGene : data.get(bna)) {
                BiologicalNodeAbstract tmp;
                String label = sourceGene.getAccession();
                if (bnas.containsKey(sourceGene.name)) {
                    tmp = bnas.get(label);
                } else {
                    tmp = new DNA(label, sourceGene.name != null ? sourceGene.name : label, pw);
                    addVertexOnCircle(pw, bna, tmp);
                    bnas.put(label, tmp);
                    counterNodes++;
                }
                if (!pw.containsEdge(tmp, bna)) {
                    Expression exp = new Expression("", "", tmp, bna);
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateLayout();
        PopUpDialog.getInstance().show("miRNA Source Enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }

    private static void enrichMiRNATargetGenes(Pathway pw, boolean hsaOnly) {
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
            Response<MatureTargetGenesResponsePayload> response = retrieveMatureTargetGenes(hsaOnly, bna.getLabel());
            if (response.payload != null && response.payload.results != null && response.payload.results.length > 0) {
                data.put(bna, response.payload.results);
            }
        }
        int counterEdges = 0;
        int counterNodes = 0;
        for (BiologicalNodeAbstract bna : data.keySet()) {
            for (DBMirnaTargetGene targetGene : data.get(bna)) {
                BiologicalNodeAbstract tmp;
                String label = targetGene.getAccession();
                if (bnas.containsKey(label)) {
                    tmp = bnas.get(label);
                } else {
                    tmp = new DNA(label, targetGene.name != null ? targetGene.name : label, pw);
                    addVertexOnCircle(pw, bna, tmp);
                    bnas.put(label, tmp);
                    counterNodes++;
                }
                if (!pw.containsEdge(bna, tmp)) {
                    PhysicalInteraction exp = new PhysicalInteraction("", "", bna, tmp);
                    exp.setDirected(true);
                    pw.addEdge(exp);
                    pw.addEdgeToView(exp, true);
                    counterEdges++;
                }
            }
        }
        pw.getGraph().updateLayout();
        PopUpDialog.getInstance().show("miRNA Target Enrichment",
                counterNodes + " nodes and " + counterEdges + " edges have been added!");
    }
}
