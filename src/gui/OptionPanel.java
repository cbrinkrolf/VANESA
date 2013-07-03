package gui;

import graph.gui.ElementWindow;
import graph.gui.HeatgraphPropertiesWindow;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import biologicalElements.Pathway;
import configurations.DeveloperClass;
import database.gui.DatabaseWindow;

public class OptionPanel {

	private JPanel p = new JPanel();

	private JScrollPane scrollPane;

	private DatabaseWindow dw;

	 private ElementTree tree;

	private SatelliteWindow satelliteWindow;

	private GraphProperties graphProperties;

	// private ElementInformationWindow information;

	private ElementWindow elementWindow;

	private ProjectWindow projectWindow;

	private JXTaskPane elements;

	private JXTaskPane satellite;

	private PetriNetProperties petriNetProperties;


	// Taskpane for the microarray data visualization component
	private JXTaskPane pcpview;

	// GUI component used for rendering the parallel coordinates plot
	private ParallelCoordinatesPlot PCPWindow;

	private JXTaskPane filter;

	// private JXTaskPane dbProperties;

	private JXTaskPane generalProperties;

	private JXTaskPane databaseSearch;

	private JXTaskPane project;

	// private JXTaskPane edges;

	private JXTaskPane theory;

	private JXTaskPane petriNet;

	private JXTaskPane pathways;
	
	private PathwayTree pathwayTree;
	
	private boolean updatePanels = true;

	private GraphAlgorithmsWindow graphAlgorithms;

	private JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

	// private GraphAlignmentOptionTab alignmentOptions;
	// private JXTaskPane alignment;
	// private HashMap<String, GraphAlignmentOptionTab> alignmentTabs = new
	// HashMap<String, GraphAlignmentOptionTab>();

	private HeatgraphPropertiesWindow heatgraphPropertiesWindow;

	private JXTaskPane heatgraphProperties;

	public JXTaskPaneContainer getTaskPaneContainer() {
		return taskPaneContainer;
	}

	public OptionPanel() {

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

		 pathways =new JXTaskPane();
		 pathways.setTitle("Pathway Tree");
		 pathwayTree = new PathwayTree();
		 pathways.add(pathwayTree);
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
		//
		// // init task pane and viz-component
		pcpview = new JXTaskPane();
		pcpview.setTitle("Petri Net Simulation");
		pcpview.setCollapsed(true);
		PCPWindow = new ParallelCoordinatesPlot();
		pcpview.add(PCPWindow.getPanel());

		 elements.setCollapsed(true);
		// databaseSearch.setCollapsed(false);
		// satellite.setCollapsed(true);
		//
		filter = new JXTaskPane();
		filter.setTitle("Show/Hide Nodes");
		graphProperties = new GraphProperties();
		filter.add(graphProperties.getPanel());
		filter.setCollapsed(true);

		// // edges = new JXTaskPane();
		// // edges.setTitle("Show/Hide Edges");
		// // graphProperties = new GraphProperties();
		// // edges.add(graphProperties.getPanel());
		// // edges.setCollapsed(true);
		//
		// dbProperties = new JXTaskPane();
		// dbProperties.setTitle("Element Information");
		// information = new ElementInformationWindow();
		// dbProperties.add(information.getPanel());
		// dbProperties.setCollapsed(true);

		generalProperties = new JXTaskPane();
		generalProperties.setTitle("Element Properties");
		elementWindow = new ElementWindow();
		generalProperties.add(elementWindow.getPanel());
		generalProperties.setCollapsed(true);

		heatgraphProperties = new JXTaskPane();
		heatgraphProperties.setTitle("Heatgraph Properties");
		heatgraphPropertiesWindow = new HeatgraphPropertiesWindow();
		heatgraphProperties.add(heatgraphPropertiesWindow.getPanel());
		heatgraphProperties.setCollapsed(true);

		project = new JXTaskPane();
		project.setTitle("Project Description");
		projectWindow = new ProjectWindow();
		project.add(projectWindow.getPanel());
		project.setCollapsed(true);

		// alignment = new JXTaskPane();
		// alignment.setTitle("Graph Alignment");
		// alignment.setCollapsed(false);
		// alignment.setVisible(false);

		/*petriNet = new JXTaskPane();
		petriNet.setTitle("PetriNetProperties");
		petriNet.setCollapsed(false);
		petriNet.setVisible(true);
		petriNetProperties = new PetriNetProperties();
		petriNet.add(petriNetProperties.getPanel());
		taskPaneContainer.add(petriNet);*/

		if (DeveloperClass.isDeveloperStatus) {
			taskPaneContainer.add(databaseSearch);
			taskPaneContainer.add(pcpview);
			taskPaneContainer.add(satellite);
			taskPaneContainer.add(elements);
			taskPaneContainer.add(pathways);
			taskPaneContainer.add(filter);
			taskPaneContainer.add(theory);
			// taskPaneContainer.add(dbProperties);
			taskPaneContainer.add(generalProperties);
			taskPaneContainer.add(heatgraphProperties);
			taskPaneContainer.add(project);
			// taskPaneContainer.add(alignment);
		} else {
			taskPaneContainer.add(databaseSearch);
			taskPaneContainer.add(satellite);
			taskPaneContainer.add(elements);
			taskPaneContainer.add(pathways);
			taskPaneContainer.add(filter);
			taskPaneContainer.add(theory);
			// taskPaneContainer.add(dbProperties);
			taskPaneContainer.add(generalProperties);
			taskPaneContainer.add(project);
			//taskPaneContainer.add(pcpview);
			

		}

		taskPaneContainer.setScrollableTracksViewportHeight(true);
		p.add(taskPaneContainer, BorderLayout.CENTER);
		taskPaneContainer.setDoubleBuffered(true);

	}

