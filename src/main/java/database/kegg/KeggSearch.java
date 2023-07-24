package database.kegg;

import api.VanesaApi;
import api.payloads.Response;
import api.payloads.kegg.*;
import biologicalElements.Pathway;
import biologicalObjects.edges.Compound;
import biologicalObjects.nodes.*;
import com.fasterxml.jackson.core.type.TypeReference;
import database.kegg.gui.KEGGSearchResultWindow;
import graph.CreatePathway;
import gui.MainWindow;
import gui.PopUpDialog;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KeggSearch {
    public static void searchPathways(String pathway, String organism, String enzyme, String gene, String compound) {
        searchPathways(pathway, organism, enzyme, gene, compound, null);
    }

    public static void searchPathways(String pathway, String organism, String enzyme, String gene, String compound,
                                      Pathway mergePW) {
        PathwaySearchRequestPayload payload = new PathwaySearchRequestPayload();
        payload.pathway = pathway;
        payload.organism = organism;
        payload.enzyme = enzyme;
        payload.gene = gene;
        payload.compound = compound;
        Response<PathwaySearchResponsePayload> response = VanesaApi.postSync("/kegg/pathway/search", payload,
                                                                             new TypeReference<>() {
                                                                             });
        MainWindow.getInstance().closeProgressBar();
        if (response.hasError()) {
            PopUpDialog.getInstance().show("KEGG search", "Sorry, no entries have been found.\n" + response.error);
            return;
        }
        if (response.payload == null || response.payload.results == null || response.payload.results.length == 0) {
            PopUpDialog.getInstance().show("KEGG search", "Sorry, no entries have been found.");
            return;
        }
        KEGGSearchResultWindow searchResultWindow = new KEGGSearchResultWindow(response.payload.results);
        if (!searchResultWindow.show()) {
            return;
        }
        KeggPathway[] results = searchResultWindow.getSelectedValues();
        if (results.length == 0) {
            return;
        }
        int answer = JOptionPane.YES_OPTION;
        if (results.length > 1 || mergePW != null) {
            answer = JOptionPane.showOptionDialog(MainWindow.getInstance().getFrame(),
                                                  "Shall the selected Pathways be loaded each into a separate tab, be combined into an overview Pathway or merged?",
                                                  "Several Pathways selected...", JOptionPane.YES_NO_CANCEL_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE, null, new String[]{
                            "Separate Tabs", "Overview Pathway", "Merge Pathways"
                    }, JOptionPane.YES_OPTION);
        }
        MainWindow.getInstance().showProgressBar("Drawing network");
        if (answer == JOptionPane.NO_OPTION) {
            Pathway newPW = new CreatePathway("Overview Pathway").getPathway();
            List<BiologicalNodeAbstract> bnas = new ArrayList<>();
            if (StringUtils.isNotBlank(enzyme))
                bnas.add(new Enzyme(enzyme, enzyme));
            if (StringUtils.isNotBlank(gene))
                bnas.add(new Gene(gene, gene));
            if (StringUtils.isNotBlank(compound))
                bnas.add(new Metabolite(compound, compound));
            for (KeggPathway s : results) {
                PathwayMap map = new PathwayMap(s.name, s.id);
                map = (PathwayMap) newPW.addVertex(map, new Point(0, 0));
                for (BiologicalNodeAbstract bna : bnas) {
                    bna = newPW.addVertex(bna, new Point(0, 0));
                    bna.setColor(Color.red);
                    Compound c = new Compound("", "", bna, map);
                    c.setDirected(true);
                    newPW.addEdge(c);
                }
            }
            newPW.getGraph().restartVisualizationModel();
            MainWindow.getInstance().updateAllGuiElements();
            newPW.getGraph().changeToGEMLayout();
            newPW.getGraph().normalCentering();
        } else {
            KEGGConnector kc = new KEGGConnector(results[0].id, results[0].taxonomyId, mergePW != null);
            kc.addPropertyChangeListener((evt) -> {
                if (evt.getNewValue().equals("finished")) {
                    /* TODO: KEGG
                    if (answer == JOptionPane.CANCEL_OPTION)
                        if (mergePW == null)
                            mergePW = kc.getPw();
                        else {
                            MainWindow.getInstance().removeTab(false);
                            mergePW = new MergeGraphs(mergePW, kc.getPw(), false).getPw_new();
                        }
                    if (it != null && it.hasNext()) {
                        String[] details = it.next();
                        KEGGConnector kc = new KEGGConnector(details[0], details[2], answer != JOptionPane.YES_OPTION);
                        kc.addPropertyChangeListener(this);
                        kc.setSearchMicroRNAs(searchResultWindow.getSearchMirnas());
                        kc.setAutoCoarse(searchResultWindow.getAutoCoarse());
                        kc.execute();
                    } else {
                        mergePW = GraphInstance.getContainer().getPathway(MainWindow.getInstance().getCurrentPathway());
                        MainWindow.getInstance().closeProgressBar();
                        MainWindow.getInstance().updateAllGuiElements();
                        mergePW.getGraph().getVisualizationViewer().repaint();
                        mergePW.getGraph().disableGraphTheory();
                        mergePW.getGraph().normalCentering();
                    }
                    */
                }
            });
            kc.execute();
        }
        MainWindow.getInstance().closeProgressBar();
    }

    public static KeggPathway getPathway(String pathwayId) {
        GetPathwayRequestPayload payload = new GetPathwayRequestPayload();
        payload.pathwayId = pathwayId;
        Response<GetPathwayResponsePayload> response = VanesaApi.postSync("/kegg/pathway", payload,
                                                                          new TypeReference<>() {
                                                                          });
        if (response.hasError()) {
            PopUpDialog.getInstance().show("KEGG search", "Failed to retrieve pathway with id '" + pathwayId + "'.\n" +
                                                          response.error);
            return null;
        }
        if (response.payload == null || response.payload.id == null) {
            PopUpDialog.getInstance().show("KEGG search", "Failed to retrieve pathway with id '\" + pathwayId + \"'.");
            return null;
        }

        KeggPathway result = new KeggPathway();
        result.id = response.payload.id;
        result.name = response.payload.name;
        result.taxonomyId = response.payload.taxonomyId;
        result.taxonomyName = response.payload.taxonomyName;
        return result;
    }
}
