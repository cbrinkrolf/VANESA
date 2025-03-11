package gui;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.Workspace;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.hierarchies.AutoCoarse;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class NodeGroupingToolBarMenuButton extends ToolBarMenuButton {
	private final ToolBarButton mergeSelectedNodes;
	private final ToolBarButton splitNode;
	private final ToolBarButton coarseSelectedNodes;
	private final ToolBarButton flatSelectedNodes;
	private final ToolBarButton groupSelectedNodes;
	private final ToolBarButton deleteGroup;
	private final ToolBarButton enterSelectedNode;

	public NodeGroupingToolBarMenuButton() {
		super(ImagePath.getInstance().getImageIcon("grouping.svg"), new GridLayout(4, 2, 4, 4));
		setToolTipText("Grouping Tools");

		mergeSelectedNodes = ToolBarButton.create("MergeNodesButton.png", "Merge Selected Nodes",
				this::onMergeSelectedNodesClicked);
		splitNode = ToolBarButton.create("SplitNodesButton.png", "Split Node (inverse operation of \"merge nodes\")",
				this::onSplitNodeClicked);
		coarseSelectedNodes = ToolBarButton.create("CoarseNodesButton.png", "Coarse Selected Nodes",
				this::onCoarseSelectedNodesClicked);
		flatSelectedNodes = ToolBarButton.create("FlatNodesButton.png", "Flat Selected Coarse Node(s)",
				this::onFlatSelectedNodesClicked);
		groupSelectedNodes = ToolBarButton.create("GroupButton.png", "Group Selected Nodes", this::onGroupClicked);
		deleteGroup = ToolBarButton.create("UngroupButton.png", "Delete Selected Group", this::onDeleteGroupClicked);
		enterSelectedNode = ToolBarButton.create("enterNode.png", "Enter Selected Coarse Node(s)",
				this::onEnterNodeClicked);
		final ToolBarButton autoCoarse = ToolBarButton.create("autocoarse.png", "Autocoarse Current Pathway",
				this::onAutoCoarseClicked);

		addMenuButton(mergeSelectedNodes);
		addMenuButton(splitNode);
		addMenuButton(coarseSelectedNodes);
		addMenuButton(flatSelectedNodes);
		addMenuButton(enterSelectedNode);
		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			addMenuButton(autoCoarse);
		}
		addMenuButton(groupSelectedNodes);
		addMenuButton(deleteGroup);
	}

	private void onMergeSelectedNodesClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Pathway pw = GraphInstance.getPathway();
			pw.mergeNodes(pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
		}
	}

	private void onGroupClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			GraphInstance.getPathway().groupSelectedNodes();
			GraphInstance.getPathway().updateMyGraph();
			final Set<BiologicalNodeAbstract> selection = GraphInstance.getPathway().getSelectedNodes();
			updateEnabledStateForSelectionDependentButtons(selection);
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
			final Pathway pathway = GraphInstance.getPathway();
			final PickedState<BiologicalNodeAbstract> pickedState = pathway.getGraph().getVisualizationViewer()
					.getPickedVertexState();
			Set<BiologicalNodeAbstract> selectedNodes = new HashSet<>(pickedState.getPicked());
			BiologicalNodeAbstract.coarse(selectedNodes);
			GraphInstance.getPathway().updateMyGraph();
			GraphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
			pickedState.clear();
			updateEnabledStateForSelectionDependentButtons(new HashSet<>());
		}
	}

	private void onFlatSelectedNodesClicked() {
		if (GraphInstance.getMyGraph() != null) {
			final Pathway pathway = GraphInstance.getPathway();
			final PickedState<BiologicalNodeAbstract> pickedState = pathway.getGraph().getVisualizationViewer()
					.getPickedVertexState();
			for (BiologicalNodeAbstract node : pickedState.getPicked()) {
				node.flat();
				pathway.updateMyGraph();
				MainWindow.getInstance().removeTab(false, node.getTab(), node);
			}
			pathway.getGraph().getVisualizationViewer().repaint();
			pickedState.clear();
			updateEnabledStateForSelectionDependentButtons(new HashSet<>());
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
				w.addTab(pw.getTab());
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

	private void onSplitNodeClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Pathway pw = GraphInstance.getPathway();
			pw.splitNode(pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked());
		}
	}

	public void updateEnabledStateForSelectionDependentButtons(final Set<BiologicalNodeAbstract> selection) {
		final int selectedNodeCount = selection.size();
		mergeSelectedNodes.setEnabled(selectedNodeCount > 1);
		splitNode.setEnabled(selectedNodeCount > 0);
		coarseSelectedNodes.setEnabled(selectedNodeCount > 1);
		final boolean allCoarseNodes = selection.stream().allMatch(BiologicalNodeAbstract::isCoarseNode);
		flatSelectedNodes.setEnabled(selectedNodeCount > 0 && allCoarseNodes);
		// Restrict entering a coarse node to a single node as it crashes otherwise.
		enterSelectedNode.setEnabled(selectedNodeCount == 1 && allCoarseNodes);
		groupSelectedNodes.setEnabled(selectedNodeCount > 1);
		deleteGroup.setEnabled(selectedNodeCount > 0 && selection.stream().allMatch(BiologicalNodeAbstract::isInGroup));
	}
}
