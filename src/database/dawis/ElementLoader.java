package database.dawis;

import graph.jung.classes.MyGraph;

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import pojos.DBColumn;
import biologicalElements.InternalGraphRepresentation;
import biologicalElements.Pathway;
import biologicalObjects.edges.KEGGEdge;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.ReactionPairEdge;
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
import biologicalObjects.nodes.Site;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import edu.uci.ics.jung.graph.Vertex;

@SuppressWarnings("unchecked")
public class ElementLoader {

	// creates pathway components
	private MyGraph myGraph;

	// controls the depth
	private DAWISTree tree = new DAWISTree();

	// stores all elements information
	private DAWISNode dawisNode = null;

	// creates query
	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();

	// controls organisms
	private OrganismController orgController = new OrganismController();

	// controls edges
	private EdgeController edgeController;

	// needed for marking of loaded elements of the collector node
	// HashSet <vertex> of new loaded elements
	public HashSet<BiologicalNodeAbstract> loadedElements = new HashSet<BiologicalNodeAbstract>();
	// Vector <label> of new loaded elements
	public Vector<String> loadedElementsVector = new Vector<String>();
	// Vector with edges of the loaded elements
	// private Vector<String[]> newEdges = new Vector<String[]>();
	private Hashtable<String, String[]> newEdges = new Hashtable<String, String[]>();

	// Vector with directed edges
	private Vector<String> directedEdges = new Vector<String>();

	// array with synonyms of organism of interest
	public String[] synonyms;

	// shows if the elements are loaded down from collectorNode
	private boolean isDownload = false;

	// control depth
	private int searchDepth = 0;

	// Hashtable <synonym_identification-number, identification-number>
	private Hashtable<String, String> pathways = new Hashtable<String, String>();
	private Hashtable<String, String> proteine = new Hashtable<String, String>();
	private Hashtable<String, String> geneTable = new Hashtable<String, String>();
	private Hashtable<String, String> diseaseTable = new Hashtable<String, String>();
	private Hashtable<String, String> enzyme = new Hashtable<String, String>();
	private Hashtable<String, String> geneOntology = new Hashtable<String, String>();
	private Hashtable<String, String> glycan = new Hashtable<String, String>();
	private Hashtable<String, String> compound = new Hashtable<String, String>();
	private Hashtable<String, String> drug = new Hashtable<String, String>();
	private Hashtable<String, String> reaction = new Hashtable<String, String>();
	private Hashtable<String, String> reactionPair = new Hashtable<String, String>();

	// Vector <BiologicalNodeAbstract> reaction components
	Vector<BiologicalNodeAbstract> reactionEnzymes = null;
	Vector<BiologicalNodeAbstract> reactionSubstrates = null;
	Vector<BiologicalNodeAbstract> reactionProducts = null;

	// Vector <domain> of the special elements
	private Vector<String> pathwayElements = new Vector<String>();
	private Vector<String> proteinElements = new Vector<String>();
	private Vector<String> geneElements = new Vector<String>();
	private Vector<String> diseaseElements = new Vector<String>();
	private Vector<String> enzymeElements = new Vector<String>();
	private Vector<String> geneOntologyElements = new Vector<String>();
	private Vector<String> glycanElements = new Vector<String>();
	private Vector<String> compoundElements = new Vector<String>();
	private Vector<String> drugElements = new Vector<String>();
	private Vector<String> reactionPairElements = new Vector<String>();
	private Vector<String> interactionElements = new Vector<String>();

	// Hashtable with all nodes of the pathway
	private Hashtable<String, BiologicalNodeAbstract> allElements = new Hashtable<String, BiologicalNodeAbstract>();
	// Hashtable with all edges of the pathway
	private Hashtable<String, String[]> edges = new Hashtable<String, String[]>();
	// Hashtable with all reactionPairEdge informations
	private Hashtable<String, ReactionPairEdge> rPairEdges = new Hashtable<String, ReactionPairEdge>();
	// Hashtable with all reactionEdge informations
	private Hashtable<String, KEGGEdge> keggEdges = new Hashtable<String, KEGGEdge>();

	// needed for coloring the edges blue if it's the same organism as
	// the organism of interest
	public boolean organismSpecific = false;

	// identifies whether synonyms of organism of interest exist
	private boolean synonymsExist = false;

	// domains to show
	private boolean showPathways = true;
	private boolean showDiseases = true;
	private boolean showGenes = true;
	private boolean showGOs = true;
	private boolean showProteins = true;
	private boolean showEnzymes = true;
	private boolean showReactions = true;
	private boolean showReactionPairs = true;
	private boolean showCompounds = true;
	private boolean showGlycans = true;
	private boolean showDrugs = true;
	private boolean loadedPath = false;

	// organism name
	private String organism = "";

	// prepares environment for pathway showing
	DAWISConnector con;

	// // shows the progress of loading
	// ProgressBar bar;

	// parent of the actual biological node
	BiologicalNodeAbstract parentNode = null;

	private int row = 0;
	private int column = -1;

	// needed for edge control
	private InternalGraphRepresentation adjazenzList;

	/**
	 * general constructor creates pathways elements tests relations between
	 * elements and add them to the pathway prepares environment for information
	 * update of loaded elements
	 * 
	 * @param connector
	 * @param pathway
	 * @param progress
	 *            bar
	 */
	public ElementLoader(DAWISConnector connector, boolean[] settings) {

		this.con = connector;

		// create elements vector for elements to be shown
		createElementsVector(settings);

	}

	/**
	 * constructor for working with opened files creates pathways elements of
	 * collector nodes tests relations between elements and add them to the
	 * pathway prepares environment for information update of loaded elements
	 * 
	 * @param path
	 * @param b
	 */
	public ElementLoader(Pathway pw) {

		this.loadedPath = true;
		this.organismSpecific = pw.getSpecification();

		// create elements vector for elements to be shown
		createElementsVector(pw.getSettings());

		HashSet labels = pw.getAllNodeLabels();
		Iterator<String> it = labels.iterator();
		while (it.hasNext()) {
			String elementLabel = it.next();
			allElements.put(elementLabel, (BiologicalNodeAbstract) pw
					.getNodeByLabel(elementLabel));
		}

	}

	/**
	 * add domains of elements to be shown to the vectors of related domains
	 */
	private void createElementsVector(boolean[] settings) {

		showPathways = settings[0];
		showDiseases = settings[1];
		showGOs = settings[2];
		showGenes = settings[3];
		showProteins = settings[4];
		showEnzymes = settings[5];
		showCompounds = settings[6];
		showGlycans = settings[7];
		showDrugs = settings[8];
		showReactions = settings[9];
		showReactionPairs = settings[10];

		if (showPathways) {
			pathwayElements.add("Pathway Map");
			geneElements.add("Pathway Map");
			enzymeElements.add("Pathway Map");
			compoundElements.add("Pathway Map");
			glycanElements.add("Pathway Map");
			drugElements.add("Pathway Map");
		}

		if (showProteins) {
			diseaseElements.add("Protein");
			geneElements.add("Protein");
			interactionElements.add("Protein");
			geneOntologyElements.add("Protein");
			compoundElements.add("Protein");
		}

		if (showCompounds) {
			pathwayElements.add("Compound");
			enzymeElements.add("Compound");
			reactionPairElements.add("Compound");
			geneElements.add("Compound");
			diseaseElements.add("Compound");
			proteinElements.add("Compound");
			geneOntologyElements.add("Compound");
		}

		if (showReactions) {
			pathwayElements.add("Reaction");
			reactionPairElements.add("Reaction");
		}

		if (showEnzymes) {
			pathwayElements.add("Enzyme");
			geneElements.add("Enzyme");
			compoundElements.add("Enzyme");
			glycanElements.add("Enzyme");
			geneOntologyElements.add("Enzyme");
		}

		if (showDiseases) {
			geneElements.add("Disease");
			proteinElements.add("Disease");
			compoundElements.add("Disease");
		}

		if (showDrugs) {
			pathwayElements.add("Drug");
		}

		if (showGenes) {
			pathwayElements.add("Gene");
			diseaseElements.add("Gene");
			proteinElements.add("Gene");
			enzymeElements.add("Gene");
			compoundElements.add("Gene");
		}

		if (showGlycans) {
			pathwayElements.add("Glycan");
			enzymeElements.add("Glycan");
		}

		if (showGOs) {
			proteinElements.add("Gene Ontology");
			enzymeElements.add("Gene Ontology");
			interactionElements.add("Gene Ontology");
			compoundElements.add("Gene Ontology");
		}

		if (showReactionPairs) {
			compoundElements.add("Reaction Pair");
		}

	}

	/**
	 * get details of the element
	 * 
	 * @param elementData
	 *            <object, id, name, organism, database>
	 * @param parent
	 * @throws SQLException
	 */
	public void getDetails(String[] elementData, BiologicalNodeAbstract parent)
			throws SQLException {

		String object = elementData[0];
		String id = elementData[1];
		String name = elementData[2];
		String db = elementData[4];

		BiologicalNodeAbstract newBNA = null;

		if (name == null || name == "") {
			name = id;
		}

		if (object.equals("Pathway Map")) {

			if (!this.pathways.containsKey(id)) {

				pathways.put(id, id);

				PathwayMap p = new PathwayMap(id, name, null);
				p.setAbstract(false);
				p.setReference(false);
				p.setElementsVector(pathwayElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(p);
				}

				if (parent != null) {
					p.setParentNode(parent);
				}

				newBNA = p;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);
				p.lookUpAtAllDatabases();

				addAllIDsToPathwaysVector(newBNA, this.pathways);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {
					String id2 = pathways.get(id);
					createEdge(id2, parent);
				}
			}

		} else if (object.equals("Disease")) {

			if (!diseaseTable.containsKey(id)) {

				diseaseTable.put(id, id);

				Disease d = new Disease(id, name, null);
				d.setAbstract(false);
				d.setReference(false);
				d.setElementsVector(diseaseElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(d);
				}

				if (parent != null) {
					d.setParentNode(parent);
				}

				newBNA = d;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);

				addAllIDsToPathwaysVector(newBNA, this.diseaseTable);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {
					String id2 = diseaseTable.get(id);
					createEdge(id2, parent);
				}
			}

		} else if (object.equals("Gene")) {

			if (!geneTable.containsKey(id)) {

				geneTable.put(id, id);

				Gene g = new Gene(id, name, null);
				g.setAbstract(false);
				g.setReference(false);
				g.setElementsVector(geneElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(g);
				}

				if (parent != null) {
					g.setParentNode(parent);
				}

				newBNA = g;

				allElements.put(newBNA.getLabel(), newBNA);
				newBNA.setDB(db);

				createDAWISNode(newBNA);
				g.lookUpAtAllDatabases();

				addAllIDsToPathwaysVector(newBNA, this.geneTable);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

				getEMBLGenes(g);
				amendGeneNode(g);

			} else {
				if (parent != null) {
					createEdge(geneTable.get(id), parent);
				}

			}

		} else if (object.equals("Factor")) {

			if (!allElements.containsKey(id)) {

				Factor g = new Factor(id, name, null);
				g.setAbstract(false);
				g.setReference(false);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(g);
				}

				if (parent != null) {
					g.setParentNode(parent);
				}

				newBNA = g;

				allElements.put(newBNA.getLabel(), newBNA);
				newBNA.setDB(db);

				createDAWISNode(newBNA);

			} else {

				if (parent != null) {
					createEdge(id, parent);
				}

			}

		} else if (object.equals("Fragment")) {

			if (!allElements.containsKey(id)) {

				Fragment g = new Fragment(id, name, null);
				g.setAbstract(false);
				g.setReference(false);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(g);
				}

				if (parent != null) {
					g.setParentNode(parent);
				}

				newBNA = g;

				allElements.put(newBNA.getLabel(), newBNA);
				newBNA.setDB(db);

				createDAWISNode(newBNA);

			} else {
				if (parent != null) {
					createEdge(id, parent);
				}
			}

		} else if (object.equals("Site")) {

			if (!allElements.containsKey(id)) {

				Site g = new Site(id, name, null);
				g.setAbstract(false);
				g.setReference(false);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(g);
				}

				if (parent != null) {
					g.setParentNode(parent);
				}

				newBNA = g;

				allElements.put(newBNA.getLabel(), newBNA);
				newBNA.setDB(db);

				createDAWISNode(newBNA);

			} else {
				if (parent != null) {
					createEdge(id, parent);
				}
			}

		} else if (object.equals("Protein")) {

			if (!proteine.containsKey(id)) {

				proteine.put(id, id);

				Protein p = new Protein(id, name, null);
				p.setAbstract(false);
				p.setReference(false);
				p.setElementsVector(proteinElements);
				if (parent != null) {
					p.setParentNode(parent);
				}

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(p);
				}

				newBNA = p;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);
				p.lookUpAtAllDatabases();

				addAllIDsToPathwaysVector(newBNA, this.proteine);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

				if (db.equalsIgnoreCase("HPRD")
						| db.equalsIgnoreCase("Transfac")) {
					getProteinInteractors(p);
				}

			} else {
				if (parent != null) {
					createEdge(proteine.get(id), parent);
				}
			}

		} else if (object.equals("Protein Interactor")) {

			if (!proteine.containsKey(id)) {

				proteine.put(id, id);

				Protein p = new Protein(id, name, null);
				p.setAbstract(false);
				p.setReference(false);
				p.setElementsVector(proteinElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(p);
				}

				if (parent != null) {
					p.setParentNode(parent);
				}

				newBNA = p;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);
				p.lookUpAtAllDatabases();

				addAllIDsToPathwaysVector(newBNA, this.proteine);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {
					createEdge(proteine.get(id), parent);
				}
			}

		} else if (object.equals("Enzyme")) {

			if (!enzyme.containsKey(id)) {

				enzyme.put(id, id);

				Enzyme e = new Enzyme(id, name, null);
				e.setAbstract(false);
				e.setReference(false);
				e.setElementsVector(enzymeElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(e);
				}

				if (parent != null) {
					e.setParentNode(parent);
				}

				newBNA = e;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);

				addAllIDsToPathwaysVector(newBNA, this.enzyme);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {
					createEdge(enzyme.get(id), parent);
				}
			}

		} else if (object.equals("Compound")) {

			if (!compound.containsKey(id)) {

				compound.put(id, id);

				CompoundNode c = new CompoundNode(id, name, null);
				c.setAbstract(false);
				c.setReference(false);
				c.setElementsVector(compoundElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(c);
				}

				if (parent != null) {
					c.setParentNode(parent);
				}

				newBNA = c;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);

				addAllIDsToPathwaysVector(newBNA, this.compound);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {

					createEdge(compound.get(id), parent);

				}
			}

		} else if (object.equals("Glycan")) {

			if (!glycan.containsKey(id)) {

				glycan.put(id, id);

				Glycan gl = new Glycan(id, name, null);
				gl.setAbstract(false);
				gl.setReference(false);
				gl.setElementsVector(glycanElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(gl);
				}

				if (parent != null) {
					gl.setParentNode(parent);
				}

				newBNA = gl;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);

				addAllIDsToPathwaysVector(newBNA, this.glycan);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {
					createEdge(glycan.get(id), parent);
				}
			}

		} else if (object.equals("Drug")) {

			if (!drug.containsKey(id)) {

				drug.put(id, id);

				Drug dr = new Drug(id, name, null);
				dr.setAbstract(false);
				dr.setReference(false);
				dr.setElementsVector(drugElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(dr);
				}

				if (parent != null) {
					dr.setParentNode(parent);
				}

				newBNA = dr;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);

				addAllIDsToPathwaysVector(newBNA, this.drug);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {
					createEdge(drug.get(id), parent);
				}
			}

		} else if (object.equals("Gene Ontology")) {

			if (!geneOntology.containsKey(id)) {

				geneOntology.put(id, id);

				GeneOntology go = new GeneOntology(id, name, null);
				go.setAbstract(false);
				go.setReference(false);
				go.setElementsVector(geneOntologyElements);

				if (loadedElementsVector.contains(id)) {
					loadedElements.add(go);
				}

				if (parent != null) {
					go.setParentNode(parent);
				}

				newBNA = go;
				allElements.put(newBNA.getLabel(), newBNA);

				newBNA.setDB(db);
				createDAWISNode(newBNA);

				addAllIDsToPathwaysVector(newBNA, this.geneOntology);

				if (!loadedPath) {
					createTreeNode(newBNA, parent);
				}

			} else {
				if (parent != null) {
					createEdge(geneOntology.get(id), parent);
				}
			}
		} else if (object.equals("Reaction")) {

			String[] r = { id };

			getReactionDetails(r, parent, db);

		} else if (object.equals("Reaction Pair")) {

			if (!reactionPair.containsKey(id)) {
				reactionPair.put(id, id);

				String[] rp = { id };
				createReactionPair(rp, parent, db);
			}
		}
		prozessElement(newBNA, parent, null);
	}
	
	private void getEMBLGenes(Gene g)
	{
		String tfGene="";
		DAWISNode node=g.getDAWISNode();
		Hashtable<String, String> ht=node.getAllIDDBRelationsAsHashtable();
		if (ht.contains("Transfac"))
		{
			tfGene=ht.get("Transfac");
		}
		String[] attributes={tfGene};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, DAWISQueries.getEMBLGeneFromTFGene, attributes);
		
		for (DBColumn column : results)
		{
			String[] res=column.getColumn();
			if (!geneTable.containsKey(res[0]))
			{

				geneTable.put(res[0], res[0]);

				Gene emblGene=new Gene(res[0], "", null);
				emblGene.setAbstract(false);
				emblGene.setReference(false);

				if (loadedElementsVector.contains(res[0]))
				{
					loadedElements.add(emblGene);
				}

				allElements.put(emblGene.getLabel(), emblGene);
				emblGene.setDB("EMBL");

				createDAWISNode(emblGene);

				createEdge(emblGene.getLabel(), g);
			}
		}
	}
	
