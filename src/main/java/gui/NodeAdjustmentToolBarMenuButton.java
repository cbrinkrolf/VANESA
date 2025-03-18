package gui;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.GraphInstance;

import java.awt.*;
import java.util.Collection;

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
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.stretchGraph(1.1);
			pathway.updateMyGraph();
		}
	}

	private void onCompressEdgesClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			pathway.stretchGraph(0.9);
			pathway.updateMyGraph();
		}
	}

	private void onAdjustDownClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			Collection<BiologicalNodeAbstract> nodes = pathway.getSelectedNodes();
			pathway.adjustDown(nodes);
		}
	}

	private void onAdjustLeftClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			Collection<BiologicalNodeAbstract> nodes = pathway.getSelectedNodes();
			pathway.adjustLeft(nodes);
		}
	}

	private void onAdjustHorizontalSpaceClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			Collection<BiologicalNodeAbstract> nodes = pathway.getSelectedNodes();
			pathway.adjustHorizontalSpace(nodes);
		}
	}

	private void onAdjustVerticalSpaceClicked() {
		final Pathway pathway = GraphInstance.getPathway();
		if (pathway != null) {
			Collection<BiologicalNodeAbstract> nodes = pathway.getSelectedNodes();
			pathway.adjustVerticalSpace(nodes);
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