	public JScrollPane getPanel() {
		scrollPane = new JScrollPane(p);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		return scrollPane;
	}

	public void removeAllElements() {
		tree.removeTree();
		pathwayTree.removeTree();
		satelliteWindow.removeAllElements();
		PCPWindow.removeAllElements();
		graphProperties.removeAllElements();
		elementWindow.removeAllElements();
		// information.removeAllElements();
		projectWindow.removeAllElements();
		graphAlgorithms.removeAllElements();

	}

	public void enableDatabaseWindow(boolean enable) {
		databaseSearch.setEnabled(enable);
	}

	public void updatePanel(String element) {
		if (updatePanels) {
			if (element.equals("GraphTree")) {
				 tree.revalidateTree();
			} else if (element.equals("Satellite")) {
				satelliteWindow.revalidateSatelliteView();
				// } else if (element.equals("pcp")) {
				PCPWindow.revalidateView();
			} else if (element.equals("element")) {
				elementWindow.revalidateView();
				System.out.println("bla");
			} else if (element.equals("Filter")) {
				graphProperties.revalidateView();
			} else if (element.equals("project")) {
				projectWindow.revalidateView();
				// } // else if (element.equals("Database")) {
				// System.out.println("updatePanelDatabase");
				// information.revalidateView();
			} else if (element.equals("theory")) {
			//	System.out.println("update graph thoery");
				graphAlgorithms.revalidateView();
			}// else if (element.equals("DAWISVertexWindow")) {
				// information.revalidateDAWISVertexWindow();
				// } else if (element.equals("DAWISVertexWindow")) {
				// //
				// }
			else if (element.equals("pathwayTree")){
				pathwayTree.revalidateView();
			}
		}
	}

	public boolean isUpdatePanels() {
		return updatePanels;
	}

	public void setUpdatePanels(boolean updatePanels) {
		this.updatePanels = updatePanels;
	}

	public void openAlignmentPanel(Pathway a, Pathway b) {

	//	System.out.println("update option panel - alignmet");

		// elements.setCollapsed(true);
		// satellite.setCollapsed(true);
		// pcpview.setCollapsed(true);
		// filter.setCollapsed(true);
		// dbProperties.setCollapsed(true);
		// generalProperties.setCollapsed(true);
		// databaseSearch.setCollapsed(true);
		// project.setCollapsed(true);
		// theory.setCollapsed(true);
		//
		// String name = "Alignment of " + a.getName() + " and " + b.getName();
		// name = ContainerSingelton.getInstance().checkNameDuplicates(name, 1);
		// alignmentOptions = new GraphAlignmentOptionTab(a, b, name);
		// alignmentTabs.put(name, alignmentOptions);
		//
		// alignment.removeAll();
		// alignment.add(alignmentOptions.getPanel());
		// alignment.setCollapsed(false);
		// alignment.setVisible(true);

	}

	public void updateAlignmentTab() {

	//	System.out.println("update option panel -alignment");

		String title = MainWindowSingelton.getInstance().getCurrentPathway();
		// if (alignmentTabs.containsKey(title)) {
		// alignmentOptions = alignmentTabs.get(title);
		// alignment.removeAll();
		// alignmentOptions.updateWindow();
		// alignment.add(alignmentOptions.getPanel());
		// alignment.setCollapsed(false);
		// alignment.setVisible(true);
		// } else {
		// alignment.setVisible(false);
		// }
	}

	public void tryUpdateAlignmentOptionTab(String oldName, String newName) {
		// if (alignmentTabs.containsKey(oldName)) {
		// GraphAlignmentOptionTab aliTab = alignmentTabs.get(oldName);
		// aliTab.setTabTitle(newName);
		// alignmentTabs.remove(oldName);
		// alignmentTabs.put(newName, aliTab);
		// }

	}
}
