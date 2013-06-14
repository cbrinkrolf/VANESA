package database.dawis;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import database.dawis.gui.DAWISVertexWindow;
import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import miscalleanous.internet.FollowLink;
import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTable;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.hyperlink.LinkAction;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.Disease;
import biologicalObjects.nodes.Drug;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Factor;
import biologicalObjects.nodes.Fragment;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.GeneOntology;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.Reaction;
import biologicalObjects.nodes.Site;

@SuppressWarnings("unchecked")
public class SingleElementLoader extends SwingWorker {

	ProgressBar bar;
	Pathway pw;
	String[] par;
	BiologicalNodeAbstract bna;
	MyGraph myGraph;
	GraphInstance graphInstance;
	Object selectedObject;
	BiologicalNodeAbstract ab;
	BiologicalNodeAbstract parent;
	Object[][] values = null;
	public JPanel p;
	NodePropertyTable table;
	boolean loaded = false;
	int[] selectedRows;
	Vector<String> loadedElements = new Vector<String>();
	public int countElements, rowCount, restElements;
	public Vector<String[]> selectedElements = new Vector<String[]>();
	ElementLoader loader;
	JButton loadElement = new JButton("Load the element(s) down");
	DAWISVertexWindow win;
	CollectorNode node = null;
	DAWISNode dawisNode;

	public SingleElementLoader(DAWISVertexWindow vw, Pathway path,
			JPanel panel, NodePropertyTable t) {
		pw = path;
		p = panel;
		table = t;
		win = vw;
		myGraph = pw.getGraph();
		graphInstance = new GraphInstance();
		selectedObject = graphInstance.getSelectedObject();
		node = (CollectorNode) graphInstance.getPathwayElement(selectedObject);
		dawisNode = node.getDAWISNode();
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

	/**
	 * add chosen elements to the graph
	 * 
	 * @param bar
	 * @throws SQLException
	 */
	private void getElementDetails() throws SQLException {

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
			loader.getDetails(param, parent);
		}

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

		// get selected rows from the table
		getAnswer();

		// getElementDetails
		getElementDetails();

		return null;
	}

