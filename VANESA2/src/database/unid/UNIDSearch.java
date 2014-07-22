package database.unid;

import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.GraphNode;

import cluster.IJobServer;
import cluster.SearchCallback;
import cluster.graphdb.GraphDBTransportNode;

/**
 * 
 * @author mlewinsk
 * June 2014
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
	
	private HashMap<GraphDBTransportNode, HashSet<GraphDBTransportNode>> adjacencylist;

	public UNIDSearch(String[] input) {
		this.organism = input[0];
		this.fullName = input[1];
		this.commonName = input[2];
		this.graphid = input[3];
		this.depth = (int) Double.parseDouble(input[4]);
		try{
			this.helper = new SearchCallback(this);
		}catch(RemoteException re){
			re.printStackTrace();
		}
	}

	protected Object doInBackground() throws Exception {
		
		boolean multi_id_search = false;
		HashSet<String> commonNames = new HashSet<String>();
		if(commonName.contains(",")){
			multi_id_search = true;
			String name[] = commonName.split(",");
			for (int i = 0; i < name.length; i++) {
				commonNames.add(name[i]);
			}			
		}		

		try{
		 String url = "rmi://cassiopeidae/ClusterJobs";
		 server = (IJobServer) Naming.lookup(url);
		 if(multi_id_search){
			 server.submitSearch(commonNames, depth, "any", helper);
		 }else{
			 //DEBUG dataset search
			 server.submitSearch(commonName,depth,helper);
			 
//			 HashSet<String> datasets = new HashSet<String>();
//			 datasets.add("FC_68_S01_GE2_107_Sep09_1_1");
//			 server.submitSearch(datasets, 1, helper);
		 }
		}catch(Exception e){
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(MainWindowSingelton
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
		mw = MainWindowSingelton.getInstance();
		mw.setEnable(true);
		mw.setLockedPane(false);
	}

	/**
	 * Creates a new Network tab with the 
	 */
	public void createNetworkFromSearch() {
		
		Pathway pw = new CreatePathway(fullName+commonName+graphid+" depth="+depth+"(UNID)").getPathway();
		MyGraph myGraph = pw.getGraph();

		myGraph.lockVertices();
		myGraph.stopVisualizationModel();

		// DO ADDING
		GraphNode bna;
		HashSet<GraphDBTransportNode> nodeset = new HashSet<>();
		HashMap<GraphDBTransportNode, BiologicalNodeAbstract> nodes = new HashMap<>();

		
		
		//Nodes first
		for (GraphDBTransportNode node : adjacencylist.keySet()) {
			if (!nodeset.contains(node)) {
				nodeset.add(node);
				bna = new GraphNode(node);
				bna.setReference(false);
				pw.addVertex(bna, new Point(150, 100));
				nodes.put(node, bna);
			}
			HashSet<GraphDBTransportNode> companions = adjacencylist.get(node);
			for (GraphDBTransportNode companion : companions) {
				if (!nodeset.contains(companion)) {
					nodeset.add(companion);
					bna = new GraphNode(companion);
					bna.setReference(false);
					pw.addVertex(bna, new Point(150, 100));
					nodes.put(companion, bna);
				}
			}
		}
		
		//then edges
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

		myGraph.normalCentering();

		MainWindow window = MainWindowSingelton.getInstance();
		window.updateOptionPanel();
		window.setEnabled(true);
		pw.getGraph().changeToCircleLayout();

		reactivateUI();

	}
	
	/**
	 * Set adjacency list, usually called by SearchCallback
	 * @param adjacencylist
	 */
	public void setAdjacencyList(HashMap<GraphDBTransportNode, HashSet<GraphDBTransportNode>> adjacencylist){
		this.adjacencylist = adjacencylist;
		
	}
}
