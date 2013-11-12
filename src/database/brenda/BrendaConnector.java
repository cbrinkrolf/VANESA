package database.brenda;

import graph.CreatePathway;
import graph.GraphInstance;
import graph.algorithms.MergeGraphs;
import graph.jung.classes.MyGraph;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.awt.Color;
import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import pojos.DBColumn;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Factor;
import biologicalObjects.nodes.Inhibitor;
import biologicalObjects.nodes.SmallMolecule;
import configurations.Wrapper;

//import edu.uci.ics.jung.graph.Vertex;

public class BrendaConnector extends SwingWorker {

	private MyGraph myGraph;

	private boolean CoFactors = false;

	private boolean Inhibitors = false;

	private Pathway pw = null;

	private String title = "";

	private String organism = "";

	private String pathwayLink = "";

	private String pathwayImage = "";

	private String pathwayNumber = "";

	private int row = 0;

	private int column = -1;

	private int searchDepth = 4;

	private BrendaTree tree = new BrendaTree();

	private String enzyme_organism = "";

	private boolean organism_specific = false;

	private boolean disregarded = false;

	private MoleculeBox box = MoleculeBoxSingelton.getInstance();

	private InternalGraphRepresentation adjazenzList;

	private ProgressBar bar;

	private String[] enzymeToSearch;

	private Hashtable<String, BiologicalNodeAbstract> enzymes = new Hashtable<String, BiologicalNodeAbstract>();

	private Vector<String[]> edges = new Vector<String[]>();

	private Pathway mergePW = null;

	public BrendaConnector(ProgressBar bar, String[] details, Pathway mergePW) {

		this.bar = bar;
		enzymeToSearch = details;
		this.mergePW = mergePW;

	}

	private void updateGraph() {
		myGraph.updateGraph();
	}

	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	private void startVisualizationModel() {
		myGraph.unlockVertices();
		myGraph.restartVisualizationModel();
	}

	private void getPathway() {

		// pathwayID="";
		title = "BRENDA Pathway";
		organism = "";
		pathwayLink = "";
		pathwayImage = "";
		pathwayNumber = "";

	}

	private BiologicalNodeAbstract addReactionNodes(String node)
			throws SQLException {
		if (!enzymes.containsKey(node)) {
			SmallMolecule sm = new SmallMolecule(node, "");
			sm.setReference(false);
			sm.setColor(Color.yellow);
			enzymes.put(node, sm);

			return sm;

		} else {
			return enzymes.get(node);
		}
	}

	private void searchPossibleEnzyms(BiologicalNodeAbstract node,
			DefaultMutableTreeNode parentNode) throws SQLException {

		if (parentNode.getLevel() == 0
				|| (parentNode.getLevel() / 2) < searchDepth + 1) {

			if (!disregarded || !box.getElementValue(node.getLabel())) {

				String queryString = node.getLabel().replaceAll("'", "''");
				ArrayList<DBColumn> results = new ArrayList<DBColumn>();

				if (organism_specific) {
					String[] param = { "%" + queryString + "%",
							"%" + enzyme_organism + "%" };

					results = new Wrapper().requestDbContent(1,
							BRENDAQueries.getPossibleEnzymeDetailsWithOrganism,
							param);
				} else {
					results = new Wrapper().requestDbContent(1,
							BRENDAQueries.getPossibleEnzymeDetails + "'%"
									+ queryString + "%';");
				}

				Enzyme e;
				DefaultMutableTreeNode newNode;
				for (DBColumn column : results) {

					String[] resultDetails = (String[]) column.getColumn();

					bar.setProgressBarString("Gathering information for Enzyme: "
							+ resultDetails[0]);

					if (!enzymes.containsKey(resultDetails[0])) {

						String[] gesplittet = resultDetails[3].split("=");
						if (gesplittet.length == 2) {
						} else {
						}

						e = new Enzyme(resultDetails[0], resultDetails[1]);
						e.setReference(false);
						e.setColor(Color.GREEN);

						enzymes.put(e.getLabel(), e);

						StringTokenizer tok = new StringTokenizer(
								resultDetails[3], "=");
						int tokenCount = tok.countTokens();
						String[] result = new String[tokenCount];
						int i = 0;
						while (tok.hasMoreTokens()) {
							result[i++] = tok.nextToken();
						}

						if (result[0].contains(node.getLabel())) {
							// buildEdge(node.getVertex(), e.getVertex(), true);
							String[] entry = new String[3];
							entry[0] = node.getLabel();
							entry[1] = e.getLabel();
							entry[2] = "True";
							edges.add(entry);
						} else {
							String[] entry = new String[3];
							entry[0] = e.getLabel();
							entry[1] = node.getLabel();
							entry[2] = "True";
							edges.add(entry);
						}

						newNode = new DefaultMutableTreeNode(
								e.getLabel());

						tree.addNode(parentNode, newNode);

						if (resultDetails[3] != null) {
							separateReaction(resultDetails[3], e, newNode);
						}
					}
				}
			}
		}
	}

