package xmlInput.sbml;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graph.groups.Group;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderSAX2Factory;
import org.xml.sax.InputSource;

import biologicalElements.Elementdeclerations;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.BiologicalEdgeAbstractFactory;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.edges.ReactionPairEdge;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.KEGGNode;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.RNA;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.CreatePathway;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.RangeSelector;

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
	private final HashMap<String, Integer> string2id = new HashMap<String, Integer>();
	private boolean coarsePathway = false;

	private Hashtable<BiologicalNodeAbstract, Integer> bna2Ref = new Hashtable<BiologicalNodeAbstract, Integer>();

	private boolean reverseEngineering = false;

	private ArrayList<ArrayList<String>> inputgroups = new ArrayList<ArrayList<String>>();

	public JSBMLinput() {

	}

	public JSBMLinput(Pathway pw) {
		pathway = pw;
	}

	public String loadSBMLFile(InputStream is, String name) {
		// System.out.println("neu");

		String message = "";
		Document doc = null;
		InputSource in = new InputSource(is);

		/*
		 * SBMLReader reader = new SBMLReader(); try { SBMLDocument document =
		 * reader.readSBMLFromStream(is); document.getNumErrors();
		 * //document.getModel().get } catch (XMLStreamException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 */

		// siehe http://www.javabeginners.de/XML/XML-Datei_lesen.php
		// create document


		// changed empty constructor SAXBuilder builder = new SAXBuilder(); to following, cause open JDK got an
		// error with empty constructor
		// see: https://stackoverflow.com/questions/11409025/exceptionininitializererror-while-creating-ant-custom-task
		SAXBuilder builder = new SAXBuilder(new XMLReaderSAX2Factory(false, "org.apache.xerces.parsers.SAXParser"));
		try {
			doc = builder.build(in);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
			message = "An error occured";
		}
		if (pathway == null) {
			pathway = new CreatePathway(name).getPathway();
		} else {
			coarsePathway = true;
		}
		if (pathway.getFilename() == null) {
			pathway.setFilename(name);
		}
		// get root-element
		Element sbmlNode = doc.getRootElement();
		Element modelNode = sbmlNode.getChild("model", null);
		// List<Element> modelNodeChildren = modelNode.getChildren();

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

		createGroup();


		// refresh view
		try {
			is.close();
			this.pathway.getGraph().restartVisualizationModel();
			MainWindow.getInstance().updateProjectProperties();
			MainWindow.getInstance().updateOptionPanel();

		} catch (Exception ex) {
			ex.printStackTrace();
			message = "An error occured during the loading.";
		}
		return message;
	}

	/**
	 * Groups and their members are saved in list, cause groups cant be created at this point, because
	 * nodes arent created yet
	 *
	 */
	private void getInputGroups(Element groupNode) {
		if (groupNode == null) {

			// System.out.println("keine gruppen gefunden");
			return;
		}
		List<Element> groupChildren = groupNode.getChildren();
		int size = groupChildren.size();
		for (int i = 0; i < size; i++) {
			Element group = groupChildren.get(i);
			List<Element> groupmembers = group.getChildren();
			int groupSize = groupmembers.size();

			ArrayList<String> tmp = new ArrayList<String>();
			for (int j = 0; j < groupSize; j++) {
				//add nodes mit ID/label to nodes list
				Element node = groupmembers.get(j);
				String label = node.getAttributeValue("Node");
				tmp.add(label);
			}
			inputgroups.add(tmp);
		}

		//System.out.println("es sind gespeicherte gruppen" + inputgroups.size());
	}

	/**
	 * Groups will be created after all nodes are there
	 *
	 */
	private void createGroup() {
		for (int i = 0; i < inputgroups.size(); i++) {

			ArrayList<BiologicalNodeAbstract> nodeslist = new ArrayList<BiologicalNodeAbstract>();
			for (int j = 0; j < inputgroups.get(i).size(); j++) {
				nodeslist.add(nodes.get(Integer.parseInt(inputgroups.get(i).get(j))));
			}
			Group tmp = new Group(nodeslist);
			for (int k = 0; k < nodeslist.size(); k++){
				nodeslist.get(k).setisinGroup(true);
				nodeslist.get(k).setGroup(tmp);
			}
			pathway.groupes.add(tmp);
		}
		inputgroups.clear();
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

		Boolean isPetri = false;
		if (modelNode != null) {
			Element isPetriNetNode = modelNode.getChild("isPetriNet", null);
			isPetri = Boolean.parseBoolean(isPetriNetNode.getAttributeValue("isPetriNet"));
			if (reverseEngineering) {
				isPetri = false;
			}
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
			Element groupNode = modelNode.getChild("listOfGroups", null);
			getInputGroups(groupNode);
		}
		this.pathway.setPetriNet(isPetri);
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
		BiologicalNodeAbstract from;
		BiologicalNodeAbstract to;
		for (int i = 0; i < size; i++) {
			Element reaction = reactionNodeChildren.get(i);
			// test which bea has to be created

			// get name and label to create the bea
			String name = reaction.getAttributeValue("name");
			if (name == null) {
				name = "";
			}

			// get from an to nodes for the reaction
			Element rectantsNode = reaction.getChild("listOfReactants", null);
			Element productsNode = reaction.getChild("listOfProducts", null);
			if (rectantsNode != null && productsNode != null) {

				Element rectant = rectantsNode.getChild("speciesReference", null);
				String id = rectant.getAttributeValue("species");
				// System.out.println(id);
				from = nodes.get(string2id.get(id));

				Element product = productsNode.getChild("speciesReference", null);
				id = product.getAttributeValue("species");
				to = nodes.get(string2id.get(id));
				String attr = "";
				String label = name;

				bea = new ReactionEdge(label, name, from, to);
				bea.setDirected(true);
				bea.setFrom(from);
				bea.setTo(to);
				bea.setLabel(label);
				bea.setName(name);
				
				Element annotation = reaction.getChild("annotation", null);
				if (annotation != null) {
					Element reacAnnotation = annotation.getChild("reac", null);
					if (reacAnnotation != null) {
						Element elSub = reacAnnotation.getChild("BiologicalElement", null);
						String biologicalElement = elSub.getAttributeValue("BiologicalElement");

						elSub = reacAnnotation.getChild("label", null);
						if (elSub != null) {
							label = elSub.getAttributeValue("label");
						}
						bea = BiologicalEdgeAbstractFactory.create(biologicalElement, null);
						bea.setDirected(true);
						bea.setFrom(from);
						bea.setTo(to);
						bea.setLabel(label);
						bea.setName(name);

						// switch (biologicalElement) {
						// case Elementdeclerations.pnEdge:

						elSub = reacAnnotation.getChild("Probability", null);
						if (elSub != null) {
							attr = elSub.getAttributeValue("Probability");
							((PNEdge) bea).setProbability(Double.parseDouble(attr));
						}
						
						elSub = reacAnnotation.getChild("Priority", null);
						if (elSub != null) {
							attr = elSub.getAttributeValue("Priority");
							((PNEdge) bea).setPriority(Integer.parseInt(attr));
						}

						// break;
						// case Elementdeclerations.pnInhibitionEdge:
						// elSub = reacAnnotation.getChild("Function", null);
						// attr = "";
						// if (elSub != null) {
						// attr = elSub.getAttributeValue("Function");
						// }
						// bea = new PNEdge(from, to, label, name,
						// biologicalElements.Elementdeclerations.pnInhibitionEdge,
						// attr);
						// ((PNEdge)
						// bea).setActivationProbability(Double.parseDouble(attr));
						// break;
						// default:
						// System.out.println(biologicalElement);
						// break;
						// }
						// get additional information
						List<Element> reacAnnotationChildren = reacAnnotation.getChildren();
						for (int j = 0; j < reacAnnotationChildren.size(); j++) {
							// go through all Nodes and look up what is set
							Element child = reacAnnotationChildren.get(j);
							handleEdgeInformation(child.getName(), child);
						}
					}
				}

				// set ID of the reaction
				id = reaction.getAttributeValue("id");

				int idint = this.getID(id);
				try {
					if (idint > -1) {
						bea.setID(idint);
					} else {
						bea.setID();
					}
				} catch (IDAlreadyExistException ex) {
					bea.setID();
				}
				this.pathway.addEdge(bea);
				// reset because bea is global defined
				bea = null;
			}
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

			// test which bna has to be created

			// get name and label to create the bna
			String name = species.getAttributeValue("name");
			if (name == null) {
				name = "";
			}

			String label = name;
			bna = new Other(label, name);
			Point2D.Double p = new Point2D.Double(0.0, 0.0);

			Element annotation = species.getChild("annotation", null);
			if (annotation != null) {
				Element specAnnotation = annotation.getChild("spec", null);
				if (specAnnotation != null) {
					Element elSub = specAnnotation.getChild("BiologicalElement", null);
					String biologicalElement = elSub.getAttributeValue("BiologicalElement");
					elSub = specAnnotation.getChild("label", null);

					if (elSub != null) {
						label = elSub.getAttributeValue("label");
					} else {
						elSub = specAnnotation.getChild("Label", null);
						if (elSub != null) {
							label = elSub.getAttributeValue("Label");
						}
					}
					String attr;

					bna = BiologicalNodeAbstractFactory.create(biologicalElement, null);
					if (reverseEngineering) {
						if (bna instanceof Place) {
							bna = BiologicalNodeAbstractFactory.create(Elementdeclerations.smallMolecule, null);
						} else if (bna instanceof Transition) {
							bna = BiologicalNodeAbstractFactory.create(Elementdeclerations.enzyme, null);
						}
					}
					bna.setLabel(label);
					bna.setName(name);
					switch (bna.getBiologicalElement()) {
					case Elementdeclerations.mRNA:
						elSub = specAnnotation.getChild("NtSequence", null);
						if (elSub != null) {
							attr = elSub.getAttributeValue("NtSequence");
							((biologicalObjects.nodes.MRNA) bna).setNtSequence(attr);
						}
						break;
					case Elementdeclerations.pathwayMap:
						elSub = specAnnotation.getChild("PathwayLink", null);
						if (elSub != null) {
							pathwayLink = String.valueOf(elSub.getAttributeValue("PathwayLink"));
						}
						break;
					case Elementdeclerations.sRNA:
						elSub = specAnnotation.getChild("NtSequence", null);
						if (elSub != null) {
							attr = String.valueOf(elSub.getAttributeValue("NtSequence"));
							((biologicalObjects.nodes.SRNA) bna).setNtSequence(attr);
						}
						break;
					case Elementdeclerations.discretePlace:
						// System.out.println("plce");
						elSub = specAnnotation.getChild("token", null);
						attr = String.valueOf(elSub.getAttributeValue("token"));
						((biologicalObjects.nodes.petriNet.Place) bna).setToken(Double.parseDouble(attr));
						elSub = specAnnotation.getChild("tokenMin", null);
						attr = String.valueOf(elSub.getAttributeValue("tokenMin"));
						((biologicalObjects.nodes.petriNet.Place) bna).setTokenMin(Double.parseDouble(attr));
						elSub = specAnnotation.getChild("tokenMax", null);
						attr = String.valueOf(elSub.getAttributeValue("tokenMax"));
						((biologicalObjects.nodes.petriNet.Place) bna).setTokenMax(Double.parseDouble(attr));
						elSub = specAnnotation.getChild("tokenStart", null);
						attr = String.valueOf(elSub.getAttributeValue("tokenStart"));
						((biologicalObjects.nodes.petriNet.Place) bna).setTokenStart(Double.parseDouble(attr));
						((biologicalObjects.nodes.petriNet.Place) bna).setDiscrete(true);
						
						elSub = specAnnotation.getChild("ConflictStrategy", null);
						if (elSub != null) {
							attr = elSub.getAttributeValue("ConflictStrategy");
							((Place) bna).setConflictStrategy(Integer.parseInt(attr));
						}
						break;
					case Elementdeclerations.continuousPlace:
						elSub = specAnnotation.getChild("token", null);
						attr = String.valueOf(elSub.getAttributeValue("token"));
						// System.out.println(attr);
						((biologicalObjects.nodes.petriNet.Place) bna).setToken(Double.parseDouble(attr));
						elSub = specAnnotation.getChild("tokenMin", null);
						attr = String.valueOf(elSub.getAttributeValue("tokenMin"));
						((biologicalObjects.nodes.petriNet.Place) bna).setTokenMin(Double.parseDouble(attr));
						elSub = specAnnotation.getChild("tokenMax", null);
						attr = String.valueOf(elSub.getAttributeValue("tokenMax"));
						((biologicalObjects.nodes.petriNet.Place) bna).setTokenMax(Double.parseDouble(attr));
						elSub = specAnnotation.getChild("tokenStart", null);
						attr = String.valueOf(elSub.getAttributeValue("tokenStart"));
						((biologicalObjects.nodes.petriNet.Place) bna).setTokenStart(Double.parseDouble(attr));
						((biologicalObjects.nodes.petriNet.Place) bna).setDiscrete(false);
						
						elSub = specAnnotation.getChild("ConflictStrategy", null);
						if (elSub != null) {
							attr = elSub.getAttributeValue("ConflictStrategy");
							((Place) bna).setConflictStrategy(Integer.parseInt(attr));
						}
						
						break;
					case Elementdeclerations.discreteTransition:
						elSub = specAnnotation.getChild("delay", null);
						attr = String.valueOf(elSub.getAttributeValue("delay"));
						((biologicalObjects.nodes.petriNet.DiscreteTransition) bna).setDelay(Double.parseDouble(attr));
						break;
					case Elementdeclerations.continuousTransition:
						break;
					case Elementdeclerations.stochasticTransition:
						elSub = specAnnotation.getChild("distribution", null);
						attr = String.valueOf(elSub.getAttributeValue("distribution"));
						((biologicalObjects.nodes.petriNet.StochasticTransition) bna).setDistribution(attr);
						break;
					}

					// get additional information
					List<Element> specAnnotationChildren = specAnnotation.getChildren();
					for (int j = 0; j < specAnnotationChildren.size(); j++) {
						// go through all Nodes and look up what is set
						Element child = specAnnotationChildren.get(j);
						handleNodeInformation(child.getName(), child);
					}
					// get the coordinates of the bna
					elSub = specAnnotation.getChild("Coordinates", null);
					Element elSubSub = elSub.getChild("x_Coordinate", null);
					Double xCoord = new Double(elSubSub.getAttributeValue("x_Coordinate"));
					elSubSub = elSub.getChild("y_Coordinate", null);
					Double yCoord = new Double(elSubSub.getAttributeValue("y_Coordinate"));
					p = new Point2D.Double(xCoord, yCoord);

					elSub = specAnnotation.getChild("environmentNode", null);
					if (elSub != null) {
						if (String.valueOf(elSub.getAttributeValue("environmentNode")).equals("true")) {
							bna.markAsEnvironment(true);
						}
					}

					elSub = specAnnotation.getChild("Parameters", null);

					// elSubSub = elSub.getChild("x_Coordinate", null);

					ArrayList<Parameter> parameters = new ArrayList<Parameter>();
					Parameter param;
					String pname = "";
					Double value = 0.0;
					String unit = "";
					for (int j = 0; j < elSub.getChildren().size(); j++) {
						elSubSub = elSub.getChildren().get(j);
						// System.out.println(elSubSub.getChild("Name",
						// null).getAttributeValue("Name"));
						pname = elSubSub.getChild("Name", null).getAttributeValue("Name");
						value = Double.valueOf(elSubSub.getChild("Value", null).getAttributeValue("Value"));
						unit = elSubSub.getChild("Unit", null).getAttributeValue("Unit");
						param = new Parameter(pname, value, unit);
						parameters.add(param);
					}
					bna.setParameters(parameters);
				}
			}
			// test which annotations are set
			// only if bna was created above
			// set id and compartment of the bna

			String id = species.getAttributeValue("id");

			int intid = this.getID(id);
			try {
				if (intid > -1) {
					bna.setID(intid);
				} else {
					bna.setID();
				}
			} catch (IDAlreadyExistException ex) {
				bna.setID();
			}

			String compartment = species.getAttributeValue("compartment");
			if (compartment.contains("comp_")) {
				String[] comp = compartment.split("_");
				bna.setCompartment(comp[1]);
			} else {
				bna.setCompartment(compartment);
			}

			// add bna to the graph
			this.pathway.addVertex(bna, p);
			// add bna to hashtable
			nodes.put(bna.getID(), bna);
			string2id.put(id, bna.getID());
			// reset because bna is global defined
			bna = null;
		}
	}

	/**
	 * Coarses the nodes as described in the loaded sbml file.
	 *
	 * @param annotationNode
	 *            Annotation Area of the imported model.
	 * @author tloka
	 */
	private void buildUpHierarchy(Element annotationNode) {

		if (annotationNode == null)
			return;

		Element modelNode = annotationNode.getChild("model", null);
		if (modelNode == null)
			return;

		Element hierarchyList = modelNode.getChild("listOfHierarchies", null);
		if (hierarchyList == null)
			return;

		Map<Integer, Set<Integer>> hierarchyMap = new HashMap<Integer, Set<Integer>>();
		Map<Integer, String> coarseNodeLabels = new HashMap<Integer, String>();
		Map<Integer, Integer> hierarchyRootNodes = new HashMap<Integer, Integer>();
		Set<Integer> openedCoarseNodes = new HashSet<Integer>();

		for (Element coarseNode : hierarchyList.getChildren("coarseNode", null)) {
			if (coarseNode.getChildren("child", null) == null)
				continue;

			Set<Integer> childrenSet = new HashSet<Integer>();
			for (Element childElement : coarseNode.getChildren("child", null)) {
				Integer childNode = Integer.parseInt(childElement.getAttributeValue("id").split("_")[1]);
				childrenSet.add(childNode);
			}

			Integer id = Integer.parseInt(coarseNode.getAttributeValue("id").split("_")[1]);
			String rootNode = coarseNode.getAttribute("root", null) == null ? "null" : coarseNode.getAttributeValue("root");
			if (!rootNode.equals("null")) {
				hierarchyRootNodes.put(id, Integer.parseInt(coarseNode.getAttributeValue("root").split("_")[1]));
			}
			hierarchyMap.put(id, childrenSet);
			coarseNodeLabels.put(id, coarseNode.getAttributeValue("label"));
			if (coarseNode.getAttributeValue("opened") != null && coarseNode.getAttributeValue("opened").equals("true")) {
				openedCoarseNodes.add(id);
			}
		}

		int coarsedNodes = 0;

		while (coarsedNodes < hierarchyMap.size()) {
			for (Integer parent : hierarchyMap.keySet()) {
				boolean toBeCoarsed = true;
				Set<BiologicalNodeAbstract> coarseNodes = new HashSet<BiologicalNodeAbstract>();
				for (Integer child : hierarchyMap.get(parent)) {
					if (!nodes.containsKey(child) || nodes.containsKey(parent)) {
						toBeCoarsed = false;
						break;
					}
					coarseNodes.add(nodes.get(child));
				}
				if (toBeCoarsed) {
					BiologicalNodeAbstract coarseNode;
					if (hierarchyRootNodes.containsKey(parent)) {
						coarseNode = BiologicalNodeAbstract.coarse(coarseNodes, parent, coarseNodeLabels.get(parent),
								nodes.get(hierarchyRootNodes.get(parent)));

					} else {
						coarseNode = BiologicalNodeAbstract.coarse(coarseNodes, parent, coarseNodeLabels.get(parent));
					}

					nodes.put(parent, coarseNode);
					coarsedNodes += 1;
				}
			}
			if (!coarsePathway) {
				while (!openedCoarseNodes.isEmpty()) {
					Set<Integer> ocn = new HashSet<Integer>();
					ocn.addAll(openedCoarseNodes);
					for (Integer id : ocn) {
						if (pathway.containsVertex(nodes.get(id))) {
							pathway.openSubPathway(nodes.get(id));
							openedCoarseNodes.remove(id);
						}
					}
				}
			}
		}
		if (coarsePathway) {
			Set<BiologicalNodeAbstract> roughestAbstractionNodes = new HashSet<BiologicalNodeAbstract>();
			roughestAbstractionNodes.addAll(nodes.values());
			roughestAbstractionNodes.removeIf(p -> p.getParentNode() != null && p.getParentNode() != p);
			roughestAbstractionNodes.removeIf(p -> p.isMarkedAsEnvironment());
			BiologicalNodeAbstract.coarse(roughestAbstractionNodes);
			for (BiologicalNodeAbstract node : nodes.values()) {
				node.markAsEnvironment(false);
			}
		}
	}

	private void handleEdgeInformation(String attrtmp, Element child) {
		String value = child.getAttributeValue(attrtmp);
		switch (attrtmp) {
		// standard cases
		case "IsWeighted":
			//bea.setWeighted(Boolean.parseBoolean(value));
			break;
		case "Weight":
			// old cases when there was "weight" and "function" for edges
			bea.setFunction(value);
			break;
		case "Function":
			bea.setFunction(value);
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
			//bea.hasFeatureEdge(Boolean.parseBoolean(value));
			break;
		case "HasKEGGEdge":
			//bea.hasKEGGEdge(Boolean.parseBoolean(value));
			break;
		case "HasReactionPairEdge":
			//bea.hasReactionPairEdge(Boolean.parseBoolean(value));
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
		case "absoluteInhibition":
			if(bea instanceof Inhibition){
				((Inhibition)bea).setAbsoluteInhibition(Boolean.parseBoolean(value));
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
		// System.out.println("child: "+child);
		String value = child.getAttributeValue(attrtmp);
		if (reverseEngineering) {
			attrtmp = attrtmp.replace("token", "concentration");
		}
		switch (attrtmp) {
		// standard cases
		case "Nodesize":
			if (reverseEngineering) {
				break;
			}
			Double d = Double.parseDouble(value);
			bna.setNodesize(d);
			break;
		case "Comments":
			bna.setComments(value);
			break;
		case "Description":
			bna.setDescription(value);
			break;
		case "Networklabel":
			// no set-method available
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
			break;
		case "Color":
			if (reverseEngineering) {
				break;
			}
			Element elSub = child.getChild("RGB", null);
			if (elSub != null) {
				int rgb = Integer.parseInt(elSub.getAttributeValue("RGB"));
				// System.out.println(rgb);
				Color col = new Color(rgb);
				// System.out.println(col.getRGB());
				bna.setColor(col);
				// System.out.println("color set");
			} else {
				// System.out.println("skipped");
			}
			// System.out.println(bna.isReference() ? "reference" :
			// "no reference");
			// System.out.println(bna.isHidden() ? "hidden" : "not hidden");
			// System.out.println(bna.getColor().getRGB());
			break;
		case "NodeReference":
			elSub = child.getChild("hasRef", null);
			if (elSub.getAttributeValue("hasRef").equals("true")) {
				elSub = child.getChild("RefID", null);
				this.bna2Ref.put(bna, Integer.parseInt(elSub.getAttributeValue("RefID")));
			}
			break;
		case "constCheck":
			if (value.equals("true")) {
				bna.setConstant(true);
			} else {
				bna.setConstant(false);
			}
			break;
		case "concentration":
			bna.setConcentration(Double.parseDouble(value));
			break;
		case "concentrationStart":
			bna.setConcentrationStart(Double.parseDouble(value));
			break;
		case "concentrationMin":
			bna.setConcentrationMin(Double.parseDouble(value));
			break;
		case "concentrationMax":
			bna.setConcentrationMax(Double.parseDouble(value));
			break;
		case "isDiscrete":
			bna.setDiscrete(Boolean.parseBoolean(value));
			break;

		// special cases

		case "firingCondition":
			if (bna instanceof Transition) {
				((biologicalObjects.nodes.petriNet.Transition) bna).setFiringCondition(value);
			}
			break;
		case "maximumSpeed":
			if (bna instanceof DynamicNode) {
				String speed = value;
				if (value == null || value.equals("")) {
					speed = "1";
				}
				((DynamicNode) bna).setMaximumSpeed(speed);
			}
			break;
		case "knockedOut":
			if (bna instanceof DynamicNode) {
				((biologicalObjects.nodes.DynamicNode) bna).setKnockedOut(false);
				if (value != null && value.equals("true")) {
					((biologicalObjects.nodes.DynamicNode) bna).setKnockedOut(true);
				}
			}
			break;
		case "NtSequence":
			if (bna instanceof DNA) {
				((biologicalObjects.nodes.DNA) bna).setNtSequence(value);
			} else if (bna instanceof RNA) {
				// System.out.println(bna.getLabel());
				((biologicalObjects.nodes.RNA) bna).setNtSequence(value);
			}
			break;
		case "Proteins":
			((biologicalObjects.nodes.Gene) bna).addProtein(stringToArray(value));
			break;
		case "Enzymes":
			((biologicalObjects.nodes.Gene) bna).addEnzyme(stringToArray(value));
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

	/**
	 * adds ranges to the graph
	 *
	 * @param rangeElement
	 */
	private void addRange(Element rangeElement) {
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("title", "");
		String[] keys = { "textColor", "outlineType", "fillColor", "alpha", "maxY", "outlineColor", "maxX", "isEllipse", "minX", "minY", "titlePos",
				"title" };
		for (int i = 0; i < keys.length; i++) {
			Element tmp = rangeElement.getChild(keys[i], null);
			if (tmp != null) {
				String value = tmp.getAttributeValue(keys[i]);
				attrs.put(keys[i], value);
			}
		}
		RangeSelector.getInstance().addRangesInMyGraph(pathway.getGraph(), attrs);
	}

	private void handleReferences() {
		Iterator<BiologicalNodeAbstract> it = bna2Ref.keySet().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();
			bna.setRef(this.nodes.get(bna2Ref.get(bna)));
		}
	}

	private Integer getID(String id) {
		if (NumberUtils.isNumber(id)) {
			return Integer.parseInt(id);
		}
		if (id.contains("spec_")) {
			String[] idSplit = id.split("_");
			if (NumberUtils.isNumber(idSplit[1])) {
				return Integer.parseInt(idSplit[1]);
			}
		}
		return -1;
	}
}
