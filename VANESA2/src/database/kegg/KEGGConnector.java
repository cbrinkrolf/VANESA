package database.kegg;

import graph.CreatePathway;
import graph.hierarchies.HierarchyList;
import graph.hierarchies.HierarchyListComparator;
import graph.jung.classes.MyGraph;
import graph.layouts.Circle;
import gui.MainWindow;
import gui.MainWindowSingleton;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.SwingWorker;

import pojos.DBColumn;
import biologicalElements.Pathway;
import biologicalObjects.edges.Compound;
import biologicalObjects.edges.Expression;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Complex;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.KEGGNode;
import biologicalObjects.nodes.OrthologGroup;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.SRNA;
import biologicalObjects.nodes.SmallMolecule;
import configurations.Wrapper;
import database.mirna.miRNAqueries;
//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.utils.Pair;

public class KEGGConnector extends SwingWorker<Object, Object> {

	private String title;
	private String organism;
	private String pathwayID;
	private String pathwayLink;
	private String pathwayImage;
	private String pathwayNumber;
	private String mirnaName = null;
	private boolean searchMicroRNAs = true;
	private HashMap<Integer, Integer> srnaParents = new HashMap<Integer,Integer>();

	public String getMirnaName() {
		return mirnaName;
	}

	public void setMirnaName(String mirnaName) {
		this.mirnaName = mirnaName;
	}

	private Vector<String> mirnas;

	public Vector<String> getMirnas() {
		return mirnas;
	}

	public void setMirnas(Vector<String> mirnas) {
		this.mirnas = mirnas;
	}

	private Pathway pw;

	public Pathway getPw() {
		return pw;
	}

	private MyGraph myGraph;

	private ArrayList<DBColumn> allOrgElements = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allEcElements = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allRnElements = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allKoElements = new ArrayList<DBColumn>();

	private ArrayList<DBColumn> allOrgRelations = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allEcRelations = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allRnRelations = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allKoRelations = new ArrayList<DBColumn>();

	private ArrayList<DBColumn> allOrgReactions = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allEcReactions = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allRnReactions = new ArrayList<DBColumn>();
	private ArrayList<DBColumn> allKoReactions = new ArrayList<DBColumn>();

	private HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>> allSpecificMicroRNAs = new HashMap<BiologicalNodeAbstract, ArrayList<DBColumn>>();
	private HashMap<String, SRNA>srnas = new HashMap<String, SRNA>();
	private boolean dontCreatePathway = false;
	private String pathwayOrg;

	private class KeggNodeDescribtion {
		public String keggPathwayName;
		public String keggEntryId;

		public KeggNodeDescribtion(String keggPathwayName, String keggEntryId) {
			this.keggEntryId = keggEntryId;
			this.keggPathwayName = keggPathwayName;
		}

