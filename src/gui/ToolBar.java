package gui;

import gui.eventhandlers.ToolBarListener;
import gui.images.ImagePath;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import configurations.DeveloperClass;

public class ToolBar {

	private JToolBar bar = new JToolBar();

	public void paintToolbar(Boolean petriNetView) {

		bar.removeAll();
		ToolBarListener toolBarListener = new ToolBarListener();

		ImagePath imagePath = ImagePath.getInstance();

		bar.setOrientation(1);
		bar.setFloatable(false);

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
		
		parallelview = new JButton(new ImageIcon(
				imagePath.getPath("parallelview.png")));
		parallelview.setToolTipText("Create ParallelView from graphs");
		parallelview.setActionCommand("parallelview");
		parallelview.addActionListener(toolBarListener);
		

		JButton newDoc = new JButton(new ImageIcon(
				imagePath.getPath("newDocumentSmall.png")));

		newDoc.setToolTipText("Create new network");
		newDoc.setActionCommand("new Network");
		newDoc.addActionListener(toolBarListener);

		JButton pick = new JButton(new ImageIcon(
				imagePath.getPath("newPick.png")));
		pick.setToolTipText("Pick element");
		pick.setActionCommand("pick");
		pick.addActionListener(toolBarListener);

		JButton discretePlace = new JButton(new ImageIcon(
				imagePath.getPath("discretePlace.png")));
		discretePlace.setToolTipText("Discrete Place");
		discretePlace.setActionCommand("discretePlace");
		discretePlace.addActionListener(toolBarListener);

		JButton continiousPlace = new JButton(new ImageIcon(
				imagePath.getPath("continiousPlace.png")));
		continiousPlace.setToolTipText("Continuouse Place");
		continiousPlace.setActionCommand("continuousPlace");
		continiousPlace.addActionListener(toolBarListener);

		convertIntoPetriNet = new JButton("convertIntoPetriNet");
		convertIntoPetriNet.setToolTipText("Convert into Petri Net");
		convertIntoPetriNet.setActionCommand("convertIntoPetriNet");
		convertIntoPetriNet.addActionListener(toolBarListener);

		JButton discreteTransition = new JButton(new ImageIcon(
				imagePath.getPath("discreteTransition.png")));
		discreteTransition.setToolTipText("Discrete Transition");
		discreteTransition.setActionCommand("discreteTransition");
		discreteTransition.addActionListener(toolBarListener);

		JButton continiousTransition = new JButton(new ImageIcon(
				imagePath.getPath("continiousTransition2.png")));
		continiousTransition.setToolTipText("Continuouse Transition");
		continiousTransition.setActionCommand("continiousTransition");
		continiousTransition.addActionListener(toolBarListener);

		JButton stochasticTransition = new JButton(new ImageIcon(
					imagePath.getPath("stochasticTransition2.png")));
	stochasticTransition.setToolTipText("Stochastic Transition");
		stochasticTransition.setActionCommand("stochasticTransition");
		stochasticTransition.addActionListener(toolBarListener);

		JPanel petriNetcontrols = new JPanel();
		petriNetcontrols.setLayout(new GridLayout(3, 2));

		// Add buttons to experiment with Grid Layout
		petriNetcontrols.add(discretePlace);
		petriNetcontrols.add(continiousPlace);
		petriNetcontrols.add(discreteTransition);
		petriNetcontrols.add(continiousTransition);
		petriNetcontrols.add(stochasticTransition);

		JButton center = new JButton(new ImageIcon(
				imagePath.getPath("centerGraph.png")));
		center.setToolTipText("Center graph");
		center.setActionCommand("center");
		center.addActionListener(toolBarListener);

		JButton move = new JButton(new ImageIcon(imagePath.getPath("move.png")));
		move.setToolTipText("Move graph");
		move.setActionCommand("move");
		move.addActionListener(toolBarListener);

		JButton picture = new JButton(new ImageIcon(
				imagePath.getPath("picture.png")));
		picture.setToolTipText("Save graph - picture");
		picture.setActionCommand("picture");
		picture.addActionListener(toolBarListener);

		JButton zoomIn = new JButton(new ImageIcon(
				imagePath.getPath("zoomPlus.png")));
		zoomIn.setToolTipText("Zoom in");
		zoomIn.setActionCommand("zoom in");
		zoomIn.addActionListener(toolBarListener);

		JButton zoomOut = new JButton(new ImageIcon(
				imagePath.getPath("zoomMinus.png")));
		zoomOut.setToolTipText("Zoom out");
		zoomOut.setActionCommand("zoom out");
		zoomOut.addActionListener(toolBarListener);

		JButton trash = new JButton(new ImageIcon(
				imagePath.getPath("Trash.png")));
		trash.setToolTipText("Delete selected items");
		trash.setMnemonic(KeyEvent.VK_DELETE);
		trash.setActionCommand("del");
		trash.addActionListener(toolBarListener);

		
		JPanel infopanel = new JPanel();
		infopanel.setLayout(new GridLayout(2, 2));
		
		JButton info = new JButton(new ImageIcon(
				imagePath.getPath("InfoToolBarButton.png")));
		info.setToolTipText("Info");
		info.setActionCommand("info");
		info.addActionListener(toolBarListener);
		
		
		JButton infoextended = new JButton(new ImageIcon(
				imagePath.getPath("InfoToolBarButtonextended.png")));
		infoextended.setToolTipText("More Info");
		infoextended.setActionCommand("infoextended");
		infoextended.addActionListener(toolBarListener);
		
		JButton mergeSelectedNodes = new JButton(new ImageIcon(imagePath.getPath("MergeNodesButton.png")));
		mergeSelectedNodes.setToolTipText("Merge Selected Nodes");
		mergeSelectedNodes.setActionCommand("mergeSelectedNodes");
		mergeSelectedNodes.addActionListener(toolBarListener);
		
		JButton coarseSelectedNodes = new JButton("Coarse");
		coarseSelectedNodes.setToolTipText("Coarse the selected Nodes");
		coarseSelectedNodes.setActionCommand("coarseSelectedNodes");
		coarseSelectedNodes.addActionListener(toolBarListener);
	
		JButton flatSelectedNodes = new JButton("Flat");
		flatSelectedNodes.setToolTipText("Flat the selected Node");
		flatSelectedNodes.setActionCommand("flatSelectedNodes");
		flatSelectedNodes.addActionListener(toolBarListener);
		
		JButton enterSelectedNode = new JButton("Enter Node");
		enterSelectedNode.setToolTipText("Enter selected coarse node in Hierarchy view.");
		enterSelectedNode.setActionCommand("enterNode");
		enterSelectedNode.addActionListener(toolBarListener);
		
		infopanel.add(info);
		infopanel.add(infoextended);
		infopanel.add(mergeSelectedNodes);
		infopanel.add(coarseSelectedNodes);
		infopanel.add(flatSelectedNodes);
		infopanel.add(enterSelectedNode);

		JButton dimView = new JButton(new ImageIcon(
				imagePath.getPath("view.png")));
		dimView.setToolTipText("3D View");
		dimView.setActionCommand("3DView");
		dimView.addActionListener(toolBarListener);

		JButton printer = new JButton(new ImageIcon(
				imagePath.getPath("printer.png")));
		printer.setToolTipText("Print graph");
		printer.setActionCommand("print");
		printer.addActionListener(toolBarListener);

		JButton fullScreen = new JButton(new ImageIcon(
				imagePath.getPath("newFullScreen.png")));
		fullScreen.setToolTipText("Full screen");
		fullScreen.setActionCommand("full screen");
		fullScreen.addActionListener(toolBarListener);

		JButton merge = new JButton(new ImageIcon(
				imagePath.getPath("maximize.png")));
		merge.setToolTipText("Compare / Align graphs");
		merge.setActionCommand("merge");
		merge.addActionListener(toolBarListener);

		ButtonChooser chooser = new ButtonChooser(AnnotationPainter.getInstance()
				.getSelectShapeActions());

		chooser.setToolTipText("Draw compartments");

		ButtonChooser colorChooser = new ButtonChooser(AnnotationPainter
				.getInstance().getSelectColorActions());

		colorChooser.setToolTipText("Set compartment colours");

//		petriNet = new JButton(PetriViewString);
//		petriNet.setActionCommand("createPetriNet");
//		petriNet.setToolTipText("Change to Petri Net View");
//		petriNet.addActionListener(toolBarListener);
		
		covGraph = new JButton("Cov/Reach Graph");
		covGraph.setActionCommand("createCov");
		covGraph.setToolTipText("Create Cov/Reach Graph");
		covGraph.addActionListener(toolBarListener);
		
		JButton editNodes = new JButton("Edit PN-Elements");
		editNodes.setActionCommand("editElements");
		editNodes.setToolTipText("Edit PN-Elements");
		editNodes.addActionListener(toolBarListener);
		
		JButton loadModResult = new JButton("Load Modellica Result");
		loadModResult.setActionCommand("loadModResult");
		loadModResult.setToolTipText("Load Modellica Result");
		loadModResult.addActionListener(toolBarListener);
	
		
		JButton simulate = new JButton("Simulate Petri Net");
		simulate.setActionCommand("simulate");
		simulate.setToolTipText("Simulate Petri Net");
		simulate.addActionListener(toolBarListener);

		modelling = new JButton(ModellingViewString);
		modelling.setActionCommand("modelling");
		modelling.addActionListener(toolBarListener);
		modelling.setToolTipText("Change to Modelling View");

		heatmap = new JButton(new ImageIcon(
				imagePath.getPath("heatmapGraph.png")));
		heatmap.setToolTipText("Create heatgraph");
		heatmap.setActionCommand("heatmap");
		heatmap.addActionListener(toolBarListener);

		edit = new JButton(new ImageIcon(imagePath.getPath("TitleGraph.png")));
		edit.setSelectedIcon(new ImageIcon(imagePath
				.getPath("editSelected.png")));
		edit.setToolTipText("Edit graph");
		edit.setActionCommand("edit");
		edit.addActionListener(toolBarListener);

		JPanel viewPortControls = new JPanel();
		viewPortControls.setLayout(new GridLayout(2, 2));

		viewPortControls.add(fullScreen);
		viewPortControls.add(center);
		viewPortControls.add(zoomIn);
		viewPortControls.add(zoomOut);
		if (DeveloperClass.isDeveloperStatus) {
			viewPortControls.add(dimView);
		}
		JPanel editControls = new JPanel();
		editControls.setLayout(new GridLayout(2, 2));

		if (!petriNetView) {
			editControls.add(edit);
		}
		editControls.add(pick);
		editControls.add(move);
		editControls.add(trash);

		JPanel printControls = new JPanel();
		printControls.setLayout(new GridLayout(2, 2));

		printControls.add(newDoc);
		printControls.add(new JLabel());
		printControls.add(printer);
		printControls.add(picture);

		// Add buttons to experiment with Grid Layout
		petriNetcontrols.add(discretePlace);
		petriNetcontrols.add(continiousPlace);
		petriNetcontrols.add(discreteTransition);
		petriNetcontrols.add(continiousTransition);
		petriNetcontrols.add(stochasticTransition);

		JPanel featureControls = new JPanel();

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

		JPanel toolBarControlControls = new JPanel();
		toolBarControlControls.setLayout(new GridLayout(2, 1));
//		if (petriNetView) {
//			toolBarControlControls.add(modelling);
//		} else {
//			toolBarControlControls.add(petriNet);
//			toolBarControlControls.add(convertIntoPetriNet);
//
//		}
//		if (!petriNetView) toolBarControlControls.add(convertIntoPetriNet);
		
		
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
				bar.add(infopanel);
			} else {
//				bar.add(toolBarControlControls);
//				bar.add(new JSeparator());
				bar.add(printControls);
				bar.add(new JSeparator());
				bar.add(editControls);
				bar.add(new JSeparator());
				bar.add(featureControls);
				bar.add(new JSeparator());
				bar.add(viewPortControls);
				bar.add(new JSeparator());
				bar.add(infopanel);
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
