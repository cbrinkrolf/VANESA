package database.unid;

import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Other;

import cluster.IJobServer;
import cluster.SearchCallback;

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
	private String alias;
	private String organism;
	private int depth;
	
	private HashMap<String, HashSet<String>> adjacencylist;

	public UNIDSearch(String[] input) {
		this.organism = input[0];
		this.fullName = input[1];
		this.alias = input[2];
		this.graphid = input[3];
		this.depth = (int) Double.parseDouble(input[4]);
		try{
			this.helper = new SearchCallback(this);
		}catch(RemoteException re){
			re.printStackTrace();
		}
	}

	protected Object doInBackground() throws Exception {

		try{
		 String url = "rmi://cassiopeidae/ClusterJobs";
		 server = (IJobServer) Naming.lookup(url);
		 server.submitSearch(fullName,depth,helper);
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public void reactiveateUI() {
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
		
		Pathway pw = new CreatePathway(fullName+" depth="+depth+"(UNID)").getPathway();
		MyGraph myGraph = pw.getGraph();

		myGraph.lockVertices();
		myGraph.stopVisualizationModel();

		// DO ADDING
		Other bna;
		HashSet<String> nodeset = new HashSet<String>();
		HashMap<String, BiologicalNodeAbstract> nodes = new HashMap<>();

		//Nodes first
		for (String node : adjacencylist.keySet()) {
			if (!nodeset.contains(node)) {
				nodeset.add(node);
				bna = new Other(node, node);
				bna.setReference(false);
				pw.addVertex(bna, new Point(150, 100));
				nodes.put(node, bna);
			}
			HashSet<String> companions = adjacencylist.get(node);
			for (String companion : companions) {
				if (!nodeset.contains(companion)) {
					nodeset.add(companion);
					bna = new Other(companion, companion);
					bna.setReference(false);
					pw.addVertex(bna, new Point(150, 100));
					nodes.put(companion, bna);
				}
			}
		}
		
		//then edges
		ReactionEdge r;
		for (String node : adjacencylist.keySet()) {
			HashSet<String> companions = adjacencylist.get(node);
			for (String companion : companions) {
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
		pw.getGraph().changeToGEMLayout();

		reactiveateUI();

	}
	
	/**
	 * Set adjacency list, usually called by SearchCallback
	 * @param adjacencylist
	 */
	public void setAdjacencyList(HashMap<String, HashSet<String>> adjacencylist){
		this.adjacencylist = adjacencylist;
		
	}
}
