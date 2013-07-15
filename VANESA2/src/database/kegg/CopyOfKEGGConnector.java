package database.kegg;

import graph.Box;
import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import graph.layouts.GraphDimension;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import miscalleanous.linkedList.LinkedList;
import miscalleanous.linkedList.Node;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Compound;
import biologicalObjects.edges.KEGGEdge;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.Complex;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.KEGGNode;
import biologicalObjects.nodes.OrthologGroup;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.SmallMolecule;
import configurations.Wrapper;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;

public class CopyOfKEGGConnector extends SwingWorker {

	private Wrapper wrapper = new Wrapper();

	private MyGraph myGraph;

	private Pathway pw = null;

	private HashMap entrySet = new HashMap();

	private HashMap groupSet = new HashMap();

	private HashMap relationSetNodes = new HashMap();

	private HashSet relationIDs = new HashSet();

	private LinkedList ll = new LinkedList();

	private String pathwayID = null;

	private String title = null;

	private String organism = null;

	private String pathwayLink = null;

	private String pathwayImage = null;

	private String pathwayNumber = "";

	private Vector reactions = new Vector();

	private String referenceMap = "";

	private Box relationBox = new Box();

	private boolean gettingAllEnzymes = false;

	private Vector allPathwayEnzymes;

	private Vector allPathwayEnzymeNames;

	private Vector allPathwayEnzymeDBlinks;

	private Vector allPathwayEnzymeStructures;

	private Vector allPathwayEnzymePathways;

	private Vector allCompoundDetails;

	private Vector allCompoundDBlinks;

	private Vector allCompoundNames;

	private Vector allGlycanDetails;

	private Vector allGlycanDBlinks;

	private Vector allGlycanPathways;

	private Vector allGlycanEnzymes;

	private Vector getAllGeneDetails;

	private Vector getAllGeneDBlinks;

	private Vector getAllGenePathways;

	private Vector getAllGeneMotifs;

	private Vector getAllGeneOrthologs;

	private Vector getAllGeneEnzymes;

	private Vector reactionDetails;

	private Vector reactionSubstrates;

	private Vector reactionProducts;

	private Vector reactionType;

	private ProgressBar bar;

	private GraphDimension graphDimension = new GraphDimension();

	private InternalGraphRepresentation internalgraphRepresentation;
	
	private String[] details;
	
	private Vector allElements;

	private Vector allReferences;
	
	public CopyOfKEGGConnector(ProgressBar bar) {
		this.bar = bar;
	}
	
	public CopyOfKEGGConnector(ProgressBar bar, String[] details){
		this.bar = bar;
		this.details = details;
		for (int i = 0; i<details.length; i++){
			System.out.println(details[i]);
		}
	}