//	private void getEMBLGenes(Gene g) {
//		String tfGene = "";
//		DAWISNode node = g.getDAWISNode();
//		Hashtable<String, String> ht = node.getAllIDDBRelationsAsHashtable();
//		if (ht.contains("Transfac")) {
//			tfGene = ht.get("Transfac");
//		}
//		String[] attributes = { tfGene };
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3,
//				DAWISQueries.getEMBLGeneFromTFGene, attributes);
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] res = it.next();
//			if (!geneTable.containsKey(res[0])) {
//
//				geneTable.put(res[0], res[0]);
//
//				Gene emblGene = new Gene(res[0], "", null);
//				emblGene.setAbstract(false);
//				emblGene.setReference(false);
//
//				if (loadedElementsVector.contains(res[0])) {
//					loadedElements.add(emblGene);
//				}
//
//				allElements.put(emblGene.getLabel(), emblGene);
//				emblGene.setDB("EMBL");
//
//				createDAWISNode(emblGene);
//
//				createEdge(emblGene.getLabel(), g);
//			}
//		}
//	}

	private void getProteinInteractors(Protein protein)
	{

		Hashtable<String, String> ht=protein.getDAWISNode().getAllIDsAsHashtable();
		Set<String> set=ht.keySet();
		Iterator<String> it=set.iterator();

		String[] det=new String[1];
		ArrayList<DBColumn> results=new ArrayList<DBColumn>();
		Iterator<String[]> resIt;
		String[] interactors;

		while (it.hasNext())
		{
			String db=(String)it.next();

			String query=new String();
			if (db.equalsIgnoreCase("Transfac"))
			{

				query=DAWISQueries.getTFProteinInteractor+"'"+ht.get(db)+"' or t.factor_id = '"+ht.get(db)+"'";

				results=new Wrapper().requestDbContent(3, query);

				if (results.size()<10)
				{
					for (DBColumn column : results)
					{
						interactors=column.getColumn();

						if (interactors!=null)
						{
							if (!interactors[0].equals(""))
							{
								if (!interactors[0].trim().equals(protein.getLabel()))
								{
									if (!this.proteine.containsKey(interactors[0].trim()))
									{
										proteine.put(interactors[0].trim(), interactors[0].trim());

										Protein interactor=new Protein(interactors[0].trim(), interactors[1], null);
										interactor.setAbstract(false);
										interactor.setReference(false);
										interactor.setDB(db);

										if (loadedElementsVector.contains(interactors[0].trim()))
										{
											loadedElements.add(interactor);
										}

										createDAWISNode(interactor);

										allElements.put(interactor.getLabel(), interactor);
										interactor.lookUpAtAllDatabases();

										addAllIDsToPathwaysVector(interactor, this.proteine);

										createEdge(interactor.getLabel(), protein);
									}
									else
									{
										createEdge(proteine.get(interactors[0].trim()), protein);
									}
								}
								else
								{
									if (!this.proteine.containsKey(interactors[2].trim()))
									{
										proteine.put(interactors[2].trim(), interactors[2].trim());
										Protein interactor=new Protein(interactors[2].trim(), interactors[3], null);
										interactor.setAbstract(false);
										interactor.setReference(false);
										interactor.setDB(db);

										if (loadedElementsVector.contains(interactors[2].trim()))
										{
											loadedElements.add(interactor);
										}

										createDAWISNode(interactor);
										allElements.put(interactor.getLabel(), interactor);

										interactor.lookUpAtAllDatabases();

										addAllIDsToPathwaysVector(interactor, this.proteine);

										createEdge(interactor.getLabel(), protein);
									}
									else
									{
										createEdge(proteine.get(interactors[2].trim()), protein);
									}
								}
							}
						}
					}
				}
				else
				{
					collectInElementInformationWindow("Protein Interactor", protein, null, results, "Transfac");
				}

			}
			else if (db.equalsIgnoreCase("HPRD"))
			{
				query=DAWISQueries.getHPRDProteinInteractor;
				det[0]=ht.get(db);
				results=new Wrapper().requestDbContent(3, query, det);
				
				if (results.size()<10)
				{
					for (DBColumn column : results)
					{
						interactors=column.getColumn();
						
						if (interactors!=null)
						{
							if (!interactors[0].equals(""))
							{
								if (!this.proteine.containsKey(interactors[0]))
								{
									proteine.put(interactors[0], interactors[0]);
									Protein interactor=new Protein(interactors[0], interactors[1], null);
									interactor.setAbstract(false);
									interactor.setReference(false);
									interactor.setDB(db);

									if (loadedElementsVector.contains(interactors[0].trim()))
									{
										loadedElements.add(interactor);
									}

									createDAWISNode(interactor);

									allElements.put(interactor.getLabel(), interactor);
									interactor.lookUpAtAllDatabases();

									addAllIDsToPathwaysVector(interactor, this.proteine);

									String key=protein.getLabel()+interactor.getLabel();
									String key2=interactor.getLabel()+protein.getLabel();
									String[] entry={protein.getLabel(), interactor.getLabel(), "False"};

									if (loadedElementsVector.contains(protein.getLabel())||loadedElementsVector.contains(interactor.getLabel()))
									{
										newEdges.put(key, entry);
									}
									else
									{
										// -- test directed edge --
										testForDirectedEdge(key, key2, entry);
									}

								}
								else
								{
									String key=protein.getLabel()+interactors[0].trim();
									String key2=interactors[0].trim()+protein.getLabel();
									String[] entry={proteine.get(interactors[0].trim()), interactors[0].trim(), "False"};

									if (loadedElementsVector.contains(protein.getLabel())||loadedElementsVector.contains(interactors[0].trim()))
									{
										newEdges.put(key, entry);
									}
									else
									{
										// -- test directed edge --
										testForDirectedEdge(key, key2, entry);
									}
								}
							}
						}
					}
				}
				else
				{
					collectInElementInformationWindow("Protein Interactor", protein, null, results, "HPRD");
				}

			}

		}
	}
	
