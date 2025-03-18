package gui;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.Workspace;
import graph.GraphContainer;
import graph.GraphInstance;
import graph.hierarchies.AutoCoarse;

import java.awt.*;
import java.util.Collection;
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
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.mergeNodes(pathway.getSelectedNodes());
		}
	}

	private void onGroupClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.groupSelectedNodes();
			pathway.updateMyGraph();
			updateEnabledStateForSelectionDependentButtons(pathway.getSelectedNodes());
		}
	}

	private void onDeleteGroupClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.deleteGroup();
			pathway.updateMyGraph();
		}
	}

	private void onCoarseSelectedNodesClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			Set<BiologicalNodeAbstract> selectedNodes = new HashSet<>(pathway.getSelectedNodes());
			BiologicalNodeAbstract.coarse(selectedNodes);
			pathway.updateMyGraph();
			pathway.getGraph2().clearNodeSelection();
			updateEnabledStateForSelectionDependentButtons(new HashSet<>());
		}
	}

	private void onFlatSelectedNodesClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			for (BiologicalNodeAbstract node : pathway.getSelectedNodes()) {
				node.flat();
				pathway.updateMyGraph();
				MainWindow.getInstance().removeTab(false, node.getTab(), node);
			}
			pathway.getGraph2().clearNodeSelection();
			updateEnabledStateForSelectionDependentButtons(new HashSet<>());
		}
	}

	private void onEnterNodeClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			final MainWindow w = MainWindow.getInstance();
			final GraphContainer con = GraphContainer.getInstance();
			for (final BiologicalNodeAbstract node : pathway.getSelectedNodes()) {
				if (!node.isCoarseNode() && !node.isMarkedAsCoarseNode()) {
					continue;
				}
				w.setCursor(Cursor.WAIT_CURSOR);
				for (final BiologicalNodeAbstract n : node.getVertices().keySet()) {
					node.getVertices().put(n, GraphInstance.getPathway().getVertices().get(n));
				}
				final Pathway enteredPathway = con.addPathway(node.getLabel(), node);
				w.addTab(node.getTab());
				w.setCursor(Cursor.DEFAULT_CURSOR);
				enteredPathway.setIsPetriNet(node.isPetriNet());
				w.getBar().updateVisibility();
				w.updateAllGuiElements();
				enteredPathway.updateMyGraph();
				enteredPathway.getGraphRenderer().zoomAndCenterGraph();
			}
		}
	}

	private void onAutoCoarseClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			AutoCoarse.coarseSeparatedSubGraphs(pathway);
		}
	}

	private void onSplitNodeClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.splitNode(pathway.getSelectedNodes());
		}
	}

	public void updateEnabledStateForSelectionDependentButtons(final Collection<BiologicalNodeAbstract> selection) {
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
