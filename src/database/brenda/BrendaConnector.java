package database.brenda;

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import graph.CreatePathway;
import graph.algorithms.MergeGraphs;
import graph.hierarchies.EnzymeNomenclature;
import graph.hierarchies.HierarchyList;
import graph.hierarchies.HierarchyListComparator;
import graph.hierarchies.HierarchyStructure;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingleton;
import gui.ProgressBar;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import pojos.DBColumn;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Factor;
import biologicalObjects.nodes.Inhibitor;
import biologicalObjects.nodes.SmallMolecule;
import configurations.Wrapper;

//import edu.uci.ics.jung.graph.Vertex;

public class BrendaConnector extends SwingWorker<Object, Object> {

	private MyGraph myGraph;

	private boolean CoFactors = false;

	private boolean Inhibitors = false;

	private Pathway pw = null;

	private String title = "";

	private String organism = "";

	private String pathwayLink = "";

	private String pathwayImage = "";

	private String pathwayNumber = "";

	private int searchDepth = 4;

	protected BrendaTree tree = new BrendaTree();

	protected String enzyme_organism = "";

	private boolean organism_specific = false;

	private boolean disregarded = false;

	private MoleculeBox box = MoleculeBoxSingelton.getInstance();

	private String[] enzymeToSearch;

	protected Hashtable<String, BiologicalNodeAbstract> enzymes = new Hashtable<String, BiologicalNodeAbstract>();
	
	private Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();

	private Pathway mergePW = null;
	
	boolean headless;
	
	boolean autoCoarseDepth = false;

	boolean autoCoarseEnzymeNomenclature = false;
	
	public BrendaConnector(String[] details, Pathway mergePW,
			boolean headless) {
		enzymeToSearch = details;
		this.mergePW = mergePW;
		this.headless = headless;

	}