	private void testCollectorNodesIfReactionLoaded() {

		// get neighbors of parent
		Set<Vertex> set = parent.getVertex().getNeighbors();

		// get collectors
		Iterator<Vertex> it = set.iterator();
		while (it.hasNext()) {

			Vertex neighbor = it.next();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) pw
					.getElement(neighbor);
			if (bna instanceof CollectorNode) {

				Vector<Integer> rows = new Vector<Integer>();
				Vector<String> elementsToRemove = new Vector<String>();

				// get dawis node of collector node
				DAWISNode node = bna.getDAWISNode();

				// add loaded elements to loaded elements vector
				Iterator<BiologicalNodeAbstract> elementsIterator = loader
						.getNewNodes().iterator();
				while (elementsIterator.hasNext()) {
					BiologicalNodeAbstract actBNA = elementsIterator.next();
					if (!loadedElements.contains(actBNA.getLabel())) {
						loadedElements.add(actBNA.getLabel());
					}
				}

				// get loaded elements
				Iterator<String> loadedElementsIterator = loadedElements
						.iterator();
				while (loadedElementsIterator.hasNext()) {
					String element = loadedElementsIterator.next();

					// test for elements
					Hashtable<String, Integer> ht = node.getAllTableIDs();
					Set<String> colSet = ht.keySet();
					if (colSet.contains(element)) {

						// get position of the element in the table
						int pos = ht.get(element);
						rows.add(pos);
						elementsToRemove.add(element);

					}
				}

				// create rows array
				int rowsSize = rows.size();
				int[] rowsToRemove = new int[rowsSize];
				for (int i = 0; i < rowsSize; i++) {
					rowsToRemove[i] = rows.get(i);
				}

				// remove elements
				node.removeElementsFromTable(rowsToRemove);

				// refresh node name
				refreshCollectorNodeName(bna);

				Iterator<String> removeIterator = elementsToRemove.iterator();
				while (removeIterator.hasNext()) {
					loadedElements.remove(removeIterator.next());
				}

			}
		}
	}

	/**
	 * check the names of collectorNodes of the parent of the loaded element
	 * 
	 * @param parent
	 */
	public void refreshCollectorNodeName(BiologicalNodeAbstract bna) {

		int bnaSize = bna.getDAWISNode().getListAsVector().size();
		bna.setName(bnaSize + " Elemente");

	}

	/**
	 * find collectors
	 * 
	 * @param bna
	 * @param parent
	 */
	public void testCollector(BiologicalNodeAbstract bna, String object) {

		Vertex neighbor;
		Set<Vertex> neighbors = bna.getVertex().getNeighbors();
		Iterator<Vertex> n = neighbors.iterator();
		while (n.hasNext()) {
			neighbor = n.next();
			BiologicalNodeAbstract node = (BiologicalNodeAbstract) pw
					.getElement(neighbor);
			if (node instanceof CollectorNode) {
				if (node.getLabel().equals(object)) {
					testElements(node);
				}
			}
		}
	}

	private void testElements(BiologicalNodeAbstract testNode) {

	}

	@Override
	public void done() {

		// remove elements from collector nodes
		testCollectorNodesIfReactionLoaded();

		// rename collector node
		refreshCollectorNodeName(node);

		stopVisualizationModel();

		// draw nodes
		loader.drawNodes(pw);

		// draw edges
		loader.drawEdges(pw);

		// mark new vertices with red border
		myGraph.markNewVertices();

		startVisualizationModel();

		// refresh DAWISNodeWindow
		restElements = node.getDAWISNode().getListAsVector().size();

		if (restElements > 0) {
			p.removeAll();
			updateInformation(selectedObject);
			p.add(win.getLoadButton());

			MainWindowSingelton.getInstance().updateDatabaseProperties();
		} else {
			node.hasDAWISNode(false);
			MainWindowSingelton.getInstance().updateDatabaseProperties();
			myGraph.removeSelection();
			myGraph.restartVisualizationModel();
		}

		p.setVisible(true);
		p.repaint();
		p.validate();

		// refresh mainWindow
		MainWindow window = MainWindowSingelton.getInstance();
		updateWindow(window, bar);
		myGraph.markNewVertices();
		myGraph.changeToKKLayout();
		myGraph.normalCentering();
		
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

	@SuppressWarnings("serial")
	private void updateInformation(Object element) {

		p.removeAll();

		this.ab = (BiologicalNodeAbstract) graphInstance
				.getPathwayElement(element);

		DAWISNode node = ab.getDAWISNode();

		if (this.ab instanceof PathwayMap) {
			if (!node.getDataLoaded()) {
				PathwayMap p = (PathwayMap) this.ab;
				new GetPathwayDetails(p);
			}
		} else if (this.ab instanceof Enzyme) {
			if (!node.getDataLoaded()) {
				Enzyme e = (Enzyme) this.ab;
				new GetEnzymeDetails(e);
			}
		} else if (this.ab instanceof Protein) {
			if (!node.getDataLoaded()) {
				Protein p = (Protein) this.ab;
				new GetProteinDetails(p);
			}
		} else if (this.ab instanceof Disease) {
			if (!node.getDataLoaded()) {
				Disease d = (Disease) this.ab;
				new GetDiseaseDetails(d);
			}
		} else if (this.ab instanceof Drug) {
			if (!node.getDataLoaded()) {
				Drug d = (Drug) this.ab;
				new GetDrugDetails(d);
			}
		} else if (this.ab instanceof GeneOntology) {
			if (!node.getDataLoaded()) {
				GeneOntology go = (GeneOntology) this.ab;
				new GetGODetails(go);
			}
		} else if (this.ab instanceof Reaction) {
			if (!node.getDataLoaded()) {
				Reaction r = (Reaction) this.ab;
				new GetReactionDetails(r);
			}
		} else if (this.ab instanceof Glycan) {
			if (!node.getDataLoaded()) {
				Glycan gl = (Glycan) this.ab;
				new GetGlycanDetails(gl);
			}
		} else if (this.ab instanceof CompoundNode) {
			if (!node.getDataLoaded()) {
				CompoundNode c = (CompoundNode) this.ab;
				new GetCompoundDetails(c);
			}
		} else if (this.ab instanceof Gene) {
			if (!node.getDataLoaded()) {
				Gene g = (Gene) this.ab;
				new GetGeneDetails(g);
			}
		} else if (this.ab instanceof Factor) {
			if (!node.getDataLoaded()) {
				Factor g = (Factor) this.ab;
				new GetTransfacFactorDetails(g);
			}
		} else if (this.ab instanceof Fragment) {
			if (!node.getDataLoaded()) {
				Fragment g = (Fragment) this.ab;
				new GetTransfacFragmentDetails(g);
			}
		} else if (this.ab instanceof Site) {
			if (!node.getDataLoaded()) {
				Site g = (Site) this.ab;
				new GetTransfacSiteDetails(g);
			}
		}

		final String link = node.getLink(ab);
		LinkAction linkAction = new LinkAction("Original Database Link") {
			public void actionPerformed(ActionEvent e) {
				setVisited(true);
				FollowLink.openURL(link);
			}
		};

		JXHyperlink hyperlink = new JXHyperlink(linkAction);

		final String link2 = node.getDAWISLink(ab);
		LinkAction linkAction2 = new LinkAction("DAWIS-M.D. Link") {
			public void actionPerformed(ActionEvent e) {
				setVisited(true);
				FollowLink.openURL(link2);
			}
		};

		JXHyperlink hyperlink2 = new JXHyperlink(linkAction2);

		String[] header;

		values = node.getDAWISDetailsFor(node.getObject());

		int countColumns = values[0].length;

		if (!node.getObject().equals("Collector")) {
			if (countColumns == 1) {
				header = new String[1];
			} else {
				header = new String[2];
				header[1] = "Value";
			}
			header[0] = "Attribute";
		} else {
			if (countColumns == 1) {
				header = new String[1];
			} else {
				header = new String[2];
				header[1] = "Element-Name";
			}
			header[0] = "Element-ID";
		}

		if (loaded) {
			node.removeElementsFromTable(selectedRows);
			loaded = false;
		}

		table = new NodePropertyTable(values, header);
		MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "");

		JPanel headerPanel = new JPanel(layout);
		headerPanel.setBackground(new Color(192, 215, 227));
		headerPanel.add(hyperlink, "");
		headerPanel.add(hyperlink2, "dock east");

		MigLayout layout2 = new MigLayout("fillx", "[grow,fill]",
				"[]5[fill]5[]");

		p.setLayout(layout2);
		p.add(headerPanel, "wrap");
		p.add(table.getTable(), "");

	}

	/*
	 * get selected rows from the table store in loadedElements <label> and
	 * selectedElements <label, name>
	 */
	public void getAnswer() {

		MyTable t = table.getMyTable();

		// number of elements in the table
		this.countElements = t.getRowCount();

		// number of table rows
		rowCount = t.getRowCount();

		// number of selected rows
		selectedRows = t.getSelectedRows();

		// number of table columns
		int columnCount = t.getColumnCount();

		// create vector [label, name] for storage of selected elements
		selectedElements = new Vector<String[]>();

		// put selected elements into array [label, name]
		for (int i = 0; i < selectedRows.length; i++) {
			String det[];
			String id = t.getValueAt(selectedRows[i], 0).toString();
			String name = "";
			if (columnCount == 2) {
				det = new String[2];
				name = t.getValueAt(selectedRows[i], 1).toString();
				det[0] = id;
				det[1] = name;
			} else {
				det = new String[1];
				det[0] = id;
			}

			// store labels of loaded elements in vector loadedElements <label>
			if (!loadedElements.contains(id)) {
				loadedElements.add(id);
			}

			// store labels and names of loaded elements in selectedElements
			// <label, name>
			selectedElements.add(det);
		}

	}

}
