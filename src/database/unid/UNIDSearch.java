package database.unid;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.HashSet;

import graph.CreatePathway;
import graph.algorithms.gui.GraphColoringGUI;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Other;

import cluster.IJobServer;
import cluster.SearchCallback;

public class UNIDSearch extends SwingWorker {

	public static ProgressBar progressBar;
	private MainWindow mw;
	private IJobServer server;
	private SearchCallback helper;

	private String graphid;
	private String fullName;
	private String alias;
	private String organism;
	private int depth;

	public UNIDSearch(String[] input) {
		this.organism = input[0];
		this.fullName = input[1];
		this.alias = input[2];
		this.graphid = input[3];

		// MARTIN set search depth by user
		this.depth = 2;
		this.helper = new SearchCallback(this);
	}

	protected Object doInBackground() throws Exception {

		// String url = "rmi://cassiopeidae/ClusterJobs";
		// server = (IJobServer) Naming.lookup(url);
		// server.submitSearch(fullName,depth,helper);
		
		
		SwingUtilities.invokeLater(new Runnable() {
			  public void run() {
				  HashMap<String, HashSet<String>> adjacencylist = new HashMap<>();

					HashSet<String> set = new HashSet<>();

					set.add("A");
					set.add("B");

					adjacencylist.put("C", set);

					createNetworkFromSearch(adjacencylist);
			  }
			});
		

		return null;
	}

	public void done() {
		// DONE method useless, helper handles Callback already

		// Debug
//		HashMap<String, HashSet<String>> adjacencylist = new HashMap<>();
//
//		HashSet<String> set = new HashSet<>();
//
//		set.add("A");
//		set.add("B");
//
//		adjacencylist.put("C", set);
//
//		createNetworkFromSearch(adjacencylist);
	}

	public void reactiveateUI() {
		// close Progress bar and reactivate UI
		UNIDSearch.progressBar.closeWindow();
		mw = MainWindowSingelton.getInstance();
		mw.setEnable(true);
		mw.setLockedPane(false);
	}

	public void createNetworkFromSearch(
			HashMap<String, HashSet<String>> adjacencylist) {

		// MARTIN create network panel from adjacency list

		Pathway pw = new CreatePathway("GRAPHDBSEARCH").getPathway();
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
}
