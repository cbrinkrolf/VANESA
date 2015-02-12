package database.unid;

import java.awt.Color;
import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import graph.CreatePathway;
import graph.algorithms.NodeAttributeNames;
import graph.algorithms.NodeAttributeTypes;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ProgressBar;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.GraphNode;
import biologicalObjects.nodes.Protein;
import cluster.IJobServer;
import cluster.SearchCallback;
import cluster.graphdb.DatabaseEntry;
import cluster.graphdb.GraphDBTransportNode;

/**
 * 
 * @author mlewinsk June 2014
 */
public class UNIDSearch extends SwingWorker<Object, Object> {

	public static ProgressBar progressBar;
	private MainWindow mw;
	private IJobServer server;
	private SearchCallback helper;

	private String graphid;
	private String fullName;
	private String commonName;
	private String organism;
	private int depth;
	private HashSet<String> searchNames;

	private boolean headless;

	private HashMap<GraphDBTransportNode, HashSet<GraphDBTransportNode>> adjacencylist;

	public UNIDSearch(String[] input, boolean headless) {
		this.organism = input[0];
		this.fullName = input[1];
		this.commonName = input[2];
		this.graphid = input[3];
		this.depth = (int) Double.parseDouble(input[4]);
		this.searchNames = new HashSet<>();
		try {
			this.helper = new SearchCallback(this);
		} catch (RemoteException re) {
			re.printStackTrace();
		}
		this.headless = headless;
	}

	protected Object doInBackground() throws Exception {

		// check for multi name input
		boolean multi_id_search = false;
		HashSet<String> commonNames = new HashSet<String>();
		if (commonName.contains(",")) {
			multi_id_search = true;
			String name[] = commonName.split(",");
			for (int i = 0; i < name.length; i++) {
				searchNames.add(name[i]);
				commonNames.add(name[i]);
			}
		} else {
			searchNames.add(commonName);
		}

		try {
			String url = "rmi://cassiopeidae/ClusterJobs";
			server = (IJobServer) Naming.lookup(url);
			if (multi_id_search) {
				server.submitSearch(commonNames, depth, "any", helper);
			} else {

				server.submitSearch(commonName, depth, helper);
				// DEBUG Dataset Search
				// HashSet<String> datasets = new HashSet<String>();
				// datasets.add("FC_68_S01_GE2_107_Sep09_1_1");
				// server.submitSearch(datasets, 1, helper);
			}
		} catch (Exception e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingleton
							.getInstance().returnFrame(),
							"Cluster not reachable.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			});
			reactivateUI();
		}

		return null;
	}

	public void reactivateUI() {
		// close Progress bar and reactivate UI
		UNIDSearch.progressBar.closeWindow();
		mw = MainWindowSingleton.getInstance();
		mw.setLockedPane(false);
	}

	/**
	 * Creates a new Network tab with the
	 */
	public void createNetworkFromSearch() {

		Pathway pw = new CreatePathway(fullName + commonName + graphid
				+ " depth=" + depth).getPathway();
		MyGraph myGraph = pw.getGraph();

		myGraph.lockVertices();
		myGraph.stopVisualizationModel();

		// DO ADDING
		Protein bna;
		HashSet<GraphDBTransportNode> nodeset = new HashSet<>();
		HashMap<GraphDBTransportNode, BiologicalNodeAbstract> nodes = new HashMap<>();

		// Nodes first
		for (GraphDBTransportNode node : adjacencylist.keySet()) {
			if (!nodeset.contains(node)) {
				nodeset.add(node);
				bna = new Protein(node.commonName, node.fullName);
				//Add Attributes
				addAttributes(bna, node);								
				
				bna.setReference(false);
				if (searchNames.contains(node.commonName)) {
					bna.setColor(Color.RED);
				}
				pw.addVertex(bna, new Point(150, 100));
				nodes.put(node, bna);
			}
			HashSet<GraphDBTransportNode> companions = adjacencylist.get(node);
			for (GraphDBTransportNode companion : companions) {
				if (!nodeset.contains(companion)) {
					nodeset.add(companion);
					bna = new Protein(companion.commonName, companion.fullName);
					addAttributes(bna, companion);
					bna.setReference(false);
					if (searchNames.contains(companion.commonName)) {
						bna.setColor(Color.RED);
					}
					pw.addVertex(bna, new Point(150, 100));
					nodes.put(companion, bna);
				}
			}
		}

		// then edges
		ReactionEdge r;
		for (GraphDBTransportNode node : adjacencylist.keySet()) {
			HashSet<GraphDBTransportNode> companions = adjacencylist.get(node);
			for (GraphDBTransportNode companion : companions) {
				r = new ReactionEdge("", "", nodes.get(node),
						nodes.get(companion));

				r.setDirected(false);
				r.setReference(false);
				r.setHidden(false);
				r.setVisible(true);

				pw.addEdge(r);
			}
		}

		myGraph.unlockVertices();
		myGraph.restartVisualizationModel();

		MainWindow window = MainWindowSingleton.getInstance();
		window.updateOptionPanel();
		window.setEnabled(true);
		if (!headless) {
			pw.getGraph().changeToCircleLayout();
			myGraph.normalCentering();
		}
		reactivateUI();

	}

	private void addAttributes(Protein bna, GraphDBTransportNode node) {
		//Experiments
		int i = 0;
		for(i = 0; i<node.biodata.length; i++){
			bna.addAttribute(NodeAttributeTypes.EXPERIMENT,node.biodata[i], node.biodataEntries[i]);
		}
		
		//Database IDs
		for(DatabaseEntry de :node.dbIds){
			bna.addAttribute(NodeAttributeTypes.DATABASE_ID, de.getDatabase(), de.getId());
		}				
		
		//Annotations
		for(i = 0; i<node.biologicalProcess.length; i++){
			bna.addAttribute(NodeAttributeTypes.ANNOTATION,NodeAttributeNames.GO_BIOLOGICAL_PROCESS, node.biologicalProcess[i]);
		}
		for(i = 0; i<node.cellularComponent.length; i++){
			bna.addAttribute(NodeAttributeTypes.ANNOTATION,NodeAttributeNames.GO_CELLULAR_COMPONENT, node.cellularComponent[i]);
		}
		for(i = 0; i<node.molecularFunction.length; i++){
			bna.addAttribute(NodeAttributeTypes.ANNOTATION,NodeAttributeNames.GO_MOLECULAR_FUNCTION, node.molecularFunction[i]);
		}	
	}

	/**
	 * Set adjacency list, usually called by SearchCallback
	 * 
	 * @param adjacencylist
	 */
	public void setAdjacencyList(
			HashMap<GraphDBTransportNode, HashSet<GraphDBTransportNode>> adjacencylist) {
		this.adjacencylist = adjacencylist;

	}
}
