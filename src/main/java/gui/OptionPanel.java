package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import database.gui.DatabaseWindow;
import gui.optionPanelWindows.BuildingBlocks;
import gui.optionPanelWindows.ElementTree;
import gui.optionPanelWindows.ElementWindow;
import gui.optionPanelWindows.GraphAlgorithmsWindow;
import gui.optionPanelWindows.PathwayPropertiesWindow;
import gui.optionPanelWindows.PathwayTree;
import gui.optionPanelWindows.ProjectWindow;
import gui.optionPanelWindows.SatelliteWindow;
import gui.optionPanelWindows.SimulationResultsPlot;
import net.miginfocom.swing.MigLayout;

public class OptionPanel {
	private final JPanel p = new JPanel(new MigLayout("insets 0"));

	private final JScrollPane scrollPane;
	private final DatabaseWindow dw;
	private final ElementTree tree;
	private final SimulationResultsPlot simResWindow;
	private final BuildingBlocks bb;
	private final SatelliteWindow satelliteWindow;
	private final ElementWindow elementWindow;
	private final ProjectWindow projectWindow;
	private final PathwayTree pathwayTree;
	private final GraphAlgorithmsWindow graphAlgorithms;
	private final PathwayPropertiesWindow pathwayPropertiesWindow;

	private final JXTaskPane elements;
	private final JXTaskPane satellite;
	private final JXTaskPane simResView;
	private final JXTaskPane generalProperties;
	private final JXTaskPane databaseSearch;
	private final JXTaskPane project;
	private final JXTaskPane theory;
	private final JXTaskPane pathways;
	private final JXTaskPane bbProperties;
	private final JXTaskPane pathwayProperties;

	private boolean updatePanels = true;

	private final JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

	// private GraphAlignmentOptionTab alignmentOptions;
	// private JXTaskPane alignment;
	// private HashMap<String, GraphAlignmentOptionTab> alignmentTabs = new
	// HashMap<String, GraphAlignmentOptionTab>();



	public JXTaskPaneContainer getTaskPaneContainer() {
		return taskPaneContainer;
	}

	public OptionPanel() {
		taskPaneContainer.setLayout(new MigLayout("insets 0, wrap 1"));

		databaseSearch = new JXTaskPane();
		databaseSearch.setTitle("Database Search");
		databaseSearch.setSpecial(true);
		databaseSearch.setCollapsed(false);
		databaseSearch.setScrollOnExpand(true);

		dw = new DatabaseWindow();
		databaseSearch.add(dw.getPanel());

		elements = new JXTaskPane();
		elements.setTitle("Graph Elements");
		tree = new ElementTree();
		elements.add(tree.getScrollTree());
		elements.setCollapsed(true);

		pathways = new JXTaskPane();
		pathways.setTitle("Pathway Tree");
		pathwayTree = new PathwayTree();
		pathways.add(pathwayTree.getPanel());
		pathways.setCollapsed(true);

		theory = new JXTaskPane();
		theory.setTitle("Graph Analysis");
		graphAlgorithms = new GraphAlgorithmsWindow();
		theory.add(graphAlgorithms.getTheoryPane());
		theory.setCollapsed(true);

		satellite = new JXTaskPane();
		satellite.setTitle("Satellite View");
		satelliteWindow = new SatelliteWindow();
		satellite.add(satelliteWindow.getSatellitePane());
		satellite.setCollapsed(true);

		//
		// // init task pane and viz-component
		simResView = new JXTaskPane();
		simResView.setTitle("Petri Net Simulation");
		simResView.setCollapsed(true);
		//simResView.setAnimated(false);
		simResWindow = new SimulationResultsPlot();
		simResView.add(simResWindow.getPanel());

		
		generalProperties = new JXTaskPane();
		generalProperties.setTitle("Element Properties");
		elementWindow = new ElementWindow();
		generalProperties.add(elementWindow.getPanel());
		generalProperties.setCollapsed(true);
		
		pathwayProperties = new JXTaskPane();
		pathwayProperties.setTitle("Pathway Properties");
		pathwayPropertiesWindow = new PathwayPropertiesWindow();
		pathwayProperties.add(pathwayPropertiesWindow.getPanel());
		pathwayProperties.setCollapsed(true);

		bbProperties = new JXTaskPane();
		bbProperties.setTitle("Building Blocks");
		bb = new BuildingBlocks();
		bbProperties.add(bb.getPanel());
		bbProperties.setCollapsed(true);

		project = new JXTaskPane();
		project.setTitle("Project Description");
		projectWindow = new ProjectWindow();
		project.add(projectWindow.getPanel());
		project.setCollapsed(true);

		// alignment = new JXTaskPane();
		// alignment.setTitle("Graph Alignment");
		// alignment.setCollapsed(false);
		// alignment.setVisible(false);

		/*
		 * petriNet = new JXTaskPane(); petriNet.setTitle("PetriNetProperties");
		 * petriNet.setCollapsed(false); petriNet.setVisible(true); petriNetProperties =
		 * new PetriNetProperties(); petriNet.add(petriNetProperties.getPanel());
		 * taskPaneContainer.add(petriNet);
		 */

		if (MainWindow.developer) {
			taskPaneContainer.add(databaseSearch, "growx");
			taskPaneContainer.add(simResView, "growx");
			taskPaneContainer.add(generalProperties, "growx");
			taskPaneContainer.add(pathwayProperties, "growx");
			taskPaneContainer.add(theory, "growx");
			taskPaneContainer.add(satellite, "growx");
			taskPaneContainer.add(elements, "growx");
			taskPaneContainer.add(bbProperties, "growx");
			taskPaneContainer.add(pathways, "growx");
			// taskPaneContainer.add(dbProperties);
//			taskPaneContainer.add(heatgraphProperties, "growx");
			taskPaneContainer.add(project, "growx");
			// taskPaneContainer.add(alignment);
		} else {
			taskPaneContainer.add(databaseSearch, "growx");
			taskPaneContainer.add(simResView, "growx");
			taskPaneContainer.add(generalProperties, "growx");
			taskPaneContainer.add(pathwayProperties, "growx");
			taskPaneContainer.add(theory, "growx");
			taskPaneContainer.add(satellite, "growx");
			taskPaneContainer.add(elements, "growx");
			taskPaneContainer.add(bbProperties, "growx");
			taskPaneContainer.add(pathways, "growx");
			// taskPaneContainer.add(dbProperties);
			taskPaneContainer.add(project, "growx");
			// taskPaneContainer.add(pcpview);

		}

		taskPaneContainer.setScrollableTracksViewportHeight(true);
		p.add(taskPaneContainer, BorderLayout.CENTER);
		taskPaneContainer.setDoubleBuffered(true);

		scrollPane = new JScrollPane(p);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	}

