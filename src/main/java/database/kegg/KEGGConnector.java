package database.kegg;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.SwingWorker;

import biologicalElements.Elementdeclerations;
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
import biologicalObjects.nodes.Metabolite;
import configurations.Wrapper;
import database.mirna.miRNAqueries;
import graph.CreatePathway;
import graph.hierarchies.HierarchyList;
import graph.hierarchies.HierarchyListComparator;
import graph.jung.classes.MyGraph;
import graph.layouts.Circle;
import gui.MainWindow;
import pojos.DBColumn;

public class KEGGConnector extends SwingWorker<Object, Object> {
	private String title;
	private final String organism;
	private final String pathwayID;
	private String pathwayLink;
	private String pathwayNumber;
	private boolean searchMicroRNAs = true;
	private final HashMap<Integer, Integer> srnaParents = new HashMap<>();

	private Pathway pw;

	public Pathway getPw() {
		return pw;
	}

	private MyGraph myGraph;

	private List<DBColumn> allOrgElements = new ArrayList<>();
	private List<DBColumn> allEcElements = new ArrayList<>();
	private List<DBColumn> allRnElements = new ArrayList<>();
	private List<DBColumn> allKoElements = new ArrayList<>();

	private List<DBColumn> allOrgRelations = new ArrayList<>();
	private List<DBColumn> allEcRelations = new ArrayList<>();
	private List<DBColumn> allRnRelations = new ArrayList<>();
	private List<DBColumn> allKoRelations = new ArrayList<>();

	private List<DBColumn> allOrgReactions = new ArrayList<>();
	private List<DBColumn> allEcReactions = new ArrayList<>();
	private List<DBColumn> allRnReactions = new ArrayList<>();
	private List<DBColumn> allKoReactions = new ArrayList<>();

	private final HashMap<BiologicalNodeAbstract, List<DBColumn>> allSpecificMicroRNAs = new HashMap<>();
	private final HashMap<String, SRNA> srnas = new HashMap<>();
	private boolean dontCreatePathway = false;
	private String pathwayOrg;

	private static class KeggNodeDescription {
		public String keggPathwayName;
		public String keggEntryId;

		public KeggNodeDescription(String keggPathwayName, String keggEntryId) {
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
			KeggNodeDescription knd = (KeggNodeDescription) o;
			return knd.keggEntryId.equals(keggEntryId) && knd.keggPathwayName.equals(keggPathwayName);
		}
	}

	private final HashMap<KeggNodeDescription, BiologicalNodeAbstract> nodeLowToHighPriorityMap = new HashMap<>();
	
	private boolean autoCoarse;

	public KEGGConnector(String[] details, boolean dontCreatePathway) {
		this(details);
		this.dontCreatePathway = dontCreatePathway;
	}

	private KEGGConnector(String[] details) {
		pathwayID = details[0];
		organism = details[2];
	}