	private void buildEdge(BiologicalNodeAbstract one,
			BiologicalNodeAbstract two, boolean directed) {

		ReactionEdge r = new ReactionEdge("", "", one, two);

		r.setDirected(directed);
		r.setReference(false);
		r.setHidden(false);
		r.setVisible(true);

		pw.addEdge(r);
	}

	private void drawEdges() {

		Iterator<String[]> it = edges.iterator();

		String[] entry;
		BiologicalNodeAbstract first;
		BiologicalNodeAbstract second;
		while (it.hasNext()) {

			entry = it.next();
			first = enzymes.get(entry[0]);
			second = enzymes.get(entry[1]);

			if (!adjazenzList.doesEdgeExist(first, second)) {
				if (entry[2].equals("True")) {
					buildEdge(enzymes.get(entry[0]), enzymes.get(entry[1]),
							true);
				} else {
					buildEdge(enzymes.get(entry[0]), enzymes.get(entry[1]),
							false);
				}
			}
		}
	}

	private void separateReaction(String reaction,
			BiologicalNodeAbstract enzyme, DefaultMutableTreeNode parentNode)
			throws SQLException {

		int i = 0;
		StringTokenizer tok = new StringTokenizer(reaction, "=");
		int tokenCount = tok.countTokens();
		String[] result = new String[tokenCount];

		while (tok.hasMoreTokens()) {
			result[i++] = tok.nextToken();
		}

		String[] gesplittet = result[0].split("\\s\\+\\s");
		BiologicalNodeAbstract substrate;
		DefaultMutableTreeNode newNode;
		for (int j = 0; j < gesplittet.length; j++) {
			String temp = gesplittet[j].trim();

			if (!parentNode.getParent().toString().equals(temp)) {

				substrate = addReactionNodes(temp);

				String[] entry = new String[3];
				entry[0] = substrate.getLabel();
				entry[1] = enzyme.getLabel();
				entry[2] = "True";
				edges.add(entry);

				// buildEdge(substrate.getVertex(), enzyme.getVertex(), true);
				// !!!!!

				newNode = new DefaultMutableTreeNode(
						substrate.getLabel());

				tree.addNode(parentNode, newNode);
				searchPossibleEnzyms(substrate, newNode);

			} else {

				substrate = addReactionNodes(temp);

				String[] entry = new String[3];
				entry[0] = substrate.getLabel();
				entry[1] = parentNode.toString();
				entry[2] = "True";
				edges.add(entry);

			}
		}

		if (tokenCount > 1) {
			String[] gesplittet_b = result[1].split("\\s\\+\\s");
			BiologicalNodeAbstract product;
			for (int j = 0; j < gesplittet_b.length; j++) {
				String temp = gesplittet_b[j].trim();

				if (!parentNode.getParent().toString().equals(temp)) {
					product = addReactionNodes(temp);
					// buildEdge(enzyme.getVertex(), product.getVertex(), true);

					String[] entry = new String[3];
					entry[1] = product.getLabel();
					entry[0] = enzyme.getLabel();
					entry[2] = "True";
					edges.add(entry);

					newNode = new DefaultMutableTreeNode(
							product.getLabel());

					tree.addNode(parentNode, newNode);
					searchPossibleEnzyms(product, newNode);

				} else {

					String[] entry = new String[3];
					entry[1] = parentNode.getParent().toString();
					entry[0] = parentNode.toString();
					entry[2] = "True";
					edges.add(entry);
				}
			}
		}
	}

