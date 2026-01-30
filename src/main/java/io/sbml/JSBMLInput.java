package io.sbml;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import biologicalElements.Elementdeclerations;
import biologicalElements.GraphElementAbstract;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.BiologicalEdgeAbstractFactory;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.ReactionPairEdge;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.KEGGNode;
import biologicalObjects.nodes.NodeWithLogFC;
import biologicalObjects.nodes.NodeWithNTSequence;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import graph.CreatePathway;
import graph.compartment.Compartment;
import graph.groups.Group;
import graph.gui.Parameter;
import gui.MainWindow;
import simulation.ConflictHandling;
import util.StochasticDistribution;

/**
 * To read a SBML file and put the results on the graph. A SBML which has been
 * passed over to an instance of this class will be parsed to the VANESA graph.
 *
 * @author Annika, Sandra, cbrinkrolf
 */
public class JSBMLInput {
	private Pathway pathway;
	private final Hashtable<Integer, BiologicalNodeAbstract> nodes = new Hashtable<>();
	private final HashMap<String, Integer> string2id = new HashMap<>();
	private boolean coarsePathway = false;
	private final Hashtable<BiologicalNodeAbstract, Integer> bna2Ref = new Hashtable<>();
	private final boolean reverseEngineering = false;
	private final ArrayList<ArrayList<String>> inputGroups = new ArrayList<>();
	private final StringBuilder errorString = new StringBuilder();
	// returns all parsing errors / missing values that could occur loading old
	// files that lack parameters introduced later during development
	private boolean strictParsing = false;

	public JSBMLInput(Pathway pw) {
		pathway = pw;
	}

