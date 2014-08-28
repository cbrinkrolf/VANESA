package xmlInput.sbml;

import graph.CreatePathway;
import graph.gui.Parameter;
import gui.MainWindowSingelton;
import gui.RangeSelector;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import petriNet.PNEdge;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.Activation;
import biologicalObjects.edges.BindingAssociation;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Compound;
import biologicalObjects.edges.Dephosphorylation;
import biologicalObjects.edges.Dissociation;
import biologicalObjects.edges.Expression;
import biologicalObjects.edges.Glycosylation;
import biologicalObjects.edges.HiddenCompound;
import biologicalObjects.edges.IndirectEffect;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.KEGGEdge;
import biologicalObjects.edges.Methylation;
import biologicalObjects.edges.Phosphorylation;
import biologicalObjects.edges.PhysicalInteraction;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.ReactionPairEdge;
import biologicalObjects.edges.Repression;
import biologicalObjects.edges.StateChange;
import biologicalObjects.edges.Ubiquitination;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.KEGGNode;

/**
 * To read a SBML file and put the results on the graph. A SBML which has been
 * passed over to an instance of this class will be parsed to the VANESA graph.
 * 
 * @author Annika and Sandra
 * 
 */
public class JSBMLinput {

	/**
	 * data for the graph
	 */
	private Pathway pathway = null;
	/**
	 * currently handled bna
	 */
	private BiologicalNodeAbstract bna = null;
	/**
	 * currently handled bea
	 */
	private BiologicalEdgeAbstract bea = null;
	private final Hashtable<Integer, BiologicalNodeAbstract> nodes = new Hashtable<Integer, BiologicalNodeAbstract>();
	
	private Hashtable<BiologicalNodeAbstract, Integer> bna2Ref = new Hashtable<BiologicalNodeAbstract, Integer>();

	public JSBMLinput() {

	}

	public String loadSBMLFile(File file) {
		//System.out.println("neu");
		String message = "Import was successful";
		Document doc = null;

		// siehe http://www.javabeginners.de/XML/XML-Datei_lesen.php
		// create document
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(file);
		} catch (JDOMException | IOException e) {
			message = "An error occured";
		}
		if (pathway == null) {
			pathway = new CreatePathway(file.getName()).getPathway();
		}
		pathway.setFilename(file);
		pathway.getGraph().lockVertices();
		pathway.getGraph().stopVisualizationModel();
		// get root-element
		Element sbmlNode = doc.getRootElement();
		Element modelNode = sbmlNode.getChild("model", null);
		List<Element> modelNodeChildren = modelNode.getChildren();

		Element annotationNode = modelNode.getChild("annotation", null);
		createAnnotation(annotationNode);

		// not needed yet
		// Element compartmentNode = modelNode
		// .getChild("listOfCompartments", null);
		// createCompartment(compartmentNode);

		Element speciesNode = modelNode.getChild("listOfSpecies", null);
		createSpecies(speciesNode);
		handleReferences();
		Element reactionNode = modelNode.getChild("listOfReactions", null);
		createReaction(reactionNode);
		
