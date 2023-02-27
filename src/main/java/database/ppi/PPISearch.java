package database.ppi;

import api.VanesaApi;
import api.payloads.Response;
import api.payloads.ppi.*;
import biologicalElements.Pathway;
import biologicalObjects.edges.PhysicalInteraction;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Protein;
import com.fasterxml.jackson.core.type.TypeReference;
import database.ppi.gui.HPRDSearchResultWindow;
import database.ppi.gui.IntActSearchResultWindow;
import database.ppi.gui.MintSearchResultWindow;
import graph.CreatePathway;
import graph.hierarchies.HierarchyList;
import graph.hierarchies.HierarchyListComparator;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MyPopUp;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class PPISearch {
    private PPISearch() {
    }

    public static void requestHPRDEntries(String fullName, String alias, String accession) {
        HPRDEntrySearchRequestPayload payload = new HPRDEntrySearchRequestPayload();
        payload.name = fullName;
        payload.accession = accession;
        payload.alias = alias;
        Response<HPRDEntrySearchResponsePayload> response = VanesaApi.postSync("/ppi/hprd_entry/search", payload,
                                                                               new TypeReference<>() {
                                                                               });
        MainWindow.getInstance().closeProgressBar();
        if (response.hasError()) {
            MyPopUp.getInstance().show("HPRD search", "Sorry, no entries have been found.\n" + response.error);
            return;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            MyPopUp.getInstance().show("HPRD search", "Sorry, no entries have been found.");
            return;
        }
        HPRDSearchResultWindow searchResultWindow = new HPRDSearchResultWindow(response.payload.results);
        if (!searchResultWindow.show()) {
            return;
        }
        HPRDEntry[] results = searchResultWindow.getSelectedValues();
        if (results.length > 0) {
            MainWindow.getInstance().showProgressBar("Retrieving PPI Network(s)");
            int depth = searchResultWindow.getSearchDepth();
            boolean autoCoarse = searchResultWindow.getAutoCoarse();
            for (HPRDEntry entry : results) {
                requestHPRDPPI(entry, depth, autoCoarse);
            }
            MainWindow.getInstance().closeProgressBar();
        }
    }

    private static void requestHPRDPPI(HPRDEntry root, int depth, boolean autoCoarse) {
        HPRDRetrievePPIRequestPayload payload = new HPRDRetrievePPIRequestPayload();
        payload.id = root.id;
        payload.depth = depth;
        Response<HPRDRetrievePPIResponsePayload> response = VanesaApi.postSync("/ppi/hprd_entry/ppi", payload,
                                                                               new TypeReference<>() {
                                                                               });
        MainWindow.getInstance().closeProgressBar();
        if (response.hasError()) {
            MyPopUp.getInstance().show("HPRD search", "Failed to retrieve PPI network.\n" + response.error);
            return;
        }
        if (response.payload == null || response.payload.entries == null || response.payload.entries.length <= 1) {
            MyPopUp.getInstance().show("HPRD search", "No interactions found!");
            return;
        }
        MainWindow.getInstance().showProgressBar("Drawing Network");
        Pathway pw = new CreatePathway("HPRD network for " + root.name + " (depth=" + depth + ")").getPathway();
        Map<Integer, Protein> idProteinMap = drawNodes(pw, root.id, response.payload);
        drawEdges(pw, response.payload, idProteinMap);
        MyGraph graph = pw.getGraph();
        graph.restartVisualizationModel();
        graph.changeToGEMLayout();
        graph.fitScaleOfViewer(graph.getSatelliteView());
        graph.normalCentering();
        pw.saveVertexLocations();
        if (autoCoarse) {
            autoCoarse(graph);
        }
        MainWindow.getInstance().closeProgressBar();
        MainWindow window = MainWindow.getInstance();
        window.updateOptionPanel();
        window.getFrame().setVisible(true);
    }

    private static Map<Integer, Protein> drawNodes(Pathway pw, int rootId, HPRDRetrievePPIResponsePayload payload) {
        Map<Integer, Protein> idProteinMap = new HashMap<>();
        for (HPRDEntry entry : payload.entries) {
            Protein protein = new Protein(entry.geneSymbol, entry.name);
            idProteinMap.put(entry.id, protein);
            if (entry.id.equals(rootId)) {
                protein.setColor(Color.RED);
            }
            BiologicalNodeAbstract node = pw.addVertex(protein, new Point(10, 10));
            if (entry.id.equals(rootId)) {
                pw.setRootNode(node);
            }
        }
        return idProteinMap;
    }

    private static void drawEdges(Pathway pw, HPRDRetrievePPIResponsePayload payload,
                                  Map<Integer, Protein> idProteinMap) {
        var graph = pw.getGraph().getJungGraph();
        for (int[] entry : payload.binaryInteractions) {
            BiologicalNodeAbstract first = idProteinMap.get(entry[0]);
            BiologicalNodeAbstract second = idProteinMap.get(entry[1]);
            if (graph.findEdge(first, second) == null && first != second) {
                buildEdge(pw, first, second);
            }
        }
    }

    public static void requestMintEntries(String fullName, String alias, String accession) {
        MintEntrySearchRequestPayload payload = new MintEntrySearchRequestPayload();
        payload.name = fullName;
        payload.accession = accession;
        payload.alias = alias;
        Response<MintEntrySearchResponsePayload> response = VanesaApi.postSync("/ppi/mint_entry/search", payload,
                                                                               new TypeReference<>() {
                                                                               });
        MainWindow.getInstance().closeProgressBar();
        if (response.hasError()) {
            MyPopUp.getInstance().show("Mint search", "Sorry, no entries have been found.\n" + response.error);
            return;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            MyPopUp.getInstance().show("Mint search", "Sorry, no entries have been found.");
            return;
        }
        MintSearchResultWindow searchResultWindow = new MintSearchResultWindow(response.payload.results);
        if (!searchResultWindow.show()) {
            return;
        }
        MintEntry[] results = searchResultWindow.getSelectedValues();
        if (results.length > 0) {
            MainWindow.getInstance().showProgressBar("Retrieving PPI Network(s)");
            int depth = searchResultWindow.getSearchDepth();
            boolean binary = searchResultWindow.getBinaryInteractions();
            boolean complex = searchResultWindow.getComplexInteractions();
            boolean autoCoarse = searchResultWindow.getAutoCoarse();
            for (MintEntry entry : results) {
                requestMintPPI(entry, depth, binary, complex, autoCoarse);
            }
            MainWindow.getInstance().closeProgressBar();
        }
    }

    private static void requestMintPPI(MintEntry root, int depth, boolean binary, boolean complex, boolean autoCoarse) {
        MintRetrievePPIRequestPayload payload = new MintRetrievePPIRequestPayload();
        payload.id = root.id;
        payload.depth = depth;
        payload.binary = binary;
        payload.complex = complex;
        Response<MintRetrievePPIResponsePayload> response = VanesaApi.postSync("/ppi/mint_entry/ppi", payload,
                                                                               new TypeReference<>() {
                                                                               });
        MainWindow.getInstance().closeProgressBar();
        if (response.hasError()) {
            MyPopUp.getInstance().show("Mint search", "Failed to retrieve PPI network.\n" + response.error);
            return;
        }
        if (response.payload == null || response.payload.entries == null || response.payload.entries.length <= 1) {
            MyPopUp.getInstance().show("Mint search", "No interactions found!");
            return;
        }
        MainWindow.getInstance().showProgressBar("Drawing Network");
        Pathway pw = new CreatePathway("Mint network for " + root.name + " (depth=" + depth + ")").getPathway();
        Map<Integer, Protein> idProteinMap = drawNodes(pw, root.id, response.payload);
        drawEdges(pw, response.payload, idProteinMap);
        MyGraph graph = pw.getGraph();
        graph.restartVisualizationModel();
        graph.changeToGEMLayout();
        graph.fitScaleOfViewer(graph.getSatelliteView());
        graph.normalCentering();
        pw.saveVertexLocations();
        if (autoCoarse) {
            autoCoarse(graph);
        }
        MainWindow.getInstance().closeProgressBar();
        MainWindow window = MainWindow.getInstance();
        window.updateOptionPanel();
        window.getFrame().setVisible(true);
    }

    private static Map<Integer, Protein> drawNodes(Pathway pw, int rootId, MintRetrievePPIResponsePayload payload) {
        Map<Integer, Protein> idProteinMap = new HashMap<>();
        for (MintEntry entry : payload.entries) {
            Protein protein = new Protein(entry.shortLabel, entry.name);
            idProteinMap.put(entry.id, protein);
            if (entry.id.equals(rootId)) {
                protein.setColor(Color.RED);
            }
            BiologicalNodeAbstract node = pw.addVertex(protein, new Point(10, 10));
            if (entry.id.equals(rootId)) {
                pw.setRootNode(node);
            }
        }
        return idProteinMap;
    }

    private static void drawEdges(Pathway pw, MintRetrievePPIResponsePayload payload,
                                  Map<Integer, Protein> idProteinMap) {
        var graph = pw.getGraph().getJungGraph();
        for (int[] entry : payload.binaryInteractions) {
            BiologicalNodeAbstract first = idProteinMap.get(entry[0]);
            BiologicalNodeAbstract second = idProteinMap.get(entry[1]);
            if (graph.findEdge(first, second) == null && first != second) {
                buildEdge(pw, first, second);
            }
        }
        for (int[] entry : payload.complexInteractions) {
            BiologicalNodeAbstract first = idProteinMap.get(entry[0]);
            BiologicalNodeAbstract second = idProteinMap.get(entry[1]);
            if (graph.findEdge(first, second) == null && first != second) {
                buildEdge(pw, first, second);
            }
        }
    }

    public static void requestIntActEntries(String fullName, String alias, String accession) {
        IntActEntrySearchRequestPayload payload = new IntActEntrySearchRequestPayload();
        payload.name = fullName;
        payload.accession = accession;
        payload.alias = alias;
        Response<IntActEntrySearchResponsePayload> response = VanesaApi.postSync("/ppi/intact_entry/search", payload,
                                                                                 new TypeReference<>() {
                                                                                 });
        MainWindow.getInstance().closeProgressBar();
        if (response.hasError()) {
            MyPopUp.getInstance().show("IntAct search", "Sorry, no entries have been found.\n" + response.error);
            return;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            MyPopUp.getInstance().show("IntAct search", "Sorry, no entries have been found.");
            return;
        }
        IntActSearchResultWindow searchResultWindow = new IntActSearchResultWindow(response.payload.results);
        if (!searchResultWindow.show()) {
            return;
        }
        IntActEntry[] results = searchResultWindow.getSelectedValues();
        if (results.length > 0) {
            MainWindow.getInstance().showProgressBar("Retrieving PPI Network(s)");
            int depth = searchResultWindow.getSearchDepth();
            boolean binary = searchResultWindow.getBinaryInteractions();
            boolean complex = searchResultWindow.getComplexInteractions();
            boolean autoCoarse = searchResultWindow.getAutoCoarse();
            for (IntActEntry entry : results) {
                requestIntActPPI(entry, depth, binary, complex, autoCoarse);
            }
            MainWindow.getInstance().closeProgressBar();
        }
    }

    private static void requestIntActPPI(IntActEntry root, int depth, boolean binary, boolean complex,
                                         boolean autoCoarse) {
        IntActRetrievePPIRequestPayload payload = new IntActRetrievePPIRequestPayload();
        payload.id = root.id;
        payload.depth = depth;
        payload.binary = binary;
        payload.complex = complex;
        Response<IntActRetrievePPIResponsePayload> response = VanesaApi.postSync("/ppi/intact_entry/ppi", payload,
                                                                                 new TypeReference<>() {
                                                                                 });
        MainWindow.getInstance().closeProgressBar();
        if (response.hasError()) {
            MyPopUp.getInstance().show("IntAct search", "Failed to retrieve PPI network.\n" + response.error);
            return;
        }
        if (response.payload == null || response.payload.entries == null || response.payload.entries.length <= 1) {
            MyPopUp.getInstance().show("IntAct search", "No interactions found!");
            return;
        }
        MainWindow.getInstance().showProgressBar("Drawing Network");
        Pathway pw = new CreatePathway("IntAct network for " + root.name + " (depth=" + depth + ")").getPathway();
        Map<Integer, Protein> idProteinMap = drawNodes(pw, root.id, response.payload);
        drawEdges(pw, response.payload, idProteinMap);
        MyGraph graph = pw.getGraph();
        graph.restartVisualizationModel();
        graph.changeToGEMLayout();
        graph.fitScaleOfViewer(graph.getSatelliteView());
        graph.normalCentering();
        pw.saveVertexLocations();
        if (autoCoarse) {
            autoCoarse(graph);
        }
        MainWindow.getInstance().closeProgressBar();
        MainWindow window = MainWindow.getInstance();
        window.updateOptionPanel();
        window.getFrame().setVisible(true);
    }

    private static Map<Integer, Protein> drawNodes(Pathway pw, int rootId, IntActRetrievePPIResponsePayload payload) {
        Map<Integer, Protein> idProteinMap = new HashMap<>();
        for (IntActEntry entry : payload.entries) {
            Protein protein = new Protein(entry.shortLabel, entry.name);
            idProteinMap.put(entry.id, protein);
            if (entry.id.equals(rootId)) {
                protein.setColor(Color.RED);
            }
            BiologicalNodeAbstract node = pw.addVertex(protein, new Point(10, 10));
            if (entry.id.equals(rootId)) {
                pw.setRootNode(node);
            }
        }
        return idProteinMap;
    }

    private static void drawEdges(Pathway pw, IntActRetrievePPIResponsePayload payload,
                                  Map<Integer, Protein> idProteinMap) {
        var graph = pw.getGraph().getJungGraph();
        for (int[] entry : payload.binaryInteractions) {
            BiologicalNodeAbstract first = idProteinMap.get(entry[0]);
            BiologicalNodeAbstract second = idProteinMap.get(entry[1]);
            if (graph.findEdge(first, second) == null && first != second) {
                buildEdge(pw, first, second);
            }
        }
        for (int[] entry : payload.complexInteractions) {
            BiologicalNodeAbstract first = idProteinMap.get(entry[0]);
            BiologicalNodeAbstract second = idProteinMap.get(entry[1]);
            if (graph.findEdge(first, second) == null && first != second) {
                buildEdge(pw, first, second);
            }
        }
    }

    private static void buildEdge(Pathway pw, BiologicalNodeAbstract one, BiologicalNodeAbstract two) {
        PhysicalInteraction r = new PhysicalInteraction("", "", one, two);
        r.setDirected(false);
        r.setVisible(true);
        pw.addEdge(r);
    }

    private static void autoCoarse(MyGraph graph) {
        class HLC implements HierarchyListComparator<Integer> {
            public HLC() {
            }

            public Integer getValue(BiologicalNodeAbstract n) {
                /* TODO
                String parent = parentNodes.get(vertex2Name.get(n));
                if (name2Vertex.get(parent) != null) {
                    return name2Vertex.get(parent).getID();
                }
                */
                return getSubValue(n);
            }

            public Integer getSubValue(BiologicalNodeAbstract n) {
                return n.getID();
            }
        }
        HierarchyList<Integer> l = new HierarchyList<>();
        l.addAll(graph.getAllVertices());
        l.sort(new HLC());
        l.coarse();
    }
}
