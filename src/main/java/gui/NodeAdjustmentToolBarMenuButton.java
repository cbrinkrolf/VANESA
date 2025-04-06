package gui;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphContainer;
import graph.GraphInstance;

import java.awt.*;
import java.util.Set;

public class NodeAdjustmentToolBarMenuButton extends ToolBarMenuButton {
	private final ToolBarButton adjustVerticalUp;
	private final ToolBarButton adjustVerticalCenter;
	private final ToolBarButton adjustVerticalDown;
	private final ToolBarButton adjustHorizontalLeft;
	private final ToolBarButton adjustHorizontalCenter;
	private final ToolBarButton adjustHorizontalRight;
	private final ToolBarButton adjustHorizontalSpace;
	private final ToolBarButton adjustVerticalSpace;

	public NodeAdjustmentToolBarMenuButton() {
		super(ImagePath.getInstance().getImageIcon("nodeAdjust.svg"), new GridLayout(5, 2, 4, 4));
		setToolTipText("Alignment Tools");
		final ToolBarButton stretchEdges = ToolBarButton.create("nodeAdjustStretch.svg", "Stretch Edge Length",
				this::onStretchEdgesClicked);
		final ToolBarButton compressEdges = ToolBarButton.create("nodeAdjustCompress.svg", "Compress Edge Length",
				this::onCompressEdgesClicked);

		adjustVerticalUp = ToolBarButton.create("nodeAdjustVerticalUp.svg", "Adjust Selected Nodes To Uppermost Node",
				this::onAdjustVerticalUpClicked);
		adjustVerticalCenter = ToolBarButton.create("nodeAdjustVerticalCenter.svg",
				"Adjust Selected Nodes To Vertical Center", this::onAdjustVerticalCenterClicked);
		adjustVerticalDown = ToolBarButton.create("nodeAdjustVerticalDown.svg",
				"Adjust Selected Nodes To Lowermost Node", this::onAdjustVerticalDownClicked);
		adjustHorizontalLeft = ToolBarButton.create("nodeAdjustHorizontalLeft.svg",
				"Adjust Selected Nodes To Leftmost Node", this::onAdjustHorizontalLeftClicked);
		adjustHorizontalCenter = ToolBarButton.create("nodeAdjustHorizontalCenter.svg",
				"Adjust Selected Nodes To Horizontal Center", this::onAdjustHorizontalCenterClicked);
		adjustHorizontalRight = ToolBarButton.create("nodeAdjustHorizontalRight.svg",
				"Adjust Selected Nodes To Rightmost Node", this::onAdjustHorizontalRightClicked);
		adjustHorizontalSpace = ToolBarButton.create("nodeAdjustHorizontalSpace.svg",
				"Adjust Horizontal Space of Selected Nodes", this::onAdjustHorizontalSpaceClicked);
		adjustVerticalSpace = ToolBarButton.create("nodeAdjustVerticalSpace.svg",
				"Adjust Vertical Space of Selected Nodes", this::onAdjustVerticalSpaceClicked);

		addMenuButton(compressEdges);
		addMenuButton(stretchEdges);
		addMenuButton(adjustHorizontalLeft);
		addMenuButton(adjustVerticalUp);
		addMenuButton(adjustHorizontalCenter);
		addMenuButton(adjustVerticalCenter);
		addMenuButton(adjustHorizontalRight);
		addMenuButton(adjustVerticalDown);
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

	private void onAdjustVerticalUpClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustVerticalUp(nodes);
		}
	}

	private void onAdjustVerticalCenterClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustVerticalCenter(nodes);
		}
	}

	private void onAdjustVerticalDownClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustVerticalDown(nodes);
		}
	}

	private void onAdjustHorizontalLeftClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustHorizontalLeft(nodes);
		}
	}

	private void onAdjustHorizontalCenterClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustHorizontalCenter(nodes);
		}
	}

	private void onAdjustHorizontalRightClicked() {
		if (GraphInstance.getMyGraph() != null) {
			Set<BiologicalNodeAbstract> nodes = GraphInstance.getPathway().getSelectedNodes();
			GraphInstance.getPathway().adjustHorizontalRight(nodes);
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

	public void updateEnabledStateForSelectionDependentButtons(final Set<BiologicalNodeAbstract> selection) {
		final int selectedNodeCount = selection.size();
		adjustVerticalUp.setEnabled(selectedNodeCount > 1);
		adjustVerticalCenter.setEnabled(selectedNodeCount > 1);
		adjustVerticalDown.setEnabled(selectedNodeCount > 1);
		adjustHorizontalLeft.setEnabled(selectedNodeCount > 1);
		adjustHorizontalCenter.setEnabled(selectedNodeCount > 1);
		adjustHorizontalRight.setEnabled(selectedNodeCount > 1);
		adjustHorizontalSpace.setEnabled(selectedNodeCount > 2);
		adjustVerticalSpace.setEnabled(selectedNodeCount > 2);
	}
}
