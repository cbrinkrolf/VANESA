package xmlOutput.sbml;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.xml.XMLAttributes;
import org.sbml.jsbml.xml.XMLNode;
import org.sbml.jsbml.xml.XMLTriple;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class JSBMLoutput {

	/*
	 * the sbml document which has to be filled
	 */
	private File file = null;
	/*
	 * data from the graph
	 */
	private Pathway pathway = null;
	private static final double INITIALVALUE = 1.0;
	/*
	 * current version number
	 */
	private static final String VERSION = "1.0";
	private static final String COMP = "comp_";
	private static final String SPEC = "spec_";
	private static final String REAC = "reac_";

	public JSBMLoutput(File file, Pathway pathway) {
		this.file = file;
		this.pathway = pathway;
	}

	/**
	 * Generates a SBML document via jSBML.
	 */
	public String generateSBMLDocument() {
		// Create a new SBMLDocument object, using SBML Level 3 Version 1.
		SBMLDocument doc = new SBMLDocument(3, 1);
		Model model = doc.createModel("VANESA");

		Compartment compartment;
		// read all nodes from graph
		Iterator<BiologicalNodeAbstract> nodeIterator = this.pathway
				.getAllNodes().iterator();

		BiologicalNodeAbstract oneNode;
		// try {
		while (nodeIterator.hasNext()) {
			oneNode = nodeIterator.next();
			// test to what compartment the node belongs
			String nodeCompartment = COMP + oneNode.getCompartment();
			// test if compartment already exists
			Compartment testCompartment = model.getCompartment(nodeCompartment);
			if (testCompartment != null) {
				compartment = testCompartment;
			} else {
				// if there is no compartment it will be created here
				compartment = model.createCompartment();
				compartment.setId(nodeCompartment);
				compartment.setConstant(false);
			}
			// The ID of a species has to be a string and could not begin
			// with a number
			String str_id = SPEC + String.valueOf(oneNode.getID());
			// create species from current node
			Species spec = model.createSpecies(str_id, compartment);
			spec.setName(oneNode.getName());
			spec.setConstant(false);
			spec.setHasOnlySubstanceUnits(false);
			spec.setBoundaryCondition(false);
			spec.setInitialAmount(INITIALVALUE);
			spec.setInitialConcentration(INITIALVALUE);

			// create additional annotation and add it to the species
			Annotation a = createAnnotation(oneNode);
			spec.setAnnotation(a);
		}
		// } catch (Exception e) {
		// // if something went wrong, the user get's a notification
		// return "\nCreating SBML was not successful.";
		// }

		// reactions to sbml
		Iterator<BiologicalEdgeAbstract> edgeIterator = this.pathway
				.getAllEdges().iterator();

		BiologicalEdgeAbstract oneEdge;
		// try {
		while (edgeIterator.hasNext()) {
			// go through all edges to get their data
			oneEdge = edgeIterator.next();
			// The ID of a reaction has to be a string and could not begin with
			// a number
			String str_id = REAC + String.valueOf(oneEdge.getID());
			// create reaction from the current node
			Reaction reac = model.createReaction(str_id);
			reac.setFast(false);
			reac.setReversible(false);
			reac.setName(oneEdge.getName());

			// create additional annotation and add to the reaction
			Annotation a = createAnnotation(oneEdge);
			reac.setAnnotation(a);

			// search and assign products and reactants
			BiologicalNodeAbstract from = oneEdge.getFrom();
			BiologicalNodeAbstract to = oneEdge.getTo();
			// treat "to-nodes" (products)
			Species spec = doc.getModel().getSpecies(SPEC + to.getID());
			SpeciesReference subs = reac.createProduct(spec);
			subs.setConstant(false);
			// treat "from-nodes" (reactants)
			spec = doc.getModel().getSpecies(SPEC + from.getID());
			subs = reac.createReactant(spec);
			subs.setConstant(false);
			String nodeCompartment = spec.getCompartment();
			// choose compartment of the "from-nodes" and add the reactant
			reac.setCompartment(model.getCompartment(nodeCompartment));
		}
		// } catch (Exception e) {
		// // if something went wrong, the user get's a notification
		// return "\nCreating SBML was not successful.";
		// }

		// Write the SBML document to a file.
		try {
			SBMLWriter.write(doc, file, "VANESA", VERSION);
			return "\nExport was successful.";
		} catch (SBMLException e) {
			e.printStackTrace();
			return "\nWriting SBML file was not successful.";
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return "\nWriting SBML file was not successful.";
		} catch (IOException e) {
			e.printStackTrace();
			return "\nWriting SBML file was not successful.";
		}
	}

	// save additional data of the nodes
	private Annotation createAnnotation(BiologicalNodeAbstract oneNode) {
		// create new annotation
		Annotation a = new Annotation();
		// Save attributes that every node has
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("spec", "", ""),
				new XMLAttributes()));
		XMLNode elSub;

		String attr = oneNode.getLabel();
		el.addChild(createElSub(attr, "Label"));

		attr = String.valueOf(oneNode.getNodesize());
		el.addChild(createElSub(attr, "Nodesize"));

		attr = oneNode.getBiologicalElement();
		el.addChild(createElSub(attr, "BiologicalElement"));

		attr = oneNode.getComments();
		el.addChild(createElSub(attr, "Comments"));

		attr = String.valueOf(oneNode.getElementsVector());
		if (attr.equals("[]")) {
			attr = null;
		}
		el.addChild(createElSub(attr, "ElementsVector"));

		attr = String.valueOf(oneNode.getMicroarrayAttributes());
		if (attr.equals("[]")) {
			attr = null;
		}
		el.addChild(createElSub(attr, "MicroarrayAttributes"));

		attr = String.valueOf(oneNode.getSbml());
		el.addChild(createElSub(attr, "Sbml"));

		attr = String.valueOf(oneNode.getShape());
		el.addChild(createElSub(attr, "Shape"));

		attr = oneNode.getDescription();
		el.addChild(createElSub(attr, "Description"));

		attr = oneNode.getNetworklabel();
		el.addChild(createElSub(attr, "Networklabel"));

		attr = oneNode.getDB();
		el.addChild(createElSub(attr, "DB"));

		attr = oneNode.getOrganism();
		el.addChild(createElSub(attr, "Organism"));

		attr = String.valueOf(oneNode.getParentNode());
		el.addChild(createElSub(attr, "ParentNode"));

		attr = String.valueOf(oneNode.getPetriNetSimulationData());
		if (attr.equals("[]")) {
			attr = null;
		}
		el.addChild(createElSub(attr, "PetriNetSimulationData"));

		attr = String.valueOf(oneNode.getCollectorNodes());
		if (attr.equals("[]")) {
			attr = null;
		}
		el.addChild(createElSub(attr, "CollectorNodes"));

		boolean attrb = oneNode.hasKEGGNode();
		attr = String.valueOf(attrb);
		el.addChild(createElSub(attr, "HasKEGGNode"));

		// only if hasKEGGNode = true the following data should be saved.
		if (attrb && oneNode.getKEGGnode() != null) {
			String[] attrs = new String[63];
			attrs[0] = oneNode.getKEGGnode().getAllDBLinks();
			attrs[1] = oneNode.getKEGGnode().getAllGeneMotifs();
			attrs[2] = oneNode.getKEGGnode().getAllInvolvedElements();
			attrs[3] = oneNode.getKEGGnode().getAllNames();
			attrs[4] = oneNode.getKEGGnode().getAllPathwayLinks();
			attrs[5] = oneNode.getKEGGnode().getAllStructures();
			attrs[6] = oneNode.getKEGGnode().getBackgroundColour();
			attrs[7] = oneNode.getKEGGnode().getCompoundAtoms();
			attrs[8] = oneNode.getKEGGnode().getCompoundAtomsNr();
			attrs[9] = oneNode.getKEGGnode().getCompoundBondNr();
			attrs[10] = oneNode.getKEGGnode().getCompoundBonds();
			attrs[11] = oneNode.getKEGGnode().getCompoundComment();
			attrs[12] = oneNode.getKEGGnode().getCompoundFormula();
			attrs[13] = oneNode.getKEGGnode().getCompoundMass();
			attrs[14] = oneNode.getKEGGnode().getCompoundModule();
			attrs[15] = oneNode.getKEGGnode().getCompoundOrganism();
			attrs[16] = oneNode.getKEGGnode().getCompoundRemarks();
			attrs[17] = oneNode.getKEGGnode().getCompoundSequence();
			attrs[18] = oneNode.getKEGGnode().getForegroundColour();
			attrs[19] = oneNode.getKEGGnode().getGeneAAseq();
			attrs[20] = oneNode.getKEGGnode().getGeneAAseqNr();
			attrs[21] = oneNode.getKEGGnode().getGeneCodonUsage();
			attrs[22] = oneNode.getKEGGnode().getGeneDefinition();
			attrs[23] = oneNode.getKEGGnode().getGeneEnzyme();
			attrs[24] = oneNode.getKEGGnode().getGeneName();
			attrs[25] = oneNode.getKEGGnode().getGeneNtSeq();
			attrs[26] = oneNode.getKEGGnode().getGeneNtseqNr();
			attrs[27] = oneNode.getKEGGnode().getGeneOrthology();
			attrs[28] = oneNode.getKEGGnode().getGeneOrthologyName();
			attrs[29] = oneNode.getKEGGnode().getGenePosition();
			attrs[30] = oneNode.getKEGGnode().getGlycanBracket();
			attrs[31] = oneNode.getKEGGnode().getGlycanComposition();
			attrs[32] = oneNode.getKEGGnode().getGlycanEdge();
			attrs[33] = oneNode.getKEGGnode().getGlycanName();
			attrs[34] = oneNode.getKEGGnode().getGlycanNode();
			attrs[35] = oneNode.getKEGGnode().getGlycanOrthology();
			attrs[36] = oneNode.getKEGGnode().getHeight();
			attrs[37] = oneNode.getKEGGnode().getKeggcofactor();
			attrs[38] = oneNode.getKEGGnode().getKeggComment();
			attrs[39] = oneNode.getKEGGnode().getKEGGComponent();
			attrs[40] = oneNode.getKEGGnode().getKeggeffector();
			attrs[41] = oneNode.getKEGGnode().getKEGGentryID();
			attrs[42] = oneNode.getKEGGnode().getKEGGentryLink();
			attrs[43] = oneNode.getKEGGnode().getKEGGentryMap();
			attrs[44] = oneNode.getKEGGnode().getKEGGentryName();
			attrs[45] = oneNode.getKEGGnode().getKEGGentryReaction();
			attrs[46] = oneNode.getKEGGnode().getKEGGentryType();
			attrs[47] = oneNode.getKEGGnode().getKeggenzymeClass();
			attrs[48] = oneNode.getKEGGnode().getKeggorthology();
			attrs[49] = oneNode.getKEGGnode().getKEGGPathway();
			attrs[50] = oneNode.getKEGGnode().getKeggprodukt();
			attrs[51] = oneNode.getKEGGnode().getKeggreaction();
			attrs[52] = oneNode.getKEGGnode().getKeggreference();
			attrs[53] = oneNode.getKEGGnode().getKeggsubstrate();
			attrs[54] = oneNode.getKEGGnode().getKeggsysName();
			attrs[55] = oneNode.getKEGGnode().getNodeLabel();
			attrs[56] = oneNode.getKEGGnode().getShape();
			attrs[57] = oneNode.getKEGGnode().getWidth();
			attrs[58] = oneNode.getKEGGnode().getAllDBLinksAsVector()
					.toString();
			attrs[59] = oneNode.getKEGGnode().getAllGeneMotifsAsVector()
					.toString();
			attrs[60] = oneNode.getKEGGnode().getAllNamesAsVector().toString();
			attrs[61] = oneNode.getKEGGnode().getAllPathwayLinksAsVector()
					.toString();
			attrs[62] = oneNode.getKEGGnode().getAllStructuresAsVector()
					.toString();
			for (int i = 58; i <= 62; i++) {
				if (attrs[i].equals("[]")) {
					attrs[i] = null;
				}
			}
			boolean kegg = false;
			for (int i = 0; i < 63; i++) {
				if (attrs[i] != "" && attrs[i] != null) {
					kegg = true;
					break;
				}
			}
			if (kegg) {
				elSub = new XMLNode(new XMLNode(new XMLTriple("KEGGNode", "",
						""), new XMLAttributes()));
				// test which data are set to save them
				elSub.addChild(createElSub(attrs[0], "AllDBLinks"));
				elSub.addChild(createElSub(attrs[1], "AllGeneMotifs"));
				elSub.addChild(createElSub(attrs[2], "AllInvolvedElements"));
				elSub.addChild(createElSub(attrs[3], "AllNames"));
				elSub.addChild(createElSub(attrs[4], "AllPathwayLinks"));
				elSub.addChild(createElSub(attrs[5], "AllStructures"));
				elSub.addChild(createElSub(attrs[6], "BackgroundColour"));
				elSub.addChild(createElSub(attrs[7], "CompoundAtoms"));
				elSub.addChild(createElSub(attrs[8], "CompoundAtomsNr"));
				elSub.addChild(createElSub(attrs[9], "CompoundBondNr"));
				elSub.addChild(createElSub(attrs[10], "CompoundBonds"));
				elSub.addChild(createElSub(attrs[11], "CompoundComment"));
				elSub.addChild(createElSub(attrs[12], "CompoundFormula"));
				elSub.addChild(createElSub(attrs[13], "CompoundMass"));
				elSub.addChild(createElSub(attrs[14], "CompoundModule"));
				elSub.addChild(createElSub(attrs[15], "CompoundOrganism"));
				elSub.addChild(createElSub(attrs[16], "CompoundRemarks"));
				elSub.addChild(createElSub(attrs[17], "CompoundSequence"));
				elSub.addChild(createElSub(attrs[18], "ForegroundColour"));
				elSub.addChild(createElSub(attrs[19], "GeneAAseq"));
				elSub.addChild(createElSub(attrs[20], "GeneAAseqNr"));
				elSub.addChild(createElSub(attrs[21], "GeneCodonUsage"));
				elSub.addChild(createElSub(attrs[22], "GeneDefinition"));
				elSub.addChild(createElSub(attrs[23], "GeneEnzyme"));
				elSub.addChild(createElSub(attrs[24], "GeneName"));
				elSub.addChild(createElSub(attrs[25], "GeneNtSeq"));
				elSub.addChild(createElSub(attrs[26], "GeneNtSeqNr"));
				elSub.addChild(createElSub(attrs[27], "GeneOrthology"));
				elSub.addChild(createElSub(attrs[28], "GeneOrthologyName"));
				elSub.addChild(createElSub(attrs[29], "GenePosition"));
				elSub.addChild(createElSub(attrs[30], "GlycanBracket"));
				elSub.addChild(createElSub(attrs[31], "GlycanComposition"));
				elSub.addChild(createElSub(attrs[32], "GlycanEdge"));
				elSub.addChild(createElSub(attrs[33], "GlycanName"));
				elSub.addChild(createElSub(attrs[34], "GlycanNode"));
				elSub.addChild(createElSub(attrs[35], "GlycanOrthology"));
				elSub.addChild(createElSub(attrs[36], "Height"));
				elSub.addChild(createElSub(attrs[37], "Keggcofactor"));
				elSub.addChild(createElSub(attrs[38], "KeggComment"));
				elSub.addChild(createElSub(attrs[39], "KEGGComponent"));
				elSub.addChild(createElSub(attrs[40], "Keggeffector"));
				elSub.addChild(createElSub(attrs[41], "KEGGentryID"));
				elSub.addChild(createElSub(attrs[42], "KEGGentryLink"));
				elSub.addChild(createElSub(attrs[43], "KEGGentryMap"));
				elSub.addChild(createElSub(attrs[44], "KEGGentryName"));
				elSub.addChild(createElSub(attrs[45], "KEGGentryReaction"));
				elSub.addChild(createElSub(attrs[46], "KEGGentryType"));
				elSub.addChild(createElSub(attrs[47], "KeggenzymeClass"));
				elSub.addChild(createElSub(attrs[48], "Keggorthology"));
				elSub.addChild(createElSub(attrs[49], "KEGGPathway"));
				elSub.addChild(createElSub(attrs[50], "Keggprodukt"));
				elSub.addChild(createElSub(attrs[51], "Keggreaction"));
				elSub.addChild(createElSub(attrs[52], "Keggreference"));
				elSub.addChild(createElSub(attrs[53], "Keggsubstrate"));
				elSub.addChild(createElSub(attrs[54], "KeggsysName"));
				elSub.addChild(createElSub(attrs[55], "NodeLabel"));
				elSub.addChild(createElSub(attrs[56], "Shape"));
				elSub.addChild(createElSub(attrs[57], "Width"));
				elSub.addChild(createElSub(attrs[58], "AllDBLinksAsVector"));
				elSub.addChild(createElSub(attrs[59], "AllGeneMotifsAsVector"));
				elSub.addChild(createElSub(attrs[60], "AllNamesAsVector"));
				elSub.addChild(createElSub(attrs[61], "AllPathwayLinksAsVector"));
				elSub.addChild(createElSub(attrs[62], "AllStructuresAsVector"));

				el.addChild(elSub);
			}
		}

		attrb = oneNode.hasDAWISNode();
		attr = String.valueOf(attrb);
		el.addChild(createElSub(attr, "HasDAWISNode"));
		// only if hasDAWISNode = true the following data should be saved.
		if (attrb) {
			String[] attrs = new String[15];
			attrs[0] = oneNode.getDAWISNode().getAccessionnumber();
			attrs[1] = oneNode.getDAWISNode().getActivity();
			attrs[2] = oneNode.getDAWISNode().getAminoAcidSeq();
			attrs[3] = oneNode.getDAWISNode().getAminoAcidSeqLength();
			attrs[4] = oneNode.getDAWISNode().getAtoms();
			attrs[5] = oneNode.getDAWISNode().getAtomsNr();
			attrs[6] = oneNode.getDAWISNode().getBonds();
			attrs[7] = oneNode.getDAWISNode().getBondsNumber();
			attrs[8] = oneNode.getDAWISNode().getBracket();
			attrs[9] = oneNode.getDAWISNode().getCatalystsNames();
			attrs[10] = oneNode.getDAWISNode().getClassification();
			attrs[11] = oneNode.getDAWISNode().getCodonUsage();
			attrs[12] = oneNode.getDAWISNode().getCofactors();
			attrs[13] = oneNode.getDAWISNode().getCofactorsName();
			attrs[14] = oneNode.getDAWISNode().getComment();
			attrs[15] = oneNode.getDAWISNode().getComplexName();
			attrs[16] = oneNode.getDAWISNode().getComponent();
			attrs[17] = oneNode.getDAWISNode().getComposition();
			attrs[18] = oneNode.getDAWISNode().getDataLoadedAsString();
			attrs[19] = oneNode.getDAWISNode().getDataLoadedString();
			attrs[20] = oneNode.getDAWISNode().getDB();
			attrs[21] = oneNode.getDAWISNode().getDefinition();
			attrs[22] = oneNode.getDAWISNode().getDiagnosisType();
			attrs[23] = oneNode.getDAWISNode().getDisorder();
			attrs[24] = oneNode.getDAWISNode().getDomain();
			attrs[25] = oneNode.getDAWISNode().getEdge();
			attrs[26] = oneNode.getDAWISNode().getEffect();
			attrs[27] = oneNode.getDAWISNode().getEffectors();
			attrs[28] = oneNode.getDAWISNode().getEffectorsName();
			attrs[29] = oneNode.getDAWISNode().getElement();
			attrs[30] = oneNode.getDAWISNode().getEncodingGene();
			attrs[31] = oneNode.getDAWISNode().getEndPoint();
			attrs[32] = oneNode.getDAWISNode().getEquation();
			attrs[33] = oneNode.getDAWISNode().getFactorClass();
			attrs[34] = oneNode.getDAWISNode().getFeatures();
			attrs[35] = oneNode.getDAWISNode().getFormula();
			attrs[36] = oneNode.getDAWISNode().getGeneName();
			attrs[37] = oneNode.getDAWISNode().getID();
			attrs[38] = oneNode.getDAWISNode().getInformation();
			attrs[39] = oneNode.getDAWISNode().getInhibitors();
			attrs[40] = oneNode.getDAWISNode().getInhibitorsName();
			attrs[41] = oneNode.getDAWISNode().getIsoelectricPoint();
			attrs[42] = oneNode.getDAWISNode().getIsoformenNumber();
			attrs[43] = oneNode.getDAWISNode().getLocations();
			attrs[44] = oneNode.getDAWISNode().getMethods();
			attrs[45] = oneNode.getDAWISNode().getModule();
			attrs[46] = oneNode.getDAWISNode().getMotifs();
			attrs[47] = oneNode.getDAWISNode().getName();
			attrs[48] = oneNode.getDAWISNode().getNode();
			attrs[49] = oneNode.getDAWISNode().getNucleotidSequence();
			attrs[50] = oneNode.getDAWISNode().getNucleotidSequenceLength();
			attrs[51] = oneNode.getDAWISNode().getObject();
			attrs[52] = oneNode.getDAWISNode().getOntology();
			attrs[53] = oneNode.getDAWISNode().getOrganelle();
			attrs[54] = oneNode.getDAWISNode().getOrganism();
			attrs[55] = oneNode.getDAWISNode().getOriginal();
			attrs[56] = oneNode.getDAWISNode().getOrthology();
			attrs[57] = oneNode.getDAWISNode().getPathwayMap();
			attrs[58] = oneNode.getDAWISNode().getPDBs();
			attrs[59] = oneNode.getDAWISNode().getPosition();
			attrs[60] = oneNode.getDAWISNode().getProducts();
			attrs[61] = oneNode.getDAWISNode().getProductsName();
			attrs[62] = oneNode.getDAWISNode().getRDM();
			attrs[63] = oneNode.getDAWISNode().getReference();
			attrs[64] = oneNode.getDAWISNode().getRemarks();
			attrs[65] = oneNode.getDAWISNode().getRepeat();
			attrs[66] = oneNode.getDAWISNode().getSequenceSource();
			attrs[67] = oneNode.getDAWISNode().getSpecificityNeg();
			attrs[68] = oneNode.getDAWISNode().getSpecificityPos();
			attrs[69] = oneNode.getDAWISNode().getStartPoint();
			attrs[70] = oneNode.getDAWISNode().getSubfamilies();
			attrs[71] = oneNode.getDAWISNode().getSubstrates();
			attrs[72] = oneNode.getDAWISNode().getSubstratesName();
			attrs[73] = oneNode.getDAWISNode().getSuperfamilies();
			attrs[74] = oneNode.getDAWISNode().getSynonyms();
			attrs[75] = oneNode.getDAWISNode().getTarget();
			attrs[76] = oneNode.getDAWISNode().getTransfacGene();
			attrs[77] = oneNode.getDAWISNode().getType();
			attrs[78] = oneNode.getDAWISNode().getWeigth();
			attrs[79] = oneNode.getDAWISNode().getAccessionnumbersAsVector()
					.toString();
			attrs[80] = oneNode.getDAWISNode().getCatalystNamesAsVector()
					.toString();
			attrs[81] = oneNode.getDAWISNode().getClassificationAsVector()
					.toString();
			attrs[83] = oneNode.getDAWISNode().getCofactorsAsVector()
					.toString();
			attrs[82] = oneNode.getDAWISNode().getCofactorNamesAsVector()
					.toString();
			attrs[84] = oneNode.getDAWISNode().getDBLinksAsVector().toString();
			attrs[85] = oneNode.getDAWISNode().getDomainsAsVector().toString();
			attrs[86] = oneNode.getDAWISNode().getEffectorNamesAsVector()
					.toString();
			attrs[87] = oneNode.getDAWISNode().getEffectorsAsVector()
					.toString();
			attrs[88] = oneNode.getDAWISNode().getElementsAsVector().toString();
			attrs[89] = oneNode.getDAWISNode().getFeaturesAsVector().toString();
			attrs[90] = oneNode.getDAWISNode().getFunctionsAsVector()
					.toString();
			attrs[91] = oneNode.getDAWISNode().getGeneNamesAsVector()
					.toString();
			attrs[92] = oneNode.getDAWISNode().getInhibitorNamesAsVector()
					.toString();
			attrs[93] = oneNode.getDAWISNode().getListAsVector().toString();
			attrs[94] = oneNode.getDAWISNode().getLocationsAsVector()
					.toString();
			attrs[95] = oneNode.getDAWISNode().getMethodsAsSVector().toString();
			attrs[96] = oneNode.getDAWISNode().getMotifsAsVector().toString();
			attrs[97] = oneNode.getDAWISNode().getOrthologyAsVector()
					.toString();
			attrs[98] = oneNode.getDAWISNode().getPDBsAsVector().toString();
			attrs[90] = oneNode.getDAWISNode().getProductNamesAsVector()
					.toString();
			attrs[100] = oneNode.getDAWISNode().getProductsAsVector()
					.toString();
			attrs[101] = oneNode.getDAWISNode().getProzessesAsVector()
					.toString();
			attrs[102] = oneNode.getDAWISNode().getReferenceAsVector()
					.toString();
			attrs[103] = oneNode.getDAWISNode().getSubfamiliesAsVector()
					.toString();
			attrs[104] = oneNode.getDAWISNode().getSubstrateNamesAsVector()
					.toString();
			attrs[105] = oneNode.getDAWISNode().getSubstratesAsVector()
					.toString();

			for (int i = 79; i <= 105; i++) {
				if (attrs[i].equals("[]")) {
					attrs[i] = null;
				}
			}
			boolean dawis = false;
			for (int i = 0; i < 79; i++) {
				if (attrs[i] != "" && attrs[i] != null) {
					dawis = true;
					break;
				}
			}
			if (dawis) {
				elSub = new XMLNode(new XMLNode(new XMLTriple("DAWISNode", "",
						""), new XMLAttributes()));
				// test which data are set to save them
				elSub.addChild(createElSub(attrs[0], "Accessionnumber"));
				elSub.addChild(createElSub(attrs[1], "Activity"));
				elSub.addChild(createElSub(attrs[2], "AminoAcidSeq"));
				elSub.addChild(createElSub(attrs[3], "AminoAcidSeqLength"));
				elSub.addChild(createElSub(attrs[4], "Atoms"));
				elSub.addChild(createElSub(attrs[5], "AtomsNr"));
				elSub.addChild(createElSub(attrs[6], "Bonds"));
				elSub.addChild(createElSub(attrs[7], "BondsNumber"));
				elSub.addChild(createElSub(attrs[8], "Bracket"));
				elSub.addChild(createElSub(attrs[9], "CatalystsNames"));
				elSub.addChild(createElSub(attrs[10], "Classification"));
				elSub.addChild(createElSub(attrs[11], "CodonUsage"));
				elSub.addChild(createElSub(attrs[12], "Cofactors"));
				elSub.addChild(createElSub(attrs[13], "CofactorsName"));
				elSub.addChild(createElSub(attrs[14], "Comment"));
				elSub.addChild(createElSub(attrs[15], "ComplexName"));
				elSub.addChild(createElSub(attrs[16], "Component"));
				elSub.addChild(createElSub(attrs[17], "Composition"));
				elSub.addChild(createElSub(attrs[18], "DataLoadedAsString"));
				elSub.addChild(createElSub(attrs[19], "DataLoadedString"));
				elSub.addChild(createElSub(attrs[20], "DB"));
				elSub.addChild(createElSub(attrs[21], "Definition"));
				elSub.addChild(createElSub(attrs[22], "DiagnosisType"));
				elSub.addChild(createElSub(attrs[23], "Disorder"));
				elSub.addChild(createElSub(attrs[25], "Domain"));
				elSub.addChild(createElSub(attrs[26], "Edge"));
				elSub.addChild(createElSub(attrs[27], "Effect"));
				elSub.addChild(createElSub(attrs[28], "Effectors"));
				elSub.addChild(createElSub(attrs[29], "EffectorsName"));
				elSub.addChild(createElSub(attrs[30], "Element"));
				elSub.addChild(createElSub(attrs[31], "EncodingGene"));
				elSub.addChild(createElSub(attrs[32], "EndPoint"));
				elSub.addChild(createElSub(attrs[33], "Equation"));
				elSub.addChild(createElSub(attrs[34], "FactorClass"));
				elSub.addChild(createElSub(attrs[35], "Features"));
				elSub.addChild(createElSub(attrs[36], "GeneName"));
				elSub.addChild(createElSub(attrs[37], "ID"));
				elSub.addChild(createElSub(attrs[38], "Information"));
				elSub.addChild(createElSub(attrs[39], "Inhibitors"));
				elSub.addChild(createElSub(attrs[40], "InhibitorsName"));
				elSub.addChild(createElSub(attrs[41], "IsoelectricPoint"));
				elSub.addChild(createElSub(attrs[42], "IsoformenNumber"));
				elSub.addChild(createElSub(attrs[43], "Location"));
				elSub.addChild(createElSub(attrs[44], "Methods"));
				elSub.addChild(createElSub(attrs[45], "Module"));
				elSub.addChild(createElSub(attrs[46], "Motifs"));
				elSub.addChild(createElSub(attrs[47], "Name"));
				elSub.addChild(createElSub(attrs[48], "Node"));
				elSub.addChild(createElSub(attrs[49], "Locations"));
				elSub.addChild(createElSub(attrs[50], "NucleotidSequence"));
				elSub.addChild(createElSub(attrs[51], "NucleotidSequenceLenght"));
				elSub.addChild(createElSub(attrs[52], "Object"));
				elSub.addChild(createElSub(attrs[53], "OntologyOrgnelle"));
				elSub.addChild(createElSub(attrs[54], "Organism"));
				elSub.addChild(createElSub(attrs[55], "Original"));
				elSub.addChild(createElSub(attrs[56], "Orthology"));
				elSub.addChild(createElSub(attrs[57], "PathwayMap"));
				elSub.addChild(createElSub(attrs[58], "PDBs"));
				elSub.addChild(createElSub(attrs[59], "Position"));
				elSub.addChild(createElSub(attrs[60], "Products"));
				elSub.addChild(createElSub(attrs[61], "ProductsName"));
				elSub.addChild(createElSub(attrs[62], "RDM"));
				elSub.addChild(createElSub(attrs[63], "Reference"));
				elSub.addChild(createElSub(attrs[64], "Remarks"));
				elSub.addChild(createElSub(attrs[65], "Repeat"));
				elSub.addChild(createElSub(attrs[66], "SequenceSource"));
				elSub.addChild(createElSub(attrs[67], "SpecificityNeg"));
				elSub.addChild(createElSub(attrs[68], "SpecificityPos"));
				elSub.addChild(createElSub(attrs[69], "StartPoint"));
				elSub.addChild(createElSub(attrs[70], "Subfamilies"));
				elSub.addChild(createElSub(attrs[71], "Substrates"));
				elSub.addChild(createElSub(attrs[72], "SubstratesName"));
				elSub.addChild(createElSub(attrs[73], "Superfamilies"));
				elSub.addChild(createElSub(attrs[74], "Synonyms"));
				elSub.addChild(createElSub(attrs[75], "Target"));
				elSub.addChild(createElSub(attrs[76], "TransfacGene"));
				elSub.addChild(createElSub(attrs[77], "Type"));
				elSub.addChild(createElSub(attrs[78], "Weight"));
				elSub.addChild(createElSub(attrs[79],
						"AccessionnumbersAsVector"));
				elSub.addChild(createElSub(attrs[80], "CatalystNamesAsVector"));
				elSub.addChild(createElSub(attrs[81], "ClassificationAsVector"));
				elSub.addChild(createElSub(attrs[82], "CofactorNamesAsVector"));
				elSub.addChild(createElSub(attrs[83], "CofactorsAsVector"));
				elSub.addChild(createElSub(attrs[84], "DBLinksAsVector"));
				elSub.addChild(createElSub(attrs[85], "DomainsAsVector"));
				elSub.addChild(createElSub(attrs[86], "EffectorNamesAsVector"));
				elSub.addChild(createElSub(attrs[87], "EffectorsAsVector"));
				elSub.addChild(createElSub(attrs[88], "ElementsAsVector"));
				elSub.addChild(createElSub(attrs[89], "FeaturesAsVector"));
				elSub.addChild(createElSub(attrs[90], "FunctionsAsVector"));
				elSub.addChild(createElSub(attrs[91], "GeneNamesAsVector"));
				elSub.addChild(createElSub(attrs[92], "InhibitorNamesAsVector"));
				elSub.addChild(createElSub(attrs[93], "ListAsVector"));
				elSub.addChild(createElSub(attrs[94], "LocationAsVector"));
				elSub.addChild(createElSub(attrs[95], "MethodsAsVector"));
				elSub.addChild(createElSub(attrs[96], "MotifsAsVector"));
				elSub.addChild(createElSub(attrs[97], "OrthologyAsVector"));
				elSub.addChild(createElSub(attrs[98], "PBDAsVector"));
				elSub.addChild(createElSub(attrs[99], "ProductNamesAsVector"));
				elSub.addChild(createElSub(attrs[100], "ProductsAsVector"));
				elSub.addChild(createElSub(attrs[101], "ProcessesAsVector"));
				elSub.addChild(createElSub(attrs[102], "ReferencesAsVector"));
				elSub.addChild(createElSub(attrs[103], "SubfamiliesAsVector"));
				elSub.addChild(createElSub(attrs[104], "SubstrateNameAsVector"));
				elSub.addChild(createElSub(attrs[105], "SubstratesAsVector"));

				el.addChild(elSub);
			}
		}

		Color col = oneNode.getColor();
		if (col != null) {
			elSub = new XMLNode(new XMLNode(new XMLTriple("Color", "", ""),
					new XMLAttributes()));
			XMLNode elSubSub = new XMLNode(new XMLNode(new XMLTriple("RGB", "",
					""), new XMLAttributes()));
			elSubSub.addAttr("RGB", "" + col.getRGB());
			elSub.addChild(elSubSub);
			elSubSub = new XMLNode(new XMLNode(new XMLTriple("Blue", "", ""),
					new XMLAttributes()));
			elSubSub.addAttr("Blue", "" + col.getBlue());
			elSub.addChild(elSubSub);
			elSubSub = new XMLNode(new XMLNode(new XMLTriple("Green", "", ""),
					new XMLAttributes()));
			elSubSub.addAttr("Green", "" + col.getGreen());
			elSub.addChild(elSubSub);
			elSubSub = new XMLNode(new XMLNode(new XMLTriple("Red", "", ""),
					new XMLAttributes()));
			elSubSub.addAttr("Red", "" + col.getRed());
			elSub.addChild(elSubSub);
			el.addChild(elSub);
		}

		// test which type the node is to save additional data
		if (oneNode instanceof biologicalObjects.nodes.CollectorNode) {
			if (((biologicalObjects.nodes.CollectorNode) oneNode).getParent() != null) {
				attr = ((biologicalObjects.nodes.CollectorNode) oneNode)
						.getParent().toString();
				el.addChild(createElSub(attr, "Parent"));
			}
			if (((biologicalObjects.nodes.CollectorNode) oneNode)
					.getParentTreeNode() != null) {
				attr = ((biologicalObjects.nodes.CollectorNode) oneNode)
						.getParentTreeNode().toString();
				el.addChild(createElSub(attr, "ParentTreeNode"));
			}
			attr = ((biologicalObjects.nodes.CollectorNode) oneNode)
					.getObject();
			el.addChild(createElSub(attr, "ElementObject"));

		} else if (oneNode instanceof biologicalObjects.nodes.Complex) {
			attr = ((biologicalObjects.nodes.Complex) oneNode).getAllElements()
					.toString();
			if (attr.equals("[]")) {
				attr = null;
			}
			el.addChild(createElSub(attr, "AllElements"));

		} else if (oneNode instanceof biologicalObjects.nodes.DNA) {
			attr = ((biologicalObjects.nodes.DNA) oneNode).getNtSequence();
			el.addChild(createElSub(attr, "NtSequence"));

		} else if (oneNode instanceof biologicalObjects.nodes.Enzyme) {
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getCofactor();
			el.addChild(createElSub(attr, "Cofactor"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getEffector();
			el.addChild(createElSub(attr, "Effector"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getEnzymeClass();
			el.addChild(createElSub(attr, "EnzymeClass"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getOrthology();
			el.addChild(createElSub(attr, "Orthology"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getProdukt();
			el.addChild(createElSub(attr, "Produkt"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getReaction();
			el.addChild(createElSub(attr, "Reaction"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getReference();
			el.addChild(createElSub(attr, "Reference"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getSubstrate();
			el.addChild(createElSub(attr, "Substrate"));
			attr = ((biologicalObjects.nodes.Enzyme) oneNode).getSysName();
			el.addChild(createElSub(attr, "SysName"));

		} else if (oneNode instanceof biologicalObjects.nodes.Gene) {

			attr = ((biologicalObjects.nodes.Gene) oneNode).getNtSequence();
			el.addChild(createElSub(attr, "NtSequence"));

			attr = ((biologicalObjects.nodes.Gene) oneNode).getProteins()
					.toString();
			if (attr.equals("[]")) {
				attr = null;
			}
			el.addChild(createElSub(attr, "Proteins"));
			attr = ((biologicalObjects.nodes.Gene) oneNode).getEnzymes()
					.toString();
			if (attr.equals("[]")) {
				attr = null;
			}
			el.addChild(createElSub(attr, "Enzymes"));

		} else if (oneNode instanceof biologicalObjects.nodes.PathwayMap) {
			attr = String
					.valueOf(((biologicalObjects.nodes.PathwayMap) oneNode)
							.getSpecification());
			el.addChild(createElSub(attr, "Specification"));
			if (((biologicalObjects.nodes.PathwayMap) oneNode).getPathwayLink() != null) {
				attr = ((biologicalObjects.nodes.PathwayMap) oneNode)
						.getPathwayLink().toString();
				el.addChild(createElSub(attr, "PathwayLink"));
			}
		} else if (oneNode instanceof biologicalObjects.nodes.Protein) {
			attr = ((biologicalObjects.nodes.Protein) oneNode).getAaSequence();
			el.addChild(createElSub(attr, "AaSequence"));

		} else if (oneNode instanceof biologicalObjects.nodes.RNA) {
			attr = ((biologicalObjects.nodes.RNA) oneNode).getNtSequence();
			el.addChild(createElSub(attr, "NtSequence"));

		} else if (oneNode instanceof biologicalObjects.nodes.SmallMolecule) {
			attr = ((biologicalObjects.nodes.SmallMolecule) oneNode)
					.getFormula();
			el.addChild(createElSub(attr, "Formula"));
			attr = ((biologicalObjects.nodes.SmallMolecule) oneNode).getMass();
			el.addChild(createElSub(attr, "Mass"));

		} else if (oneNode instanceof biologicalObjects.nodes.SRNA) {
			attr = ((biologicalObjects.nodes.SRNA) oneNode)
					.getTarbase_accession();
			el.addChild(createElSub(attr, "Tarbase_accession"));
			attr = ((biologicalObjects.nodes.SRNA) oneNode).getTarbase_DS();
			el.addChild(createElSub(attr, "Tarbase_DS"));
			attr = ((biologicalObjects.nodes.SRNA) oneNode)
					.getTarbase_ensemble();
			el.addChild(createElSub(attr, "Tarbase_ensemble"));
			attr = ((biologicalObjects.nodes.SRNA) oneNode).getTarbase_IS();
			el.addChild(createElSub(attr, "Tarbase_IS"));
		}

		a.appendNoRDFAnnotation(el.toXMLString());
		return a;
	}

	// Save additional data of the edges
	private Annotation createAnnotation(BiologicalEdgeAbstract oneEdge) {
		Annotation a = new Annotation();
		// Save the attributes which every edge has
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("reac", "", ""),
				new XMLAttributes()));
		XMLNode elSub;
		String attr = oneEdge.getLabel();
		el.addChild(createElSub(attr, "Label"));
		attr = String.valueOf(oneEdge.isWeighted());
		el.addChild(createElSub(attr, "IsWeighted"));
		attr = String.valueOf(oneEdge.getWeight());
		el.addChild(createElSub(attr, "Weight"));
		Color col = oneEdge.getColor();
		if (col != null) {
			elSub = new XMLNode(new XMLNode(new XMLTriple("Color", "", ""),
					new XMLAttributes()));
			attr = String.valueOf(col.getRGB());
			elSub.addChild(createElSub(attr, "RGB"));
			attr = String.valueOf(col.getBlue());
			elSub.addChild(createElSub(attr, "Blue"));
			attr = String.valueOf(col.getGreen());
			elSub.addChild(createElSub(attr, "Green"));
			attr = String.valueOf(col.getRed());
			elSub.addChild(createElSub(attr, "Red"));
			el.addChild(elSub);
		}
		attr = String.valueOf(oneEdge.isDirected());
		el.addChild(createElSub(attr, "IsDirected"));
		attr = oneEdge.getBiologicalElement();
		el.addChild(createElSub(attr, "BiologicalElement"));
		attr = oneEdge.getDescription();
		el.addChild(createElSub(attr, "Description"));
		attr = oneEdge.getComments();
		el.addChild(createElSub(attr, "Comments"));
		attr = String.valueOf(oneEdge.hasFeatureEdge());
		el.addChild(createElSub(attr, "HasFeatureEdge"));

		boolean attrb = oneEdge.hasKEGGEdge();
		attr = String.valueOf(attrb);
		el.addChild(createElSub(attr, "HasKEGGEdge"));

		// Test if hasKEGGEdge = true
		// only if the following data has to be saved
		if (attrb && oneEdge.getKeggEdge() != null) {
			String[] attrs = new String[23];
			attrs[0] = oneEdge.getKeggEdge().getKEEGReactionID();
			attrs[1] = oneEdge.getKeggEdge().getEntry1();
			attrs[2] = oneEdge.getKeggEdge().getEntry2();
			attrs[3] = oneEdge.getKeggEdge().getType();
			attrs[4] = oneEdge.getKeggEdge().getDescription();
			attrs[5] = oneEdge.getKeggEdge().getName();
			attrs[6] = oneEdge.getKeggEdge().getRemark();
			attrs[7] = oneEdge.getKeggEdge().getOrthology();
			attrs[8] = oneEdge.getKeggEdge().getReference();
			attrs[9] = oneEdge.getKeggEdge().getComment();
			attrs[10] = oneEdge.getKeggEdge().getDefinition();
			attrs[11] = oneEdge.getKeggEdge().getEquation();
			attrs[12] = oneEdge.getKeggEdge().getRpair();
			attrs[13] = oneEdge.getKeggEdge().getEffect();
			attrs[14] = oneEdge.getKeggEdge().getReactionType();
			attrs[15] = oneEdge.getKeggEdge().getInvolvedEnzyme();
			// Save vectors
			attrs[16] = oneEdge.getKeggEdge().getAllProducts().toString();
			attrs[17] = oneEdge.getKeggEdge().getAllEnzymes().toString();
			attrs[18] = oneEdge.getKeggEdge().getAllSubstrates().toString();
			attrs[19] = oneEdge.getKeggEdge().getCatalystsAsVector().toString();
			attrs[20] = oneEdge.getKeggEdge().getCatalystNamesAsVector()
					.toString();
			attrs[21] = oneEdge.getKeggEdge().getInhibitorsAsVector()
					.toString();
			attrs[22] = oneEdge.getKeggEdge().getInhibitorNamesAsVector()
					.toString();

			// If the vector is empty it should not be saved
			for (int i = 16; i < 23; i++) {
				if (attrs[i].equals("[]")) {
					attrs[i] = null;
				}
			}

			// Test if min. 1 attribute is set
			// only if the XML-Node has to be created
			boolean kegg = false;
			for (int i = 0; i < 23; i++) {
				if (attrs[i] != "" && attrs[i] != null) {
					kegg = true;
					break;
				}
			}
			if (kegg) {
				// Test which attributes are set and save them
				elSub = new XMLNode(new XMLNode(new XMLTriple("KEGGEdge", "",
						""), new XMLAttributes()));
				// Names of the attributes in the appropriate order
				String[] names = { "KEEGReactionID", "Entry1", "Entry2",
						"Type", "Description", "Name", "Remark", "Orthology",
						"Reference", "Comment", "Definition", "Equation",
						"Rpair", "Effect", "ReactionType", "InvolvedEnzyme",
						"AllProducts", "AllEnzymes", "AllSubstrates",
						"Catalysts", "CatalystNames", "Inhibitors",
						"InhibitorNames", };
				for (int i = 0; i < 23; i++) {
					elSub.addChild(createElSub(attrs[i], names[i]));
				}

				el.addChild(elSub);
			}
		}
		// Save additional data
		if (oneEdge instanceof biologicalObjects.edges.ReactionPair) {
			attrb = ((biologicalObjects.edges.ReactionPair) oneEdge)
					.hasRPairEdge();
			attr = String.valueOf(attrb);
			if (attr != "" && attr != null) {
				el.addChild(createElSub(attr, "HasRPairEdge"));
			}
			// Test if hasRPairEdge = true
			// only if the following data has to be saved
			if (attrb) {
				// Data to save
				String[] attrs = new String[3];
				attrs[0] = ((biologicalObjects.edges.ReactionPair) oneEdge)
						.getReactionPairEdge().getReactionPairID();
				attrs[1] = ((biologicalObjects.edges.ReactionPair) oneEdge)
						.getReactionPairEdge().getName();
				attrs[2] = ((biologicalObjects.edges.ReactionPair) oneEdge)
						.getReactionPairEdge().getType();
				// Test if one of the attributes is set
				// if not, no node has to be created
				boolean kegg = false;
				for (int i = 0; i < 3; i++) {
					if (attrs[i] != "" && attrs[i] != null) {
						kegg = true;
						break;
					}
				}
				if (kegg) {
					elSub = new XMLNode(new XMLNode(new XMLTriple(
							"ReactionPairEdge", "", ""), new XMLAttributes()));
					// Test which attributes are set
					elSub.addChild(createElSub(attrs[0], "ReactionPairEdgeID"));
					elSub.addChild(createElSub(attrs[1], "ReactionPairName"));
					elSub.addChild(createElSub(attrs[2], "ReactionPairType"));

					el.addChild(elSub);
				}
			}
		}
		a.appendNoRDFAnnotation(el.toXMLString());
		return a;
	}

	private XMLNode createElSub(String attr, String name) {
		if (attr != "" && attr != null && !attr.equals("null")) {
			XMLNode elSub = new XMLNode(new XMLNode(
					new XMLTriple(name, "", ""), new XMLAttributes()));
			elSub.addAttr(name, attr);
			return elSub;
		} else {
			return null;
		}
	}
}
