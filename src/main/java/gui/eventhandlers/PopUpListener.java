package gui.eventhandlers;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.PathwayMap;
import configurations.gui.LayoutConfig;
import copy.CopySelection;
import copy.CopySelectionSingleton;
import database.brenda.BRENDASearch;
import database.kegg.KEGGConnector;
import database.kegg.KeggSearch;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.gemLayout.GEMLayout;
import gui.MainWindow;
import gui.MyPopUp;
import io.SaveDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class PopUpListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        MainWindow w = MainWindow.getInstance();
        GraphInstance graphInstance = new GraphInstance();
        String event = e.getActionCommand();
        final Pathway pw = graphInstance.getPathway();
        if ("center".equals(event)) {
            graphInstance.getPathway().getGraph().animatedCentering();
        } else if ("springLayout".equals(event)) {
            LayoutConfig.changeToLayout(SpringLayout.class);
        } else if ("kkLayout".equals(event)) {
            LayoutConfig.changeToLayout(KKLayout.class);
        } else if ("frLayout".equals(event)) {
            LayoutConfig.changeToLayout(FRLayout.class);
        } else if ("circleLayout".equals(event)) {
            LayoutConfig.changeToLayout(CircleLayout.class);
        } else if ("gemLayout".equals(event)) {
            LayoutConfig.changeToLayout(GEMLayout.class);
        } else if ("isomLayout".equals(event)) {
            LayoutConfig.changeToLayout(ISOMLayout.class);
        } else if ("MDLayout".equals(event)) {
            // LayoutConfig.changeToLayout(MDForceLayout.class);
        } else if ("copy".equals(event)) {
            if (GraphContainer.getInstance().containsPathway()) {
                VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
                        .getVisualizationViewer();
                Set<BiologicalNodeAbstract> vertices = new HashSet<>(vv.getPickedVertexState().getPicked());
                Set<BiologicalEdgeAbstract> edges = new HashSet<>(vv.getPickedEdgeState().getPicked());
                CopySelectionSingleton.setInstance(new CopySelection(vertices, edges));
            }
        } else if ("cut".equals(event)) {
            if (GraphContainer.getInstance().containsPathway()) {
                VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
                        .getVisualizationViewer();
                Set<BiologicalNodeAbstract> vertices = new HashSet<>(vv.getPickedVertexState().getPicked());
                Set<BiologicalEdgeAbstract> edges = new HashSet<>(vv.getPickedEdgeState().getPicked());
                CopySelectionSingleton.setInstance(new CopySelection(vertices, edges));
                pw.removeSelection();
                w.updateElementTree();
                w.updatePathwayTree();
                w.updateTheoryProperties();
            }
        } else if ("paste".equals(event)) {
            if (GraphContainer.getInstance().containsPathway()) {
                // MyGraph g = con.getPathway(w.getCurrentPathway()).getGraph();
                CopySelectionSingleton.getInstance().paste();
                pw.getGraph().restartVisualizationModel();
                pw.getGraph().getVisualizationViewer().repaint();
            }
        } else if ("delete".equals(event)) {
            if (GraphContainer.getInstance().containsPathway()) {
                pw.removeSelection();
                w.updateElementTree();
                w.updateTheoryProperties();
            }
        } else if ("keggSearch".equals(event) || "brendaSearch".equals(event)) {
            String[] input = {"", "", "", "", ""};
            Set<BiologicalNodeAbstract> vertices = pw.getSelectedNodes();
            if (pw.getSelectedNodes().isEmpty()) {
                JOptionPane.showMessageDialog(w.getFrame(), "Please select a node to search after it in a database!",
                        "Operation not possible...", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BiologicalNodeAbstract bna = vertices.iterator().next();
            if (vertices.size() > 1) {
                String[] possibilities = new String[vertices.size()];
                int i = 0;
                for (BiologicalNodeAbstract vertex : vertices) {
                    possibilities[i++] = vertex.getLabel();
                }
                String answer = (String) JOptionPane.showInputDialog(w.getFrame(),
                        "Choose one of the selected nodes, which shall be searched in a database", "Select a node...",
                        JOptionPane.QUESTION_MESSAGE, null, possibilities, possibilities[0]);
                if (answer == null)
                    return;
                bna = pw.getNodeByLabel(answer);
            }
            if ("keggSearch".equals(event)) {
                if (bna.getBiologicalElement().equals(biologicalElements.Elementdeclerations.enzyme))
                    input[2] = bna.getLabel();
                else if (bna.getBiologicalElement().equals(biologicalElements.Elementdeclerations.gene))
                    input[3] = bna.getLabel();
                else if (bna.getBiologicalElement().equals(biologicalElements.Elementdeclerations.pathwayMap))
                    input[0] = bna.getLabel();
                else
                    input[4] = bna.getLabel();
                KeggSearch keggSearch = new KeggSearch(input[0], input[1], input[2], input[3], input[4], graphInstance.getPathway());
                keggSearch.execute();
                MainWindow.getInstance().showProgressBar("KEGG query");
            } else {
                if (bna.getBiologicalElement().equals(biologicalElements.Elementdeclerations.enzyme))
                    input[0] = bna.getLabel();
                else {
                    input[2] = bna.getLabel();
                    input[3] = bna.getLabel();
                }
                BRENDASearch brendaSearch = new BRENDASearch(input, pw, false);
                brendaSearch.execute();
            }
        } else if ("openPathway".equals(event)) {
            String pwName = w.getCurrentPathway();
            for (BiologicalNodeAbstract bna : pw.getSelectedNodes()) {
                if (bna instanceof PathwayMap) {
                    PathwayMap map = (PathwayMap) bna;
                    Pathway pwLink = map.getPathwayLink();
                    if (pwLink == null) {
                        KEGGConnector kc = new KEGGConnector(map.getName(), "", true);
                        kc.setSearchMicroRNAs(JOptionPane.showConfirmDialog(w.getFrame(),
                                "Search also after possibly connected microRNAs in mirBase/tarBase?",
                                "Search parameters...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
                        kc.addPropertyChangeListener((evt) -> {
                            if (evt.getNewValue().equals("finished")) {
                                Pathway newPW = kc.getPw();
                                newPW.setParent(pw);
                                w.removeTab(false);
                                w.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                GraphContainer con = GraphContainer.getInstance();
                                String newPathwayName = con.addPathway(pwName, newPW);
                                newPW = con.getPathway(newPathwayName);
                                w.addTab(newPW.getTab().getTitelTab());
                                w.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                map.setPathwayLink(newPW);
                                map.setColor(Color.BLUE);
                                w.updateAllGuiElements();
                            }
                        });
                        kc.execute();
                        MainWindow.getInstance().showProgressBar("KEGG query");
                    } else {
                        w.removeTab(false);
                        w.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                        GraphContainer con = GraphContainer.getInstance();
                        String newPathwayName = con.addPathway(pwName, pwLink);
                        pwLink = con.getPathway(newPathwayName);
                        w.addTab(pwLink.getTab().getTitelTab());
                        w.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                    w.updateAllGuiElements();
                    return;
                }
            }
        } else if ("openPathwayTab".equals(event)) {
            for (BiologicalNodeAbstract bna : pw.getSelectedNodes()) {
                if (bna instanceof PathwayMap) {
                    PathwayMap map = (PathwayMap) bna;
                    // Pathway newPW = map.getPathwayLink();
                    KEGGConnector kc = new KEGGConnector(map.getName(), "", false);
                    kc.setSearchMicroRNAs(JOptionPane.showConfirmDialog(w.getFrame(),
                            "Search also after possibly connected microRNAs in mirBase/tarBase?",
                            "Search paramaters...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
                    kc.execute();
                    MainWindow.getInstance().showProgressBar("KEGG query");
                }
            }
        } else if ("returnToParent".equals(event) && pw.getParent() != null) {
            String pwName = w.getCurrentPathway();
            w.removeTab(false);
            w.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            GraphContainer con = GraphContainer.getInstance();
            String newPathwayName = con.addPathway(pwName, pw.getParent());
            Pathway newPW = con.getPathway(newPathwayName);
            w.addTab(newPW.getTab().getTitelTab());
            w.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            w.updateAllGuiElements();
        } else if ("graphPicture".equals(event)) {
            JMenuItem item = (JMenuItem) e.getSource();
            JPopupMenu popup = (JPopupMenu) item.getParent();
            @SuppressWarnings("unchecked")
            MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv =
                    (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) popup.getInvoker();
            Pathway vvPw = vv.getPathway();// graphInstance.getPathway();
            VisualizationImageServer<BiologicalNodeAbstract, BiologicalEdgeAbstract> wvv = vvPw.prepareGraphToPrint();
            if (GraphContainer.getInstance().containsPathway()) {
                if (vvPw.hasGotAtLeastOneElement()) {
                    new SaveDialog(SaveDialog.FORMAT_PNG + SaveDialog.FORMAT_SVG, SaveDialog.DATA_TYPE_GRAPH_PICTURE, wvv,
                            MainWindow.getInstance().getFrame(), null);
                } else {
                    MyPopUp.getInstance().show("Error", "Please create a network first.");
                }
            } else {
                MyPopUp.getInstance().show("Error", "Please create a network first.");
            }
        }
    }
}