		public int hashCode() {
			return keggEntryId.hashCode() ^ keggPathwayName.hashCode();
		}

		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			KeggNodeDescribtion knd = (KeggNodeDescribtion) o;
			return knd.keggEntryId.equals(keggEntryId)
					&& knd.keggPathwayName.equals(keggPathwayName);
		}

	}

	private HashMap<KeggNodeDescribtion, BiologicalNodeAbstract> nodeLowToHighPriorityMap = new HashMap<KeggNodeDescribtion, BiologicalNodeAbstract>();
	
	private boolean autocoarse;

	public KEGGConnector(String[] details,
			boolean dontCreatePathway) {

		this(details);
		this.dontCreatePathway = dontCreatePathway;
	}

	private KEGGConnector(String[] details) {
		pathwayID = details[0];
		organism = details[2];
	}

	@Override
	protected Void doInBackground() throws Exception {

		MainWindowSingleton.getInstance().showProgressBar("Loading Data");
		
//		bar.setProgressBarString("Getting Pathway Information");
		getPathway(pathwayID);

//		bar.setProgressBarString("Getting Pathway Elements");

		allOrgElements = KEGGQueries.getPathwayElements(pathwayOrg
				+ pathwayNumber);
		allEcElements = KEGGQueries.getPathwayElements("ec" + pathwayNumber);
		allRnElements = KEGGQueries.getPathwayElements("rn" + pathwayNumber);
		allKoElements = KEGGQueries.getPathwayElements("ko" + pathwayNumber);

//		bar.setProgressBarString("Getting Element Relations");

		allOrgRelations = KEGGQueries.getRelations(pathwayOrg + pathwayNumber);
		allEcRelations = KEGGQueries.getRelations("ec" + pathwayNumber);
		allRnRelations = KEGGQueries.getRelations("rn" + pathwayNumber);
		allKoRelations = KEGGQueries.getRelations("ko" + pathwayNumber);

		allOrgReactions = KEGGQueries.getAllReactions(pathwayOrg
				+ pathwayNumber);
		allEcReactions = KEGGQueries.getAllReactions("ec" + pathwayNumber);
		allRnReactions = KEGGQueries.getAllReactions("rn" + pathwayNumber);
		allKoReactions = KEGGQueries.getAllReactions("ko" + pathwayNumber);

		if (isSearchMicroRNAs()) {
			// allSpecificMicroRNAs =
			// miRNAqueries.getMiRNAsOfPathway(pathwayID);
		}

		return null;
	}

	@Override
	public void done() {

		if (dontCreatePathway)
			pw = new Pathway(title);
		else if (title != null)
			pw = new CreatePathway(title).getPathway();
		else
			pw = new CreatePathway().getPathway();

		pw.setOrganism(organism);
		pw.setLink(pathwayLink);
		pw.setImagePath(pathwayImage);

		myGraph = pw.getGraph();

		drawNodes(allOrgElements);
		drawNodes(allEcElements);
		drawNodes(allRnElements);
		drawNodes(allKoElements);

		drawReactions(allOrgReactions, false);
		drawReactions(allEcReactions, false);
		drawReactions(allRnReactions, false);
		drawReactions(allKoReactions, false);

		drawRelations(allOrgRelations, false);
		drawRelations(allEcRelations, false);
		drawRelations(allRnRelations, false);
		drawRelations(allKoRelations, false);

		// if (colorMirnas) {
		// colorMirnas(this.mirnas, this.mirnaName);
		// }
		if (isSearchMicroRNAs()) {

			Iterator<BiologicalNodeAbstract> it = pw.getVertices().keySet().iterator();
			BiologicalNodeAbstract bna;
			ArrayList<DBColumn> dbcol;
			String finalQueryString;
			final String QUESTION_MARK = new String("\\?");
			while (it.hasNext()) {
				bna = it.next();

				if (bna instanceof DNA) {

					finalQueryString = miRNAqueries.miRNA_get_Mirnas
							.replaceFirst(QUESTION_MARK, "'" + bna.getLabel()
									+ "'");

					dbcol = new Wrapper().requestDbContent(
							Wrapper.dbtype_MiRNA, finalQueryString);
					//System.out.println(finalQueryString);
					//System.out.println(dbcol.size());
					if (dbcol.size() > 0) {
						this.allSpecificMicroRNAs.put(bna, dbcol);
					}
				}
			}
			drawMicroRNAs();

		}

		myGraph.restartVisualizationModel();
		myGraph.normalCentering();
		
		pw.saveVertexLocations();

		if(isAutoCoarse()){
			autoCoarse();
		}
		
		MainWindow window = MainWindowSingleton.getInstance();
		window.updateOptionPanel();

		firePropertyChange("finished", null, "finished");
	}

	private void getPathway(String name) {
		ArrayList<DBColumn> result = KEGGQueries.getPathway(pathwayID);

		for (DBColumn column : result) {
			String[] resultDetails = column.getColumn();
			title = resultDetails[1];
			pathwayLink = resultDetails[5];
			pathwayImage = resultDetails[4];
			pathwayNumber = resultDetails[3];
			pathwayOrg = resultDetails[2];
			// System.out.println("inside");
		}

	}

	private void processKeggElements(String[] set) {

		BiologicalNodeAbstract bna = null;

		// boolean check = true;
		boolean validElement = false;

		KEGGNode node = new KEGGNode();
		node.setKEGGPathway(set[14]);
		node.setKEGGentryID(set[0]);
		node.setKEGGentryName(set[3]);
		node.setKEGGentryType(set[2]);
		node.setKEGGentryLink(set[1]);
		node.setNodeLabel(set[3]);
		double xPos;
		double yPos;
		try {
			xPos = Double.parseDouble(set[8]);
			yPos = Double.parseDouble(set[9]);
		} catch (NumberFormatException e) {
			xPos = 0;
			yPos = 0;
		}

		node.setXPos(xPos);
		node.setYPos(yPos);

		node.setShape(set[7]);
		node.setForegroundColour(set[5]);
		node.setBackgroundColour(set[4]);

		if (set[2].equals("gene")) {
			String label = set[6].split(",")[0];
			if (label != null)
				node.setNodeLabel(label);
			validElement = true;
			bna = new DNA(node.getNodeLabel(), node.getKEGGentryName());

		} else if (set[2].equals("compound")) {
			node.setNodeLabel(set[10]);
			validElement = true;
			SmallMolecule sm = new SmallMolecule(node.getNodeLabel(),
					node.getKEGGentryName());

			bna = sm;

		} else if (set[2].equals("ortholog")) {

			validElement = true;
			OrthologGroup g = new OrthologGroup(node.getNodeLabel(),
					node.getKEGGentryName());
			bna = g;

		} else if (set[2].equals("map")) {
			node.setNodeLabel(set[11]);
			validElement = true;
			PathwayMap map = new PathwayMap(node.getNodeLabel(),
					node.getKEGGentryName());
			bna = map;
		} else if (set[2].equals("undefiened")) {

			validElement = true;
			Other other = new Other(node.getNodeLabel(),
					node.getKEGGentryName());

			bna = other;

		} else if (set[2].equals("enzyme")) {
			validElement = true;
			Enzyme e = new Enzyme(node.getNodeLabel(), node.getKEGGentryName());
			bna = e;

		} else if (set[2].equals("other")) {

			validElement = true;
			Other other = new Other(node.getNodeLabel(),
					node.getKEGGentryName());

			bna = other;

		} else if (set[2].equals("group")) {

			validElement = true;
			Complex c = new Complex("Complex", "");

			bna = c;

		}

		if (validElement) {
			bna.setKEGGnode(node);
			bna.hasKEGGNode(true);
			boolean addBNA = true;
			Iterator<BiologicalNodeAbstract> it = pw.getVertices().keySet().iterator();
			BiologicalNodeAbstract old_bna;
			while (it.hasNext()) {
				old_bna = it.next();
				KEGGNode oldKeggNode = old_bna.getKEGGnode();
				if (oldKeggNode.getXPos() == node.getXPos()
						&& oldKeggNode.getYPos() == node.getYPos()) {
					if (keggVisualizationPriority(bna) > keggVisualizationPriority(old_bna)) {
						pw.removeElement(old_bna);
						nodeLowToHighPriorityMap.put(
								new KeggNodeDescribtion(oldKeggNode
										.getKEGGPathway(), oldKeggNode
										.getKEGGentryID()), bna);
						if (nodeLowToHighPriorityMap.containsValue(old_bna)) {
							KeggNodeDescribtion deleteKey = null;
							for (Entry<KeggNodeDescribtion, BiologicalNodeAbstract> entry : nodeLowToHighPriorityMap
									.entrySet()) {
								if (entry.getValue().equals(old_bna))
									deleteKey = entry.getKey();
							}
							nodeLowToHighPriorityMap.remove(deleteKey);
							nodeLowToHighPriorityMap.put(deleteKey, bna);
						}
					} else {
						addBNA = false;
						nodeLowToHighPriorityMap.put(new KeggNodeDescribtion(
								bna.getKEGGnode().getKEGGPathway(), bna
										.getKEGGnode().getKEGGentryID()),
								old_bna);
					}
					break;
				}
			}

			if (addBNA) {
				bna = (BiologicalNodeAbstract) pw.addVertex(bna,
						new Point2D.Double(bna.getKEGGnode().getXPos(), bna
								.getKEGGnode().getYPos()));
			}
			// myGraph.moveVertex(bna.getVertex(), bna.getKEGGnode().getXPos(),
			// bna.getKEGGnode().getYPos());
			// if (!addBNA)
			// pw.removeElement(bna);

		}
	}

	private int keggVisualizationPriority(BiologicalNodeAbstract bna) {
		if (bna instanceof Enzyme)
			return 3;
		else if (bna instanceof DNA)
			return 2;
		else if (bna instanceof PathwayMap)
			return 1;
		else if (bna instanceof SmallMolecule)
			return 0;
		else if (bna instanceof Complex)
			return -1;
		else if (bna instanceof OrthologGroup)
			return -2;
		else if (bna instanceof Other)
			return -3;
		else
			return 0;
	}

	private void drawNodes(ArrayList<DBColumn> allElements) {
		for (DBColumn column : allElements) {
			String[] resultDetails = column.getColumn();

			processKeggElements(resultDetails);
		}

	}

	private void drawMicroRNAs() {
		Iterator<BiologicalNodeAbstract> it = this.allSpecificMicroRNAs
				.keySet().iterator();

		BiologicalNodeAbstract bna;

		Iterator<DBColumn> itCol;
		String[] column;
		SRNA srna;
		Point2D p;
		Expression e;
		while (it.hasNext()) {
			bna = it.next();

			itCol = this.allSpecificMicroRNAs.get(bna).iterator();

			while (itCol.hasNext()) {
				column = itCol.next().getColumn();
				
				if(srnas.containsKey(column[0])){
					srna = srnas.get(column[0]);
				}else{
				srna = new SRNA(column[0], column[0]);
//				p = new Point2D.Double(myGraph.getVertexLocation(bna).getX(), myGraph.getVertexLocation(bna).getY());
				//.findNearestFreeVertexPosition(bna.getKEGGnode()
						//.getXPos(), bna.getKEGGnode().getYPos(), 100);
				p = Circle.getPointOnCircle(myGraph.getVertexLocation(bna), 20, 2.0*((double) (Math.random()%(Math.PI))));
				pw.addVertex(srna, p);
				srnas.put(column[0], srna);
				srnaParents.put(srna.getID(), bna.getID());
				}
				e = new Expression("", "", srna, bna);
				e.setDirected(true);
				pw.addEdge(e);

			}

		}

	}
	
	private void autoCoarse(){
		class HLC implements HierarchyListComparator<Integer> {

			public HLC() {
			}

			public Integer getValue(BiologicalNodeAbstract n) {
				if(n instanceof SRNA){
					if(!srnaParents.containsKey(n.getID())){
						return getSubValue(n);
					} else {
						return srnaParents.get(n.getID());
					}
				}
				return getSubValue(n);
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

	private void drawRelations(ArrayList<DBColumn> allGeneralRelations,
			boolean specific) {

		boolean first = true;
		// System.out.println("drin"+allGeneralRelations.size());

		for (DBColumn column : allGeneralRelations) {
			if (first) {
				for (int i = 0; i < column.getLenght(); i++) {
					// System.out.println(column.getColumn()[i]);
				}

				first = false;
			}

			String entry1 = column.getColumn()[3];
			String entry2 = column.getColumn()[4];
			String subtypeValue = column.getColumn()[2];
			// String relationType = column.getColumn()[5];
			String edgeType = column.getColumn()[1];
			String keggPathway = column.getColumn()[0];
			BiologicalNodeAbstract bna1 = null;
			BiologicalNodeAbstract subtype = null;
			BiologicalNodeAbstract bna2 = null;
			BiologicalNodeAbstract bna;
			for (Iterator<BiologicalNodeAbstract> it = pw.getVertices().keySet()
					.iterator(); it.hasNext();) {
				bna = it.next();
				if (bna.getKEGGnode() != null
						&& bna.getKEGGnode().getKEGGentryID().equals(entry1)
						&& bna.getKEGGnode().getKEGGPathway()
								.equals(keggPathway))
					bna1 = bna;
				if (bna.getKEGGnode() != null
						&& bna.getKEGGnode().getKEGGentryID().equals(entry2)
						&& bna.getKEGGnode().getKEGGPathway()
								.equals(keggPathway))
					bna2 = bna;
				if (bna.getKEGGnode() != null
						&& bna.getKEGGnode().getKEGGentryID()
								.equals(subtypeValue)
						&& bna.getKEGGnode().getKEGGPathway()
								.equals(keggPathway))
					subtype = bna;
			}

			if (!pw.containsVertex(bna1))
				bna1 = nodeLowToHighPriorityMap.get(new KeggNodeDescribtion(
						keggPathway, entry1));
			if (!pw.containsVertex(bna2))
				bna2 = nodeLowToHighPriorityMap.get(new KeggNodeDescribtion(
						keggPathway, entry2));
			if (!pw.containsVertex(subtype))
				subtype = nodeLowToHighPriorityMap.get(new KeggNodeDescribtion(
						keggPathway, subtypeValue));

			if (bna1 != null && bna2 != null) {
				// Vertex vertex1 = bna1.getVertex();
				// Vertex vertex2 = bna2.getVertex();

				if (subtype != null) {
					// Vertex subVertex = subtype.getVertex();
					if (!pw.existEdge(bna1, subtype)
							&& (!pw.existEdge(subtype, bna1))) {
						Compound c = new Compound("", "", bna1, subtype);
						c.setDirected(true);
						if (specific)
							bna1.setColor(Color.GREEN);
						pw.addEdge(c);
					}

					if (!pw.existEdge(subtype, bna2)
							&& (!pw.existEdge(bna2, subtype))) {
						Compound c2 = new Compound("", "", subtype, bna2);
						c2.setDirected(true);
						if (specific)
							bna2.setColor(Color.GREEN);
						pw.addEdge(c2);
					}

				} else
				// if (bna1.getBiologicalElement().equals(
				// Elementdeclerations.dna)
				// && bna2.getBiologicalElement().equals(
				// biologicalElements.Elementdeclerations.dna))
				{
					if (!pw.existEdge(bna1, bna2)
							&& (!pw.existEdge(bna2, bna1))) {
						Compound c = new Compound("", "", bna1, bna2);
						c.setDirected(true);
						c.setBiologicalElement(edgeType);
						if (edgeType
								.equals(biologicalElements.Elementdeclerations.phosphorylationEdge))
							c.setLabel("+p");
						else if (edgeType
								.equals(biologicalElements.Elementdeclerations.dephosphorylationEdge))
							c.setLabel("-p");
						else if (edgeType
								.equals(biologicalElements.Elementdeclerations.ubiquitinationEdge))
							c.setLabel("+u");
						else if (edgeType
								.equals(biologicalElements.Elementdeclerations.glycosylationEdge))
							c.setLabel("+g");
						else if (edgeType
								.equals(biologicalElements.Elementdeclerations.methylationEdge))
							c.setLabel("+m");
						else if (edgeType
								.equals(biologicalElements.Elementdeclerations.expressionEdge))
							c.setLabel("e");
						if (specific)
							bna1.setColor(Color.GREEN);
						pw.addEdge(c);
						// System.out.println("type: "+edgeType);
					}
				}
			}
		}
	}

	private void drawReactions(ArrayList<DBColumn> allReactions,
			boolean specific) {
		boolean first = true;
		// System.out.println("drin"+allReactions.size());
		for (DBColumn column : allReactions) {

			if (first) {
				for (int i = 0; i < column.getLenght(); i++) {
					// System.out.println(column.getColumn()[i]);
				}

				first = false;
			}

			String substrateId = column.getColumn()[0];
			String enzymeId = column.getColumn()[1];
			String productId = column.getColumn()[2];
			boolean reversible = column.getColumn()[3].equals("reversible");
			String keggPathway = column.getColumn()[4];
			BiologicalNodeAbstract substrate = null;
			BiologicalNodeAbstract enzyme = null;
			BiologicalNodeAbstract product = null;
			BiologicalNodeAbstract bna;
			for (Iterator<BiologicalNodeAbstract> it = pw.getVertices().keySet()
					.iterator(); it.hasNext();) {
				bna = it.next();
				if (bna.getKEGGnode().getKEGGPathway().equals(keggPathway)) {
					if (bna.getKEGGnode() != null
							&& bna.getKEGGnode().getKEGGentryID()
									.equals(substrateId))
						substrate = bna;
					if (bna.getKEGGnode() != null
							&& bna.getKEGGnode().getKEGGentryID()
									.equals(productId))
						product = bna;
					if (bna.getKEGGnode() != null
							&& bna.getKEGGnode().getKEGGentryID()
									.equals(enzymeId))
						enzyme = bna;
				}
			}
			if (!pw.containsVertex(substrate))
				substrate = nodeLowToHighPriorityMap
						.get(new KeggNodeDescribtion(keggPathway, substrateId));
			if (!pw.containsVertex(product))
				product = nodeLowToHighPriorityMap.get(new KeggNodeDescribtion(
						keggPathway, productId));
			if (!pw.containsVertex(enzyme))
				enzyme = nodeLowToHighPriorityMap.get(new KeggNodeDescribtion(
						keggPathway, enzymeId));

			if (substrate != null && product != null && enzyme != null) {
				// Vertex substrateVertex = substrate.getVertex();
				// Vertex productVertex = product.getVertex();
				// Vertex enzymeVertex = enzyme.getVertex();
				if (!pw.existEdge(substrate, enzyme)
						&& !pw.existEdge(enzyme, substrate)) {
					Compound c = null;
					if (reversible) {
						c = new Compound("", "", enzyme, substrate);
					} else {
						c = new Compound("", "", substrate, enzyme);
					}
					pw.addEdge(c);
					c.setDirected(true);
				}
				if (specific) {
					// enzyme.setColor(Color.GREEN);
				}
				if (!pw.existEdge(enzyme, product)
						&& !pw.existEdge(product, enzyme)) {
					Compound c2 = new Compound("", "", enzyme, product);
					pw.addEdge(c2);
					c2.setDirected(true);
				}

			}
		}
	}

	public void setSearchMicroRNAs(boolean searchMicroRNAs) {
		this.searchMicroRNAs = searchMicroRNAs;
	}
	
	public void setAutoCoarse(boolean autoCoarse){
		this.autocoarse = autoCoarse;
	}
	
	public boolean isAutoCoarse(){
		return autocoarse;
	}

	public boolean isSearchMicroRNAs() {
		return searchMicroRNAs;
	}

}