	private void updateGraph() {
		myGraph.updateGraph();
	}

	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}

	private void startVisualizationModel() {
		myGraph.restartVisualizationModel();
		myGraph.unlockVertices();
	}

	private void buildComplexStructure() {

		Iterator it = groupSet.keySet().iterator();
		while (it.hasNext()) {

			String nodeId = it.next().toString();
			String complexId = groupSet.get(nodeId).toString();

			Complex c = (Complex) entrySet.get(complexId);
			Object ob = entrySet.get(nodeId);

			c.addElement(ob);
		}
	}

	private void processElement(Object ob, boolean check) {

		BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ob;
		pw.addElement(ob);
		myGraph.moveVertex(bna.getVertex(), bna.getKEGGnode().getXPos(), bna
				.getKEGGnode().getYPos());

		listGroups(ob, check);
		listRelations(ob);
	}

	private void listGroups(Object ob, boolean check) {

		BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ob;

		if (check) {
			if (groupSet.containsKey(bna.getKEGGnode().getKEGGentryID())) {
				entrySet.put(bna.getKEGGnode().getKEGGentryID(), ob);
			}
		} else {
			entrySet.put(bna.getKEGGnode().getKEGGentryID(), ob);
		}
	}

	private void listRelations(Object ob) {

		BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ob;
		if (relationIDs.contains(bna.getKEGGnode().getKEGGentryID())) {
			relationSetNodes.put(bna.getKEGGnode().getKEGGentryID(), ob);
		}
	}

	private boolean getSpecificRelations(Vector entries, String firstElement,String secondElement) {

		Iterator it = entries.iterator();
		while (it.hasNext()) {
			String[] resultDetails = (String[]) it.next();
			if ((resultDetails[3].equals(firstElement) && resultDetails[4]
					.equals(secondElement))
					|| (resultDetails[4].equals(firstElement) && resultDetails[3]
							.equals(secondElement)))
				return true;
		}

		return false;
	}

	private void getRelations() {

		String[] query_map = { referenceMap };
		String[] query_organism = { pathwayID };
		//TODO
		/*Vector v = wrapper.requestDbContent(2, KEGGQueries.getKEGGGRelations,query_map);

		Vector v2 = wrapper.requestDbContent(2, KEGGQueries.getKEGGGRelations,query_organism);

		Iterator it = v.iterator();
		while (it.hasNext()) {

			String[] resultDetails = (String[]) it.next();

			String firstEntry = resultDetails[3];
			String secondEntry = resultDetails[4];

			if (Character.isDigit(resultDetails[2].charAt(0))) {

				String[] details = new String[4];
				details[0] = resultDetails[4];
				details[1] = resultDetails[2];
				details[2] = resultDetails[5];
				details[3] = "reference";

				if (getSpecificRelations(v2, firstEntry, secondEntry)) {
					details[3] = "no_reference";
				}

				if (!relationBox.doEntriesExist(resultDetails[4],
						resultDetails[2])) {

					ll.addItem(new Node(details));
					relationIDs.add(resultDetails[4]);
					relationIDs.add(resultDetails[2]);
					relationBox.addElements(resultDetails[4], resultDetails[2]);

				}
			} else {

				String[] details = new String[4];
				details[0] = resultDetails[4];
				details[1] = resultDetails[3];
				details[2] = resultDetails[5];
				details[3] = "reference";

				if (getSpecificRelations(v2, firstEntry, secondEntry))
					details[3] = "no_reference";

				if (!relationBox.doEntriesExist(resultDetails[4],
						resultDetails[3])) {

					ll.addItem(new Node(details));
					relationIDs.add(resultDetails[4]);
					relationIDs.add(resultDetails[3]);
					relationBox.addElements(resultDetails[4], resultDetails[3]);

				}
			}
		}*/
	}

	private void buildRelationType(String[] details, boolean checkEdgesBefore) {

		String firstID = details[0].toString();
		String secondID = details[1].toString();

		BiologicalNodeAbstract first = (BiologicalNodeAbstract) relationSetNodes
				.get(firstID);

		BiologicalNodeAbstract second = (BiologicalNodeAbstract) relationSetNodes
				.get(secondID);

		Vertex one = first.getVertex();
		Vertex two = second.getVertex();

		if (!internalgraphRepresentation.doesEdgeExist(one, two)
				&& !internalgraphRepresentation.doesEdgeExist(two, one)) {

			if (checkEdgesBefore) {

				if (!internalgraphRepresentation
						.are2NodesConnectedThroughOneElement(one, two)) {
					KEGGEdge edge = new KEGGEdge();
					edge.setName("Relation");

					edge.setEntry1(((BiologicalNodeAbstract) relationSetNodes
							.get(firstID)).getName());
					edge.setEntry1(((BiologicalNodeAbstract) relationSetNodes
							.get(secondID)).getName());

					edge.setType(details[2]);

					Compound c = new Compound(myGraph.createEdge(one, two,
							false), "", "");
					c.setDirected(false);
					c.setKeggEdge(edge);

					pw.addElement(c);

					if (details[3].toString().equals("reference")) {
						c.setReference(true);
					} else {
						c.setReference(false);
						c.setColor(Color.BLUE);
						setReferencesBetweenEdges(c.getEdge());
					}
				}
			} else {
				KEGGEdge edge = new KEGGEdge();
				edge.setName("Relation");

				edge.setEntry1(((BiologicalNodeAbstract) relationSetNodes
						.get(firstID)).getName());
				edge.setEntry1(((BiologicalNodeAbstract) relationSetNodes
						.get(secondID)).getName());

				edge.setType(details[2]);

				Compound c = new Compound(myGraph.createEdge(one, two, false),
						"", "");
				c.setDirected(false);
				c.setKeggEdge(edge);

				pw.addElement(c);

				if (details[3].toString().equals("reference")) {
					c.setReference(true);
				} else {
					c.setReference(false);
					c.setColor(Color.BLUE);
					setReferencesBetweenEdges(c.getEdge());
				}
			}
		}
	}

	private void buildRelations() {

		Node item = ll.first;
		while (item != null) {

			String[] details = (String[]) item.element;
			if (!details[2].equalsIgnoreCase("ECrel")) {
				buildRelationType(details, false);
			}
			item = item.next;
		}

		item = ll.first;
		while (item != null) {

			String[] details = (String[]) item.element;
			if (details[2].equalsIgnoreCase("ECrel")) {
				buildRelationType(details, false);
			}
			item = item.next;
		}

	}

	private void getGroups(String pathwayID) {

		String[] param = { pathwayID };
		//TODO
		/*Vector v = wrapper.requestDbContent(2, KEGGQueries.getKEGGGroups, param);
		Iterator it = v.iterator();

		while (it.hasNext()) {
			String[] resultDetails = (String[]) it.next();
			groupSet.put(resultDetails[1], resultDetails[0]);
		}*/
	}

	private void getPathway(String name) {

		String[] param = { name };
		//TODO
		/*Vector v = wrapper.requestDbContent(2, KEGGQueries.getKEGGpathwayByName,param);
		Iterator it = v.iterator();
		while (it.hasNext()) {

			String[] resultDetails = (String[]) it.next();
			pathwayID = resultDetails[0];
			title = resultDetails[1];
			organism = resultDetails[2];
			pathwayLink = resultDetails[5];
			pathwayImage = resultDetails[4];
			pathwayNumber = resultDetails[3];

		}
		 */
		referenceMap = "map" + pathwayNumber;
	}

	
	private void preCollectingReactions(){
		
		Iterator it = allElements.iterator();
		String currentEntryID = "0";
		Object elementObject = null;
		
		while (it.hasNext()) {
			String[] resultDetails = (String[]) it.next();
			if (currentEntryID.equals(resultDetails[0])) {
				extendInformation(resultDetails, elementObject);
			} else {
				if (resultDetails[4].length() != 0) {
					String reaction_temp = resultDetails[4].trim();

					if (!relationBox.doEntriesExist(reaction_temp, resultDetails[0])) {
						collectReactions(reaction_temp, resultDetails[0]);
						relationBox.addElements(reaction_temp, resultDetails[0]);
					}
				}
			}
			currentEntryID = resultDetails[0];
		}
		
	}
	
	
	private void getPathwayElements() {

//		String[] details = { referenceMap, referenceMap, referenceMap };
		String currentEntryID = "0";
//
		Object elementObject = null;
//
		//TODO allElements = wrapper.requestDbContent(2,KEGGQueries.getKEGGentriesByPathwayName, details);
//
//		getAllEntries(allElements);
		Iterator it = allElements.iterator();

		while (it.hasNext()) {

			String[] resultDetails = (String[]) it.next();
			if (currentEntryID.equals(resultDetails[0])) {
			//	extendInformation(resultDetails, elementObject);
			} else {
				elementObject = processKeggElements(resultDetails);
			}
			currentEntryID = resultDetails[0];
		}
	}

	private void extendInformation(String[] set, Object obj) {

		BiologicalNodeAbstract bna = (BiologicalNodeAbstract) obj;
		bna.setName(bna.getName() + " " + set[1]);
		bna.getKEGGnode().setKEGGentryName(
				bna.getKEGGnode().getKEGGentryName() + " " + set[1]);

	}

	private void collectReactions(String reaction, String entryID) {

		StringTokenizer Tok = new StringTokenizer(reaction);
		while (Tok.hasMoreElements()) {
			String[] details = { Tok.nextElement().toString(), entryID };
			reactions.add(details);
		}

	}

	private void buildReactions() {

		if (reactions.size() > 0) {
			Iterator it = reactions.iterator();
			String reaction_details = "";
			boolean details = false;

			while (it.hasNext()) {
				String id = ((String[]) it.next())[0].trim();
				if (!details) {
					details = true;
					reaction_details = reaction_details + "'" + id + "'";
				} else {
					reaction_details = reaction_details + ",'" + id + "'";
				}
			}
			
			//TODO
			/*reactionDetails = wrapper.requestDbContent(2, KEGGQueries.getAllReactions+ " (" + reaction_details + ");");

			String[] param = { referenceMap };

			reactionSubstrates = wrapper.requestDbContent(2,KEGGQueries.getAllSubstrate, param);

			reactionProducts = wrapper.requestDbContent(2,KEGGQueries.getAllProducts, param);

			reactionType = wrapper.requestDbContent(2,KEGGQueries.getAllReactionType, param);*/

//			it = reactions.iterator();
//
//			while (it.hasNext()) {
//
//				getReactionDetails((String[]) it.next());
//			}
		}
	}

	private void getReactionDetails(String[] reaction) {

		KEGGEdge edge = new KEGGEdge();
		Vector v;

		Iterator it = reactionDetails.iterator();
		while (it.hasNext()) {
			String[] resultDetails = (String[]) it.next();

			if (resultDetails[0].equalsIgnoreCase(reaction[0])) {

				edge.setName(resultDetails[1]);
				edge.setRemark(resultDetails[2]);
				edge.setOrthology(resultDetails[3]);
				edge.setReference(resultDetails[4]);
				edge.setComment(resultDetails[5]);
				edge.setDefinition(resultDetails[6]);
				edge.setEquation(resultDetails[7]);
				edge.setRpair(resultDetails[8]);
				edge.setKEEGReactionID(reaction[0]);
				break;
			}
		}

		it = reactionSubstrates.iterator();
		while (it.hasNext()) {
			String[] resultDetails = (String[]) it.next();
			if (resultDetails[2].equalsIgnoreCase(reaction[0])) {
				edge.addSubstrate(resultDetails[1]);
			}
		}

		it = reactionProducts.iterator();
		while (it.hasNext()) {
			String[] resultDetails = (String[]) it.next();
			if (resultDetails[1].equalsIgnoreCase(reaction[0])) {
				edge.addProduct(resultDetails[2]);

			}
		}

		it = reactionType.iterator();
		while (it.hasNext()) {
			String[] resultDetails = (String[]) it.next();
			if (resultDetails[1].equalsIgnoreCase(reaction[0])) {
				edge.setReactionType(resultDetails[2]);

			}
		}
		edge.setInvolvedEnzyme(reaction[1]);
		buildReactionEdges(edge);
	}

	private void buildReactionEdges(KEGGEdge edge) {
		Vector v = edge.getAllSubstrates();
		Iterator it = v.iterator();

		while (it.hasNext()) {
			String substrate = it.next().toString();

			BiologicalNodeAbstract substrateNode = (BiologicalNodeAbstract) pw
					.getNodeByName(substrate);
			BiologicalNodeAbstract enzymeNode = (BiologicalNodeAbstract) pw
					.getNodeByKEGGEntryID(edge.getInvolvedEnzyme());

			if (substrateNode != null && enzymeNode != null) {

				Vertex one = substrateNode.getVertex();
				Vertex two = enzymeNode.getVertex();

				if (!internalgraphRepresentation.doesEdgeExist(one, two)) {

					ReactionEdge r = new ReactionEdge(myGraph.createEdge(one,
							two, true), "", "");
					r.setDirected(true);
					r.hasKEGGEdge(true);
					r.setKeggEdge(edge);
					pw.addElement(r);
				}
			}

		}

		v = edge.getAllProducts();
		it = v.iterator();

		while (it.hasNext()) {
			String product = it.next().toString();
			BiologicalNodeAbstract productNode = (BiologicalNodeAbstract) pw
					.getNodeByName(product);
			BiologicalNodeAbstract enzymeNode = (BiologicalNodeAbstract) pw
					.getNodeByKEGGEntryID(edge.getInvolvedEnzyme());

			if (productNode != null && enzymeNode != null) {
				Vertex two = productNode.getVertex();
				Vertex one = enzymeNode.getVertex();
				if (!internalgraphRepresentation.doesEdgeExist(one, two)) {
					ReactionEdge r = new ReactionEdge(myGraph.createEdge(one,
							two, true), "", "");
					r.setDirected(true);
					r.hasKEGGEdge(true);
					r.setKeggEdge(edge);
					pw.addElement(r);
				}
			}
		}

	}

	private void getAllEntries(Vector v) {
		Iterator it = v.iterator();

		boolean enzymes = false;
		boolean genes = false;
		boolean glycans = false;
		boolean molecules = false;

		String molecules_str = "";
		String genes_str = "";
		String glycans_str = "";

		while (it.hasNext()) {

			String[] resultDetails = (String[]) it.next();
			System.out.println(resultDetails[2]);
			
			if (resultDetails[2].equals("enzyme")) {
				
				enzymes = true;
			} else if (resultDetails[2].equals("gene")) {

				if (!genes) {
					genes = true;
					genes_str = genes_str + "'" + resultDetails[1] + "'";
				} else {
					genes_str = genes_str + ",'" + resultDetails[1] + "'";
				}

			} else if (resultDetails[2].equals("compound")) {
				if (resultDetails[1].startsWith("G")
						|| resultDetails[1].startsWith("g")) {
					if (!glycans) {
						glycans = true;
						glycans_str = glycans_str + "'" + resultDetails[1]
								+ "'";
					} else {
						glycans_str = glycans_str + ",'" + resultDetails[1]
								+ "'";
					}

				} else {
					if (!molecules) {
						molecules = true;
						molecules_str = molecules_str + "'" + resultDetails[1]
								+ "'";
					} else {
						molecules_str = molecules_str + ",'" + resultDetails[1]
								+ "'";
					}
				}
			}
		}

		if (enzymes) {

			String[] atr = { pathwayNumber };
			//TODO
			/*allPathwayEnzymes = wrapper.requestDbContent(2,	KEGGQueries.getAllInvolvedEnzymes, atr);
			allPathwayEnzymeNames = wrapper.requestDbContent(2,KEGGQueries.getAllEnzymNamesInPathway, atr);
			allPathwayEnzymeDBlinks = wrapper.requestDbContent(2,KEGGQueries.getAllEnzymeDBLinks, atr);
			allPathwayEnzymeStructures = wrapper.requestDbContent(2,KEGGQueries.getAllEnzymeStructures, atr);
			allPathwayEnzymePathways = wrapper.requestDbContent(2,KEGGQueries.getEnzymePathways, atr);*/

		}

		if (genes) {

			//TODO
			/*getAllGeneDetails = wrapper.requestDbContent(2,	KEGGQueries.getAllGeneDetails + " (" + genes_str + ");");
			getAllGeneDBlinks = wrapper.requestDbContent(2,	KEGGQueries.getAllGeneDbLinks + " (" + genes_str + ");");
			getAllGenePathways = wrapper.requestDbContent(2,KEGGQueries.getAllGenePathways + " (" + genes_str + ");");
			getAllGeneMotifs = wrapper.requestDbContent(2,KEGGQueries.getAllGeneMotifs + " (" + genes_str + ");");
			getAllGeneOrthologs = wrapper.requestDbContent(2,KEGGQueries.getAllGeneOrthology + " (" + genes_str + ");");
			getAllGeneEnzymes = wrapper.requestDbContent(2,KEGGQueries.getAllGeneEnzyms + " (" + genes_str + ");");*/

		}

		if (molecules) {
			//TODO
			/*allCompoundDetails = wrapper.requestDbContent(2,KEGGQueries.getAllCompoundDetails + " (" + molecules_str+ ");");
			allCompoundDBlinks = wrapper.requestDbContent(2,KEGGQueries.getAllCompoundDbLinks + " (" + molecules_str+ ");");
			allCompoundNames = wrapper.requestDbContent(2,KEGGQueries.getAllCompoundNames + " (" + molecules_str+ ");");*/

		}

		if (glycans) {

			//TODO
			/*allGlycanDetails = wrapper.requestDbContent(2, KEGGQueries.getAllGlycanDetails + " ("+ glycans_str + ");");

			allGlycanDBlinks = wrapper.requestDbContent(2, KEGGQueries.getAllGlycanDbLinks + " ("+ glycans_str + ");");

			allGlycanPathways = wrapper.requestDbContent(2,KEGGQueries.getAllGlycanPathways + " (" + glycans_str+ ");");

			allGlycanEnzymes = wrapper.requestDbContent(2,KEGGQueries.getAllGlycanEnzyms + " (" + glycans_str + ");");*/

		}
	}

	private Object processKeggElements(String[] set) {

		Object obj = null;

		boolean check = true;
		boolean validElement = false;

		KEGGNode node = new KEGGNode();
		node.setKEGGPathway(pathwayID);
		node.setKEGGentryID(set[0]);
		node.setKEGGentryMap(set[5]);
		node.setKEGGentryName(set[1]);
		node.setKEGGentryType(set[2]);
		node.setKEGGentryLink(set[3]);
		node.setKEGGentryReaction(set[4]);
		node.setNodeLabel(set[6]);

		double xPos = Double.parseDouble(set[7]);
		double yPos = Double.parseDouble(set[8]);

		node.setXPos(xPos);
		node.setYPos(yPos);
		graphDimension.updateboundaries(new Point2D.Double(xPos, yPos));

		node.setShape(set[9]);
		node.setWidth(set[10]);
		node.setHeight(set[11]);
		node.setForegroundColour(set[12]);
		node.setBackgroundColour(set[13]);

//		if (set[4].length() != 0) {
//			String reaction_temp = set[4].trim();
//
//			if (!relationBox.doEntriesExist(reaction_temp, set[0])) {
//				collectReactions(reaction_temp, set[0]);
//				relationBox.addElements(reaction_temp, set[0]);
//			}
//		}

		if (set[2].equals("enzyme")) {
			validElement = true;

			Enzyme e = new Enzyme(node.getNodeLabel(), node.getKEGGentryName(),
					myGraph.createNewVertex());

			Iterator it = allPathwayEnzymes.iterator();
			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {

					node.setKeggComment(resultDetails[4]);
					node.setKeggenzymeClass(resultDetails[5]);
					node.setKeggsysName(resultDetails[6]);
					node.setKeggreaction(resultDetails[7]);
					node.setKeggsubstrate(resultDetails[9]);
					node.setKeggprodukt(resultDetails[10]);
					node.setKeggcofactor(resultDetails[11]);
					node.setKeggreference(resultDetails[12]);
					node.setKeggorthology(resultDetails[13]);
					node.setKeggeffector(resultDetails[14]);

					break;
				}
			}
			Vector v = new Vector();

			String[] param = { set[1] };
			it = allPathwayEnzymeNames.iterator();
			while (it.hasNext()) {

				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {
					node.addAlternativeName(resultDetails[1]);

				}

			}

			it = allPathwayEnzymeDBlinks.iterator();
			while (it.hasNext()) {

				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {
					node.addDBLink(resultDetails[1] + " (" + resultDetails[2]
							+ ")");

				}
			}

			it = allPathwayEnzymeStructures.iterator();
			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {
					node.addStructure(resultDetails[1]);

				}
			}

			it = allPathwayEnzymePathways.iterator();
			while (it.hasNext()) {

				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {
					node.addPathwayLink(resultDetails[2] + resultDetails[1]);

				}
			}

			obj = e;

		} else if (set[2].equals("gene")) {

			validElement = true;
			DNA dna = new DNA(node.getNodeLabel(), node.getKEGGentryName(),
					myGraph.createNewVertex());

			Iterator it = getAllGeneDetails.iterator();

			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {

					node.setGeneName(resultDetails[1]);
					node.setGeneDefinition(resultDetails[2]);
					node.setGenePosition(resultDetails[3]);
					node.setGeneCodonUsage(resultDetails[4]);
					node.setGeneAAseqNr(resultDetails[5]);
					node.setGeneAAseq(resultDetails[6]);
					node.setGeneNtseqNr(resultDetails[7]);
					node.setGeneNtSeq(resultDetails[8]);
					break;
				}
			}

			it = getAllGeneDBlinks.iterator();
			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {
					node.addDBLink(resultDetails[1] + " (" + resultDetails[2]
							+ ")");

				}
			}
			it = getAllGenePathways.iterator();
			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {
					node.addPathwayLink(resultDetails[2] + resultDetails[1]);

				}
			}
			it = getAllGeneMotifs.iterator();
			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {

					node.addGeneMotif(resultDetails[1] + " ("
							+ resultDetails[2] + ") ");

				}
			}

			it = getAllGeneOrthologs.iterator();
			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {

					node.setGeneOrthology(resultDetails[2]);
					node.setGeneOrthologyName(resultDetails[1]);

				}
			}

			it = getAllGeneEnzymes.iterator();
			while (it.hasNext()) {
				String[] resultDetails = (String[]) it.next();
				if (resultDetails[0].equals(set[1])) {
					node.setGeneEnzyme(resultDetails[1]);

				}
			}

			obj = dna;

		} else if (set[2].equals("group")) {
			validElement = true;
			Complex c = new Complex(node.getNodeLabel(), node
					.getKEGGentryName(), myGraph.createNewVertex());

			obj = c;

			check = false;

		} else if (set[2].equals("map")) {
			validElement = true;
			PathwayMap map = new PathwayMap(node.getNodeLabel(), node
					.getKEGGentryName(), myGraph.createNewVertex());

			obj = map;

		} else if (set[2].equals("ortholog")) {
			validElement = true;
			OrthologGroup g = new OrthologGroup(node.getNodeLabel(), node
					.getKEGGentryName(), myGraph.createNewVertex());
			obj = g;

		} else if (set[2].equals("compound")) {
			validElement = true;

			if (set[1].startsWith("G") || set[1].startsWith("g")) {

				Glycan gl = new Glycan(node.getNodeLabel(), node
						.getKEGGentryName(), myGraph.createNewVertex());

				Iterator it = allGlycanDetails.iterator();

				while (it.hasNext()) {
					String[] resultDetails = (String[]) it.next();
					if (resultDetails[0].equals(set[1])) {
						node.setGlycanName(resultDetails[1]);
						node.setCompoundMass(resultDetails[2]);
						node.setCompoundRemarks(resultDetails[3]);
						node.setGlycanOrthology(resultDetails[4]);
						node.setKeggreference(resultDetails[5]);
						node.setGlycanBracket(resultDetails[6]);
						node.setGlycanComposition(resultDetails[7]);
						node.setGlycanNode(resultDetails[8]);
						node.setGlycanEdge(resultDetails[9]);
						break;
					}
				}

				it = allGlycanDBlinks.iterator();

				while (it.hasNext()) {
					String[] resultDetails = (String[]) it.next();
					if (resultDetails[0].equals(set[1])) {

						node.addDBLink(resultDetails[1] + " ("
								+ resultDetails[2] + ")");

					}
				}

				it = allGlycanPathways.iterator();

				while (it.hasNext()) {
					String[] resultDetails = (String[]) it.next();
					if (resultDetails[0].equals(set[1])) {

						node
								.addPathwayLink(resultDetails[2]
										+ resultDetails[1]);

					}
				}

				it = allGlycanEnzymes.iterator();
				while (it.hasNext()) {
					String[] resultDetails = (String[]) it.next();
					if (resultDetails[0].equals(set[1])) {

						node.addInvolvedElement(resultDetails[1]);

					}
				}

				obj = gl;

			} else {
				SmallMolecule sm = new SmallMolecule(node.getNodeLabel(), node
						.getKEGGentryName(), myGraph.createNewVertex());

				Iterator it = allCompoundDetails.iterator();
				while (it.hasNext()) {
					String[] resultDetails = (String[]) it.next();
					if (resultDetails[0].equals(set[1])) {
						node.setCompoundFormula(resultDetails[1]);
						node.setCompoundMass(resultDetails[2]);
						node.setCompoundComment(resultDetails[3]);
						node.setCompoundRemarks(resultDetails[4]);
						node.setCompoundAtomsNr(resultDetails[5]);
						node.setCompoundAtoms(resultDetails[6]);
						node.setCompoundBondNr(resultDetails[7]);
						node.setCompoundBonds(resultDetails[8]);
						node.setCompoundSequence(resultDetails[9]);
						node.setCompoundModule(resultDetails[10]);
						node.setCompoundOrganism(resultDetails[11]);
						break;
					}
				}

				it = allCompoundNames.iterator();
				while (it.hasNext()) {
					String[] resultDetails = (String[]) it.next();
					if (resultDetails[0].equals(set[1])) {
						node.addAlternativeName(resultDetails[1]);

					}
				}

				it = allCompoundDBlinks.iterator();
				while (it.hasNext()) {
					String[] resultDetails = (String[]) it.next();
					if (resultDetails[0].equals(set[1])) {
						node.addAlternativeName(resultDetails[1]);
						node.addDBLink(resultDetails[1] + " ("
								+ resultDetails[2] + ")");

					}
				}
				obj = sm;
			}
		} else {
			validElement = true;
			Other o = new Other(node.getNodeLabel(), node.getKEGGentryName(),
					myGraph.createNewVertex());

			obj = o;

		}

		if (validElement) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) obj;
			bna.setKEGGnode(node);
			bna.hasKEGGNode(true);
			processElement(obj, check);
		}

		return obj;
	}

	private void findReferences() {

//		String[] param = { pathwayID.substring(0, 3), pw.getNumber() };
//		allReferences = wrapper.requestDbContent(2, KEGGQueries.getReactionsOutOfPathways,
//				param);
		Iterator it = allReferences.iterator();

		while (it.hasNext()) {

			String[] resultDetails = (String[]) it.next();

			String reaction = resultDetails[0];
			HashSet set = pw.getAllKEGGEdges();

			Iterator it2 = set.iterator();
			while (it2.hasNext()) {
				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) it2
						.next();
				if (bea.getKeggEdge().getKEEGReactionID().equals(reaction)) {

					bea.setReference(false);
					bea.setColor(Color.BLUE);
					Edge edge = bea.getEdge();
					setReferencesBetweenEdges(edge);

				}
			}

		}
	}

	private void setReferencesBetweenEdges(Edge e) {

		Pair vertices = e.getEndpoints();
		Vertex one = (Vertex) vertices.getFirst();
		Vertex two = (Vertex) vertices.getSecond();

		((BiologicalNodeAbstract) pw.getElement(one)).setReference(false);
		((BiologicalNodeAbstract) pw.getElement(two)).setReference(false);
		((BiologicalNodeAbstract) pw.getElement(one)).setColor(Color.GREEN);
		((BiologicalNodeAbstract) pw.getElement(two)).setColor(Color.GREEN);
	}

	public void getPathwayByName(final String[] details) throws SQLException {

		ResultSet rs = null;
		getPathway(details[0]);

		if (pathwayID != null) {
			
			if (title != null)
				pw = new CreatePathway(title).getPathway();
			else
				pw = new CreatePathway().getPathway();

			pw.setOrganism(details[2]);
			pw.setLink(pathwayLink);

			pw.setImagePath(pathwayImage);
			pw.setNumber(pathwayNumber);
			myGraph = pw.getGraph();
			internalgraphRepresentation = pw.getGraphRepresentation();

			stopVisualizationModel();
			bar.setProgressBarString("Checking Relations");

			getRelations();
			bar.setProgressBarString("Checking Groups");

			getGroups(pathwayID);
			bar.setProgressBarString("Checking Pathway Elements");

			getPathwayElements();
			bar.setProgressBarString("Building Complexes");

			buildComplexStructure();
			bar.setProgressBarString("Building Reactions");

			buildReactions();
			bar.setProgressBarString("Building Relations");

			buildRelations();
			bar.setProgressBarString("Checking References");

			findReferences();
			bar.setProgressBarString("Finishing");

		

			startVisualizationModel();
			pw.getGraph().normalCentering();

		}
	}

	@Override
	protected Void doInBackground() throws Exception {

		Runnable run=new Runnable()
		{
			public void run()
			{
				bar = new ProgressBar();
				bar.init(100, "   Loading Data ", true);
			}
		};
		SwingUtilities.invokeLater(run);
		
		getPathway(details[0]);
		
		if (pathwayID != null) {
			
			bar.setProgressBarString("Checking Relations");

			getRelations();
			bar.setProgressBarString("Checking Groups");

			getGroups(pathwayID);
			
			bar.setProgressBarString("Checking Pathway Elements");
			
			String[] details = { referenceMap, referenceMap, referenceMap };
			//TODO allElements = wrapper.requestDbContent(2,KEGGQueries.getKEGGentriesByPathwayName, details);

			getAllEntries(allElements);
			
			bar.setProgressBarString("Building Complexes");
			buildComplexStructure();
			
			bar.setProgressBarString("Building Reactions");
			preCollectingReactions();
//			buildReactions();
			
			bar.setProgressBarString("Building Relations");
			
			String[] param = { pathwayID.substring(0, 3), pathwayNumber };
//			allReferences = wrapper.requestDbContent(2, KEGGQueries.getReactionsOutOfPathways,
//					param);
			
			bar.setProgressBarString("Finishing");
			
		}
		
		return null;
	}
	
	public void done(){

		if (title != null)
			pw = new CreatePathway(title).getPathway();
		else
			pw = new CreatePathway().getPathway();
		
		pw.setOrganism(details[2]);
		pw.setLink(pathwayLink);

		pw.setImagePath(pathwayImage);
		pw.setNumber(pathwayNumber);
		myGraph = pw.getGraph();
		internalgraphRepresentation = pw.getGraphRepresentation();
		getPathwayElements();
		
	
//		Iterator it = reactions.iterator();
//		while (it.hasNext()) {
//			getReactionDetails((String[]) it.next());
//		}
//		
//		buildRelations();
//		findReferences();
		
		
		pw.setOrganism(details[2]);
		pw.setLink(pathwayLink);

		pw.setImagePath(pathwayImage);
		pw.setNumber(pathwayNumber);
		myGraph = pw.getGraph();
		internalgraphRepresentation = pw.getGraphRepresentation();

		stopVisualizationModel();

		startVisualizationModel();
		pw.getGraph().normalCentering();
		
		bar.closeWindow();
		MainWindow window = MainWindowSingelton.getInstance();
		window.updateOptionPanel();
		window.enable(true);
		
	
		myGraph.fitScaleOfViewer(myGraph.getVisualizationViewer());
		myGraph.fitScaleOfViewer(myGraph.getSatelliteView());
		myGraph.normalCentering();
		
	}
}
