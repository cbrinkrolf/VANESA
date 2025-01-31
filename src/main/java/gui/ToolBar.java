package gui;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.gui.CompareGraphsGUI;
import graph.hierarchies.AutoCoarse;
import graph.jung.classes.MyGraph;
import net.miginfocom.swing.MigLayout;
import petriNet.PNTableDialog;
import petriNet.ReachController;

public class ToolBar {
	private final JToolBar bar = new JToolBar();
    private final JButton merge;
    private final JButton parallelView;
	private final JButton edit;
	private final JPanel petriNetControls;
	private final JPanel editControls;
	private final JPanel viewPortControls;
	private final JPanel infoPanel;
	private final JPanel featureControls;
	private final JPanel nodeAdjustmentControls;

	public ToolBar() {
		bar.setOrientation(1);
		bar.setFloatable(true);
		MigLayout bl = new MigLayout("insets 0, wrap 1");
		bar.setLayout(bl);

		final JButton newDoc = new NewDocumentToolBarButton();
		parallelView = createToolBarButton("parallelview.png", "Create ParallelView From Graphs",
										   this::onParallelViewClicked);
		final JButton pick = createToolBarButton("newPick.png", "Pick Element", this::onPickClicked);
		final JButton hierarchy = createToolBarButton("hierarchy_button.png", "Hierarchy Mode",
													  this::onHierarchyClicked);
		final JButton discretePlace = createToolBarButton("discretePlace.png", "Discrete Place",
														  this::onDiscretePlaceClicked);
		final JButton continuousPlace = createToolBarButton("continuousPlace.png", "Continuous Place",
															this::onContinuousPlaceClicked);
		final JButton discreteTransition = createToolBarButton("discreteTransition.png", "Discrete Transition",
															   this::onDiscreteTransitionClicked);
		final JButton continuousTransition = createToolBarButton("continuousTransition.png", "Continuous Transition",
																 this::onContinuousTransitionClicked);
		final JButton stochasticTransition = createToolBarButton("stochasticTransition.png", "Stochastic Transition",
																 this::onStochasticTransitionClicked);
		final JButton center = createToolBarButton("centerGraph.png", "Center Graph", this::onCenterClicked);
		final JButton move = createToolBarButton("move.png", "Move Graph", this::onMoveClicked);
		final JButton zoomIn = createToolBarButton("zoomPlus.png", "Zoom In", this::onZoomInClicked);
		final JButton zoomOut = createToolBarButton("zoomMinus.png", "Zoom Out", this::onZoomOutClicked);
		final JButton trash = createToolBarButton("Trash.png", "Delete Selected Items", this::onDelClicked);
		// trash.setMnemonic(KeyEvent.VK_DELETE);
		final JButton info = createToolBarButton("InfoToolBarButton.png", "Info", this::onInfoClicked);
		final JButton infoExtended = createToolBarButton("InfoToolBarButtonextended.png", "More Info",
														 this::onInfoExtendedClicked);
		final JButton mergeSelectedNodes = createToolBarButton("MergeNodesButton.png", "Merge Selected Nodes",
															   this::onMergeSelectedNodesClicked);
		final JButton splitNode = createToolBarButton("SplitNodesButton.png",
													  "Split Node (inverse operation of \"merge nodes\")",
													  this::onSplitNodeClicked);
		final JButton coarseSelectedNodes = createToolBarButton("CoarseNodesButton.png", "Coarse Selected Nodes",
																this::onCoarseSelectedNodesClicked);
		final JButton flatSelectedNodes = createToolBarButton("FlatNodesButton.png", "Flat Selected Coarse Node(s)",
															  this::onFlatSelectedNodesClicked);
		final JButton groupSelectedNodes = createToolBarButton("GroupButton.png", "Group Selected Nodes",
															   this::onGroupClicked);
		final JButton deleteGroup = createToolBarButton("UngroupButton.png", "Delete Selected Group",
														this::onDeleteGroupClicked);
		final JButton enterSelectedNode = createToolBarButton("enterNode.png", "Enter Selected Coarse Node(s)",
															  this::onEnterNodeClicked);
		final JButton autoCoarse = createToolBarButton("autocoarse.png", "Autocoarse Current Pathway",
													   this::onAutoCoarseClicked);
		final JButton newWindow = createToolBarButton("newWindow.png", "Open New Window", this::onNewWindowClicked);

		/*
		 * JButton convertIntoPetriNet = new ToolBarButton("convertIntoPetriNet");
		 * convertIntoPetriNet.setToolTipText("Convert Into Petri Net");
		 * convertIntoPetriNet.setActionCommand(ToolbarActionCommands.convertIntoPetriNet.value);
		 * convertIntoPetriNet.addActionListener(ToolBarListener.getInstance());
		 */

		final JButton fullScreen = createToolBarButton("newFullScreen.png", "Full Screen", this::onFullScreenClicked);
		final JButton stretchEdges = createToolBarButton("stretchEdges.png", "Stretch Edge Length",
														 this::onStretchEdgesClicked);
		final JButton compressEdges = createToolBarButton("compressEdges.png", "Compress Edge Length",
														  this::onCompressEdgesClicked);
		merge = createToolBarButton("compare.png", "Compare / Align Graphs", this::onMergeClicked);

		infoPanel = new ToolBarPanel();
		infoPanel.setLayout(new GridLayout(0, 3, 4, 4));
		infoPanel.add(mergeSelectedNodes);
		infoPanel.add(splitNode);
		infoPanel.add(new JLabel());
		infoPanel.add(coarseSelectedNodes);
		infoPanel.add(flatSelectedNodes);
		infoPanel.add(enterSelectedNode);
		if (MainWindow.developer) {
			infoPanel.add(autoCoarse);
		}
		infoPanel.add(groupSelectedNodes);
		infoPanel.add(deleteGroup);

		final ButtonChooser chooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectShapeActions());
		chooser.setToolTipText("Draw compartments");
		final ButtonChooser colorChooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectColorActions());
		colorChooser.setToolTipText("Set compartment colors");

		JButton covGraph = new ToolBarButton("Cov/Reach Graph");
		covGraph.setToolTipText("Create Cov/Reach Graph");
		covGraph.addActionListener(e -> onCreateCovClicked());

		JButton editNodes = new ToolBarButton("Edit PN-Elements");
		editNodes.setToolTipText("Edit PN-Elements");
		editNodes.addActionListener(e -> onEditElementsClicked());

		// JButton heatmap = createToolBarButton("heatmapGraph.png", "Create Heatgraph",
		// ToolbarActionCommands.heatmap.value);

		edit = new ToolBarButton(ImagePath.getInstance().getImageIcon("TitleGraph.png"));
		edit.setSelectedIcon(ImagePath.getInstance().getImageIcon("editSelected.png"));
		edit.setToolTipText("Edit Graph");
		edit.addActionListener(e -> onEditClicked());

		viewPortControls = new ToolBarPanel();
		viewPortControls.setLayout(new GridLayout(2, 3, 4, 4));
		viewPortControls.add(fullScreen);
		viewPortControls.add(zoomIn);
		viewPortControls.add(zoomOut);
		viewPortControls.add(center);
		viewPortControls.add(info);
		viewPortControls.add(infoExtended);

		editControls = new ToolBarPanel();
		editControls.setLayout(new GridLayout(2, 3, 4, 4));
		editControls.add(edit);
		editControls.add(pick);
		editControls.add(move);
		editControls.add(trash);
		editControls.add(hierarchy);

		JPanel printControls = new ToolBarPanel();
		printControls.setLayout(new GridLayout(1, 2, 4, 4));
		printControls.add(newDoc);
		printControls.add(newWindow);

		// Add buttons to experiment with Grid Layout
		petriNetControls = new ToolBarPanel();
		petriNetControls.setLayout(new GridLayout(2, 3, 4, 4));

		petriNetControls.add(discretePlace);
		petriNetControls.add(continuousPlace);
		petriNetControls.add(new JLabel());
		petriNetControls.add(discreteTransition);
		petriNetControls.add(continuousTransition);
		petriNetControls.add(stochasticTransition);

		featureControls = new ToolBarPanel();
		featureControls.setLayout(new GridLayout(1, 3, 4, 4));
		featureControls.add(merge);
		if (MainWindow.developer) {
			// featureControls.add(heatmap);
			featureControls.add(parallelView);
		}
		featureControls.add(chooser);
		featureControls.add(colorChooser);

		// featureControls.setMaximumSize(featureControls.getPreferredSize());
		// featureControls.setAlignmentX(Component.LEFT_ALIGNMENT);
		// featureControls.setAlignmentY(Component.TOP_ALIGNMENT);

		JPanel toolBarControlControls = new ToolBarPanel();
		toolBarControlControls.setLayout(new GridLayout(2, 1, 4, 4));
		// if (!petriNetView) {
		// toolBarControlControls.add(petriNet);
		// toolBarControlControls.add(convertIntoPetriNet);
		// }

		JButton adjustDown = createToolBarButton("adjustDown.png", "Adjust Selected Nodes To Lowest Node",
												 this::onAdjustDownClicked);
		JButton adjustLeft = createToolBarButton("adjustLeft.png", "Adjust Selected Nodes To Left",
												 this::onAdjustLeftClicked);
		JButton adjustHorizontalSpace = createToolBarButton("adjustHorizontalSpace.png",
															"Adjust Horizontal Space of Selected Nodes",
															this::onAdjustHorizontalSpaceClicked);
		JButton adjustVerticalSpace = createToolBarButton("adjustVerticalSpace.png",
														  "Adjust Vertical Space of Selected Nodes",
														  this::onAdjustVerticalSpaceClicked);
		nodeAdjustmentControls = new ToolBarPanel();
		nodeAdjustmentControls.setLayout(new GridLayout(2, 2, 4, 4));
		nodeAdjustmentControls.add(compressEdges);
		nodeAdjustmentControls.add(adjustDown);
		nodeAdjustmentControls.add(adjustHorizontalSpace);
		nodeAdjustmentControls.add(stretchEdges);
		nodeAdjustmentControls.add(adjustLeft);
		nodeAdjustmentControls.add(adjustVerticalSpace);

		// bar.add(toolBarControlControls);
		// if (petriNetView) {
		//   toolBarControlControls.add(covGraph);
		//   toolBarControlControls.add(editNodes);
		//   toolBarControlControls.add(loadModResult);
		//   toolBarControlControls.add(simulate);
		// }
		// bar.add(new JSeparator());
		bar.add(printControls);
		bar.add(new ToolBarSeparator(), "growx, wrap");
		bar.add(editControls);
		bar.add(new ToolBarSeparator(), "growx, wrap");
		bar.add(petriNetControls);
		bar.add(new ToolBarSeparator(), "growx, wrap");
		bar.add(featureControls, "wrap");
		bar.add(new ToolBarSeparator(), "growx, wrap");
		bar.add(viewPortControls);
		bar.add(new ToolBarSeparator(), "growx, wrap");
		bar.add(nodeAdjustmentControls);
		bar.add(new ToolBarSeparator(), "growx, wrap");
		bar.add(infoPanel, "wrap");

		bar.validate();
		bar.repaint();
		bar.setVisible(true);

		updateVisibility();
	}

	public JToolBar getToolBar() {
		return bar;
	}

	public void updateVisibility() {
		final GraphContainer con = GraphContainer.getInstance();
		final boolean petriNetView =
				con.containsPathway() && GraphInstance.getPathway() != null && GraphInstance.getPathway().isPetriNet();
		// Enable/disable editing buttons
		final boolean editButtonsEnabled = con.containsPathway() && GraphInstance.getPathway() != null;
		for (final Component child : editControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		for (final Component child : petriNetControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		for (final Component child : viewPortControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		for (final Component child : infoPanel.getComponents())
			child.setEnabled(editButtonsEnabled);
		for (final Component child : featureControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		for (final Component child : nodeAdjustmentControls.getComponents())
			child.setEnabled(editButtonsEnabled);

		if (editButtonsEnabled) {
			// Enable/disable petri net buttons
			for (final Component child : petriNetControls.getComponents())
				child.setEnabled(petriNetView);
			// Enable/disable biological graph buttons
			edit.setEnabled(!petriNetView);
			merge.setEnabled(!petriNetView);
			// heatmap.setEnabled(!petriNetView);
			parallelView.setEnabled(!petriNetView);
		}
	}

	private static JButton createToolBarButton(String imageFileName, String toolTipText, Runnable action) {
		JButton button = new ToolBarButton(ImagePath.getInstance().getImageIcon(imageFileName));
		button.setToolTipText(toolTipText);
		button.addActionListener(e -> action.run());
		return button;
	}

	private void onParallelViewClicked() {
		// create a graph choosing popup and calculate network properties
		new ParallelChooseGraphsWindow();
	}

	private void onMoveClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			con.changeMouseFunction("move");
			MyGraph g = GraphInstance.getPathway().getGraph();
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
			MyGraph g = GraphInstance.getPathway().getGraph();
			g.disableGraphTheory();
		}
	}

	private void onCenterClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			// CENTERING WITH SCALING
			GraphInstance.getPathway().getGraph().normalCentering();
			// ONLY FOR CENTERING, NOT SCALING
			// graphInstance.getPathway().getGraph().animatedCentering();
		}
	}

	private void onZoomInClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			MyGraph g = GraphInstance.getPathway().getGraph();
			g.zoomIn();
		}
	}

	private void onZoomOutClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			MyGraph g = GraphInstance.getPathway().getGraph();
			g.zoomOut();
		}
	}

	private void onFullScreenClicked() {
		MainWindow.getInstance().setFullScreen();
	}

	private void onCompressEdgesClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			GraphInstance.getPathway().stretchGraph(0.9);
			GraphInstance.getPathway().updateMyGraph();
		}
	}

	private void onStretchEdgesClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			GraphInstance.getPathway().stretchGraph(1.1);
			GraphInstance.getPathway().updateMyGraph();
		}
	}

	private void onEditClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			con.changeMouseFunction("edit");
			MyGraph g = GraphInstance.getPathway().getGraph();
			g.disableGraphTheory();
		}
	}

	private void onMergeClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.getPathwayNumbers() > 1) {
			MyGraph g = GraphInstance.getPathway().getGraph();
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
			GraphInstance.getPathway().removeSelection();
			w.updateElementTree();
			w.updatePathwayTree();
			// w.updateTheoryProperties();
			// g.restartVisualizationModel();
		}
	}

	private void onInfoClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			if (GraphInstance.getPathway().hasGotAtLeastOneElement()) {
				new InfoWindow(false);
			}
		}
	}

	private void onInfoExtendedClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			if (GraphInstance.getPathway().hasGotAtLeastOneElement()) {
				new InfoWindow(true);
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

	private void onCreateCovClicked() {
		if (JOptionPane.showConfirmDialog(MainWindow.getInstance().getFrame(),
				"Calculating the reachability graph may take a long time, especially for large networks. Calculate anyway?",
				"Please confirm your action...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			new ReachController(GraphInstance.getPathway());
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

	private void onGroupClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			GraphInstance.getPathway().groupSelectedNodes();
			GraphInstance.getPathway().updateMyGraph();
		}
	}

	private void onDeleteGroupClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			GraphInstance.getPathway().deleteGroup();
			GraphInstance.getPathway().updateMyGraph();
		}
	}

	private void onCoarseSelectedNodesClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> selectedNodes = new HashSet<>(GraphInstance.getPathway().getSelectedNodes());
			BiologicalNodeAbstract.coarse(selectedNodes);
			GraphInstance.getPathway().updateMyGraph();
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
		}

	}

	private void onFlatSelectedNodesClicked() {
		if (GraphInstance.getMyGraph() != null) {
			for (BiologicalNodeAbstract node : GraphInstance.getPathway().getGraph().getVisualizationViewer()
					.getPickedVertexState().getPicked()) {
				node.flat();
				GraphInstance.getPathway().updateMyGraph();
				MainWindow.getInstance().removeTab(false, node.getTab().getTitleTab(), node);
			}
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
		}
	}

	private void onEnterNodeClicked() {
		if (GraphInstance.getMyGraph() != null) {
			MainWindow w = MainWindow.getInstance();
			GraphContainer con = GraphContainer.getInstance();
			for (BiologicalNodeAbstract node : GraphInstance.getPathway().getGraph().getVisualizationViewer()
					.getPickedVertexState().getPicked()) {
				if (!node.isCoarseNode() && !node.isMarkedAsCoarseNode()) {
					continue;
				}
				w.setCursor(Cursor.WAIT_CURSOR);
				for (BiologicalNodeAbstract n : node.getVertices().keySet()) {
					node.getVertices().put(n, GraphInstance.getPathway().getVertices().get(n));
				}
				String newPathwayName = con.addPathway(node.getLabel(), node);
				Pathway pw = con.getPathway(newPathwayName);
				w.addTab(pw.getTab().getTitleTab());
				w.setCursor(Cursor.DEFAULT_CURSOR);
				GraphInstance.getPathway().setIsPetriNet(node.isPetriNet());
				w.getBar().updateVisibility();
				w.updateAllGuiElements();
				GraphInstance.getPathway().updateMyGraph();
				GraphInstance.getPathway().getGraph().normalCentering();
			}
		}
	}

	private void onAutoCoarseClicked() {
		if (GraphInstance.getMyGraph() != null) {
			AutoCoarse.coarseSeparatedSubGraphs(GraphInstance.getPathway());
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
		}
	}

	private void onNewWindowClicked() {
		MainWindow.getInstance().addView();
	}

	private void onHierarchyClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			con.changeMouseFunction("hierarchy");
			MyGraph g = GraphInstance.getPathway().getGraph();
			g.disableGraphTheory();
		}
	}

	private void onMergeSelectedNodesClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Pathway pw = GraphInstance.getPathway();
			pw.mergeNodes(pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
		}
	}

	private void onSplitNodeClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Pathway pw = GraphInstance.getPathway();
			pw.splitNode(pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
		}
	}

	private void onAdjustDownClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustDown(nodes);
		}
	}

	private void onAdjustLeftClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustLeft(nodes);
		}
	}

	private void onAdjustHorizontalSpaceClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustHorizontalSpace(nodes);
		}
	}

	private void onAdjustVerticalSpaceClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustVerticalSpace(nodes);
		}
	}
}
