package gui;

import gui.eventhandlers.ToolBarListener;
import gui.images.ImagePath;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import net.miginfocom.swing.MigLayout;
import configurations.DeveloperClass;

public class ToolBar {

	private JToolBar bar = new JToolBar();

	public void paintToolbar(Boolean petriNetView) {

		bar.removeAll();
		ToolBarListener toolBarListener = new ToolBarListener();

		ImagePath imagePath = ImagePath.getInstance();

		bar.setOrientation(1);
		bar.setFloatable(false);
		MigLayout bl = new MigLayout("insets 0, wrap 1");
		bar.setLayout(bl);
		

		String ModellingViewString = "<html>" + "<b>Change View</b> <br>"
				+ "to Modelling" + "</html>";

		/*String PetriViewString = "<html>" + "<b>Change View</b><br>"
				+ "to PetriNet" + "</html>";*/

		JButton modelling = null;
		//JButton petriNet = null;
		JButton covGraph = null;
		JButton heatmap = null;
		JButton edit = null;
		JButton convertIntoPetriNet = null;
		JButton parallelview = null;
		
		parallelview = new ToolBarButton(new ImageIcon(
				imagePath.getPath("parallelview.png")));
		parallelview.setToolTipText("Create ParallelView from graphs");
		parallelview.setActionCommand("parallelview");
		parallelview.addActionListener(toolBarListener);
		

		JButton newDoc = new ToolBarButton(new ImageIcon(
				imagePath.getPath("newDocumentSmall.png")));

		newDoc.setToolTipText("Create new network");
		newDoc.setActionCommand("new Network");
		newDoc.addActionListener(toolBarListener);

		JButton pick = new ToolBarButton(new ImageIcon(
				imagePath.getPath("newPick.png")));
		pick.setToolTipText("Pick element");
		pick.setActionCommand("pick");
		pick.addActionListener(toolBarListener);
		
		JButton hierarchy = new ToolBarButton("Hierarchy");
		hierarchy.setToolTipText("Zoom through hierarchy");
		hierarchy.setActionCommand("hierarchy");
		hierarchy.addActionListener(toolBarListener);

		JButton discretePlace = new ToolBarButton(new ImageIcon(
				imagePath.getPath("discretePlace.png")));
		discretePlace.setToolTipText("Discrete Place");
		discretePlace.setActionCommand("discretePlace");
		discretePlace.addActionListener(toolBarListener);

		JButton continiousPlace = new ToolBarButton(new ImageIcon(
				imagePath.getPath("continiousPlace.png")));
		continiousPlace.setToolTipText("Continuouse Place");
		continiousPlace.setActionCommand("continuousPlace");
		continiousPlace.addActionListener(toolBarListener);

		convertIntoPetriNet = new ToolBarButton("convertIntoPetriNet");
		convertIntoPetriNet.setToolTipText("Convert into Petri Net");
		convertIntoPetriNet.setActionCommand("convertIntoPetriNet");
		convertIntoPetriNet.addActionListener(toolBarListener);

		JButton discreteTransition = new ToolBarButton(new ImageIcon(
				imagePath.getPath("discreteTransition.png")));
		discreteTransition.setToolTipText("Discrete Transition");
		discreteTransition.setActionCommand("discreteTransition");
		discreteTransition.addActionListener(toolBarListener);

		JButton continiousTransition = new ToolBarButton(new ImageIcon(
				imagePath.getPath("continiousTransition2.png")));
		continiousTransition.setToolTipText("Continuouse Transition");
		continiousTransition.setActionCommand("continiousTransition");
		continiousTransition.addActionListener(toolBarListener);

		JButton stochasticTransition = new ToolBarButton(new ImageIcon(
					imagePath.getPath("stochasticTransition2.png")));
	stochasticTransition.setToolTipText("Stochastic Transition");
		stochasticTransition.setActionCommand("stochasticTransition");
		stochasticTransition.addActionListener(toolBarListener);

		JPanel petriNetcontrols = new ToolBarPanel();
		petriNetcontrols.setLayout(new GridLayout(3, 2));

		// Add buttons to experiment with Grid Layout
		petriNetcontrols.add(discretePlace);
		petriNetcontrols.add(continiousPlace);
		petriNetcontrols.add(discreteTransition);
		petriNetcontrols.add(continiousTransition);
		petriNetcontrols.add(stochasticTransition);

		JButton center = new ToolBarButton(new ImageIcon(
				imagePath.getPath("centerGraph.png")));
		center.setToolTipText("Center graph");
		center.setActionCommand("center");
		center.addActionListener(toolBarListener);

		JButton move = new ToolBarButton(new ImageIcon(imagePath.getPath("move.png")));
		move.setToolTipText("Move graph");
		move.setActionCommand("move");
		move.addActionListener(toolBarListener);

		JButton picture = new ToolBarButton(new ImageIcon(
				imagePath.getPath("picture.png")));
		picture.setToolTipText("Save graph - picture");
		picture.setActionCommand("picture");
		picture.addActionListener(toolBarListener);

		JButton zoomIn = new ToolBarButton(new ImageIcon(
				imagePath.getPath("zoomPlus.png")));
		zoomIn.setToolTipText("Zoom in");
		zoomIn.setActionCommand("zoom in");
		zoomIn.addActionListener(toolBarListener);

		JButton zoomOut = new ToolBarButton(new ImageIcon(
				imagePath.getPath("zoomMinus.png")));
		zoomOut.setToolTipText("Zoom out");
		zoomOut.setActionCommand("zoom out");
		zoomOut.addActionListener(toolBarListener);

		JButton trash = new ToolBarButton(new ImageIcon(
				imagePath.getPath("Trash.png")));
		trash.setToolTipText("Delete selected items");
		trash.setMnemonic(KeyEvent.VK_DELETE);
		trash.setActionCommand("del");
		trash.addActionListener(toolBarListener);

		
		JPanel infopanel = new ToolBarPanel();
		infopanel.setLayout(new GridLayout(2, 2));
		
		JButton info = new ToolBarButton(new ImageIcon(
				imagePath.getPath("InfoToolBarButton.png")));
		info.setToolTipText("Info");
		info.setActionCommand("info");
		info.addActionListener(toolBarListener);
		
		
		JButton infoextended = new ToolBarButton(new ImageIcon(
				imagePath.getPath("InfoToolBarButtonextended.png")));
		infoextended.setToolTipText("More Info");
		infoextended.setActionCommand("infoextended");
		infoextended.addActionListener(toolBarListener);
		
		JButton mergeSelectedNodes = new ToolBarButton(new ImageIcon(imagePath.getPath("MergeNodesButton.png")));
		mergeSelectedNodes.setToolTipText("Merge Selected Nodes");
		mergeSelectedNodes.setActionCommand("mergeSelectedNodes");
		mergeSelectedNodes.addActionListener(toolBarListener);
		
		JButton splitNode = new ToolBarButton(new ImageIcon(imagePath.getPath("SplitNodesButton.png")));
		splitNode.setToolTipText("Split node (inverse operation of \"merge nodes\"");
		splitNode.setActionCommand("splitNode");
		splitNode.addActionListener(toolBarListener);
		
		JButton coarseSelectedNodes = new ToolBarButton(new ImageIcon(imagePath.getPath("CoarseNodesButton.png")));
		coarseSelectedNodes.setToolTipText("Coarse selected nodes");
		coarseSelectedNodes.setActionCommand("coarseSelectedNodes");
		coarseSelectedNodes.addActionListener(toolBarListener);
	
		JButton flatSelectedNodes = new ToolBarButton(new ImageIcon(imagePath.getPath("FlatNodesButton.png")));
		flatSelectedNodes.setToolTipText("Flat selected coarse node(s)");
		flatSelectedNodes.setActionCommand("flatSelectedNodes");
		flatSelectedNodes.addActionListener(toolBarListener);
		
		JButton enterSelectedNode = new ToolBarButton(new ImageIcon(imagePath.getPath("enterNode.png")));
		enterSelectedNode.setToolTipText("Enter selected coarse node(s)");
		enterSelectedNode.setActionCommand("enterNode");
		enterSelectedNode.addActionListener(toolBarListener);
		
		JButton autoCoarse = new ToolBarButton("Autocoarse");
		autoCoarse.setToolTipText("Autocoarse current Pathway");
		autoCoarse.setActionCommand("autocoarse");
		autoCoarse.addActionListener(toolBarListener);
		
		JButton newWindow = new ToolBarButton(new ImageIcon(imagePath.getPath("newWindow.png")));
		newWindow.setToolTipText("Open new window.");
		newWindow.setActionCommand("newWindow");
		newWindow.addActionListener(toolBarListener);
		
		infopanel.add(info);
		infopanel.add(infoextended);
		infopanel.add(mergeSelectedNodes);
		infopanel.add(splitNode);
		infopanel.add(coarseSelectedNodes);
		infopanel.add(flatSelectedNodes);
		infopanel.add(enterSelectedNode);
		infopanel.add(autoCoarse);

		JButton dimView = new ToolBarButton(new ImageIcon(
				imagePath.getPath("view.png")));
		dimView.setToolTipText("3D View");
		dimView.setActionCommand("3DView");
		dimView.addActionListener(toolBarListener);

		JButton printer = new ToolBarButton(new ImageIcon(
				imagePath.getPath("printer.png")));
		printer.setToolTipText("Print graph");
		printer.setActionCommand("print");
		printer.addActionListener(toolBarListener);

		JButton fullScreen = new ToolBarButton(new ImageIcon(
				imagePath.getPath("newFullScreen.png")));
		fullScreen.setToolTipText("Full screen");
		fullScreen.setActionCommand("full screen");
		fullScreen.addActionListener(toolBarListener);
		
		JButton stretchEdges = new ToolBarButton("Stretch");
		stretchEdges.setToolTipText("Stretch edge length");
		stretchEdges.setActionCommand("stretchEdges");
		stretchEdges.addActionListener(toolBarListener);
		
		JButton compressEdges = new ToolBarButton("Compress");
		compressEdges.setToolTipText("Compress edge length");
		compressEdges.setActionCommand("compressEdges");
		compressEdges.addActionListener(toolBarListener);
		
		JButton merge = new ToolBarButton(new ImageIcon(
				imagePath.getPath("maximize.png")));
		merge.setToolTipText("Compare / Align graphs");
		merge.setActionCommand("merge");
		merge.addActionListener(toolBarListener);

		ButtonChooser chooser = new ButtonChooser(AnnotationPainter.getInstance()
				.getSelectShapeActions());

		chooser.setToolTipText("Draw compartments");
		chooser.setBorder(null);
		chooser.setBorderPainted(false);
		

		ButtonChooser colorChooser = new ButtonChooser(AnnotationPainter
				.getInstance().getSelectColorActions());

		colorChooser.setToolTipText("Set compartment colours");
		colorChooser.setBorder(null);
		colorChooser.setBorderPainted(false);

//		petriNet = new JButton(PetriViewString);
//		petriNet.setActionCommand("createPetriNet");
//		petriNet.setToolTipText("Change to Petri Net View");
//		petriNet.addActionListener(toolBarListener);
		
		covGraph = new ToolBarButton("Cov/Reach Graph");
		covGraph.setActionCommand("createCov");
		covGraph.setToolTipText("Create Cov/Reach Graph");
		covGraph.addActionListener(toolBarListener);
		
		JButton editNodes = new ToolBarButton("Edit PN-Elements");
		editNodes.setActionCommand("editElements");
		editNodes.setToolTipText("Edit PN-Elements");
		editNodes.addActionListener(toolBarListener);
		
		JButton loadModResult = new ToolBarButton("Load Modellica Result");
		loadModResult.setActionCommand("loadModResult");
		loadModResult.setToolTipText("Load Modellica Result");
		loadModResult.addActionListener(toolBarListener);
	
		
		JButton simulate = new ToolBarButton("Simulate Petri Net");
		simulate.setActionCommand("simulate");
		simulate.setToolTipText("Simulate Petri Net");
		simulate.addActionListener(toolBarListener);

		modelling = new ToolBarButton(ModellingViewString);
		modelling.setActionCommand("modelling");
		modelling.addActionListener(toolBarListener);
		modelling.setToolTipText("Change to Modelling View");

		heatmap = new ToolBarButton(new ImageIcon(
				imagePath.getPath("heatmapGraph.png")));
		heatmap.setToolTipText("Create heatgraph");
		heatmap.setActionCommand("heatmap");
		heatmap.addActionListener(toolBarListener);

		edit = new ToolBarButton(new ImageIcon(imagePath.getPath("TitleGraph.png")));
		edit.setSelectedIcon(new ImageIcon(imagePath
				.getPath("editSelected.png")));
		edit.setToolTipText("Edit graph");
		edit.setActionCommand("edit");
		edit.addActionListener(toolBarListener);

		JPanel viewPortControls = new ToolBarPanel();
		viewPortControls.setLayout(new GridLayout(2, 2));

		compressEdges.setBorder(null);
		compressEdges.setBorderPainted(false);
		//compressEdges.setContentAreaFilled(false);
		//compressEdges.setBackground(new Color(0,255,0,0));
		//compressEdges.setForeground(new Color(255,0,0,255));
		
		viewPortControls.add(fullScreen);
		viewPortControls.add(center);
		viewPortControls.add(zoomIn);
		viewPortControls.add(zoomOut);
		viewPortControls.add(compressEdges);
		viewPortControls.add(stretchEdges); 
		
		if (DeveloperClass.isDeveloperStatus) {
			viewPortControls.add(dimView);
		}
		JPanel editControls = new ToolBarPanel();
		editControls.setLayout(new GridLayout(2, 2));

		if (!petriNetView) {
			editControls.add(edit);
		}
		editControls.add(pick);
		editControls.add(move);
		editControls.add(trash);
		editControls.add(hierarchy);

		JPanel printControls = new ToolBarPanel();
		printControls.setLayout(new GridLayout(1, 2));

		printControls.add(newDoc);
		printControls.add(newWindow);
		printControls.add(printer);
		printControls.add(picture);

		// Add buttons to experiment with Grid Layout
		petriNetcontrols.add(discretePlace);
		petriNetcontrols.add(continiousPlace);
		petriNetcontrols.add(discreteTransition);
		petriNetcontrols.add(continiousTransition);
		petriNetcontrols.add(stochasticTransition);

		JPanel featureControls = new ToolBarPanel();

		if (!petriNetView) {
			featureControls.setLayout(new GridLayout(2, 2));
			featureControls.add(merge);
			if (DeveloperClass.isDeveloperStatus) {
				featureControls.add(heatmap);
				featureControls.add(parallelview);
			}
			featureControls.add(dimView);
			featureControls.add(chooser);
			featureControls.add(colorChooser);
		} else {
			featureControls.setLayout(new GridLayout(1, 2));
			featureControls.add(chooser);
			featureControls.add(colorChooser);
		}

		//featureControls.setMaximumSize(featureControls.getPreferredSize());
		//featureControls.set
		//featureControls.setAlignmentX(Component.LEFT_ALIGNMENT);
		//featureControls.setAlignmentY(Component.TOP_ALIGNMENT);
		
		
		
		JPanel toolBarControlControls = new ToolBarPanel();
		toolBarControlControls.setLayout(new GridLayout(2, 1));
//		if (petriNetView) {
//			toolBarControlControls.add(modelling);
//		} else {
//			toolBarControlControls.add(petriNet);
//			toolBarControlControls.add(convertIntoPetriNet);
//
//		}
//		if (!petriNetView) toolBarControlControls.add(convertIntoPetriNet);
		
		JPanel nodeAdjustment = new ToolBarPanel();
		nodeAdjustment.setLayout(new GridLayout(1,1));
		
		
		JButton adjustDown = new JButton("_");
		adjustDown.setActionCommand("adjustDown");
		adjustDown.addActionListener(toolBarListener);
		adjustDown.setToolTipText("Adjust selected nodes to lowest node");
		
		JButton adjustLeft = new JButton("|");
		adjustLeft.setActionCommand("adjustLeft");
		adjustLeft.addActionListener(toolBarListener);
		adjustLeft.setToolTipText("Adjust selected nodes to left");
		
		JButton adjustHorizontalSpace = new JButton("||");
		adjustHorizontalSpace.setActionCommand("adjustHorizontalSpace");
		adjustHorizontalSpace.addActionListener(toolBarListener);
		adjustHorizontalSpace.setToolTipText("Adjust horizontal space of selected nodes");
		
		JButton adjustVerticalSpace = new JButton("=");
		adjustVerticalSpace.setActionCommand("adjustVerticalSpace");
		adjustVerticalSpace.addActionListener(toolBarListener);
		adjustVerticalSpace.setToolTipText("Adjust vertical space of selected nodes");
		
		nodeAdjustment.add(adjustDown);
		nodeAdjustment.add(adjustLeft);
		nodeAdjustment.add(adjustHorizontalSpace);
		nodeAdjustment.add(adjustVerticalSpace);
		
		
		
		
		
		if (DeveloperClass.isDeveloperStatus) {
			if (petriNetView) {
//				bar.add(toolBarControlControls);
//				toolBarControlControls.add(covGraph);
//				toolBarControlControls.add(editNodes);
//				toolBarControlControls.add(loadModResult);
//				toolBarControlControls.add(simulate);
//				bar.add(new JSeparator());
				bar.add(printControls);
				bar.add(new JSeparator());
				bar.add(editControls);
				bar.add(new JSeparator());
				bar.add(petriNetcontrols);
				bar.add(new JSeparator());
				bar.add(featureControls);
				bar.add(new JSeparator());
				bar.add(viewPortControls);
				bar.add(new JSeparator());
				bar.add(nodeAdjustment);
				bar.add(new JSeparator());
				
				bar.add(infopanel);
			} else {
//				bar.add(toolBarControlControls);
//				bar.add(new JSeparator());
				bar.add(printControls, "wrap");
				bar.add(new ToolBarSeperator(), "growx, wrap");
				bar.add(editControls, "wrap");
				
				bar.add(new ToolBarSeperator(), "growx, wrap");
				
				//
				//bar.add(new JSeparator());
				bar.add(featureControls, "wrap");
				bar.add(new ToolBarSeperator(), "growx, wrap");
				bar.add(viewPortControls, "wrap");
				bar.add(new JSeparator());
				bar.add(nodeAdjustment);
				bar.add(new ToolBarSeperator(), "growx, wrap");
				bar.add(infopanel, "wrap");
			}
		} else {

			bar.add(printControls);
			bar.add(new JSeparator());
			bar.add(editControls);
			
			bar.add(new JSeparator());
			bar.add(featureControls);
			bar.add(new JSeparator());
			bar.add(viewPortControls);
			bar.add(new JSeparator());
			bar.add(nodeAdjustment);
			bar.add(new JSeparator());
			bar.add(infopanel);

		}
		bar.setVisible(true);
		bar.repaint();
		bar.revalidate();
	}

	public ToolBar(Boolean petriNetView) {
		paintToolbar(petriNetView);
	}

	public JToolBar getToolBar() {
		return bar;
	}
}