	private void processBrendaElement(String enzyme, DefaultMutableTreeNode node)
			throws SQLException {
		// System.out.println("l "+ node.getLevel());
		// System.out.println("depth: "+searchDepth);
		if (node.getLevel() == 0 || (node.getLevel() / 2) < searchDepth) {
			// System.out.println("if");
			String[] param = { enzyme };
			bar.setProgressBarString("Gathering information for Enzyme: "
					+ enzyme);

			ArrayList<DBColumn> results = new Wrapper().requestDbContent(1,
					BRENDAQueries.getBRENDAenzymeDetails, param);

			Enzyme e;
			DefaultMutableTreeNode newNode;
			String[] resultDetails;
			for (DBColumn column : results) {

				resultDetails = column.getColumn();
				// System.out.println(resultDetails[0]);
				if (!enzymes.containsKey(resultDetails[0])) {
					// System.out.println(resultDetails[1]);
					// System.out.println(resultDetails[2]);
					// System.out.println("durch");
					e = new Enzyme(resultDetails[0], resultDetails[1]);

					// System.out.println("dort");
					e.setReference(false);
					e.setColor(Color.RED);

					//String[] gesplittet = resultDetails[3].split("=");
					e.hasBrendaNode(true);

					newNode = new DefaultMutableTreeNode(
							e.getLabel());

					tree.addNode(node, newNode);
					enzymes.put(e.getLabel(), e);

					if (resultDetails[3] != null
							&& resultDetails[3].length() > 0) {
						separateReaction(resultDetails[3], e, newNode);
					}

				}
			}
		}
	}

	private void drawNodes() {

		String key;
		BiologicalNodeAbstract node;
		
		Iterator<String> i = enzymes.keySet().iterator();
		while (i.hasNext()) {

			key = i.next();
			// BiologicalNodeAbstract temp_node =

			node = enzymes.get(key);
			// node.setVertex(myGraph.createNewVertex());
			pw.addVertex(node, new Point(column * 150, row * 100));
			setPosition();
			// myGraph.moveVertex(node.getVertex(), column * 150, row * 100);

		}
	}

	private void setPosition() {

		column++;
		if (column == 10) {
			row++;
			column = 0;
		}
	}

	private String adoptOrganism(String organism) {

		StringTokenizer tok = new StringTokenizer(organism);
		Vector<String> v = new Vector<String>();
		String temp;
		
		int count = 0;
		boolean breakLoop = false;

		while (tok.hasMoreTokens() && !breakLoop) {
			temp = tok.nextToken();

			if (temp.equalsIgnoreCase("SwissProt")
					|| temp.equalsIgnoreCase("GENBANK")
					|| temp.equalsIgnoreCase("TREMBL")
					|| temp.equalsIgnoreCase("IFO")
					|| temp.equalsIgnoreCase("EMBL")
					|| temp.equalsIgnoreCase("SRI")
					|| temp.equalsIgnoreCase("NCBI")) {

				breakLoop = true;
				count--;

			} else if (temp.contains("(")) {

				breakLoop = true;

			} else if (temp.contains("sp.")) {

				v.add(temp);
				count++;
				breakLoop = true;

			} else if (temp.contains("\\d")) {

				breakLoop = true;

			} else {
				v.add(temp);
				count++;
			}
		}
		String org = "";
		for (int i = 0; i < count; i++) {
			org = org + " " + v.elementAt(i);
		}

		return org.trim();
	}

