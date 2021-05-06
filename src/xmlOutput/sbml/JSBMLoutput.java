package xmlOutput.sbml;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import graph.groups.Group;
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
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.RangeSelector;
import util.MyIntComparable;

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
	private OutputStream os = null;
	/*
	 * data from the graph
	 */
	private Pathway pathway = null;
	private Pathway rootPathway;
	private static final double INITIALVALUE = 1.0;
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
	 * @throws IOException
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
		Set<BiologicalNodeAbstract> flattenedPathwayNodes = new HashSet<BiologicalNodeAbstract>();
		for (BiologicalNodeAbstract node : rootPathway.getAllGraphNodesSorted()) {
			flattenedPathwayNodes.addAll(node.getLeafNodes());
		}

		Set<BiologicalEdgeAbstract> flattenedPathwayEdges = new HashSet<BiologicalEdgeAbstract>();
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
						XMLNode elSub;

						String attr;

						Color col = c.getColor();
						if (col != null) {
							elSub = new XMLNode(new XMLNode(new XMLTriple("Color", "", ""), new XMLAttributes()));
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
				spec.setInitialAmount(INITIALVALUE);
				spec.setInitialConcentration(INITIALVALUE);

				// create additional annotation and add it to the species
				a = createAnnotation(oneNode);
				spec.setAnnotation(a);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// if something went wrong, the user get's a notification
			message = "\nCreating SBML was not successful.";
		}

		// reactions to sbml

		HashMap<Integer, BiologicalEdgeAbstract> map = new HashMap<Integer, BiologicalEdgeAbstract>();

		for (BiologicalEdgeAbstract bea : flattenedPathwayEdges) {
			map.put(bea.getID(), bea);
		}

		ArrayList<Integer> ids = new ArrayList<Integer>(map.keySet());
		Collections.sort(ids, new MyIntComparable());

		List<BiologicalEdgeAbstract> sortedEdges = new ArrayList<BiologicalEdgeAbstract>();
		for (int i = 0; i < ids.size(); i++) {
			sortedEdges.add(map.get(ids.get(i)));
		}

		Iterator<BiologicalEdgeAbstract> edgeIterator = sortedEdges.iterator();

		BiologicalEdgeAbstract oneEdge;
		Reaction reac;
		BiologicalNodeAbstract from;
		BiologicalNodeAbstract to;
		SpeciesReference subs;
		try {
			while (edgeIterator.hasNext()) {
				// go through all edges to get their data
				oneEdge = edgeIterator.next();
				// The ID of a reaction has to be a string and could not begin
				// with
				// a number
				str_id = REAC + String.valueOf(oneEdge.getID());
				// create reaction from the current node
				reac = model.createReaction(str_id);
				reac.setFast(false);
				reac.setReversible(false);
				reac.setName(oneEdge.getName());

				// create additional annotation and add to the reaction
				a = createAnnotation(oneEdge);
				reac.setAnnotation(a);

				// search and assign products and reactants
				from = oneEdge.getFrom();
				to = oneEdge.getTo();
				// treat "to-nodes" (products)
				spec = doc.getModel().getSpecies(SPEC + to.getID());
				subs = reac.createProduct(spec);
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
			// if something went wrong, the user get's a notification
			message = "\nCreating SBML was not successful.";
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
		} catch (SBMLException e) {
			e.printStackTrace();
			message = "\nWriting SBML file was not successful.";
		} catch (XMLStreamException e) {
			e.printStackTrace();
			message = "\nWriting SBML file was not successful.";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	private Annotation createAnnotation() throws XMLStreamException {
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

		if (pathway.getGroupes().size() != 0) {
			XMLNode groups = new XMLNode(new XMLNode(new XMLTriple("listOfGroups", "", ""), new XMLAttributes()));
			XMLNode groupSub;
			for (Group group : pathway.getGroupes()) {
				groupSub = new XMLNode(new XMLNode(new XMLTriple("Group", "", ""), new XMLAttributes()));
				for (BiologicalNodeAbstract node : group.nodes) {
					groupSub.addChild(createElSub(Integer.toString(node.getID()), "Node"));
				}
				groups.addChild(groupSub);
			}
			el.addChild(groups);
		}

		XMLNode hierarchy = new XMLNode(new XMLNode(new XMLTriple("listOfHierarchies", "", ""), new XMLAttributes()));
		Set<BiologicalNodeAbstract> hierarchyNodes = new HashSet<BiologicalNodeAbstract>();

		Set<BiologicalNodeAbstract> flattenedPathwayNodes = new HashSet<BiologicalNodeAbstract>();
		for (BiologicalNodeAbstract node : rootPathway.getAllGraphNodesSorted()) {
			flattenedPathwayNodes.addAll(node.getLeafNodes());
		}

		Set<BiologicalNodeAbstract> parentNodes = new HashSet<BiologicalNodeAbstract>();
		for (BiologicalNodeAbstract flattenedNode : flattenedPathwayNodes) {
			parentNodes = flattenedNode.getAllParentNodes();
			for (BiologicalNodeAbstract parent : parentNodes) {
				hierarchyNodes.add(parent);
			}
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
	private Annotation createAnnotation(BiologicalNodeAbstract oneNode) throws XMLStreamException {
		// create new annotation
		Annotation a = new Annotation();
		// Save attributes that every node has
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("spec", "", ""), new XMLAttributes()));
		XMLNode elSub;

		String attr = oneNode.getLabel() + "";
		el.addChild(createElSub(attr, "label"));

		attr = String.valueOf(oneNode.getNodesize());
		el.addChild(createElSub(attr, "Nodesize"));

		attr = oneNode.getBiologicalElement();
		el.addChild(createElSub(attr, "BiologicalElement"));

		// Point2D p = pathway.getGraph().getVertexLocation(oneNode);
		// if(oneNode.getParentNode()!=null){
		// p = oneNode.getParentNode().getGraph().getVertexLocation(oneNode);
		// }
		Point2D p = new Point2D.Double(0, 0);
		if (!oneNode.isCoarseNode()) {
			p = pathway.getVertices().get(oneNode);
		}

		elSub = new XMLNode(new XMLNode(new XMLTriple("Coordinates", "", ""), new XMLAttributes()));
		attr = String.valueOf(p.getX());
		elSub.addChild(createElSub(attr, "x_Coordinate"));
		attr = String.valueOf(p.getY());
		elSub.addChild(createElSub(attr, "y_Coordinate"));
		el.addChild(elSub);

		attr = oneNode.isEnvironmentNodeOf(rootPathway) ? "true" : "false";
		el.addChild(createElSub(attr, "environmentNode"));

		XMLNode elSubSub;
		elSub = new XMLNode(new XMLNode(new XMLTriple("Parameters", "", ""), new XMLAttributes()));

		Parameter param;
		Iterator<Parameter> it = oneNode.getParameters().iterator();
		while (it.hasNext()) {
			param = it.next();
			elSubSub = new XMLNode(new XMLNode(new XMLTriple("Parameter", "", ""), new XMLAttributes()));
			elSubSub.addChild(createElSub(param.getName(), "Name"));
			elSubSub.addChild(createElSub(param.getValue() + "", "Value"));
			elSubSub.addChild(createElSub(param.getUnit(), "Unit"));
			elSub.addChild(elSubSub);
		}

		// attr = "bla";
		// elSub.addChild(createElSub(attr, "parameter"));

		// elSub.addChild(elSub);
		el.addChild(elSub);

		attr = oneNode.getComments();
		el.addChild(createElSub(attr, "Comments"));

		attr = oneNode.getDescription();
		el.addChild(createElSub(attr, "Description"));

		attr = oneNode.getNetworklabel();
		el.addChild(createElSub(attr, "Networklabel"));

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
			attrs[58] = oneNode.getKEGGnode().getAllDBLinksAsVector().toString();
			attrs[59] = oneNode.getKEGGnode().getAllGeneMotifsAsVector().toString();
			attrs[60] = oneNode.getKEGGnode().getAllNamesAsVector().toString();
			attrs[61] = oneNode.getKEGGnode().getAllPathwayLinksAsVector().toString();
			attrs[62] = oneNode.getKEGGnode().getAllStructuresAsVector().toString();

			boolean kegg = false;
			for (int i = 0; i < attrs.length; i++) {
				if (attrs[i] != "" && attrs[i] != null && !attrs[i].equals("[]")) {
					kegg = true;
					break;
				}
			}
			if (kegg) {
				elSub = new XMLNode(new XMLNode(new XMLTriple("KEGGNode", "", ""), new XMLAttributes()));
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

		// only if hasDAWISNode = true the following data should be saved.
		Color col = oneNode.getColor();
		if (col != null) {
			elSub = new XMLNode(new XMLNode(new XMLTriple("Color", "", ""), new XMLAttributes()));
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

		col = oneNode.getPlotColor();
		if (col != null) {
			elSub = new XMLNode(new XMLNode(new XMLTriple("plotColor", "", ""), new XMLAttributes()));
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

		elSub = new XMLNode(new XMLNode(new XMLTriple("NodeReference", "", ""), new XMLAttributes()));
		if (oneNode.hasRef()) {
			attr = "true";
		} else {
			attr = "false";
		}
		elSub.addChild(createElSub(attr, "hasRef"));
		if (oneNode.hasRef()) {
			attr = oneNode.getRef().getID() + "";
			elSub.addChild(createElSub(attr, "RefID"));
		}

		el.addChild(elSub);

		if (oneNode.isConstant()) {
			attr = "true";
		} else {
			attr = "false";
		}
		el.addChild(createElSub(attr, "constCheck"));

		if (!(oneNode instanceof PNNode)) {
			attr = oneNode.getConcentration() + "";
			el.addChild(createElSub(attr, "concentration"));
			attr = oneNode.getConcentrationStart() + "";
			el.addChild(createElSub(attr, "concentrationStart"));
			attr = oneNode.getConcentrationMin() + "";
			el.addChild(createElSub(attr, "concentrationMin"));
			attr = oneNode.getConcentrationMax() + "";
			el.addChild(createElSub(attr, "concentrationMax"));
			attr = oneNode.isDiscrete() + "";
			el.addChild(createElSub(attr, "isDiscrete"));
		}
		// test which type the node is to save additional data
		if (oneNode instanceof biologicalObjects.nodes.DNA) {
			attr = ((biologicalObjects.nodes.DNA) oneNode).getNtSequence();
			el.addChild(createElSub(attr, "NtSequence"));

		} else if (oneNode instanceof biologicalObjects.nodes.Gene) {

			attr = ((biologicalObjects.nodes.Gene) oneNode).getNtSequence();
			el.addChild(createElSub(attr, "NtSequence"));

			attr = ((biologicalObjects.nodes.Gene) oneNode).getProteins().toString();
			el.addChild(createElSub(attr, "Proteins"));
			attr = ((biologicalObjects.nodes.Gene) oneNode).getEnzymes().toString();
			el.addChild(createElSub(attr, "Enzymes"));
		} else if (oneNode instanceof biologicalObjects.nodes.PathwayMap) {
			attr = String.valueOf(((biologicalObjects.nodes.PathwayMap) oneNode).getSpecification());
			el.addChild(createElSub(attr, "Specification"));
			if (((biologicalObjects.nodes.PathwayMap) oneNode).getPathwayLink() != null) {
				attr = ((biologicalObjects.nodes.PathwayMap) oneNode).getPathwayLink().toString();
				el.addChild(createElSub(attr, "PathwayLink"));
			}
		} else if (oneNode instanceof biologicalObjects.nodes.Protein) {
			attr = ((biologicalObjects.nodes.Protein) oneNode).getAaSequence();
			el.addChild(createElSub(attr, "AaSequence"));

		} else if (oneNode instanceof biologicalObjects.nodes.RNA) {
			attr = ((biologicalObjects.nodes.RNA) oneNode).getNtSequence();
			el.addChild(createElSub(attr, "NtSequence"));

		} else if (oneNode instanceof biologicalObjects.nodes.SmallMolecule) {
			attr = ((biologicalObjects.nodes.SmallMolecule) oneNode).getFormula();
			el.addChild(createElSub(attr, "Formula"));
			attr = ((biologicalObjects.nodes.SmallMolecule) oneNode).getMass();
			el.addChild(createElSub(attr, "Mass"));

		} else if (oneNode instanceof biologicalObjects.nodes.SRNA) {
			attr = ((biologicalObjects.nodes.SRNA) oneNode).getTarbase_accession();
			el.addChild(createElSub(attr, "Tarbase_accession"));
			attr = ((biologicalObjects.nodes.SRNA) oneNode).getTarbase_DS();
			el.addChild(createElSub(attr, "Tarbase_DS"));
			attr = ((biologicalObjects.nodes.SRNA) oneNode).getTarbase_ensemble();
			el.addChild(createElSub(attr, "Tarbase_ensemble"));
			attr = ((biologicalObjects.nodes.SRNA) oneNode).getTarbase_IS();
			el.addChild(createElSub(attr, "Tarbase_IS"));
		}

		if (oneNode instanceof DynamicNode) {
			attr = String.valueOf(((DynamicNode) oneNode).getMaximumSpeed());
			el.addChild(createElSub(attr, "maximumSpeed"));
			attr = String.valueOf(((DynamicNode) oneNode).isKnockedOut());
			el.addChild(createElSub(attr, "knockedOut"));
		}
		// if Net is a petri Net
		if (pathway.isPetriNet()) {
			if (oneNode instanceof biologicalObjects.nodes.petriNet.Place) {
				attr = String.valueOf(((biologicalObjects.nodes.petriNet.Place) oneNode).getToken());
				el.addChild(createElSub(attr, "token"));
				attr = String.valueOf(((biologicalObjects.nodes.petriNet.Place) oneNode).getTokenMin());
				el.addChild(createElSub(attr, "tokenMin"));
				attr = String.valueOf(((biologicalObjects.nodes.petriNet.Place) oneNode).getTokenMax());
				el.addChild(createElSub(attr, "tokenMax"));
				attr = String.valueOf(((biologicalObjects.nodes.petriNet.Place) oneNode).getTokenStart());
				el.addChild(createElSub(attr, "tokenStart"));
				attr = String.valueOf(((biologicalObjects.nodes.petriNet.Place) oneNode).getConflictStrategy());
				el.addChild(createElSub(attr, "ConflictStrategy"));

			} else if (oneNode instanceof Transition) {
				attr = String.valueOf(((Transition) oneNode).getFiringCondition());
				el.addChild(createElSub(attr, "firingCondition"));
				if (oneNode instanceof DiscreteTransition) {
					attr = String.valueOf(((DiscreteTransition) oneNode).getDelay());
					el.addChild(createElSub(attr, "delay"));
				} else if (oneNode instanceof ContinuousTransition) {

				} else if (oneNode instanceof StochasticTransition) {
					attr = ((StochasticTransition) oneNode).getDistribution();
					el.addChild(createElSub(attr, "distribution"));
				}
			}
		}

		a.appendNonRDFAnnotation(el);
		return a;
	}

	// Save additional data of the edges
	private Annotation createAnnotation(BiologicalEdgeAbstract oneEdge) throws XMLStreamException {
		Annotation a = new Annotation();
		// Save the attributes which every edge has
		XMLNode el = new XMLNode(new XMLNode(new XMLTriple("reac", "", ""), new XMLAttributes()));
		XMLNode elSub;
		String attr = oneEdge.getLabel();
		el.addChild(createElSub(attr, "label"));
		// attr = String.valueOf(oneEdge.isWeighted());
		// el.addChild(createElSub(attr, "IsWeighted"));
		attr = oneEdge.getFunction();
		el.addChild(createElSub(attr, "Function"));
		Color col = oneEdge.getColor();
		if (col != null) {
			elSub = new XMLNode(new XMLNode(new XMLTriple("Color", "", ""), new XMLAttributes()));
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
		// attr = String.valueOf(oneEdge.hasFeatureEdge());
		// el.addChild(createElSub(attr, "HasFeatureEdge"));

		boolean attrb; // = oneEdge.hasKEGGEdge();
		// attr = String.valueOf(attrb);
		// el.addChild(createElSub(attr, "HasKEGGEdge"));

		// Save additional data
		if (oneEdge instanceof biologicalObjects.edges.ReactionPair) {
			attrb = ((biologicalObjects.edges.ReactionPair) oneEdge).hasRPairEdge();
			attr = String.valueOf(attrb);
			el.addChild(createElSub(attr, "HasReactionPairEdge"));
			// Test if hasRPairEdge = true
			// only if the following data has to be saved
			if (attrb) {
				// Data to save
				String[] attrs = new String[3];
				attrs[0] = ((biologicalObjects.edges.ReactionPair) oneEdge).getReactionPairEdge().getReactionPairID();
				attrs[1] = ((biologicalObjects.edges.ReactionPair) oneEdge).getReactionPairEdge().getName();
				attrs[2] = ((biologicalObjects.edges.ReactionPair) oneEdge).getReactionPairEdge().getType();
				// Test if one of the attributes is set
				// if not, no node has to be created
				boolean kegg = false;
				for (int i = 0; i < 3; i++) {
					if (attrs[i] != null && attrs[i].length() > 0) {
						kegg = true;
						break;
					}
				}
				if (kegg) {
					elSub = new XMLNode(new XMLNode(new XMLTriple("ReactionPairEdge", "", ""), new XMLAttributes()));
					// Test which attributes are set
					elSub.addChild(createElSub(attrs[0], "ReactionPairEdgeID"));
					elSub.addChild(createElSub(attrs[1], "ReactionPairName"));
					elSub.addChild(createElSub(attrs[2], "ReactionPairType"));

					el.addChild(elSub);
				}
			}
		} else if (oneEdge instanceof biologicalObjects.edges.petriNet.PNEdge) {

			attr = String.valueOf(((biologicalObjects.edges.petriNet.PNEdge) oneEdge).getProbability());
			el.addChild(createElSub(attr, "Probability"));

			attr = String.valueOf(((biologicalObjects.edges.petriNet.PNEdge) oneEdge).getPriority());
			el.addChild(createElSub(attr, "Priority"));
		} else if (oneEdge instanceof Inhibition) {
			attr = String.valueOf(((Inhibition) oneEdge).isAbsoluteInhibition());
			el.addChild(createElSub(attr, "absoluteInhibition"));
		}
		a.appendNonRDFAnnotation(el);
		return a;
	}

	private XMLNode createElSub(String attr, String name) {
		if (attr != "" && attr != null && !attr.equals("null") && !attr.equals("[]")) {
			XMLNode elSub = new XMLNode(new XMLNode(new XMLTriple(name, "", ""), new XMLAttributes()));
			elSub.addAttr(name, attr);
			return elSub;
		} else {
			return null;
		}
	}
}