	public JScrollPane getPanel() {
		return scrollPane;
	}

	public void removeAllElements() {
		tree.removeTree();
		bb.removeTree();
		pathwayTree.removeTree();
		satelliteWindow.removeAllElements();
		simResWindow.removeAllElements();
		elementWindow.removeAllElements();
		pathwayPropertiesWindow.removeAllElements();
		projectWindow.removeAllElements();
		graphAlgorithms.removeAllElements();

	}

	public void enableDatabaseWindow(boolean enable) {
		databaseSearch.setEnabled(enable);
	}

	public void updatePanel(String element) {
		// System.out.println(element);
		if (updatePanels) {
			if (element.equals("GraphTree")) {
				tree.revalidateTree();
			} else if (element.equals("Satellite")) {
				satelliteWindow.revalidateSatelliteView();
			} else if (element.equals("simulation")) {
				// PCPWindow.initGraphs();
				simResWindow.revalidateView();
			} else if (element.equals("element")) {
				elementWindow.revalidateView();
			} else if (element.equals("project")) {
				projectWindow.revalidateView();
			} else if (element.equals("theory")) {
				graphAlgorithms.revalidateView();
			}
			else if (element.equals("pathwayTree")) {
				pathwayTree.revalidateView();
			} else if (element.equals("initSimulation")) {
				simResWindow.initGraphs();
			} else if (element.equals("bb")) {
//				bb.revalidateView();
			} else if(element.equals("pathwayProperties")){
				pathwayPropertiesWindow.revalidateView();
			}
		}
	}

	public boolean isUpdatePanels() {
		return updatePanels;
	}

	public void setUpdatePanels(boolean updatePanels) {
		this.updatePanels = updatePanels;
	}

	public void redrawGraphs(boolean fireSerieState) {
		if (simResView.isCollapsed()) {
			simResView.setAnimated(false);
			simResView.setCollapsed(false);
			simResView.setAnimated(true);
		}
		simResWindow.updateDateCurrentSimulation(fireSerieState);
	}

	public void initSimResGraphs() {
		simResWindow.initGraphs();
	}
	
	public void redrawTokens(){
		elementWindow.redrawTokens();
	}
}
