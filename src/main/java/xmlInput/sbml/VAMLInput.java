package xmlInput.sbml;

import biologicalElements.Elementdeclerations;
import biologicalElements.IDAlreadyExistException;
import biologicalElements.Pathway;
import biologicalObjects.edges.*;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.*;
import biologicalObjects.nodes.petriNet.*;
import graph.CreatePathway;
import gui.MainWindow;
import gui.RangeSelector;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.List;
import java.util.*;

public class VAMLInput {
    private final File file;
    private final Hashtable<Integer, BiologicalNodeAbstract> mapping = new Hashtable<>();

    public VAMLInput(File file) throws IOException {
        this(file, null);
    }

    public VAMLInput(File file, Pathway pw) throws IOException {
        this.file = file;
        try {
            getData(pw);
        } catch (ParserConfigurationException | SAXException | XMLStreamException e) {
            throw new IOException(e);
        }
    }

    private void addProjectDetails(Pathway pw, Element projectEL) {
        for (Element element : getChildren(projectEL)) {
            String localName = element.getLocalName();
            boolean[] set = new boolean[11];
            // if (localName.equals("title")) {
            //     pw.setTitle(file.getName());
            // }
            if (localName.equals("isPetriNet")) {
                pw.setIsPetriNet(Boolean.parseBoolean(element.getTextContent()));
            } else if (localName.equals("organism")) {
                pw.setOrganism(element.getTextContent());
            } else if (localName.equals("organismSpecification")) {
                pw.setOrganismSpecification(element.getTextContent());
            } else if (localName.equals("author")) {
                pw.setAuthor(element.getTextContent());
            } else if (localName.equals("version")) {
                pw.setVersion(element.getTextContent());
            } else if (localName.equals("date")) {
                pw.setDate(element.getTextContent());
            } else if (localName.equals("description")) {
                pw.setDescription(element.getTextContent());
            } else if (localName.equals("settings")) {
                int i = 0;
                for (Element s : getChildren(element)) {
                    set[i] = !s.getTextContent().equals("false");
                    i++;
                }
                // pw.setSettings(set);
            }
        }
    }

