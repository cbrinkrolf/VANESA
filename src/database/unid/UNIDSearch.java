package database.unid;

import java.awt.Point;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;

import graph.CreatePathway;
import graph.algorithms.gui.GraphColoringGUI;
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
import biologicalObjects.nodes.Other;

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
	private String alias;
	private String organism;
	private int depth;
	
	private HashMap<GraphDBTransportNode, HashSet<GraphDBTransportNode>> adjacencylist;

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
		
		Pathway pw = new CreatePathway(fullName+" depth="+depth+"(UNID)").getPathway();
		MyGraph myGraph = pw.getGraph();

		myGraph.lockVertices();
		myGraph.stopVisualizationModel();

		// DO ADDING
		Other bna;
		HashSet<GraphDBTransportNode> nodeset = new HashSet<>();
		HashMap<GraphDBTransportNode, BiologicalNodeAbstract> nodes = new HashMap<>();

		//Nodes first
		for (GraphDBTransportNode node : adjacencylist.keySet()) {
			if (!nodeset.contains(node)) {
				nodeset.add(node);
				bna = new Other(node.commonName, node.commonName);
				bna.setReference(false);
				pw.addVertex(bna, new Point(150, 100));
				nodes.put(node, bna);
			}
			HashSet<GraphDBTransportNode> companions = adjacencylist.get(node);
			for (GraphDBTransportNode companion : companions) {
				if (!nodeset.contains(companion)) {
					nodeset.add(companion);
					bna = new Other(companion.commonName, companion.commonName);
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