//	private void getProteinInteractors(Protein protein) {
//
//		Hashtable<String, String> ht = protein.getDAWISNode()
//				.getAllIDsAsHashtable();
//		Set<String> set = ht.keySet();
//		Iterator<String> it = set.iterator();
//
//		String[] det = new String[1];
//		Vector<String[]> results;
//		Iterator<String[]> resIt;
//		String[] interactors;
//
//		while (it.hasNext()) {
//			String db = (String) it.next();
//
//			String query = "";
//			if (db.equalsIgnoreCase("Transfac")) {
//
//				query = DAWISQueries.getTFProteinInteractor + "'" + ht.get(db)
//						+ "' or t.factor_id = '" + ht.get(db) + "'";
//
//				results = new Wrapper().requestDbContent(3, query);
//
//				if (results.size() < 10) {
//					resIt = results.iterator();
//					while (resIt.hasNext()) {
//						interactors = (String[]) resIt.next();
//						if (interactors != null) {
//							if (!interactors[0].equals("")) {
//								if (!interactors[0].trim().equals(
//										protein.getLabel())) {
//									if (!this.proteine
//											.containsKey(interactors[0].trim())) {
//										proteine.put(interactors[0].trim(),
//												interactors[0].trim());
//
//										Protein interactor = new Protein(
//												interactors[0].trim(),
//												interactors[1], null);
//										interactor.setAbstract(false);
//										interactor.setReference(false);
//										interactor.setDB(db);
//
//										if (loadedElementsVector
//												.contains(interactors[0].trim())) {
//											loadedElements.add(interactor);
//										}
//
//										createDAWISNode(interactor);
//
//										allElements.put(interactor.getLabel(),
//												interactor);
//										interactor.lookUpAtAllDatabases();
//
//										addAllIDsToPathwaysVector(interactor,
//												this.proteine);
//
//										createEdge(interactor.getLabel(),
//												protein);
//									} else {
//										createEdge(proteine.get(interactors[0]
//												.trim()), protein);
//									}
//								} else {
//									if (!this.proteine
//											.containsKey(interactors[2].trim())) {
//										proteine.put(interactors[2].trim(),
//												interactors[2].trim());
//										Protein interactor = new Protein(
//												interactors[2].trim(),
//												interactors[3], null);
//										interactor.setAbstract(false);
//										interactor.setReference(false);
//										interactor.setDB(db);
//
//										if (loadedElementsVector
//												.contains(interactors[2].trim())) {
//											loadedElements.add(interactor);
//										}
//
//										createDAWISNode(interactor);
//										allElements.put(interactor.getLabel(),
//												interactor);
//
//										interactor.lookUpAtAllDatabases();
//
//										addAllIDsToPathwaysVector(interactor,
//												this.proteine);
//
//										createEdge(interactor.getLabel(),
//												protein);
//									} else {
//										createEdge(proteine.get(interactors[2]
//												.trim()), protein);
//									}
//								}
//							}
//						}
//					}
//				} else {
//					collectInElementInformationWindow("Protein Interactor",
//							protein, null, results, "Transfac");
//				}
//
//			} else if (db.equalsIgnoreCase("HPRD")) {
//				query = DAWISQueries.getHPRDProteinInteractor;
//				det[0] = ht.get(db);
//				results = new Wrapper().requestDbContent(3, query, det);
//				if (results.size() < 10) {
//					resIt = results.iterator();
//					while (resIt.hasNext()) {
//						interactors = (String[]) resIt.next();
//						if (interactors != null) {
//							if (!interactors[0].equals("")) {
//								if (!this.proteine.containsKey(interactors[0])) {
//
//									proteine
//											.put(interactors[0], interactors[0]);
//									Protein interactor = new Protein(
//											interactors[0], interactors[1],
//											null);
//									interactor.setAbstract(false);
//									interactor.setReference(false);
//									interactor.setDB(db);
//
//									if (loadedElementsVector
//											.contains(interactors[0].trim())) {
//										loadedElements.add(interactor);
//									}
//
//									createDAWISNode(interactor);
//
//									allElements.put(interactor.getLabel(),
//											interactor);
//									interactor.lookUpAtAllDatabases();
//
//									addAllIDsToPathwaysVector(interactor,
//											this.proteine);
//
//									String key = protein.getLabel()
//											+ interactor.getLabel();
//									String key2 = interactor.getLabel()
//											+ protein.getLabel();
//									String[] entry = { protein.getLabel(),
//											interactor.getLabel(), "False" };
//
//									if (loadedElementsVector.contains(protein
//											.getLabel())
//											|| loadedElementsVector
//													.contains(interactor
//															.getLabel())) {
//										newEdges.put(key, entry);
//									} else {
//
//										// test directed edge
//										testForDirectedEdge(key, key2, entry);
//
//									}
//
//								} else {
//									String key = protein.getLabel()
//											+ interactors[0].trim();
//									String key2 = interactors[0].trim()
//											+ protein.getLabel();
//									String[] entry = {
//											proteine.get(interactors[0].trim()),
//											interactors[0].trim(), "False" };
//
//									if (loadedElementsVector.contains(protein
//											.getLabel())
//											|| loadedElementsVector
//													.contains(interactors[0]
//															.trim())) {
//										newEdges.put(key, entry);
//									} else {
//										// test directed edge
//										testForDirectedEdge(key, key2, entry);
//									}
//								}
//							}
//						}
//					}
//				} else {
//					collectInElementInformationWindow("Protein Interactor",
//							protein, null, results, "HPRD");
//				}
//
//			}
//
//		}
//	}

	private void testForDirectedEdge(String key, String key2, String[] entry) {
		if (!directedEdges.contains(key)) {
			if (!directedEdges.contains(key2)) {
				if (!edges.containsKey(key2)) {
					edges.put(key, entry);
				}
			}
		}
	}

	/**
	 * add all identificationnumbers to hashtable <synonym, label>
	 * 
	 * @param newBNA
	 * @param hashtable
	 *            <synonym, label>
	 */
	private void addAllIDsToPathwaysVector(BiologicalNodeAbstract newBNA,
			Hashtable<String, String> elements) {
		Hashtable<String, String> ht = newBNA.getDAWISNode()
				.getAllIDsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();
		while (it.hasNext()) {
			String actualID = (String) it.next();
			elements.put(actualID, newBNA.getLabel());
		}
	}

	/**
	 * create edge between new element and its parent
	 * 
	 * @param id
	 * @param parent
	 */
	private void createEdge(String id, BiologicalNodeAbstract parent) {

		if (allElements.get(id) != null) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) allElements
					.get(id);
			if (organismSpecific) {

				if (bna.getLabel().equals(parent.getLabel())) {

					String key = parent.getLabel() + bna.getLabel();
					String key2 = bna.getLabel() + parent.getLabel();
					String[] entry = { parent.getLabel(), bna.getLabel(),
							"False", "SO" };

					if (loadedElementsVector.contains(parent.getLabel())
							|| loadedElementsVector.contains(bna.getLabel())) {
						newEdges.put(key, entry);
					} else {
						// test directed edge
						testForDirectedEdge(key, key2, entry);
					}
				} else {

					boolean isSameOrganism = false;

					if (bna.getBiologicalElement().equals("Disease")
							|| parent.getBiologicalElement().equals("Disease")) {
						isSameOrganism = true;
					} else {
						if (synonymsExist) {
							isSameOrganism = createTheEdgeDependendOnOrganism(
									this.synonyms, bna, parent);
						} else {
							synonyms = new String[1];
							synonyms[0] = this.organism;
							isSameOrganism = createTheEdgeDependendOnOrganism(
									synonyms, bna, parent);
						}

					}

					if (isSameOrganism) {

						String key = parent.getLabel() + bna.getLabel();
						String key2 = bna.getLabel() + parent.getLabel();

						String[] entry = { parent.getLabel(), bna.getLabel(),
								"False", "SO" };

						if (loadedElementsVector.contains(parent.getLabel())
								|| loadedElementsVector
										.contains(bna.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test directed edge
							testForDirectedEdge(key, key2, entry);
						}

					} else {
						String key = parent.getLabel() + bna.getLabel();
						String key2 = bna.getLabel() + parent.getLabel();
						String[] entry = { parent.getLabel(), bna.getLabel(),
								"False", "NSO" };

						if (loadedElementsVector.contains(parent.getLabel())
								|| loadedElementsVector
										.contains(bna.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test directed edge
							testForDirectedEdge(key, key2, entry);
						}
					}
				}
			} else {
				String key = parent.getLabel() + bna.getLabel();
				String key2 = bna.getLabel() + parent.getLabel();
				String[] entry = { parent.getLabel(), bna.getLabel(), "False",
						"NSO" };

				if (loadedElementsVector.contains(parent.getLabel())
						|| loadedElementsVector.contains(bna.getLabel())) {
					newEdges.put(key, entry);
				} else {
					// test directed edge
					testForDirectedEdge(key, key2, entry);
				}
			}
			if (parent != null) {
				try {
					testTheDepthLevel(id, parent);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * create the blue edge if the organism fits or the gray edge if not
	 * 
	 * @param bna
	 * @param prozessData
	 */
	public boolean createTheEdgeDependendOnOrganism(
			String[] organismOfInterest, BiologicalNodeAbstract parent,
			BiologicalNodeAbstract newBNA) {

		boolean isSameOrganismus = false;

		String actualObject = newBNA.getBiologicalElement();
		String organism = newBNA.getOrganism();

		if (!organism.equals("") && !organism.equals("map")) {

			if (actualObject.equals("Disease")
					|| parent.getBiologicalElement().equals("Disease")) {
				isSameOrganismus = true;
			} else {
				if (!actualObject.equals("Drug")
						&& !actualObject.equals("Gene Ontology")) {

					isSameOrganismus = orgController.testForOrganism(organism,
							organismOfInterest);

				}

			}

		}
		if (organism.equals("")) {
			isSameOrganismus = orgController.testForOrganism(orgController
					.getOrganisms(newBNA), newBNA.getBiologicalElement(),
					organismOfInterest);
		}
		if (isSameOrganismus) {
			newBNA.setOrganism(organismOfInterest[0]);
		}

		return isSameOrganismus;

	}

	private void testTheDepthLevel(String id, BiologicalNodeAbstract parent)
			throws SQLException {

		BiologicalNodeAbstract bna = (BiologicalNodeAbstract) allElements
				.get(id);
		DefaultMutableTreeNode bnaNode = bna.getDefaultMutableTreeNode();
		int bnaLevel = bnaNode.getLevel();

		DefaultMutableTreeNode bnaParent = (DefaultMutableTreeNode) bnaNode
				.getParent();
		DefaultMutableTreeNode parentNode = parent.getDefaultMutableTreeNode();
		int parentLevel = parentNode.getLevel();

		if (!bnaParent.equals(parentNode)) {
			if (bnaLevel - parentLevel > 1) {
				bnaParent.remove(bnaNode);
				parentNode.add(bnaNode);
				controlTheDepth(bna);
			}

		}
	}

	/**
	 * construct the edge(s) for the element
	 * 
	 * @param prozessData
	 * @param bna
	 * @param newNode
	 * @param elem
	 * @throws SQLException
	 */
	private void prozessElement(BiologicalNodeAbstract newBNA,
			BiologicalNodeAbstract parent, BiologicalNodeAbstract parent2)
			throws SQLException {

		boolean parentExists = false;
		boolean parent2Exists = false;
		boolean organismOfElementExists = false;
		boolean sameOrganism = false;

		if (newBNA != null) {

			if (newBNA.getBiologicalElement().equals("Enzyme")
					|| newBNA.getBiologicalElement().equals("Compound")
					|| newBNA.getBiologicalElement().equals("Glycan")) {
				if (showReactions) {
					getReactions(newBNA);
				}
			}

			if (parent != null) {
				parentExists = true;
			}

			if (parent2 != null) {
				parent2Exists = true;
			}

			String orgOfElement = newBNA.getOrganism();
			if (orgOfElement != null && !orgOfElement.equals("")) {
				organismOfElementExists = true;
			}

			if (parentExists) {
				if (organismSpecific) {
					if (!(newBNA instanceof CollectorNode)) {
						sameOrganism = testOrganismRelation(
								organismOfElementExists, newBNA);
					}

					if (sameOrganism) {
						newBNA.setOrganism(this.organism);
						String key = parent.getLabel() + newBNA.getLabel();
						String key2 = newBNA.getLabel() + parent.getLabel();
						String[] entry = { parent.getLabel(),
								newBNA.getLabel(), "False", "SO" };

						if (loadedElementsVector.contains(parent.getLabel())
								|| loadedElementsVector.contains(newBNA
										.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test directed edge
							testForDirectedEdge(key, key2, entry);
						}
					} else {
						String key = parent.getLabel() + newBNA.getLabel();
						String key2 = newBNA.getLabel() + parent.getLabel();
						String[] entry = { parent.getLabel(),
								newBNA.getLabel(), "False", "NSO" };

						if (loadedElementsVector.contains(parent.getLabel())
								|| loadedElementsVector.contains(newBNA
										.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test directed edge
							testForDirectedEdge(key, key2, entry);
						}
					}

				} else {
					String key = parent.getLabel() + newBNA.getLabel();
					String key2 = newBNA.getLabel() + parent.getLabel();
					String[] entry = { parent.getLabel(), newBNA.getLabel(),
							"False", "NSO" };

					if (loadedElementsVector.contains(parent.getLabel())
							|| loadedElementsVector.contains(newBNA.getLabel())) {
						newEdges.put(key, entry);
					} else {
						// test directed edge
						testForDirectedEdge(key, key2, entry);
					}
				}
			}

			if (parent2Exists) {
				if (organismSpecific) {
					if (sameOrganism) {
						String key = parent2.getLabel() + newBNA.getLabel();
						String key2 = newBNA.getLabel() + parent.getLabel();
						String[] entry = { parent2.getLabel(),
								newBNA.getLabel(), "False", "SO" };

						if (loadedElementsVector.contains(parent2.getLabel())
								|| loadedElementsVector.contains(newBNA
										.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test directed edge
							testForDirectedEdge(key, key2, entry);
						}
					} else {
						String key = parent2.getLabel() + newBNA.getLabel();
						String key2 = newBNA.getLabel() + parent.getLabel();
						String[] entry = { parent2.getLabel(),
								newBNA.getLabel(), "False", "NSO" };

						if (loadedElementsVector.contains(parent2.getLabel())
								|| loadedElementsVector.contains(newBNA
										.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test directed edge
							testForDirectedEdge(key, key2, entry);
						}
					}

				} else {
					String key = parent2.getLabel() + newBNA.getLabel();
					String key2 = newBNA.getLabel() + parent.getLabel();
					String[] entry = { parent2.getLabel(), newBNA.getLabel(),
							"False", "NSO" };

					if (loadedElementsVector.contains(parent2.getLabel())
							|| loadedElementsVector.contains(newBNA.getLabel())) {
						newEdges.put(key, entry);
					} else {
						// test directed edge
						testForDirectedEdge(key, key2, entry);
					}
				}
			}

			controlTheDepth(newBNA);

		}

	}

	/**
	 * test whether the element exists in the organism of interest
	 * 
	 * @param organismOfElementExists
	 * @param newBNA
	 * @return
	 */
	private boolean testOrganismRelation(boolean organismOfElementExists,
			BiologicalNodeAbstract newBNA) {

		boolean sameOrganism = false;
		String orgOfElement = newBNA.getOrganism();

		if (organismOfElementExists) {

			/*
			 * test if the organism of interest equals the organism of the
			 * element or its synonyms
			 */
			if (this.organism.equals(orgOfElement)) {
				sameOrganism = true;
			} else {
				if (!synonymsExist) {
					synonyms = new String[1];
					synonyms[0] = this.organism;

				}
				sameOrganism = orgController.testForOrganism(orgOfElement,
						synonyms);
			}
		} else {
			/*
			 * test if organism of interest exists for this element
			 */
			if (!newBNA.getBiologicalElement().equals("Disease")) {

				Vector<String> elementOrganisms = orgController
						.getOrganisms(newBNA);

				if (!synonymsExist) {
					synonyms = new String[1];
					synonyms[0] = this.organism;
				}
				sameOrganism = orgController.testForOrganism(elementOrganisms,
						newBNA.getBiologicalElement(), synonyms);
			}

		}
		return sameOrganism;
	}

	/**
	 * control the depth of the pathway
	 * 
	 * @param prozessData
	 * @param bna
	 * @param newNode
	 * @param elem
	 * @throws SQLException
	 */
	private void controlTheDepth(BiologicalNodeAbstract bna)
			throws SQLException {

		if (!bna.getBiologicalElement().equals("Collector")) {
			if (!loadedPath) {
				DefaultMutableTreeNode node = bna.getDefaultMutableTreeNode();

				Vector<String> elem = bna.getElementsVector();

				if (!elem.isEmpty()) {

					Iterator<String> it = elem.iterator();

					while (it.hasNext()) {

						String el = (String) it.next();

						if (el.equals("Pathway Map") && showPathways
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getPathways(bna);
						} else if (el.equals("Disease") && showDiseases
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getDiseases(bna);
						} else if (el.equals("Gene") && showGenes
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getGenes(bna);
						} else if (el.equals("Protein") && showProteins
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getProteins(bna);
						} else if (el.equals("Enzyme") && showEnzymes
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getEnzymes(bna);
						} else if (el.equals("Reaction") && showReactions
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getReactions(bna);
						} else if (el.equals("Compound") && showCompounds
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getCompounds(bna);
						} else if (el.equals("Glycan") && showGlycans
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getGlycans(bna);
						} else if (el.equals("Drug") && showDrugs
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getDrugs(bna);
						} else if (el.equals("Gene Ontology") && showGOs
								&& ((node.getLevel() - 1) < this.searchDepth)) {
							getGeneOntologies(bna);
						}
					}
				}
			}
		}
	}

	/**
	 * prepare query and element data from pathway for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getPathways(BiologicalNodeAbstract bna) throws SQLException {

		boolean queryComplete = false;

		String lobject = bna.getBiologicalElement();
		String query = "";
		String actualID = "";
		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {
			String dbToLook = (String) it.next();
			actualID = ht.get(dbToLook);
			if (lobject.equals("Pathway Map")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					if (actualID.length() < 6) {
						actualID = "map" + actualID;
					}
					query = DAWISQueries.getPathwayFromPathway; // id, name
					String[] data = { query, actualID, "Pathway Map", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else {
					String tpID = ht.get(dbToLook);
					query = DAWISQueries.getTPPathwayFromTPPathway; // id,
					// name
					String[] data = { query, tpID, "Pathway Map", "Transpath" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Gene")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					actualID = ht.get(dbToLook);
					if (organismSpecific) {
						query = DAWISQueries.getPathwayFromGene; // id, name,
						// org
					} else {
						query = DAWISQueries.getPathwayFromGeneOrganismIndependent; // id,
						// name
					}
					String[] data = { query, actualID, "Pathway Map", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Enzyme")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getPathwayNumberAndNameFromEnzyme; // number!=id,
					String[] data = { query, actualID, "Pathway Map", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Compound")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getPathwayFromCompound; // number!=id,
					String[] data = { query, actualID, "Pathway Map", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getTPPathwayFromCompound; // number!=id,
					String[] data = { query, actualID, "Pathway Map",
							"Transpath" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Reaction")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getPathwayFromReaction; // number!=id,
					String[] data = { query, actualID, "Pathway Map", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getTPPathwayFromReaction; // number!=id,
					String[] data = { query, actualID, "Pathway Map",
							"Transpath" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Glycan")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getPathwayFromGlycan; // number!=id,
					// name
					String[] data = { query, actualID, "Pathway Map", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Drug")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getPathwayFromDrug; // number!=id, name
					String[] data = { query, actualID, "Pathway Map", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}

			}

		}

	}
	
	/**
	 * prepare query and element data from gene ontology for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getGeneOntologies(BiologicalNodeAbstract bna) throws SQLException
	{

		boolean queryComplete=false;
		String lobject=bna.getBiologicalElement();
		String query=new String();
		String actualID=new String();

		Hashtable<String, String> ht=bna.getDAWISNode().getAllIDDBRelationsAsHashtable();
		Set<String> s=ht.keySet();
		Iterator<String> it=s.iterator();

		while (it.hasNext())
		{

			String dbToLook=it.next(); // db of the bna-element
			actualID=ht.get(dbToLook);
			if (lobject.equals("Enzyme"))
			{

				if (dbToLook.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getGOFromEnzyme; // id, name
					String[] data={query, actualID, "Gene Ontology", "GO"};
					continueSearch(bna, queryComplete, data);
				}

			}
			else if (lobject.equals("Compound"))
			{

				if (dbToLook.equalsIgnoreCase("Transpath"))
				{
					query=DAWISQueries.getGOFromCompound; // id, name
					String[] data={query, actualID, "Gene Ontology", "GO"};
					continueSearch(bna, queryComplete, data);
				}

			}
			else if (lobject.equals("Protein"))
			{

				if (dbToLook.equalsIgnoreCase("UniProt"))
				{
					String preliminaryQuery=DAWISQueries.getGOIDFromProtein;
					String[] det={actualID};
					ArrayList<DBColumn> preliminaryResult=new Wrapper().requestDbContent(3, preliminaryQuery, det);
					
					if (preliminaryResult.size()>10)
					{
						Vector goIDs=new Vector();
						for (DBColumn column : preliminaryResult)
						{
							String[] goID=column.getColumn();

							actualID=goID[0].trim();
							goIDs.add(actualID);
						}
						
						String goIDString=createIDString(goIDs);
						ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, DAWISQueries.getGOFromProtein2+goIDString);
						collectInElementInformationWindow("Gene Ontology", bna, null, results, "GO");
					}
					else
					{
						for (DBColumn column : preliminaryResult)
						{

							String[] goID=column.getColumn();
							actualID=goID[0].trim();
							query=DAWISQueries.getGOFromProtein; // id, name

							String[] data={query, actualID, "Gene Ontology", "GO"};
							continueSearch(bna, queryComplete, data);
						}
					}

				}
				else if (dbToLook.equalsIgnoreCase("HPRD"))
				{
					query=DAWISQueries.getGOFromHPRDProtein; // id, name
					String[] data={query, actualID, "Gene Ontology", "GO"};
					continueSearch(bna, queryComplete, data);
				}
			}
		}
	}
	
//	/**
//	 * prepare query and element data from gene ontology for further search
//	 * 
//	 * @param bna
//	 * @param data
//	 * @param parentNode
//	 * @throws SQLException
//	 */
//	public void getGeneOntologies(BiologicalNodeAbstract bna)
//			throws SQLException {
//
//		boolean queryComplete = false;
//		String lobject = bna.getBiologicalElement();
//		String query = "";
//		String actualID = "";
//
//		Hashtable<String, String> ht = bna.getDAWISNode()
//				.getAllIDDBRelationsAsHashtable();
//		Set<String> s = ht.keySet();
//		Iterator<String> it = s.iterator();
//
//		while (it.hasNext()) {
//
//			String dbToLook = it.next(); // db of the bna-element
//			actualID = ht.get(dbToLook);
//			if (lobject.equals("Enzyme")) {
//
//				if (dbToLook.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getGOFromEnzyme; // id, name
//					String[] data = { query, actualID, "Gene Ontology", "GO" };
//					continueSearch(bna, queryComplete, data);
//				}
//
//			} else if (lobject.equals("Compound")) {
//
//				if (dbToLook.equalsIgnoreCase("Transpath")) {
//					query = DAWISQueries.getGOFromCompound; // id, name
//					String[] data = { query, actualID, "Gene Ontology", "GO" };
//					continueSearch(bna, queryComplete, data);
//				}
//
//			} else if (lobject.equals("Protein")) {
//
//				if (dbToLook.equalsIgnoreCase("UniProt")) {
//					String preliminaryQuery = DAWISQueries.getGOIDFromProtein;
//					String[] det = { actualID };
//					Vector<String[]> preliminaryResult = new Wrapper()
//							.requestDbContent(3, preliminaryQuery, det);
//					Iterator<String[]> preliminaryIterator = preliminaryResult
//							.iterator();
//					if (preliminaryResult.size() > 10) {
//						Vector goIDs = new Vector();
//						while (preliminaryIterator.hasNext()) {
//							String[] goID = (String[]) preliminaryIterator
//									.next();
//
//							actualID = goID[0].trim();
//							goIDs.add(actualID);
//						}
//						String goIDString = createIDString(goIDs);
//						Vector results = new Wrapper().requestDbContent(3,
//								DAWISQueries.getGOFromProtein2 + goIDString);
//						collectInElementInformationWindow("Gene Ontology", bna,
//								null, results, "GO");
//					} else {
//						while (preliminaryIterator.hasNext()) {
//
//							String[] goID = (String[]) preliminaryIterator
//									.next();
//							actualID = goID[0].trim();
//							query = DAWISQueries.getGOFromProtein; // id, name
//
//							String[] data = { query, actualID, "Gene Ontology",
//									"GO" };
//							continueSearch(bna, queryComplete, data);
//						}
//					}
//
//				} else if (dbToLook.equalsIgnoreCase("HPRD")) {
//					query = DAWISQueries.getGOFromHPRDProtein; // id, name
//					String[] data = { query, actualID, "Gene Ontology", "GO" };
//					continueSearch(bna, queryComplete, data);
//				}
//			}
//		}
//	}

	private String createIDString(Vector<String> goIDs) {
		String goIDString = "(";
		boolean first = true;

		Iterator<String> it = goIDs.iterator();

		while (it.hasNext()) {

			String id = it.next();

			if (first) {
				goIDString = goIDString + "'" + id + "'";
				first = false;
			} else {
				goIDString = goIDString + ",'" + id + "'";
			}

		}
		goIDString = goIDString + ");";
		return goIDString;
	}

	/**
	 * prepare query and element data from reaction pair for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getReactionPairs(BiologicalNodeAbstract bna)
			throws SQLException {

		boolean queryComplete = false;

		String lobject = bna.getBiologicalElement();

		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {

			String dbToLook = (String) it.next(); // db of the bna-element

			if (lobject.equals("Reaction")) {

				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getReactionPairFromReaction; // id,
					// name
					String[] data = { query, actualID, "Reaction Pair", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Compound")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getReactionPairFromCompound; // id,
					// name
					String[] data = { query, actualID, "Reaction Pair", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Reaction Pair")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getReactionPairFromReactionPair;
					String[] data = { query, actualID, "Reaction Pair", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}

			}

		}

	}

	/**
	 * prepare query and element data from compound for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getCompounds(BiologicalNodeAbstract bna) throws SQLException {

		boolean queryComplete = false;

		String lobject = bna.getBiologicalElement();
		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {

			String dbToLook = it.next(); // db of the bna-element
			actualID = ht.get(dbToLook);
			if (lobject.equals("Pathway Map")) {

				if (dbToLook.equalsIgnoreCase("KEGG")) {
					if (organismSpecific) {
						query = DAWISQueries.getCompoundFromPathwayOrganismSpecific; // id
					} else {
						query = DAWISQueries.getCompoundFromPathwayByNumber; // id
					}
					String[] data = { query, actualID, "Compound", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equals(("Transpath"))) {
					if (organismSpecific) {
						query = DAWISQueries.getCompoundFromTPPathway;
					} else {
						query = DAWISQueries.getCompoundFromTPPathwayOrganismIndependent;
					}
					String[] data = { query, actualID, "Compound", "Transpath" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Enzyme")) {

				query = DAWISQueries.getCompoundFromEnzyme;
				String[] data = { query, actualID, "Compound", "KEGG" };
				continueSearch(bna, queryComplete, data);
				query = DAWISQueries.getTranspathCompoundFromEnzyme;
				String[] data2 = { query, actualID, "Compound", "Transpath" };
				continueSearch(bna, queryComplete, data2);
			} else if (lobject.equals("Reaction")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getCompoundFromReaction; // id
					String[] data = { query, actualID, "Compound", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getCompoundFromTranspathReaction; // id
					String[] data = { query, actualID, "Compound", "Transpath" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Reaction Pair")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getCompoundFromReactionPair; // id
					String[] data = { query, actualID, "Compound", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Disease")) {

				query = DAWISQueries.getCompoundFromDisease; // id
				String[] data = { query, actualID, "Compound", "Transpath" };
				continueSearch(bna, queryComplete, data);

			} else if (lobject.equals("Protein")) {
				if (dbToLook.equalsIgnoreCase("UniProt")) {
					query = DAWISQueries.getCompoundFromProtein; // id
					String[] data = { query, actualID, "Compound", "Transpath" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Gene")) {
				if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getTRANSPATHCompoundFromGene; // id
					String[] data = { query, actualID, "Compound", "Transpath" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Gene Ontology")) {
				query = DAWISQueries.getTRANSPATHCompoundFromGeneOntology; // id
				String[] data = { query, actualID, "Compound", "Transpath" };
				continueSearch(bna, queryComplete, data);
			}
		}
	}

	/**
	 * prepare query and element data from drug for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getDrugs(BiologicalNodeAbstract bna) throws SQLException {

		boolean queryComplete = false;

		String lastID = bna.getLabel();
		String lobject = bna.getBiologicalElement();
		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {

			String dbToLook = (String) it.next(); // db of the bna-element

			if (lastID.length() > 5) {
				actualID = getPathwayNumber(lastID);
			}

			if (lobject.equals("Pathway Map")) {

				if (dbToLook.equalsIgnoreCase("KEGG")) {
					if (organismSpecific) {
						query = DAWISQueries.getDrugFromPathway; // id, org
					} else {
						query = DAWISQueries.getDrugFromPathwayByNumber;
					}
					String[] data = { query, actualID, "Drug", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			}

		}

	}

	/**
	 * prepare query and element data from glycan for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getGlycans(BiologicalNodeAbstract bna) throws SQLException {

		boolean queryComplete = false;

		String lobject = bna.getBiologicalElement();
		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {

			String dbToLook = (String) it.next(); // db of the bna-element

			if (lobject.equals("Pathway Map")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					if (organismSpecific) {
						query = DAWISQueries.getGlycanFromPathway;
					} else {
						query = DAWISQueries.getGlycanFromPathwayByNumber;
					}
					String[] data = { query, actualID, "Glycan", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Enzyme")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getGlycanIDFromEnzyme; // id
					String[] data = { query, actualID, "Glycan", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Reaction")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getGlycanIDFromReaction; // id
					String[] data = { query, actualID, "Glycan", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			}
		}
	}

	/**
	 * prepare query and element data from enzyme for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getEnzymes(BiologicalNodeAbstract bna) throws SQLException {

		boolean queryComplete = false;

		String lobject = bna.getBiologicalElement();
		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {

			String dbToLook = (String) it.next(); // db of the bna-element
			actualID = ht.get(dbToLook);

			if (lobject.equals("Pathway Map")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					if (organismSpecific) {
						query = DAWISQueries.getEnzymeFromPathway; // id, name
					} else {
						query = DAWISQueries.getEnzymeFromPathwayOrganismSpecific;
					}
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Gene")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getEnzymeFromGene; // id, name, org
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getEnzymeFromTranspathGene; // id,
					// name,
					// org
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equalsIgnoreCase("Transfac")) {
					query = DAWISQueries.getEnzymeFromTransfacGene; // id, name,
					// org
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Gene Ontology")) {
				if (dbToLook.equalsIgnoreCase("GO")) {
					query = DAWISQueries.getEnzymeFromGO; // id, name
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Glycan")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getEnzymeFromGlycan; // id, name
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Compound")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getEnzymeFromCompound;
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getEnzymeFromTranspathCompound2;
					String[] data = { query, actualID, "Enzyme", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			}
		}

	}

	/**
	 * prepare query and element data from protein for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getProteins(BiologicalNodeAbstract bna) throws SQLException {

		boolean queryComplete = false;

		String lobject = bna.getBiologicalElement();
		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {

			String dbToLook = it.next(); // db of the bna-element
			actualID = ht.get(dbToLook);

			if (lobject.equals("Gene Ontology")) {
				query = DAWISQueries.getProteinFromGO; // id, name
				actualID = " " + bna.getLabel();
				String[] data = { query, actualID, "Protein", "UniProt" };
				continueSearch(bna, queryComplete, data);
			} else if (lobject.equals("Disease")) {
				query = DAWISQueries.getProteinFromDisease
						+ dqv.prepareString(" " + bna.getLabel(),
								"ud.primary_id", null); // id, name
				queryComplete = true;
				String[] data = { query, actualID, "Protein", "UniProt" };
				continueSearch(bna, queryComplete, data);
				queryComplete = false;
			} else if (lobject.equals("Gene")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getProteinFromGene;
					String[] data = { query, actualID, "Protein", "UniProt" };
					continueSearch(bna, queryComplete, data);
				} else if (dbToLook.equalsIgnoreCase("EMBL")) {
					query = DAWISQueries.getProteinFromEMBLGene
							+ "ud.primary_id = ' " + bna.getLabel() + "'";
					queryComplete = true;
					String[] data = { query, actualID, "Protein", "UniProt" };
					continueSearch(bna, queryComplete, data);
					queryComplete = false;
				}
			} else if (lobject.equals("Compound")) {
				query = DAWISQueries.getProteinFromTPCompound;
				String[] data = { query, actualID, "Protein", "UniProt" };
				continueSearch(bna, queryComplete, data);
			}

		}

	}
	
	/**
	 * prepare query and element data from disease for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getDiseases(BiologicalNodeAbstract bna) throws SQLException
	{
		boolean queryComplete=false;

		String lastID=bna.getLabel();
		String lobject=bna.getBiologicalElement();
		String query="";
		String actualID="";

		Hashtable<String, String> ht=bna.getDAWISNode().getAllIDDBRelationsAsHashtable();
		Set<String> s=ht.keySet();
		Iterator<String> it=s.iterator();

		while (it.hasNext())
		{

			String dbToLook=it.next(); // db of the bna-element
			actualID=ht.get(dbToLook);

			if (lobject.equals("Gene"))
			{
				if (dbToLook.equalsIgnoreCase("KEGG"))
				{
					query=DAWISQueries.getDiseaseFromGene; // id, name
					String[] data={query, actualID, "Disease", "OMIM"};
					continueSearch(bna, queryComplete, data);
				}
				else if (dbToLook.equalsIgnoreCase("Transpath"))
				{
					query=DAWISQueries.getDiseaseFromTRANSPATHGene; // id,
					// name
					String[] data={query, actualID, "Disease", "OMIM"};
					continueSearch(bna, queryComplete, data);
				}

			}
			else if (lobject.equals("Protein"))
			{
				if (dbToLook.equalsIgnoreCase("UniProt"))
				{
					String preliminaryQuery=DAWISQueries.getDiseaseIDFromProtein+dqv.prepareString("MIM", "ud.db_name", null)+" and "+dqv.prepareString(lastID, "ud.uniprot_id", null);

					ArrayList<DBColumn> preliminaryResult=new Wrapper().requestDbContent(3, preliminaryQuery);

					for (DBColumn column : preliminaryResult)
					{

						String[] diseaseID=column.getColumn();
						actualID=diseaseID[0].trim();

						query=DAWISQueries.getDiseaseFromProtein; // id, name

						String[] data={query, actualID, "Disease", "OMIM"};
						continueSearch(bna, queryComplete, data);

					}
				}
				else if (dbToLook.equalsIgnoreCase("HPRD"))
				{
					query=DAWISQueries.getDiseaseFromHPRDProtein; // id,
					// name
					String[] data={query, actualID, "Disease", "OMIM"};
					continueSearch(bna, queryComplete, data);
				}

			}
			else if (lobject.equals("Compound"))
			{
				if (dbToLook.equalsIgnoreCase("Transpath"))
				{
					query=DAWISQueries.getCompoundFromDisease; // id,
					// name
					String[] data={query, actualID, "Disease", "OMIM"};
					continueSearch(bna, queryComplete, data);
				}
			}

		}

	}
	
//	/**
//	 * prepare query and element data from disease for further search
//	 * 
//	 * @param bna
//	 * @param data
//	 * @param parentNode
//	 * @throws SQLException
//	 */
//	public void getDiseases(BiologicalNodeAbstract bna) throws SQLException {
//
//		boolean queryComplete = false;
//
//		String lastID = bna.getLabel();
//		String lobject = bna.getBiologicalElement();
//		String query = "";
//		String actualID = "";
//
//		Hashtable<String, String> ht = bna.getDAWISNode()
//				.getAllIDDBRelationsAsHashtable();
//		Set<String> s = ht.keySet();
//		Iterator<String> it = s.iterator();
//
//		while (it.hasNext()) {
//
//			String dbToLook = it.next(); // db of the bna-element
//			actualID = ht.get(dbToLook);
//
//			if (lobject.equals("Gene")) {
//				if (dbToLook.equalsIgnoreCase("KEGG")) {
//					query = DAWISQueries.getDiseaseFromGene; // id, name
//					String[] data = { query, actualID, "Disease", "OMIM" };
//					continueSearch(bna, queryComplete, data);
//				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
//					query = DAWISQueries.getDiseaseFromTRANSPATHGene; // id,
//					// name
//					String[] data = { query, actualID, "Disease", "OMIM" };
//					continueSearch(bna, queryComplete, data);
//				}
//
//			} else if (lobject.equals("Protein")) {
//				if (dbToLook.equalsIgnoreCase("UniProt")) {
//					String preliminaryQuery = DAWISQueries.getDiseaseIDFromProtein
//							+ dqv.prepareString("MIM", "ud.db_name", null)
//							+ " and "
//							+ dqv.prepareString(lastID, "ud.uniprot_id", null);
//
//					Vector<String[]> preliminaryResult = new Wrapper()
//							.requestDbContent(3, preliminaryQuery);
//					Iterator<String[]> preliminaryIterator = preliminaryResult
//							.iterator();
//
//					while (preliminaryIterator.hasNext()) {
//
//						String[] diseaseID = preliminaryIterator.next();
//						actualID = diseaseID[0].trim();
//
//						query = DAWISQueries.getDiseaseFromProtein; // id, name
//
//						String[] data = { query, actualID, "Disease", "OMIM" };
//						continueSearch(bna, queryComplete, data);
//
//					}
//				} else if (dbToLook.equalsIgnoreCase("HPRD")) {
//					query = DAWISQueries.getDiseaseFromHPRDProtein; // id,
//					// name
//					String[] data = { query, actualID, "Disease", "OMIM" };
//					continueSearch(bna, queryComplete, data);
//				}
//
//			} else if (lobject.equals("Compound")) {
//				if (dbToLook.equalsIgnoreCase("Transpath")) {
//					query = DAWISQueries.getCompoundFromDisease; // id,
//					// name
//					String[] data = { query, actualID, "Disease", "OMIM" };
//					continueSearch(bna, queryComplete, data);
//				}
//			}
//
//		}
//
//	}

	/**
	 * prepare query and element data from gene for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getGenes(BiologicalNodeAbstract bna) throws SQLException {

		boolean queryComplete = false;

		String lobject = bna.getBiologicalElement();
		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = bna.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {
			String dbToLook = it.next(); // database of the bna
			actualID = ht.get(dbToLook);
			if (lobject.equals("Pathway Map")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					if (organismSpecific) {
						query = DAWISQueries.getGeneFromPathway; // id, org
					} else {
						query = DAWISQueries.getGeneFromPathwayOrganismIndependent;
					}
					String[] data = { query, actualID, "Gene", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}
			} else if (lobject.equals("Disease")) {

				query = DAWISQueries.getGeneFromDisease; // id, name
				String[] data = { query, actualID, "Gene", "KEGG" };
				continueSearch(bna, queryComplete, data);

				query = DAWISQueries.getTPGeneFromDisease; // id, name
				String[] data2 = { query, actualID, "Gene", "Transpath" };
				continueSearch(bna, queryComplete, data2);

			} else if (lobject.equals("Protein")) {
				if (dbToLook.equalsIgnoreCase("UniProt")) {

					query = DAWISQueries.getGeneFromUniProtProtein; // id
					String[] data1 = { query, actualID, "Gene", "KEGG" };
					continueSearch(bna, queryComplete, data1);

					query = DAWISQueries.getEMBLGeneFromUniProtProtein; // id
					String[] data2 = { query, actualID, "Gene", "EMBL" };
					continueSearch(bna, queryComplete, data2);

				} else if (bna.getDB().equalsIgnoreCase("HPRD")) {
					query = DAWISQueries.getGeneFromHPRDProtein; // id
					String[] data = { query, actualID, "Gene", "KEGG" };
					continueSearch(bna, queryComplete, data);
				}

			} else if (lobject.equals("Enzyme")) {

				query = DAWISQueries.getGeneFromEnzyme; // id
				String[] data1 = { query, actualID, "Gene", "KEGG" };
				continueSearch(bna, queryComplete, data1);

				query = DAWISQueries.getTPGeneFromEnzyme; // id
				String[] data2 = { query, actualID, "Gene", "Transpath" };
				continueSearch(bna, queryComplete, data2);

				query = DAWISQueries.getTFGeneFromEnzyme; // id
				String[] data3 = { query, actualID, "Gene", "Transfac" };
				continueSearch(bna, queryComplete, data3);

			} else if (lobject.equals("Compound")) {
				if (dbToLook.equals("Transpath")) {
					query = DAWISQueries.getGeneFromTRANSPATHCompound; // id
					String[] data = { query, actualID, "Gene", "Transpath" };
					continueSearch(bna, queryComplete, data);
				}
			}
		}
	}
	
	/**
	 * get all found elements if there are less then 10 elements otherwise bound
	 * them in a collector node
	 * 
	 * @param queryComplete
	 * @param queryData
	 * @param parentNode
	 * @throws SQLException
	 */
	private void continueSearch(BiologicalNodeAbstract bna, boolean queryComplete, String[] elementData) throws SQLException
	{
		String lastObject=bna.getBiologicalElement();
		String query=elementData[0];
		String actualID=elementData[1];
		String actualObject=elementData[2];
		String db=new String();

		if (elementData.length>3)
		{
			db=elementData[3];
		}

		ArrayList<DBColumn> results=null;

		if (!queryComplete)
		{

			if (!organismSpecific&&!actualObject.equals("Pathway Map")&&lastObject.equals("Pathway Map"))
			{
				actualID=getPathwayNumber(actualID);
			}

			String[] det={actualID};

			results=new Wrapper().requestDbContent(3, query, det);
		}
		else
		{
			results=new Wrapper().requestDbContent(3, query);
		}

		if (results!=null&&results.size()>0)
		{

			int countResults=results.size();

			if (countResults<10)
			{

				if (actualObject.equals("Reaction"))
				{
					if (countResults<4)
					{
						
						for (DBColumn column : results)
						{
							String[] det=column.getColumn();
							String[] r={det[0]};
							getReactionDetails(r, bna, db);
						}
					}
					else
					{

						collectInElementInformationWindow(actualObject, bna, null, results, db);
					}

				}
				else
				{
					prepareElementDetails(actualObject, results, bna, db);
				}
			}
			else
			{
				collectInElementInformationWindow(actualObject, bna, null, results, db);
			}

		}
	}
	
//	/**
//	 * get all found elements if there are less then 10 elements otherwise bound
//	 * them in a collector node
//	 * 
//	 * @param queryComplete
//	 * @param queryData
//	 * @param parentNode
//	 * @throws SQLException
//	 */
//	private void continueSearch(BiologicalNodeAbstract bna,
//			boolean queryComplete, String[] elementData) throws SQLException {
//
//		String lastObject = bna.getBiologicalElement();
//		String query = elementData[0];
//		String actualID = elementData[1];
//		String actualObject = elementData[2];
//		String db = "";
//
//		if (elementData.length > 3) {
//			db = elementData[3];
//		}
//
//		Vector<String[]> results = null;
//
//		if (!queryComplete) {
//
//			if (!organismSpecific && !actualObject.equals("Pathway Map")
//					&& lastObject.equals("Pathway Map")) {
//				actualID = getPathwayNumber(actualID);
//			}
//
//			String[] det = { actualID };
//
//			results = new Wrapper().requestDbContent(3, query, det);
//		} else {
//			results = new Wrapper().requestDbContent(3, query);
//		}
//
//		if (results != null && results.size() > 0) {
//
//			int countResults = results.size();
//
//			if (countResults < 10) {
//
//				if (actualObject.equals("Reaction")) {
//					if (countResults < 4) {
//						Iterator rIter = results.iterator();
//						while (rIter.hasNext()) {
//							String[] det = (String[]) rIter.next();
//							String[] r = { det[0] };
//							getReactionDetails(r, bna, db);
//						}
//					} else {
//
//						collectInElementInformationWindow(actualObject, bna,
//								null, results, db);
//					}
//
//				} else {
//					prepareElementDetails(actualObject, results, bna, db);
//				}
//			} else {
//				collectInElementInformationWindow(actualObject, bna, null,
//						results, db);
//			}
//
//		}
//	}

	/**
	 * create reaction pair
	 * 
	 * @param rp
	 *            <id>
	 * @param bna
	 * @param db
	 * @throws SQLException
	 */
	private void createReactionPair(String[] rp, BiologicalNodeAbstract bna, String db) throws SQLException
	{
		BiologicalNodeAbstract newBNA=null;
		CompoundNode one=null;
		CompoundNode two=null;
		boolean oneExists=false;
		String query=DAWISQueries.getCompoundFromReactionPair;
		ArrayList<DBColumn> rPairDetails=new Wrapper().requestDbContent(3, query, rp);
		
		for (DBColumn column : rPairDetails)
		{
			String[] det=column.getColumn();
			String id=det[0];

			if (!compound.containsKey(id))
			{
				compound.put(id, id);

				if (!oneExists)
				{
					one=new CompoundNode(det[0], "", null);
					one.setAbstract(false);
					one.setReference(false);
					one.setElementsVector(compoundElements);

					if (loadedElementsVector.contains(rp[0]))
					{
						loadedElementsVector.add(one.getLabel());
						if (!loadedElements.contains(one))
						{
							loadedElements.add(one);
						}
					}

					if (bna!=null)
					{
						one.setParentNode(bna);
					}

					newBNA=one;
					allElements.put(newBNA.getLabel(), newBNA);
					newBNA.setDB(db);
					createDAWISNode(newBNA);

					addAllIDsToPathwaysVector(newBNA, compound);

					if (!loadedPath)
					{
						createTreeNode(newBNA, bna);
					}

					oneExists=true;
				}
				else
				{
					two=new CompoundNode(det[0], "", null);
					two.setAbstract(false);
					two.setReference(false);
					two.setElementsVector(compoundElements);

					if (loadedElementsVector.contains(rp[0]))
					{
						loadedElementsVector.add(two.getLabel());
						if (!loadedElements.contains(two))
						{
							loadedElements.add(two);
						}
					}

					if (bna!=null)
					{
						two.setParentNode(bna);
					}
					newBNA=two;
					allElements.put(newBNA.getLabel(), newBNA);
					newBNA.setDB(db);
					createDAWISNode(newBNA);

					addAllIDsToPathwaysVector(newBNA, compound);
					if (!loadedPath)
					{
						createTreeNode(newBNA, bna);
					}

				}

			}
			else
			{
				if (bna!=null)
				{
					createEdge(compound.get(id), bna);
				}
			}
		}

		if (one!=null&two!=null)
		{

			ReactionPairEdge edge=new ReactionPairEdge();
			edge.setReactionPairID(rp[0]);
			fillReactionPairEdgeWithInformation(edge);

			if (organismSpecific)
			{
				boolean isSameOrganism1=false;
				boolean isSameOrganism2=false;
				if (!this.organism.equals(""))
				{
					if (!synonymsExist)
					{
						String[] syn={this.organism};
						isSameOrganism1=orgController.testForOrganism(orgController.getOrganisms(one), one.getBiologicalElement(), syn);
						isSameOrganism2=orgController.testForOrganism(orgController.getOrganisms(two), two.getBiologicalElement(), syn);
					}
					else
					{
						isSameOrganism1=orgController.testForOrganism(orgController.getOrganisms(one), one.getBiologicalElement(), this.synonyms);
						isSameOrganism2=orgController.testForOrganism(orgController.getOrganisms(two), two.getBiologicalElement(), this.synonyms);
					}
				}

				if (isSameOrganism1&&isSameOrganism2)
				{
					one.setOrganism(this.organism);
					two.setOrganism(this.organism);
					String key=one.getLabel()+two.getLabel();
					String key2=two.getLabel()+one.getLabel();
					String[] entry={one.getLabel(), two.getLabel(), "False", "SO"};

					if (loadedElementsVector.contains(one.getLabel())||loadedElementsVector.contains(two.getLabel()))
					{
						newEdges.put(key, entry);
					}
					else
					{
						// test directed edge
						testForDirectedEdge(key, key2, entry);
					}

					rPairEdges.put(one.getLabel()+two.getLabel(), edge);
				}
				else
				{
					String key=one.getLabel()+two.getLabel();
					String key2=two.getLabel()+one.getLabel();
					String[] entry={one.getLabel(), two.getLabel(), "False", "NSO"};

					if (loadedElementsVector.contains(one.getLabel())||loadedElementsVector.contains(two.getLabel()))
					{
						newEdges.put(key, entry);
					}
					else
					{
						// test directed edge
						testForDirectedEdge(key, key2, entry);
					}

					rPairEdges.put(one.getLabel()+two.getLabel(), edge);
				}

			}
			else
			{
				String key=one.getLabel()+two.getLabel();
				String key2=two.getLabel()+one.getLabel();
				String[] entry={one.getLabel(), two.getLabel(), "False", "NSO"};

				if (loadedElementsVector.contains(one.getLabel())||loadedElementsVector.contains(two.getLabel()))
				{
					newEdges.put(key, entry);
				}
				else
				{
					// test directed edge
					testForDirectedEdge(key, key2, entry);
				}

				rPairEdges.put(one.getLabel()+two.getLabel(), edge);
			}
			if (showReactions)
			{
				if ((one.getDefaultMutableTreeNode().getLevel()-1)<this.searchDepth)
				{
					query=DAWISQueries.getReactionFromReactionPair; // id,
					// name
					requestDbContent(one, query, rp, "KEGG", two);
				}
			}

			controlTheDepth(one);
			controlTheDepth(two);
		}
	}
	
//	/**
//	 * create reaction pair
//	 * 
//	 * @param rp
//	 *            <id>
//	 * @param bna
//	 * @param db
//	 * @throws SQLException
//	 */
//	private void createReactionPair(String[] rp, BiologicalNodeAbstract bna,
//			String db) throws SQLException {
//
//		BiologicalNodeAbstract newBNA = null;
//		CompoundNode one = null;
//		CompoundNode two = null;
//		boolean oneExists = false;
//		String query = DAWISQueries.getCompoundFromReactionPair;
//		Vector<String[]> rPairDetails = new Wrapper().requestDbContent(3, query, rp);
//		Iterator<String[]> it = rPairDetails.iterator();
//		while (it.hasNext()) {
//			String[] det = it.next();
//			String id = det[0];
//
//			if (!compound.containsKey(id)) {
//
//				compound.put(id, id);
//
//				if (!oneExists) {
//					one = new CompoundNode(det[0], "", null);
//					one.setAbstract(false);
//					one.setReference(false);
//					one.setElementsVector(compoundElements);
//
//					if (loadedElementsVector.contains(rp[0])) {
//						loadedElementsVector.add(one.getLabel());
//						if (!loadedElements.contains(one)) {
//							loadedElements.add(one);
//						}
//					}
//
//					if (bna != null) {
//						one.setParentNode(bna);
//					}
//
//					newBNA = one;
//					allElements.put(newBNA.getLabel(), newBNA);
//					newBNA.setDB(db);
//					createDAWISNode(newBNA);
//
//					addAllIDsToPathwaysVector(newBNA, compound);
//
//					if (!loadedPath) {
//						createTreeNode(newBNA, bna);
//					}
//
//					oneExists = true;
//
//				} else {
//					two = new CompoundNode(det[0], "", null);
//					two.setAbstract(false);
//					two.setReference(false);
//					two.setElementsVector(compoundElements);
//
//					if (loadedElementsVector.contains(rp[0])) {
//						loadedElementsVector.add(two.getLabel());
//						if (!loadedElements.contains(two)) {
//							loadedElements.add(two);
//						}
//					}
//
//					if (bna != null) {
//						two.setParentNode(bna);
//					}
//					newBNA = two;
//					allElements.put(newBNA.getLabel(), newBNA);
//					newBNA.setDB(db);
//					createDAWISNode(newBNA);
//
//					addAllIDsToPathwaysVector(newBNA, compound);
//					if (!loadedPath) {
//						createTreeNode(newBNA, bna);
//					}
//
//				}
//
//			} else {
//				if (bna != null) {
//
//					createEdge(compound.get(id), bna);
//
//				}
//			}
//		}
//
//		if (one != null & two != null) {
//
//			ReactionPairEdge edge = new ReactionPairEdge();
//			edge.setReactionPairID(rp[0]);
//			fillReactionPairEdgeWithInformation(edge);
//
//			if (organismSpecific) {
//				boolean isSameOrganism1 = false;
//				boolean isSameOrganism2 = false;
//				if (!this.organism.equals("")) {
//					if (!synonymsExist) {
//						String[] syn = { this.organism };
//						isSameOrganism1 = orgController.testForOrganism(
//								orgController.getOrganisms(one), one
//										.getBiologicalElement(), syn);
//						isSameOrganism2 = orgController.testForOrganism(
//								orgController.getOrganisms(two), two
//										.getBiologicalElement(), syn);
//
//					} else {
//						isSameOrganism1 = orgController.testForOrganism(
//								orgController.getOrganisms(one), one
//										.getBiologicalElement(), this.synonyms);
//						isSameOrganism2 = orgController.testForOrganism(
//								orgController.getOrganisms(two), two
//										.getBiologicalElement(), this.synonyms);
//					}
//				}
//
//				if (isSameOrganism1 && isSameOrganism2) {
//					one.setOrganism(this.organism);
//					two.setOrganism(this.organism);
//					String key = one.getLabel() + two.getLabel();
//					String key2 = two.getLabel() + one.getLabel();
//					String[] entry = { one.getLabel(), two.getLabel(), "False",
//							"SO" };
//
//					if (loadedElementsVector.contains(one.getLabel())
//							|| loadedElementsVector.contains(two.getLabel())) {
//						newEdges.put(key, entry);
//					} else {
//						// test directed edge
//						testForDirectedEdge(key, key2, entry);
//					}
//
//					rPairEdges.put(one.getLabel() + two.getLabel(), edge);
//				} else {
//					String key = one.getLabel() + two.getLabel();
//					String key2 = two.getLabel() + one.getLabel();
//					String[] entry = { one.getLabel(), two.getLabel(), "False",
//							"NSO" };
//
//					if (loadedElementsVector.contains(one.getLabel())
//							|| loadedElementsVector.contains(two.getLabel())) {
//						newEdges.put(key, entry);
//					} else {
//						// test directed edge
//						testForDirectedEdge(key, key2, entry);
//					}
//
//					rPairEdges.put(one.getLabel() + two.getLabel(), edge);
//				}
//
//			} else {
//				String key = one.getLabel() + two.getLabel();
//				String key2 = two.getLabel() + one.getLabel();
//				String[] entry = { one.getLabel(), two.getLabel(), "False",
//						"NSO" };
//
//				if (loadedElementsVector.contains(one.getLabel())
//						|| loadedElementsVector.contains(two.getLabel())) {
//					newEdges.put(key, entry);
//				} else {
//					// test directed edge
//					testForDirectedEdge(key, key2, entry);
//				}
//
//				rPairEdges.put(one.getLabel() + two.getLabel(), edge);
//			}
//			if (showReactions) {
//				if ((one.getDefaultMutableTreeNode().getLevel() - 1) < this.searchDepth) {
//					query = DAWISQueries.getReactionFromReactionPair; // id,
//					// name
//					requestDbContent(one, query, rp, "KEGG", two);
//				}
//			}
//
//			controlTheDepth(one);
//			controlTheDepth(two);
//		}
//	}
	
	private void fillReactionPairEdgeWithInformation(ReactionPairEdge edge)
	{
		String[] elem={edge.getReactionPairID()};
		ArrayList<DBColumn> reactionPairDetails=new Wrapper().requestDbContent(3, DAWISQueries.getReactionPairDetails, elem);

		if (reactionPairDetails.size()>0)
		{
			for (DBColumn column : reactionPairDetails)
			{
				String[] det=column.getColumn();
				edge.setName(det[2]);
				edge.setType(det[4]);
			}

		}
	}
	
//	private void fillReactionPairEdgeWithInformation(ReactionPairEdge edge) {
//
//		String[] elem = { edge.getReactionPairID() };
//		Vector<String[]> reactionPairDetails = new Wrapper().requestDbContent(3,
//				DAWISQueries.getReactionPairDetails, elem);
//
//		Iterator<String[]> it;
//		if (reactionPairDetails.size() > 0) {
//			it = reactionPairDetails.iterator();
//			while (it.hasNext()) {
//				String[] det = (String[]) it.next();
//				edge.setName(det[2]);
//				edge.setType(det[4]);
//			}
//
//		}
//	}
	
	/**
	 * store element data from the result vector in separate arrays for getting
	 * further information
	 * 
	 * @param results
	 * @param queryData
	 * @param parentNode
	 * @param countResults
	 */
	private void prepareElementDetails(String actualObject, ArrayList<DBColumn> results, BiologicalNodeAbstract bna, String db) throws SQLException
	{
		if (results.size()>0)
		{
			for (DBColumn column : results)
			{
				String id=new String();
				String name=new String();
				String org=new String();

				String elementDet[]=column.getColumn();
				int length=elementDet.length;

				if (elementDet!=null)
				{

					id=elementDet[0];

					if (length>1)
					{
						name=elementDet[1];
					}

					if (length>2)
					{
						org=elementDet[2];
					}

					// create pathway_ID if there is only path number
					if (actualObject.equals("Pathway Map"))
					{
						if (id.length()<6)
						{
							if (organismSpecific&&synonymsExist)
							{
								id=this.synonyms[0]+id;
								if (org.equals(""))
								{
									org=this.synonyms[1];
								}
							}
							else
							{
								id="map"+elementDet[0];
							}
						}
					}

					// get compounds name
					if (actualObject.equals("Compound"))
					{
						name=getName("Compound", id, db);
					}

					// get genes name
					if (actualObject.equals("Gene"))
					{
						name=getName("Gene", id, db);
					}

					// get glycans name
					if (actualObject.equals("Glycan"))
					{
						name=getName("Glycan", id, db);
					}

					// get drugs name
					if (actualObject.equals("Drug"))
					{
						name=getName("Drug", id, db);
					}

				}

				if (organismSpecific)
				{
					if (actualObject.equals("Disease"))
					{
						org="hsa";
					}
				}

				// get element details
				String elementDetails[]={actualObject, id, name, org, db};
				getDetails(elementDetails, bna);
			}
		}
	}
	
//	/**
//	 * store element data from the result vector in separate arrays for getting
//	 * further information
//	 * 
//	 * @param results
//	 * @param queryData
//	 * @param parentNode
//	 * @param countResults
//	 */
//	private void prepareElementDetails(String actualObject,
//			Vector<String[]> results, BiologicalNodeAbstract bna, String db)
//			throws SQLException {
//
//		if (results.size() > 0) {
//
//			Iterator<String[]> it = results.iterator();
//
//			while (it.hasNext()) {
//
//				String id = "";
//				String name = "";
//				String org = "";
//
//				String elementDet[] = it.next();
//				int length = elementDet.length;
//
//				if (elementDet != null) {
//
//					id = elementDet[0];
//
//					if (length > 1) {
//						name = elementDet[1];
//					}
//
//					if (length > 2) {
//						org = elementDet[2];
//					}
//
//					// create pathway_ID if there is only path number
//					if (actualObject.equals("Pathway Map")) {
//						if (id.length() < 6) {
//							if (organismSpecific && synonymsExist) {
//								id = this.synonyms[0] + id;
//								if (org.equals("")) {
//									org = this.synonyms[1];
//								}
//							} else {
//								id = "map" + elementDet[0];
//							}
//						}
//					}
//
//					// get compounds name
//					if (actualObject.equals("Compound")) {
//						name = getName("Compound", id, db);
//					}
//
//					// get genes name
//					if (actualObject.equals("Gene")) {
//						name = getName("Gene", id, db);
//					}
//
//					// get glycans name
//					if (actualObject.equals("Glycan")) {
//						name = getName("Glycan", id, db);
//					}
//
//					// get drugs name
//					if (actualObject.equals("Drug")) {
//						name = getName("Drug", id, db);
//					}
//
//				}
//
//				if (organismSpecific) {
//					if (actualObject.equals("Disease")) {
//						org = "hsa";
//					}
//				}
//
//				// get element details
//				String elementDetails[] = { actualObject, id, name, org, db };
//				getDetails(elementDetails, bna);
//			}
//		}
//	}

	/**
	 * collect the result elements in the element information window
	 * 
	 * @param bna
	 * @param results
	 * @param actuellObject
	 * @param lastID
	 * @param lastObject
	 * @param t
	 */
	private void collectInElementInformationWindow(String actualObject, BiologicalNodeAbstract parent, BiologicalNodeAbstract parent2, ArrayList<DBColumn> results, String database)
	{

		CollectorNode collectorNode=new CollectorNode(actualObject+"_"+parent.getLabel(), results.size()+"", null);
		collectorNode.setColor(Color.red);
		collectorNode.setObject(actualObject);
		collectorNode.setParent(parent);
		collectorNode.setLoader(this);
		collectorNode.setDB(database);

		allElements.put(collectorNode.getLabel(), collectorNode);

		if (loadedElementsVector.contains(parent.getLabel()))
		{
			if (!loadedElementsVector.contains(collectorNode))
			{
				loadedElementsVector.add(collectorNode.getLabel());
			}
			if (!loadedElements.contains(collectorNode))
			{
				loadedElements.add(collectorNode);
			}
		}

		parent.addCollectorNode(collectorNode);

		createDAWISNode(collectorNode);

		if (!loadedPath)
		{
			if (!(collectorNode.getLabel().startsWith("Site"))&&!(collectorNode.getLabel().startsWith("Factor"))&&!(collectorNode.getLabel().startsWith("Fragment")))
				createTreeNode(collectorNode, parent);
		}

		collectorNode.getDAWISNode().setList(results);
		
		try
		{
			prozessElement(collectorNode, parent, parent2);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
//	/**
//	 * collect the result elements in the element information window
//	 * 
//	 * @param bna
//	 * @param results
//	 * @param actuellObject
//	 * @param lastID
//	 * @param lastObject
//	 * @param t
//	 */
//	private void collectInElementInformationWindow(String actualObject,
//			BiologicalNodeAbstract parent, BiologicalNodeAbstract parent2,
//			Vector<String[]> results, String database) {
//
//		CollectorNode collectorNode = new CollectorNode(actualObject + "_"
//				+ parent.getLabel(), results.size() + "", null);
//		collectorNode.setColor(Color.red);
//		collectorNode.setObject(actualObject);
//		collectorNode.setParent(parent);
//		collectorNode.setLoader(this);
//		collectorNode.setDB(database);
//
//		allElements.put(collectorNode.getLabel(), collectorNode);
//
//		if (loadedElementsVector.contains(parent.getLabel())) {
//			if (!loadedElementsVector.contains(collectorNode)) {
//				loadedElementsVector.add(collectorNode.getLabel());
//			}
//			if (!loadedElements.contains(collectorNode)) {
//				loadedElements.add(collectorNode);
//			}
//		}
//
//		parent.addCollectorNode(collectorNode);
//
//		createDAWISNode(collectorNode);
//
//		if (!loadedPath) {
//			if (!(collectorNode.getLabel().startsWith("Site"))
//					&& !(collectorNode.getLabel().startsWith("Factor"))
//					&& !(collectorNode.getLabel().startsWith("Fragment")))
//				createTreeNode(collectorNode, parent);
//		}
//
//		collectorNode.getDAWISNode().setList(results);
//		try {
//			prozessElement(collectorNode, parent, parent2);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/**
	 * prepare query and element data from reaction for further search
	 * 
	 * @param bna
	 * @param data
	 * @param parentNode
	 * @throws SQLException
	 */
	public void getReactions(BiologicalNodeAbstract parent) throws SQLException {

		String lastID = parent.getLabel();
		String query = "";
		String actualID = "";

		Hashtable<String, String> ht = parent.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();
		Set<String> s = ht.keySet();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {
			String dbToLook = (String) it.next(); // db of the bna-element
			actualID = ht.get(dbToLook);
			String[] elem = new String[1];
			if (parent.getBiologicalElement().equals("Pathway Map")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					if (organismSpecific) {
						query = DAWISQueries.getReactionFromPathway; // id,
						// name,
						// org
					} else {
						lastID = getPathwayNumber(lastID);
						query = DAWISQueries.getReactionFromPathwayByNumber; // id,
						// name
					}
				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getTRANSPATHReactionFromPathway; // id,
					// name,
					// org
				}
				elem[0] = lastID;
				requestDbContent(parent, query, elem, dbToLook, null);
			} else if (parent.getBiologicalElement().equals("Enzyme")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getReactionFromEnzyme; // id, name
					elem[0] = actualID;
					requestDbContent(parent, query, elem, dbToLook, null);
				}
			} else if (parent.getBiologicalElement().equals("Compound")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getReactionFromCompound; // id, name
					elem[0] = actualID;
					requestDbContent(parent, query, elem, dbToLook, null);
				} else if (dbToLook.equalsIgnoreCase("Transpath")) {
					query = DAWISQueries.getTranspathReactionFromCompound; // id
					elem[0] = actualID;
					requestDbContent(parent, query, elem, dbToLook, null);
				}

			} else if (parent.getBiologicalElement().equals("Glycan")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getReactionFromGlycan; // id, name
					elem[0] = actualID;
					requestDbContent(parent, query, elem, dbToLook, null);
				}

			} else if (parent.getBiologicalElement().equals("Reaction Pair")) {
				if (dbToLook.equalsIgnoreCase("KEGG")) {
					query = DAWISQueries.getReactionFromReactionPair; // id,
					// name
				}
				elem[0] = actualID;
				requestDbContent(parent, query, elem, dbToLook, null);
			}

		}

	}
	
	private void requestDbContent(BiologicalNodeAbstract parent, String query, String[] elem, String dbToLook, BiologicalNodeAbstract parent2)
	{
		ArrayList<DBColumn> reactions=new Wrapper().requestDbContent(3, query, elem);
		int reactionsCount=reactions.size();

		if (reactionsCount<4)
		{
			for (DBColumn column : reactions)
			{
				String r[]=column.getColumn();

				getReactionDetails(r, parent, dbToLook);
			}
		}
		else
		{
			collectInElementInformationWindow("Reaction", parent, parent2, reactions, dbToLook);
		}
	}
	
//	private void requestDbContent(BiologicalNodeAbstract parent, String query,
//			String[] elem, String dbToLook, BiologicalNodeAbstract parent2) {
//
//		Vector<String[]> reactions = new Wrapper().requestDbContent(3, query, elem);
//
//		int reactionsCount = reactions.size();
//
//		if (reactionsCount < 4) {
//			Iterator<String[]> it1 = reactions.iterator();
//			while (it1.hasNext()) {
//
//				String r[] = it1.next();
//
//				getReactionDetails(r, parent, dbToLook);
//
//			}
//		} else {
//			collectInElementInformationWindow("Reaction", parent, parent2,
//					reactions, dbToLook);
//		}
//	}

	/**
	 * get all elements which are involved in this reaction
	 * 
	 * @param r
	 * @param parent
	 */
	private void getReactionDetails(String[] r, BiologicalNodeAbstract parent, String db)
	{

		String id=r[0];

		// add reation label to loadedElementsVector, if parent was loaded
		if (loadedElementsVector.contains(parent.getLabel()))
		{
			if (!loadedElementsVector.contains(r[0]))
			{
				loadedElementsVector.add(r[0]);
			}
		}

		if (!reaction.containsKey(id))
		{

			reaction.put(id, id);

			KEGGEdge edge=new KEGGEdge();
			edge.setKEEGReactionID(r[0]);

			String reactID[]={r[0]};

			boolean enzymeKnown=false;
			// Vector<String> rProd = null;
			// Vector<String> rSub = null;
			ArrayList<DBColumn> rProd=null;
			ArrayList<DBColumn> rSub=null;

			if (db.equalsIgnoreCase("KEGG"))
			{

				fillKEGGEdgeWithElementInformation(edge);

				String equation=edge.getEquation();

				getReactionEnzymes(reactID, parent);

				ReactionAnalyser reactionAnalyser=new ReactionAnalyser(equation);

				rProd=reactionAnalyser.getProducts();
				rSub=reactionAnalyser.getSubstrates();
				reactionProducts=createReactant(rProd, r[0]);
				reactionSubstrates=createReactant(rSub, r[0]);

			}
			else if (db.equalsIgnoreCase("Transpath"))
			{
				fillKEGGEdgeWithTPElementInformation(edge);
				getTranspathReactionEnzymes(id, parent);
				rProd=getTranspathReactionSubstrates(id, parent);
				rSub=getTranspathReactionProducts(id, parent);
				reactionProducts=createTPReactant(rProd, r[0]);
				reactionSubstrates=createTPReactant(rSub, r[0]);
			}

			if (reactionEnzymes!=null)
			{
				if (reactionEnzymes.size()>0)
				{
					enzymeKnown=true;
				}
			}

			Iterator<BiologicalNodeAbstract> it;

			if (enzymeKnown)
			{
				// build enzymatic reaction
				buildReaction(parent, reactionEnzymes, reactionSubstrates, reactionProducts, edge);

			}
			else
			{

				// build non enzymatic reaction
				buildNonEnzymaticReaction(parent, reactionProducts, reactionSubstrates, edge);

			}

			/*
			 * control the depth of reaction elements
			 */
			if (reactionEnzymes!=null)
			{
				it=reactionEnzymes.iterator();
				while (it.hasNext())
				{
					BiologicalNodeAbstract en=it.next();
					try
					{
						controlTheDepth(en);
					}
					catch (SQLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (reactionProducts!=null)
			{
				it=reactionProducts.iterator();
				while (it.hasNext())
				{
					BiologicalNodeAbstract pr=it.next();
					try
					{
						controlTheDepth(pr);
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
			}

			if (reactionSubstrates!=null)
			{
				it=reactionSubstrates.iterator();
				while (it.hasNext())
				{
					BiologicalNodeAbstract su=it.next();
					try
					{
						controlTheDepth(su);
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void fillKEGGEdgeWithTPElementInformation(KEGGEdge edge)
	{
		String[] elem={edge.getKEEGReactionID()};
		ArrayList<DBColumn> reactionDetails=new Wrapper().requestDbContent(3, DAWISQueries.getTranspathReactionDetails, elem);

		if (reactionDetails.size()>0)
		{
			for (DBColumn column : reactionDetails)
			{
				String[] det=column.getColumn();
				
				edge.setEffect(det[0]);
				edge.setType(det[1]);
				edge.setName(det[2]);
			}
		}
		
		ArrayList<DBColumn> reactionComment=new Wrapper().requestDbContent(3, DAWISQueries.getTranspathReactionComment, elem);
		
		for (DBColumn column : reactionComment)
		{
			String[] det=column.getColumn();
			edge.setComment(det[0]);
		}

		ArrayList<DBColumn> reactionInhibitors=new Wrapper().requestDbContent(3, DAWISQueries.getTRANSPATHReactionInhibitors, elem);
		
		for (DBColumn column : reactionInhibitors)
		{
			String[] det=column.getColumn();
			edge.setInhibitors(det[1]+": "+det[0]);
		}

	}
	
//	private void fillKEGGEdgeWithTPElementInformation(KEGGEdge edge) {
//		String[] elem = { edge.getKEEGReactionID() };
//		Vector<String[]> reactionDetails = new Wrapper().requestDbContent(3,
//				DAWISQueries.getTranspathReactionDetails, elem);
//
//		Iterator<String[]> it;
//		if (reactionDetails.size() > 0) {
//			it = reactionDetails.iterator();
//			while (it.hasNext()) {
//				String[] det = it.next();
//				edge.setEffect(det[0]);
//				edge.setType(det[1]);
//				edge.setName(det[2]);
//			}
//		}

//		Vector reactionComment = new Wrapper().requestDbContent(3,
//				DAWISQueries.getTranspathReactionComment, elem);
//		it = reactionComment.iterator();
//		while (it.hasNext()) {
//			String[] det = (String[]) it.next();
//			edge.setComment(det[0]);
//		}
//
//		Vector reactionInhibitors = new Wrapper().requestDbContent(3,
//				DAWISQueries.getTRANSPATHReactionInhibitors, elem);
//		it = reactionInhibitors.iterator();
//		while (it.hasNext()) {
//			String[] det = (String[]) it.next();
//			edge.setInhibitors(det[1] + ": " + det[0]);
//		}
//
//	}

//	private Vector<BiologicalNodeAbstract> createTPReactant(Vector<String> react, String reactionLabel)
	private Vector<BiologicalNodeAbstract> createTPReactant(ArrayList<DBColumn> react, String reactionLabel)
	{
		Vector<BiologicalNodeAbstract> reactionElements=new Vector<BiologicalNodeAbstract>();
		BiologicalNodeAbstract reactant=null;
		String id;
//		Iterator<String> it=react.iterator();
		for (DBColumn column : react)
		{
			id=column.getColumn()[0];
			if (id.startsWith("M"))
			{
				if (!compound.containsKey(id))
				{
					compound.put(id, id);
					reactant=new CompoundNode(id, "", null);
					reactant.setElementsVector(compoundElements);
					reactant.setDB("Transpath");
					reactant.setAbstract(false);
					reactant.setReference(false);

					if (loadedElementsVector.contains(reactionLabel))
					{
						if (!loadedElementsVector.contains(reactant.getLabel()))
						{
							loadedElementsVector.add(reactant.getLabel());
						}
						if (!loadedElements.contains(reactant))
						{
							loadedElements.add(reactant);
						}
					}

					allElements.put(reactant.getLabel(), reactant);

					createDAWISNode(reactant);

					reactionElements.add(reactant);
				}
				else
				{
					BiologicalNodeAbstract bna=(BiologicalNodeAbstract)allElements.get(id);
					reactant=(CompoundNode)bna;
					reactant.setAbstract(false);
					reactant.setReference(false);

					reactionElements.add(reactant);
				}
			}
			if (id.startsWith("G"))
			{
				if (!geneTable.containsKey(id))
				{
					geneTable.put(id, id);
					reactant=new Gene(id, "", null);
					reactant.setElementsVector(geneElements);
					reactant.setDB("Transpath");
					reactant.setAbstract(false);
					reactant.setReference(false);

					if (loadedElementsVector.contains(reactionLabel))
					{
						if (!loadedElementsVector.contains(reactant.getLabel()))
						{
							loadedElementsVector.add(reactant.getLabel());
						}
						if (!loadedElements.contains(reactant))
						{
							loadedElements.add(reactant);
						}
					}

					allElements.put(reactant.getLabel(), reactant);

					createDAWISNode(reactant);
					Gene g=(Gene)reactant;
					g.lookUpAtAllDatabases();

					addAllIDsToPathwaysVector(g, this.geneTable);

					reactionElements.add(reactant);
				}
				else
				{
					String id2=geneTable.get(id);
					BiologicalNodeAbstract bna=(BiologicalNodeAbstract)allElements.get(id2);
					reactant=(Gene)bna;
					reactant.setAbstract(false);
					reactant.setReference(false);

					reactionElements.add(reactant);
				}

			}

		}
		return reactionElements;
	}
	
	private void getTranspathReactionEnzymes(String id, BiologicalNodeAbstract parent)
	{
		String[] attributes={id};

		reactionEnzymes=new Vector<BiologicalNodeAbstract>();
		String query=DAWISQueries.getTRANSPATHReactionCatalysts;
		ArrayList<DBColumn> enzymes=new Wrapper().requestDbContent(3, query, attributes);
		Enzyme en=null;
		
		for (DBColumn column : enzymes)
		{
			String[] enz=column.getColumn();

			if (!enzyme.containsKey(enz[0]))
			{
				enzyme.put(enz[0], enz[0]);

				en=new Enzyme(enz[0], enz[1], null);
				en.setAbstract(false);
				en.setReference(false);
				en.setElementsVector(enzymeElements);
				en.setDB("Transpath");

				if (loadedElementsVector.contains(parent.getLabel()))
				{
					if (!loadedElementsVector.contains(en.getLabel()))
					{
						loadedElementsVector.add(en.getLabel());
					}
					if (!loadedElements.contains(en))
					{
						loadedElements.add(en);
					}
				}

				allElements.put(en.getLabel(), en);

				createDAWISNode(en);
				en.lookUpAtAllDatabases();

				if (!loadedPath)
				{
					createTreeNode(en, parent);
				}

			}
			else
			{
				BiologicalNodeAbstract bna=(BiologicalNodeAbstract)allElements.get(enz[0]);
				en=(Enzyme)bna;
			}

			reactionEnzymes.add(en);

		}
	}
	
//	private void getTranspathReactionEnzymes(String id,
//			BiologicalNodeAbstract parent) {
//
//		String[] attributes = { id };
//
//		reactionEnzymes = new Vector<BiologicalNodeAbstract>();
//		String query = DAWISQueries.getTRANSPATHReactionCatalysts;
//		Vector<String[]> enzymes = new Wrapper().requestDbContent(3, query,
//				attributes);
//		Enzyme en = null;
//		Iterator<String[]> it = enzymes.iterator();
//		while (it.hasNext()) {
//			String[] enz = (String[]) it.next();
//
//			if (!enzyme.containsKey(enz[0])) {
//				enzyme.put(enz[0], enz[0]);
//
//				en = new Enzyme(enz[0], enz[1], null);
//				en.setAbstract(false);
//				en.setReference(false);
//				en.setElementsVector(enzymeElements);
//				en.setDB("Transpath");
//
//				if (loadedElementsVector.contains(parent.getLabel())) {
//					if (!loadedElementsVector.contains(en.getLabel())) {
//						loadedElementsVector.add(en.getLabel());
//					}
//					if (!loadedElements.contains(en)) {
//						loadedElements.add(en);
//					}
//				}
//
//				allElements.put(en.getLabel(), en);
//
//				createDAWISNode(en);
//				en.lookUpAtAllDatabases();
//
//				if (!loadedPath) {
//					createTreeNode(en, parent);
//				}
//
//			} else {
//				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) allElements
//						.get(enz[0]);
//				en = (Enzyme) bna;
//			}
//
//			reactionEnzymes.add(en);
//
//		}
//	}

//	private Vector<BiologicalNodeAbstract> createReactant(Vector<String> react, String reactionLabel)
	private Vector<BiologicalNodeAbstract> createReactant(ArrayList<DBColumn> react, String reactionLabel)
	{
		Vector<BiologicalNodeAbstract> reactionElements=new Vector<BiologicalNodeAbstract>();
		BiologicalNodeAbstract reactant=null;
		String id;
		
		for (DBColumn column : react)
		{
			id=column.getColumn()[0];
			if (id.startsWith("C"))
			{
				if (!compound.containsKey(id))
				{

					compound.put(id, id);
					reactant=new CompoundNode(id, "", null);
					reactant.setElementsVector(compoundElements);
					reactant.setDB("KEGG");
					reactant.setAbstract(false);
					reactant.setReference(false);

					if (loadedElementsVector.contains(reactionLabel))
					{
						if (!loadedElementsVector.contains(reactant.getLabel()))
						{
							loadedElementsVector.add(reactant.getLabel());
						}
						if (!loadedElements.contains(reactant))
						{
							loadedElements.add(reactant);
						}

					}

					allElements.put(reactant.getLabel(), reactant);

					createDAWISNode(reactant);

				}
				else
				{
					BiologicalNodeAbstract bna=(BiologicalNodeAbstract)allElements.get(id);
					reactant=(CompoundNode)bna;
				}
			}
			if (id.startsWith("G"))
			{
				if (!glycan.containsKey(id))
				{
					glycan.put(id, id);
					reactant=new Glycan(id, "", null);
					reactant.setElementsVector(glycanElements);
					reactant.setDB("KEGG");
					reactant.setAbstract(false);
					reactant.setReference(false);
					createDAWISNode(reactant);

					if (loadedElementsVector.contains(reactionLabel))
					{
						if (!loadedElementsVector.contains(reactant.getLabel()))
						{
							loadedElementsVector.add(reactant.getLabel());
						}
						if (!loadedElements.contains(reactant))
						{
							loadedElements.add(reactant);
						}
					}

					allElements.put(reactant.getLabel(), reactant);

				}
				else
				{
					BiologicalNodeAbstract bna=(BiologicalNodeAbstract)allElements.get(id);
					reactant=(Glycan)bna;
				}

			}
			reactant.setAbstract(false);
			reactant.setReference(false);

			reactionElements.add(reactant);
		}
		return reactionElements;
	}
	
	private ArrayList<DBColumn> getTranspathReactionProducts(String id, BiologicalNodeAbstract parent)
	{
//		Vector<String> pr=new Vector<String>();
		String[] attributes={id};
		String query=DAWISQueries.getTRANSPATHReactionProducts;
		ArrayList<DBColumn> products=new Wrapper().requestDbContent(3, query, attributes);

//		for (DBColumn column : products)
//		{
//			String[] prod=column.getColumn();
//
//			pr.add(prod[0]);
//		}
//		
//		return pr;
		
		return products;
	}
	
//	private Vector<String> getTranspathReactionProducts(String id,
//			BiologicalNodeAbstract parent) {
//		Vector<String> pr = new Vector<String>();
//		String[] attributes = { id };
//		String query = DAWISQueries.getTRANSPATHReactionProducts;
//		Vector<String[]> products = new Wrapper().requestDbContent(3, query,
//				attributes);
//
//		Iterator<String[]> it = products.iterator();
//		while (it.hasNext()) {
//			String[] prod = (String[]) it.next();
//			pr.add(prod[0]);
//		}
//		return pr;
//	}
	
	private ArrayList<DBColumn> getTranspathReactionSubstrates(String id, BiologicalNodeAbstract parent)
	{
//		Vector<String> sub=new Vector<String>();
		String[] attributes={id};
		String query=DAWISQueries.getTRANSPATHReactionSubstrates;
		ArrayList<DBColumn> substrates=new Wrapper().requestDbContent(3, query, attributes);

//		for (DBColumn column : substrates)
//		{
//			String[] substr=column.getColumn();
//
//			sub.add(substr[0]);
//		}
//		
//		return sub;
		
		return substrates;
	}
	
//	private Vector<String> getTranspathReactionSubstrates(String id,
//			BiologicalNodeAbstract parent) {
//		Vector<String> sub = new Vector<String>();
//		String[] attributes = { id };
//		String query = DAWISQueries.getTRANSPATHReactionSubstrates;
//		Vector<String[]> substrates = new Wrapper().requestDbContent(3, query,
//				attributes);
//
//		Iterator<String[]> it = substrates.iterator();
//		while (it.hasNext()) {
//			String[] substr = it.next();
//			sub.add(substr[0]);
//		}
//		return sub;
//	}

	/**
	 * get reaction enzymes as a vector
	 * 
	 * @param r
	 * @param parent
	 * @return reaction enzymes
	 */
	private void getReactionEnzymes(String[] r, BiologicalNodeAbstract parent)
	{

		reactionEnzymes=new Vector<BiologicalNodeAbstract>();

		ArrayList<DBColumn> e=new Wrapper().requestDbContent(3, DAWISQueries.getEnzymeFromReaction, r);

		Enzyme en=null;

		for (DBColumn column : e)
		{
			String[] res=column.getColumn();
			String ID=res[0];
			String name=res[1];

			if (!enzyme.containsKey(ID))
			{
				enzyme.put(ID, ID);

				en=new Enzyme(res[0], name, null);
				en.setAbstract(false);
				en.setReference(false);
				en.setElementsVector(enzymeElements);
				en.setDB("KEGG");

				if (loadedElementsVector.contains(r[0]))
				{
					if (!loadedElementsVector.contains(en.getLabel()))
					{
						loadedElementsVector.add(en.getLabel());
					}
					
					if (!loadedElements.contains(en))
					{
						loadedElements.add(en);
					}
				}

				allElements.put(en.getLabel(), en);

				createDAWISNode(en);
				en.lookUpAtAllDatabases();

				if (!loadedPath)
				{
					createTreeNode(en, parent);
				}

			}
			else
			{

				BiologicalNodeAbstract bna=(BiologicalNodeAbstract)allElements.get(res[0]);
				en=(Enzyme)bna;

			}

			reactionEnzymes.add(en);

		}
	}
	
//	/**
//	 * get reaction enzymes as a vector
//	 * 
//	 * @param r
//	 * @param parent
//	 * @return reaction enzymes
//	 */
//	private void getReactionEnzymes(String[] r, BiologicalNodeAbstract parent) {
//
//		reactionEnzymes = new Vector<BiologicalNodeAbstract>();
//
//		Vector<String[]> e = new Wrapper().requestDbContent(3,
//				DAWISQueries.getEnzymeFromReaction, r);
//
//		Enzyme en = null;
//		Iterator<String[]> it = e.iterator();
//
//		while (it.hasNext()) {
//
//			String[] res = (String[]) it.next();
//			String ID = res[0];
//			String name = res[1];
//
//			if (!enzyme.containsKey(ID)) {
//				enzyme.put(ID, ID);
//
//				en = new Enzyme(res[0], name, null);
//				en.setAbstract(false);
//				en.setReference(false);
//				en.setElementsVector(enzymeElements);
//				en.setDB("KEGG");
//
//				if (loadedElementsVector.contains(r[0])) {
//					if (!loadedElementsVector.contains(en.getLabel())) {
//						loadedElementsVector.add(en.getLabel());
//					}
//					if (!loadedElements.contains(en)) {
//						loadedElements.add(en);
//					}
//				}
//
//				allElements.put(en.getLabel(), en);
//
//				createDAWISNode(en);
//				en.lookUpAtAllDatabases();
//
//				if (!loadedPath) {
//					createTreeNode(en, parent);
//				}
//
//			} else {
//
//				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) allElements
//						.get(res[0]);
//				en = (Enzyme) bna;
//
//			}
//
//			reactionEnzymes.add(en);
//
//		}
//	}

	/**
	 * build enzymatic reaction
	 * 
	 * @param reactionEnzymes2
	 * @param reactionSubstrates2
	 * @param reactionProducts2
	 * @param edge
	 */
	private void buildReaction(BiologicalNodeAbstract parent,
			Vector<BiologicalNodeAbstract> reactionEnzymes2,
			Vector<BiologicalNodeAbstract> reactionSubstrates2,
			Vector<BiologicalNodeAbstract> reactionProducts2, KEGGEdge edge) {

		boolean parentAndEnzymeAreTheSame = false;
		boolean parentAndSubstrateAreTheSame = false;
		boolean parentAndProductAreTheSame = false;

		// for each enzyme
		Iterator<BiologicalNodeAbstract> it = reactionEnzymes2.iterator();
		while (it.hasNext()) {

			Enzyme enz = (Enzyme) it.next();

			// test relation of enzyme to parent
			if (parent != null) {
				if (!parent.getLabel().equals(enz.getLabel())) {
					if (!parent.getBiologicalElement().equals("Enzyme")) {
						// create tree node if it's not opened file
						// needed for depth control
						if (!loadedPath) {
							createTreeNode(enz, parent);
						}

						// if parent and enzyme are related
						if (testRelation(parent, enz)) {

							if (parent.getBiologicalElement()
									.equals("Compound")
									|| parent.getBiologicalElement().equals(
											"Glycan")) {
								// do nothing
							} else {
								createParentEdge(parent, enz);
							}
						}
					}
				} else {
					parentAndEnzymeAreTheSame = true;
				}
			}
			edge.addEnzyme(enz.getLabel());

			// test substrates
			if (reactionSubstrates2 != null) {
				Iterator<BiologicalNodeAbstract> sIt = reactionSubstrates2
						.iterator();

				// for each substrate
				while (sIt.hasNext()) {

					BiologicalNodeAbstract sub = (BiologicalNodeAbstract) sIt
							.next();

					// test parent
					if (parent != null) {

						// if substrate is not parent
						if (!parent.getLabel().equals(sub.getLabel())) {

							// create tree node if path is loaded from opened
							// file
							if (!loadedPath) {
								createTreeNode(sub, parent);
							}

							if (parent.getBiologicalElement().equals("Enzyme")) {
								if (!parentAndEnzymeAreTheSame) {
									createParentEdge(parent, sub);
								}
							}

						} else {
							// if substrate is parent
							parentAndSubstrateAreTheSame = true;
						}
					}

					edge.addSubstrate(sub.getLabel());

					if (organismSpecific) {
						boolean isSameOrganism = false;
						if (!this.organism.equals("")) {
							if (!synonymsExist) {
								String[] syn = { this.organism };
								isSameOrganism = orgController.testForOrganism(
										orgController.getOrganisms(sub), sub
												.getBiologicalElement(), syn);
							} else {
								isSameOrganism = orgController.testForOrganism(
										orgController.getOrganisms(sub), sub
												.getBiologicalElement(),
										this.synonyms);
							}
						}

						if (isSameOrganism) {
							sub.setOrganism(this.organism);
							String key = sub.getLabel() + enz.getLabel();
							String key2 = enz.getLabel() + sub.getLabel();

							String[] entry = { sub.getLabel(), enz.getLabel(),
									"True", "SO" };

							// store the edge in newEdges, if the reaction
							// was loaded from collectorNode

							if (loadedElementsVector.contains(enz.getLabel())
									|| loadedElementsVector.contains(sub
											.getLabel())) {
								newEdges.put(key, entry);
							} else {
								// test edges by keys
								testEdges(key, key2, entry);
							}

							keggEdges
									.put(sub.getLabel() + enz.getLabel(), edge);
						} else {
							String key = sub.getLabel() + enz.getLabel();
							String key2 = enz.getLabel() + sub.getLabel();
							String[] entry = { sub.getLabel(), enz.getLabel(),
									"True", "NSO" };

							// store the edge in newEdges, if the reaction
							// was loaded from collectorNode

							if (loadedElementsVector.contains(enz.getLabel())
									|| loadedElementsVector.contains(sub
											.getLabel())) {
								newEdges.put(key, entry);
							} else {

								// test edges by keys
								testEdges(key, key2, entry);

							}
							keggEdges
									.put(sub.getLabel() + enz.getLabel(), edge);
						}

					} else {
						String key = sub.getLabel() + enz.getLabel();
						String key2 = enz.getLabel() + sub.getLabel();
						String[] entry = { sub.getLabel(), enz.getLabel(),
								"True", "NSO" };

						// store the edge in newEdges, if the reaction was
						// loaded from collectorNode
						if (loadedElementsVector.contains(enz.getLabel())
								|| loadedElementsVector
										.contains(sub.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test edges by keys
							testEdges(key, key2, entry);
						}
						keggEdges.put(sub.getLabel() + enz.getLabel(), edge);
					}
					if (parentAndSubstrateAreTheSame) {

						if (loadedElements.contains(sub)) {
							loadedElements.remove(sub);
						}
						if (loadedElementsVector.contains(sub.getLabel())) {
							loadedElementsVector.remove(sub.getLabel());
						}

					}
				}
			}

			Iterator pIt;
			if (reactionProducts2 != null) {
				pIt = reactionProducts2.iterator();
				while (pIt.hasNext()) {
					BiologicalNodeAbstract prod = (BiologicalNodeAbstract) pIt
							.next();

					if (parent != null) {
						if (!parent.getLabel().equals(prod.getLabel())) {

							if (!loadedPath) {
								createTreeNode(prod, parent);
							}

							if (parent.getBiologicalElement().equals("Enzyme")) {
								if (!parentAndEnzymeAreTheSame) {

									createParentEdge(parent, prod);
								}
							}
						} else {
							// if product is parent
							parentAndProductAreTheSame = true;
						}
					}

					edge.addProduct(prod.getLabel());
					if (organismSpecific) {
						boolean isSameOrganism = false;
						if (!this.organism.equals("")) {
							if (!synonymsExist) {
								String[] syn = { this.organism };
								isSameOrganism = orgController.testForOrganism(
										orgController.getOrganisms(prod), prod
												.getBiologicalElement(), syn);
							} else {
								isSameOrganism = orgController.testForOrganism(
										orgController.getOrganisms(prod), prod
												.getBiologicalElement(),
										this.synonyms);
							}
						}

						if (isSameOrganism) {
							prod.setOrganism(this.organism);
							String key = enz.getLabel() + prod.getLabel();
							String key2 = prod.getLabel() + enz.getLabel();
							String[] entry = { enz.getLabel(), prod.getLabel(),
									"True", "SO" };

							// store the edge in newEdges, if the reaction
							// was loaded from collectorNode
							if (loadedElementsVector.contains(enz.getLabel())
									|| loadedElementsVector.contains(prod
											.getLabel())) {

								newEdges.put(key, entry);
							} else {
								// test edges by keys
								testEdges(key, key2, entry);
							}
							keggEdges.put(enz.getLabel() + prod.getLabel(),
									edge);
						} else {
							String key = enz.getLabel() + prod.getLabel();
							String[] entry = { enz.getLabel(), prod.getLabel(),
									"True", "NSO" };
							String key2 = prod.getLabel() + enz.getLabel();

							// store the edge in newEdges, if the reaction
							// was loaded from collectorNode
							if (loadedElementsVector.contains(enz.getLabel())
									|| loadedElementsVector.contains(prod
											.getLabel())) {
								newEdges.put(key, entry);
							} else {
								// test edges by keys
								testEdges(key, key2, entry);
							}
							keggEdges.put(enz.getLabel() + prod.getLabel(),
									edge);
						}

					} else {

						String key = enz.getLabel() + prod.getLabel();
						String key2 = prod.getLabel() + enz.getLabel();
						String[] entry = { enz.getLabel(), prod.getLabel(),
								"True", "NSO" };

						// store the edge in newEdges, if the reaction was
						// loaded from collectorNode
						if (loadedElementsVector.contains(enz.getLabel())
								|| loadedElementsVector.contains(prod
										.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test edges by keys
							testEdges(key, key2, entry);
						}

						keggEdges.put(enz.getLabel() + prod.getLabel(), edge);
					}
					if (parentAndProductAreTheSame) {
						if (loadedElements.contains(prod)) {
							loadedElements.remove(prod);
						}
						if (loadedElementsVector.contains(prod.getLabel())) {
							loadedElementsVector.remove(prod.getLabel());
						}
					}
				}
			}

			if (parentAndEnzymeAreTheSame) {
				if (loadedElements.contains(enz)) {
					loadedElements.remove(enz);
				}
				if (loadedElementsVector.contains(enz.getLabel())) {
					loadedElementsVector.remove(enz.getLabel());
				}
			} else {
				if (!loadedElements.contains(enz)) {
					loadedElements.add(enz);
				}
				if (loadedElementsVector.contains(parent.getLabel())) {
					if (!parent.getLabel().equals(enz.getLabel())) {
						loadedElementsVector.add(enz.getLabel());
					}
				}
			}
		}
	}

	private void testEdges(String key, String key2, String[] entry) {

		if (edges.containsKey(key)) {
			if (!directedEdges.contains(key)) {
				edges.remove(key);
			}
		}

		if (edges.containsKey(key2)) {
			if (!directedEdges.contains(key2)) {
				edges.remove(key2);
			}
		}
		directedEdges.add(key);
		edges.put(key, entry);
	}

	private void createParentEdge(BiologicalNodeAbstract parent,
			BiologicalNodeAbstract enz) {
		// create edge
		if (organismSpecific) {
			String[] org = { this.organism };
			boolean isSameOrganism = createTheEdgeDependendOnOrganism(org,
					parent, enz);

			if (isSameOrganism) {
				// create simple blue edge
				String key = parent.getLabel() + enz.getLabel();
				String key2 = enz.getLabel() + parent.getLabel();
				String[] entry = { parent.getLabel(), enz.getLabel(), "False",
						"SO" };

				// store the edge in newEdges, if the
				// reaction was loaded from collectorNode
				if (loadedElementsVector.contains(parent.getLabel())
						|| loadedElementsVector.contains(enz.getLabel())) {
					newEdges.put(key, entry);
				} else {
					// test directed edge
					testForDirectedEdge(key, key2, entry);
				}
			} else {
				// create simple grey edge
				String key = parent.getLabel() + enz.getLabel();
				String key2 = enz.getLabel() + parent.getLabel();
				String[] entry = { parent.getLabel(), enz.getLabel(), "False",
						"NSO" };

				// store the edge in newEdges, if the
				// reaction was loaded from collectorNode
				if (loadedElementsVector.contains(parent.getLabel())
						|| loadedElementsVector.contains(enz.getLabel())) {
					newEdges.put(key, entry);
				} else {
					// test directed edge
					testForDirectedEdge(key, key2, entry);
				}
			}
		} else {
			// store the edge in newEdges, if the reaction
			// was loaded from collectorNode
			String key = parent.getLabel() + enz.getLabel();
			String key2 = enz.getLabel() + parent.getLabel();
			String[] entry = { parent.getLabel(), enz.getLabel(), "False",
					"NSO" };

			if (loadedElementsVector.contains(parent.getLabel())
					|| loadedElementsVector.contains(enz.getLabel())) {
				newEdges.put(key, entry);
			} else {
				// test directed edge
				testForDirectedEdge(key, key2, entry);
			}
		}
	}

	/**
	 * build non enzymatic reaction
	 * 
	 * @param reactionProducts2
	 * @param reactionSubstrates2
	 * @param edge
	 */
	private void buildNonEnzymaticReaction(BiologicalNodeAbstract parent,
			Vector<BiologicalNodeAbstract> reactionProducts2,
			Vector<BiologicalNodeAbstract> reactionSubstrates2, KEGGEdge edge) {

		boolean edgeNeeded = true;
		Iterator<BiologicalNodeAbstract> sIt = reactionSubstrates2.iterator();
		while (sIt.hasNext()) {
			BiologicalNodeAbstract sub = (BiologicalNodeAbstract) sIt.next();
			if (sub.equals(parent)) {
				edgeNeeded = false;
			} else {
				if (!loadedPath) {
					createTreeNode(sub, parent);
				}
			}
			Iterator<BiologicalNodeAbstract> pIt = reactionProducts2.iterator();
			while (pIt.hasNext()) {
				BiologicalNodeAbstract prod = (BiologicalNodeAbstract) pIt
						.next();
				if (prod.equals(parent)) {
					edgeNeeded = false;
				} else {
					if (!loadedPath) {
						createTreeNode(prod, parent);
					}
				}
				if (organismSpecific) {
					boolean isSameOrganism = false;
					if (!this.organism.equals("")) {
						if (!synonymsExist) {
							String[] syn = { this.organism };
							isSameOrganism = orgController.testForOrganism(
									orgController.getOrganisms(prod), prod
											.getBiologicalElement(), syn);
						} else {
							isSameOrganism = orgController.testForOrganism(
									orgController.getOrganisms(prod), prod
											.getBiologicalElement(),
									this.synonyms);
						}
					}

					if (isSameOrganism) {
						prod.setOrganism(this.organism);
						String key = sub.getLabel() + prod.getLabel();
						String key2 = prod.getLabel() + sub.getLabel();
						String[] entry = { sub.getLabel(), prod.getLabel(),
								"True", "SO" };

						if (loadedElementsVector.contains(sub.getLabel())
								|| loadedElementsVector.contains(prod
										.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test edges by keys
							testEdges(key, key2, entry);
						}

						keggEdges.put(sub.getLabel() + prod.getLabel(), edge);

					} else {
						String key = sub.getLabel() + prod.getLabel();
						String key2 = prod.getLabel() + sub.getLabel();
						String[] entry = { sub.getLabel(), prod.getLabel(),
								"True", "NSO" };

						if (loadedElementsVector.contains(sub.getLabel())
								|| loadedElementsVector.contains(prod
										.getLabel())) {
							newEdges.put(key, entry);
						} else {
							// test edges by keys
							testEdges(key, key2, entry);
						}

						keggEdges.put(sub.getLabel() + prod.getLabel(), edge);
					}

				} else {
					String key = sub.getLabel() + prod.getLabel();
					String key2 = prod.getLabel() + sub.getLabel();
					String[] entry = { sub.getLabel(), prod.getLabel(), "True",
							"NSO" };

					if (loadedElementsVector.contains(sub.getLabel())
							|| loadedElementsVector.contains(prod.getLabel())) {
						newEdges.put(key, entry);
					} else {
						// test edges by keys
						testEdges(key, key2, entry);
					}

					keggEdges.put(sub.getLabel() + prod.getLabel(), edge);
				}
			}
		}

		if (parent != null) {
			if (edgeNeeded) {

				Iterator<BiologicalNodeAbstract> it = reactionSubstrates2
						.iterator();
				while (it.hasNext()) {
					BiologicalNodeAbstract bna = it.next();
					if (testRelation(parent, bna)) {
						if (organismSpecific) {
							String[] org = { this.organism };

							boolean sameOrganism = createTheEdgeDependendOnOrganism(
									org, parent, bna);
							if (sameOrganism) {
								String key = parent.getLabel() + bna.getLabel();
								String key2 = bna.getLabel()
										+ parent.getLabel();
								String[] entry = { parent.getLabel(),
										bna.getLabel(), "True", "SO" };

								if (loadedElementsVector.contains(parent
										.getLabel())
										|| loadedElementsVector.contains(bna
												.getLabel())) {
									newEdges.put(key, entry);
								} else {
									// test edges by keys
									testEdges(key, key2, entry);
								}

								keggEdges.put(parent.getLabel()
										+ bna.getLabel(), edge);
							} else {
								String key = parent.getLabel() + bna.getLabel();
								String key2 = bna.getLabel()
										+ parent.getLabel();
								String[] entry = { parent.getLabel(),
										bna.getLabel(), "True", "NSO" };

								if (loadedElementsVector.contains(parent
										.getLabel())
										|| loadedElementsVector.contains(bna
												.getLabel())) {
									newEdges.put(key, entry);
								} else {
									// test edges by keys
									testEdges(key, key2, entry);
								}

								keggEdges.put(parent.getLabel()
										+ bna.getLabel(), edge);
							}

						} else {

							String key = parent.getLabel() + bna.getLabel();
							String key2 = bna.getLabel() + parent.getLabel();
							String[] entry = { parent.getLabel(),
									bna.getLabel(), "True", "NSO" };

							if (loadedElementsVector
									.contains(parent.getLabel())
									|| loadedElementsVector.contains(bna
											.getLabel())) {
								newEdges.put(key, entry);
							} else {
								// test edges by keys
								testEdges(key, key2, entry);
							}

							keggEdges.put(parent.getLabel() + bna.getLabel(),
									edge);
						}
					}

				}
				it = reactionProducts2.iterator();
				while (it.hasNext()) {
					BiologicalNodeAbstract bna = it.next();
					if (testRelation(parent, bna)) {
						if (organismSpecific) {
							String[] org = { this.organism };
							boolean sameOrganism = createTheEdgeDependendOnOrganism(
									org, parent, bna);
							if (sameOrganism) {
								String key = parent.getLabel() + bna.getLabel();
								String key2 = bna.getLabel()
										+ parent.getLabel();
								String[] entry = { parent.getLabel(),
										bna.getLabel(), "True", "SO" };

								if (loadedElementsVector.contains(parent
										.getLabel())
										|| loadedElementsVector.contains(bna
												.getLabel())) {
									newEdges.put(key, entry);
								} else {
									// test edges by keys
									testEdges(key, key2, entry);
								}
								keggEdges.put(parent.getLabel()
										+ bna.getLabel(), edge);
							} else {
								String key = parent.getLabel() + bna.getLabel();
								String key2 = bna.getLabel()
										+ parent.getLabel();
								String[] entry = { parent.getLabel(),
										bna.getLabel(), "True", "NSO" };

								if (loadedElementsVector.contains(parent
										.getLabel())
										|| loadedElementsVector.contains(bna
												.getLabel())) {
									newEdges.put(key, entry);
								} else {
									// test edges by keys
									testEdges(key, key2, entry);
								}
								keggEdges.put(parent.getLabel()
										+ bna.getLabel(), edge);
							}
						} else {
							String key = parent.getLabel() + bna.getLabel();
							String key2 = bna.getLabel() + parent.getLabel();
							String[] entry = { parent.getLabel(),
									bna.getLabel(), "True", "NSO" };

							if (loadedElementsVector
									.contains(parent.getLabel())
									|| loadedElementsVector.contains(bna
											.getLabel())) {
								newEdges.put(key, entry);
							} else {
								// test edges by keys
								testEdges(key, key2, entry);
							}
							keggEdges.put(parent.getLabel() + bna.getLabel(),
									edge);
						}
					}
				}
			}
		}

	}	
	
	private boolean testRelation(BiologicalNodeAbstract parent, BiologicalNodeAbstract bna)
	{
		boolean isRelated=false;
		String[] attributes={parent.getLabel()};
		ArrayList<DBColumn> relatedElements=null;
		
		if (parent.getDB().equalsIgnoreCase("KEGG"))
		{
			if (parent.getBiologicalElement().equals("Pathway Map"))
			{
				if (bna.getBiologicalElement().equals("Compound"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getCompoundFromPathway2, attributes);
				}
				else if (bna.getBiologicalElement().equals("Glycan"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getGlycanFromPathway, attributes);
				}
				else if (bna.getBiologicalElement().equals("Enzyme"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getEnzymeFromPathway, attributes);
				}
			}
			else if (parent.getBiologicalElement().equals("Enzyme"))
			{
				if (bna.getBiologicalElement().equals("Compound"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getCompoundFromEnzyme, attributes);
				}
				else if (bna.getBiologicalElement().equals("Glycan"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getGlycanFromEnzyme, attributes);
				}
			}
			else if (parent.getBiologicalElement().equals("Compound"))
			{
				if (bna.getBiologicalElement().equals("Reaction Pair"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getReactionPairFromCompound, attributes);
				}
				else if (bna.getBiologicalElement().equals("Enzyme"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getEnzymeFromCompound, attributes);
				}
			}
			else if (parent.getBiologicalElement().equals("Glycan"))
			{
				if (bna.getBiologicalElement().equals("Enzyme"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getEnzymeFromGlycan, attributes);
				}
			}
			else if (parent.getBiologicalElement().equals("Reaction Pair"))
			{
				if (bna.getBiologicalElement().equals("Compound"))
				{
					relatedElements=new Wrapper().requestDbContent(3, DAWISQueries.getCompoundFromReactionPair, attributes);
				}
			}
			
			for (DBColumn column : relatedElements)
			{
				String[] result=column.getColumn();
				
				if (result[0].equals(bna.getLabel()))
				{
					isRelated=true;
				}
			}

		}
		else if (parent.getDB().equalsIgnoreCase("Transpath"))
		{
			isRelated=true;
		}
		
		return isRelated;
	}
	
	// test relation between parent and bna from reaction
//	private boolean testRelation(BiologicalNodeAbstract parent,
//			BiologicalNodeAbstract bna) {
//		boolean isRelated = false;
//		String[] attributes = { parent.getLabel() };
//		Vector<String[]> relatedElements = null;
//		Iterator<String[]> it = null;
//		if (parent.getDB().equalsIgnoreCase("KEGG")) {
//			if (parent.getBiologicalElement().equals("Pathway Map")) {
//				if (bna.getBiologicalElement().equals("Compound")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getCompoundFromPathway2, attributes);
//				} else if (bna.getBiologicalElement().equals("Glycan")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getGlycanFromPathway, attributes);
//				} else if (bna.getBiologicalElement().equals("Enzyme")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getEnzymeFromPathway, attributes);
//				}
//			} else if (parent.getBiologicalElement().equals("Enzyme")) {
//				if (bna.getBiologicalElement().equals("Compound")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getCompoundFromEnzyme, attributes);
//				} else if (bna.getBiologicalElement().equals("Glycan")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getGlycanFromEnzyme, attributes);
//				}
//			} else if (parent.getBiologicalElement().equals("Compound")) {
//				if (bna.getBiologicalElement().equals("Reaction Pair")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getReactionPairFromCompound,
//							attributes);
//				} else if (bna.getBiologicalElement().equals("Enzyme")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getEnzymeFromCompound, attributes);
//				}
//			} else if (parent.getBiologicalElement().equals("Glycan")) {
//				if (bna.getBiologicalElement().equals("Enzyme")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getEnzymeFromGlycan, attributes);
//				}
//			} else if (parent.getBiologicalElement().equals("Reaction Pair")) {
//				if (bna.getBiologicalElement().equals("Compound")) {
//					relatedElements = new Wrapper().requestDbContent(3,
//							DAWISQueries.getCompoundFromReactionPair,
//							attributes);
//				}
//			}
//			it = relatedElements.iterator();
//			while (it.hasNext() && !isRelated) {
//				String[] result = it.next();
//				if (result[0].equals(bna.getLabel())) {
//					isRelated = true;
//				}
//			}
//
//		} else if (parent.getDB().equalsIgnoreCase("Transpath")) {
//			isRelated = true;
//		}
//		return isRelated;
//	}
	
	/**
	 * fill KEGGEdge with element information
	 * 
	 * @param edge
	 */
	private void fillKEGGEdgeWithElementInformation(KEGGEdge edge)
	{
		String[] elem={edge.getKEEGReactionID()};
		ArrayList<DBColumn> reactionDetails=new Wrapper().requestDbContent(3, DAWISQueries.getReactionDetails, elem);

		if (reactionDetails.size()>0)
		{
			for (DBColumn column : reactionDetails)
			{
				String[] det=column.getColumn();
				
				edge.setName(det[4]);
				edge.setRemark(det[6]);
				edge.setReference(det[5]);
				edge.setComment(det[1]);
				edge.setDefinition(det[2]);
				edge.setEquation(det[3]);
				edge.setRpair(det[7]);
			}
		}

		ArrayList<DBColumn> reactionType=new Wrapper().requestDbContent(3, DAWISQueries.getReactionType, elem);

		for (DBColumn column : reactionType)
		{
			String[] det=column.getColumn();
			edge.setReactionType(det[0]);
		}

		ArrayList<DBColumn> reactionOrthology=new Wrapper().requestDbContent(3, DAWISQueries.getReactionOrthology, elem);
		
		for (DBColumn column : reactionOrthology)
		{
			String[] det=column.getColumn();
			edge.setOrthology(det[1]+": "+det[2]);
		}
	}
	
//	/**
//	 * fill KEGGEdge with element information
//	 * 
//	 * @param edge
//	 */
//	private void fillKEGGEdgeWithElementInformation(KEGGEdge edge) {
//
//		String[] elem = { edge.getKEEGReactionID() };
//		Vector<String[]> reactionDetails = new Wrapper().requestDbContent(3,
//				DAWISQueries.getReactionDetails, elem);
//
//		Iterator<String[]> it;
//		if (reactionDetails.size() > 0) {
//			it = reactionDetails.iterator();
//			while (it.hasNext()) {
//				String[] det = (String[]) it.next();
//				edge.setName(det[4]);
//				edge.setRemark(det[6]);
//				edge.setReference(det[5]);
//				edge.setComment(det[1]);
//				edge.setDefinition(det[2]);
//				edge.setEquation(det[3]);
//				edge.setRpair(det[7]);
//			}
//		}
//
//		Vector reactionType = new Wrapper().requestDbContent(3,
//				DAWISQueries.getReactionType, elem);
//
//		it = reactionType.iterator();
//		while (it.hasNext()) {
//			String[] det = (String[]) it.next();
//			edge.setReactionType(det[0]);
//		}
//
//		Vector reactionOrthology = new Wrapper().requestDbContent(3,
//				DAWISQueries.getReactionOrthology, elem);
//		it = reactionOrthology.iterator();
//		while (it.hasNext()) {
//			String[] det = (String[]) it.next();
//			edge.setOrthology(det[1] + ": " + det[2]);
//		}
//	}

	/**
	 * create DAWISNode
	 * 
	 * @param bna
	 */
	private void createDAWISNode(BiologicalNodeAbstract bna) {

		String id = bna.getLabel();
		String db = bna.getDB();
		dawisNode = new DAWISNode(bna.getBiologicalElement());
		dawisNode.setID(id);
		dawisNode.setDB(db);
		dawisNode.addID(id, id);
		dawisNode.addIDDBRelation(db, id);

		bna.setDAWISNode(dawisNode);
	}

	/**
	 * create treeNode
	 * 
	 * @param bna
	 * @param parent
	 */
	private void createTreeNode(BiologicalNodeAbstract bna,
			BiologicalNodeAbstract parent) {

		String name = bna.getName();

		if (name.equals("")) {
			name = bna.getLabel();
		}

		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
		bna.setDefaultMutableTreeNode(newNode);

		if (parent == null) {
			tree.addNode(tree.getRoot(), newNode);
			bna.setOrganism(this.organism);
		} else {
			tree.addNode(parent.getDefaultMutableTreeNode(), newNode);
		}

	}
	
	/**
	 * get pathway number
	 * 
	 * @param lastID
	 * @return
	 */
	private String getPathwayNumber(String lastID)
	{

		String number=new String();
		String[] det={lastID};

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, DAWISQueries.getPathwayNumber, det);
		
		for (DBColumn column : results)
		{
			String[] numbers=column.getColumn();
			number=numbers[0];
		}

		return number;
	}
	
//	/**
//	 * get pathway number
//	 * 
//	 * @param lastID
//	 * @return
//	 */
//	private String getPathwayNumber(String lastID) {
//
//		String number = "";
//		String[] det = { lastID };
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3,
//				DAWISQueries.getPathwayNumber, det);
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] numbers = (String[]) it.next();
//			number = numbers[0];
//		}
//
//		return number;
//	}

	/**
	 * get name of the element
	 * 
	 * @param object
	 * @param id
	 * @return
	 */
	private String getName(String object, String id, String database)
	{

		Vector<String> names=new Vector<String>();

		String[] param={id};
		String query=new String();

		if (object.equals("Gene"))
		{
			if (database.equalsIgnoreCase("KEGG"))
			{
				query=DAWISQueries.getGeneName;
			}
			else if (database.equalsIgnoreCase("EMBL"))
			{
				query=DAWISQueries.getEMBLGeneName;
			}
			else if (database.equalsIgnoreCase("Transpath"))
			{
				query=DAWISQueries.getTPGeneName;
			}
			else if (database.equalsIgnoreCase("Transfac"))
			{
				query=DAWISQueries.getTFGeneName;
			}
		}
		else if (object.equals("Glycan"))
		{
			query=DAWISQueries.getGlycanName;
		}
		else if (object.equals("Drug"))
		{
			query=DAWISQueries.getDrugName;
		}
		else if (object.equals("Compound"))
		{
			if (database.equalsIgnoreCase("KEGG"))
			{
				query=DAWISQueries.getCompoundName;
			}
			else if (database.equalsIgnoreCase("Transpath"))
			{
				query=DAWISQueries.getTPCompoundName;
			}
		}

		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, param);
		
		for (DBColumn column : results)
		{
			String[] n=column.getColumn();
			names.add(n[0]);
		}

		return createString(names);

	}
	
//	/**
//	 * get name of the element
//	 * 
//	 * @param object
//	 * @param id
//	 * @return
//	 */
//	private String getName(String object, String id, String database) {
//
//		Vector<String> names = new Vector<String>();
//
//		String[] param = { id };
//		String query = "";
//
//		if (object.equals("Gene")) {
//			if (database.equalsIgnoreCase("KEGG")) {
//				query = DAWISQueries.getGeneName;
//			} else if (database.equalsIgnoreCase("EMBL")) {
//				query = DAWISQueries.getEMBLGeneName;
//			} else if (database.equalsIgnoreCase("Transpath")) {
//				query = DAWISQueries.getTPGeneName;
//			} else if (database.equalsIgnoreCase("Transfac")) {
//				query = DAWISQueries.getTFGeneName;
//			}
//		} else if (object.equals("Glycan")) {
//			query = DAWISQueries.getGlycanName;
//		} else if (object.equals("Drug")) {
//			query = DAWISQueries.getDrugName;
//		} else if (object.equals("Compound")) {
//			if (database.equalsIgnoreCase("KEGG")) {
//				query = DAWISQueries.getCompoundName;
//			} else if (database.equalsIgnoreCase("Transpath")) {
//				query = DAWISQueries.getTPCompoundName;
//			}
//		}
//
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query, param);
//		Iterator<String[]> it = results.iterator();
//		while (it.hasNext()) {
//			String[] n = (String[]) it.next();
//			names.add(n[0]);
//		}
//
//		return createString(names);
//
//	}

	private String createString(Vector<String> v) {
		String s = "";
		Iterator<String> it = v.iterator();
		boolean first = false;
		while (it.hasNext()) {
			if (!first) {
				s = s + it.next();
				first = true;
			} else {
				s = s + "; " + it.next();
			}
		}
		return s;
	}

	/**
	 * set search depth
	 * 
	 * @param searchDepth2
	 */
	public void setSearchDepth(int searchDepth2) {
		this.searchDepth = searchDepth2;
	}

	/**
	 * set specification
	 * 
	 * @param orgSpecific
	 */
	public void setOrganismSpecification(boolean orgSpecific) {
		organismSpecific = orgSpecific;
	}

	/**
	 * set organism of interest
	 * 
	 * @param org
	 */
	public void setOrganism(String org) {

		this.organism = org;
		this.synonyms = orgController.getOrganismSynonyms(this.organism);

		if (synonyms != null && synonyms.length > 0) {
			synonymsExist = true;
		}

	}

	public void setNewLoadedElements(Vector<String> selectedElements) {

		loadedElements = new HashSet<BiologicalNodeAbstract>();
		loadedElementsVector = selectedElements;

	}

	public HashSet<BiologicalNodeAbstract> getNewNodes() {
		return loadedElements;
	}

	public void drawNodes(Pathway pw) {

		myGraph = pw.getGraph();
		Iterator<BiologicalNodeAbstract> it = null;

		// draw nodes
		if (!loadedPath) {
			// draw nodes for fresh pathway
			if (loadedElements.isEmpty()) {
				if (!isDownload) {
					Iterator<String> i = allElements.keySet().iterator();
					i = allElements.keySet().iterator();

					while (i.hasNext()) {
						String key = i.next();

						Object temp_node = (BiologicalNodeAbstract) allElements
								.get(key);
						BiologicalNodeAbstract node = (BiologicalNodeAbstract) temp_node;
						node.setVertex(myGraph.createNewVertex());

						pw.addElement(temp_node);

						setPosition();
						myGraph.moveVertex(node.getVertex(), column * 150,
								row * 100);
					}
				}
			} else {
				if (isDownload) {
					// extend fresh pathway with selected elements
					it = loadedElements.iterator();
					pw.setNewLoadedNodes(loadedElements);
					while (it.hasNext()) {

						BiologicalNodeAbstract node = (BiologicalNodeAbstract) it
								.next();
						node.setVertex(myGraph.createNewVertex());

						pw.addElement(node);

						setPosition();
						myGraph.moveVertex(node.getVertex(), column * 150,
								row * 100);
					}
				} else {
					Iterator<String> i = allElements.keySet().iterator();
					i = allElements.keySet().iterator();

					while (i.hasNext()) {
						String key = i.next();

						Object temp_node = (BiologicalNodeAbstract) allElements
								.get(key);
						BiologicalNodeAbstract node = (BiologicalNodeAbstract) temp_node;
						node.setVertex(myGraph.createNewVertex());

						pw.addElement(temp_node);

						setPosition();
						myGraph.moveVertex(node.getVertex(), column * 150,
								row * 100);
					}
				}
			}
		} else {
			// extend loaded pathway with selected elements
			it = loadedElements.iterator();
			pw.setNewLoadedNodes(loadedElements);
			while (it.hasNext()) {

				BiologicalNodeAbstract node = (BiologicalNodeAbstract) it
						.next();
				node.setVertex(myGraph.createNewVertex());

				pw.addElement(node);

				setPosition();
				myGraph.moveVertex(node.getVertex(), column * 150, row * 100);
			}
		}
	}

	private void setPosition() {

		column++;
		if (column == 10) {
			row++;
			column = 0;
		}
	}

	public void drawEdges(Pathway pw) {

		Iterator it = null;
		adjazenzList = pw.getGraphRepresentation();
		edgeController = new EdgeController(pw);

		// draw edges for fresh pathway
		if (newEdges.isEmpty()) {

			it = edges.keySet().iterator();
			while (it.hasNext()) {
				while (it.hasNext()) {
					// edges to draw
					String key = (String) it.next();
					String[] entry = edges.get(key);
					draw(pw, entry);
				}
			}
		} else {
			// extend the pathway with new edges
			it = newEdges.keySet().iterator();
			while (it.hasNext()) {
				while (it.hasNext()) {
					// edges to draw
					String key = (String) it.next();
					String[] entry = newEdges.get(key);
					draw(pw, entry);
				}
			}
		}
	}

	private void draw(Pathway pw, String[] entry) {

		BiologicalNodeAbstract bna1 = (BiologicalNodeAbstract) pw
				.getNodeByLabel(entry[0]);
		BiologicalNodeAbstract bna2 = (BiologicalNodeAbstract) pw
				.getNodeByLabel(entry[1]);

		Vertex first = bna1.getVertex();
		Vertex second = bna2.getVertex();

		if (!adjazenzList.doesEdgeExist(first, second)) {

			String key1 = bna1.getLabel() + bna2.getLabel();
			String key2 = bna2.getLabel() + bna1.getLabel();

			ReactionPairEdge rpEdge = null;
			if (rPairEdges.containsKey(key1)) {
				rpEdge = rPairEdges.get(key1);
			} else if (rPairEdges.containsKey(key2)) {
				rpEdge = rPairEdges.get(key2);
			}

			KEGGEdge keggEdge = null;
			if (keggEdges.containsKey(key1)) {
				keggEdge = keggEdges.get(key1);
			} else if (keggEdges.containsKey(key2)) {
				keggEdge = keggEdges.get(key2);
			}

			if (entry[2].equals("True")) {

				if (keggEdge != null) {
					ReactionEdge reactionEdge = edgeController
							.createReactionEdge(first, second, keggEdge);
					if (entry[3].equals("SO")) {
						reactionEdge.setColor(Color.blue);
					}
				}

			} else {
				if (rpEdge != null) {
					ReactionPair rPairEdge = edgeController.createRPairEdge(
							first, second, rpEdge);
					if (entry[3].equals("SO")) {
						rPairEdge.setColor(Color.blue);
					}
				} else {
					if (entry[3].equals("SO")) {
						edgeController.buildReferencedEdge(bna1.getVertex(),
								bna2.getVertex());
					} else {
						edgeController.buildSimpleEdge(bna1.getVertex(), bna2
								.getVertex());
					}
				}
			}
		}
	}

	private void amendGeneNode(Gene g) {

		String tfGene = "";

		Hashtable<String, String> ht = g.getDAWISNode()
				.getAllIDDBRelationsAsHashtable();

		if (ht.containsKey("Transfac")) {
			tfGene = ht.get("Transfac");
		}

		if (!tfGene.equals("")) {
			getGeneFactor(g, tfGene);
			getGeneSite(g, tfGene);
		}

	}
	
	private void getGeneFactor(Gene gene, String tfGene)
	{

		String[] det={tfGene};
		String query=DAWISQueries.getTFGeneFactor2;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		if (results.size()>4)
		{
			collectInElementInformationWindow("Factor", gene, null, results, "Transfac");
		}
		else
		{
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				Factor factor=null;
				
				if (!allElements.contains(res[0]))
				{
					factor=new Factor(res[0], res[1], null);
					factor.setAbstract(false);
					factor.setReference(false);
					factor.setDB("Transfac");

					createDAWISNode(factor);

					if (isDownload)
					{
						if (loadedElementsVector.contains(res[0]))
						{
							if (!loadedElementsVector.contains(factor.getLabel()))
							{
								loadedElementsVector.add(factor.getLabel());
							}
							if (!loadedElements.contains(factor))
							{
								loadedElements.add(factor);
							}
						}
					}

					allElements.put(factor.getLabel(), factor);

				}
				else
				{
					factor=(Factor)allElements.get(res[0]);
				}

				// create simple grey edge
				String key=gene.getLabel()+factor.getLabel();
				String[] entry={gene.getLabel(), factor.getLabel(), "False", "NSO"};

				// store the edge in newEdges, if gene
				// was loaded from collectorNode
				if (loadedElementsVector.contains(gene.getLabel()))
				{
					newEdges.put(key, entry);
				}
				else
				{
					edges.put(key, entry);
				}

				getFactorSite(factor);
				getFactorFragment(factor);
			}
		}

	}
	
//	private void getGeneFactor(Gene gene, String tfGene) {
//
//		String[] det = { tfGene };
//		String query = DAWISQueries.getTFGeneFactor2;
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//		if (results.size() > 4) {
//			collectInElementInformationWindow("Factor", gene, null, results,
//					"Transfac");
//		} else {
//			while (it.hasNext()) {
//				String[] res = (String[]) it.next();
//				Factor factor = null;
//				if (!allElements.contains(res[0])) {
//					factor = new Factor(res[0], res[1], null);
//					factor.setAbstract(false);
//					factor.setReference(false);
//					factor.setDB("Transfac");
//
//					createDAWISNode(factor);
//
//					if (isDownload) {
//						if (loadedElementsVector.contains(res[0])) {
//							if (!loadedElementsVector.contains(factor
//									.getLabel())) {
//								loadedElementsVector.add(factor.getLabel());
//							}
//							if (!loadedElements.contains(factor)) {
//								loadedElements.add(factor);
//							}
//						}
//					}
//
//					allElements.put(factor.getLabel(), factor);
//
//				} else {
//					factor = (Factor) allElements.get(res[0]);
//				}
//
//				// create simple grey edge
//				String key = gene.getLabel() + factor.getLabel();
//				String[] entry = { gene.getLabel(), factor.getLabel(), "False",
//						"NSO" };
//
//				// store the edge in newEdges, if gene
//				// was loaded from collectorNode
//				if (loadedElementsVector.contains(gene.getLabel())) {
//					newEdges.put(key, entry);
//				} else {
//					edges.put(key, entry);
//				}
//
//				getFactorSite(factor);
//				getFactorFragment(factor);
//			}
//		}
//
//	}
	
	private void getFactorFragment(Factor factor)
	{

		String det1[]={factor.getLabel()};
		ArrayList<DBColumn> fragments=new Wrapper().requestDbContent(3, DAWISQueries.getFragmentsOfFactor2, det1);

		if (fragments.size()>4)
		{
			collectInElementInformationWindow("Fragment", factor, null, fragments, "Transfac");
		}
		else
		{
			for (DBColumn column : fragments)
			{
				String[] fragm=column.getColumn();
				Fragment fragment=null;
				
				if (!allElements.contains(fragm[0]))
				{
					fragment=new Fragment(fragm[0], "", null);
					fragment.setAbstract(false);
					fragment.setReference(false);
					fragment.setDB("Transfac");
					createDAWISNode(fragment);

					if (isDownload)
					{
						if (loadedElementsVector.contains(factor.getLabel()))
						{
							if (!loadedElementsVector.contains(fragm[0]))
							{
								loadedElementsVector.add(fragm[0]);
							}
						}
						if (!loadedElements.contains(fragment))
						{
							loadedElements.add(fragment);
						}
					}

					allElements.put(fragment.getLabel(), fragment);
				}
				else
				{
					fragment=(Fragment)allElements.get(fragm[0]);
				}

				// create simple grey edge
				String key=factor.getLabel()+fragment.getLabel();
				String[] entry={factor.getLabel(), fragment.getLabel(), "False", "NSO"};

				// store the edge in newEdges, if gene
				// was loaded from collectorNode
				if (loadedElementsVector.contains(factor.getLabel()))
				{
					newEdges.put(key, entry);
				}
				else
				{
					edges.put(key, entry);
				}

			}
		}
	}
	
//	private void getFactorFragment(Factor factor) {
//
//		String det1[] = { factor.getLabel() };
//		Vector<String[]> fragments = new Wrapper().requestDbContent(3,
//				DAWISQueries.getFragmentsOfFactor2, det1);
//
//		if (fragments.size() > 4) {
//			collectInElementInformationWindow("Fragment", factor, null,
//					fragments, "Transfac");
//		} else {
//			Iterator<String[]> it2 = fragments.iterator();
//			while (it2.hasNext()) {
//				String[] fragm = (String[]) it2.next();
//				Fragment fragment = null;
//				if (!allElements.contains(fragm[0])) {
//					fragment = new Fragment(fragm[0], "", null);
//					fragment.setAbstract(false);
//					fragment.setReference(false);
//					fragment.setDB("Transfac");
//					createDAWISNode(fragment);
//
//					if (isDownload) {
//						if (loadedElementsVector.contains(factor.getLabel())) {
//							if (!loadedElementsVector.contains(fragm[0])) {
//								loadedElementsVector.add(fragm[0]);
//							}
//						}
//						if (!loadedElements.contains(fragment)) {
//							loadedElements.add(fragment);
//						}
//					}
//
//					allElements.put(fragment.getLabel(), fragment);
//				} else {
//					fragment = (Fragment) allElements.get(fragm[0]);
//				}
//
//				// create simple grey edge
//				String key = factor.getLabel() + fragment.getLabel();
//				String[] entry = { factor.getLabel(), fragment.getLabel(),
//						"False", "NSO" };
//
//				// store the edge in newEdges, if gene
//				// was loaded from collectorNode
//				if (loadedElementsVector.contains(factor.getLabel())) {
//					newEdges.put(key, entry);
//				} else {
//					edges.put(key, entry);
//				}
//
//			}
//		}
//	}
	
	private void getFactorSite(Factor factor)
	{
		String[] attributes={factor.getLabel()};
		String query=DAWISQueries.getSiteOfFactor2;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, attributes);
		
		if (results.size()>4)
		{
			collectInElementInformationWindow("Site", factor, null, results, "Transfac");
		}
		else
		{
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				Site site=null;
				
				if (!allElements.contains(res[0]))
				{
					site=new Site(res[0], "", null);
					site.setAbstract(false);
					site.setReference(false);
					site.setDB("Transfac");
					createDAWISNode(site);

					if (isDownload)
					{
						if (loadedElementsVector.contains(factor.getLabel()))
						{
							if (!loadedElementsVector.contains(res[0]))
							{
								loadedElementsVector.add(res[0]);
							}
							if (!loadedElements.contains(site))
							{
								loadedElements.add(site);
							}
						}
					}

					allElements.put(site.getLabel(), site);
				}
				else
				{
					site=(Site)allElements.get(res[0]);
				}

				// create simple grey edge
				String key=factor.getLabel()+site.getLabel();
				String[] entry={factor.getLabel(), site.getLabel(), "False", "NSO"};

				// store the edge in newEdges, if gene
				// was loaded from collectorNode
				if (loadedElementsVector.contains(factor.getLabel()))
				{
					newEdges.put(key, entry);
				}
				else
				{
					edges.put(key, entry);
				}

			}
		}
	}
	
//	private void getFactorSite(Factor factor) {
//		String[] attributes = { factor.getLabel() };
//		String query = DAWISQueries.getSiteOfFactor2;
//		Vector<String[]> results = new Wrapper().requestDbContent(3, query,
//				attributes);
//		if (results.size() > 4) {
//			collectInElementInformationWindow("Site", factor, null, results,
//					"Transfac");
//		} else {
//			Iterator<String[]> it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				Site site = null;
//				if (!allElements.contains(res[0])) {
//					site = new Site(res[0], "", null);
//					site.setAbstract(false);
//					site.setReference(false);
//					site.setDB("Transfac");
//					createDAWISNode(site);
//
//					if (isDownload) {
//						if (loadedElementsVector.contains(factor.getLabel())) {
//							if (!loadedElementsVector.contains(res[0])) {
//								loadedElementsVector.add(res[0]);
//							}
//							if (!loadedElements.contains(site)) {
//								loadedElements.add(site);
//							}
//						}
//					}
//
//					allElements.put(site.getLabel(), site);
//				} else {
//					site = (Site) allElements.get(res[0]);
//				}
//
//				// create simple grey edge
//				String key = factor.getLabel() + site.getLabel();
//				String[] entry = { factor.getLabel(), site.getLabel(), "False",
//						"NSO" };
//
//				// store the edge in newEdges, if gene
//				// was loaded from collectorNode
//				if (loadedElementsVector.contains(factor.getLabel())) {
//					newEdges.put(key, entry);
//				} else {
//					edges.put(key, entry);
//				}
//
//			}
//		}
//	}
	
	private void getGeneSite(Gene gene, String tfGene)
	{
		String[] det={tfGene};
		String query=DAWISQueries.getTFGeneSiteAndFactor;
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(3, query, det);
		
		if (results.size()>4)
		{
			collectInElementInformationWindow("Site", gene, null, results, "Transfac");
		}
		else
		{
			for (DBColumn column : results)
			{
				String[] res=column.getColumn();
				Site site=null;
				
				if (!allElements.contains(res[0]))
				{
					site=new Site(res[0], "", null);
					site.setAbstract(false);
					site.setReference(false);
					site.setDB("Transfac");

					createDAWISNode(site);

					if (isDownload)
					{
						if (loadedElementsVector.contains(gene.getLabel()))
						{
							if (!loadedElementsVector.contains(res[0]))
							{
								loadedElementsVector.add(res[0]);
							}
							if (!loadedElements.contains(site))
							{
								loadedElements.add(site);
							}
						}
					}

					allElements.put(site.getLabel(), site);

				}
				else
				{
					site=(Site)allElements.get(res[0]);
				}

				// create simple grey edge
				String key=gene.getLabel()+site.getLabel();
				String[] entry={gene.getLabel(), site.getLabel(), "False", "NSO"};

				// store the edge in newEdges, if gene
				// was loaded from collectorNode
				if (loadedElementsVector.contains(gene.getLabel()))
				{
					newEdges.put(key, entry);
				}
				else
				{
					edges.put(key, entry);
				}
			}
		}
	}
	
//	private void getGeneSite(Gene gene, String tfGene) {
//
//		String[] det = { tfGene };
//		String query = DAWISQueries.getTFGeneSiteAndFactor;
//		Vector results = new Wrapper().requestDbContent(3, query, det);
//		Iterator it = results.iterator();
//		if (results.size() > 4) {
//			collectInElementInformationWindow("Site", gene, null, results,
//					"Transfac");
//		} else {
//			while (it.hasNext()) {
//				String[] res = (String[]) it.next();
//				Site site = null;
//				if (!allElements.contains(res[0])) {
//					site = new Site(res[0], "", null);
//					site.setAbstract(false);
//					site.setReference(false);
//					site.setDB("Transfac");
//
//					createDAWISNode(site);
//
//					if (isDownload) {
//						if (loadedElementsVector.contains(gene.getLabel())) {
//							if (!loadedElementsVector.contains(res[0])) {
//								loadedElementsVector.add(res[0]);
//							}
//							if (!loadedElements.contains(site)) {
//								loadedElements.add(site);
//							}
//						}
//					}
//
//					allElements.put(site.getLabel(), site);
//
//				} else {
//					site = (Site) allElements.get(res[0]);
//				}
//
//				// create simple grey edge
//				String key = gene.getLabel() + site.getLabel();
//				String[] entry = { gene.getLabel(), site.getLabel(), "False",
//						"NSO" };
//
//				// store the edge in newEdges, if gene
//				// was loaded from collectorNode
//				if (loadedElementsVector.contains(gene.getLabel())) {
//					newEdges.put(key, entry);
//				} else {
//					edges.put(key, entry);
//				}
//			}
//		}
//	}

	public void setIsDownload(boolean b) {
		isDownload = b;
	}

}