    private void addRange(Pathway pw, Element rangeElement) {
        Map<String, String> attrs = new HashMap<>();
        NamedNodeMap attributes = rangeElement.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node n = attributes.item(i);
            if (n instanceof Attr) {
                Attr attr = (Attr) n;
                attrs.put(attr.getLocalName(), attr.getNodeValue());
            }
        }
        RangeSelector.getInstance().addRangesInMyGraph(pw.getGraph(), attrs);
    }

    private void addEdge(Pathway pw, Element edgeElement) {
        String elementSpecification = "";
        int id = 0;
        String name = "";
        String label = "";
        int from = 0;
        int to = 0;
        String directed = "";
        String comment = "";
        Color color = null;
        ReactionPairEdge rpEdge = null;
        String function = "";
        double activationProb = 0.0;
        // String sequence = "";
        for (Element element : getChildren(edgeElement)) {
            String localName = element.getLocalName();
            if (localName.equals("elementSpecification")) {
                elementSpecification = element.getTextContent();
            } else if (localName.equals("id")) {
                try {
                    id = Integer.parseInt(element.getTextContent());
                } catch (NumberFormatException e) {
                    id = Integer.parseInt(element.getTextContent().substring(1));
                }
            } else if (localName.equals("from")) {
                try {
                    from = Integer.parseInt(element.getTextContent());
                } catch (NumberFormatException e) {
                    from = Integer.parseInt(element.getTextContent().substring(1));
                }
            } else if (localName.equals("to")) {
                // to = Integer.parseInt(element.getTextContent());
                try {
                    to = Integer.parseInt(element.getTextContent());
                } catch (NumberFormatException e) {
                    to = Integer.parseInt(element.getTextContent().substring(1));
                }
            } else if (localName.equals("comment")) {
                comment = element.getTextContent();
            } else if (localName.equals("label")) {
                label = element.getTextContent();
            } else if (localName.equals("name")) {
                name = element.getTextContent();
            } else if (localName.equals("colour")) {
                color = new Color(Integer.parseInt(element.getAttribute("r")),
                        Integer.parseInt(element.getAttribute("g")),
                        Integer.parseInt(element.getAttribute("b")));
            } else if (localName.equals("isDirected")) {
                directed = element.getTextContent();
            } else if (localName.equals("ReactionPairEdge")) {
                rpEdge = new ReactionPairEdge();
                for (Element element2 : getChildren(element)) {
                    if (element2.getLocalName().equals("name")) {
                        String name2 = element2.getTextContent();
                        rpEdge.setName(name2);
                    } else if (element2.getLocalName().equals("id")) {
                        rpEdge.setReactionPairID(element2.getTextContent());
                    } else if (element2.getLocalName().equals("type")) {
                        rpEdge.setType(element2.getTextContent());
                    }
                }
            } else if (localName.equals("passingTokens")) {
                function = element.getTextContent();
            } else if (localName.equals("function")) {
                function = element.getTextContent();
            } else if (localName.equals("activationProbability"))
                activationProb = Double.parseDouble(element.getTextContent());
        }
        if (mapping.containsKey(from) && mapping.containsKey(to)) {
            boolean isDirected = directed.equals("true");
            BiologicalEdgeAbstract bea;
            if (elementSpecification.equals(Elementdeclerations.compoundEdge)) {
                bea = new Compound(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.physicalInteraction)) {
                bea = new PhysicalInteraction(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.hiddenCompoundEdge)) {
                bea = new HiddenCompound(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.reactionEdge)) {
                bea = new ReactionEdge(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.reactionPair)) {
                bea = new ReactionPair(label, name, mapping.get(from), mapping.get(to));
                if (rpEdge != null) {
                    ((ReactionPair) bea).setReactionPairEdge(rpEdge);
                    ((ReactionPair) bea).setHasReactionPairEdge(true);
                }
            } else if (elementSpecification.equals(Elementdeclerations.activationEdge)) {
                bea = new Activation(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.inhibitionEdge)) {
                bea = new Inhibition(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.expressionEdge)) {
                bea = new Expression(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.repressionEdge)) {
                bea = new Repression(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.indirectEffectEdge)) {
                bea = new IndirectEffect(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.stateChangeEdge)) {
                bea = new StateChange(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.bindingEdge)) {
                bea = new BindingAssociation(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.dissociationEdge)) {
                bea = new Dissociation(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.phosphorylationEdge)) {
                bea = new Phosphorylation(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.dephosphorylationEdge)) {
                bea = new Dephosphorylation(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.glycosylationEdge)) {
                bea = new Glycosylation(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.ubiquitinationEdge)) {
                bea = new Ubiquitination(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.methylationEdge)) {
                bea = new Methylation(label, name, mapping.get(from), mapping.get(to));
            } else if (elementSpecification.equals(Elementdeclerations.pnArc) || elementSpecification.equals("PN Discrete Edge")) {
                bea = new PNArc(mapping.get(from), mapping.get(to), label, name, Elementdeclerations.pnArc, function);
                ((PNArc) bea).setProbability(activationProb);
            } else if (elementSpecification.equals(Elementdeclerations.pnInhibitorArc)) {
                bea = new PNArc(mapping.get(from), mapping.get(to), label, name, Elementdeclerations.pnInhibitorArc, function);
                ((PNArc) bea).setProbability(activationProb);
            } else {
                System.err.println("Try to instantiate an abstract Edge!!!");
                bea = new ReactionEdge(label, name, mapping.get(from), mapping.get(to));
                // bea = new BiologicalEdgeAbstract(label, name, mapping.get(from), mapping.get(to));
            }
            try {
                bea.setID(id, pw);
            } catch (IDAlreadyExistException ex) {
                bea.setID(pw);
            }
            bea.setDirected(isDirected);
            bea.setColor(color);
            bea.setComments(comment);
            pw.addEdge(bea);
        }
    }

    private KEGGNode addKeggNode(Element keggElement) {
        KEGGNode node = new KEGGNode();
        for (Element element : getChildren(keggElement)) {
            String localName = element.getLocalName();
            String elementText = element.getTextContent();
            if (localName.equals("entryID")) {
                node.setKEGGentryID(elementText);
            } else if (localName.equals("entryMap")) {
                node.setKEGGentryMap(elementText);
            } else if (localName.equals("entryName")) {
                node.setKEGGentryName(elementText);
            } else if (localName.equals("entryType")) {
                node.setKEGGentryType(elementText);
            } else if (localName.equals("entryLink")) {
                node.setKEGGentryLink(elementText);
            } else if (localName.equals("entryReaction")) {
                node.setKEGGentryReaction(elementText);
            } else if (localName.equals("entryComponent")) {
                node.setKEGGComponent(elementText);
            } else if (localName.equals("entryPathway")) {
                node.setKEGGPathway(elementText);
            } else if (localName.equals("entryComment")) {
                node.setKeggComment(elementText);
            } else if (localName.equals("enzymeClass")) {
                node.setKeggenzymeClass(elementText);
            } else if (localName.equals("enzymeSysName")) {
                node.setKeggsysName(elementText);
            } else if (localName.equals("enzymeReaction")) {
                node.setKeggreaction(elementText);
            } else if (localName.equals("enzymeSubstrate")) {
                node.setKeggsubstrate(elementText);
            } else if (localName.equals("enzymeProduct")) {
                node.setKeggproduct(elementText);
            } else if (localName.equals("enzymeCoFactor")) {
                node.setKeggcofactor(elementText);
            } else if (localName.equals("enzymeReference")) {
                node.setKeggreference(elementText);
            } else if (localName.equals("enzymeEffector")) {
                node.setKeggeffector(elementText);
            } else if (localName.equals("enzymeOrthology")) {
                node.setKeggorthology(elementText);
            } else if (localName.equals("compoundFormular")) {
                node.setCompoundFormula(elementText);
            } else if (localName.equals("compoundMass")) {
                node.setCompoundMass(elementText);
            } else if (localName.equals("compoundComment")) {
                node.setCompoundComment(elementText);
            } else if (localName.equals("compoundRemarks")) {
                node.setCompoundRemarks(elementText);
            } else if (localName.equals("compoundAtomsNr")) {
                node.setCompoundAtomsNr(elementText);
            } else if (localName.equals("compoundAtoms")) {
                node.setCompoundAtoms(elementText);
            } else if (localName.equals("compoundBondNr")) {
                node.setCompoundBondNr(elementText);
            } else if (localName.equals("compoundBonds")) {
                node.setCompoundBonds(elementText);
            } else if (localName.equals("compoundSequence")) {
                node.setCompoundSequence(elementText);
            } else if (localName.equals("compoundModule")) {
                node.setCompoundModule(elementText);
            } else if (localName.equals("compoundOrganism")) {
                node.setCompoundOrganism(elementText);
            } else if (localName.equals("geneName")) {
                node.setGeneName(elementText);
            } else if (localName.equals("geneDefenition")) {
                node.setGeneDefinition(elementText);
            } else if (localName.equals("genePosition")) {
                node.setGenePosition(elementText);
            } else if (localName.equals("geneCodons")) {
                node.setGeneCodonUsage(elementText);
            } else if (localName.equals("geneAAseqNr")) {
                node.setGeneAAseqNr(elementText);
            } else if (localName.equals("geneAAseq")) {
                node.setGeneAAseq(elementText);
            } else if (localName.equals("geneNtSeqNr")) {
                node.setGeneNtseqNr(elementText);
            } else if (localName.equals("geneNtseq")) {
                node.setGeneNtSeq(elementText);
            } else if (localName.equals("geneOrthology")) {
                node.setGeneOrthology(elementText);
            } else if (localName.equals("geneOrthologyName")) {
                node.setGeneOrthologyName(elementText);
            } else if (localName.equals("geneEnzyme")) {
                node.setGeneEnzyme(elementText);
            } else if (localName.equals("glycanOrthology")) {
                node.setGlycanOrthology(elementText);
            } else if (localName.equals("glycanBracket")) {
                node.setGlycanBracket(elementText);
            } else if (localName.equals("glycanComposition")) {
                node.setGlycanComposition(elementText);
            } else if (localName.equals("glycanNode")) {
                node.setGlycanNode(elementText);
            } else if (localName.equals("glycanEdge")) {
                node.setGlycanEdge(elementText);
            } else if (localName.equals("glycanName")) {
                node.setGlycanName(elementText);
            } else if (localName.equals("graphicLabel")) {
                node.setNodeLabel(elementText);
            } else if (localName.equals("graphic_xPos")) {
                node.setXPos(Double.parseDouble(elementText));
            } else if (localName.equals("graphic_yPos")) {
                node.setYPos(Double.parseDouble(elementText));
            } else if (localName.equals("graphicShape")) {
                node.setShape(elementText);
            } else if (localName.equals("graphicWidth")) {
                node.setWidth(elementText);
            } else if (localName.equals("graphicHeight")) {
                node.setHeight(elementText);
            } else if (localName.equals("graphicForegroundColour")) {
                node.setForegroundColour(elementText);
            } else if (localName.equals("graphicBackgroundColour")) {
                node.setBackgroundColour(elementText);
            } else if (localName.equals("allDBLinks")) {
                for (Element temp : getChildren(element)) {
                    node.addDBLink(temp.getTextContent());
                }
            } else if (localName.equals("allPathways")) {
                for (Element temp : getChildren(element)) {
                    node.addPathwayLink(temp.getTextContent());
                }
            } else if (localName.equals("allStructures")) {
                for (Element temp : getChildren(element)) {
                    node.addStructure(temp.getTextContent());
                }
            } else if (localName.equals("allMotifs")) {
                for (Element temp : getChildren(element)) {
                    node.addGeneMotif(temp.getTextContent());
                }
            }
        }
        return node;
    }

    private void addNetworkNode(Pathway pw, Element node) {
        int vertexID;
        try {
            vertexID = Integer.parseInt(node.getAttribute("id"));
        } catch (NumberFormatException e) {
            vertexID = Integer.parseInt(node.getAttribute("id").substring(1));
        }
        String biologicalElement = "";
        String label = "";
        String name = "";
        String comment = "";
        String reference = "";
        String location = "";
        Color color = null;
        double xCoord = 0.0;
        double yCoord = 0.0;
        KEGGNode keggNode = null;
        String aaSequence = "";
        String pathway = null;
        for (Element element : getChildren(node)) {
            String localName = element.getLocalName();
            String elementText = element.getTextContent();
            if (localName.equals("pathway"))
                pathway = elementText;
            else if (localName.equals("coordinates")) {
                xCoord = Double.parseDouble(element.getAttribute("x"));
                yCoord = Double.parseDouble(element.getAttribute("y"));
            } else if (localName.equals("elementSpecification")) {
                biologicalElement = elementText;
            } else if (localName.equals("colour")) {
                color = new Color(Integer.parseInt(element.getAttribute("r")),
                        Integer.parseInt(element.getAttribute("g")),
                        Integer.parseInt(element.getAttribute("b")));
            } else if (localName.equals("label")) {
                label = elementText;
            } else if (localName.equals("name")) {
                name = elementText;
            } else if (localName.equals("reference")) {
                reference = elementText;
            } else if (localName.equals("comment")) {
                comment = elementText;
            } else if (localName.equals("location")) {
                location = elementText;
            } else if (localName.equals("keggProperties")) {
                keggNode = addKeggNode(element);
            } else if (localName.equals("aaSequence")) {
                aaSequence = elementText;
            }
        }
        BiologicalNodeAbstract bna = null;
        if (biologicalElement.equals(Elementdeclerations.enzyme)) {
            bna = new Enzyme(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.others)) {
            bna = new Other(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.complex)) {
            bna = new Complex(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.degraded)) {
            bna = new Degraded(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.dna)) {
            bna = new DNA(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.homodimerFormation)) {
            bna = new HomodimerFormation(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.ligandBinding)) {
            bna = new LigandBinding(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.membraneChannel)) {
            bna = new MembraneChannel(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.membraneReceptor)) {
            bna = new Receptor(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.mRNA)) {
            bna = new MRNA(label, name);
            ((MRNA) bna).setNtSequence(node.getAttribute("NtSequence"));
        } else if (biologicalElement.equals(Elementdeclerations.orthologGroup)) {
            bna = new OrthologGroup(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.pathwayMap)) {
            bna = new PathwayMap(label, name);
            if (pathway != null) {
                try {
                    File f = new File("Temp");
                    f.delete();
                    try (FileWriter w = new FileWriter(f, false)) {
                        w.write(pathway);
                    }
                    File file = new File("Temp");
                    Pathway newPW = new Pathway(label);
                    new VAMLInput(file, newPW);
                    ((PathwayMap) bna).setPathwayLink(newPW);
                    newPW.setParent(pw);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (biologicalElement.equals(Elementdeclerations.inhibitor)) {
            bna = new Inhibitor(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.protein)) {
            bna = new Protein(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.receptor)) {
            bna = new Receptor(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.sRNA)) {
            bna = new SRNA(label, name);
            ((SRNA) bna).setNtSequence(node.getAttribute("NtSequence"));
        } else if (biologicalElement.equals(Elementdeclerations.metabolite)) {
            bna = new Metabolite(label, name);
            // kept for legacy
        } else if (biologicalElement.equals(Elementdeclerations.smallMolecule)) {
            bna = new Metabolite(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.solubleReceptor)) {
            bna = new SolubleReceptor(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.transcriptionFactor)) {
            bna = new TranscriptionFactor(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.glycan)) {
            bna = new Glycan(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.compound)) {
            bna = new CompoundNode(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.disease)) {
            bna = new Disease(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.drug)) {
            bna = new Drug(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.gene)) {
            bna = new Gene(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.go)) {
            bna = new GeneOntology(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.reaction)) {
            bna = new Reaction(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.matrix)) {
            bna = new Matrix(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.factor)) {
            bna = new Factor(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.fragment)) {
            bna = new Fragment(label, name);
        } else if (biologicalElement.equals(Elementdeclerations.site)) {
            bna = new Site(label, name);
        } else if (biologicalElement.equals("Discrete Place")) {
            bna = new DiscretePlace(label, name);
            String token = node.getAttribute("token");
            String tokenMin = node.getAttribute("tokenMin");
            String tokenMax = node.getAttribute("tokenMax");
            String tokenStart = node.getAttribute("tokenStart");
            ((Place) bna).setToken(Double.parseDouble(token));
            ((Place) bna).setTokenMin(Double.parseDouble(tokenMin));
            ((Place) bna).setTokenMax(Double.parseDouble(tokenMax));
            ((Place) bna).setTokenStart(Double.parseDouble(tokenStart));
        } else if (biologicalElement.equals("Continuous Place")) {
            bna = new ContinuousPlace(label, name);
            String token = node.getAttribute("token");
            String tokenMin = node.getAttribute("tokenMin");
            String tokenMax = node.getAttribute("tokenMax");
            String tokenStart = node.getAttribute("tokenStart");
            ((Place) bna).setToken(Double.parseDouble(token));
            ((Place) bna).setTokenMin(Double.parseDouble(tokenMin));
            ((Place) bna).setTokenMax(Double.parseDouble(tokenMax));
            ((Place) bna).setTokenStart(Double.parseDouble(tokenStart));
        } else if (biologicalElement.equals("Discrete Transition")) {
            bna = new DiscreteTransition(label, name);
            String delay = node.getAttribute("delay");
            ((DiscreteTransition) bna).setDelay(Double.parseDouble(delay));
        } else if (biologicalElement.equals("Continuous Transition")) {
            bna = new ContinuousTransition(label, name);
            String maximalSpeed = node.getAttribute("maximalSpeed");
            if (maximalSpeed == null || maximalSpeed.equals("")) {
                maximalSpeed = node.getAttribute("maximumSpeed");
            }
            ((ContinuousTransition) bna).setMaximalSpeed(maximalSpeed);
            if (maximalSpeed == null || maximalSpeed.equals("")) {
                System.out.println("speed");
                ((ContinuousTransition) bna).setMaximalSpeed("1");
            }
        } else if (biologicalElement.equals("Stochastic Transition")) {
            bna = new StochasticTransition(label, name);
            ((StochasticTransition) bna).setDistribution(node.getAttribute("distribution"));
        }
        if (bna != null) {
            // bna.setCompartment(location);
            pw.getCompartmentManager().setCompartment(bna, pw.getCompartmentManager().getCompartment(location));
            bna.setComments(comment);
            bna.setColor(color);
            try {
                int id = Integer.parseInt(node.getAttribute("ElementID"));
                bna.setID(id, pw);
            } catch (Exception e) {
            }
            if (keggNode != null) {
                bna.setKEGGnode(keggNode);
            }
            mapping.put(vertexID, bna);
            if (bna instanceof Protein) {
                Protein protein = (Protein) bna;
                protein.setAaSequence(aaSequence);
            }
            // bna.setID(vertexID);
            pw.addVertex(bna, new Point2D.Double(xCoord, yCoord));
        }
    }

    private void getData(Pathway pw) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        InputStream in = new FileInputStream(file);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        javax.xml.stream.XMLStreamReader reader = factory.createXMLStreamReader(in);
        if (pw == null) {
            pw = new CreatePathway(file.getName()).getPathway();
        }
        pw.setFile(file);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(in);
        Element docEl = doc.getDocumentElement();
        Element modelEl = getFirstChildWithName(docEl, "model");
        if (modelEl != null) {
            Element annotationEL = getFirstChildWithName(modelEl, "annotation");
            if (annotationEL != null) {
                Element layoutsEL = getFirstChildWithName(annotationEL, "NetworkEditorSettings");
                if (layoutsEL != null) {
                    Element projectEL = getFirstChildWithName(layoutsEL, "project");
                    if (projectEL != null) {
                        addProjectDetails(pw, projectEL);
                    }
                    for (Element element : getChildrenWithName(layoutsEL, "element")) {
                        addNetworkNode(pw, element);
                    }
                    for (Element element : getChildrenWithName(layoutsEL, "edge")) {
                        addEdge(pw, element);
                    }
                    for (Element element : getChildrenWithName(layoutsEL, "rangeInfo")) {
                        addRange(pw, element);
                    }
                }
            }
        }
        pw.getGraph().restartVisualizationModel();
        MainWindow.getInstance().updateProjectProperties();
        reader.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Element getFirstChildWithName(Element element, String name) {
        NodeList children = element.getElementsByTagName(name);
        return children.getLength() == 0 ? null : (Element) children.item(0);
    }

    private List<Element> getChildren(Element element) {
        return getNodeListAsList(element.getChildNodes());
    }

    private List<Element> getChildrenWithName(Element element, String name) {
        return getNodeListAsList(element.getElementsByTagName(name));
    }

    private List<Element> getNodeListAsList(NodeList list) {
        List<Element> result = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            result.add((Element) list.item(i));
        }
        return result;
    }
}
