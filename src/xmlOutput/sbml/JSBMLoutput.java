package xmlOutput.sbml;

import graph.gui.Parameter;
import gui.RangeSelector;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.StochasticTransition;
import petriNet.Transition;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.MicroArrayAttributes;

/**
 * This class represents a writer from graph data to a SBML file. The actual
 * version supports SBML Level 3 Version 1 only by jSBML.
 * 
 * @author Annika and Sandra
 * 
 */
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
		String message = "";
		// Create a new SBMLDocument object, using SBML Level 3 Version 1.
		SBMLDocument doc = new SBMLDocument(3, 1);
		Model model = doc.createModel("VANESA");

		// create additional annotation and add it to the model
		Annotation a = createAnnotation();
		model.setAnnotation(a);

		Compartment compartment;
		// read all nodes from graph
		Iterator<BiologicalNodeAbstract> nodeIterator = this.pathway
				.getAllNodes().iterator();

		BiologicalNodeAbstract oneNode;
		try {
			while (nodeIterator.hasNext()) {
				oneNode = nodeIterator.next();
				// test to what compartment the node belongs
				String nodeCompartment = COMP + oneNode.getCompartment();
				// test if compartment already exists
				Compartment testCompartment = model
						.getCompartment(nodeCompartment);
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
				a = createAnnotation(oneNode);
				spec.setAnnotation(a);
			}
		} catch (Exception e) {
			// if something went wrong, the user get's a notification
			message = "\nCreating SBML was not successful.";
		}

		// reactions to sbml
		Iterator<BiologicalEdgeAbstract> edgeIterator = this.pathway
				.getAllEdges().iterator();

		BiologicalEdgeAbstract oneEdge;
		try {
			while (edgeIterator.hasNext()) {
				// go through all edges to get their data
				oneEdge = edgeIterator.next();
				// The ID of a reaction has to be a string and could not begin
				// with
				// a number
				String str_id = REAC + String.valueOf(oneEdge.getID());
				// create reaction from the current node
				Reaction reac = model.createReaction(str_id);
				reac.setFast(false);
				reac.setReversible(false);
				reac.setName(oneEdge.getName());

				// create additional annotation and add to the reaction
				a = createAnnotation(oneEdge);
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
		} catch (Exception e) {
			// if something went wrong, the user get's a notification
			message = "\nCreating SBML was not successful.";
		}

		// Write the SBML document to a file.
		try {
			SBMLWriter.write(doc, file, "VANESA", VERSION);
			message = "\nExport was successful.";
		} catch (SBMLException e) {
			e.printStackTrace();
			message = "\nWriting SBML file was not successful.";
		} catch (XMLStreamException e) {
			e.printStackTrace();
			message = "\nWriting SBML file was not successful.";
		} catch (IOException e) {
			e.printStackTrace();
			message = "\nWriting SBML file was not successful.";
		}
		return message;
	}

	private Annotation createAnnotation() {
		Annotation a = new Annotation();
		// Save Shape
		List<Map<String, String>> rangeInfos = RangeSelector.getInstance()
				.getRangesInMyGraph(pathway.getGraph());
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("model", "", ""),
				new XMLAttributes()));
		if (rangeInfos != null) {
			XMLNode elSub = new XMLNode(new XMLNode(new XMLTriple(
					"listOfRanges", "", ""), new XMLAttributes()));
			for (Map<String, String> range : rangeInfos) {
				XMLNode elSubSub = new XMLNode(new XMLNode(new XMLTriple(
						"Range", "", ""), new XMLAttributes()));
				for (String key : range.keySet()) {
					String value = range.get(key);
					elSubSub.addChild(createElSub(value, key));
				}
				elSub.addChild(elSubSub);
			}
			el.addChild(elSub);
		}
		String attr = String.valueOf(pathway.isPetriNet());
		el.addChild(createElSub(attr, "isPetriNet"));
		a.appendNoRDFAnnotation(el.toXMLString());
		return a;
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

		Point2D p = pathway.getGraph().getVertexLocation(oneNode);
		elSub = new XMLNode(new XMLNode(new XMLTriple("Coordinates", "", ""),
				new XMLAttributes()));
		attr = String.valueOf(p.getX());
		elSub.addChild(createElSub(attr, "x_Coordinate"));
		attr = String.valueOf(p.getY());
		elSub.addChild(createElSub(attr, "y_Coordinate"));
		el.addChild(elSub);
		
		XMLNode elSubSub;
		elSub = new XMLNode(new XMLNode(new XMLTriple("Parameters","",""), new XMLAttributes()));
		
		Parameter param;
		Iterator<Parameter> it = oneNode.getParameters().iterator();
		while(it.hasNext()){
			param = it.next();
			elSubSub = new XMLNode(new XMLNode(new XMLTriple("Parameter","",""), new XMLAttributes()));
			elSubSub.addChild(createElSub(param.getName(), "Name"));
			elSubSub.addChild(createElSub(param.getValue()+"", "Value"));
			elSubSub.addChild(createElSub(param.getUnit(), "Unit"));
			elSub.addChild(elSubSub);
		}
		
		
		
		//attr = "bla";
		//elSub.addChild(createElSub(attr, "parameter"));
		
		//elSub.addChild(elSub);
		el.addChild(elSub);

		attr = oneNode.getComments();
		el.addChild(createElSub(attr, "Comments"));

		attr = String.valueOf(oneNode.getElementsVector());
		el.addChild(createElSub(attr, "ElementsVector"));

		attr = oneNode.getDescription();
		el.addChild(createElSub(attr, "Description"));

		attr = oneNode.getNetworklabel();
		el.addChild(createElSub(attr, "Networklabel"));

		attr = oneNode.getDB();
		el.addChild(createElSub(attr, "DB"));

		attr = oneNode.getOrganism();
		el.addChild(createElSub(attr, "Organism"));

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

			boolean kegg = false;
			for (int i = 0; i < attrs.length; i++) {
				if (attrs[i] != "" && attrs[i] != null
						&& !attrs[i].equals("[]")) {
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
			DAWISNode dawisN = oneNode.getDAWISNode();
			String[] attrs = { dawisN.getActivity(), dawisN.getAminoAcidSeq(),
					dawisN.getAminoAcidSeqLength(), dawisN.getAtoms(),
					dawisN.getAtomsNr(), dawisN.getBonds(),
					dawisN.getBondsNumber(), dawisN.getBracket(),
					dawisN.getCodonUsage(), dawisN.getComment(),
					dawisN.getComplexName(), dawisN.getComponent(),
					dawisN.getComposition(), dawisN.getDataLoadedAsString(),
					dawisN.getDB(), dawisN.getDefinition(),
					dawisN.getDiagnosisType(), dawisN.getDisorder(),
					dawisN.getEdge(), dawisN.getEffect(), dawisN.getElement(),
					dawisN.getEncodingGene(), dawisN.getEndPoint(),
					dawisN.getEquation(), dawisN.getFactorClass(),
					dawisN.getFormula(), dawisN.getID(),
					dawisN.getInformation(), dawisN.getIsoelectricPoint(),
					dawisN.getIsoformenNumber(), dawisN.getModule(),
					dawisN.getName(), dawisN.getNode(),
					dawisN.getNucleotidSequence(),
					dawisN.getNucleotidSequenceLength(), dawisN.getObject(),
					dawisN.getOntology(), dawisN.getOrganelle(),
					dawisN.getOrganism(), dawisN.getOriginal(),
					dawisN.getPathwayMap(), dawisN.getPosition(),
					dawisN.getRDM(), dawisN.getRemarks(), dawisN.getRepeat(),
					dawisN.getSequenceSource(), dawisN.getSpecificityNeg(),
					dawisN.getSpecificityPos(), dawisN.getStartPoint(),
					dawisN.getTarget(), dawisN.getTransfacGene(),
					dawisN.getType(), dawisN.getWeigth(),
					dawisN.getAccessionnumbersAsVector().toString(),
					dawisN.getCatalystsAsVector().toString(),
					dawisN.getCatalystNamesAsVector().toString(),
					dawisN.getClassificationAsVector().toString(),
					dawisN.getCofactorsAsVector().toString(),
					dawisN.getCofactorNamesAsVector().toString(),
					dawisN.getDBLinksAsVector().toString(),
					dawisN.getDomainsAsVector().toString(),
					dawisN.getEffectorNamesAsVector().toString(),
					dawisN.getEffectorsAsVector().toString(),
					dawisN.getElementsAsVector().toString(),
					dawisN.getFeaturesAsVector().toString(),
					dawisN.getFunctionsAsVector().toString(),
					dawisN.getGeneNamesAsVector().toString(),
					dawisN.getInhibitorsAsVector().toString(),
					dawisN.getInhibitorNamesAsVector().toString(),
					dawisN.getLocationsAsVector().toString(),
					dawisN.getMethodsAsSVector().toString(),
					dawisN.getMotifsAsVector().toString(),
					dawisN.getOrthologyAsVector().toString(),
					dawisN.getPDBsAsVector().toString(),
					dawisN.getProductNamesAsVector().toString(),
					dawisN.getProductsAsVector().toString(),
					dawisN.getProzessesAsVector().toString(),
					dawisN.getReferenceAsVector().toString(),
					dawisN.getSubfamiliesAsVector().toString(),
					dawisN.getSubstrateNamesAsVector().toString(),
					dawisN.getSubstratesAsVector().toString() };

			boolean dawis = false;
			for (int i = 0; i < attrs.length; i++) {
				if (attrs[i] != "" && attrs[i] != null
						&& !attrs[i].equals("[]")) {
					dawis = true;
					break;
				}
			}
			if (dawis) {
				elSub = new XMLNode(new XMLNode(new XMLTriple("DAWISNode", "",
						""), new XMLAttributes()));
				String[] names = { "Activity", "AminoAcidSeq",
						"AminoAcidSeqLength", "Atoms", "AtomsNumber", "Bonds",
						"BondsNumber", "Bracket", "CodonUsage", "Comment",
						"ComplexName", "Component", "Composition",
						"DataLoaded", "DB", "Definition", "DiagnosisType",
						"Disorder", "Edge", "Effect", "Element",
						"EncodingGene", "EndPoint", "Equation", "FactorClass",
						"Formula", "ID", "Information", "IsoelectricPoint",
						"IsoformenNumber", "Module", "Name", "Node",
						"NucleotidSequence", "NucleotidSequenceLength",
						"Object", "Ontology", "Organelle", "Organism",
						"Original", "PathwayMap", "Position", "RDM", "Remarks",
						"Repeat", "SequenceSource", "SpecificityNeg",
						"SpecificityPos", "StartPoint", "Target",
						"TransfacGene", "Type", "Weigth", "Accessionnumbers",
						"Catalysts", "CatalystNames", "Classifications",
						"Cofactors", "CofactorNames", "DBLinks", "Domains",
						"EffectorNames", "Effectors", "CollectorElements",
						"Features", "Functions", "GeneNames", "Inhibitors",
						"InhibitorNames", "Locations", "Methods", "Motifs",
						"Orthologies", "PDBs", "ProductNames", "Products",
						"Processes", "References", "Subfamilies",
						"SubstrateNames", "Substrates" };
				// test which data are set to save them
				for (int i = 0; i < names.length; i++) {
					elSub.addChild(createElSub(attrs[i], names[i]));
				}
				el.addChild(elSub);
			}
		}

		Color col = oneNode.getColor();
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

		// test which type the node is to save additional data
		if (oneNode instanceof biologicalObjects.nodes.CollectorNode) {
			attr = ((biologicalObjects.nodes.CollectorNode) oneNode)
					.getObject();
			el.addChild(createElSub(attr, "ElementObject"));

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
			el.addChild(createElSub(attr, "Proteins"));
			attr = ((biologicalObjects.nodes.Gene) oneNode).getEnzymes()
					.toString();
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
		// if Net is a petri Net
		if (pathway.isPetriNet()) {
			if (oneNode instanceof petriNet.Place) {
				attr = String.valueOf(((petriNet.Place) oneNode).getToken());
				el.addChild(createElSub(attr, "token"));
				attr = String.valueOf(((petriNet.Place) oneNode).getTokenMin());
				el.addChild(createElSub(attr, "tokenMin"));
				attr = String.valueOf(((petriNet.Place) oneNode).getTokenMax());
				el.addChild(createElSub(attr, "tokenMax"));
				attr = String.valueOf(((petriNet.Place) oneNode)
						.getTokenStart());
				el.addChild(createElSub(attr, "tokenStart"));
			} else if (oneNode instanceof Transition) {
				if (oneNode instanceof DiscreteTransition) {
					attr = String.valueOf(((DiscreteTransition) oneNode)
							.getDelay());
					el.addChild(createElSub(attr, "delay"));
				} else if (oneNode instanceof ContinuousTransition) {
					attr = String.valueOf(((ContinuousTransition) oneNode)
							.getMaximumSpeed());
					el.addChild(createElSub(attr, "maximumSpeed"));
				} else if (oneNode instanceof StochasticTransition) {
					attr = ((StochasticTransition) oneNode).getDistribution();
					el.addChild(createElSub(attr, "distribution"));
				}
			}
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

			// Test if min. 1 attribute is set
			// only if the XML-Node has to be created
			boolean kegg = false;
			for (int i = 0; i < 23; i++) {
				if (attrs[i] != "" && attrs[i] != null
						&& !attrs[i].equals("[]")) {
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
						"InhibitorNames" };
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
			el.addChild(createElSub(attr, "HasReactionPairEdge"));
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
		} else if (oneEdge instanceof petriNet.PNEdge) {
			attr = ((petriNet.PNEdge) oneEdge).getFunction();
			el.addChild(createElSub(attr, "Function"));
			attr = String.valueOf(((petriNet.PNEdge) oneEdge)
					.getActivationProbability());
			el.addChild(createElSub(attr, "ActivationProbability"));
			// TODO !!!
		}
		a.appendNoRDFAnnotation(el.toXMLString());
		return a;
	}

	private XMLNode createElSub(String attr, String name) {
		if (attr != "" && attr != null && !attr.equals("null")
				&& !attr.equals("[]")) {
			XMLNode elSub = new XMLNode(new XMLNode(
					new XMLTriple(name, "", ""), new XMLAttributes()));
			elSub.addAttr(name, attr);
			return elSub;
		} else {
			return null;
		}
	}
}