	private Node getChildNode(Node node, String nodeName, boolean logErrors) {
		if (!node.hasChildNodes()) {
			if (logErrors && strictParsing) {
				errorString.append("SBML parsing error: Child node with name " + nodeName + " does not exist for Tag "
						+ node.getNodeName() + ". Tag does not have any child nodes!\n");
			}
			return null;
		}
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			if (node.getChildNodes().item(i).getNodeName().strip().equals(nodeName.strip())) {
				return node.getChildNodes().item(i);
			}
		}
		if (logErrors && strictParsing) {
			errorString.append("SBML parsing error: Child node with name " + nodeName + " does not exist for Tag "
					+ node.getNodeName() + "!\n");
		}
		return null;
	}

	private Node getChildNode(Node node, String nodeName) {
		return this.getChildNode(node, nodeName, true);
	}

	private List<Node> getChildAllNodes(Node node, String nodeName) {
		List<Node> childNodes = new ArrayList<>();
		if (!node.hasChildNodes()) {
			return childNodes;
		}
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			if (node.getChildNodes().item(i).getNodeName().strip().equals(nodeName.strip())) {
				childNodes.add(node.getChildNodes().item(i));
			}
		}
		return childNodes;
	}

	private List<Node> getChildAllNodes(Node node) {
		List<Node> childNodes = new ArrayList<>();
		if (!node.hasChildNodes()) {
			return childNodes;
		}
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			childNodes.add(node.getChildNodes().item(i));
		}
		return childNodes;
	}

	private String getAttributeValue(Node node, String attributeName, boolean logErrors) {
		if (!node.hasAttributes()) {
			if (logErrors && strictParsing) {
				errorString
						.append("SBML parsing error: Attribute " + attributeName + " does not exist for Tag with name "
								+ node.getNodeName() + ". Tag does not contain any attributes!\n");
			}
			return null;
		}
		for (int i = 0; i < node.getAttributes().getLength(); i++) {
			if (node.getAttributes().item(i).getNodeName().trim().equals(attributeName.trim())) {
				return node.getAttributes().item(i).getNodeValue().trim();
			}
		}
		if (logErrors && strictParsing) {
			errorString.append("SBML parsing error: Attribute " + attributeName + " does not exist for Tag with name "
					+ node.getNodeName() + "!\n");
		}
		return null;
	}

	private String getCascadingNodeAttribute(Node node, String attribute, boolean logErrors) {
		Node childNode = this.getChildNode(node, attribute, logErrors);
		if (childNode != null) {
			return getAttributeValue(childNode, attribute, logErrors);
		}
		return null;
	}

	private String getCascadingNodeAttribute(Node node, String attribute) {
		return getCascadingNodeAttribute(node, attribute, true);
	}

	private String getAttributeValue(Node node, String attributeName) {
		return getAttributeValue(node, attributeName, true);
	}

	public String loadSBMLFile(InputStream is, File file) {

		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		if (document == null) {
			return "An error occurred, document is null!";
		}
		document.getDocumentElement().normalize();

		Node sbmlNode = this.getChildNode(document, "sbml");
		if (sbmlNode == null) {
			return "Error while parsing SMBL document. Tag SBML missing!";
		}

		if (pathway == null) {
			pathway = new CreatePathway(file.getName()).getPathway();
		} else {
			coarsePathway = true;
		}
		if (pathway.getFile() == null) {
			pathway.setFile(file);
		}

		Node modelNode = this.getChildNode(sbmlNode, "model");
		if (modelNode == null) {
			return "Error while parsing SMBL document. Tag Model missing!";
		}
		Node annotationNode = this.getChildNode(modelNode, "annotation");
		createAnnotation(annotationNode);

		Node compartmentNode = this.getChildNode(modelNode, "listOfCompartments");
		createCompartment(compartmentNode);

		Node speciesNode = this.getChildNode(modelNode, "listOfSpecies");
		createSpecies(speciesNode);

		handleReferences();
		Node reactionNode = this.getChildNode(modelNode, "listOfReactions", false);
		createReaction(reactionNode);
		buildUpHierarchy(annotationNode);
		createGroup();
		// refresh view
		try {
			is.close();
			this.pathway.getGraph().restartVisualizationModel();
			MainWindow.getInstance().updateProjectProperties();
			// MainWindow.getInstance().updateOptionPanel();

		} catch (Exception ex) {
			ex.printStackTrace();
			return "An error occurred during the loading.";
		}
		return errorString.toString();
	}

	/**
	 * Groups and their members are saved in list, cause groups cant be created at
	 * this point, because nodes aren't created yet
	 */
	private void getInputGroups(Node groupNode) {
		List<Node> groupChildren = this.getChildAllNodes(groupNode, "Group");
		for (Node group : groupChildren) {
			List<Node> groupMembers = this.getChildAllNodes(group, "Node");
			ArrayList<String> tmp = new ArrayList<>();
			for (Node node : groupMembers) {
				// add nodes with ID/label to nodes list
				String label = this.getAttributeValue(node, "Node");
				if (label != null) {
					tmp.add(label);
				}
			}
			inputGroups.add(tmp);
		}
	}

	/**
	 * Groups will be created after all nodes are there
	 */
	private void createGroup() {
		for (ArrayList<String> inputGroup : inputGroups) {
			ArrayList<BiologicalNodeAbstract> nodesList = new ArrayList<>();
			for (String s : inputGroup) {
				nodesList.add(nodes.get(Integer.parseInt(s)));
			}
			Group tmp = new Group(nodesList);
			for (BiologicalNodeAbstract biologicalNodeAbstract : nodesList) {
				biologicalNodeAbstract.addGroup(tmp);
			}
			pathway.getGroups().add(tmp);
		}
		inputGroups.clear();
	}

	/**
	 * creates the annotation of the model
	 */
	private void createAnnotation(Node annotationNode) {
		if (annotationNode == null) {
			errorString.append("Error while parsing SBML document. Tag Annotation is missing!\n");
			return;
		}

		Node modelNode = this.getChildNode(annotationNode, "model");
		// get the information if the imported net is a Petri net
		boolean isPetri = false;
		if (modelNode == null) {
			errorString.append("Error while parsing SMBL document. Tag Model is missing!\n");
			pathway.setIsPetriNet(isPetri);
			return;
		}

		Node isPetriNetNode = this.getChildNode(modelNode, "isPetriNet");

		if (isPetriNetNode != null) {
			String value = this.getAttributeValue(isPetriNetNode, "isPetriNet");
			if (value != null) {
				isPetri = Boolean.parseBoolean(value);
			}
		}
		if (reverseEngineering) {
			isPetri = false;
		}
		// get the annotations / ranges if present
		Node annotationsNode = this.getChildNode(modelNode, "listOfRanges", false);
		if (annotationsNode != null) {
			this.addAnnotations(annotationsNode);
		}
		Node groupNode = this.getChildNode(modelNode, "listOfGroups", false);
		if (groupNode != null) {
			getInputGroups(groupNode);
		}

		pathway.setIsPetriNet(isPetri);
	}

	/**
	 * creates the compartments not needed yet
	 */
	private void createCompartment(Node compartmentNode) {
		if (compartmentNode == null && strictParsing) {
			errorString.append("Error while parsing SBML document. Tag listOfCompartments is missing!\n");
			return;
		}

		for (Node comp : this.getChildAllNodes(compartmentNode, "compartment")) {
			String name = this.getAttributeValue(comp, "id");
			if (name == null) {
				continue;
			}
			if (name.equals("comp_") || name.length() == 0) {
				continue;
			}
			Color color = Color.GRAY;
			Node annotation = this.getChildNode(comp, "annotation");
			if (annotation != null) {
				Node compAnnotation = this.getChildNode(annotation, "spec");
				if (compAnnotation != null) {
					Node colorNode = this.getChildNode(compAnnotation, "Color");
					if (colorNode != null) {
						Node rgbNode = this.getChildNode(colorNode, "RGB");
						if (rgbNode != null) {
							String value = this.getAttributeValue(rgbNode, "RGB");
							if (value != null) {
								try {
									color = new Color(Integer.parseInt(value));
								} catch (NumberFormatException e) {
									e.printStackTrace();
									errorString.append(e.getMessage());
								}
							}
						}
					}
				}
			}
			if (name.startsWith("comp_")) {
				name = name.substring(5);
			}
			Compartment c = new Compartment(name, color);
			pathway.getCompartmentManager().add(c);
		}
	}

	/**
	 * creates the reactions
	 */
	private void createReaction(Node reactionNode) {
		if (reactionNode == null) {
			return;
		}
		// for each reaction
		for (Node reaction : this.getChildAllNodes(reactionNode, "reaction")) {
			// test which bea has to be created
			// get name and label to create the bea
			String name = this.getAttributeValue(reaction, "name");
			if (name == null) {
				name = "";
			}
			// get from an to nodes for the reaction
			Node reactantsNode = this.getChildNode(reaction, "listOfReactants");
			Node productsNode = this.getChildNode(reaction, "listOfProducts");
			if (reactantsNode != null && productsNode != null) {
				Node reactant = this.getChildNode(reactantsNode, "speciesReference");
				if (reactant == null) {
					continue;
				}
				String idReact = this.getAttributeValue(reactant, "species");
				BiologicalNodeAbstract from = nodes.get(string2id.get(idReact));
				Node product = this.getChildNode(productsNode, "speciesReference");
				if (product == null) {
					continue;
				}
				String idProd = this.getAttributeValue(product, "species");
				BiologicalNodeAbstract to = nodes.get(string2id.get(idProd));
				String label = name;
				BiologicalEdgeAbstract bea = new ReactionEdge(label, name, from, to);
				bea.setDirected(true);
				// bea.setFrom(from);
				// bea.setTo(to);
				// bea.setLabel(label);
				// bea.setName(name);
				Node annotation = this.getChildNode(reaction, "annotation");
				if (annotation != null) {
					Node reacAnnotation = this.getChildNode(annotation, "reac");
					if (reacAnnotation != null) {
						String biologicalElement = this.getCascadingNodeAttribute(reacAnnotation, "BiologicalElement");
						if (biologicalElement == null) {
							biologicalElement = "";
						}
						String labelAttr = this.getCascadingNodeAttribute(reacAnnotation, "label");
						if (labelAttr != null) {
							label = labelAttr;
						}
						bea = BiologicalEdgeAbstractFactory.create(biologicalElement, from, to, label, name);
						bea.setDirected(true);

						Node parametersNode = this.getChildNode(reacAnnotation, "Parameters", false);
						handleReationParameters(parametersNode, bea);

						if (bea instanceof PNArc) {
							String probability = this.getCascadingNodeAttribute(reacAnnotation, "Probability");
							if (probability != null) {
								try {
									((PNArc) bea).setProbability(Double.parseDouble(probability));
								} catch (NumberFormatException e) {
									e.printStackTrace();
									errorString.append(e.getMessage());
								}
							}

							String priority = this.getCascadingNodeAttribute(reacAnnotation, "Priority");
							if (priority != null) {
								try {
									((PNArc) bea).setPriority(Integer.parseInt(priority));
								} catch (NumberFormatException e) {
									e.printStackTrace();
									errorString.append(e.getMessage());
								}
							}

						}

						for (Node child : this.getChildAllNodes(reacAnnotation)) {
							// go through all Nodes and look up what is set
							handleEdgeInformation(bea, child.getNodeName(), child);
						}
					}
				}
				// set ID of the reaction
				this.handleIdInformation(reaction, bea);
				this.pathway.addEdge(bea);
			}
		}
	}

	private String handleIdInformation(Node node, GraphElementAbstract gea) {
		String id = this.getAttributeValue(node, "id");
		if (id == null) {
			id = "-1";
		}
		int intid = this.getID(id);
		try {
			if (intid > -1) {
				gea.setID(intid, pathway);
			} else {
				gea.setID(pathway);
			}
		} catch (IDAlreadyExistException ex) {
			gea.setID(pathway);
		}
		return id;
	}

	/**
	 * creates the species
	 */
	private void createSpecies(Node speciesNode) {
		if (speciesNode == null && strictParsing) {
			errorString.append("Error while parsing SBML document. Tag listOfSpecies is missing!\n");
			return;
		}

		// for each species
		for (Node species : this.getChildAllNodes(speciesNode, "species")) {
			// test which bna has to be created
			// get name and label to create the bna
			String name = this.getAttributeValue(species, "name");
			String biologicalElement = "";
			if (name == null) {
				name = "";
			}
			String label = name;
			BiologicalNodeAbstract bna = new Other(label, name, pathway);
			Point2D.Double p = new Point2D.Double(0.0, 0.0);
			Node annotation = this.getChildNode(species, "annotation");
			if (annotation != null) {
				Node specAnnotation = this.getChildNode(annotation, "spec");
				if (specAnnotation != null) {
					Node biologicalElementNode = this.getChildNode(specAnnotation, "BiologicalElement");
					if (biologicalElementNode != null) {
						String value = this.getAttributeValue(biologicalElementNode, "BiologicalElement");
						if (value != null) {
							biologicalElement = value;
						}
					}
					Node labelNode = this.getChildNode(specAnnotation, "label", false);
					if (labelNode == null) {

						labelNode = this.getChildNode(specAnnotation, "Label");

						if (labelNode != null) {
							String value = this.getAttributeValue(labelNode, "Label");
							if (value != null) {
								label = value;
							}
						}

					} else {
						String value = this.getAttributeValue(labelNode, "label");
						if (value != null) {
							label = value;
						}
					}
				}
				bna = BiologicalNodeAbstractFactory.create(pathway, biologicalElement);
				if (reverseEngineering) {
					if (bna instanceof Place) {
						bna = BiologicalNodeAbstractFactory.create(pathway, Elementdeclerations.metabolite);
					} else if (bna instanceof Transition) {
						bna = BiologicalNodeAbstractFactory.create(pathway, Elementdeclerations.enzyme);
					}
				}
				bna.setLabel(label);
				bna.setName(name);

				// TODO ntSequence is missing in JSBML Export
				if (bna instanceof NodeWithNTSequence) {
					String ntSequence = getCascadingNodeAttribute(specAnnotation, "NtSequence");
					if (ntSequence != null) {
						((NodeWithNTSequence) bna).setNtSequence(ntSequence);
					}
				}

				// TODO logFC is missing in JSBML Export
				if (bna instanceof NodeWithLogFC) {
					String logFC = this.getCascadingNodeAttribute(specAnnotation, "LogFC");
					if (logFC != null) {
						try {
							((NodeWithLogFC) bna).setLogFC(Double.parseDouble(logFC));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							errorString
									.append("Error while parsing SBML document. Attribute LogFC of species with name "
											+ name + " cannot be parsed as a number!\n");
							errorString.append(e.getMessage());
						}
					}
				}

				if (bna instanceof PathwayMap) {
					// TODO missing implementation setting the pathway link to the object
					String pathwayLink = this.getCascadingNodeAttribute(specAnnotation, "PathwayLink");
				}
				if (bna instanceof Place) {
					Place place = (Place) bna;

					String tokenMin = this.getCascadingNodeAttribute(specAnnotation, "tokenMin");
					if (tokenMin != null) {
						try {
							place.setTokenMin(Double.parseDouble(tokenMin));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							errorString.append(e.getMessage());
						}
					}

					String tokenMax = this.getCascadingNodeAttribute(specAnnotation, "tokenMax");
					if (tokenMax != null) {
						try {
							place.setTokenMax(Double.parseDouble(tokenMax));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							errorString.append(e.getMessage());
						}
					}

					String tokenStart = this.getCascadingNodeAttribute(specAnnotation, "tokenStart");
					if (tokenStart != null) {
						try {
							place.setTokenStart(Double.parseDouble(tokenStart));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							errorString.append(e.getMessage());
						}
					}

					String conflictStrategy = this.getCascadingNodeAttribute(specAnnotation, "ConflictStrategy");
					if (conflictStrategy != null) {
						try {
							final int conflictHandlingId = Integer.parseInt(conflictStrategy);
							((Place) bna).setConflictStrategy(ConflictHandling.fromId(conflictHandlingId));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							errorString.append(e.getMessage());
						}
					}
				}

				if (bna instanceof DiscreteTransition) {
					String delay = this.getCascadingNodeAttribute(specAnnotation, "delay");
					if (delay != null) {
						((DiscreteTransition) bna).setDelay(delay);
					}
				}

				if (bna instanceof ContinuousTransition) {
					// for legacy
					String maximalSpeed = this.getCascadingNodeAttribute(specAnnotation, "maximumSpeed", false);
					if (maximalSpeed == null) {
						maximalSpeed = this.getCascadingNodeAttribute(specAnnotation, "maximalSpeed");
					}
					if (maximalSpeed != null) {
						((ContinuousTransition) bna).setMaximalSpeed(maximalSpeed);
					}
				}

				if (bna instanceof StochasticTransition) {
					Node distributionProperties = this.getChildNode(specAnnotation, "distributionProperties");
					if (distributionProperties != null) {
						StochasticTransition st = (StochasticTransition) bna;

						String distribution = this.getCascadingNodeAttribute(distributionProperties, "distribution");
						if (distribution != null) {
							st.setDistribution(StochasticDistribution.fromId(distribution));
						}

						String h = this.getCascadingNodeAttribute(distributionProperties, "h");
						if (h != null) {
							try {
								st.setH(Double.parseDouble(h));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								errorString.append(e.getMessage());
							}
						}

						String a = this.getCascadingNodeAttribute(distributionProperties, "a");
						if (a != null) {
							try {
								st.setA(Double.parseDouble(a));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								errorString.append(e.getMessage());
							}
						}

						String b = this.getCascadingNodeAttribute(distributionProperties, "b");
						if (b != null) {
							try {
								st.setB(Double.parseDouble(b));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								errorString.append(e.getMessage());
							}
						}

						String c = this.getCascadingNodeAttribute(distributionProperties, "c");
						if (c != null) {
							try {
								st.setC(Double.parseDouble(c));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								errorString.append(e.getMessage());
							}
						}

						String mu = this.getCascadingNodeAttribute(distributionProperties, "mu");
						if (mu != null) {
							try {
								st.setMu(Double.parseDouble(mu));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								errorString.append(e.getMessage());
							}
						}

						String sigma = this.getCascadingNodeAttribute(distributionProperties, "sigma");
						if (sigma != null) {
							try {
								st.setSigma(Double.parseDouble(sigma));
							} catch (NumberFormatException e) {
								e.printStackTrace();
								errorString.append(e.getMessage());
							}
						}

						String discreteEvents = this.getCascadingNodeAttribute(distributionProperties,
								"discreteEvents");
						if (discreteEvents != null) {
							ArrayList<Integer> events = new ArrayList<>();
							String[] eventTokens = discreteEvents.split(",");
							for (String eventToken : eventTokens) {
								try {
									events.add(Integer.parseInt(eventToken.strip()));
								} catch (NumberFormatException e) {
									e.printStackTrace();
									errorString.append(e.getMessage());
								}
							}
							st.setEvents(events);
						}

						String discreteEventProbabilities = this.getCascadingNodeAttribute(distributionProperties,
								"discreteEventProbabilities");
						if (discreteEventProbabilities != null) {
							ArrayList<Double> probs = new ArrayList<>();
							String[] probTokens = discreteEventProbabilities.split(",");
							for (String probToken : probTokens) {
								try {
									probs.add(Double.parseDouble(probToken.strip()));
								} catch (NumberFormatException e) {
									e.printStackTrace();
									errorString.append(e.getMessage());
								}
							}
							st.setProbabilities(probs);
						}
					}
				}

				// get additional information
				for (Node node : this.getChildAllNodes(specAnnotation, name)) {
					// go through all Nodes and look up what is set
					handleNodeInformation(bna, node.getNodeName(), node);
				}
				// get the coordinates of the bna
				Node coordinates = this.getChildNode(specAnnotation, "Coordinates");
				if (coordinates != null) {
					double xCoord = 0.0;
					double yCoord = 0.0;
					String xCoordString = this.getCascadingNodeAttribute(coordinates, "x_Coordinate");
					if (xCoordString != null) {
						try {
							xCoord = Double.parseDouble(xCoordString);
						} catch (NumberFormatException e) {
							e.printStackTrace();
							errorString.append(e.getMessage());
						}
					}
					String yCoordString = this.getCascadingNodeAttribute(coordinates, "y_Coordinate");
					if (yCoordString != null) {
						try {
							yCoord = Double.parseDouble(yCoordString);
						} catch (NumberFormatException e) {
							e.printStackTrace();
							errorString.append(e.getMessage());
						}
					}
					p = new Point2D.Double(xCoord, yCoord);
				}

				String isEnvironment = this.getCascadingNodeAttribute(specAnnotation, "environmentNode", false);
				if (isEnvironment != null && isEnvironment.equals("true")) {
					bna.setMarkedAsEnvironment(true);
				}

				Node parametersNode = this.getChildNode(specAnnotation, "Parameters", false);
				this.handleReationParameters(parametersNode, bna);
			}

			// test which annotations are set only if bna was created above set id and
			// compartment of the bna

			String id = this.handleIdInformation(species, bna);

			String compartment = this.getAttributeValue(species, "compartment");
			if (compartment == null) {
				compartment = "";
			}
			if (compartment.startsWith("comp_")) {
				pathway.getCompartmentManager().setCompartment(bna,
						pathway.getCompartmentManager().getCompartment(compartment.substring(5)));
			} else {
				pathway.getCompartmentManager().setCompartment(bna,
						pathway.getCompartmentManager().getCompartment(compartment));
			}
			// add bna to the graph
			pathway.addVertex(bna, p);
			// add bna to hashtable
			nodes.put(bna.getID(), bna);
			string2id.put(id, bna.getID());
		}

	}

	/**
	 * Coarses the nodes as described in the loaded sbml file.
	 *
	 * @param annotationNode Annotation Area of the imported model.
	 * @author tloka
	 */
	private void buildUpHierarchy(Node annotationNode) {
		if (annotationNode == null) {
			return;
		}
		Node modelNode = this.getChildNode(annotationNode, "model");
		if (modelNode == null) {
			return;
		}
		Node hierarchyList = this.getChildNode(modelNode, "listOfHierarchies");
		if (hierarchyList == null) {
			return;
		}
		Map<Integer, Set<Integer>> hierarchyMap = new HashMap<>();
		Map<Integer, String> coarseNodeLabels = new HashMap<>();
		Map<Integer, Integer> hierarchyRootNodes = new HashMap<>();
		Set<Integer> openedCoarseNodes = new HashSet<>();
		for (Node coarseNode : this.getChildAllNodes(hierarchyList, "coarseNode")) {
			if (this.getChildAllNodes(coarseNode, "child").size() == 0) {
				continue;
			}
			Set<Integer> childrenSet = new HashSet<>();
			for (Node childElement : this.getChildAllNodes(coarseNode, "child")) {
				String id = this.getAttributeValue(childElement, "id");
				if (id != null) {
					try {
						Integer childNode = Integer.parseInt(id.split("_")[1]);
						childrenSet.add(childNode);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						errorString.append(e.getMessage());
					}
				}
			}
			String idString = this.getAttributeValue(coarseNode, "id");

			if (idString != null) {
				try {
					Integer id = Integer.parseInt(idString.split("_")[1]);
					String rootNode = this.getAttributeValue(coarseNode, "root") == null ? "null"
							: this.getAttributeValue(coarseNode, "root");
					if (!rootNode.equals("null")) {
						String rootID = this.getAttributeValue(coarseNode, "root");
						if (rootID != null) {
							hierarchyRootNodes.put(id, Integer.parseInt(rootID.split("_")[1]));
						}
					}
					hierarchyMap.put(id, childrenSet);
					String label = this.getAttributeValue(coarseNode, "label");
					coarseNodeLabels.put(id, label);
					String opened = this.getAttributeValue(coarseNode, "opened");
					if (opened != null && opened.equals("true")) {
						openedCoarseNodes.add(id);
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorString.append(e.getMessage());
				}
			}
		}
		int coarsedNodes = 0;
		while (coarsedNodes < hierarchyMap.size()) {
			for (Integer parent : hierarchyMap.keySet()) {
				boolean toBeCoarsed = true;
				Set<BiologicalNodeAbstract> coarseNodes = new HashSet<>();
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
					Set<Integer> ocn = new HashSet<>(openedCoarseNodes);
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
			Set<BiologicalNodeAbstract> roughestAbstractionNodes = new HashSet<>(nodes.values());
			roughestAbstractionNodes.removeIf(p -> p.getParentNode() != null && p.getParentNode() != p);
			roughestAbstractionNodes.removeIf(p -> p.isMarkedAsEnvironment());
			BiologicalNodeAbstract.coarse(roughestAbstractionNodes);
			for (BiologicalNodeAbstract node : nodes.values()) {
				node.setMarkedAsEnvironment(false);
			}
		}
	}

	private void handleEdgeInformation(BiologicalEdgeAbstract bea, String attributeName, Node node) {
		String value = this.getAttributeValue(node, attributeName, false);
		if (value == null) {
			return;
		}
		switch (attributeName) {
		// standard cases
		case "IsWeighted":
			// bea.setWeighted(Boolean.parseBoolean(value));
			break;
		case "Weight":
			// old cases when there was "weight" and "function" for edges
		case "Function":
			bea.setFunction(value);
			break;
		case "Color":
			String colorString = this.getCascadingNodeAttribute(node, "RGB");
			if (colorString != null) {
				try {
					bea.setColor(new Color(Integer.parseInt(colorString)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					errorString.append(e.getMessage());
				}
			}
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
			// bea.hasFeatureEdge(Boolean.parseBoolean(value));
			break;
		case "HasKEGGEdge":
			// bea.hasKEGGEdge(Boolean.parseBoolean(value));
			break;
		case "HasReactionPairEdge":
			// bea.hasReactionPairEdge(Boolean.parseBoolean(value));
			break;
		case "ReactionPairEdge":
			if (bea instanceof ReactionPair) {
				ReactionPair reactionPair = (ReactionPair) bea;
				reactionPair.setReactionPairEdge(new ReactionPairEdge());
				for (Node subChild : this.getChildAllNodes(node)) {
					switch (subChild.getNodeName()) {
					case "ReactionPairEdgeID":
						reactionPair.getReactionPairEdge().setReactionPairID(value);
						break;
					case "ReactionPairName":
						reactionPair.getReactionPairEdge().setName(value);
						break;
					case "ReactionPairType":
						reactionPair.getReactionPairEdge().setType(value);
						break;
					}
				}
			}
			break;
		case "absoluteInhibition":
			if (bea instanceof Inhibition) {
				((Inhibition) bea).setAbsoluteInhibition(Boolean.parseBoolean(value));
			}
			break;
		}
	}

	/**
	 * Test which Information is set and handle it
	 */
	private void handleNodeInformation(BiologicalNodeAbstract bna, String attributeName, Node node) {
		String value = this.getAttributeValue(node, attributeName, false);
		if (value == null) {
			return;
		}
		if (reverseEngineering) {
			value = value.replace("token", "concentration");
		}
		switch (attributeName) {
		// standard cases
		case "Nodesize":
			if (reverseEngineering) {
				break;
			}
			try {
				bna.setNodeSize(Double.parseDouble(value));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				errorString.append(e.getMessage());
			}
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
			bna.setHasKEGGNode(Boolean.parseBoolean(value));
			break;
		case "KEGGNode":
			bna.setKEGGnode(new KEGGNode());
			addKEGGNode(bna, node);
			break;
		case "Color":
			if (reverseEngineering) {
				break;
			}
			String rgb = this.getCascadingNodeAttribute(node, "RGB", false);
			if (rgb != null) {
				try {
					bna.setColor(new Color(Integer.parseInt(rgb)));
				} catch (Exception e) {
					e.printStackTrace();
					errorString.append(e.getMessage());
				}
			}
			break;
		case "plotColor":
			String rgbPlotColor = this.getCascadingNodeAttribute(node, "RGB", false);
			if (rgbPlotColor != null) {
				try {
					bna.setPlotColor(new Color(Integer.parseInt(rgbPlotColor)));
				} catch (Exception e) {
					e.printStackTrace();
					errorString.append(e.getMessage());
				}
			}
			break;
		case "NodeReference":
			String hasRef = this.getCascadingNodeAttribute(node, "hasRef");
			if (hasRef != null && hasRef.equals("true")) {
				String refID = this.getCascadingNodeAttribute(node, "RefID");
				if (refID != null) {
					try {
						this.bna2Ref.put(bna, Integer.parseInt(refID));
					} catch (Exception e) {
						e.printStackTrace();
						errorString.append(e.getMessage());
					}
				}
			}
			break;
		case "constCheck":
			bna.setConstant(value.equals("true"));
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
		// for legacy
		case "maximumSpeed":
		case "maximalSpeed":
			if (bna instanceof DynamicNode) {
				String speed = StringUtils.isNotEmpty(value) ? value : "1";
				((DynamicNode) bna).setMaximalSpeed(speed);
			}
			break;
		case "knockedOut":
			if (bna instanceof DynamicNode) {
				((DynamicNode) bna).setKnockedOut("true".equals(value));
			} else if (bna instanceof Transition) {
				((Transition) bna).setKnockedOut("true".equals(value));
			}
			break;
		case "Proteins":
			((Gene) bna).addProtein(stringToArray(value));
			break;
		case "Enzymes":
			((Gene) bna).addEnzyme(stringToArray(value));
			break;
		case "Specification":
			((PathwayMap) bna).setSpecification(Boolean.parseBoolean(value));
			break;
		case "AaSequence":
			((Protein) bna).setAaSequence(value);
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

	private void addKEGGNode(BiologicalNodeAbstract bna, Node keggNode) {
		List<Node> keggNodeChildren = this.getChildAllNodes(keggNode);
		KEGGNode kegg = bna.getKEGGnode();
		for (Node child : keggNodeChildren) {
			// go through all Subnodes and look up what is set
			String value = this.getAttributeValue(child, child.getNodeName(), false);
			if (value == null) {
				continue;
			}
			switch (child.getNodeName()) {
			case "AllInvolvedElements":
				for (String item : value.split(" ")) {
					kegg.addInvolvedElement(item);
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
				kegg.setGeneOrthologyName(value);
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
				kegg.setKeggproduct(value);
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
				for (String item : stringToArray(value)) {
					kegg.addDBLink(item);
				}
				break;
			case "AllGeneMotifsAsVector":
				for (String item : stringToArray(value)) {
					kegg.addGeneMotif(item);
				}
				break;
			case "AllNamesAsVector":
				for (String item : stringToArray(value)) {
					kegg.addAlternativeName(item);
				}
				break;
			case "AllPathwayLinksAsVector":
				for (String item : stringToArray(value)) {
					kegg.addPathwayLink(item);
				}
				break;
			case "AllStructuresAsVector":
				for (String item : stringToArray(value)) {
					kegg.addStructure(item);
				}
				break;
			}
		}
	}

	private void addAnnotations(Node annotationsNode) {
		for (Node annotationNode : this.getChildAllNodes(annotationsNode, "Range")) {
			addAnnotation(annotationNode);
		}
	}

	/**
	 * adds ranges to the graph
	 */
	private void addAnnotation(Node annotationNode) {
		Map<String, String> attrs = new HashMap<>();
		attrs.put("title", "");
		String[] keys = { "textColor", "outlineType", "fillColor", "alpha", "maxY", "outlineColor", "maxX", "isEllipse",
				"minX", "minY", "titlePos", "title" };
		for (String key : keys) {
			Node tmp = this.getChildNode(annotationNode, key);
			if (tmp == null) {
				if (!key.equals("title") && strictParsing) {
					errorString.append("Error while parsing SMBL document. Tag " + key + " is missing!\n");
				}
			} else {
				String value = this.getAttributeValue(tmp, key);
				if (value == null && strictParsing) {
					errorString.append("Error while parsing SMBL document. Attribute " + key + " of tag " + key
							+ " is missing!\n");
				} else {
					attrs.put(key, value);
				}
			}
		}
		pathway.getGraph().addAnnotation(attrs);
	}

	private void handleReferences() {
		for (BiologicalNodeAbstract bna : bna2Ref.keySet()) {
			bna.setLogicalReference(this.nodes.get(bna2Ref.get(bna)));
		}
	}

	private Integer getID(String id) {
		if (NumberUtils.isCreatable(id)) {
			return Integer.parseInt(id);
		}
		if (id.contains("spec_")) {
			String[] idSplit = id.split("_");
			if (NumberUtils.isCreatable(idSplit[1])) {
				return Integer.parseInt(idSplit[1]);
			}
		}
		return -1;
	}

	private void handleReationParameters(Node parametersNode, GraphElementAbstract gea) {
		if (parametersNode != null) {
			for (Node parameter : this.getChildAllNodes(parametersNode, "Parameter")) {
				String pName = this.getCascadingNodeAttribute(parameter, "Name");
				if (pName == null) {
					continue;
				}
				String valueString = this.getCascadingNodeAttribute(parameter, "Value");
				if (valueString == null) {
					continue;
				}
				String unit = this.getCascadingNodeAttribute(parameter, "Unit");
				if (unit == null) {
					unit = "";
				}
				try {
					final BigDecimal value = new BigDecimal(valueString);
					gea.getParameters().add(new Parameter(pName, value, unit));
				} catch (Exception e) {
					e.printStackTrace();
					errorString.append(e.getMessage());
				}
			}
		}
	}
}