	private void startVisualizationModel() {
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

	private BiologicalNodeAbstract addReactionNodes(String node) {
		// node = node.toLowerCase();
		String clean = this.cleanString(node);
		if (!enzymes.containsKey(clean)) {
			SmallMolecule sm = new SmallMolecule(clean, "");
			sm.setReference(false);
			enzymes.put(clean, sm);
			return sm;

		} else {
			return enzymes.get(clean);
		}
	}

	private void searchPossibleEnzyms(BiologicalNodeAbstract node,
			DefaultMutableTreeNode parentNode) {
		// System.out.println("methode");
		if (parentNode.getLevel() == 0
				|| (parentNode.getLevel() / 2) < searchDepth + 1) {
			
			if (!disregarded || !box.getElementValue(node.getLabel())) {

				String queryString = node.getLabel().replaceAll("'", "''")
						.replaceAll("\"", "''");
				if (queryString.contains("?")) {
					queryString = queryString.replaceAll("\\?", "''");
				}

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
				String[] resultDetails;
				String clean;
				String[] gesplittet;
				String left[] = null;
				String right[] = null;

				String tmp;
				String[] split;
				String weight = "1";

				for (DBColumn column : results) {
					resultDetails = (String[]) column.getColumn();

					clean = this.cleanString(resultDetails[0]);
					if (!enzymes.containsKey(clean)) {
						// System.out.println("details: "+resultDetails[3]);
						resultDetails[3] = resultDetails[3].replace("\uFFFD",
								"'");
						// System.out.println(resultDetails[3]);
						gesplittet = resultDetails[3].split("=");
						if (gesplittet.length == 2) {
							left = gesplittet[0].split("\\s\\+\\s");
							right = gesplittet[1].split("\\s\\+\\s");
						} else {
							// System.out.println("No valid reaction: "+resultDetails[3]);
						}

						e = new Enzyme(clean, resultDetails[1]);
						e.setReference(false);

						enzymes.put(clean, e);

						// System.out.println("size: "+left.length);
						for (int i = 0; i < left.length; i++) {
							tmp = this.cleanString(left[i].trim());
							split = tmp.split("\\s", 2);
							if (split[0].matches("\\d+")) {
								weight = split[0];
								tmp = split[1];
							}

							if (node.getLabel().equals(tmp)) {
								this.buildEdge(node, e, true, weight);
								break;
							}

						}
						weight = "1";
						for (int i = 0; i < right.length; i++) {
							tmp = this.cleanString(right[i].trim());
							split = tmp.split("\\s", 2);
							if (split[0].matches("\\d+")) {
								weight = split[0];
								tmp = split[1];
							}

							if (node.getLabel().equals(tmp)) {
								this.buildEdge(e, node, true, weight);
								break;
							}
						}

						newNode = new DefaultMutableTreeNode(e.getLabel());

						tree.addNode(parentNode, newNode, e);

						if (resultDetails[3] != null) {
							separateReaction(resultDetails[3], e, newNode);
						}
					}
				}
			}
		}
	}

	private void buildEdge(BiologicalNodeAbstract first,
			BiologicalNodeAbstract second, boolean directed, String weight) {
		// System.out.println("e:"+ ++i);
		// System.out.println("edge");
		// String[] entry;
		// BiologicalNodeAbstract first;
		// BiologicalNodeAbstract second;
		// while (it.hasNext()) {

		// entry = it.next();
		// first = enzymes.get(this.cleanString(entry[0]));
		// second = enzymes.get(this.cleanString(entry[1]));

		if (first == null) {
			System.out.println("first is null");
		}
		if (second == null) {
			System.out.println("second is null");
		}
		// if (myGraph.getJungGraph().findEdge(first, second) == null) {
		if (first != null && second != null) {
			ReactionEdge r = new ReactionEdge(weight, "", first, second);

			r.setDirected(directed);
			r.setReference(false);
			r.setHidden(false);
			r.setVisible(true);
			this.edges.add(r);
		}
		// }
		// }

		// pw.addEdge(r);
	}

	private void drawEdges() {

		Iterator<BiologicalEdgeAbstract> it = edges.iterator();

		BiologicalEdgeAbstract bea;
		// System.out.println("edges size:"+edges.size());
		// int i = 0;
		// int vorhanden = 0;
		while (it.hasNext()) {
			// System.out.println("added");
			bea = it.next();
			if (myGraph.getJungGraph().findEdge(bea.getFrom(), bea.getTo()) == null) {

				pw.addEdge(bea);
				// i++;
			} else {
				// System.out.println("v: "+bea.getFrom().getLabel()+" n: "+bea.getTo().getLabel());
				// System.out.println("v2: "+bea.getFrom().getName()+" n: "+bea.getTo().getName());
				// vorhanden++;
			}
		}
		// System.out.println("added: "+i);
		// System.out.println("vorhanden: "+vorhanden);
		// System.out.println("edges added: "+pw.countEdges());

		/*
		 * String[] entry; BiologicalNodeAbstract first; BiologicalNodeAbstract
		 * second; while (it.hasNext()) {
		 * 
		 * entry = it.next(); first = enzymes.get(this.cleanString(entry[0]));
		 * second = enzymes.get(this.cleanString(entry[1]));
		 * 
		 * if (first == null) { System.out.println("first is null"); } if
		 * (second == null) { System.out.println("second is null"); } if
		 * (myGraph.getJungGraph().findEdge(first, second) == null) { if
		 * (entry[2].equals("True")) { buildEdge(first, second, true); } else {
		 * buildEdge(first, second, false); } } }
		 */
	}

	private void separateReaction(String reaction,
			BiologicalNodeAbstract enzyme, DefaultMutableTreeNode parentNode) {
		// System.out.println("seperate");

		int i = 0;
		StringTokenizer tok = new StringTokenizer(reaction, "=");
		int tokenCount = tok.countTokens();
		String[] result = new String[tokenCount];

		while (tok.hasMoreTokens()) {
			result[i++] = tok.nextToken();
		}
		// System.out.println("sep");
		// System.out.println(reaction);
		String[] gesplittet = result[0].split("\\s\\+\\s");
		BiologicalNodeAbstract substrate;
		DefaultMutableTreeNode newNode;
		String[] split;
		String weight = "1";
		for (int j = 0; j < gesplittet.length; j++) {

			String temp = gesplittet[j].trim();

			// if string begins with number
			split = temp.split("\\s", 2);
			if (split[0].matches("\\d+")) {
				weight = split[0];
				temp = split[1];
				// System.out.println("matchw:"+weight);
				// System.out.println("matchn:"+temp);
			}

			// if(temp.split("[\\d+]"))
			if (!parentNode.getParent().toString().equals(temp)) {

				substrate = addReactionNodes(temp);

				this.buildEdge(substrate, enzyme, true, weight);

				// buildEdge(substrate.getVertex(), enzyme.getVertex(), true);
				// !!!!!
				
				newNode = new DefaultMutableTreeNode(substrate.getLabel());
				tree.addNode(parentNode, newNode, substrate);
				searchPossibleEnzyms(substrate, newNode);
				

			} else {

				substrate = addReactionNodes(temp);

				this.buildEdge(substrate,
						this.enzymes.get(parentNode.toString()), true, weight);

			}
		}
		weight = "1";
		if (tokenCount > 1) {
			String[] gesplittet_b = result[1].split("\\s\\+\\s");
			BiologicalNodeAbstract product;
			String temp;
			for (int j = 0; j < gesplittet_b.length; j++) {
				temp = gesplittet_b[j].trim();
				split = temp.split("\\s", 2);
				if (split[0].matches("\\d+")) {
					weight = split[0];
					temp = split[1];
				}

				if (!parentNode.getParent().toString().equals(temp)) {
					product = addReactionNodes(temp);
					// buildEdge(enzyme.getVertex(), product.getVertex(), true);

					this.buildEdge(enzyme, product, true, weight);
					
					newNode = new DefaultMutableTreeNode(product.getLabel());
					tree.addNode(parentNode, newNode, product);
					searchPossibleEnzyms(product, newNode);
					

				} else {

					// System.out.println("P:"+parentNode.getParent().toString());
					this.buildEdge(
							this.enzymes.get(parentNode.toString()),
							this.enzymes.get(parentNode.getParent().toString()),
							true, weight);
				}
			}
		}
	}

	protected void processBrendaElement(String enzyme,
			DefaultMutableTreeNode node) {
		
		// System.out.println("l "+ node.getLevel());
		// System.out.println("depth: "+searchDepth);
		if (node.getLevel() == 0 || (node.getLevel() / 2) < searchDepth) {
			// System.out.println("if");
			String[] param = { enzyme };

			ArrayList<DBColumn> results = new Wrapper().requestDbContent(1,
					BRENDAQueries.getBRENDAenzymeDetails, param);

			Enzyme e;
			DefaultMutableTreeNode newNode;
			String[] resultDetails;
			String clean;
			for (DBColumn column : results) {

				resultDetails = column.getColumn();
				clean = this.cleanString(resultDetails[0]);
				// System.out.println(resultDetails[0]);
				if (!enzymes.containsKey(clean)) {
					// System.out.println(resultDetails[1]);
					// System.out.println(resultDetails[2]);
					// System.out.println("durch");
					e = new Enzyme(clean, resultDetails[1]);

					// System.out.println("dort");
					e.setReference(false);
					e.setColor(Color.RED);
					pw.setRootNode(e);

					// String[] gesplittet = resultDetails[3].split("=");
					e.hasBrendaNode(true);

					newNode = new DefaultMutableTreeNode(e.getLabel());

					tree.addNode(node, newNode, e);
					enzymes.put(clean, e);

					if (resultDetails[3] != null
							&& resultDetails[3].length() > 0) {
						// System.out.println("3: "+resultDetails[3]);

						resultDetails[3] = resultDetails[3].replace("\uFFFD",
								"'");
						separateReaction(resultDetails[3], e, newNode);
					}

				}
			}
		}
	}

	private void drawNodes() {

		// String key;
		BiologicalNodeAbstract node;
		Iterator<BiologicalNodeAbstract> i = enzymes.values().iterator();
		while (i.hasNext()) {

			node = i.next();
			// BiologicalNodeAbstract temp_node =

			// node = enzymes.get(key);
			// node.setVertex(myGraph.createNewVertex());
			pw.addVertex(node, new Point(10,10));
			// myGraph.moveVertex(node.getVertex(), column * 150, row * 100);

		}
	}
	
	/**
	 * For autocoarsing the resulting network.
	 */
	private void autoCoarseDepth() {

		/**
		 * The parent of each node is the neighbor with the shortest path to the root node.
		 * @author tobias
		 */
		class HLC implements HierarchyListComparator<Integer> {

			Map<BiologicalNodeAbstract, Number> rootDistanceMap;
			
			public HLC() {
				Pathway newPw = new Pathway("Brenda Search");
				MyGraph searchGraph = new MyGraph(newPw);
				for(BiologicalNodeAbstract nd : myGraph.getAllVertices()){
					searchGraph.addVertex(nd, myGraph.getVertexLocation(nd));
				}
				for(BiologicalEdgeAbstract e : myGraph.getAllEdges()){
					BiologicalEdgeAbstract reverseEdge = e.clone();
					reverseEdge.setFrom(e.getTo());
					reverseEdge.setTo(e.getFrom());
					searchGraph.addEdge(e);
					searchGraph.addEdge(reverseEdge);
				}
				UnweightedShortestPath<BiologicalNodeAbstract, BiologicalEdgeAbstract> path =
						new UnweightedShortestPath<BiologicalNodeAbstract,BiologicalEdgeAbstract>(searchGraph.getJungGraph());
				rootDistanceMap = path.getDistanceMap(pw.getRootNode());
			}

			public Integer getValue(BiologicalNodeAbstract n) {
				Set<BiologicalNodeAbstract> neighbors = new HashSet<BiologicalNodeAbstract>();
				neighbors.addAll(getGraph().getJungGraph().getNeighbors(n));
				BiologicalNodeAbstract bestNeighbor = n;
				int bestDistance = rootDistanceMap.get(n)==null ? Integer.MAX_VALUE : rootDistanceMap.get(n).intValue();
				for(BiologicalNodeAbstract neighbor : neighbors){
					if(rootDistanceMap.get(neighbor)!=null && (bestNeighbor==n || rootDistanceMap.get(neighbor).intValue()<=bestDistance)){
						if(neighbor instanceof Factor || neighbor instanceof Inhibitor){
							continue;
						}
						bestNeighbor = neighbor;
						bestDistance = rootDistanceMap.get(neighbor).intValue();
					}
				}
				return bestNeighbor.getID();
								
			}

			public Integer getSubValue(BiologicalNodeAbstract n) {
				return n.getID();
			}
		}

		HierarchyList<Integer> l = new HierarchyList<Integer>();
		l.addAll(myGraph.getAllVertices());
		l.sort(new HLC());
		l.coarse();
	}
	
	/**
	 * For autocoarsing the resulting network.
	 */
	private void autoCoarseEnzymeNomenclature() {

		EnzymeNomenclature struc = new EnzymeNomenclature();
		/**
		 * The parent of each node is the neighbor with the shortest path to the root node.
		 * @author tobias
		 */
		class HLC implements HierarchyListComparator<String> {

			EnzymeNomenclature struc;
			
			public HLC(EnzymeNomenclature struc) {
				this.struc = struc;
			}

			public String getValue(BiologicalNodeAbstract n) {
				return struc.ECtoClass(n.getLabel());
			}

			public String getSubValue(BiologicalNodeAbstract n) {
				return n.getLabel();
			}
		}

		HierarchyList<String> l = new HierarchyList<String>();
		for(BiologicalNodeAbstract n : myGraph.getAllVertices()){
			if(n instanceof Enzyme){
				l.add(n);
			}
		}
		l.sort(new HLC(struc),struc);
		l.coarse();
	}

	protected String adoptOrganism(String organism) {

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
		Factor f;
		Enzyme e;
		String[] resultDetails;
		String result0;
		String result1;
		for (DBColumn column : results) {
			resultDetails = column.getColumn();
			result0 = this.cleanString(resultDetails[0]);
			result1 = this.cleanString(resultDetails[1]);
			System.out.println("found Cofactor");
			if (enzymes.containsKey(result1)) {

				bna = enzymes.get(result0);
				bna2 = enzymes.get(result1);

				this.buildEdge(bna2, bna, true, "1");

			} else {
				f = new Factor(result1, result1);

				f.setReference(false);
				f.setColor(Color.cyan);

				enzymes.put(result1, f);

				e = ((Enzyme) enzymes.get(result0));

				this.buildEdge(f, e, true, "1");

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
		Inhibitor f;
		Enzyme e;
		String result0;
		String result1;
		for (DBColumn column : results) {
			resultDetails = column.getColumn();
			result0 = this.cleanString(resultDetails[0]);
			result1 = this.cleanString(resultDetails[1]);
			System.out.println("found Inhibitor");
			if (enzymes.containsKey(result1)) {
				System.out.println("drin2");
				bna = enzymes.get(result0);
				bna2 = enzymes.get(result1);

				bna2.setColor(Color.pink);

				this.buildEdge(bna2, bna, true, "1");

			} else {

				f = new Inhibitor(result1, result1);

				f.setReference(false);
				enzymes.put(result1, f);

				e = ((Enzyme) enzymes.get(result0));

				this.buildEdge(f, e, true, "1");

			}
		}
	}

	protected void getEnzymeDetails(String[] details) {

		// System.out.println("len: "+details.length);

		// System.out.println("zyme: "+details[1]);
		enzymes.clear();
		enzyme_organism = adoptOrganism(details[1]);
		// System.out.println(enzyme);
		// System.out.println(tree.getRoot());
		// System.out.println("vor");
		// System.out.println("e: " + enzyme);
		processBrendaElement(details[0], tree.getRoot());
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
		
		getPathway();

		box.getDisregardedValues();

		title = enzymeToSearch[0];

		return null;
	}

	@Override
	public void done() {
		int answer = JOptionPane.YES_OPTION;
		if (mergePW != null)
			answer = JOptionPane
					.showOptionDialog(
							MainWindowSingleton.getInstance(),
							"A new tab will be created with the pathway you selected. Shall this tab be a merge between the current pathway and the selected or contain only the selected pathway?",
							"", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null,
							new String[] { "Selected Pathway Only",
									"Merge Pathways" },
							JOptionPane.CANCEL_OPTION);
		if (answer == JOptionPane.YES_OPTION || answer == JOptionPane.NO_OPTION) {
			if (answer == JOptionPane.NO_OPTION) {
				// System.out.println("drin");
				pw = new Pathway(title);
			} else if (title != null) {
				// System.out.println("drin1");
				pw = new CreatePathway("EC: " + title).getPathway();
			} else {
				// System.out.println("drin2");
				pw = new CreatePathway("BRENDA").getPathway();
			}
			pw.setOrganism(organism);
			pw.setLink(pathwayLink);
			pw.setImagePath(pathwayImage);

			// GraphInstance i = new GraphInstance();
			// Pathway pw = new CreatePathway().getPathway();
			// System.out.println("p: "+i.getPathway());
			// System.out.println(enzymeToSearch[0]);
			getEnzymeDetails(enzymeToSearch);

			// System.out.println("ende");
			// System.out.println("enz: "+enzymeToSearch);
			String enzymeList = enzymesInPathway();

			// System.out.println(enzymeList);
			if (CoFactors) {
				MainWindow.progressbar.setProgressBarString("Getting Cofactors");
				getCofactors(enzymeList);
			}

			if (Inhibitors) {
				MainWindow.progressbar.setProgressBarString("Getting Inhibitors");
				getInhibitors(enzymeList);
			}

			MainWindow.progressbar.setProgressBarString("Drawing network");

			myGraph = pw.getGraph();

			drawNodes();
			drawEdges();

			startVisualizationModel();
			if (!headless) {
//				myGraph.changeToCircleLayout();
				myGraph.changeToGEMLayout();
				// GraphInstance.getMyGraph().getVisualizationViewer().restart();
				myGraph.normalCentering();
			}
			
			pw.saveVertexLocations();
			
			if (autoCoarseDepth) {
				autoCoarseDepth();
			}
			if (autoCoarseEnzymeNomenclature) {
				autoCoarseEnzymeNomenclature();
			}
			if (answer == JOptionPane.NO_OPTION)
				new MergeGraphs(pw, mergePW, true);
			MainWindowSingleton.getInstance().closeProgressBar();
		}
		MainWindowSingleton.getInstance().updateAllGuiElements();
	}
	
	public void setAutoCoarseDepth(boolean ac){
		autoCoarseDepth = ac;
	}
	
	public void setAutoCoarseEnzymeNomenclature(
			boolean autoCoarseEnzymeNomenclature) {
		this.autoCoarseEnzymeNomenclature = autoCoarseEnzymeNomenclature;
			}

	private String cleanString(String s) {
		return s.toLowerCase();
	}

	static public String byteToHex(byte b) {
		// Returns hex String representation of byte b
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(array);
	}

	static public String charToHex(char c) {
		// Returns hex String representation of char c
		byte hi = (byte) (c >>> 8);
		byte lo = (byte) (c & 0xff);
		return byteToHex(hi) + byteToHex(lo);
	}

}
