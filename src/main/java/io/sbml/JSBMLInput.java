package io.sbml;

import biologicalElements.Elementdeclerations;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.*;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.*;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import graph.compartment.Compartment;
import graph.CreatePathway;
import graph.groups.Group;
import graph.gui.Parameter;
import gui.MainWindow;
import gui.RangeSelector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import util.VanesaUtility;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.*;

/**
 * To read a SBML file and put the results on the graph. A SBML which has been
 * passed over to an instance of this class will be parsed to the VANESA graph.
 *
 * @author Annika and Sandra
 */
public class JSBMLInput {
    private Pathway pathway;
    private final Hashtable<Integer, BiologicalNodeAbstract> nodes = new Hashtable<>();
    private final HashMap<String, Integer> string2id = new HashMap<>();
    private boolean coarsePathway = false;
    private final Hashtable<BiologicalNodeAbstract, Integer> bna2Ref = new Hashtable<>();
    private final boolean reverseEngineering = false;
    private final ArrayList<ArrayList<String>> inputGroups = new ArrayList<>();

    public JSBMLInput(Pathway pw) {
        pathway = pw;
    }

    public String loadSBMLFile(InputStream is, File file) {
        Document doc = VanesaUtility.loadXmlDocument(is);
        if (doc == null) {
            return "An error occurred";
        }
        if (pathway == null) {
            pathway = new CreatePathway(file.getName()).getPathway();
        } else {
            coarsePathway = true;
        }
        if (pathway.getFile() == null) {
            pathway.setFile(file);
        }
        Element sbmlNode = doc.getRootElement();
        Element modelNode = sbmlNode.getChild("model", null);
        // List<Element> modelNodeChildren = modelNode.getChildren();
        Element annotationNode = modelNode.getChild("annotation", null);
        createAnnotation(annotationNode);
        // not needed yet
        Element compartmentNode = modelNode.getChild("listOfCompartments", null);
        createCompartment(compartmentNode);
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
            return "An error occurred during the loading.";
        }
        return "";
    }

    /**
     * Groups and their members are saved in list, cause groups cant be created at this point, because nodes aren't
     * created yet
     */
    private void getInputGroups(Element groupNode) {
        if (groupNode == null) {
            return;
        }
        List<Element> groupChildren = groupNode.getChildren();
        for (Element group : groupChildren) {
            List<Element> groupMembers = group.getChildren();
            ArrayList<String> tmp = new ArrayList<>();
            for (Element node : groupMembers) {
                // add nodes mit ID/label to nodes list
                String label = node.getAttributeValue("Node");
                tmp.add(label);
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
                biologicalNodeAbstract.setInGroup(true);
                biologicalNodeAbstract.addGroup(tmp);
            }
            pathway.getGroups().add(tmp);
        }
        inputGroups.clear();
    }

    /**
     * creates the annotation of the model
     */
    private void createAnnotation(Element annotationNode) {
        if (annotationNode == null) {
            return;
        }
        Element modelNode = annotationNode.getChild("model", null);
        // get the information if the imported net is a Petri net
        boolean isPetri = false;
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
                for (Element range : rangeNodeChildren) {
                    addRange(range);
                }
            }
            Element groupNode = modelNode.getChild("listOfGroups", null);
            getInputGroups(groupNode);
        }
        pathway.setIsPetriNet(isPetri);
    }

    /**
     * creates the compartments not needed yet
     */
    private void createCompartment(Element compartmentNode) {
        if (compartmentNode == null) {
            return;
        }
        List<Element> compartmentNodeChildren = compartmentNode.getChildren();
        for (Element comp : compartmentNodeChildren) {
            String name = comp.getAttributeValue("id");
            if (name == null || name.equals("comp_") || name.length() == 0) {
                continue;
            }
            Color color = Color.GRAY;
            Element annotation = comp.getChild("annotation", null);
            if (annotation != null) {
                Element compAnnotation = annotation.getChild("spec", null);
                if (compAnnotation != null) {
                    Element elColor = compAnnotation.getChild("Color", null);
                    if (elColor != null) {

                        Element elSub = elColor.getChild("RGB", null);
                        if (elSub != null) {
                            color = new Color(Integer.parseInt(elSub.getAttributeValue("RGB")));
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
    private void createReaction(Element reactionNode) {
        if (reactionNode == null) {
            return;
        }
        List<Element> reactionNodeChildren = reactionNode.getChildren();
        // for each reaction
        for (Element reaction : reactionNodeChildren) {
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
                BiologicalNodeAbstract from = nodes.get(string2id.get(id));
                Element product = productsNode.getChild("speciesReference", null);
                id = product.getAttributeValue("species");
                BiologicalNodeAbstract to = nodes.get(string2id.get(id));
                String label = name;
                BiologicalEdgeAbstract bea = new ReactionEdge(label, name, from, to);
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
                        String attr;
                        if (elSub != null && bea instanceof PNArc) {
                            attr = elSub.getAttributeValue("Probability");
                            ((PNArc) bea).setProbability(Double.parseDouble(attr));
                        }

                        elSub = reacAnnotation.getChild("Priority", null);
                        if (elSub != null && bea instanceof PNArc) {
                            attr = elSub.getAttributeValue("Priority");
                            ((PNArc) bea).setPriority(Integer.parseInt(attr));
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
                        for (Element child : reacAnnotationChildren) {
                            // go through all Nodes and look up what is set
                            handleEdgeInformation(bea, child.getName(), child);
                        }
                    }
                }
                // set ID of the reaction
                id = reaction.getAttributeValue("id");
                int idint = this.getID(id);
                try {
                    if (idint > -1) {
                        bea.setID(idint, pathway);
                    } else {
                        bea.setID(pathway);
                    }
                } catch (IDAlreadyExistException ex) {
                    bea.setID(pathway);
                }
                this.pathway.addEdge(bea);
            }
        }
    }

    /**
     * creates the species
     */
    private void createSpecies(Element speciesNode) {
        if (speciesNode == null) {
            return;
        }
        List<Element> speciesNodeChildren = speciesNode.getChildren();
        String pathwayLink = null;
        // for each species
        for (Element species : speciesNodeChildren) {
            // test which bna has to be created
            // get name and label to create the bna
            String name = species.getAttributeValue("name");
            if (name == null) {
                name = "";
            }
            String label = name;
            BiologicalNodeAbstract bna = new Other(label, name);
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
                    bna = BiologicalNodeAbstractFactory.create(biologicalElement, null);
                    if (reverseEngineering) {
                        if (bna instanceof Place) {
                            bna = BiologicalNodeAbstractFactory.create(Elementdeclerations.metabolite, null);
                        } else if (bna instanceof Transition) {
                            bna = BiologicalNodeAbstractFactory.create(Elementdeclerations.enzyme, null);
                        }
                    }
                    bna.setLabel(label);
                    bna.setName(name);
                    String attr;
                    switch (bna.getBiologicalElement()) {
                        case Elementdeclerations.mRNA:
                        case Elementdeclerations.miRNA:
                        case Elementdeclerations.lncRNA:
                        case Elementdeclerations.sRNA:
                            // TODO
                            elSub = specAnnotation.getChild("NtSequence", null);
                            if (elSub != null) {
                                attr = elSub.getAttributeValue("NtSequence");
                                ((RNA) bna).setNtSequence(attr);
                            }
                            break;
                        case Elementdeclerations.pathwayMap:
                            elSub = specAnnotation.getChild("PathwayLink", null);
                            if (elSub != null) {
                                pathwayLink = String.valueOf(elSub.getAttributeValue("PathwayLink"));
                            }
                            break;
                        case Elementdeclerations.discretePlace:
                            elSub = specAnnotation.getChild("token", null);
                            attr = String.valueOf(elSub.getAttributeValue("token"));
                            ((Place) bna).setToken(Double.parseDouble(attr));
                            elSub = specAnnotation.getChild("tokenMin", null);
                            attr = String.valueOf(elSub.getAttributeValue("tokenMin"));
                            ((Place) bna).setTokenMin(Double.parseDouble(attr));
                            elSub = specAnnotation.getChild("tokenMax", null);
                            attr = String.valueOf(elSub.getAttributeValue("tokenMax"));
                            ((Place) bna).setTokenMax(Double.parseDouble(attr));
                            elSub = specAnnotation.getChild("tokenStart", null);
                            attr = String.valueOf(elSub.getAttributeValue("tokenStart"));
                            ((Place) bna).setTokenStart(Double.parseDouble(attr));
                            bna.setDiscrete(true);
                            elSub = specAnnotation.getChild("ConflictStrategy", null);
                            if (elSub != null) {
                                attr = elSub.getAttributeValue("ConflictStrategy");
                                ((Place) bna).setConflictStrategy(Integer.parseInt(attr));
                            }
                            break;
                        case Elementdeclerations.continuousPlace:
                            elSub = specAnnotation.getChild("token", null);
                            attr = String.valueOf(elSub.getAttributeValue("token"));
                            ((Place) bna).setToken(Double.parseDouble(attr));
                            elSub = specAnnotation.getChild("tokenMin", null);
                            attr = String.valueOf(elSub.getAttributeValue("tokenMin"));
                            ((Place) bna).setTokenMin(Double.parseDouble(attr));
                            elSub = specAnnotation.getChild("tokenMax", null);
                            attr = String.valueOf(elSub.getAttributeValue("tokenMax"));
                            ((Place) bna).setTokenMax(Double.parseDouble(attr));
                            elSub = specAnnotation.getChild("tokenStart", null);
                            attr = String.valueOf(elSub.getAttributeValue("tokenStart"));
                            ((Place) bna).setTokenStart(Double.parseDouble(attr));
                            bna.setDiscrete(false);
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
                            elSub = specAnnotation.getChild("maximalSpeed", null);
                            if (elSub != null) {
                                attr = String.valueOf(elSub.getAttributeValue("maximalSpeed"));
                                ((ContinuousTransition) bna).setMaximalSpeed(attr);
                            } else {
                                elSub = specAnnotation.getChild("maximumSpeed", null);
                                if (elSub != null) {
                                    attr = String.valueOf(elSub.getAttributeValue("maximumSpeed"));
                                    ((ContinuousTransition) bna).setMaximalSpeed(attr);
                                }
                            }
                            break;
                        case Elementdeclerations.stochasticTransition:
                            StochasticTransition st = (StochasticTransition) bna;
                            elSub = specAnnotation.getChild("distributionProperties", null);
                            Element elSubSub = elSub.getChild("distribution", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("distribution"));
                            st.setDistribution(attr);
                            elSubSub = elSub.getChild("h", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("h"));
                            st.setH(Double.parseDouble(attr));
                            elSubSub = elSub.getChild("a", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("a"));
                            st.setA(Double.parseDouble(attr));
                            elSubSub = elSub.getChild("b", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("b"));
                            st.setB(Double.parseDouble(attr));
                            elSubSub = elSub.getChild("c", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("c"));
                            st.setC(Double.parseDouble(attr));
                            elSubSub = elSub.getChild("mu", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("mu"));
                            st.setMu(Double.parseDouble(attr));
                            elSubSub = elSub.getChild("sigma", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("sigma"));
                            st.setSigma(Double.parseDouble(attr));
                            elSubSub = elSub.getChild("discreteEvents", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("discreteEvents"));
                            ArrayList<Integer> events = new ArrayList<>();
                            String[] eventTokens = attr.split(",");
                            for (String eventToken : eventTokens) {
                                events.add(Integer.parseInt(eventToken.trim()));
                            }
                            st.setEvents(events);
                            elSubSub = elSub.getChild("discreteEventProbabilities", null);
                            attr = String.valueOf(elSubSub.getAttributeValue("discreteEventProbabilities"));
                            ArrayList<Double> probs = new ArrayList<>();
                            String[] probTokens = attr.split(",");
                            for (String probToken : probTokens) {
                                probs.add(Double.parseDouble(probToken.trim()));
                            }
                            st.setProbabilities(probs);
                            break;
                    }
                    // get additional information
                    for (Element child : specAnnotation.getChildren()) {
                        // go through all Nodes and look up what is set
                        handleNodeInformation(bna, child.getName(), child);
                    }
                    // get the coordinates of the bna
                    elSub = specAnnotation.getChild("Coordinates", null);
                    Element elSubSub = elSub.getChild("x_Coordinate", null);
                    double xCoord = Double.parseDouble(elSubSub.getAttributeValue("x_Coordinate"));
                    elSubSub = elSub.getChild("y_Coordinate", null);
                    double yCoord = Double.parseDouble(elSubSub.getAttributeValue("y_Coordinate"));
                    p = new Point2D.Double(xCoord, yCoord);
                    elSub = specAnnotation.getChild("environmentNode", null);
                    if (elSub != null) {
                        if (String.valueOf(elSub.getAttributeValue("environmentNode")).equals("true")) {
                            bna.setMarkedAsEnvironment(true);
                        }
                    }
                    elSub = specAnnotation.getChild("Parameters", null);
                    // elSubSub = elSub.getChild("x_Coordinate", null);
                    ArrayList<Parameter> parameters = new ArrayList<>();
                    for (int j = 0; j < elSub.getChildren().size(); j++) {
                        elSubSub = elSub.getChildren().get(j);
                        String pname = elSubSub.getChild("Name", null).getAttributeValue("Name");
                        double value = Double.parseDouble(elSubSub.getChild("Value", null).getAttributeValue("Value"));
                        String unit = elSubSub.getChild("Unit", null).getAttributeValue("Unit");
                        parameters.add(new Parameter(pname, value, unit));
                    }
                    bna.setParameters(parameters);
                }
            }
            // test which annotations are set only if bna was created above set id and compartment of the bna
            String id = species.getAttributeValue("id");
            int intid = this.getID(id);
            try {
                if (intid > -1) {
                    bna.setID(intid, pathway);
                } else {
                    bna.setID(pathway);
                }
            } catch (IDAlreadyExistException ex) {
                bna.setID(pathway);
            }
            String compartment = species.getAttributeValue("compartment");
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
    private void buildUpHierarchy(Element annotationNode) {
        if (annotationNode == null) {
            return;
        }
        Element modelNode = annotationNode.getChild("model", null);
        if (modelNode == null) {
            return;
        }
        Element hierarchyList = modelNode.getChild("listOfHierarchies", null);
        if (hierarchyList == null) {
            return;
        }
        Map<Integer, Set<Integer>> hierarchyMap = new HashMap<>();
        Map<Integer, String> coarseNodeLabels = new HashMap<>();
        Map<Integer, Integer> hierarchyRootNodes = new HashMap<>();
        Set<Integer> openedCoarseNodes = new HashSet<>();
        for (Element coarseNode : hierarchyList.getChildren("coarseNode", null)) {
            if (coarseNode.getChildren("child", null) == null) {
                continue;
            }
            Set<Integer> childrenSet = new HashSet<>();
            for (Element childElement : coarseNode.getChildren("child", null)) {
                Integer childNode = Integer.parseInt(childElement.getAttributeValue("id").split("_")[1]);
                childrenSet.add(childNode);
            }
            Integer id = Integer.parseInt(coarseNode.getAttributeValue("id").split("_")[1]);
            String rootNode = coarseNode.getAttribute("root", null) == null ? "null"
                    : coarseNode.getAttributeValue("root");
            if (!rootNode.equals("null")) {
                hierarchyRootNodes.put(id, Integer.parseInt(coarseNode.getAttributeValue("root").split("_")[1]));
            }
            hierarchyMap.put(id, childrenSet);
            coarseNodeLabels.put(id, coarseNode.getAttributeValue("label"));
            if (coarseNode.getAttributeValue("opened") != null
                    && coarseNode.getAttributeValue("opened").equals("true")) {
                openedCoarseNodes.add(id);
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

    private void handleEdgeInformation(BiologicalEdgeAbstract bea, String attrtmp, Element child) {
        String value = child.getAttributeValue(attrtmp);
        switch (attrtmp) {
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
                    for (Element subChild : child.getChildren()) {
                        switch (subChild.getName()) {
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
    private void handleNodeInformation(BiologicalNodeAbstract bna, String attrtmp, Element child) {
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
                bna.setNodesize(Double.parseDouble(value));
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
                addKEGGNode(bna, child);
                break;
            case "Color":
                if (reverseEngineering) {
                    break;
                }
                Element elSub = child.getChild("RGB", null);
                if (elSub != null) {
                    int rgb = Integer.parseInt(elSub.getAttributeValue("RGB"));
                    Color col = new Color(rgb);
                    bna.setColor(col);
                }
                break;
            case "plotColor":
                elSub = child.getChild("RGB", null);
                if (elSub != null) {
                    int rgb = Integer.parseInt(elSub.getAttributeValue("RGB"));
                    Color col = new Color(rgb);
                    bna.setPlotColor(col);
                }
                break;
            case "NodeReference":
                elSub = child.getChild("hasRef", null);
                if (elSub.getAttributeValue("hasRef").equals("true")) {
                    elSub = child.getChild("RefID", null);
                    this.bna2Ref.put(bna, Integer.parseInt(elSub.getAttributeValue("RefID")));
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
                    ((DynamicNode) bna).setKnockedOut(false);
                    if (value != null && value.equals("true")) {
                        ((DynamicNode) bna).setKnockedOut(true);
                    }
                } else if (bna instanceof Transition) {
                    ((Transition) bna).setKnockedOut(false);
                    if (value != null && value.equals("true")) {
                        ((Transition) bna).setKnockedOut(true);
                    }
                }
                break;
            case "NtSequence":
                if (bna instanceof DNA) {
                    ((biologicalObjects.nodes.DNA) bna).setNtSequence(value);
                } else if (bna instanceof RNA) {
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
                ((biologicalObjects.nodes.PathwayMap) bna).setSpecification(Boolean.parseBoolean(value));
                break;
            case "AaSequence":
                ((biologicalObjects.nodes.Protein) bna).setAaSequence(value);
                break;
            case "Tarbase_accession":
                ((biologicalObjects.nodes.SRNA) bna).setTarbaseAccession(value);
                break;
            case "Tarbase_DS":
                ((biologicalObjects.nodes.SRNA) bna).setTarbaseDS(value);
                break;
            case "Tarbase_ensemble":
                ((biologicalObjects.nodes.SRNA) bna).setTarbaseEnsemble(value);
                break;
            case "Tarbase_IS":
                ((biologicalObjects.nodes.SRNA) bna).setTarbaseIS(value);
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

    private void addKEGGNode(BiologicalNodeAbstract bna, Element keggNode) {
        List<Element> keggNodeChildren = keggNode.getChildren();
        KEGGNode kegg = bna.getKEGGnode();
        for (Element child : keggNodeChildren) {
            // go through all Subnodes and look up what is set
            String name = child.getName();
            String value = child.getAttributeValue(name);
            switch (name) {
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

    /**
     * adds ranges to the graph
     */
    private void addRange(Element rangeElement) {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("title", "");
        String[] keys = {"textColor", "outlineType", "fillColor", "alpha", "maxY", "outlineColor", "maxX", "isEllipse",
                "minX", "minY", "titlePos", "title"};
        for (String key : keys) {
            Element tmp = rangeElement.getChild(key, null);
            if (tmp != null) {
                String value = tmp.getAttributeValue(key);
                attrs.put(key, value);
            }
        }
        RangeSelector.getInstance().addRangesInMyGraph(pathway.getGraph(), attrs);
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
}
