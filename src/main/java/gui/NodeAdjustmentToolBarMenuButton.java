package gui;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphContainer;
import graph.GraphInstance;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

public class NodeAdjustmentToolBarMenuButton extends ToolBarMenuButton {
	private final ToolBarButton adjustDown;
	private final ToolBarButton adjustLeft;
	private final ToolBarButton adjustHorizontalSpace;
	private final ToolBarButton adjustVerticalSpace;

	public NodeAdjustmentToolBarMenuButton() {
		super(ImagePath.getInstance().getImageIcon("nodeAdjust.svg"), new GridLayout(3, 2, 4, 4));
		setToolTipText("Alignment Tools");
		final ToolBarButton stretchEdges = ToolBarButton.create("nodeAdjustStretch.svg", "Stretch Edge Length",
				this::onStretchEdgesClicked);
		final ToolBarButton compressEdges = ToolBarButton.create("nodeAdjustCompress.svg", "Compress Edge Length",
				this::onCompressEdgesClicked);

		adjustDown = ToolBarButton.create("nodeAdjustDown.svg", "Adjust Selected Nodes To Lowest Node",
				this::onAdjustDownClicked);
		adjustLeft = ToolBarButton.create("nodeAdjustLeft.svg", "Adjust Selected Nodes To Left",
				this::onAdjustLeftClicked);
		adjustHorizontalSpace = ToolBarButton.create("nodeAdjustHorizontalSpace.svg",
				"Adjust Horizontal Space of Selected Nodes", this::onAdjustHorizontalSpaceClicked);
		adjustVerticalSpace = ToolBarButton.create("nodeAdjustVerticalSpace.svg",
				"Adjust Vertical Space of Selected Nodes", this::onAdjustVerticalSpaceClicked);

		addMenuButton(compressEdges);
		addMenuButton(stretchEdges);
		addMenuButton(adjustLeft);
		addMenuButton(adjustDown);
		addMenuButton(adjustHorizontalSpace);
		addMenuButton(adjustVerticalSpace);
	}

	private void onStretchEdgesClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			GraphInstance.getPathway().stretchGraph(1.1);
			GraphInstance.getPathway().updateMyGraph();
		}
	}

	private void onCompressEdgesClicked() {
		GraphContainer con = GraphContainer.getInstance();
		if (con.containsPathway()) {
			GraphInstance.getPathway().stretchGraph(0.9);
			GraphInstance.getPathway().updateMyGraph();
		}
	}

	private void onAdjustDownClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Collection<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustDown(nodes);
		}
	}

	private void onAdjustLeftClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Collection<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustLeft(nodes);
		}
	}

	private void onAdjustHorizontalSpaceClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Collection<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustHorizontalSpace(nodes);
		}
	}

	private void onAdjustVerticalSpaceClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Collection<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustVerticalSpace(nodes);
		}
	}

	public void updateEnabledStateForSelectionDependentButtons(final Collection<BiologicalNodeAbstract> selection) {
		final int selectedNodeCount = selection.size();
		adjustDown.setEnabled(selectedNodeCount > 1);
		adjustLeft.setEnabled(selectedNodeCount > 1);
		adjustHorizontalSpace.setEnabled(selectedNodeCount > 2);
		adjustVerticalSpace.setEnabled(selectedNodeCount > 2);
	}
}
