package gui;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.*;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.SettingsManager;
import graph.CreatePathway;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.algorithms.gui.CompareGraphsGUI;
import graph.jung.classes.MyGraph;
import net.miginfocom.swing.MigLayout;
import petriNet.PNTableDialog;
import petriNet.ReachController;

public class ToolBar {
	private final JToolBar bar = new JToolBar();
	private final JButton merge;
	private final JButton edit;
	private final JPanel petriNetControls;
	private final JPanel editControls;
	private final JPanel viewPortControls;
	private final JPanel featureControls;
	private final NodeAdjustmentToolBarMenuButton nodeAdjustmentMenuButton;
	private final NodeGroupingToolBarMenuButton nodeGroupingMenuButton;
	private final AnnotationToolBarMenuButton annotationToolBarMenuButton;
	private final JButton trash;

	private Pathway lastPathway;
	private ItemListener lastPathwayItemListener;

	public ToolBar() {
		bar.setOrientation(SwingConstants.HORIZONTAL);
		bar.setFloatable(false);
		bar.setLayout(new MigLayout("insets 0"));

		final JButton newBiologicalNetwork = ToolBarButton.create("newBiologicalNetwork.svg",
				"Create New Biological Network", this::onNewBiologicalNetwork);
		final JButton newPetriNet = ToolBarButton.create("newPetriNetwork.svg", "Create New Petri Net",
				this::onNewPetriNet);
		final JButton parallelView = ToolBarButton.create("parallelview.png", "Create ParallelView From Graphs",
				this::onParallelViewClicked);
		final JButton pick = ToolBarButton.create("newPick.png", "Pick Element", this::onPickClicked);
		final JButton hierarchy = ToolBarButton.create("hierarchy.svg", "Hierarchy Mode", this::onHierarchyClicked);
		final JButton discretePlace = ToolBarButton.create("discretePlace.png", "Discrete Place",
				this::onDiscretePlaceClicked);
		final JButton continuousPlace = ToolBarButton.create("continuousPlace.png", "Continuous Place",
				this::onContinuousPlaceClicked);
		final JButton discreteTransition = ToolBarButton.create("discreteTransition.png", "Discrete Transition",
				this::onDiscreteTransitionClicked);
		final JButton continuousTransition = ToolBarButton.create("continuousTransition.png", "Continuous Transition",
				this::onContinuousTransitionClicked);
		final JButton stochasticTransition = ToolBarButton.create("stochasticTransition.png", "Stochastic Transition",
				this::onStochasticTransitionClicked);
		final JButton center = ToolBarButton.create("centerGraph.png", "Center Graph", this::onCenterClicked);
		final JButton move = ToolBarButton.create("move.png", "Move Graph", this::onMoveClicked);
		final JButton zoomIn = ToolBarButton.create("zoomPlus.png", "Zoom In", this::onZoomInClicked);
		final JButton zoomOut = ToolBarButton.create("zoomMinus.png", "Zoom Out", this::onZoomOutClicked);
		trash = ToolBarButton.create("Trash.png", "Delete Selected Items", this::onDelClicked);
		// trash.setMnemonic(KeyEvent.VK_DELETE);
		final JButton info = ToolBarButton.create("InfoToolBarButton.png", "Info", this::onInfoClicked);
		final JButton infoExtended = ToolBarButton.create("InfoToolBarButtonextended.png", "More Info",
				this::onInfoExtendedClicked);

		final JButton fullScreen = ToolBarButton.create("newFullScreen.png", "Full Screen", this::onFullScreenClicked);
		merge = ToolBarButton.create("compare.png", "Compare / Align Graphs", this::onMergeClicked);

		JButton covGraph = new ToolBarButton("Cov/Reach Graph");
		covGraph.setToolTipText("Create Cov/Reach Graph");
		covGraph.addActionListener(e -> onCreateCovClicked());

		JButton editNodes = new ToolBarButton("Edit PN-Elements");
		editNodes.setToolTipText("Edit PN-Elements");
		editNodes.addActionListener(e -> onEditElementsClicked());

		// JButton heatmap = ToolBarButton.create("heatmapGraph.png", "Create Heatgraph",
		// ToolbarActionCommands.heatmap.value);

		edit = new ToolBarButton(ImagePath.getInstance().getImageIcon("TitleGraph.png"));
		edit.setToolTipText("Edit Graph");
		edit.addActionListener(e -> onEditClicked());

		viewPortControls = new ToolBarPanel();
		viewPortControls.setLayout(new GridLayout(1, 6, 4, 4));
		viewPortControls.add(fullScreen);
		viewPortControls.add(zoomIn);
		viewPortControls.add(zoomOut);
		viewPortControls.add(center);
		viewPortControls.add(info);
		viewPortControls.add(infoExtended);

		editControls = new ToolBarPanel();
		editControls.setLayout(new GridLayout(1, 6, 4, 4));
		editControls.add(edit);
		editControls.add(pick);
		editControls.add(move);
		editControls.add(trash);
		editControls.add(hierarchy);

		petriNetControls = new ToolBarPanel();
		petriNetControls.setLayout(new GridLayout(1, 6, 4, 4));
		petriNetControls.add(discretePlace);
		petriNetControls.add(continuousPlace);
		petriNetControls.add(discreteTransition);
		petriNetControls.add(continuousTransition);
		petriNetControls.add(stochasticTransition);

		featureControls = new ToolBarPanel();
		featureControls.setLayout(new GridLayout(1, 4, 4, 4));
		featureControls.add(merge);
		if (SettingsManager.getInstance().isDeveloperMode()) {
			featureControls.add(parallelView);
		}

		nodeAdjustmentMenuButton = new NodeAdjustmentToolBarMenuButton();
		nodeGroupingMenuButton = new NodeGroupingToolBarMenuButton();
		annotationToolBarMenuButton = new AnnotationToolBarMenuButton();

		bar.add(newBiologicalNetwork);
		bar.add(newPetriNet);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(viewPortControls);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(editControls);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(petriNetControls);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(featureControls);
		bar.add(annotationToolBarMenuButton);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(nodeAdjustmentMenuButton);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(nodeGroupingMenuButton);

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
		final Pathway pathway = con.containsPathway() ? GraphInstance.getPathway() : null;
		if (lastPathway != null && lastPathway != pathway) {
			final MyGraph graph = lastPathway.getGraph(false);
			if (graph != null) {
				graph.getVisualizationViewer().getPickedVertexState().removeItemListener(lastPathwayItemListener);
			}
			lastPathwayItemListener = null;
			lastPathway = null;
		}
		if (lastPathway == null && pathway != null) {
			final MyGraph graph = pathway.getGraph(false);
			if (graph != null) {
				lastPathway = pathway;
				lastPathwayItemListener = this::onPathwaySelectedNodesChanged;
				graph.getVisualizationViewer().getPickedVertexState().addItemListener(lastPathwayItemListener);
			}
		}
		final boolean petriNetView = pathway != null && pathway.isPetriNet();
		// Enable/disable editing buttons
		final boolean editButtonsEnabled = pathway != null;
		for (final Component child : editControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		for (final Component child : petriNetControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		for (final Component child : viewPortControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		annotationToolBarMenuButton.setEnabled(editButtonsEnabled);
		nodeGroupingMenuButton.setEnabled(editButtonsEnabled);
		for (final Component child : featureControls.getComponents())
			child.setEnabled(editButtonsEnabled);
		nodeAdjustmentMenuButton.setEnabled(editButtonsEnabled);

		if (editButtonsEnabled) {
			// Enable/disable petri net buttons
			for (final Component child : petriNetControls.getComponents())
				child.setEnabled(petriNetView);
			// Enable/disable biological graph buttons
			edit.setEnabled(!petriNetView);
			merge.setEnabled(!petriNetView);
			// heatmap.setEnabled(!petriNetView);
			//parallelView.setEnabled(!petriNetView);

			final MyGraph graph = pathway.getGraph(false);
			if (graph != null) {
				final Set<BiologicalNodeAbstract> selection = lastPathway.getSelectedNodes();
				updateEnabledStateForSelectionDependentButtons(selection);
			}
		}
	}

	private void onPathwaySelectedNodesChanged(ItemEvent e) {
		if (lastPathway == null) {
			return;
		}
		final MyGraph graph = lastPathway.getGraph(false);
		if (graph != null) {
			final Set<BiologicalNodeAbstract> selection = lastPathway.getSelectedNodes();
			updateEnabledStateForSelectionDependentButtons(selection);
		}
	}

	private void updateEnabledStateForSelectionDependentButtons(final Set<BiologicalNodeAbstract> selection) {
		final int selectedNodeCount = selection.size();
		nodeAdjustmentMenuButton.updateEnabledStateForSelectionDependentButtons(selection);
		nodeGroupingMenuButton.updateEnabledStateForSelectionDependentButtons(selection);
		trash.setEnabled(selectedNodeCount > 0);
	}

	private void onNewBiologicalNetwork() {
		new CreatePathway();
		GraphInstance.getPathway().setIsPetriNet(false);
		MainWindow.getInstance().getBar().updateVisibility();
		MainWindow.getInstance().updateAllGuiElements();
	}

	private void onNewPetriNet() {
		new CreatePathway();
		GraphInstance.getPathway().setIsPetriNet(true);
		MainWindow.getInstance().getBar().updateVisibility();
		MainWindow.getInstance().updateAllGuiElements();
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

	private void onHierarchyClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			con.changeMouseFunction("hierarchy");
			MyGraph g = GraphInstance.getPathway().getGraph();
			g.disableGraphTheory();
		}
	}
}
