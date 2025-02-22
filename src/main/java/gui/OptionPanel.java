package gui;

import java.awt.BorderLayout;

import javax.swing.*;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import configurations.SettingsManager;
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
	private final JScrollPane scrollPane;
	private final JPanel panel;
	private final DatabaseWindow databaseWindow;
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
	private int lastFullWidth = -1;

	private final JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

	// private GraphAlignmentOptionTab alignmentOptions;
	// private JXTaskPane alignment;
	// private HashMap<String, GraphAlignmentOptionTab> alignmentTabs = new
	// HashMap<String, GraphAlignmentOptionTab>();

	public JXTaskPaneContainer getTaskPaneContainer() {
		return taskPaneContainer;
	}

	public OptionPanel() {
		taskPaneContainer.setLayout(new MigLayout("insets 0, wrap, fill"));
		taskPaneContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

		databaseSearch = new JXTaskPane("Database Search",
				ImagePath.getInstance().getImageIcon("database-search-outline.png", 16, 16));
		databaseSearch.setSpecial(true);
		databaseSearch.setCollapsed(false);
		databaseSearch.setScrollOnExpand(true);
		databaseWindow = new DatabaseWindow();
		databaseSearch.add(databaseWindow.getPanel());

		elements = new JXTaskPane("Graph Elements");
		tree = new ElementTree();
		elements.add(tree.getScrollTree());
		elements.setCollapsed(true);

		pathways = new JXTaskPane("Pathway Tree");
		pathwayTree = new PathwayTree();
		pathways.add(pathwayTree.getPanel());
		pathways.setCollapsed(true);

		theory = new JXTaskPane("Graph Analysis");
		graphAlgorithms = new GraphAlgorithmsWindow();
		theory.add(graphAlgorithms.getTheoryPane());
		theory.setCollapsed(true);

		satellite = new JXTaskPane("Satellite View");
		satelliteWindow = new SatelliteWindow();
		satellite.add(satelliteWindow.getSatellitePane());
		satellite.setCollapsed(true);

		// // init task pane and viz-component
		simResView = new JXTaskPane("Petri Net Simulation");
		simResView.setCollapsed(true);
		// simResView.setAnimated(false);
		simResWindow = new SimulationResultsPlot();
		simResView.add(simResWindow.getPanel());

		generalProperties = new JXTaskPane("Element Properties");
		elementWindow = new ElementWindow();
		generalProperties.add(elementWindow.getPanel());
		generalProperties.setCollapsed(true);

		pathwayProperties = new JXTaskPane("Pathway Properties");
		pathwayPropertiesWindow = new PathwayPropertiesWindow();
		pathwayProperties.add(pathwayPropertiesWindow.getPanel());
		pathwayProperties.setCollapsed(true);

		bbProperties = new JXTaskPane("Building Blocks");
		bb = new BuildingBlocks();
		bbProperties.add(bb.getPanel());
		bbProperties.setCollapsed(true);

		project = new JXTaskPane("Project Description");
		projectWindow = new ProjectWindow();
		project.add(projectWindow.getPanel());
		project.setCollapsed(true);

		// alignment = new JXTaskPane("Graph Alignment");
		// alignment.setCollapsed(false);
		// alignment.setVisible(false);

		/*
		 * petriNet = new JXTaskPane("Petri Net Properties");
		 * petriNet.setCollapsed(false); petriNet.setVisible(true); petriNetProperties =
		 * new PetriNetProperties(); petriNet.add(petriNetProperties.getPanel());
		 * taskPaneContainer.add(petriNet);
		 */

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
		if (SettingsManager.getInstance().isDeveloperMode()) {
//			taskPaneContainer.add(heatgraphProperties, "growx");
			taskPaneContainer.add(project, "growx");
			// taskPaneContainer.add(alignment);
		} else {
			taskPaneContainer.add(project, "growx");
			// taskPaneContainer.add(pcpview);
		}

		taskPaneContainer.setScrollableTracksViewportHeight(true);
		panel = new JPanel(new MigLayout("insets 0, fill"));
		panel.add(taskPaneContainer, "growx, top");
		taskPaneContainer.setDoubleBuffered(true);

		scrollPane = new JScrollPane(panel);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	}

	public JScrollPane getPanel() {
		return scrollPane;
	}

	public JPanel getContentPanel() {
		return panel;
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
		// System.out.println("element: " + element);
		if (!updatePanels)
			return;
		switch (element) {
		case "GraphTree":
			tree.revalidateTree();
			break;
		case "Satellite":
			satelliteWindow.revalidateSatelliteView();
			break;
		case "simulation":
			// PCPWindow.initGraphs();
			simResWindow.revalidateView();
			break;
		case "element":
			elementWindow.revalidateView();
			break;
		case "project":
			projectWindow.revalidateView();
			break;
		case "theory":
			graphAlgorithms.revalidateView();
			break;
		case "pathwayTree":
			pathwayTree.revalidateView();
			break;
		case "initSimulation":
			simResWindow.initGraphs();
			break;
		case "bb":
			// bb.revalidateView();
			break;
		case "pathwayProperties":
			pathwayPropertiesWindow.revalidateView();
			break;
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

	public void redrawTokens() {
		elementWindow.redrawTokens();
	}

	public void addSimulationResults() {
		simResWindow.addSimulationResults();
	}

	public int getFullWidth() {
		if (scrollPane.getVerticalScrollBar().isShowing()) {
			lastFullWidth = (int) scrollPane.getPreferredSize().getWidth() + UIManager.getInt("ScrollBar.width");
		} else {
			lastFullWidth = (int) scrollPane.getPreferredSize().getWidth();
		}
		return lastFullWidth;
	}

	public int getLastFullWidth() {
		return lastFullWidth;
	}
}
