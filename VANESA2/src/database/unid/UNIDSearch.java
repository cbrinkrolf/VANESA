package database.unid;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.HashSet;

import graph.algorithms.gui.GraphColoringGUI;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import javax.swing.SwingWorker;

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
		
		//MARTIN set search depth by user
		this.depth = 2;
		this.helper = new SearchCallback(this);
	}

	protected Object doInBackground() throws Exception {
		
		String url = "rmi://cassiopeidae/ClusterJobs";
		server = (IJobServer) Naming.lookup(url);
		server.submitSearch(fullName,depth,helper);
		
		return null;
	}

	public void done() {
		//DONE method useless, helper handles Callback already
	}

	public void reactiveateUI() {
		// close Progress bar and reactivate UI
		UNIDSearch.progressBar.closeWindow();
		mw = MainWindowSingelton.getInstance();
		mw.setEnable(true);
		mw.setLockedPane(false);
	}
	
	
	public void createNetworkFromSearch(HashMap<String,HashSet<String>> adjacencylist){
		
		//MARTIN create network panel from adjacency list
		
//		
//		Pathway pw = new CreatePathway("GRAPHDBSEARCH")
//		.getPathway();
//		MyGraph myGraph = pw.getGraph();
//
//		myGraph.lockVertices();
//		myGraph.stopVisualizationModel();
//		
//		//DO ADDING
//		
//		
//		myGraph.unlockVertices();
//		myGraph.restartVisualizationModel();
//
//		myGraph.normalCentering();
//
//		 MainWindow window = MainWindowSingelton.getInstance();
//		 window.updateOptionPanel();
//		// window.enable(true);
//		pw.getGraph().changeToGEMLayout();
	}
}
