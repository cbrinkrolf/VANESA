package database.dawis;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import edu.uci.ics.jung.graph.Vertex;

import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.DAWISNode;

@SuppressWarnings("unchecked")
public class AllElementLoader extends SwingWorker {

	ProgressBar bar;
	Pathway pw;
	MyGraph myGraph;
	BiologicalNodeAbstract parent;
	ElementLoader loader;
	GraphInstance graphInstance;

	public Vector<String[]> selectedElements = new Vector<String[]>();
	Vector<String> loadedElements = new Vector<String>();
	CollectorNode node = null;
	DAWISNode dawisNode;

	public AllElementLoader(Pathway pw2, CollectorNode bna) {
		pw = pw2;
		myGraph = pw.getGraph();
		graphInstance = new GraphInstance();
		node = bna;
		dawisNode = node.getDAWISNode();
		selectedElements = dawisNode.getElementsAsVector();
		getLoadedElementsLabels();
	}

	private void getLoadedElementsLabels() {
		Iterator<String[]> it = selectedElements.iterator();
		while (it.hasNext()) {
			String[] res = it.next();
			if (!loadedElements.contains(res[0])) {
				loadedElements.add(res[0]);
			}
		}
	}

	/*
	 * stop visualization
	 */
	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	/**
	 * start visualization
	 */
	private void startVisualizationModel() {
		bar.closeWindow();
		myGraph.unlockVertices();
		myGraph.restartVisualizationModel();
	}

	@Override
	protected Object doInBackground() throws Exception {
		Runnable run = new Runnable() {
			public void run() {
				bar = new ProgressBar();
				bar.init(100, "   Loading Data for DAWIS-Function ", true);
				bar.setProgressBarString("Querying Database");
			}
		};
		SwingUtilities.invokeLater(run);

		getElementDetails();

		return null;
	}

	private void getElementDetails() {

		// get loader from node
		if (node.getLoader() != null) {
			loader = node.getLoader();
			loader.setIsDownload(true);
		} else {
			// create new loader
			loader = new ElementLoader(pw);
			loader.setIsDownload(true);
		}

		// set loaded elements
		loader.setNewLoadedElements(loadedElements);

		// prepare parameter details for loading
		Iterator<String[]> it = selectedElements.iterator();
		while (it.hasNext()) {

			// get element details
			String[] elementDetails = it.next();

			// get element label
			String id = elementDetails[0];

			// get element name
			String name = "";
			if (elementDetails.length > 1) {
				name = elementDetails[1];
			}

			// get parent vertex
			Set<Vertex> parents = node.getVertex().getNeighbors();
			Iterator<Vertex> itParents = parents.iterator();
			while (itParents.hasNext()) {
				Vertex v = itParents.next();
				parent = (BiologicalNodeAbstract) graphInstance
						.getPathwayElement(v);
			}

			// parameter details
			String object = "";

			int i = node.getLabel().indexOf("_");
			object = node.getLabel().substring(0, i);
			String[] param = { object, id, name,
					node.getDAWISNode().getOrganism(),
					node.getDAWISNode().getDB() };

			// load element
			try {
				loader.getDetails(param, parent);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void done() {

		stopVisualizationModel();

		// draw nodes
		loader.drawNodes(pw);

		// draw edges
		loader.drawEdges(pw);

		// mark new vertices with red border
		myGraph.markNewVertices();

		startVisualizationModel();

		node.hasDAWISNode(false);
		MainWindowSingelton.getInstance().updateDatabaseProperties();
		myGraph.removeSelection();
		myGraph.restartVisualizationModel();

		// refresh mainWindow
		MainWindow window = MainWindowSingelton.getInstance();
		updateWindow(window, bar);
		myGraph.markNewVertices();
		myGraph.normalCentering();
		myGraph.changeToKKLayout();
		bar.closeWindow();

		window.updateOptionPanel();
		window.setEnable(true);
	}

	private void updateWindow(MainWindow w, ProgressBar bar) {

		w.updateElementTree();
		w.updateSatelliteView();
		w.updateFilterView();
		// w.updateTheoryProperties();
		bar.closeWindow();
		w.setEnable(true);

	}

}
