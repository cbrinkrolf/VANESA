package gui;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import gui.eventhandlers.ToolBarListener;
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

		ImagePath imagePath = ImagePath.getInstance();

		bar.setOrientation(1);
		bar.setFloatable(true);
		MigLayout bl = new MigLayout("insets 0, wrap 1");
		bar.setLayout(bl);

		String ModellingViewString = "<html>" + "<b>Change View</b> <br>" + "to Modelling" + "</html>";

		/*
		 * String PetriViewString = "<html>" + "<b>Change View</b><br>" + "to PetriNet"
		 * + "</html>";
		 */

		JButton modelling = null;
		// JButton petriNet = null;
		JButton covGraph = null;
		// JButton heatmap = null;
		JButton edit = null;
		JButton convertIntoPetriNet = null;
		JButton parallelview = null;

		parallelview = new ToolBarButton(new ImageIcon(imagePath.getPath("parallelview.png")));
		parallelview.setToolTipText("Create ParallelView from graphs");
		parallelview.setActionCommand("parallelview");
		parallelview.addActionListener(ToolBarListener.getInstance());

		JButton newDoc = new ToolBarButton(new ImageIcon(imagePath.getPath("newDocumentSmall.png")));

		newDoc.setToolTipText("Create new network");
		newDoc.setActionCommand("new Network");
		newDoc.addActionListener(ToolBarListener.getInstance());

		JButton pick = new ToolBarButton(new ImageIcon(imagePath.getPath("newPick.png")));
		pick.setToolTipText("Pick element");
		pick.setActionCommand("pick");
		pick.addActionListener(ToolBarListener.getInstance());

		JButton hierarchy = new ToolBarButton(new ImageIcon(imagePath.getPath("hierarchy_button.png")));
		hierarchy.setToolTipText("Hierarchy mode");
		hierarchy.setActionCommand("hierarchy");
		hierarchy.addActionListener(ToolBarListener.getInstance());

		JButton discretePlace = new ToolBarButton(new ImageIcon(imagePath.getPath("discretePlace.png")));
		discretePlace.setToolTipText("Discrete Place");
		discretePlace.setActionCommand("discretePlace");
		discretePlace.addActionListener(ToolBarListener.getInstance());

		JButton continiousPlace = new ToolBarButton(new ImageIcon(imagePath.getPath("continiousPlace.png")));
		continiousPlace.setToolTipText("Continuouse Place");
		continiousPlace.setActionCommand("continuousPlace");
		continiousPlace.addActionListener(ToolBarListener.getInstance());

		convertIntoPetriNet = new ToolBarButton("convertIntoPetriNet");
		convertIntoPetriNet.setToolTipText("Convert into Petri Net");
		convertIntoPetriNet.setActionCommand("convertIntoPetriNet");
		convertIntoPetriNet.addActionListener(ToolBarListener.getInstance());

		JButton discreteTransition = new ToolBarButton(new ImageIcon(imagePath.getPath("discreteTransition.png")));
		discreteTransition.setToolTipText("Discrete Transition");
		discreteTransition.setActionCommand("discreteTransition");
		discreteTransition.addActionListener(ToolBarListener.getInstance());

		JButton continiousTransition = new ToolBarButton(new ImageIcon(imagePath.getPath("continiousTransition2.png")));
		continiousTransition.setToolTipText("Continuouse Transition");
		continiousTransition.setActionCommand("continiousTransition");
		continiousTransition.addActionListener(ToolBarListener.getInstance());

		JButton stochasticTransition = new ToolBarButton(new ImageIcon(imagePath.getPath("stochasticTransition2.png")));
		stochasticTransition.setToolTipText("Stochastic Transition");
		stochasticTransition.setActionCommand("stochasticTransition");
		stochasticTransition.addActionListener(ToolBarListener.getInstance());

		JButton center = new ToolBarButton(new ImageIcon(imagePath.getPath("centerGraph.png")));
		center.setToolTipText("Center graph");
		center.setActionCommand("center");
		center.addActionListener(ToolBarListener.getInstance());

		JButton move = new ToolBarButton(new ImageIcon(imagePath.getPath("move.png")));
		move.setToolTipText("Move graph");
		move.setActionCommand("move");
		move.addActionListener(ToolBarListener.getInstance());

		JButton zoomIn = new ToolBarButton(new ImageIcon(imagePath.getPath("zoomPlus.png")));
		zoomIn.setToolTipText("Zoom in");
		zoomIn.setActionCommand("zoom in");
		zoomIn.addActionListener(ToolBarListener.getInstance());

		JButton zoomOut = new ToolBarButton(new ImageIcon(imagePath.getPath("zoomMinus.png")));
		zoomOut.setToolTipText("Zoom out");
		zoomOut.setActionCommand("zoom out");
		zoomOut.addActionListener(ToolBarListener.getInstance());

		JButton trash = new ToolBarButton(new ImageIcon(imagePath.getPath("Trash.png")));
		trash.setToolTipText("Delete selected items");
		trash.setMnemonic(KeyEvent.VK_DELETE);
		trash.setActionCommand("del");
		trash.addActionListener(ToolBarListener.getInstance());

		JPanel infopanel = new ToolBarPanel();
		infopanel.setLayout(new GridLayout(5, 2));

		JButton info = new ToolBarButton(new ImageIcon(imagePath.getPath("InfoToolBarButton.png")));
		info.setToolTipText("Info");
		info.setActionCommand("info");
		info.addActionListener(ToolBarListener.getInstance());

		JButton infoextended = new ToolBarButton(new ImageIcon(imagePath.getPath("InfoToolBarButtonextended.png")));
		infoextended.setToolTipText("More Info");
		infoextended.setActionCommand("infoextended");
		infoextended.addActionListener(ToolBarListener.getInstance());

		JButton mergeSelectedNodes = new ToolBarButton(new ImageIcon(imagePath.getPath("MergeNodesButton.png")));
		mergeSelectedNodes.setToolTipText("Merge Selected Nodes");
		mergeSelectedNodes.setActionCommand("mergeSelectedNodes");
		mergeSelectedNodes.addActionListener(ToolBarListener.getInstance());

		JButton splitNode = new ToolBarButton(new ImageIcon(imagePath.getPath("SplitNodesButton.png")));
		splitNode.setToolTipText("Split node (inverse operation of \"merge nodes\"");
		splitNode.setActionCommand("splitNode");
		splitNode.addActionListener(ToolBarListener.getInstance());

		JButton coarseSelectedNodes = new ToolBarButton(new ImageIcon(imagePath.getPath("CoarseNodesButton.png")));
		coarseSelectedNodes.setToolTipText("Coarse selected nodes");
		coarseSelectedNodes.setActionCommand("coarseSelectedNodes");
		coarseSelectedNodes.addActionListener(ToolBarListener.getInstance());

		JButton flatSelectedNodes = new ToolBarButton(new ImageIcon(imagePath.getPath("FlatNodesButton.png")));
		flatSelectedNodes.setToolTipText("Flat selected coarse node(s)");
		flatSelectedNodes.setActionCommand("flatSelectedNodes");
		flatSelectedNodes.addActionListener(ToolBarListener.getInstance());

		JButton groupSelectedNodes = new ToolBarButton(new ImageIcon(imagePath.getPath("GroupButton.png")));
		groupSelectedNodes.setToolTipText("Group selected nodes");
		groupSelectedNodes.setActionCommand("group");
		groupSelectedNodes.addActionListener(ToolBarListener.getInstance());

		JButton deleteGroup = new ToolBarButton(new ImageIcon(imagePath.getPath("UngroupButton.png")));
		deleteGroup.setToolTipText("Delete selected group");
		deleteGroup.setActionCommand("deleteGroup");
		deleteGroup.addActionListener(ToolBarListener.getInstance());

		JButton enterSelectedNode = new ToolBarButton(new ImageIcon(imagePath.getPath("enterNode.png")));
		enterSelectedNode.setToolTipText("Enter selected coarse node(s)");
		enterSelectedNode.setActionCommand("enterNode");
		enterSelectedNode.addActionListener(ToolBarListener.getInstance());

		JButton autoCoarse = new ToolBarButton(new ImageIcon(imagePath.getPath("autocoarse.png")));
		autoCoarse.setToolTipText("Autocoarse current Pathway");
		autoCoarse.setActionCommand("autocoarse");
		autoCoarse.addActionListener(ToolBarListener.getInstance());

		JButton newWindow = new ToolBarButton(new ImageIcon(imagePath.getPath("newWindow.png")));
		newWindow.setToolTipText("Open new window.");
		newWindow.setActionCommand("newWindow");
		newWindow.addActionListener(ToolBarListener.getInstance());

		infopanel.add(info);
		infopanel.add(infoextended);

		// if (MainWindow.developer) {
		infopanel.add(mergeSelectedNodes);
		infopanel.add(splitNode);
		// }

		infopanel.add(coarseSelectedNodes);
		infopanel.add(flatSelectedNodes);
		infopanel.add(enterSelectedNode);

		if (MainWindow.developer) {
			infopanel.add(autoCoarse);
		}

		infopanel.add(groupSelectedNodes);
		infopanel.add(deleteGroup);

		JButton fullScreen = new ToolBarButton(new ImageIcon(imagePath.getPath("newFullScreen.png")));
		fullScreen.setToolTipText("Full screen");
		fullScreen.setActionCommand("full screen");
		fullScreen.addActionListener(ToolBarListener.getInstance());

		JButton stretchEdges = new ToolBarButton(new ImageIcon(imagePath.getPath("stretchEdges.png")));
		stretchEdges.setToolTipText("Stretch edge length");
		stretchEdges.setActionCommand("stretchEdges");
		stretchEdges.addActionListener(ToolBarListener.getInstance());

		JButton compressEdges = new ToolBarButton(new ImageIcon(imagePath.getPath("compressEdges.png")));
		compressEdges.setToolTipText("Compress edge length");
		compressEdges.setActionCommand("compressEdges");
		compressEdges.addActionListener(ToolBarListener.getInstance());

		JButton merge = new ToolBarButton(new ImageIcon(imagePath.getPath("maximize.png")));
		merge.setToolTipText("Compare / Align graphs");
		merge.setActionCommand("merge");
		merge.addActionListener(ToolBarListener.getInstance());

		ButtonChooser chooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectShapeActions());

		chooser.setToolTipText("Draw compartments");

		ButtonChooser colorChooser = new ButtonChooser(AnnotationPainter.getInstance().getSelectColorActions());

		colorChooser.setToolTipText("Set compartment colours");

		// petriNet = new JButton(PetriViewString);
		// petriNet.setActionCommand("createPetriNet");
		// petriNet.setToolTipText("Change to Petri Net View");
		// petriNet.addActionListener(toolBarListener);

		covGraph = new ToolBarButton("Cov/Reach Graph");
		covGraph.setActionCommand("createCov");
		covGraph.setToolTipText("Create Cov/Reach Graph");
		covGraph.addActionListener(ToolBarListener.getInstance());

		JButton editNodes = new ToolBarButton("Edit PN-Elements");
		editNodes.setActionCommand("editElements");
		editNodes.setToolTipText("Edit PN-Elements");
		editNodes.addActionListener(ToolBarListener.getInstance());

		JButton loadModResult = new ToolBarButton("Load Modellica Result");
		loadModResult.setActionCommand("loadModResult");
		loadModResult.setToolTipText("Load Modellica Result");
		loadModResult.addActionListener(ToolBarListener.getInstance());

		modelling = new ToolBarButton(ModellingViewString);
		modelling.setActionCommand("modelling");
		modelling.addActionListener(ToolBarListener.getInstance());
		modelling.setToolTipText("Change to Modelling View");

		// heatmap = new ToolBarButton(new
		// ImageIcon(imagePath.getPath("heatmapGraph.png")));
		// heatmap.setToolTipText("Create heatgraph");
		// heatmap.setActionCommand("heatmap");
		// heatmap.addActionListener(ToolBarListener.getInstance());

		edit = new ToolBarButton(new ImageIcon(imagePath.getPath("TitleGraph.png")));
		edit.setSelectedIcon(new ImageIcon(imagePath.getPath("editSelected.png")));
		edit.setToolTipText("Edit graph");
		edit.setActionCommand("edit");
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
		JPanel petriNetcontrols = new ToolBarPanel();
		petriNetcontrols.setLayout(new GridLayout(2, 3));

		petriNetcontrols.add(discretePlace);
		petriNetcontrols.add(continiousPlace);
		petriNetcontrols.add(new JLabel());
		petriNetcontrols.add(discreteTransition);
		petriNetcontrols.add(continiousTransition);
		petriNetcontrols.add(stochasticTransition);

		JPanel featureControls = new ToolBarPanel();

		if (!petriNetView) {
			featureControls.setLayout(new GridLayout(2, 2));
			featureControls.add(merge);
			if (MainWindow.developer) {
				// featureControls.add(heatmap);
				featureControls.add(parallelview);
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

		JButton adjustDown = new ToolBarButton(new ImageIcon(imagePath.getPath("adjustDown.png")));
		adjustDown.setActionCommand("adjustDown");
		adjustDown.addActionListener(ToolBarListener.getInstance());
		adjustDown.setToolTipText("Adjust selected nodes to lowest node");

		JButton adjustLeft = new ToolBarButton(new ImageIcon(imagePath.getPath("adjustLeft.png")));
		adjustLeft.setActionCommand("adjustLeft");
		adjustLeft.addActionListener(ToolBarListener.getInstance());
		adjustLeft.setToolTipText("Adjust selected nodes to left");

		JButton adjustVerticalSpace = new ToolBarButton(new ImageIcon(imagePath.getPath("adjustVerticalSpace.png")));
		adjustVerticalSpace.setActionCommand("adjustVerticalSpace");
		adjustVerticalSpace.addActionListener(ToolBarListener.getInstance());
		adjustVerticalSpace.setToolTipText("Adjust vertical space of selected nodes");

		JButton adjustHorizontalSpace = new ToolBarButton(
				new ImageIcon(imagePath.getPath("adjustHorizontalSpace.png")));
		adjustHorizontalSpace.setActionCommand("adjustHorizontalSpace");
		adjustHorizontalSpace.addActionListener(ToolBarListener.getInstance());
		adjustHorizontalSpace.setToolTipText("Adjust horizontal space of selected nodes");

		nodeAdjustment.add(adjustDown);
		nodeAdjustment.add(adjustLeft);

		nodeAdjustment.add(adjustVerticalSpace);
		nodeAdjustment.add(adjustHorizontalSpace);

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
			bar.add(petriNetcontrols);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(featureControls);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(viewPortControls);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(nodeAdjustment);
			bar.add(new ToolBarSeperator(), "growx, wrap");

			bar.add(infopanel);
		} else {
			// bar.add(toolBarControlControls);
			// bar.add(new JSeparator());
			bar.add(printControls, "wrap");
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(editControls, "wrap");

			bar.add(new ToolBarSeperator(), "growx, wrap");

			//
			// bar.add(new JSeparator());
			bar.add(featureControls, "wrap");
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(viewPortControls, "wrap");
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(nodeAdjustment);
			bar.add(new ToolBarSeperator(), "growx, wrap");
			bar.add(infopanel, "wrap");
		}

		// bar.revalidate();
		bar.validate();
		bar.repaint();
		bar.setVisible(true);
	}
}
