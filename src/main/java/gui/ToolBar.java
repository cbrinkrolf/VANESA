package gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import gui.eventhandlers.ToolBarListener;
import gui.eventhandlers.ToolbarActionCommands;
import gui.images.ImagePath;
import net.miginfocom.swing.MigLayout;

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
		if (bar != null && isPetri && petriNetView) {
			return;
		}
		if (bar != null && !isPetri && !petriNetView) {
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

		String modelingViewString = "<html>" + "<b>Change View</b> <br>" + "to Modeling" + "</html>";
		// String PetriViewString = "<html>" + "<b>Change View</b><br>" + "to PetriNet" + "</html>";

		JButton newDoc = createToolBarButton("newDocumentSmall.png", "Create New Network", ToolbarActionCommands.newNetwork);
		JButton parallelView = createToolBarButton("parallelview.png", "Create ParallelView From Graphs", ToolbarActionCommands.parallelView);
		JButton pick = createToolBarButton("newPick.png", "Pick Element", ToolbarActionCommands.pick);
		JButton hierarchy = createToolBarButton("hierarchy_button.png", "Hierarchy Mode", ToolbarActionCommands.hierarchy);
		JButton discretePlace = createToolBarButton("discretePlace.png", "Discrete Place", ToolbarActionCommands.discretePlace);
		JButton continuousPlace = createToolBarButton("continiousPlace.png", "Continuous Place", ToolbarActionCommands.continuousPlace);
		JButton discreteTransition = createToolBarButton("discreteTransition.png", "Discrete Transition", ToolbarActionCommands.discreteTransition);
		JButton continuousTransition = createToolBarButton("continiousTransition2.png", "Continuous Transition", ToolbarActionCommands.continuousTransition);
		JButton stochasticTransition = createToolBarButton("stochasticTransition2.png", "Stochastic Transition", ToolbarActionCommands.stochasticTransition);
		JButton center = createToolBarButton("centerGraph.png", "Center Graph", ToolbarActionCommands.center);
		JButton move = createToolBarButton("move.png", "Move Graph", ToolbarActionCommands.move);
		JButton zoomIn = createToolBarButton("zoomPlus.png", "Zoom In", ToolbarActionCommands.zoomIn);
		JButton zoomOut = createToolBarButton("zoomMinus.png", "Zoom Out", ToolbarActionCommands.zoomOut);
		JButton trash = createToolBarButton("Trash.png", "Delete Selected Items", ToolbarActionCommands.del);
		// trash.setMnemonic(KeyEvent.VK_DELETE);
		JButton info = createToolBarButton("InfoToolBarButton.png", "Info", ToolbarActionCommands.info);
		JButton infoExtended = createToolBarButton("InfoToolBarButtonextended.png", "More Info", ToolbarActionCommands.infoExtended);
		JButton mergeSelectedNodes = createToolBarButton("MergeNodesButton.png", "Merge Selected Nodes", ToolbarActionCommands.mergeSelectedNodes);
		JButton splitNode = createToolBarButton("SplitNodesButton.png", "Split Node (inverse operation of \"merge nodes\")", ToolbarActionCommands.splitNode);
		JButton coarseSelectedNodes = createToolBarButton("CoarseNodesButton.png", "Coarse Selected Nodes", ToolbarActionCommands.coarseSelectedNodes);
		JButton flatSelectedNodes = createToolBarButton("FlatNodesButton.png", "Flat Selected Coarse Node(s)", ToolbarActionCommands.flatSelectedNodes);
		JButton groupSelectedNodes = createToolBarButton("GroupButton.png", "Group Selected Nodes", ToolbarActionCommands.group);
		JButton deleteGroup = createToolBarButton("UngroupButton.png", "Delete Selected Group", ToolbarActionCommands.deleteGroup);
		JButton enterSelectedNode = createToolBarButton("enterNode.png", "Enter Selected Coarse Node(s)", ToolbarActionCommands.enterNode);
		JButton autoCoarse = createToolBarButton("autocoarse.png", "Autocoarse Current Pathway", ToolbarActionCommands.autoCoarse);
		JButton newWindow = createToolBarButton("newWindow.png", "Open New Window", ToolbarActionCommands.newWindow);

		JButton convertIntoPetriNet = new ToolBarButton("convertIntoPetriNet");
		convertIntoPetriNet.setToolTipText("Convert Into Petri Net");
		convertIntoPetriNet.setActionCommand(ToolbarActionCommands.convertIntoPetriNet.value);
		convertIntoPetriNet.addActionListener(ToolBarListener.getInstance());

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

		JButton fullScreen = createToolBarButton("newFullScreen.png", "Full Screen", ToolbarActionCommands.fullScreen);
		JButton stretchEdges = createToolBarButton("stretchEdges.png", "Stretch Edge Length", ToolbarActionCommands.stretchEdges);
		JButton compressEdges = createToolBarButton("compressEdges.png", "Compress Edge Length", ToolbarActionCommands.compressEdges);
		JButton merge = createToolBarButton("maximize.png", "Compare / Align Graphs", ToolbarActionCommands.merge);

		ButtonChooser chooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectShapeActions());

		chooser.setToolTipText("Draw compartments");

		ButtonChooser colorChooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectColorActions());

		colorChooser.setToolTipText("Set compartment colours");

		// JButton petriNet = new JButton(PetriViewString);
		// petriNet.setActionCommand(ToolbarActionCommands.createPetriNet.value);
		// petriNet.setToolTipText("Change to Petri Net View");
		// petriNet.addActionListener(ToolBarListener.getInstance());

		JButton covGraph = new ToolBarButton("Cov/Reach Graph");
		covGraph.setActionCommand(ToolbarActionCommands.createCov.value);
		covGraph.setToolTipText("Create Cov/Reach Graph");
		covGraph.addActionListener(ToolBarListener.getInstance());

		JButton editNodes = new ToolBarButton("Edit PN-Elements");
		editNodes.setActionCommand(ToolbarActionCommands.editElements.value);
		editNodes.setToolTipText("Edit PN-Elements");
		editNodes.addActionListener(ToolBarListener.getInstance());

		JButton loadModResult = new ToolBarButton("Load Modellica Result");
		loadModResult.setActionCommand(ToolbarActionCommands.loadModResult.value);
		loadModResult.setToolTipText("Load Modellica Result");
		loadModResult.addActionListener(ToolBarListener.getInstance());

		JButton modelling = new ToolBarButton(modelingViewString);
		modelling.setActionCommand(ToolbarActionCommands.modelling.value);
		modelling.addActionListener(ToolBarListener.getInstance());
		modelling.setToolTipText("Change to Modeling View");

		// JButton heatmap = createToolBarButton("heatmapGraph.png", "Create Heatgraph", ToolbarActionCommands.heatmap.value);

		JButton edit = new ToolBarButton(ImagePath.getInstance().getImageIcon("TitleGraph.png"));
		edit.setSelectedIcon(ImagePath.getInstance().getImageIcon("editSelected.png"));
		edit.setToolTipText("Edit Graph");
		edit.setActionCommand(ToolbarActionCommands.edit.value);
		edit.addActionListener(ToolBarListener.getInstance());

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

		JButton adjustDown = createToolBarButton("adjustDown.png", "Adjust Selected Nodes To Lowest Node", ToolbarActionCommands.adjustDown);
		nodeAdjustment.add(adjustDown);
		JButton adjustLeft = createToolBarButton("adjustLeft.png", "Adjust Selected Nodes To Left", ToolbarActionCommands.adjustLeft);
		nodeAdjustment.add(adjustLeft);

		JButton adjustHorizontalSpace = createToolBarButton("adjustHorizontalSpace.png",
															"Adjust Horizontal Space of Selected Nodes",
															ToolbarActionCommands.adjustHorizontalSpace);
		nodeAdjustment.add(adjustHorizontalSpace);
		JButton adjustVerticalSpace = createToolBarButton("adjustVerticalSpace.png",
														  "Adjust Vertical Space of Selected Nodes",
														  ToolbarActionCommands.adjustVerticalSpace);
		nodeAdjustment.add(adjustVerticalSpace);

		if (petriNetView) {
			// bar.add(toolBarControlControls);
			// toolBarControlControls.add(covGraph);
			// toolBarControlControls.add(editNodes);
			// toolBarControlControls.add(loadModResult);
			// toolBarControlControls.add(simulate);
			// bar.add(new JSeparator());
			bar.add(printControls);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(editControls);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(petriNetControls);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(featureControls);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(viewPortControls);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(nodeAdjustment);
			bar.add(new ToolBarSeperator(), "growx, wrap");

			bar.add(infoPanel);
		} else {
			// bar.add(toolBarControlControls);
			// bar.add(new JSeparator());
			bar.add(printControls, "wrap");
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(editControls, "wrap");

			bar.add(new ToolBarSeperator(), "growx, wrap");

			// bar.add(new JSeparator());
			bar.add(featureControls, "wrap");
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(viewPortControls, "wrap");
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(nodeAdjustment);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(infoPanel, "wrap");
		}

		// bar.revalidate();
		bar.validate();
		bar.repaint();
		bar.setVisible(true);
	}

	private static JButton createToolBarButton(String imageFileName, String toolTipText,
											   ToolbarActionCommands actionCommand) {
		JButton button = new ToolBarButton(ImagePath.getInstance().getImageIcon(imageFileName));
		button.setToolTipText(toolTipText);
		button.setActionCommand(actionCommand.value);
		button.addActionListener(ToolBarListener.getInstance());
		return button;
	}
}