	@Override
	protected Void doInBackground() throws Exception {
		MainWindow.getInstance().showProgressBar("Loading Data");
		//bar.setProgressBarString("Getting Pathway Information");
		getPathway(pathwayID);
		//bar.setProgressBarString("Getting Pathway Elements");

		allOrgElements = KEGGQueries.getPathwayElements(pathwayOrg + pathwayNumber);
		allEcElements = KEGGQueries.getPathwayElements("ec" + pathwayNumber);
		allRnElements = KEGGQueries.getPathwayElements("rn" + pathwayNumber);
		allKoElements = KEGGQueries.getPathwayElements("ko" + pathwayNumber);

		//bar.setProgressBarString("Getting Element Relations");

		allOrgRelations = KEGGQueries.getRelations(pathwayOrg + pathwayNumber);
		allEcRelations = KEGGQueries.getRelations("ec" + pathwayNumber);
		allRnRelations = KEGGQueries.getRelations("rn" + pathwayNumber);
		allKoRelations = KEGGQueries.getRelations("ko" + pathwayNumber);

		allOrgReactions = KEGGQueries.getAllReactions(pathwayOrg + pathwayNumber);
		allEcReactions = KEGGQueries.getAllReactions("ec" + pathwayNumber);
		allRnReactions = KEGGQueries.getAllReactions("rn" + pathwayNumber);
		allKoReactions = KEGGQueries.getAllReactions("ko" + pathwayNumber);
		if (isSearchMicroRNAs()) { // TODO: remove option in GUI if currently disabled?
			// allSpecificMicroRNAs = miRNAqueries.getMiRNAsOfPathway(pathwayID);
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
			final String QUESTION_MARK = "\\?";
			for (BiologicalNodeAbstract bna : pw.getVertices().keySet()) {
				if (bna instanceof DNA) {
					String finalQueryString = miRNAqueries.miRNA_get_TargetingMirnas.replaceFirst(QUESTION_MARK,
																						   "'" + bna.getLabel() + "'");
					List<DBColumn> dbcol = new Wrapper().requestDbContent(Wrapper.dbtype_MiRNA, finalQueryString);
					//System.out.println(finalQueryString);
					//System.out.println(dbcol.size());
					if (dbcol.size() > 0) {
						allSpecificMicroRNAs.put(bna, dbcol);
					}
				}
			}
			drawMicroRNAs();
		}
		myGraph.restartVisualizationModel();
		myGraph.normalCentering();
		pw.saveVertexLocations();
		if(isAutoCoarse()) {
			autoCoarse();
		}
		MainWindow window = MainWindow.getInstance();
		window.updateOptionPanel();
		firePropertyChange("finished", null, "finished");
	}

	private void getPathway(String name) {
		ArrayList<DBColumn> result = KEGGQueries.getPathway(pathwayID);
		for (DBColumn column : result) {
			String[] resultDetails = column.getColumn();
			title = resultDetails[1];
			pathwayLink = resultDetails[5];
			//pathwayImage = resultDetails[4];
			pathwayNumber = resultDetails[3];
			pathwayOrg = resultDetails[2];
		}
	}