	private void getCofactors(String enzyme) {

		ArrayList<DBColumn> results = null;

		if (organism_specific) {

			String brendaQuerie = BRENDAQueries.getSpecificCoFactor;
			String QUESTION_MARK = new String("\\?");

			brendaQuerie = brendaQuerie.replaceFirst(QUESTION_MARK, "\""
					+ enzyme_organism + "\"");
			results = new Wrapper().requestDbContent(1, brendaQuerie + enzyme);
		} else {
			results = new Wrapper().requestDbContent(1,
					BRENDAQueries.getCoFactor + enzyme);
		}

		BiologicalNodeAbstract bna;
		BiologicalNodeAbstract bna2;
		String[] entry;
		Factor f;
		Enzyme e;
		for (DBColumn column : results) {
			String[] resultDetails = column.getColumn();

			if (enzymes.containsKey(resultDetails[1])) {

				bna = enzymes.get(resultDetails[0]);
				bna2 =enzymes.get(resultDetails[1]);

				bna2.setColor(Color.cyan);

				entry = new String[3];
				entry[0] = bna2.getLabel();
				entry[1] = bna.getLabel();
				entry[2] = "True";
				edges.add(entry);

			} else {

				f = new Factor(resultDetails[1], resultDetails[1]);

				f.setReference(false);
				f.setColor(Color.cyan);

				enzymes.put(resultDetails[1], f);

				e = ((Enzyme) enzymes.get(resultDetails[0]));

				entry = new String[3];
				entry[0] = f.getLabel();
				entry[1] = e.getLabel();
				entry[2] = "True";
				edges.add(entry);

			}
		}
	}

	private void getInhibitors(String enzyme) {

		ArrayList<DBColumn> results = null;

		if (organism_specific) {

			String brendaQuerie = BRENDAQueries.getInhibitor;
			String QUESTION_MARK = new String("\\?");

			brendaQuerie = brendaQuerie.replaceFirst(QUESTION_MARK, "\""
					+ enzyme_organism + "\"");
			results = new Wrapper().requestDbContent(1, brendaQuerie + enzyme);
		} else {
			results = new Wrapper().requestDbContent(1,
					BRENDAQueries.getInhibitor + enzyme);
		}

		String[] resultDetails;
		BiologicalNodeAbstract bna;
		BiologicalNodeAbstract bna2;
		String[] entry;
		Inhibitor f;
		Enzyme e;
		for (DBColumn column : results) {
			resultDetails = column.getColumn();

			if (enzymes.containsKey(resultDetails[1])) {

				bna = enzymes.get(resultDetails[0]);
				bna2 = enzymes.get(resultDetails[1]);

				bna2.setColor(Color.pink);

				entry = new String[3];
				entry[0] = bna2.getLabel();
				entry[1] = bna.getLabel();
				entry[2] = "True";
				edges.add(entry);

			} else {

				f = new Inhibitor(resultDetails[1], resultDetails[1]);

				f.setReference(false);
				f.setColor(Color.pink);

				enzymes.put(resultDetails[1], f);

				e = ((Enzyme) enzymes.get(resultDetails[0]));

				entry = new String[3];
				entry[0] = f.getLabel();
				entry[1] = e.getLabel();
				entry[2] = "True";
				edges.add(entry);

			}
		}
	}

	private void getEnzymeDetails(String[] details) throws SQLException {

		// System.out.println("len: "+details.length);
		String enzyme = details[0];
		// System.out.println("zyme: "+details[1]);
		enzymes.clear();
		enzyme_organism = adoptOrganism(details[1]);
		// System.out.println(enzyme);
		// System.out.println(tree.getRoot());
		// System.out.println("vor");

		processBrendaElement(enzyme, tree.getRoot());
		// System.out.println("ende");

	}

