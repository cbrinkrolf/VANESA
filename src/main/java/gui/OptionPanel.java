package gui;

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
	private final ElementTree tree;
	private final SimulationResultsPlot simResWindow;
	private final BuildingBlocks bb;
	private final SatelliteWindow satelliteWindow;
	private final ElementWindow elementWindow;
	private final ProjectWindow projectWindow;
	private final PathwayTree pathwayTree;
	private final GraphAlgorithmsWindow graphAlgorithms;
	private final PathwayPropertiesWindow pathwayPropertiesWindow;

	private final JXTaskPane simResView;
	private final JXTaskPane databaseSearch;

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

		final DatabaseWindow databaseWindow = new DatabaseWindow();
		databaseSearch = createCollapsiblePane("Database Search",
				ImagePath.getInstance().getImageIcon("database-search-outline.png", 16, 16), false, databaseWindow);
		databaseSearch.setSpecial(true);
		databaseSearch.setScrollOnExpand(true);

		tree = new ElementTree();
		final JXTaskPane elements = createCollapsiblePane("Graph Elements", null, true, tree);

		pathwayTree = new PathwayTree();
		final JXTaskPane pathways = createCollapsiblePane("Pathway Tree", null, true, pathwayTree);

		graphAlgorithms = new GraphAlgorithmsWindow();
		final JXTaskPane theory = createCollapsiblePane("Graph Analysis", null, true, graphAlgorithms.getTheoryPane());

		satelliteWindow = new SatelliteWindow();
		final JXTaskPane satellite = createCollapsiblePane("Satellite View", null, true, satelliteWindow);

		simResWindow = new SimulationResultsPlot();
		simResView = createCollapsiblePane("Petri Net Simulation", null, true, simResWindow);

		elementWindow = new ElementWindow();
		final JXTaskPane generalProperties = createCollapsiblePane("Element Properties", null, true,
				elementWindow.getPanel());

		pathwayPropertiesWindow = new PathwayPropertiesWindow();
		final JXTaskPane pathwayProperties = createCollapsiblePane("Pathway Properties", null, true,
				pathwayPropertiesWindow);

		bb = new BuildingBlocks();
		final JXTaskPane bbProperties = createCollapsiblePane("Building Blocks", null, true, bb.getPanel());

		projectWindow = new ProjectWindow();
		final JXTaskPane project = createCollapsiblePane("Project Description", null, true, projectWindow.getPanel());

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
			// taskPaneContainer.add(heatgraphProperties, "growx");
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

	private JXTaskPane createCollapsiblePane(final String title, final Icon icon, final boolean collapsed,
			final JComponent container) {
		final JXTaskPane pane = new JXTaskPane(title, icon);
		pane.setLayout(new MigLayout("ins 0, fill"));
		pane.add(container, "growx");
		pane.setCollapsed(collapsed);
		return pane;
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