		buildUpHierarchy(annotationNode);
		// refresh view
		try {
			this.pathway.getGraph().unlockVertices();
			this.pathway.getGraph().restartVisualizationModel();
			MainWindowSingelton.getInstance().updateProjectProperties();
			MainWindowSingelton.getInstance().updateOptionPanel();

		} catch (Exception ex) {
			message = "An error occured during the loading.";
		}
		return message;
	}

	/**
	 * creates the annotation of the model
	 * 
	 * @param annotationNode
	 */
	private void createAnnotation(Element annotationNode) {
		if (annotationNode == null) {
			return;
		}
		List<Element> annotationNodeChildren = annotationNode.getChildren();
		int size = annotationNodeChildren.size();
		Element modelNode = annotationNode.getChild("model", null);
		// get the information if the imported net is a Petri net
		Element isPetriNetNode = modelNode.getChild("isPetriNet", null);
		Boolean isPetri = Boolean.parseBoolean(isPetriNetNode
				.getAttributeValue("isPetriNet"));
		this.pathway.setPetriNet(isPetri);
		// get the ranges if present
		Element rangeNode = modelNode.getChild("listOfRanges", null);
		if (rangeNode != null) {
			List<Element> rangeNodeChildren = rangeNode.getChildren();
			size = rangeNodeChildren.size();
			for (int i = 0; i < size; i++) {
				Element range = rangeNodeChildren.get(i);
				addRange(range);
			}
		}
	}

	/**
	 * creates the compartments not needed yet
	 * 
	 * @param compartmentNode
	 */
	private void createCompartment(Element compartmentNode) {
		// if(compartmentNode==null){
		// return;
		// }
		// List<Element> compartmentNodeChildren =
		// compartmentNode.getChildren();
		// int size = compartmentNodeChildren.size();
	}

	/**
	 * creates the reactions
	 * 
	 * @param reactionNode
	 */
	private void createReaction(Element reactionNode) {
		if (reactionNode == null) {
			return;
		}
		List<Element> reactionNodeChildren = reactionNode.getChildren();
		int size = reactionNodeChildren.size();
		// for each reaction
		for (int i = 0; i < size; i++) {
			Element reaction = reactionNodeChildren.get(i);
			Element annotation = reaction.getChild("annotation", null);
			Element reacAnnotation = annotation.getChild("reac", null);
			// test which bea has to be created
			Element elSub = reacAnnotation.getChild("BiologicalElement", null);
			String biologicalElement = elSub
					.getAttributeValue("BiologicalElement");
			// get name and label to create the bea
			String name = reaction.getAttributeValue("name");
			if (name == null) {
				name = "";
			}
			elSub = reacAnnotation.getChild("Label", null);
			String label = "";
			if (elSub != null) {
				label = elSub.getAttributeValue("Label");
			}
			// get from an to nodes for the reaction
			Element rectantsNode = reaction.getChild("listOfReactants", null);
			Element rectant = rectantsNode.getChild("speciesReference", null);
			String id = rectant.getAttributeValue("species");
			String[] tmp = id.split("_");
			int from = Integer.parseInt(tmp[1]);
			Element productsNode = reaction.getChild("listOfProducts", null);
			Element product = productsNode.getChild("speciesReference", null);
			id = product.getAttributeValue("species");
			tmp = id.split("_");
			int to = Integer.parseInt(tmp[1]);
			String attr = "";

			switch (biologicalElement) {
			case Elementdeclerations.compoundEdge:
				bea = new Compound(label, name, nodes.get(from), nodes.get(to));
				break;
			case Elementdeclerations.physicalInteraction:
				bea = new PhysicalInteraction(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.hiddenCompoundEdge:
				bea = new HiddenCompound(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.reactionEdge:
				// System.out.println("reaction edge");
				bea = new ReactionEdge(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.reactionPair:
				// System.out.println("reaction pair");
				bea = new ReactionPair(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.activationEdge:
				bea = new Activation(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.inhibitionEdge:
				bea = new Inhibition(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.expressionEdge:
				bea = new Expression(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.repressionEdge:
				bea = new Repression(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.indirectEffectEdge:
				bea = new IndirectEffect(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.stateChangeEdge:
				bea = new StateChange(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.bindingEdge:
				bea = new BindingAssociation(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.dissociationEdge:
				bea = new Dissociation(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.phosphorylationEdge:
				bea = new Phosphorylation(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.dephosphorylationEdge:
				//System.out.println("dephos:" + biologicalElement);
				bea = new Dephosphorylation(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.glycosylationEdge:
				bea = new Glycosylation(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.ubiquitinationEdge:
				bea = new Ubiquitination(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.methylationEdge:
				bea = new Methylation(label, name, nodes.get(from),
						nodes.get(to));
				break;
			case Elementdeclerations.pnDiscreteEdge:
				// TODO: ???
				elSub = reacAnnotation.getChild("Function", null);
				attr = "";
				if (elSub != null) {
					attr = elSub.getAttributeValue("Function");
				}
				bea = new PNEdge(nodes.get(from), nodes.get(to), label, name,
						biologicalElements.Elementdeclerations.pnDiscreteEdge,
						attr);
				elSub = reacAnnotation.getChild("ActivationProbability", null);
				if (elSub != null) {
					attr = elSub.getAttributeValue("ActivationProbability");
				}
				((PNEdge) bea).setActivationProbability(Double
						.parseDouble(attr));
				break;
			case Elementdeclerations.pnContinuousEdge:
				elSub = reacAnnotation.getChild("Function", null);
				attr = "";
				if (elSub != null) {
					attr = elSub.getAttributeValue("Function");
				}
				bea = new PNEdge(
						nodes.get(from),
						nodes.get(to),
						label,
						name,
						biologicalElements.Elementdeclerations.pnContinuousEdge,
						attr);
				elSub = reacAnnotation.getChild("ActivationProbability", null);
				if (elSub != null) {
					attr = elSub.getAttributeValue("ActivationProbability");
				}
				 ((PNEdge) bea).setActivationProbability(Double
							.parseDouble(attr));
				break;
			case Elementdeclerations.pnInhibitionEdge:
				elSub = reacAnnotation.getChild("Function", null);
				attr = "";
				if (elSub != null) {
					attr = elSub.getAttributeValue("Function");
				}
				bea = new PNEdge(
						nodes.get(from),
						nodes.get(to),
						label,
						name,
						biologicalElements.Elementdeclerations.pnInhibitionEdge,
						attr);
				((PNEdge) bea).setActivationProbability(Double
						.parseDouble(attr));
				break;
			default:
				//System.out.println(biologicalElement);
				break;
			}
			if (bea != null) {
				// set ID of the reaction
				id = reaction.getAttributeValue("id");
				tmp = id.split("_");
				bea.setID(Integer.parseInt(tmp[1]));
				// get additional information
				List<Element> reacAnnotationChildren = reacAnnotation
						.getChildren();
				for (int j = 0; j < reacAnnotationChildren.size(); j++) {
					// go through all Nodes and look up what is set
					Element child = reacAnnotationChildren.get(j);
					handleEdgeInformation(child.getName(), child);
				}
				this.pathway.addEdge(bea);
			}
			// reset because bea is global defined
			bea = null;
		}
	}

	/**
	 * creates the species
	 * 
	 * @param speciesNode
	 */
	private void createSpecies(Element speciesNode) {
		if (speciesNode == null) {
			return;
		}
		List<Element> speciesNodeChildren = speciesNode.getChildren();
		int size = speciesNodeChildren.size();

		String pathwayLink = null;

		// for each species
		for (int i = 0; i < size; i++) {
			Element species = speciesNodeChildren.get(i);
			Element annotation = species.getChild("annotation", null);
			Element specAnnotation = annotation.getChild("spec", null);

			// test which bna has to be created
			Element elSub = specAnnotation.getChild("BiologicalElement", null);
			String biologicalElement = elSub
					.getAttributeValue("BiologicalElement");
			// get name and label to create the bna
			String name = species.getAttributeValue("name");
			if (name == null) {
				name = "";
			}
			elSub = specAnnotation.getChild("Label", null);
			String label = elSub.getAttributeValue("Label");
			String attr;
			switch (biologicalElement) {
			case Elementdeclerations.enzyme:
				bna = new biologicalObjects.nodes.Enzyme(label, name);
				break;
			case Elementdeclerations.others:
				bna = new biologicalObjects.nodes.Other(label, name);
				break;
			case Elementdeclerations.complex:
				bna = new biologicalObjects.nodes.Complex(label, name);
				break;
			case Elementdeclerations.degraded:
				bna = new biologicalObjects.nodes.Degraded(label, name);
				break;
			case Elementdeclerations.dna:
				bna = new biologicalObjects.nodes.DNA(label, name);
				break;
			case Elementdeclerations.homodimerFormation:
				bna = new biologicalObjects.nodes.HomodimerFormation(label,
						name);
				break;
			case Elementdeclerations.ligandBinding:
				bna = new biologicalObjects.nodes.LigandBinding(label, name);
				break;
			case Elementdeclerations.membraneChannel:
				bna = new biologicalObjects.nodes.MembraneChannel(label, name);
				break;
			case Elementdeclerations.membraneReceptor:
				bna = new biologicalObjects.nodes.Receptor(label, name);
				break;
			case Elementdeclerations.mRNA:
				bna = new biologicalObjects.nodes.MRNA(label, name);
				elSub = specAnnotation.getChild("NtSequence", null);
				attr = elSub.getAttributeValue("NtSequence");
				((biologicalObjects.nodes.MRNA) bna).setNtSequence(attr);
				break;
			case Elementdeclerations.orthologGroup:
				bna = new biologicalObjects.nodes.OrthologGroup(label, name);
				break;
			case Elementdeclerations.pathwayMap:
				bna = new biologicalObjects.nodes.PathwayMap(label, name);
				elSub = specAnnotation.getChild("PathwayLink", null);
				if (elSub != null) {
					pathwayLink = String.valueOf(elSub
							.getAttributeValue("PathwayLink"));
				}
				// if (pathwayLink != null) {
				// TODO: ???
				// try {
				// new File("Temp").delete();
				// FileWriter w = new FileWriter(new File("Temp"), false);
				// w.write(pathway);
				// w.close();
				// File file = new File("Temp");
				// Pathway newPW = new Pathway(label);
				// new VAMLInput(file, newPW);
				// ((PathwayMap) bna).setPathwayLink(newPW);
				// newPW.setParent(pw);
				// } catch (Exception e1) {
				// e1.printStackTrace();
				// }
				// }
				break;
			case Elementdeclerations.inhibitor:
				bna = new biologicalObjects.nodes.Inhibitor(label, name);
				break;
			case Elementdeclerations.protein:
				bna = new biologicalObjects.nodes.Protein(label, name);
				break;
			case Elementdeclerations.receptor:
				bna = new biologicalObjects.nodes.Receptor(label, name);
				break;
			case Elementdeclerations.sRNA:
				bna = new biologicalObjects.nodes.SRNA(label, name);
				elSub = specAnnotation.getChild("NtSequence", null);
				attr = String.valueOf(elSub.getAttributeValue("NtSequence"));
				((biologicalObjects.nodes.SRNA) bna).setNtSequence(attr);
				break;
			case Elementdeclerations.smallMolecule:
				bna = new biologicalObjects.nodes.SmallMolecule(label, name);
				break;
			case Elementdeclerations.solubleReceptor:
				bna = new biologicalObjects.nodes.SolubleReceptor(label, name);
				break;
			case Elementdeclerations.transcriptionFactor:
				bna = new biologicalObjects.nodes.TranscriptionFactor(label,
						name);
				break;
			case Elementdeclerations.glycan:
				bna = new biologicalObjects.nodes.Glycan(label, name);
				break;
			case Elementdeclerations.collector:
				bna = new biologicalObjects.nodes.CollectorNode(label, name);
				break;
			case Elementdeclerations.compound:
				bna = new biologicalObjects.nodes.CompoundNode(label, name);
				break;
			case Elementdeclerations.disease:
				bna = new biologicalObjects.nodes.Disease(label, name);
				break;
			case Elementdeclerations.drug:
				bna = new biologicalObjects.nodes.Drug(label, name);
				break;
			case Elementdeclerations.gene:
				bna = new biologicalObjects.nodes.Gene(label, name);
				break;
			case Elementdeclerations.go:
				bna = new biologicalObjects.nodes.GeneOntology(label, name);
				break;
			case Elementdeclerations.reaction:
				bna = new biologicalObjects.nodes.Reaction(label, name);
				break;
			case Elementdeclerations.matrix:
				bna = new biologicalObjects.nodes.Matrix(label, name);
				break;
			case Elementdeclerations.factor:
				bna = new biologicalObjects.nodes.Factor(label, name);
				break;
			case Elementdeclerations.fragment:
				bna = new biologicalObjects.nodes.Fragment(label, name);
				break;
			case Elementdeclerations.site:
				bna = new biologicalObjects.nodes.Site(label, name);
				break;
			case Elementdeclerations.place:
				//System.out.println("plce");
				bna = new petriNet.Place(label, name, 1.0, true);
				elSub = specAnnotation.getChild("token", null);
				attr = String.valueOf(elSub.getAttributeValue("token"));
				((petriNet.Place) bna).setToken(Double.parseDouble(attr));
				elSub = specAnnotation.getChild("tokenMin", null);
				attr = String.valueOf(elSub.getAttributeValue("tokenMin"));
				((petriNet.Place) bna).setTokenMin(Double.parseDouble(attr));
				elSub = specAnnotation.getChild("tokenMax", null);
				attr = String.valueOf(elSub.getAttributeValue("tokenMax"));
				((petriNet.Place) bna).setTokenMax(Double.parseDouble(attr));
				elSub = specAnnotation.getChild("tokenStart", null);
				attr = String.valueOf(elSub.getAttributeValue("tokenStart"));
				((petriNet.Place) bna).setTokenStart(Double.parseDouble(attr));
				((petriNet.Place) bna).setDiscrete(true);
				break;
			case Elementdeclerations.s_place:
				bna = new petriNet.Place(label, name, 1.0, false);
				elSub = specAnnotation.getChild("token", null);
				attr = String.valueOf(elSub.getAttributeValue("token"));
				//System.out.println(attr);
				((petriNet.Place) bna).setToken(Double.parseDouble(attr));
				elSub = specAnnotation.getChild("tokenMin", null);
				attr = String.valueOf(elSub.getAttributeValue("tokenMin"));
				((petriNet.Place) bna).setTokenMin(Double.parseDouble(attr));
				elSub = specAnnotation.getChild("tokenMax", null);
				attr = String.valueOf(elSub.getAttributeValue("tokenMax"));
				((petriNet.Place) bna).setTokenMax(Double.parseDouble(attr));
				elSub = specAnnotation.getChild("tokenStart", null);
				attr = String.valueOf(elSub.getAttributeValue("tokenStart"));
				((petriNet.Place) bna).setTokenStart(Double.parseDouble(attr));
				((petriNet.Place) bna).setDiscrete(false);
				break;
			case Elementdeclerations.discreteTransition:
				bna = new petriNet.DiscreteTransition(label, name);
				elSub = specAnnotation.getChild("delay", null);
				attr = String.valueOf(elSub.getAttributeValue("delay"));
				((petriNet.DiscreteTransition) bna).setDelay(Double
						.parseDouble(attr));
				break;
			case Elementdeclerations.continuousTransition:
				bna = new petriNet.ContinuousTransition(label, name);
				elSub = specAnnotation.getChild("maximumSpeed", null);
				attr = String.valueOf(elSub.getAttributeValue("maximumSpeed"));
				((petriNet.ContinuousTransition) bna).setMaximumSpeed(attr);
				if (attr == null || attr.equals("")) {
					((petriNet.ContinuousTransition) bna).setMaximumSpeed("1");
				}
				break;
			case Elementdeclerations.stochasticTransition:
				bna = new petriNet.StochasticTransition(label, name);
				elSub = specAnnotation.getChild("distribution", null);
				attr = String.valueOf(elSub.getAttributeValue("distribution"));
				((petriNet.StochasticTransition) bna).setDistribution(attr);
				break;
			}
			// test which annotations are set
			// only if bna was created above
			if (bna != null) {
				// set id and compartment of the bna
				String id = species.getAttributeValue("id");
				String[] idSplit = id.split("_");
				int idInt = Integer.parseInt(idSplit[1]);
				bna.setID(idInt);
				String compartment = species.getAttributeValue("compartment");
				String[] comp = compartment.split("_");
				bna.setCompartment(comp[1]);
				// get additional information
				List<Element> specAnnotationChildren = specAnnotation
						.getChildren();
				for (int j = 0; j < specAnnotationChildren.size(); j++) {
					// go through all Nodes and look up what is set
					Element child = specAnnotationChildren.get(j);
					handleNodeInformation(child.getName(), child);
				}
				// get the coordinates of the bna
				elSub = specAnnotation.getChild("Coordinates", null);
				Element elSubSub = elSub.getChild("x_Coordinate", null);
				Double xCoord = new Double(
						elSubSub.getAttributeValue("x_Coordinate"));
				elSubSub = elSub.getChild("y_Coordinate", null);
				Double yCoord = new Double(
						elSubSub.getAttributeValue("y_Coordinate"));
				Point2D.Double p = new Point2D.Double(xCoord, yCoord);
				
				elSub = specAnnotation.getChild("Parameters", null);
				
				//elSubSub = elSub.getChild("x_Coordinate", null);
				
				ArrayList<Parameter> parameters = new ArrayList<Parameter>();
				Parameter param;
				String pname = "";
				Double value = 0.0;
				String unit = "";
				for(int j = 0; j<elSub.getChildren().size(); j++){
					elSubSub = 	elSub.getChildren().get(j);
					//System.out.println(elSubSub.getChild("Name", null).getAttributeValue("Name"));
					pname = elSubSub.getChild("Name", null).getAttributeValue("Name");
					value = Double.valueOf(elSubSub.getChild("Value", null).getAttributeValue("Value"));
					unit = elSubSub.getChild("Unit", null).getAttributeValue("Unit");
					param = new Parameter(pname, value, unit);
					parameters.add(param);
				}
				bna.setParameters(parameters);
				// add bna to the graph
				this.pathway.addVertex(bna, p);
				// add bna to hashtable
				nodes.put(idInt, bna);
			}
			// reset because bna is global defined
			bna = null;
		}
	}
	
	/**
	 * Coarses the nodes as described in the loaded sbml file.
	 * @param annotationNode Annotation Area of the imported model.
	 * @author tloka
	 */
	private void buildUpHierarchy(Element annotationNode){
		
		if(annotationNode == null)
			return;
		
		Element modelNode = annotationNode.getChild("model", null);
		if(modelNode == null)
			return;
		
		Element hierarchyList = modelNode.getChild("listOfHierarchies", null);
		if(hierarchyList == null)
			return;
		
		Map<Integer, Set<Integer>> hierarchyMap = new HashMap<Integer, Set<Integer>>();
		Map<Integer, String> coarseNodeLabels = new HashMap<Integer, String>();
		
		for(Element coarseNode : hierarchyList.getChildren("coarseNode", null)){
			if(coarseNode.getChildren("child", null)==null)
				continue;
			
			Set<Integer> childrenSet = new HashSet<Integer>();
			for(Element childElement : coarseNode.getChildren("child", null)){
				Integer childNode = Integer.parseInt(childElement.getAttributeValue("id").split("_")[1]);
				childrenSet.add(childNode);
			}
			
			Integer id = Integer.parseInt(coarseNode.getAttributeValue("id").split("_")[1]);
			hierarchyMap.put(id, childrenSet);
			coarseNodeLabels.put(id, coarseNode.getAttributeValue("label"));
		}
		
		int coarsedNodes = 0;
		
		while(coarsedNodes<hierarchyMap.size()){
			for(Integer parent : hierarchyMap.keySet()){
				boolean toBeCoarsed = true;
				Set<BiologicalNodeAbstract> coarseNodes = new HashSet<BiologicalNodeAbstract>();
				for(Integer child : hierarchyMap.get(parent)){
					if(!nodes.containsKey(child) || nodes.containsKey(parent)){
						toBeCoarsed = false;
						break;
					}
					coarseNodes.add(nodes.get(child));
				}
				if(toBeCoarsed){
					BiologicalNodeAbstract coarseNode = BiologicalNodeAbstract.coarse(coarseNodes, 
							parent, coarseNodeLabels.get(parent));
					nodes.put(parent, coarseNode);
					coarsedNodes+=1;
				}
			}
		}
	}

	private void handleEdgeInformation(String attrtmp, Element child) {
		String value = child.getAttributeValue(attrtmp);
		switch (attrtmp) {
		// standard cases
		case "IsWeighted":
			bea.setWeighted(Boolean.parseBoolean(value));
			break;
		case "Weight":
			bea.setWeight(Integer.parseInt(value));
			break;
		case "Color":
			Element elSub = child.getChild("RGB", null);
			int rgb = Integer.parseInt(elSub.getAttributeValue("RGB"));
			bea.setColor(new Color(rgb));
			break;
		case "IsDirected":
			bea.setDirected(Boolean.parseBoolean(value));
			break;
		case "Description":
			bea.setDescription(value);
			break;
		case "Comments":
			bea.setComments(value);
			break;
		case "HasFeatureEdge":
			bea.hasFeatureEdge(Boolean.parseBoolean(value));
			break;
		case "HasKEGGEdge":
			bea.hasKEGGEdge(Boolean.parseBoolean(value));
			break;
		case "KEGGEdge":
			bea.setKeggEdge(new KEGGEdge());
			addKEGGEdge(child);
			break;
		case "HasReactionPairEdge":
			bea.hasReactionPairEdge(Boolean.parseBoolean(value));
			break;
		case "ReactionPairEdge":
			bea.setReactionPairEdge(new ReactionPairEdge());
			List<Element> tmp = child.getChildren();
			for (int j = 0; j < tmp.size(); j++) {
				Element tmpi = tmp.get(j);
				String tmpiName = tmpi.getName();
				switch (tmpiName) {
				case "ReactionPairEdgeID":
					bea.getReactionPairEdge().setReactionPairID(value);
					break;
				case "ReactionPairName":
					bea.getReactionPairEdge().setName(value);
					break;
				case "ReactionPairType":
					bea.getReactionPairEdge().setType(value);
					break;
				}
			}
			break;
		}
	}

	/**
	 * Test which Information is set and handle it
	 * 
	 * @param attrtmp
	 */
	private void handleNodeInformation(String attrtmp, Element child) {
		String value = child.getAttributeValue(attrtmp);
		switch (attrtmp) {
		// standard cases
		case "Nodesize":
			Double d = Double.parseDouble(value);
			bna.setNodesize(d);
			break;
		case "Comments":
			bna.setComments(value);
			break;
		case "ElementsVector":
			Vector<String> v = new Vector<String>(
					Arrays.asList(stringToArray(value)));
			bna.setElementsVector(v);
			break;
		case "Description":
			bna.setDescription(value);
			break;
		case "Networklabel":
			// no set-method available
			break;
		case "DB":
			bna.setDB(value);
			break;
		case "Organism":
			bna.setOrganism(value);
			break;
		case "HasKEGGNode":
			Boolean b = Boolean.parseBoolean(value);
			bna.hasKEGGNode(b);
			break;
		case "KEGGNode":
			bna.setKEGGnode(new KEGGNode());
			addKEGGNode(child);
		case "HasDAWISNode":
			b = Boolean.parseBoolean(value);
			bna.hasDAWISNode(b);
			break;
		case "DAWISNode":
			bna.setDAWISNode(new DAWISNode(""));
			addDAWISNode(child);
		case "Color":
			Element elSub = child.getChild("RGB", null);
			int rgb = Integer.parseInt(elSub.getAttributeValue("RGB"));
			bna.setColor(new Color(rgb));
			break;
		case "NodeReference":
			elSub = child.getChild("hasRef", null);
			if(elSub.getAttributeValue("hasRef").equals("true")){
				elSub = child.getChild("RefID", null);
				this.bna2Ref.put(bna, Integer.parseInt(elSub.getAttributeValue("RefID")));
			}
			break;
		// special cases
		case "ElementObject":
			((biologicalObjects.nodes.CollectorNode) bna).setObject(value);
			break;
		case "NtSequence":
			((biologicalObjects.nodes.DNA) bna).setNtSequence(value);
			break;
		case "Cofactor":
			((biologicalObjects.nodes.Enzyme) bna).setCofactor(value);
			break;
		case "Effector":
			((biologicalObjects.nodes.Enzyme) bna).setEffector(value);
			break;
		case "EnzymeClass":
			((biologicalObjects.nodes.Enzyme) bna).setEnzymeClass(value);
			break;
		case "Orthology":
			((biologicalObjects.nodes.Enzyme) bna).setOrthology(value);
			break;
		case "Produkt":
			((biologicalObjects.nodes.Enzyme) bna).setProdukt(value);
			break;
		case "Reaction":
			((biologicalObjects.nodes.Enzyme) bna).setReaction(value);
			break;
		case "Reference":
			b = Boolean.parseBoolean(value);
			((biologicalObjects.nodes.Enzyme) bna).setReference(b);
			break;
		case "Substrate":
			((biologicalObjects.nodes.Enzyme) bna).setSubstrate(value);
			break;
		case "SysName":
			((biologicalObjects.nodes.Enzyme) bna).setSysName(value);
			break;
		case "Proteins":
			((biologicalObjects.nodes.Gene) bna)
					.addProtein(stringToArray(value));
			break;
		case "Enzymes":
			((biologicalObjects.nodes.Gene) bna)
					.addEnzyme(stringToArray(value));
			break;
		case "Specification":
			b = Boolean.parseBoolean(value);
			((biologicalObjects.nodes.PathwayMap) bna).setSpecification(b);
			break;
		case "AaSequence":
			((biologicalObjects.nodes.Protein) bna).setAaSequence(value);
			break;
		case "Formula":
			((biologicalObjects.nodes.SmallMolecule) bna).setFormula(value);
			break;
		case "Mass":
			((biologicalObjects.nodes.SmallMolecule) bna).setMass(value);
			break;
		case "Tarbase_accession":
			((biologicalObjects.nodes.SRNA) bna).setTarbase_accession(value);
			break;
		case "Tarbase_DS":
			((biologicalObjects.nodes.SRNA) bna).setTarbase_DS(value);
			break;
		case "Tarbase_ensemble":
			((biologicalObjects.nodes.SRNA) bna).setTarbase_ensemble(value);
			break;
		case "Tarbase_IS":
			((biologicalObjects.nodes.SRNA) bna).setTarbase_IS(value);
			break;
		}
	}

	private String[] stringToArray(String value) {
		String[] x = value.split(",");
		for (int i = 0; i < x.length; i++) {
			if (i == 0) {
				x[i] = x[i].substring(1);
			} else if (i == x.length - 1) {
				x[i] = x[i].substring(0, x[i].length() - 1);
			}
			x[i] = x[i].trim();
		}
		return x;
	}

	private void addKEGGEdge(Element keggEdge) {
		List<Element> keggEdgeChildren = keggEdge.getChildren();
		KEGGEdge kegg = bea.getKeggEdge();
		for (int i = 0; i < keggEdgeChildren.size(); i++) {
			Element child = keggEdgeChildren.get(i);
			String name = child.getName();
			String value = child.getAttributeValue(name);
			switch (name) {
			case "KEEGReactionID":
				kegg.setKEEGReactionID(value);
				break;
			case "Entry1":
				kegg.setEntry1(value);
				break;
			case "Entry2":
				kegg.setEntry2(value);
				break;
			case "Type":
				kegg.setType(value);
				break;
			case "Description":
				kegg.setDescription(value);
				break;
			case "Name":
				kegg.setName(value);
				break;
			case "Remark":
				kegg.setRemark(value);
				break;
			case "Orthology":
				kegg.setOrthology(value);
				break;
			case "Reference":
				kegg.setReference(value);
				break;
			case "Comment":
				kegg.setComment(value);
				break;
			case "Definition":
				kegg.setDefinition(value);
				break;
			case "Equation":
				kegg.setEquation(value);
				break;
			case "Rpair":
				kegg.setRpair(value);
				break;
			case "Effect":
				kegg.setEffect(value);
				break;
			case "ReactionType":
				kegg.setReactionType(value);
				break;
			case "InvolvedEnzyme":
				kegg.setInvolvedEnzyme(value);
				break;
			// Vectors:
			case "AllProducts":
				String[] s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addProduct(s[j]);
				}
				break;
			case "AllEnzymes":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addEnzyme(s[j]);
				}
				break;
			case "AllSubstrates":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addSubstrate(s[j]);
				}
				break;
			case "Catalysts":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.setCatalysts(s[j]);
				}
				break;
			case "CatalystNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.setCatalystsName(s[j]);
				}
				break;
			case "Inhibitors":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.setInhibitors(s[j]);
				}
				break;
			case "InhibitorNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.setInhibitorsName(s[j]);
				}
				break;
			}
		}
	}

	private void addKEGGNode(Element keggNode) {
		List<Element> keggNodeChildren = keggNode.getChildren();
		KEGGNode kegg = bna.getKEGGnode();
		for (int i = 0; i < keggNodeChildren.size(); i++) {
			// go through all Subnodes and look up what is set
			Element child = keggNodeChildren.get(i);
			String name = child.getName();
			String value = child.getAttributeValue(name);
			switch (name) {
			case "AllInvolvedElements":
				String[] s = value.split(" ");
				for (int j = 0; j < s.length; i++) {
					kegg.addInvolvedElement(s[j]);
				}
				break;
			case "BackgroundColour":
				kegg.setBackgroundColour(value);
				break;
			case "CompoundAtoms":
				kegg.setCompoundAtoms(value);
				break;
			case "CompoundAtomsNr":
				kegg.setCompoundAtomsNr(value);
				break;
			case "CompoundBondNr":
				kegg.setCompoundBondNr(value);
				break;
			case "CompoundBonds":
				kegg.setCompoundBonds(value);
				break;
			case "CompoundComment":
				kegg.setCompoundComment(value);
				break;
			case "CompoundFormula":
				kegg.setCompoundFormula(value);
				break;
			case "CompoundMass":
				kegg.setCompoundMass(value);
				break;
			case "CompoundModule":
				kegg.setCompoundModule(value);
				break;
			case "CompoundOrganism":
				kegg.setCompoundOrganism(value);
				break;
			case "CompoundRemarks":
				kegg.setCompoundRemarks(value);
				break;
			case "CompoundSequence":
				kegg.setCompoundSequence(value);
				break;
			case "ForegroundColour":
				kegg.setForegroundColour(value);
				break;
			case "GeneAAseq":
				kegg.setGeneAAseq(value);
				break;
			case "GeneAAseqNr":
				kegg.setGeneAAseqNr(value);
				break;
			case "GeneCodonUsage":
				kegg.setGeneCodonUsage(value);
				break;
			case "GeneDefinition":
				kegg.setGeneDefinition(value);
				break;
			case "GeneEnzyme":
				kegg.setGeneEnzyme(value);
				break;
			case "GeneName":
				kegg.setGeneName(value);
				break;
			case "GeneNtSeq":
				kegg.setGeneNtSeq(value);
				break;
			case "GeneNtSeqNr":
				kegg.setGeneNtseqNr(value);
				break;
			case "GeneOrthology":
				kegg.setGeneOrthology(value);
				break;
			case "GeneOrthologyName":
				kegg.setGeneOrthologyName(name);
				break;
			case "GenePosition":
				kegg.setGenePosition(value);
				break;
			case "GlycanBracket":
				kegg.setGlycanBracket(value);
				break;
			case "GlycanComposition":
				kegg.setGlycanComposition(value);
				break;
			case "GlycanEdge":
				kegg.setGlycanEdge(value);
				break;
			case "GlycanName":
				kegg.setGlycanName(value);
				break;
			case "GlycanNode":
				kegg.setGlycanNode(value);
				break;
			case "GlycanOrthology":
				kegg.setGlycanOrthology(value);
				break;
			case "Height":
				kegg.setHeight(value);
				break;
			case "Keggcofactor":
				kegg.setKeggcofactor(value);
				break;
			case "KeggComment":
				kegg.setKeggComment(value);
				break;
			case "KEGGComponent":
				kegg.setKeggComment(value);
				break;
			case "Keggeffector":
				kegg.setKeggeffector(value);
				break;
			case "KEGGentryID":
				kegg.setKEGGentryID(value);
				break;
			case "KEGGentryLink":
				kegg.setKEGGentryLink(value);
				break;
			case "KEGGentryMap":
				kegg.setKEGGentryMap(value);
				break;
			case "KEGGentryName":
				kegg.setKEGGentryName(value);
				break;
			case "KEGGentryReaction":
				kegg.setKEGGentryReaction(value);
				break;
			case "KEGGentryType":
				kegg.setKEGGentryType(value);
				break;
			case "KeggenzymeClass":
				kegg.setKeggenzymeClass(value);
				break;
			case "Keggorthology":
				kegg.setKeggorthology(value);
				break;
			case "KEGGPathway":
				kegg.setKEGGPathway(value);
				break;
			case "Keggprodukt":
				kegg.setKeggprodukt(value);
				break;
			case "Keggreaction":
				kegg.setKeggreaction(value);
				break;
			case "Keggreference":
				kegg.setKeggreference(value);
				break;
			case "Keggsubstrate":
				kegg.setKeggsubstrate(value);
				break;
			case "KeggsysName":
				kegg.setKeggsysName(value);
				break;
			case "NodeLabel":
				kegg.setNodeLabel(value);
				break;
			case "Shape":
				kegg.setShape(value);
				break;
			case "Width":
				kegg.setWidth(value);
				break;
			case "AllDBLinksAsVector":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addDBLink(s[j]);
				}
				break;
			case "AllGeneMotifsAsVector":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addGeneMotif(s[j]);
				}
				break;
			case "AllNamesAsVector":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addAlternativeName(s[j]);
				}
				break;
			case "AllPathwayLinksAsVector":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addPathwayLink(s[j]);
				}
				break;
			case "AllStructuresAsVector":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					kegg.addStructure(s[j]);
				}
				break;
			}
		}
	}

	private void addDAWISNode(Element dawisNode) {
		List<Element> dawisNodeChildren = dawisNode.getChildren();
		DAWISNode dawis = bna.getDAWISNode();
		for (int i = 0; i < dawisNodeChildren.size(); i++) {
			// go through all Subnodes and look up what is set
			Element child = dawisNodeChildren.get(i);
			String name = child.getName();
			String value = child.getAttributeValue(name);
			switch (name) {
			case "Activity":
				dawis.setActivity(value);
				break;
			case "AminoAcidSeq":
				dawis.setAminoAcidSeq(value);
				break;
			case "AminoAcidSeqLength":
				dawis.setAminoAcidSeqLength(value);
				break;
			case "Atoms":
				dawis.setAtoms(value);
				break;
			case "AtomsNumber":
				dawis.setAtomsNumber(value);
				break;
			case "Bonds":
				dawis.setBonds(value);
				break;
			case "BondsNumber":
				dawis.setBondsNumber(value);
				break;
			case "Bracket":
				dawis.setBracket(value);
				break;
			case "CodonUsage":
				dawis.setCodonUsage(value);
				break;
			case "Comment":
				dawis.setComment(value);
				break;
			case "ComplexName":
				dawis.setComplexName(value);
				break;
			case "Component":
				dawis.setComponent(value);
				break;
			case "Composition":
				dawis.setComposition(value);
				break;
			case "DataLoaded":
				dawis.setDataLoaded();
				break;
			case "DB":
				dawis.setDB(value);
				break;
			case "Definition":
				dawis.setDefinition(value);
				break;
			case "DiagnosisType":
				dawis.setDiagnosisType(value);
				break;
			case "Disorder":
				dawis.setDisorder(value);
				break;
			case "Edge":
				dawis.setEdge(value);
				break;
			case "Effect":
				dawis.setEffect(value);
				break;
			case "Element":
				dawis.setElement(value);
				break;
			case "EncodingGene":
				dawis.setEncodingGene(value);
				break;
			case "EndPoint":
				dawis.setEndPoint(value);
				break;
			case "Equation":
				dawis.setEquation(value);
				break;
			case "FactorClass":
				dawis.setFactorClass(value);
				break;
			case "Formula":
				dawis.setFormula(value);
				break;
			case "ID":
				dawis.setID(value);
				break;
			case "Information":
				dawis.setInformation(value);
				break;
			case "IsoelectricPoint":
				dawis.setIsoelectricPoint(value);
				break;
			case "IsoformenNumber":
				dawis.setIsoformenNumber(value);
				break;
			case "Module":
				dawis.setModule(value);
				break;
			case "Name":
				dawis.setName(value);
				break;
			case "Node":
				dawis.setNode(value);
				break;
			case "NucleotidSequence":
				dawis.setNucleotidSequence(value);
				break;
			case "NucleotidSequenceLength":
				dawis.setNucleotidSequenceLength(value);
				break;
			case "Object":
				dawis.setObject(value);
				break;
			case "Ontology":
				dawis.setOrthology(value);
				break;
			case "Organelle":
				dawis.setOrganelle(value);
				break;
			case "Organism":
				dawis.setOrganism(value);
				break;
			case "Original":
				dawis.setOriginal(value);
				break;
			case "PathwayMap":
				dawis.setPathwayMap(value);
				break;
			case "Position":
				dawis.setPosition(value);
				break;
			case "RDM":
				dawis.setRDM(value);
				break;
			case "Remarks":
				dawis.setRemark(value);
				break;
			case "Repeat":
				dawis.setRepeat(value);
				break;
			case "SequenceSource":
				dawis.setSequenceSource(value);
				break;
			case "SpecificityNeg":
				dawis.setSpecificityNeg(value);
				break;
			case "SpecificityPos":
				dawis.setSpecificityPos(value);
				break;
			case "StartPoint":
				dawis.setStartPoint(value);
				break;
			case "Target":
				dawis.setTarget(value);
				break;
			case "TransfacGene":
				dawis.setTransfacGene(value);
				break;
			case "Type":
				dawis.setType(value);
				break;
			case "Weigth":
				dawis.setWeight(value);
				break;
			// information which was saved as a String from a Vector
			case "Accessionnumbers":
				String[] s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setAccessionnumber(s[j]);
				}
				break;
			case "Catalysts":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setCatalysts(s[j]);
				}
				break;
			case "CatalystNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setCatalystsName(s[j]);
				}
				break;
			case "Classifications":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setClassification(s[j]);
				}
				break;
			case "Cofactors":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setCofactors(s[j]);
				}
				break;
			case "CofactorNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setCofactorsName(s[j]);
				}
				break;
			case "DBLinks":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setDBLink(s[j]);
				}
				break;
			case "Domains":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setDomain(s[j]);
				}
				break;
			case "EffectorNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setEffectorsName(s[j]);
				}
				break;
			case "Effectors":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setEffectors(s[j]);
				}
				break;
			case "CollectorElements":
				s = stringToArray(value);
				dawis.setCollectorElements(s);
				break;
			case "Features":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setFeature(s[j]);
				}
				break;
			case "Functions":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setFunction(s[j]);
				}
				break;
			case "GeneNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setGeneName(s[j]);
				}
				break;
			case "Inhibitors":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setInhibitors(s[j]);
				}
				break;
			case "InhibitorNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setInhibitorsName(s[j]);
				}
				break;
			case "Locations":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setLocation(s[j]);
				}
				break;
			case "Methods":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setMethod(s[j]);
				}
				break;
			case "Motifs":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setMotif(s[j]);
				}
				break;
			case "Orthologies":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setOrthology(s[j]);
				}
				break;
			case "PDBs":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setPDBs(s[j]);
				}
				break;
			case "ProductNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setProductsName(s[j]);
				}
				break;
			case "Products":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setProducts(s[j]);
				}
				break;
			case "Processes":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setProzess(s[j]);
				}
				break;
			case "References":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setReference(s[j]);
				}
				break;
			case "Subfamilies":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setSubfamily(s[j]);
				}
				break;
			case "SubstrateNames":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setSubstratesName(s[j]);
				}
				break;
			case "Substrates":
				s = stringToArray(value);
				for (int j = 0; j < s.length; j++) {
					dawis.setSubstrates(s[j]);
				}
				break;
			}
		}
	}

	/**
	 * adds ranges to the graph
	 * 
	 * @param rangeElement
	 */
	private void addRange(Element rangeElement) {
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("title", "");
		String[] keys = { "textColor", "outlineType", "fillColor", "alpha",
				"maxY", "outlineColor", "maxX", "isEllipse", "minX", "minY",
				"titlePos" };
		for (int i = 0; i < keys.length; i++) {
			Element tmp = rangeElement.getChild(keys[i], null);
			String value = tmp.getAttributeValue(keys[i]);
			attrs.put(keys[i], value);
		}
		RangeSelector.getInstance().addRangesInMyGraph(pathway.getGraph(),
				attrs);
	}
	
	private void handleReferences(){
		Iterator<BiologicalNodeAbstract> it = bna2Ref.keySet().iterator();
		BiologicalNodeAbstract bna;
		while(it.hasNext()){
			bna = it.next();
			bna.setRef(this.nodes.get(bna2Ref.get(bna)));
		}
	}
}
