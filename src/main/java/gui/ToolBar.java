package gui;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalElements.PathwayType;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.Workspace;
import graph.*;
import graph.algorithms.gui.CompareGraphsGUI;
import graph.jung.classes.MyGraph;
import net.miginfocom.swing.MigLayout;
import petriNet.PNTableDialog;
import petriNet.ReachController;

public class ToolBar {
	private final JToolBar bar = new JToolBar();
	private final JButton merge;
	private final JButton edit;
	private final NodeAdjustmentToolBarMenuButton nodeAdjustmentMenuButton;
	private final NodeGroupingToolBarMenuButton nodeGroupingMenuButton;
	private final AnnotationToolBarMenuButton annotationToolBarMenuButton;
	private final JButton trash;
	private final JButton center;
	private final JButton move;
	private final JButton zoomIn;
	private final JButton zoomOut;
	private final JButton pick;
	private final JButton hierarchy;
	private final JButton discretePlace;
	private final JButton continuousPlace;
	private final JButton discreteTransition;
	private final JButton continuousTransition;
	private final JButton stochasticTransition;
	private final JButton info;
	private final JButton parallelView;

	private Pathway lastPathway;
	private GraphSelectionChangedListener lastPathwayItemListener;

	public ToolBar() {
		bar.setOrientation(SwingConstants.HORIZONTAL);
		bar.setFloatable(false);
		bar.setLayout(new MigLayout("ins 0"));

		final JButton newBiologicalNetwork = ToolBarButton.create("newBiologicalNetwork.svg",
				"Create New Biological Network", this::onNewBiologicalNetwork);
		final JButton newPetriNet = ToolBarButton.create("newPetriNetwork.svg", "Create New Petri Net",
				this::onNewPetriNet);
		parallelView = ToolBarButton.create("parallelview.png", "Create ParallelView From Graphs",
				this::onParallelViewClicked);
		pick = ToolBarButton.create("newPick.png", "Pick Element", this::onPickClicked);
		hierarchy = ToolBarButton.create("hierarchy.svg", "Hierarchy Mode", this::onHierarchyClicked);
		discretePlace = ToolBarButton.create("discretePlace.png", "Discrete Place", this::onDiscretePlaceClicked);
		continuousPlace = ToolBarButton.create("continuousPlace.png", "Continuous Place",
				this::onContinuousPlaceClicked);
		discreteTransition = ToolBarButton.create("discreteTransition.png", "Discrete Transition",
				this::onDiscreteTransitionClicked);
		continuousTransition = ToolBarButton.create("continuousTransition.png", "Continuous Transition",
				this::onContinuousTransitionClicked);
		stochasticTransition = ToolBarButton.create("stochasticTransition.png", "Stochastic Transition",
				this::onStochasticTransitionClicked);
		center = ToolBarButton.create("centerGraph.png", "Center Graph", this::onCenterClicked);
		move = ToolBarButton.create("move.png", "Move Graph", this::onMoveClicked);
		zoomIn = ToolBarButton.create("zoomPlus.png", "Zoom In", this::onZoomInClicked);
		zoomOut = ToolBarButton.create("zoomMinus.png", "Zoom Out", this::onZoomOutClicked);
		trash = ToolBarButton.create("Trash.png", "Delete Selected Items", this::onDelClicked);
		// trash.setMnemonic(KeyEvent.VK_DELETE);
		info = ToolBarButton.create("showInfoWindow.svg", "Graph Metrics", this::onInfoClicked);
		merge = ToolBarButton.create("compare.png", "Compare / Align Graphs", this::onMergeClicked);

		JButton covGraph = new ToolBarButton("Cov/Reach Graph");
		covGraph.setToolTipText("Create Cov/Reach Graph");
		covGraph.addActionListener(e -> onCreateCovClicked());

		JButton editNodes = new ToolBarButton("Edit PN-Elements");
		editNodes.setToolTipText("Edit PN-Elements");
		editNodes.addActionListener(e -> onEditElementsClicked());

		edit = new ToolBarButton(ImagePath.getInstance().getImageIcon("TitleGraph.png"));
		edit.setToolTipText("Edit Graph");
		edit.addActionListener(e -> onEditClicked());

		nodeAdjustmentMenuButton = new NodeAdjustmentToolBarMenuButton();
		nodeGroupingMenuButton = new NodeGroupingToolBarMenuButton();
		annotationToolBarMenuButton = new AnnotationToolBarMenuButton();

		bar.add(newBiologicalNetwork);
		bar.add(newPetriNet);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(zoomIn);
		bar.add(zoomOut);
		bar.add(center);
		bar.add(info);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(edit);
		bar.add(pick);
		bar.add(move);
		bar.add(trash);
		bar.add(hierarchy);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(discretePlace);
		bar.add(continuousPlace);
		bar.add(discreteTransition);
		bar.add(continuousTransition);
		bar.add(stochasticTransition);
		bar.add(new ToolBarSeparator(), "growy");
		bar.add(merge);
		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			bar.add(parallelView);
		}
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
			final VanesaGraph graph = lastPathway.getGraph2();
			graph.removeSelectionChangedListener(lastPathwayItemListener);
			lastPathwayItemListener = null;
			lastPathway = null;
		}
		if (lastPathway == null && pathway != null) {
			final VanesaGraph graph = pathway.getGraph2();
			if (graph != null) {
				lastPathway = pathway;
				lastPathwayItemListener = this::onPathwaySelectedNodesChanged;
				graph.addSelectionChangedListener(lastPathwayItemListener);
			}
		}
		final boolean petriNetView = pathway != null && pathway.isPetriNet();
		// Enable/disable editing buttons
		final boolean editButtonsEnabled = pathway != null;
		edit.setEnabled(editButtonsEnabled);
		pick.setEnabled(editButtonsEnabled);
		move.setEnabled(editButtonsEnabled);
		trash.setEnabled(editButtonsEnabled);
		hierarchy.setEnabled(editButtonsEnabled);
		discretePlace.setEnabled(editButtonsEnabled);
		continuousPlace.setEnabled(editButtonsEnabled);
		discreteTransition.setEnabled(editButtonsEnabled);
		continuousTransition.setEnabled(editButtonsEnabled);
		stochasticTransition.setEnabled(editButtonsEnabled);
		zoomIn.setEnabled(editButtonsEnabled);
		zoomOut.setEnabled(editButtonsEnabled);
		center.setEnabled(editButtonsEnabled);
		info.setEnabled(editButtonsEnabled);
		annotationToolBarMenuButton.setEnabled(editButtonsEnabled);
		nodeGroupingMenuButton.setEnabled(editButtonsEnabled);
		merge.setEnabled(editButtonsEnabled);
		parallelView.setEnabled(editButtonsEnabled);
		nodeAdjustmentMenuButton.setEnabled(editButtonsEnabled);

		if (editButtonsEnabled) {
			// Enable/disable petri net buttons
			discretePlace.setEnabled(petriNetView);
			continuousPlace.setEnabled(petriNetView);
			discreteTransition.setEnabled(petriNetView);
			continuousTransition.setEnabled(petriNetView);
			stochasticTransition.setEnabled(petriNetView);
			// Enable/disable biological graph buttons
			edit.setEnabled(!petriNetView);
			merge.setEnabled(!petriNetView);
			// heatmap.setEnabled(!petriNetView);
			// parallelView.setEnabled(!petriNetView);

			final VanesaGraph graph = lastPathway.getGraph2();
			if (graph != null) {
				final Collection<BiologicalNodeAbstract> selection = lastPathway.getSelectedNodes();
				updateEnabledStateForSelectionDependentButtons(selection);
			}
		}
	}

	private void onPathwaySelectedNodesChanged() {
		if (lastPathway == null) {
			return;
		}
		final VanesaGraph graph = lastPathway.getGraph2();
		if (graph != null) {
			final Collection<BiologicalNodeAbstract> selection = lastPathway.getSelectedNodes();
			updateEnabledStateForSelectionDependentButtons(selection);
		}
	}

	private void updateEnabledStateForSelectionDependentButtons(final Collection<BiologicalNodeAbstract> selection) {
		final int selectedNodeCount = selection.size();
		nodeAdjustmentMenuButton.updateEnabledStateForSelectionDependentButtons(selection);
		nodeGroupingMenuButton.updateEnabledStateForSelectionDependentButtons(selection);
		trash.setEnabled(selectedNodeCount > 0);
	}

	private void onNewBiologicalNetwork() {
		CreatePathway.create(PathwayType.BiologicalNetwork);
		MainWindow.getInstance().getBar().updateVisibility();
		MainWindow.getInstance().updateAllGuiElements();
	}

	private void onNewPetriNet() {
		CreatePathway.create(PathwayType.PetriNet);
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
			GraphInstance.getPathway().getGraphRenderer().zoomAndCenterGraph(100);
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
				new GraphInfoWindow();
			}
		}
	}

	private void onInfoExtendedClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			if (GraphInstance.getPathway().hasGotAtLeastOneElement()) {
				new GraphInfoWindow();
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