	private String enzymesInPathway() {

		String result = "(";
		boolean first = true;

		Iterator<String> it = enzymes.keySet().iterator();

		String enzyme;
		Enzyme e;
		while (it.hasNext()) {

			enzyme = it.next();
			if (enzymes.get(enzyme) instanceof Enzyme) {
				e = (Enzyme) enzymes.get(enzyme);
				if (first) {
					result = result + "'" + e.getLabel() + "'";
					first = false;
				} else {
					result = result + ",'" + e.getLabel() + "'";
				}
			}
		}
		result = result + ");";

		return result;
	}

	public int getSearchDepth() {
		return searchDepth;
	}

	public void setSearchDepth(int searchDepth) {
		this.searchDepth = searchDepth;
	}

	public boolean isOrganism_specific() {
		return organism_specific;
	}

	public void setOrganism_specific(boolean organism_specific) {
		this.organism_specific = organism_specific;
	}

	public MyGraph getGraph() {
		return myGraph;
	}

	public void setDisregarded(boolean disregarded) {
		this.disregarded = disregarded;
	}

	public boolean isCoFactors() {
		return CoFactors;
	}

	public void setCoFactors(boolean coFactors) {
		CoFactors = coFactors;
	}

	public boolean isInhibitors() {
		return Inhibitors;
	}

	public void setInhibitors(boolean inhibitors) {
		Inhibitors = inhibitors;
	}

	@Override
	protected Object doInBackground() throws Exception {
		
		Runnable run = new Runnable() {
			public void run() {
				bar = new ProgressBar();
				bar.init(100,
						"   Loading Data for Enzyme " + enzymeToSearch[0], true);
				bar.setProgressBarString("Querying Database");
			}
		};
		SwingUtilities.invokeLater(run);
		
		getPathway();
		
		box.getDisregardedValues();

		title = enzymeToSearch[0];

		bar.setProgressBarString("Getting Enzymes");
		//System.out.println("123");
		// System.out.println("do");
		
		try{
			GraphInstance i = new GraphInstance();
			
			//System.out.println("p: "+i.getPathway());
		
		getEnzymeDetails(enzymeToSearch);
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("ende");
		// System.out.println("enz: "+enzymeToSearch);
		String enzymeList = enzymesInPathway();
		
		// System.out.println(enzymeList);
		if (CoFactors) {
			bar.setProgressBarString("Getting Cofactors");
			getCofactors(enzymeList);
		}

		if (Inhibitors) {
			bar.setProgressBarString("Getting Inhibitors");
			getInhibitors(enzymeList);
		}

		bar.setProgressBarString("Drawing network");
		
		return null;
	}

	@Override
	public void done() {
		int answer = JOptionPane.YES_OPTION;
		if (mergePW != null)
			answer = JOptionPane
					.showOptionDialog(
							MainWindowSingelton.getInstance(),
							"A new tab will be created with the pathway you selected. Shall this tab be a merge between the current pathway and the selected or contain only the selected pathway?",
							"", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null,
							new String[] { "Selected Pathway Only",
									"Merge Pathways" },
							JOptionPane.CANCEL_OPTION);
		if (answer == JOptionPane.YES_OPTION || answer == JOptionPane.NO_OPTION) {
			if (answer == JOptionPane.NO_OPTION)
				pw = new Pathway(title);
			else if (title != null)
				pw = new CreatePathway("EC: " + title).getPathway();
			else
				pw = new CreatePathway("BRENDA").getPathway();

			pw.setOrganism(organism);
			pw.setLink(pathwayLink);
			pw.setImagePath(pathwayImage);
			pw.setNumber(pathwayNumber);
			myGraph = pw.getGraph();
			adjazenzList = pw.getGraphRepresentation();

			stopVisualizationModel();
			drawNodes();
			drawEdges();

			startVisualizationModel();
			pw.getGraph().changeToCircleLayout();
			// GraphInstance.getMyGraph().getVisualizationViewer().restart();
			pw.getGraph().normalCentering();

			if (answer == JOptionPane.NO_OPTION)
				new MergeGraphs(pw, mergePW, true);
			bar.closeWindow();
		}
		MainWindowSingelton.getInstance().updateAllGuiElements();
		MainWindowSingelton.getInstance().enable(true);
	}

}
