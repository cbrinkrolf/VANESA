package gui;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.gui.CompareGraphsGUI;
import graph.hierarchies.AutoCoarse;
import graph.jung.classes.MyGraph;
import net.miginfocom.swing.MigLayout;
import petriNet.OpenModelicaResult;
import petriNet.PNTableDialog;
import petriNet.ReachController;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class ToolBar {
    private JToolBar bar = null;
    private boolean isPetri = false;

    public ToolBar(Boolean petriNetView) {
        paintToolbar(petriNetView);
    }

    public JToolBar getToolBar() {
        return bar;
    }

    public void paintToolbar(Boolean petriNetView) {
        // only repaint if necessary
        if (bar != null && ((isPetri && petriNetView) || (!isPetri && !petriNetView))) {
            return;
        }
        if (bar == null) {
            bar = new JToolBar();
        }
        isPetri = petriNetView;
        bar.removeAll();
        bar.setOrientation(1);
        bar.setFloatable(true);
        MigLayout bl = new MigLayout("insets 0, wrap 1");
        bar.setLayout(bl);
        String modelingViewString = "<html><b>Change View</b><br>to Modeling</html>";
        // String PetriViewString = "<html><b>Change View</b><br>to PetriNet</html>";

        JButton newDoc = createToolBarButton("newDocumentSmall.png", "Create New Network", this::onNewNetworkClicked);
        JButton parallelView = createToolBarButton("parallelview.png", "Create ParallelView From Graphs",
                                                   this::onParallelViewClicked);
        JButton pick = createToolBarButton("newPick.png", "Pick Element", this::onPickClicked);
        JButton hierarchy = createToolBarButton("hierarchy_button.png", "Hierarchy Mode", this::onHierarchyClicked);
        JButton discretePlace = createToolBarButton("discretePlace.png", "Discrete Place",
                                                    this::onDiscretePlaceClicked);
        JButton continuousPlace = createToolBarButton("continiousPlace.png", "Continuous Place",
                                                      this::onContinuousPlaceClicked);
        JButton discreteTransition = createToolBarButton("discreteTransition.png", "Discrete Transition",
                                                         this::onDiscreteTransitionClicked);
        JButton continuousTransition = createToolBarButton("continiousTransition2.png", "Continuous Transition",
                                                           this::onContinuousTransitionClicked);
        JButton stochasticTransition = createToolBarButton("stochasticTransition2.png", "Stochastic Transition",
                                                           this::onStochasticTransitionClicked);
        JButton center = createToolBarButton("centerGraph.png", "Center Graph", this::onCenterClicked);
        JButton move = createToolBarButton("move.png", "Move Graph", this::onMoveClicked);
        JButton zoomIn = createToolBarButton("zoomPlus.png", "Zoom In", this::onZoomInClicked);
        JButton zoomOut = createToolBarButton("zoomMinus.png", "Zoom Out", this::onZoomOutClicked);
        JButton trash = createToolBarButton("Trash.png", "Delete Selected Items", this::onDelClicked);
        // trash.setMnemonic(KeyEvent.VK_DELETE);
        JButton info = createToolBarButton("InfoToolBarButton.png", "Info", this::onInfoClicked);
        JButton infoExtended = createToolBarButton("InfoToolBarButtonextended.png", "More Info",
                                                   this::onInfoExtendedClicked);
        JButton mergeSelectedNodes = createToolBarButton("MergeNodesButton.png", "Merge Selected Nodes",
                                                         this::onMergeSelectedNodesClicked);
        JButton splitNode = createToolBarButton("SplitNodesButton.png",
                                                "Split Node (inverse operation of \"merge nodes\")",
                                                this::onSplitNodeClicked);
        JButton coarseSelectedNodes = createToolBarButton("CoarseNodesButton.png", "Coarse Selected Nodes",
                                                          this::onCoarseSelectedNodesClicked);
        JButton flatSelectedNodes = createToolBarButton("FlatNodesButton.png", "Flat Selected Coarse Node(s)",
                                                        this::onFlatSelectedNodesClicked);
        JButton groupSelectedNodes = createToolBarButton("GroupButton.png", "Group Selected Nodes",
                                                         this::onGroupClicked);
        JButton deleteGroup = createToolBarButton("UngroupButton.png", "Delete Selected Group",
                                                  this::onDeleteGroupClicked);
        JButton enterSelectedNode = createToolBarButton("enterNode.png", "Enter Selected Coarse Node(s)",
                                                        this::onEnterNodeClicked);
        JButton autoCoarse = createToolBarButton("autocoarse.png", "Autocoarse Current Pathway",
                                                 this::onAutoCoarseClicked);
        JButton newWindow = createToolBarButton("newWindow.png", "Open New Window", this::onNewWindowClicked);

        /*
        JButton convertIntoPetriNet = new ToolBarButton("convertIntoPetriNet");
        convertIntoPetriNet.setToolTipText("Convert Into Petri Net");
        convertIntoPetriNet.setActionCommand(ToolbarActionCommands.convertIntoPetriNet.value);
        convertIntoPetriNet.addActionListener(ToolBarListener.getInstance());
        */

        JPanel infoPanel = new ToolBarPanel();
        infoPanel.setLayout(new GridLayout(5, 2));
        infoPanel.add(info);
        infoPanel.add(infoExtended);
        // if (MainWindow.developer) {
        infoPanel.add(mergeSelectedNodes);
        infoPanel.add(splitNode);
        // }
        infoPanel.add(coarseSelectedNodes);
        infoPanel.add(flatSelectedNodes);
        infoPanel.add(enterSelectedNode);
        if (MainWindow.developer) {
            infoPanel.add(autoCoarse);
        }
        infoPanel.add(groupSelectedNodes);
        infoPanel.add(deleteGroup);

        JButton fullScreen = createToolBarButton("newFullScreen.png", "Full Screen", this::onFullScreenClicked);
        JButton stretchEdges = createToolBarButton("stretchEdges.png", "Stretch Edge Length",
                                                   this::onStretchEdgesClicked);
        JButton compressEdges = createToolBarButton("compressEdges.png", "Compress Edge Length",
                                                    this::onCompressEdgesClicked);
        JButton merge = createToolBarButton("maximize.png", "Compare / Align Graphs", this::onMergeClicked);

        ButtonChooser chooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectShapeActions());

        chooser.setToolTipText("Draw compartments");

        ButtonChooser colorChooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectColorActions());

        colorChooser.setToolTipText("Set compartment colours");

        // JButton petriNet = new JButton(PetriViewString);
        // petriNet.setToolTipText("Change to Petri Net View");
        // petriNet.addActionListener(e -> onCreatePetriNetClicked());

        JButton covGraph = new ToolBarButton("Cov/Reach Graph");
        covGraph.setToolTipText("Create Cov/Reach Graph");
        covGraph.addActionListener(e -> onCreateCovClicked());

        JButton editNodes = new ToolBarButton("Edit PN-Elements");
        editNodes.setToolTipText("Edit PN-Elements");
        editNodes.addActionListener(e -> onEditElementsClicked());

        JButton loadModResult = new ToolBarButton("Load Modellica Result");
        loadModResult.setToolTipText("Load Modellica Result");
        loadModResult.addActionListener(e -> onLoadModResultClicked());

        JButton modelling = new ToolBarButton(modelingViewString);
        modelling.addActionListener(e -> onModellingClicked());
        modelling.setToolTipText("Change to Modeling View");

        // JButton heatmap = createToolBarButton("heatmapGraph.png", "Create Heatgraph", ToolbarActionCommands.heatmap.value);

        JButton edit = new ToolBarButton(ImagePath.getInstance().getImageIcon("TitleGraph.png"));
        edit.setSelectedIcon(ImagePath.getInstance().getImageIcon("editSelected.png"));
        edit.setToolTipText("Edit Graph");
        edit.addActionListener(e -> onEditClicked());

        JPanel viewPortControls = new ToolBarPanel();
        viewPortControls.setLayout(new GridLayout(3, 2));

        viewPortControls.add(fullScreen);
        viewPortControls.add(center);
        viewPortControls.add(zoomIn);
        viewPortControls.add(zoomOut);
        viewPortControls.add(compressEdges);
        viewPortControls.add(stretchEdges);

        JPanel editControls = new ToolBarPanel();
        editControls.setLayout(new GridLayout(0, 2));

        if (!petriNetView) {
            editControls.add(edit);
            editControls.add(new JLabel());
        }
        editControls.add(pick);
        editControls.add(move);
        editControls.add(trash);
        editControls.add(hierarchy);

        JPanel printControls = new ToolBarPanel();
        printControls.setLayout(new GridLayout(1, 2));

        printControls.add(newDoc);
        printControls.add(newWindow);

        // Add buttons to experiment with Grid Layout
        JPanel petriNetControls = new ToolBarPanel();
        petriNetControls.setLayout(new GridLayout(2, 3));

        petriNetControls.add(discretePlace);
        petriNetControls.add(continuousPlace);
        petriNetControls.add(new JLabel());
        petriNetControls.add(discreteTransition);
        petriNetControls.add(continuousTransition);
        petriNetControls.add(stochasticTransition);

        JPanel featureControls = new ToolBarPanel();

        if (!petriNetView) {
            featureControls.setLayout(new GridLayout(2, 2));
            featureControls.add(merge);
            if (MainWindow.developer) {
                // featureControls.add(heatmap);
                featureControls.add(parallelView);
            }
            featureControls.add(chooser);
            featureControls.add(colorChooser);
        } else {
            featureControls.setLayout(new GridLayout(1, 2));
            featureControls.add(chooser);
            featureControls.add(colorChooser);
        }

        // featureControls.setMaximumSize(featureControls.getPreferredSize());
        // featureControls.set
        // featureControls.setAlignmentX(Component.LEFT_ALIGNMENT);
        // featureControls.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel toolBarControlControls = new ToolBarPanel();
        toolBarControlControls.setLayout(new GridLayout(2, 1));
        // if (petriNetView) {
        // toolBarControlControls.add(modelling);
        // } else {
        // toolBarControlControls.add(petriNet);
        // toolBarControlControls.add(convertIntoPetriNet);
        //
        // }
        // if (!petriNetView) toolBarControlControls.add(convertIntoPetriNet);

        JPanel nodeAdjustment = new ToolBarPanel();
        nodeAdjustment.setLayout(new GridLayout(2, 2));

        JButton adjustDown = createToolBarButton("adjustDown.png", "Adjust Selected Nodes To Lowest Node",
                                                 this::onAdjustDownClicked);
        nodeAdjustment.add(adjustDown);
        JButton adjustLeft = createToolBarButton("adjustLeft.png", "Adjust Selected Nodes To Left",
                                                 this::onAdjustLeftClicked);
        nodeAdjustment.add(adjustLeft);

        JButton adjustHorizontalSpace = createToolBarButton("adjustHorizontalSpace.png",
                                                            "Adjust Horizontal Space of Selected Nodes",
                                                            this::onAdjustHorizontalSpaceClicked);
        nodeAdjustment.add(adjustHorizontalSpace);
        JButton adjustVerticalSpace = createToolBarButton("adjustVerticalSpace.png",
                                                          "Adjust Vertical Space of Selected Nodes",
                                                          this::onAdjustVerticalSpaceClicked);
        nodeAdjustment.add(adjustVerticalSpace);

        if (petriNetView) {
            // bar.add(toolBarControlControls);
            // toolBarControlControls.add(covGraph);
            // toolBarControlControls.add(editNodes);
            // toolBarControlControls.add(loadModResult);
            // toolBarControlControls.add(simulate);
            // bar.add(new JSeparator());
            bar.add(printControls);
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(editControls);
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(petriNetControls);
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(featureControls);
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(viewPortControls);
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(nodeAdjustment);
            bar.add(new ToolBarSeparator(), "growx, wrap");

            bar.add(infoPanel);
        } else {
            // bar.add(toolBarControlControls);
            // bar.add(new JSeparator());
            bar.add(printControls, "wrap");
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(editControls, "wrap");

            bar.add(new ToolBarSeparator(), "growx, wrap");

            // bar.add(new JSeparator());
            bar.add(featureControls, "wrap");
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(viewPortControls, "wrap");
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(nodeAdjustment);
            bar.add(new ToolBarSeparator(), "growx, wrap");
            bar.add(infoPanel, "wrap");
        }
        bar.validate();
        bar.repaint();
        bar.setVisible(true);
    }

    private static JButton createToolBarButton(String imageFileName, String toolTipText, Runnable action) {
        JButton button = new ToolBarButton(ImagePath.getInstance().getImageIcon(imageFileName));
        button.setToolTipText(toolTipText);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void onNewNetworkClicked() {
        MainWindow w = MainWindow.getInstance();
        int option = JOptionPane.showOptionDialog(MainWindow.getInstance().getFrame(),
                                                  "Which type of modeling do you prefer?", "Choose Network Type...",
                                                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                  new String[]{"Biological Graph", "Petri Net"},
                                                  JOptionPane.CANCEL_OPTION);
        if (option != -1) {
            new CreatePathway();
            GraphInstance.getPathwayStatic().setIsPetriNet(option == JOptionPane.NO_OPTION);
            w.getBar().paintToolbar(option == JOptionPane.NO_OPTION);
            w.updateAllGuiElements();
        }
    }

    private void onParallelViewClicked() {
        // create a graph choosing popup and calculate network properties
        new ParallelChooseGraphsWindow();
    }

    private void onMoveClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            con.changeMouseFunction("move");
            MyGraph g = GraphInstance.getPathwayStatic().getGraph();
            g.disableGraphTheory();
            // g.getVisualizationViewer().resize(20, 20);
            Dimension d = g.getVisualizationViewer().getPreferredSize();
            d.setSize(d.width * 2, d.height * 2);
            g.getVisualizationViewer().setPreferredSize(d);
            g.getVisualizationViewer().repaint();
        }
    }

    private void onPickClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            con.changeMouseFunction("pick");
            MyGraph g = GraphInstance.getPathwayStatic().getGraph();
            g.disableGraphTheory();
        }
    }

    private void onCenterClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            // CENTERING WITH SCALING
            GraphInstance.getPathwayStatic().getGraph().normalCentering();
            // ONLY FOR CENTERING, NOT SCALING
            // graphInstance.getPathway().getGraph().animatedCentering();
        }
    }

    private void onZoomInClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            MyGraph g = GraphInstance.getPathwayStatic().getGraph();
            g.zoomIn();
        }
    }

    private void onZoomOutClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            MyGraph g = GraphInstance.getPathwayStatic().getGraph();
            g.zoomOut();
        }
    }

    private void onFullScreenClicked() {
        MainWindow.getInstance().setFullScreen();
    }

    private void onCompressEdgesClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            GraphInstance.getPathwayStatic().stretchGraph(0.9);
            GraphInstance.getPathwayStatic().updateMyGraph();
        }
    }

    private void onStretchEdgesClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            GraphInstance.getPathwayStatic().stretchGraph(1.1);
            GraphInstance.getPathwayStatic().updateMyGraph();
        }
    }

    private void onEditClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            con.changeMouseFunction("edit");
            MyGraph g = GraphInstance.getPathwayStatic().getGraph();
            g.disableGraphTheory();
        }
    }

    private void onMergeClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.getPathwayNumbers() > 1) {
            MyGraph g = GraphInstance.getPathwayStatic().getGraph();
            g.disableGraphTheory();
            new CompareGraphsGUI();
        } else {
            PopUpDialog.getInstance().show("Error", "Please create a network first!");
        }
    }

    private void onDelClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            MainWindow w = MainWindow.getInstance();
            // g.stopVisualizationModel();
            GraphInstance.getPathwayStatic().removeSelection();
            w.updateElementTree();
            w.updatePathwayTree();
            // w.updateTheoryProperties();
            // g.restartVisualizationModel();
        }
    }

    private void onInfoClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            if (GraphInstance.getPathwayStatic().hasGotAtLeastOneElement()) {
                new InfoWindow(false);
            }
        }
    }

    private void onInfoExtendedClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            if (GraphInstance.getPathwayStatic().hasGotAtLeastOneElement()) {
                new InfoWindow(true);
            }
        }
    }

    private void onModellingClicked() {
        GraphContainer con = GraphContainer.getInstance();
        con.setPetriView(false);
        for (Component component : MainWindow.getInstance().getFrame().getContentPane().getComponents()) {
            if (component.getClass().getName().equals("javax.swing.JPanel")) {
                MainWindow.getInstance().getBar().paintToolbar(false);
                break;
            }
        }
    }

    private void onDiscretePlaceClicked() {
        GraphContainer con = GraphContainer.getInstance();
        con.changeMouseFunction("edit");
        con.setPetriView(true);
        con.setPetriNetEditingMode(Elementdeclerations.discretePlace);
    }

    private void onContinuousPlaceClicked() {
        GraphContainer con = GraphContainer.getInstance();
        con.changeMouseFunction("edit");
        con.setPetriView(true);
        con.setPetriNetEditingMode(Elementdeclerations.continuousPlace);
    }

    private void onDiscreteTransitionClicked() {
        GraphContainer con = GraphContainer.getInstance();
        con.changeMouseFunction("edit");
        con.setPetriView(true);
        con.setPetriNetEditingMode(Elementdeclerations.discreteTransition);
    }

    private void onContinuousTransitionClicked() {
        GraphContainer con = GraphContainer.getInstance();
        con.changeMouseFunction("edit");
        con.setPetriView(true);
        con.setPetriNetEditingMode(Elementdeclerations.continuousTransition);
    }

    private void onStochasticTransitionClicked() {
        GraphContainer con = GraphContainer.getInstance();
        con.changeMouseFunction("edit");
        con.setPetriView(true);
        con.setPetriNetEditingMode(Elementdeclerations.stochasticTransition);
    }

    private void onCreatePetriNetClicked() {
        GraphContainer con = GraphContainer.getInstance();
        con.setPetriView(true);
        for (Component component : MainWindow.getInstance().getFrame().getContentPane().getComponents()) {
            if (component.getClass().getName().equals("javax.swing.JPanel")) {
                MainWindow.getInstance().getBar().paintToolbar(true);
                break;
            }
        }
        /*
         * if (con.getPathwayNumbers() > 0) { MyGraph g =
         * GraphInstance.getPathwayStatic().getGraph(); g.disableGraphTheory(); //
         * new CompareGraphsGUI(); new ConvertToPetriNet(); }
         */
        // if (con.getPathwayNumbers() > 0) {
        // MyGraph g = GraphInstance.getPathwayStatic().getGraph();
        // g.disableGraphTheory();
        // //new CompareGraphsGUI();
        // new ConvertToPetriNet();
        // }
    }

    private void onCreateCovClicked() {
        // MyGraph g = GraphInstance.getPathwayStatic().getGraph();
        // Cov cov = new Cov();
        if (JOptionPane.showConfirmDialog(MainWindow.getInstance().getFrame(),
                                          "The calculation of the reach graph could take long time, especially if you have many places in your network. Do you want to perform the calculation anyway?",
                                          "Please Conform your action...", JOptionPane.YES_NO_OPTION) ==
            JOptionPane.YES_OPTION) {
            new ReachController();
        }
        if (GraphInstance.getMyGraph() != null) {
            GraphInstance.getMyGraph().changeToGEMLayout();
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onEditElementsClicked() {
        new PNTableDialog().setVisible(true);
    }

    private void onLoadModResultClicked() {
        new OpenModelicaResult().execute();
    }

    private void onGroupClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            GraphInstance.getPathwayStatic().groupSelectedNodes();
            GraphInstance.getPathwayStatic().updateMyGraph();
        }
    }

    private void onDeleteGroupClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            GraphInstance.getPathwayStatic().deleteGroup();
            GraphInstance.getPathwayStatic().updateMyGraph();
        }
    }

    private void onCoarseSelectedNodesClicked() {
        if (GraphInstance.getMyGraph() != null) {
            Set<BiologicalNodeAbstract> selectedNodes = new HashSet<>(
                    GraphInstance.getPathwayStatic().getSelectedNodes());
            BiologicalNodeAbstract.coarse(selectedNodes);
            GraphInstance.getPathwayStatic().updateMyGraph();
            GraphInstance.getPathwayStatic().getGraph().getVisualizationViewer().repaint();
        } else {
            System.out.println("No Graph exists!");
        }

    }

    private void onFlatSelectedNodesClicked() {
        if (GraphInstance.getMyGraph() != null) {
            for (BiologicalNodeAbstract node : GraphInstance.getPathwayStatic().getGraph().getVisualizationViewer()
                                                            .getPickedVertexState().getPicked()) {
                node.flat();
                GraphInstance.getPathwayStatic().updateMyGraph();
                MainWindow.getInstance().removeTab(false, node.getTab().getTitleTab(), node);
            }
            new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onEnterNodeClicked() {
        if (GraphInstance.getMyGraph() != null) {
            MainWindow w = MainWindow.getInstance();
            GraphContainer con = GraphContainer.getInstance();
            for (BiologicalNodeAbstract node : GraphInstance.getPathwayStatic().getGraph().getVisualizationViewer()
                                                            .getPickedVertexState().getPicked()) {
                if (!node.isCoarseNode() && !node.isMarkedAsCoarseNode()) {
                    continue;
                }
                w.getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                for (BiologicalNodeAbstract n : node.getVertices().keySet()) {
                    node.getVertices().put(n, GraphInstance.getPathwayStatic().getVertices().get(n));
                }
                String newPathwayName = con.addPathway(node.getLabel(), node);
                Pathway pw = con.getPathway(newPathwayName);
                w.addTab(pw.getTab().getTitleTab());
                w.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                GraphInstance.getPathwayStatic().setIsPetriNet(node.isPetriNet());
                w.getBar().paintToolbar(node.isPetriNet());
                w.updateAllGuiElements();
                GraphInstance.getPathwayStatic().updateMyGraph();
                GraphInstance.getPathwayStatic().getGraph().normalCentering();
            }
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onAutoCoarseClicked() {
        if (GraphInstance.getMyGraph() != null) {
            AutoCoarse.coarseSeparatedSubGraphs(GraphInstance.getPathwayStatic());
            new GraphInstance().getPathway().getGraph().getVisualizationViewer().repaint();
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onNewWindowClicked() {
        MainWindow.getInstance().addView();
    }

    private void onHierarchyClicked() {
        GraphContainer con = GraphContainer.getInstance();
        if (con.containsPathway()) {
            con.changeMouseFunction("hierarchy");
            MyGraph g = GraphInstance.getPathwayStatic().getGraph();
            g.disableGraphTheory();
        }
    }

    private void onMergeSelectedNodesClicked() {
        if (GraphInstance.getMyGraph() != null) {
            Pathway pw = GraphInstance.getPathwayStatic();
            pw.mergeNodes(pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onSplitNodeClicked() {
        if (GraphInstance.getMyGraph() != null) {
            Pathway pw = GraphInstance.getPathwayStatic();
            pw.splitNode(pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onAdjustDownClicked() {
        if (GraphInstance.getMyGraph() != null) {
            GraphInstance graphInstance = new GraphInstance();
            Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
            graphInstance.getPathway().adjustDown(nodes);
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onAdjustLeftClicked() {
        if (GraphInstance.getMyGraph() != null) {
            GraphInstance graphInstance = new GraphInstance();
            Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
            graphInstance.getPathway().adjustLeft(nodes);
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onAdjustHorizontalSpaceClicked() {
        if (GraphInstance.getMyGraph() != null) {
            GraphInstance graphInstance = new GraphInstance();
            Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
            graphInstance.getPathway().adjustHorizontalSpace(nodes);
        } else {
            System.out.println("No Graph exists!");
        }
    }

    private void onAdjustVerticalSpaceClicked() {
        if (GraphInstance.getMyGraph() != null) {
            GraphInstance graphInstance = new GraphInstance();
            Set<BiologicalNodeAbstract> nodes = graphInstance.getPathway().getSelectedNodes();
            graphInstance.getPathway().adjustVerticalSpace(nodes);
        } else {
            System.out.println("No Graph exists!");
        }
    }
}
