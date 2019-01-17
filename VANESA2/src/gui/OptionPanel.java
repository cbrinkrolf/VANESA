package gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import biologicalElements.Pathway;
import database.gui.DatabaseWindow;
import graph.gui.ElementWindow;
import net.miginfocom.swing.MigLayout;

public class OptionPanel {

	private JPanel p = new JPanel(new MigLayout("insets 0"));

	private JScrollPane scrollPane;

	private DatabaseWindow dw;

	 private ElementTree tree;
	 
	 private BuildingBlocks bb;

	private SatelliteWindow satelliteWindow;
	
	// private ElementInformationWindow information;

	private ElementWindow elementWindow;

	private ProjectWindow projectWindow;

	private JXTaskPane elements;

	private JXTaskPane satellite;
	
	//private PetriNetProperties petriNetProperties;


	// Taskpane for the microarray data visualization component
	private JXTaskPane pcpview;

	// GUI component used for rendering the parallel coordinates plot
	private ParallelCoordinatesPlot PCPWindow;

	// private JXTaskPane dbProperties;

	private JXTaskPane generalProperties;

	private JXTaskPane databaseSearch;

	private JXTaskPane project;

	// private JXTaskPane edges;

	private JXTaskPane theory;

	//private JXTaskPane petriNet;

	private JXTaskPane pathways;
	
	private PathwayTree pathwayTree;
	
	private boolean updatePanels = true;

	private GraphAlgorithmsWindow graphAlgorithms;

	private JXTaskPaneContainer taskPaneContainer = new JXTaskPaneContainer();

	// private GraphAlignmentOptionTab alignmentOptions;
	// private JXTaskPane alignment;
	// private HashMap<String, GraphAlignmentOptionTab> alignmentTabs = new
	// HashMap<String, GraphAlignmentOptionTab>();

	private JXTaskPane bbProperties;

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
		satellite.setCollapsed(true);
		
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
		
		bbProperties = new JXTaskPane();
		bbProperties.setTitle("Building Blocks");
		bb = new BuildingBlocks();
		bbProperties.add(bb);
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

		/*petriNet = new JXTaskPane();
		petriNet.setTitle("PetriNetProperties");
		petriNet.setCollapsed(false);
		petriNet.setVisible(true);
		petriNetProperties = new PetriNetProperties();
		petriNet.add(petriNetProperties.getPanel());
		taskPaneContainer.add(petriNet);*/

		if (MainWindow.developer) {
			taskPaneContainer.add(databaseSearch, "growx");
			taskPaneContainer.add(pcpview, "growx");
			taskPaneContainer.add(theory, "growx");
			taskPaneContainer.add(generalProperties, "growx");
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
			taskPaneContainer.add(pcpview, "growx");
			taskPaneContainer.add(theory, "growx");
			taskPaneContainer.add(generalProperties, "growx");
			taskPaneContainer.add(satellite, "growx");
			taskPaneContainer.add(elements, "growx");
			taskPaneContainer.add(bbProperties, "growx");
			taskPaneContainer.add(pathways, "growx");
			// taskPaneContainer.add(dbProperties);
			taskPaneContainer.add(project, "growx");
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
		bb.removeTree();
		pathwayTree.removeTree();
		satelliteWindow.removeAllElements();
		PCPWindow.removeAllElements();
		elementWindow.removeAllElements();
		// information.removeAllElements();
		projectWindow.removeAllElements();
		graphAlgorithms.removeAllElements();

	}

	public void enableDatabaseWindow(boolean enable) {
		databaseSearch.setEnabled(enable);
	}

	public void updatePanel(String element) {
		//System.out.println(element);
		if (updatePanels) {
			if (element.equals("GraphTree")) {
				 tree.revalidateTree();
			} else if (element.equals("Satellite")) {
				satelliteWindow.revalidateSatelliteView();
			} else if (element.equals("pcp")) {
				//PCPWindow.initGraphs();
				PCPWindow.revalidateView();
			} else if (element.equals("element")) {
				elementWindow.revalidateView();
				//System.out.println("bla");
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
			}else if (element.equals("initPCP")){
				PCPWindow.initGraphs();
			} else if (element.equals("bb")){
//				bb.revalidateView();
			}
		}
	}

	public boolean isUpdatePanels() {
		return updatePanels;
	}

	public void setUpdatePanels(boolean updatePanels) {
		this.updatePanels = updatePanels;
	}

	public void redrawGraphs(){
		PCPWindow.updateDateCurrentSimulation();
	}
	
	public void initPCPGraphs(){
		PCPWindow.initGraphs();
	}
}