	private void processKeggElements(String[] set) {
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
		BiologicalNodeAbstract bna = null;
		switch (set[2]) {
			case "gene":
				String label = set[6].split(",")[0];
				if (label != null) {
					node.setNodeLabel(label);
				}
				bna = new DNA(node.getNodeLabel(), node.getKEGGentryName());
				break;
			case "compound":
				node.setNodeLabel(set[10]);
				bna = new Metabolite(node.getNodeLabel(), node.getKEGGentryName());
				break;
			case "ortholog":
				bna = new OrthologGroup(node.getNodeLabel(), node.getKEGGentryName());
				break;
			case "map":
				node.setNodeLabel(set[11]);
				bna = new PathwayMap(node.getNodeLabel(), node.getKEGGentryName());
				break;
			case "enzyme":
				bna = new Enzyme(node.getNodeLabel(), node.getKEGGentryName());
				break;
			case "other":
			case "undefiened": // TODO: typo for a reason?
				bna = new Other(node.getNodeLabel(), node.getKEGGentryName());
				break;
			case "group":
				bna = new Complex("Complex", "");
				break;
		}
		if (bna != null) {
			bna.setKEGGnode(node);
			bna.setHasKEGGNode(true);
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
								new KeggNodeDescription(oldKeggNode.getKEGGPathway(), oldKeggNode.getKEGGentryID()), bna);
						if (nodeLowToHighPriorityMap.containsValue(old_bna)) {
							KeggNodeDescription deleteKey = null;
							for (Entry<KeggNodeDescription, BiologicalNodeAbstract> entry : nodeLowToHighPriorityMap
									.entrySet()) {
								if (entry.getValue().equals(old_bna))
									deleteKey = entry.getKey();
							}
							nodeLowToHighPriorityMap.remove(deleteKey);
							nodeLowToHighPriorityMap.put(deleteKey, bna);
						}
					} else {
						addBNA = false;
						nodeLowToHighPriorityMap.put(new KeggNodeDescription(bna.getKEGGnode().getKEGGPathway(),
																			 bna.getKEGGnode().getKEGGentryID()), old_bna);
					}
					break;
				}
			}
			if (addBNA) {
				bna = pw.addVertex(bna, new Point2D.Double(bna.getKEGGnode().getXPos(), bna.getKEGGnode().getYPos()));
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
		else if (bna instanceof Metabolite)
			return 0;
		else if (bna instanceof OrthologGroup)
			return -2;
		else if (bna instanceof Complex)
			return -1;
		else if (bna instanceof Other)
			return -3;
		else
			return 0;
	}

	private void drawNodes(List<DBColumn> allElements) {
		for (DBColumn column : allElements) {
			processKeggElements(column.getColumn());
		}
	}

	private void drawMicroRNAs() {
		for (BiologicalNodeAbstract bna : allSpecificMicroRNAs.keySet()) {
			for (DBColumn dbColumn : allSpecificMicroRNAs.get(bna)) {
				String[] column = dbColumn.getColumn();
				SRNA srna;
				if (srnas.containsKey(column[0])) {
					 srna = srnas.get(column[0]);
				} else {
					srna = new SRNA(column[0], column[0]);
					//p = new Point2D.Double(myGraph.getVertexLocation(bna).getX(), myGraph.getVertexLocation(bna).getY());
					//.findNearestFreeVertexPosition(bna.getKEGGnode()
					//.getXPos(), bna.getKEGGnode().getYPos(), 100);
					Point2D p = Circle.getPointOnCircle(myGraph.getVertexLocation(bna), 20, 2.0*((double) (Math.random()%(Math.PI))));
					pw.addVertex(srna, p);
					srnas.put(column[0], srna);
					srnaParents.put(srna.getID(), bna.getID());
				}
				Expression e = new Expression("", "", srna, bna);
				e.setDirected(true);
				pw.addEdge(e);
			}
		}
	}
	
	private void autoCoarse() {
		class HLC implements HierarchyListComparator<Integer> {
			public Integer getValue(BiologicalNodeAbstract n) {
				if (n instanceof SRNA){
					if (srnaParents.containsKey(n.getID())) {
						return srnaParents.get(n.getID());
					} else {
						return getSubValue(n);
					}
				}
				return getSubValue(n);
			}

			public Integer getSubValue(BiologicalNodeAbstract n) {
				return n.getID();
			}
		}
		HierarchyList<Integer> l = new HierarchyList<>();
		l.addAll(myGraph.getAllVertices());
		l.sort(new HLC());
		l.coarse();
	}

	private void drawRelations(List<DBColumn> allGeneralRelations, boolean specific) {
		for (DBColumn column : allGeneralRelations) {
			String entry1 = column.getColumn()[3];
			String entry2 = column.getColumn()[4];
			String subtypeValue = column.getColumn()[2];
			// String relationType = column.getColumn()[5];
			String edgeType = column.getColumn()[1];
			String keggPathway = column.getColumn()[0];
			BiologicalNodeAbstract bna1 = null;
			BiologicalNodeAbstract subtype = null;
			BiologicalNodeAbstract bna2 = null;
			for (BiologicalNodeAbstract bna : pw.getVertices().keySet()) {
				if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(entry1) &&
					bna.getKEGGnode().getKEGGPathway().equals(keggPathway))
					bna1 = bna;
				if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(entry2) &&
					bna.getKEGGnode().getKEGGPathway().equals(keggPathway))
					bna2 = bna;
				if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(subtypeValue) &&
					bna.getKEGGnode().getKEGGPathway().equals(keggPathway))
					subtype = bna;
			}

			if (!pw.containsVertex(bna1))
				bna1 = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, entry1));
			if (!pw.containsVertex(bna2))
				bna2 = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, entry2));
			if (!pw.containsVertex(subtype))
				subtype = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, subtypeValue));

			if (bna1 != null && bna2 != null) {
				// Vertex vertex1 = bna1.getVertex();
				// Vertex vertex2 = bna2.getVertex();
				if (subtype != null) {
					//System.out.println("subtype not null:"+subtypeValue+" bna:"+subtype.getName()+" "+subtype.getLabel());
					// Vertex subVertex = subtype.getVertex();
					if (!pw.existEdge(bna1, subtype) && (!pw.existEdge(subtype, bna1))) {
						Compound c = new Compound("", "", bna1, subtype);
						c.setDirected(true);
						if (specific)
							bna1.setColor(Color.GREEN);
						//pw.addEdgeToView(c, true);
						pw.addEdge(c);
						pw.updateMyGraph();
					}

					if (!pw.existEdge(subtype, bna2) && (!pw.existEdge(bna2, subtype))) {
						Compound c2 = new Compound("", "", subtype, bna2);
						c2.setDirected(true);
						if (specific)
							bna2.setColor(Color.GREEN);
						pw.addEdge(c2);
						pw.updateMyGraph();
						//pw.addEdgeToView(c2, true);
					}
				} else
				// if (bna1.getBiologicalElement().equals(Elementdeclerations.dna)
				// && bna2.getBiologicalElement().equals(biologicalElements.Elementdeclerations.dna))
				{
					if (!pw.existEdge(bna1, bna2) && (!pw.existEdge(bna2, bna1))) {
						Compound c = new Compound("", "", bna1, bna2);
						c.setDirected(true);
						c.setBiologicalElement(edgeType);
						switch (edgeType) {
							case Elementdeclerations.phosphorylationEdge:
								c.setLabel("+p");
								break;
							case Elementdeclerations.dephosphorylationEdge:
								c.setLabel("-p");
								break;
							case Elementdeclerations.ubiquitinationEdge:
								c.setLabel("+u");
								break;
							case Elementdeclerations.glycosylationEdge:
								c.setLabel("+g");
								break;
							case Elementdeclerations.methylationEdge:
								c.setLabel("+m");
								break;
							case Elementdeclerations.expressionEdge:
								c.setLabel("e");
								break;
						}
						if (specific)
							bna1.setColor(Color.GREEN);
						pw.addEdge(c);
						pw.updateMyGraph();
						System.out.println("type: "+edgeType);
					}
				}
			}
		}
	}

	private void drawReactions(List<DBColumn> allReactions, boolean specific) {
		for (DBColumn column : allReactions) {
			String substrateId = column.getColumn()[0];
			String enzymeId = column.getColumn()[1];
			String productId = column.getColumn()[2];
			boolean reversible = column.getColumn()[3].equals("reversible");
			String keggPathway = column.getColumn()[4];
			BiologicalNodeAbstract substrate = null;
			BiologicalNodeAbstract enzyme = null;
			BiologicalNodeAbstract product = null;
			for (BiologicalNodeAbstract bna : pw.getVertices().keySet()) {
				if (bna.getKEGGnode().getKEGGPathway().equals(keggPathway)) {
					if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(substrateId))
						substrate = bna;
					if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(productId))
						product = bna;
					if (bna.getKEGGnode() != null && bna.getKEGGnode().getKEGGentryID().equals(enzymeId))
						enzyme = bna;
				}
			}
			if (!pw.containsVertex(substrate))
				substrate = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, substrateId));
			if (!pw.containsVertex(product))
				product = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, productId));
			if (!pw.containsVertex(enzyme))
				enzyme = nodeLowToHighPriorityMap.get(new KeggNodeDescription(keggPathway, enzymeId));

			if (substrate != null && product != null && enzyme != null) {
				// Vertex substrateVertex = substrate.getVertex();
				// Vertex productVertex = product.getVertex();
				// Vertex enzymeVertex = enzyme.getVertex();
				if (!pw.existEdge(substrate, enzyme) && !pw.existEdge(enzyme, substrate)) {
					Compound c;
					if (reversible) {
						c = new Compound("", "", enzyme, substrate);
					} else {
						c = new Compound("", "", substrate, enzyme);
					}
					pw.addEdge(c);
					c.setDirected(true);
					pw.updateMyGraph();
				}
				if (specific) {
					// enzyme.setColor(Color.GREEN);
				}
				if (!pw.existEdge(enzyme, product) && !pw.existEdge(product, enzyme)) {
					Compound c2 = new Compound("", "", enzyme, product);
					pw.addEdge(c2);
					c2.setDirected(true);
					pw.updateMyGraph();
				}
			}
		}
	}

	public void setSearchMicroRNAs(boolean searchMicroRNAs) {
		this.searchMicroRNAs = searchMicroRNAs;
	}
	
	public void setAutoCoarse(boolean autoCoarse){
		this.autoCoarse = autoCoarse;
	}
	
	public boolean isAutoCoarse(){
		return autoCoarse;
	}

	public boolean isSearchMicroRNAs() {
		return searchMicroRNAs;
	}
}