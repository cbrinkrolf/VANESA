package xmlOutput.sbml;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import biologicalObjects.edges.ReactionPairEdge;
import biologicalObjects.edges.petriNet.PNArc;
import org.apache.commons.lang3.StringUtils;
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
import biologicalObjects.edges.Inhibition;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.PNNode;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import graph.groups.Group;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.RangeSelector;

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
	private final OutputStream os;
	/*
	 * data from the graph
	 */
	private final Pathway pathway;
	private Pathway rootPathway;
	private static final double INITIAL_VALUE = 1.0;
	/*
	 * current version number
	 */
	private static final String VERSION = "1.0";
	private static final String COMP = "comp_";
	private static final String SPEC = "spec_";
	private static final String REAC = "reac_";

	public JSBMLoutput(OutputStream os, Pathway pathway) {
		this.os = os;
		this.pathway = pathway;
	}

	/**
	 * Generates a SBML document via jSBML.
	 * 
	 * @throws XMLStreamException
	 */
	public String generateSBMLDocument() throws XMLStreamException {
		int answer = JOptionPane.YES_OPTION;
		if (pathway instanceof BiologicalNodeAbstract) {
			Object[] options = { "Save subpathway", "Save complete pathway" };
			answer = JOptionPane.showOptionDialog(MainWindow.getInstance().getFrame(),
					"You try to save a opened subpathway. Do you want to save this subpathway?", "Save subpathway",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		}
		if (answer == JOptionPane.YES_OPTION) {
			rootPathway = pathway;
		} else if (answer == JOptionPane.NO_OPTION) {
			rootPathway = pathway.getRootPathway();
		}

		String message = "";
		// Create a new SBMLDocument object, using SBML Level 3 Version 1.
		SBMLDocument doc = new SBMLDocument(3, 1);
		Model model = doc.createModel("VANESA");

		// create additional annotation and add it to the model
		Annotation a = createAnnotation();
		model.setAnnotation(a);

		Compartment compartment;

		// read all nodes from graph
		Set<BiologicalNodeAbstract> flattenedPathwayNodes = new HashSet<>();
		for (BiologicalNodeAbstract node : rootPathway.getAllGraphNodesSorted()) {
			flattenedPathwayNodes.addAll(node.getLeafNodes());
		}

		Set<BiologicalEdgeAbstract> flattenedPathwayEdges = new HashSet<>();
		Iterator<BiologicalNodeAbstract> nodeIterator = flattenedPathwayNodes.iterator();

		BiologicalNodeAbstract oneNode;
		String nodeCompartment;
		Compartment testCompartment;

		String str_id;
		Species spec;
		try {
			while (nodeIterator.hasNext()) {
				oneNode = nodeIterator.next();
				for (BiologicalEdgeAbstract conEdge : oneNode.getConnectingEdges()) {
					if (!(conEdge.getFrom().isEnvironmentNodeOf(rootPathway)
							&& conEdge.getTo().isEnvironmentNodeOf(rootPathway))) {
						flattenedPathwayEdges.add(conEdge);
					}
				}
				// test to what compartment the node belongs
				String compName = pathway.getCompartmentManager().getCompartment(oneNode);
				graph.Compartment.Compartment c = pathway.getCompartmentManager().getCompartment(compName);
				nodeCompartment = COMP + compName;
				// System.out.println(nodeCompartment);
				// test if compartment already exists
				testCompartment = model.getCompartment(nodeCompartment);
				// System.out.println(testCompartment);
				if (testCompartment != null) {
					compartment = testCompartment;
				} else {
					// if there is no compartment it will be created here
					compartment = model.createCompartment();
					compartment.setId(nodeCompartment);
					compartment.setConstant(false);

					if (c != null) {
						Annotation annotation = new Annotation();
						// Save attributes that every node has
						XMLNode el = new XMLNode(new XMLNode(new XMLTriple("spec", "", ""), new XMLAttributes()));
						Color col = c.getColor();
						if (col != null) {
							el.addChild(createColorNode(col, "Color"));
						}
						annotation.appendNonRDFAnnotation(el);
						compartment.setAnnotation(annotation);
					}

					// System.out.println("durch");
				}
				// The ID of a species has to be a string and could not begin
				// with a number
				str_id = SPEC + String.valueOf(oneNode.getID());
				// create species from current node
				spec = model.createSpecies(str_id, compartment);
				spec.setName(oneNode.getName());
				spec.setConstant(false);
				spec.setHasOnlySubstanceUnits(false);
				spec.setBoundaryCondition(false);
				spec.setInitialAmount(INITIAL_VALUE);
				spec.setInitialConcentration(INITIAL_VALUE);

				// create additional annotation and add it to the species
				a = createAnnotation(oneNode);
				spec.setAnnotation(a);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// if something went wrong, the user gets a notification
			message = "\nCreating SBML was not successful.";
		}

		// reactions to sbml
		HashMap<Integer, BiologicalEdgeAbstract> map = new HashMap<>();
		for (BiologicalEdgeAbstract bea : flattenedPathwayEdges) {
			map.put(bea.getID(), bea);
		}
		ArrayList<Integer> ids = new ArrayList<>(map.keySet());
		ids.sort(Integer::compare);
		List<BiologicalEdgeAbstract> sortedEdges = new ArrayList<>();
		for (Integer id : ids) {
			sortedEdges.add(map.get(id));
		}
		Iterator<BiologicalEdgeAbstract> edgeIterator = sortedEdges.iterator();
		try {
			while (edgeIterator.hasNext()) {
				// go through all edges to get their data
				BiologicalEdgeAbstract oneEdge = edgeIterator.next();
				// The ID of a reaction has to be a string and could not begin with a number
				str_id = REAC + String.valueOf(oneEdge.getID());
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
				spec = doc.getModel().getSpecies(SPEC + to.getID());
				SpeciesReference subs = reac.createProduct(spec);
				subs.setConstant(false);
				// treat "from-nodes" (reactants)
				spec = doc.getModel().getSpecies(SPEC + from.getID());
				subs = reac.createReactant(spec);
				subs.setConstant(false);
				nodeCompartment = spec.getCompartment();
				// choose compartment of the "from-nodes" and add the reactant
				reac.setCompartment(model.getCompartment(nodeCompartment));
			}
		} catch (Exception e) {
			e.printStackTrace();
			// if something went wrong, the user get's a notification
			message = "\nCreating SBML was not successful.";
		}

		// Do not write the SBML document if error occurred.
		System.out.println("message lengths: "+message.length());
		if(message.length() > 0){
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return message;
		}
		
		// Write the SBML document to a file.
		try {
			// System.out.println("vor write");
			SBMLWriter.write(doc, os, "VANESA", VERSION);
			// PipedInputStream in = new PipedInputStream();
			// PipedOutputStream out = new PipedOutputStream(in);
			// SBMLWriter.write(doc, out, "VANESA", VERSION)
			// OutputStream os = new Outputstream
			// System.out.println("nach write");

			os.close();
			message = "";
		} catch (SBMLException | XMLStreamException e) {
			e.printStackTrace();
			message = "\nWriting SBML file was not successful.";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	private Annotation createAnnotation() {
		Annotation a = new Annotation();
		// Save Shape
		List<Map<String, String>> rangeInfos = RangeSelector.getInstance().getRangesInMyGraph(pathway.getGraph());
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("model", "", ""), new XMLAttributes()));
		if (rangeInfos != null) {
			XMLNode elSub = new XMLNode(new XMLNode(new XMLTriple("listOfRanges", "", ""), new XMLAttributes()));
			XMLNode elSubSub;
			String value;
			for (Map<String, String> range : rangeInfos) {
				elSubSub = new XMLNode(new XMLNode(new XMLTriple("Range", "", ""), new XMLAttributes()));
				for (String key : range.keySet()) {
					value = range.get(key);
					elSubSub.addChild(createElSub(value, key));
				}
				elSub.addChild(elSubSub);
			}
			el.addChild(elSub);
		}

		if (pathway.getGroups().size() != 0) {
			XMLNode groups = new XMLNode(new XMLNode(new XMLTriple("listOfGroups", "", ""), new XMLAttributes()));
			XMLNode groupSub;
			for (Group group : pathway.getGroups()) {
				groupSub = new XMLNode(new XMLNode(new XMLTriple("Group", "", ""), new XMLAttributes()));
				for (BiologicalNodeAbstract node : group.nodes) {
					groupSub.addChild(createElSub(Integer.toString(node.getID()), "Node"));
				}
				groups.addChild(groupSub);
			}
			el.addChild(groups);
		}

		XMLNode hierarchy = new XMLNode(new XMLNode(new XMLTriple("listOfHierarchies", "", ""), new XMLAttributes()));
		Set<BiologicalNodeAbstract> hierarchyNodes = new HashSet<>();
		Set<BiologicalNodeAbstract> flattenedPathwayNodes = new HashSet<>();
		for (BiologicalNodeAbstract node : rootPathway.getAllGraphNodesSorted()) {
			flattenedPathwayNodes.addAll(node.getLeafNodes());
		}
		for (BiologicalNodeAbstract flattenedNode : flattenedPathwayNodes) {
			hierarchyNodes.addAll(flattenedNode.getAllParentNodes());
		}
		for (BiologicalNodeAbstract node : hierarchyNodes) {
			if (node.isCoarseNode()) {
				addHierarchyXMLNode(hierarchy, node);
			}
		}
		el.addChild(hierarchy);
		String attr = String.valueOf(pathway.isPetriNet());
		el.addChild(createElSub(attr, "isPetriNet"));
		a.appendNonRDFAnnotation(el);
		return a;
	}

	private void addHierarchyXMLNode(XMLNode hierarchy, BiologicalNodeAbstract node) {
		XMLNode hierarchyXMLNode = new XMLNode(new XMLNode(new XMLTriple("coarseNode", "", ""), new XMLAttributes()));
		hierarchyXMLNode.addAttr("id", "spec_" + node.getID());
		hierarchyXMLNode.addAttr("label", node.getLabel());
		hierarchyXMLNode.addAttr("opened", !rootPathway.getClosedSubPathways().contains(node) ? "true" : "false");
		hierarchyXMLNode.addAttr("root", node.getRootNode() == null ? "null" : "spec_" + node.getRootNode().getID());
		XMLNode childXMLNode;
		for (BiologicalNodeAbstract childNode : node.getChildrenNodes()) {
			childXMLNode = new XMLNode(new XMLNode(new XMLTriple("child", "", ""), new XMLAttributes()));
			childXMLNode.addAttr("id", "spec_" + childNode.getID());
			hierarchyXMLNode.addChild(childXMLNode);
		}
		hierarchy.addChild(hierarchyXMLNode);
	}

	// save additional data of the nodes
	private Annotation createAnnotation(BiologicalNodeAbstract oneNode) {
		// Save attributes that every node has
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("spec", "", ""), new XMLAttributes()));
		el.addChild(createElSub(oneNode.getLabel(), "label"));
		el.addChild(createElSub(String.valueOf(oneNode.getNodesize()), "Nodesize"));
		el.addChild(createElSub(oneNode.getBiologicalElement(), "BiologicalElement"));
		// Point2D p = pathway.getGraph().getVertexLocation(oneNode);
		// if(oneNode.getParentNode()!=null){
		// p = oneNode.getParentNode().getGraph().getVertexLocation(oneNode);
		// }
		Point2D p = new Point2D.Double(0, 0);
		if (!oneNode.isCoarseNode()) {
			p = pathway.getVertices().get(oneNode);
		}

		XMLNode elCoordinates = new XMLNode(new XMLNode(new XMLTriple("Coordinates", "", ""), new XMLAttributes()));
		elCoordinates.addChild(createElSub(String.valueOf(p.getX()), "x_Coordinate"));
		elCoordinates.addChild(createElSub(String.valueOf(p.getY()), "y_Coordinate"));
		el.addChild(elCoordinates);
		el.addChild(createElSub(oneNode.isEnvironmentNodeOf(rootPathway) ? "true" : "false", "environmentNode"));

		XMLNode elParameters = new XMLNode(new XMLNode(new XMLTriple("Parameters", "", ""), new XMLAttributes()));
		for (Parameter param : oneNode.getParameters()) {
			XMLNode elSubSub = new XMLNode(new XMLNode(new XMLTriple("Parameter", "", ""), new XMLAttributes()));
			elSubSub.addChild(createElSub(param.getName(), "Name"));
			elSubSub.addChild(createElSub(param.getValue() + "", "Value"));
			elSubSub.addChild(createElSub(param.getUnit(), "Unit"));
			elParameters.addChild(elSubSub);
		}
		el.addChild(elParameters);
		el.addChild(createElSub(oneNode.getComments(), "Comments"));
		el.addChild(createElSub(oneNode.getDescription(), "Description"));
		el.addChild(createElSub(oneNode.getNetworklabel(), "Networklabel"));
		el.addChild(createElSub(oneNode.getOrganism(), "Organism"));
		el.addChild(createElSub(String.valueOf(oneNode.hasKEGGNode()), "HasKEGGNode"));

		// only if hasKEGGNode = true the following data should be saved.
		if (oneNode.hasKEGGNode() && oneNode.getKEGGnode() != null) {
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
			attrs[50] = oneNode.getKEGGnode().getKeggproduct();
			attrs[51] = oneNode.getKEGGnode().getKeggreaction();
			attrs[52] = oneNode.getKEGGnode().getKeggreference();
			attrs[53] = oneNode.getKEGGnode().getKeggsubstrate();
			attrs[54] = oneNode.getKEGGnode().getKeggsysName();
			attrs[55] = oneNode.getKEGGnode().getNodeLabel();
			attrs[56] = oneNode.getKEGGnode().getShape();
			attrs[57] = oneNode.getKEGGnode().getWidth();
			attrs[58] = oneNode.getKEGGnode().getAllDBLinksAsVector().toString();
			attrs[59] = oneNode.getKEGGnode().getAllGeneMotifsAsVector().toString();
			attrs[60] = oneNode.getKEGGnode().getAllNamesAsVector().toString();
			attrs[61] = oneNode.getKEGGnode().getAllPathwayLinksAsVector().toString();
			attrs[62] = oneNode.getKEGGnode().getAllStructuresAsVector().toString();
			boolean kegg = false;
			for (String s : attrs) {
				if (StringUtils.isNotEmpty(s) && !s.equals("[]")) {
					kegg = true;
					break;
				}
			}
			if (kegg) {
				XMLNode elKEGGNode = new XMLNode(new XMLNode(new XMLTriple("KEGGNode", "", ""), new XMLAttributes()));
				// test which data are set to save them
				elKEGGNode.addChild(createElSub(attrs[0], "AllDBLinks"));
				elKEGGNode.addChild(createElSub(attrs[1], "AllGeneMotifs"));
				elKEGGNode.addChild(createElSub(attrs[2], "AllInvolvedElements"));
				elKEGGNode.addChild(createElSub(attrs[3], "AllNames"));
				elKEGGNode.addChild(createElSub(attrs[4], "AllPathwayLinks"));
				elKEGGNode.addChild(createElSub(attrs[5], "AllStructures"));
				elKEGGNode.addChild(createElSub(attrs[6], "BackgroundColour"));
				elKEGGNode.addChild(createElSub(attrs[7], "CompoundAtoms"));
				elKEGGNode.addChild(createElSub(attrs[8], "CompoundAtomsNr"));
				elKEGGNode.addChild(createElSub(attrs[9], "CompoundBondNr"));
				elKEGGNode.addChild(createElSub(attrs[10], "CompoundBonds"));
				elKEGGNode.addChild(createElSub(attrs[11], "CompoundComment"));
				elKEGGNode.addChild(createElSub(attrs[12], "CompoundFormula"));
				elKEGGNode.addChild(createElSub(attrs[13], "CompoundMass"));
				elKEGGNode.addChild(createElSub(attrs[14], "CompoundModule"));
				elKEGGNode.addChild(createElSub(attrs[15], "CompoundOrganism"));
				elKEGGNode.addChild(createElSub(attrs[16], "CompoundRemarks"));
				elKEGGNode.addChild(createElSub(attrs[17], "CompoundSequence"));
				elKEGGNode.addChild(createElSub(attrs[18], "ForegroundColour"));
				elKEGGNode.addChild(createElSub(attrs[19], "GeneAAseq"));
				elKEGGNode.addChild(createElSub(attrs[20], "GeneAAseqNr"));
				elKEGGNode.addChild(createElSub(attrs[21], "GeneCodonUsage"));
				elKEGGNode.addChild(createElSub(attrs[22], "GeneDefinition"));
				elKEGGNode.addChild(createElSub(attrs[23], "GeneEnzyme"));
				elKEGGNode.addChild(createElSub(attrs[24], "GeneName"));
				elKEGGNode.addChild(createElSub(attrs[25], "GeneNtSeq"));
				elKEGGNode.addChild(createElSub(attrs[26], "GeneNtSeqNr"));
				elKEGGNode.addChild(createElSub(attrs[27], "GeneOrthology"));
				elKEGGNode.addChild(createElSub(attrs[28], "GeneOrthologyName"));
				elKEGGNode.addChild(createElSub(attrs[29], "GenePosition"));
				elKEGGNode.addChild(createElSub(attrs[30], "GlycanBracket"));
				elKEGGNode.addChild(createElSub(attrs[31], "GlycanComposition"));
				elKEGGNode.addChild(createElSub(attrs[32], "GlycanEdge"));
				elKEGGNode.addChild(createElSub(attrs[33], "GlycanName"));
				elKEGGNode.addChild(createElSub(attrs[34], "GlycanNode"));
				elKEGGNode.addChild(createElSub(attrs[35], "GlycanOrthology"));
				elKEGGNode.addChild(createElSub(attrs[36], "Height"));
				elKEGGNode.addChild(createElSub(attrs[37], "Keggcofactor"));
				elKEGGNode.addChild(createElSub(attrs[38], "KeggComment"));
				elKEGGNode.addChild(createElSub(attrs[39], "KEGGComponent"));
				elKEGGNode.addChild(createElSub(attrs[40], "Keggeffector"));
				elKEGGNode.addChild(createElSub(attrs[41], "KEGGentryID"));
				elKEGGNode.addChild(createElSub(attrs[42], "KEGGentryLink"));
				elKEGGNode.addChild(createElSub(attrs[43], "KEGGentryMap"));
				elKEGGNode.addChild(createElSub(attrs[44], "KEGGentryName"));
				elKEGGNode.addChild(createElSub(attrs[45], "KEGGentryReaction"));
				elKEGGNode.addChild(createElSub(attrs[46], "KEGGentryType"));
				elKEGGNode.addChild(createElSub(attrs[47], "KeggenzymeClass"));
				elKEGGNode.addChild(createElSub(attrs[48], "Keggorthology"));
				elKEGGNode.addChild(createElSub(attrs[49], "KEGGPathway"));
				elKEGGNode.addChild(createElSub(attrs[50], "Keggprodukt"));
				elKEGGNode.addChild(createElSub(attrs[51], "Keggreaction"));
				elKEGGNode.addChild(createElSub(attrs[52], "Keggreference"));
				elKEGGNode.addChild(createElSub(attrs[53], "Keggsubstrate"));
				elKEGGNode.addChild(createElSub(attrs[54], "KeggsysName"));
				elKEGGNode.addChild(createElSub(attrs[55], "NodeLabel"));
				elKEGGNode.addChild(createElSub(attrs[56], "Shape"));
				elKEGGNode.addChild(createElSub(attrs[57], "Width"));
				elKEGGNode.addChild(createElSub(attrs[58], "AllDBLinksAsVector"));
				elKEGGNode.addChild(createElSub(attrs[59], "AllGeneMotifsAsVector"));
				elKEGGNode.addChild(createElSub(attrs[60], "AllNamesAsVector"));
				elKEGGNode.addChild(createElSub(attrs[61], "AllPathwayLinksAsVector"));
				elKEGGNode.addChild(createElSub(attrs[62], "AllStructuresAsVector"));
				el.addChild(elKEGGNode);
			}
		}
		// only if hasDAWISNode = true the following data should be saved.
		Color col = oneNode.getColor();
		if (col != null) {
			el.addChild(createColorNode(col, "Color"));
		}
		col = oneNode.getPlotColor();
		if (col != null) {
			el.addChild(createColorNode(col, "plotColor"));
		}
		XMLNode elNodeReference = new XMLNode(new XMLNode(new XMLTriple("NodeReference", "", ""), new XMLAttributes()));
		elNodeReference.addChild(createElSub(oneNode.isLogical() ? "true" : "false", "hasRef"));
		if (oneNode.isLogical()) {
			elNodeReference.addChild(createElSub(String.valueOf(oneNode.getLogicalReference().getID()), "RefID"));
		}
		el.addChild(elNodeReference);
		el.addChild(createElSub(oneNode.isConstant() ? "true" : "false", "constCheck"));
		if (!(oneNode instanceof PNNode)) {
			el.addChild(createElSub(String.valueOf(oneNode.getConcentration()), "concentration"));
			el.addChild(createElSub(String.valueOf(oneNode.getConcentrationStart()), "concentrationStart"));
			el.addChild(createElSub(String.valueOf(oneNode.getConcentrationMin()), "concentrationMin"));
			el.addChild(createElSub(String.valueOf(oneNode.getConcentrationMax()), "concentrationMax"));
			el.addChild(createElSub(String.valueOf(oneNode.isDiscrete()), "isDiscrete"));
		}
		// test which type the node is to save additional data
		if (oneNode instanceof biologicalObjects.nodes.DNA) {
			el.addChild(createElSub(((biologicalObjects.nodes.DNA) oneNode).getNtSequence(), "NtSequence"));
		} else if (oneNode instanceof biologicalObjects.nodes.Gene) {
			biologicalObjects.nodes.Gene gene = (biologicalObjects.nodes.Gene) oneNode;
			el.addChild(createElSub(gene.getNtSequence(), "NtSequence"));
			el.addChild(createElSub(gene.getProteins().toString(), "Proteins"));
			el.addChild(createElSub(gene.getEnzymes().toString(), "Enzymes"));
		} else if (oneNode instanceof biologicalObjects.nodes.PathwayMap) {
			biologicalObjects.nodes.PathwayMap pathwayMap = (biologicalObjects.nodes.PathwayMap) oneNode;
			el.addChild(createElSub(String.valueOf(pathwayMap.isSpecification()), "Specification"));
			if (pathwayMap.getPathwayLink() != null) {
				el.addChild(createElSub(pathwayMap.getPathwayLink().toString(), "PathwayLink"));
			}
		} else if (oneNode instanceof biologicalObjects.nodes.Protein) {
			el.addChild(createElSub(((biologicalObjects.nodes.Protein) oneNode).getAaSequence(), "AaSequence"));
		} else if (oneNode instanceof biologicalObjects.nodes.RNA) {
			el.addChild(createElSub(((biologicalObjects.nodes.RNA) oneNode).getNtSequence(), "NtSequence"));
			if (oneNode instanceof biologicalObjects.nodes.SRNA) {
				biologicalObjects.nodes.SRNA sRna = (biologicalObjects.nodes.SRNA) oneNode;
				el.addChild(createElSub(sRna.getTarbaseAccession(), "Tarbase_accession"));
				el.addChild(createElSub(sRna.getTarbaseDS(), "Tarbase_DS"));
				el.addChild(createElSub(sRna.getTarbaseEnsemble(), "Tarbase_ensemble"));
				el.addChild(createElSub(sRna.getTarbaseIS(), "Tarbase_IS"));
			}
		}
		if (oneNode instanceof DynamicNode) {
			DynamicNode dynamicNode = (DynamicNode) oneNode;
			el.addChild(createElSub(String.valueOf(dynamicNode.getMaximalSpeed()), "maximalSpeed"));
			el.addChild(createElSub(String.valueOf(dynamicNode.isKnockedOut()), "knockedOut"));
		}
		// if Net is a petri Net
		if (pathway.isPetriNet()) {
			if (oneNode instanceof Place) {
				Place place = (Place) oneNode;
				el.addChild(createElSub(String.valueOf(place.getToken()), "token"));
				el.addChild(createElSub(String.valueOf(place.getTokenMin()), "tokenMin"));
				el.addChild(createElSub(String.valueOf(place.getTokenMax()), "tokenMax"));
				el.addChild(createElSub(String.valueOf(place.getTokenStart()), "tokenStart"));
				el.addChild(createElSub(String.valueOf(place.getConflictStrategy()), "ConflictStrategy"));
			} else if (oneNode instanceof Transition) {
				Transition transition = (Transition) oneNode;
				el.addChild(createElSub(String.valueOf(transition.isKnockedOut()), "knockedOut"));
				el.addChild(createElSub(transition.getFiringCondition(), "firingCondition"));
				if (oneNode instanceof DiscreteTransition) {
					el.addChild(createElSub(String.valueOf(((DiscreteTransition) oneNode).getDelay()), "delay"));
				} else if (oneNode instanceof ContinuousTransition) {
					el.addChild(createElSub(((ContinuousTransition) oneNode).getMaximalSpeed(), "maximalSpeed"));
				} else if (oneNode instanceof StochasticTransition) {
					XMLNode elDistributionProps = new XMLNode(new XMLNode(new XMLTriple("distributionProperties", "", ""), new XMLAttributes()));
					StochasticTransition st = (StochasticTransition) oneNode;
					elDistributionProps.addChild(createElSub(st.getDistribution(), "distribution"));
					elDistributionProps.addChild(createElSub(String.valueOf(st.getH()), "h"));
					elDistributionProps.addChild(createElSub(String.valueOf(st.getA()), "a"));
					elDistributionProps.addChild(createElSub(String.valueOf(st.getB()), "b"));
					elDistributionProps.addChild(createElSub(String.valueOf(st.getC()), "c"));
					elDistributionProps.addChild(createElSub(String.valueOf(st.getMu()), "mu"));
					elDistributionProps.addChild(createElSub(String.valueOf(st.getSigma()), "sigma"));
					String eventsString = st.getEvents().stream().map(String::valueOf).collect(Collectors.joining(","));
					elDistributionProps.addChild(createElSub(eventsString, "discreteEvents"));
					String probsString = st.getProbabilities().stream().map(String::valueOf).collect(Collectors.joining(","));
					elDistributionProps.addChild(createElSub(probsString, "discreteEventProbabilities"));
					el.addChild(elDistributionProps);
				}
			}
		}
		Annotation a = new Annotation();
		a.appendNonRDFAnnotation(el);
		return a;
	}

	// Save additional data of the edges
	private Annotation createAnnotation(BiologicalEdgeAbstract oneEdge) {
		// Save the attributes which every edge has
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("reac", "", ""), new XMLAttributes()));
		el.addChild(createElSub(oneEdge.getLabel(), "label"));
		// el.addChild(createElSub(String.valueOf(oneEdge.isWeighted()), "IsWeighted"));
		el.addChild(createElSub(oneEdge.getFunction(), "Function"));
		Color col = oneEdge.getColor();
		if (col != null) {
			el.addChild(createColorNode(col, "Color"));
		}
		el.addChild(createElSub(String.valueOf(oneEdge.isDirected()), "IsDirected"));
		el.addChild(createElSub(oneEdge.getBiologicalElement(), "BiologicalElement"));
		el.addChild(createElSub(oneEdge.getDescription(), "Description"));
		el.addChild(createElSub(oneEdge.getComments(), "Comments"));
		// el.addChild(createElSub(String.valueOf(oneEdge.hasFeatureEdge()), "HasFeatureEdge"));
		// el.addChild(createElSub(String.valueOf(oneEdge.hasKEGGEdge()), "HasKEGGEdge"));

		// Save additional data
		if (oneEdge instanceof biologicalObjects.edges.ReactionPair) {
			biologicalObjects.edges.ReactionPair reactionPair = (biologicalObjects.edges.ReactionPair) oneEdge;
			el.addChild(createElSub(String.valueOf(reactionPair.hasReactionPairEdge()), "HasReactionPairEdge"));
			// Test if hasRPairEdge = true
			// only if the following data has to be saved
			if (reactionPair.hasReactionPairEdge()) {
				ReactionPairEdge rpe = reactionPair.getReactionPairEdge();
				boolean kegg = StringUtils.isNotEmpty(rpe.getReactionPairID()) ||
							   StringUtils.isNotEmpty(rpe.getName()) || StringUtils.isNotEmpty(rpe.getType());
				if (kegg) {
					XMLNode elSub = new XMLNode(new XMLNode(new XMLTriple("ReactionPairEdge", "", ""), new XMLAttributes()));
					elSub.addChild(createElSub(rpe.getReactionPairID(), "ReactionPairEdgeID"));
					elSub.addChild(createElSub(rpe.getName(), "ReactionPairName"));
					elSub.addChild(createElSub(rpe.getType(), "ReactionPairType"));
					el.addChild(elSub);
				}
			}
		} else if (oneEdge instanceof PNArc) {
			PNArc pnArc = (PNArc) oneEdge;
			el.addChild(createElSub(String.valueOf(pnArc.getProbability()), "Probability"));
			el.addChild(createElSub(String.valueOf(pnArc.getPriority()), "Priority"));
		} else if (oneEdge instanceof Inhibition) {
			el.addChild(createElSub(String.valueOf(((Inhibition) oneEdge).isAbsoluteInhibition()), "absoluteInhibition"));
		}
		Annotation a = new Annotation();
		a.appendNonRDFAnnotation(el);
		return a;
	}

	private XMLNode createElSub(String attr, String name) {
		if (StringUtils.isNotEmpty(attr) && !attr.equals("null") && !attr.equals("[]")) {
			XMLNode elSub = new XMLNode(new XMLNode(new XMLTriple(name, "", ""), new XMLAttributes()));
			elSub.addAttr(name, attr);
			return elSub;
		} else {
			return null;
		}
	}

	private XMLNode createColorNode(Color col, String key) {
		XMLNode node = new XMLNode(new XMLNode(new XMLTriple(key, "", ""), new XMLAttributes()));
		node.addChild(createElSub(String.valueOf(col.getRGB()), "RGB"));
		node.addChild(createElSub(String.valueOf(col.getBlue()), "Blue"));
		node.addChild(createElSub(String.valueOf(col.getGreen()), "Green"));
		node.addChild(createElSub(String.valueOf(col.getRed()), "Red"));
		return node;
	}
}
