package xmlInput.sbml;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
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
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.Complex;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.DAWISNode;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Degraded;
import biologicalObjects.nodes.Disease;
import biologicalObjects.nodes.Drug;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Factor;
import biologicalObjects.nodes.Fragment;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.GeneOntology;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.HomodimerFormation;
import biologicalObjects.nodes.Inhibitor;
import biologicalObjects.nodes.KEGGNode;
import biologicalObjects.nodes.LigandBinding;
import biologicalObjects.nodes.MRNA;
import biologicalObjects.nodes.Matrix;
import biologicalObjects.nodes.MembraneChannel;
import biologicalObjects.nodes.OrthologGroup;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.Reaction;
import biologicalObjects.nodes.Receptor;
import biologicalObjects.nodes.SRNA;
import biologicalObjects.nodes.Site;
import biologicalObjects.nodes.SmallMolecule;
import biologicalObjects.nodes.SolubleReceptor;
import biologicalObjects.nodes.TranscriptionFactor;
//import edu.uci.ics.jung.graph.Vertex;
import graph.ContainerSingelton;
import graph.CreatePathway;
import graph.GraphContainer;
import gui.MainWindowSingelton;
import gui.RangeSelector;

/**
 * @author sebastian and olga
 */

public class VAMLInput {

	private File file = null;
	private final Hashtable<Integer, BiologicalNodeAbstract> mapping = new Hashtable<Integer, BiologicalNodeAbstract>();
	GraphContainer con = ContainerSingelton.getInstance();

	public VAMLInput(File file) throws IOException, XMLStreamException {
		this.file = file;

		getData(null);

	}

	public VAMLInput(File file, Pathway pw) throws IOException,
			XMLStreamException {
		this.file = file;

		getData(pw);

	}

	private void addProjectDetails(Pathway pw, OMElement projectEL) {

		Iterator<OMElement> it = projectEL.getChildren();
		while (it.hasNext()) {

			OMElement element = it.next();
			boolean[] set = new boolean[11];
			// if (element.getLocalName().equals("title")) {
			// pw.setTitle(file.getName());
			// }
			if (element.getLocalName().equals("isPetriNet")) {
				// System.out.println(element.getText());
				pw.setPetriNet(Boolean.parseBoolean(element.getText()));
			} else if (element.getLocalName().equals("organism")) {
				pw.setOrganism(element.getText());
			} else if (element.getLocalName().equals("organismSpecification")) {
				pw.setSpecification(element.getText());
			} else if (element.getLocalName().equals("author")) {
				pw.setAuthor(element.getText());
			} else if (element.getLocalName().equals("version")) {
				pw.setVersion(element.getText());
			} else if (element.getLocalName().equals("date")) {
				pw.setDate(element.getText());
			} else if (element.getLocalName().equals("description")) {
				pw.setDescription(element.getText());
			} else if (element.getLocalName().equals("settings")) {
				Iterator iterator = element.getChildElements();
				int i = 0;
				while (iterator.hasNext()) {
					OMElement s = (OMElement) iterator.next();
					if (s.getText().equals("false")) {
						set[i] = false;
						i = i + 1;

					} else {
						set[i] = true;
						i = i + 1;
					}
				}
				pw.setDAWISProject();
				pw.setSettings(set);
			}
		}
	}

	private void addRange(Pathway pw, OMElement rangeElement) {
		Map<String, String> attrs = new HashMap<String, String>();
		for (Iterator it = rangeElement.getAllAttributes(); it.hasNext();) {
			OMAttribute attr = (OMAttribute) it.next();
			attrs.put(attr.getLocalName(), attr.getAttributeValue());
		}
		RangeSelector.getInstance().addRangesInMyGraph(pw.getGraph(), attrs);
	}

	private void addEdge(Pathway pw, OMElement edgeElement) {

		Iterator<OMElement> it = edgeElement.getChildren();

		String elementSpecification = "";
		Integer id = 0;
		String name = "";
		String label = "";
		Integer from = 0;
		Integer to = 0;
		String reference = "";
		String directed = "";
		String comment = "";
		Color color = null;
		BiologicalEdgeAbstract bea = null;
		KEGGEdge keggEdge = null;
		ReactionPairEdge rpEdge = null;
		Double passingTokens = 0.0;
		String function = "";
		Double activationProb = 0.0;

		// String sequence = "";

		
		
		
		while (it.hasNext()) {

			OMElement element = it.next();

			if (element.getLocalName().equals("elementSpecification")) {
				elementSpecification = element.getText();
			} else if (element.getLocalName().equals("id")) {
				// System.out.println("ID gefunden_____"+element.getText());
				
				
				try {
					id = Integer.parseInt(element.getText());
				} catch (NumberFormatException e) {
					id = Integer.parseInt(element.getText().substring(1));
				}
				
				// System.out.println("idddd "+id);
			} else if (element.getLocalName().equals("from")) {
				
				try {
					from = Integer.parseInt(element.getText());
				} catch (NumberFormatException e) {
					from = Integer.parseInt(element.getText().substring(1));
				}
			} else if (element.getLocalName().equals("to")) {
				//to = Integer.parseInt(element.getText());
				
				try {
					to = Integer.parseInt(element.getText());
				} catch (NumberFormatException e) {
					to = Integer.parseInt(element.getText().substring(1));
				}
			} else if (element.getLocalName().equals("comment")) {
				comment = element.getText();
			} else if (element.getLocalName().equals("label")) {
				label = element.getText();
			} else if (element.getLocalName().equals("name")) {
				name = element.getText();
			} else if (element.getLocalName().equals("reference")) {
				reference = element.getText();
			} else if (element.getLocalName().equals("colour")) {
				color = new Color(Integer.parseInt(element
						.getAttributeValue(new QName("r"))),
						Integer.parseInt(element.getAttributeValue(new QName(
								"g"))), Integer.parseInt(element
								.getAttributeValue(new QName("b"))));
			} else if (element.getLocalName().equals("isDirected")) {
				directed = element.getText();
			} else if (element.getLocalName().equals("KeggEdge")) {

				keggEdge = new KEGGEdge();
				Iterator<OMElement> it3 = element.getChildren();

				while (it3.hasNext()) {
					OMElement element2 = it3.next();

					if (element2.getLocalName().equals("entry1")) {
						keggEdge.setEntry1(element2.getText());
					} else if (element2.getLocalName().equals("entry2")) {
						keggEdge.setEntry2(element2.getText());
					} else if (element2.getLocalName().equals("type")) {
						keggEdge.setType(element2.getText());
					} else if (element2.getLocalName().equals("description")) {
						keggEdge.setDescription(element2.getText());
					} else if (element2.getLocalName().equals("name")) {
						keggEdge.setName(element2.getText());
					} else if (element2.getLocalName().equals("remark")) {
						keggEdge.setRemark(element2.getText());
					} else if (element2.getLocalName().equals("orthology")) {
						keggEdge.setOrthology(element2.getText());
					} else if (element2.getLocalName().equals("reference")) {
						keggEdge.setReference(element2.getText());
					} else if (element2.getLocalName().equals("comment")) {
						keggEdge.setComment(element2.getText());
					} else if (element2.getLocalName().equals("definition")) {
						keggEdge.setDefinition(element2.getText());
					} else if (element2.getLocalName().equals("equation")) {
						keggEdge.setEquation(element2.getText());
					} else if (element2.getLocalName().equals("rpair")) {
						keggEdge.setRpair(element2.getText());
					} else if (element2.getLocalName().equals("KEEGReactionID")) {
						keggEdge.setKEEGReactionID(element2.getText());
					} else if (element2.getLocalName().equals("InvolvedEnzyme")) {
						keggEdge.setInvolvedEnzyme(element2.getText());
					} else if (element2.getLocalName().equals("products")) {

						Iterator<OMElement> it4 = element2.getChildren();
						while (it4.hasNext()) {
							OMElement element3 = it4.next();
							keggEdge.addProduct(element3.getText());
						}

					} else if (element2.getLocalName().equals("substrates")) {

						Iterator<OMElement> it4 = element2.getChildren();
						while (it4.hasNext()) {
							OMElement element3 = it4.next();
							keggEdge.addSubstrate(element3.getText());
						}
					}
				}
			} else if (element.getLocalName().equals("ReactionPairEdge")) {
				rpEdge = new ReactionPairEdge();

				Iterator it3 = element.getChildren();

				while (it3.hasNext()) {
					OMElement element2 = (OMElement) it3.next();
					if (element2.getLocalName().equals("name")) {
						String name2 = element2.getText();
						rpEdge.setName(name2);
					} else if (element2.getLocalName().equals("id")) {
						rpEdge.setReactionPairID(element2.getText());
					} else if (element2.getLocalName().equals("type")) {
						rpEdge.setType(element2.getText());
					}
				}
			}
			// fuer alte vaml-Dateien
			else if (element.getLocalName().equals("passingTokens")) {
				function = element.getText();
			} else if (element.getLocalName().equals("function")) {
				function = element.getText();
			} else if (element.getLocalName().equals("activationProbability"))
				activationProb = Double.valueOf(element.getText());
		}

		if (mapping.containsKey(from) && mapping.containsKey(to)) {

			boolean isDirected = false;
			if (directed.equals("true")) {
				isDirected = true;
			}

			if (elementSpecification.equals(Elementdeclerations.compoundEdge)) {

				bea = new Compound(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.physicalInteraction)) {

				bea = new PhysicalInteraction(label, name, mapping.get(from),
						mapping.get(to));
			} else if (elementSpecification
					.equals(Elementdeclerations.hiddenCompoundEdge)) {

				bea = new HiddenCompound(label, name, mapping.get(from),
						mapping.get(to));
			} else if (elementSpecification
					.equals(Elementdeclerations.reactionEdge)) {

				// System.out.println("reaction edge");
				bea = new ReactionEdge(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.reactionPair)) {

				// System.out.println("reaction pair");
				bea = new ReactionPair(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.activationEdge)) {

				bea = new Activation(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.inhibitionEdge)) {

				bea = new Inhibition(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.expressionEdge)) {

				bea = new Expression(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.repressionEdge)) {

				bea = new Repression(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.indirectEffectEdge)) {

				bea = new IndirectEffect(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.stateChangeEdge)) {

				bea = new StateChange(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.bindingEdge)) {

				bea = new BindingAssociation(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.dissociationEdge)) {

				bea = new Dissociation(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.phosphorylationEdge)) {

				bea = new Phosphorylation(label, name, mapping.get(from),
						mapping.get(to));
			} else if (elementSpecification
					.equals(Elementdeclerations.dephosphorylationEdge)) {

				bea = new Dephosphorylation(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.glycosylationEdge)) {

				bea = new Glycosylation(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.ubiquitinationEdge)) {

				bea = new Ubiquitination(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.methylationEdge)) {

				bea = new Methylation(label, name, mapping.get(from),
						mapping.get(to));

			} else if (elementSpecification
					.equals(Elementdeclerations.pnDiscreteEdge)) {
				bea = new PNEdge(mapping.get(from), mapping.get(to), label,
						name, "PN Discrete Edge", function);
				((PNEdge) bea).setActivationProbability(activationProb);
			} else if (elementSpecification
					.equals(Elementdeclerations.pnContinuousEdge)) {
				bea = new PNEdge(mapping.get(from), mapping.get(to), label,
						name, "PN Continues Edge", function);
				((PNEdge) bea).setActivationProbability(activationProb);
			} else if (elementSpecification
					.equals(Elementdeclerations.pnInhibitionEdge)) {
				bea = new PNEdge(mapping.get(from), mapping.get(to), label,
						name, "PN Inhibition Edge", function);
				((PNEdge) bea).setActivationProbability(activationProb);
			}

			else {
				System.err.println("Try to instantiate an abstract Edge!!!");
				bea = new ReactionEdge(label, name, mapping.get(from),
						mapping.get(to));

				// bea = new BiologicalEdgeAbstract(label, name,
				// mapping.get(from), mapping.get(to));

			}
			// System.out.println("spech: "+elementSpecification);
			bea.setID(id);
			bea.setDirected(isDirected);
			bea.setColor(color);
			bea.setComments(comment);

			if (reference.equals("true")) {
				bea.setReference(true);
			} else {
				bea.setReference(false);
			}

			if (keggEdge != null) {
				bea.setKeggEdge(keggEdge);
				bea.hasKEGGEdge(true);
			}
			if (rpEdge != null) {
				bea.setReactionPairEdge(rpEdge);
				bea.hasReactionPairEdge(true);
			}

			// System.out.println("vor: "+bea.getID());
			// System.out.println(elementSpecification);
			// System.out.println("edge_id: "+bea.getID());
			pw.addEdge(bea);
		}

	}

	private KEGGNode addKeggNode(OMElement keggElement) {

		KEGGNode node = new KEGGNode();
		Iterator<OMElement> it = keggElement.getChildren();
		while (it.hasNext()) {

			OMElement element = it.next();

			if (element.getLocalName().equals("entryID")) {
				node.setKEGGentryID(element.getText());
			} else if (element.getLocalName().equals("entryMap")) {
				node.setKEGGentryMap(element.getText());
			} else if (element.getLocalName().equals("entryName")) {
				node.setKEGGentryName(element.getText());
			} else if (element.getLocalName().equals("entryType")) {
				node.setKEGGentryType(element.getText());
			} else if (element.getLocalName().equals("entryLink")) {
				node.setKEGGentryLink(element.getText());
			} else if (element.getLocalName().equals("entryReaction")) {
				node.setKEGGentryReaction(element.getText());
			} else if (element.getLocalName().equals("entryComponent")) {
				node.setKEGGComponent(element.getText());
			} else if (element.getLocalName().equals("entryPathway")) {
				node.setKEGGPathway(element.getText());
			} else if (element.getLocalName().equals("entryComment")) {
				node.setKeggComment(element.getText());
			}

			else if (element.getLocalName().equals("enzymeClass")) {
				node.setKeggenzymeClass(element.getText());
			} else if (element.getLocalName().equals("enzymeSysName")) {
				node.setKeggsysName(element.getText());
			} else if (element.getLocalName().equals("enzymeReaction")) {
				node.setKeggreaction(element.getText());
			} else if (element.getLocalName().equals("enzymeSubstrate")) {
				node.setKeggsubstrate(element.getText());
			} else if (element.getLocalName().equals("enzymeProduct")) {
				node.setKeggprodukt(element.getText());
			} else if (element.getLocalName().equals("enzymeCoFactor")) {
				node.setKeggcofactor(element.getText());
			} else if (element.getLocalName().equals("enzymeReference")) {
				node.setKeggreference(element.getText());
			} else if (element.getLocalName().equals("enzymeEffector")) {
				node.setKeggeffector(element.getText());
			} else if (element.getLocalName().equals("enzymeOrthology")) {
				node.setKeggorthology(element.getText());
			}

			else if (element.getLocalName().equals("compoundFormular")) {
				node.setCompoundFormula(element.getText());
			} else if (element.getLocalName().equals("compoundMass")) {
				node.setCompoundMass(element.getText());
			} else if (element.getLocalName().equals("compoundComment")) {
				node.setCompoundComment(element.getText());
			} else if (element.getLocalName().equals("compoundRemarks")) {
				node.setCompoundRemarks(element.getText());
			} else if (element.getLocalName().equals("compoundAtomsNr")) {
				node.setCompoundAtomsNr(element.getText());
			} else if (element.getLocalName().equals("compoundAtoms")) {
				node.setCompoundAtoms(element.getText());
			} else if (element.getLocalName().equals("compoundBondNr")) {
				node.setCompoundBondNr(element.getText());
			} else if (element.getLocalName().equals("compoundBonds")) {
				node.setCompoundBonds(element.getText());
			} else if (element.getLocalName().equals("compoundSequence")) {
				node.setCompoundSequence(element.getText());
			} else if (element.getLocalName().equals("compoundModule")) {
				node.setCompoundModule(element.getText());
			} else if (element.getLocalName().equals("compoundOrganism")) {
				node.setCompoundOrganism(element.getText());
			}

			else if (element.getLocalName().equals("geneName")) {
				node.setGeneName(element.getText());
			} else if (element.getLocalName().equals("geneDefenition")) {
				node.setGeneDefinition(element.getText());
			} else if (element.getLocalName().equals("genePosition")) {
				node.setGenePosition(element.getText());
			} else if (element.getLocalName().equals("geneCodons")) {
				node.setGeneCodonUsage(element.getText());
			} else if (element.getLocalName().equals("geneAAseqNr")) {
				node.setGeneAAseqNr(element.getText());
			} else if (element.getLocalName().equals("geneAAseq")) {
				node.setGeneAAseq(element.getText());
			} else if (element.getLocalName().equals("geneNtSeqNr")) {
				node.setGeneNtseqNr(element.getText());
			} else if (element.getLocalName().equals("geneNtseq")) {
				node.setGeneNtSeq(element.getText());
			} else if (element.getLocalName().equals("geneOrthology")) {
				node.setGeneOrthology(element.getText());
			} else if (element.getLocalName().equals("geneOrthologyName")) {
				node.setGeneOrthologyName(element.getText());
			} else if (element.getLocalName().equals("geneEnzyme")) {
				node.setGeneEnzyme(element.getText());
			}

			else if (element.getLocalName().equals("glycanOrthology")) {
				node.setGlycanOrthology(element.getText());
			} else if (element.getLocalName().equals("glycanBracket")) {
				node.setGlycanBracket(element.getText());
			} else if (element.getLocalName().equals("glycanComposition")) {
				node.setGlycanComposition(element.getText());
			} else if (element.getLocalName().equals("glycanNode")) {
				node.setGlycanNode(element.getText());
			} else if (element.getLocalName().equals("glycanEdge")) {
				node.setGlycanEdge(element.getText());
			} else if (element.getLocalName().equals("glycanName")) {
				node.setGlycanName(element.getText());
			}

			else if (element.getLocalName().equals("graphicLabel")) {
				node.setNodeLabel(element.getText());
			} else if (element.getLocalName().equals("graphic_xPos")) {
				node.setXPos(new Double(element.getText()));
			} else if (element.getLocalName().equals("graphic_yPos")) {
				node.setYPos(new Double(element.getText()));
			} else if (element.getLocalName().equals("graphicShape")) {
				node.setShape(element.getText());
			} else if (element.getLocalName().equals("graphicWidth")) {
				node.setWidth(element.getText());
			} else if (element.getLocalName().equals("graphicHeight")) {
				node.setHeight(element.getText());
			} else if (element.getLocalName().equals("graphicForegroundColour")) {
				node.setForegroundColour(element.getText());
			} else if (element.getLocalName().equals("graphicBackgroundColour")) {
				node.setBackgroundColour(element.getText());
			}

			else if (element.getLocalName().equals("allDBLinks")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.addDBLink(temp.getText());
				}

			} else if (element.getLocalName().equals("allPathways")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.addPathwayLink(temp.getText());
				}

			} else if (element.getLocalName().equals("allStructures")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.addStructure(temp.getText());
				}
			} else if (element.getLocalName().equals("allMotifs")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.addGeneMotif(temp.getText());
				}
			}
		}

		return node;
	}

	private void addNetworkNode(Pathway pw, OMElement node) {

		Iterator<OMElement> it = node.getChildren();
		Integer vertexID = 0;
		try {
			vertexID = Integer
					.parseInt(node.getAttributeValue(new QName("id")));
		} catch (NumberFormatException e) {
			vertexID = Integer.parseInt(node.getAttributeValue(new QName("id"))
					.substring(1));
		}

		String biologicalElement = "";
		String label = "";
		String name = "";
		String comment = "";
		String reference = "";
		String location = "";
		Color color = null;
		Double x_coord = 0.0;
		Double y_coord = 0.0;

		Boolean isReference = false;
		BiologicalNodeAbstract bna = null;
		KEGGNode keggNode = null;
		DAWISNode dawisNode = null;

		String aaSequence = "";
		String pathway = null;

		OMElement element;

		while (it.hasNext()) {

			element = it.next();

			if (element.getLocalName().equals("pathway"))
				pathway = element.getText();
			else if (element.getLocalName().equals("coordinates")) {
				x_coord = new Double(element.getAttributeValue(new QName("x")));
				y_coord = new Double(element.getAttributeValue(new QName("y")));
			} else if (element.getLocalName().equals("elementSpecification")) {
				biologicalElement = element.getText();
			} else if (element.getLocalName().equals("colour")) {
				color = new Color(Integer.parseInt(element
						.getAttributeValue(new QName("r"))),
						Integer.parseInt(element.getAttributeValue(new QName(
								"g"))), Integer.parseInt(element
								.getAttributeValue(new QName("b"))));
			} else if (element.getLocalName().equals("label")) {
				label = element.getText();
			} else if (element.getLocalName().equals("name")) {
				name = element.getText();
			} else if (element.getLocalName().equals("reference")) {
				reference = element.getText();
			} else if (element.getLocalName().equals("comment")) {
				comment = element.getText();
			} else if (element.getLocalName().equals("location")) {
				location = element.getText();
			} else if (element.getLocalName().equals("keggProperties")) {
				keggNode = addKeggNode(element);
			} else if (element.getLocalName().equals("dawisProperties")) {
				dawisNode = addDawisNode(element);
			} else if (element.getLocalName().equals("aaSequence")) {
				aaSequence = element.getText();
			}

		}

		if (reference.equals("true")) {
			isReference = true;
		}

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

		} else if (biologicalElement
				.equals(Elementdeclerations.homodimerFormation)) {
			bna = new HomodimerFormation(label, name);

		} else if (biologicalElement.equals(Elementdeclerations.ligandBinding)) {
			bna = new LigandBinding(label, name);

		} else if (biologicalElement
				.equals(Elementdeclerations.membraneChannel)) {
			bna = new MembraneChannel(label, name);

		} else if (biologicalElement
				.equals(Elementdeclerations.membraneReceptor)) {
			bna = new Receptor(label, name);

		} else if (biologicalElement.equals(Elementdeclerations.mRNA)) {
			bna = new MRNA(label, name);
			((MRNA) bna).setNtSequence(node.getAttributeValue(new QName(
					"NtSequence")));

		} else if (biologicalElement.equals(Elementdeclerations.orthologGroup)) {
			bna = new OrthologGroup(label, name);

		} else if (biologicalElement.equals(Elementdeclerations.pathwayMap)) {
			bna = new PathwayMap(label, name);
			if (pathway != null) {
				try {
					new File("Temp").delete();
					FileWriter w = new FileWriter(new File("Temp"), false);
					w.write(pathway);
					w.close();
					File file = new File("Temp");
					Pathway newPW = new Pathway(label);
					new VAMLInput(file, newPW);
					((PathwayMap) bna).setPathwayLink(newPW);
					newPW.setParent(pw);
				} catch (Exception e1) {
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
			((SRNA) bna).setNtSequence(node.getAttributeValue(new QName(
					"NtSequence")));

		} else if (biologicalElement.equals(Elementdeclerations.smallMolecule)) {
			bna = new SmallMolecule(label, name);

		} else if (biologicalElement
				.equals(Elementdeclerations.solubleReceptor)) {
			bna = new SolubleReceptor(label, name);

		} else if (biologicalElement
				.equals(Elementdeclerations.transcriptionFactor)) {
			bna = new TranscriptionFactor(label, name);
		} else if (biologicalElement.equals(Elementdeclerations.glycan)) {
			bna = new Glycan(label, name);
		} else if (biologicalElement.equals(Elementdeclerations.collector)) {
			bna = new CollectorNode(label, name);
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
			bna = new Place(label, name, 1.0, true);
			String token = node.getAttributeValue(new QName("token"));
			String tokenMin = node.getAttributeValue(new QName("tokenMin"));
			String tokenMax = node.getAttributeValue(new QName("tokenMax"));
			String tokenStart = node.getAttributeValue(new QName("tokenStart"));
			((Place) bna).setToken(Double.parseDouble(token));
			((Place) bna).setTokenMin(Double.parseDouble(tokenMin));
			((Place) bna).setTokenMax(Double.parseDouble(tokenMax));
			((Place) bna).setTokenStart(Double.parseDouble(tokenStart));
			((Place) bna).setDiscrete(true);
		} else if (biologicalElement.equals("Continuous Place")) {
			bna = new Place(label, name, 1.0, false);
			String token = node.getAttributeValue(new QName("token"));
			String tokenMin = node.getAttributeValue(new QName("tokenMin"));
			String tokenMax = node.getAttributeValue(new QName("tokenMax"));
			String tokenStart = node.getAttributeValue(new QName("tokenStart"));
			((Place) bna).setToken(Double.parseDouble(token));
			((Place) bna).setTokenMin(Double.parseDouble(tokenMin));
			((Place) bna).setTokenMax(Double.parseDouble(tokenMax));
			((Place) bna).setTokenStart(Double.parseDouble(tokenStart));
			((Place) bna).setDiscrete(false);
		} else if (biologicalElement.equals("Discrete Transition")) {
			bna = new DiscreteTransition(label, name);
			String delay = node.getAttributeValue(new QName("delay"));
			((DiscreteTransition) bna).setDelay(Double.parseDouble(delay));
		} else if (biologicalElement.equals("Continuous Transition")) {
			bna = new ContinuousTransition(label, name);
		} else if (biologicalElement.equals("Stochastic Transition")) {
			bna = new StochasticTransition(label, name);
			((StochasticTransition) bna).setDistribution(node
					.getAttributeValue(new QName("distribution")));
		}

		bna.setCompartment(location);
		bna.setComments(comment);
		bna.setColor(color);
		bna.setIsVertex(true);
		bna.setReference(isReference);
		try {
			int id = Integer.parseInt(node.getAttributeValue(new QName(
					"ElementID")));
			bna.setID(id);
		} catch (Exception e) {
		}

		if (keggNode != null) {
			bna.setKEGGnode(keggNode);
		}

		if (dawisNode != null) {
			bna.setDAWISNode(dawisNode);
			bna.setDB(dawisNode.getDB());
		}

		mapping.put(vertexID, bna);

		if (bna instanceof Protein) {
			Protein protein = (Protein) bna;
			protein.setAaSequence(aaSequence);
		}
		// bna.setID(vertexID);
		Point2D.Double p = new Point2D.Double(x_coord, y_coord);
		// System.out.println("node_id "+bna.getID());
		pw.addVertex(bna, p);

		// pw.getGraph().moveVertex(bna.getVertex(), x_coord, y_coord);
	}

	private DAWISNode addDawisNode(OMElement dawisElement) {
		DAWISNode node = new DAWISNode(null);
		String elementID = "";
		Iterator<OMElement> it = dawisElement.getChildren();
		while (it.hasNext()) {

			OMElement element = it.next();
			if (element.getLocalName().equals("object")) {
				node.setObject(element.getText());
			} else if (element.getLocalName().equals("loaded")) {
				String loaded = element.getText();
				if (loaded.equals("true")) {
					node.setDataLoaded();
				}
			} else if (element.getLocalName().equals("id")) {
				elementID = element.getText();
				node.setID(elementID);
			} else if (element.getLocalName().equals("name")) {
				node.setName(element.getText());
			} else if (element.getLocalName().equals("db")) {
				node.setDB(element.getText());
			} else if (element.getLocalName().equals("organism")) {
				node.setOrganism(element.getText());
			} else if (element.getLocalName().equals("diagnosisType")) {
				node.setDiagnosisType(element.getText());
			} else if (element.getLocalName().equals("disorder")) {
				node.setDisorder(element.getText());
			} else if (element.getLocalName().equals("pathwayMap")) {
				node.setPathwayMap(element.getText());
			} else if (element.getLocalName().equals("ontology")) {
				node.setOntology(element.getText());
			} else if (element.getLocalName().equals("definition")) {
				node.setDefinition(element.getText());
			} else if (element.getLocalName().equals("position")) {
				node.setPosition(element.getText());
			} else if (element.getLocalName().equals("codonUsage")) {
				node.setCodonUsage(element.getText());
			} else if (element.getLocalName().equals("nucleotidSequenceLength")) {
				node.setNucleotidSequenceLength(element.getText());
			} else if (element.getLocalName().equals("nucleotidSequence")) {
				node.setNucleotidSequence(element.getText());
			} else if (element.getLocalName().equals("aminoAcidSequenceLength")) {
				node.setAminoAcidSeqLength(element.getText());
			} else if (element.getLocalName().equals("aminoAcidSequence")) {
				node.setAminoAcidSeq(element.getText());
			} else if (element.getLocalName().equals("motif")) {
				node.setMotif(element.getText());
			} else if (element.getLocalName().equals("organelle")) {
				node.setOrganelle(element.getText());
			} else if (element.getLocalName().equals("weight")) {
				node.setWeight(element.getText());
			} else if (element.getLocalName().equals("comment")) {
				node.setComment(element.getText());
			} else if (element.getLocalName().equals("equation")) {
				node.setEquation(element.getText());
			} else if (element.getLocalName().equals("rDM")) {
				node.setRDM(element.getText());
			} else if (element.getLocalName().equals("formula")) {
				node.setFormula(element.getText());
			} else if (element.getLocalName().equals("atoms")) {
				node.setAtoms(element.getText());
			} else if (element.getLocalName().equals("atomsNumber")) {
				node.setAtomsNumber(element.getText());
			} else if (element.getLocalName().equals("bonds")) {
				node.setBonds(element.getText());
			} else if (element.getLocalName().equals("bondsNumber")) {
				node.setBondsNumber(element.getText());
			} else if (element.getLocalName().equals("module")) {
				node.setModule(element.getText());
			} else if (element.getLocalName().equals("sequenceSource")) {
				node.setSequenceSource(element.getText());
			} else if (element.getLocalName().equals("remark")) {
				node.setRemark(element.getText());
			} else if (element.getLocalName().equals("composition")) {
				node.setComposition(element.getText());
			} else if (element.getLocalName().equals("node")) {
				node.setNode(element.getText());
			} else if (element.getLocalName().equals("edge")) {
				node.setEdge(element.getText());
			} else if (element.getLocalName().equals("target")) {
				node.setTarget(element.getText());
			} else if (element.getLocalName().equals("bracket")) {
				node.setBracket(element.getText());
			} else if (element.getLocalName().equals("original")) {
				node.setOriginal(element.getText());
			} else if (element.getLocalName().equals("repeat")) {
				node.setRepeat(element.getText());
			} else if (element.getLocalName().equals("activity")) {
				node.setActivity(element.getText());
			} else if (element.getLocalName().equals("type")) {
				node.setType(element.getText());
			} else if (element.getLocalName().equals("effect")) {
				node.setEffect(element.getText());
			} else if (element.getLocalName().equals("information")) {
				node.setInformation(element.getText());
			} else if (element.getLocalName().equals("isoelectricPoint")) {
				node.setIsoelectricPoint(element.getText());
			} else if (element.getLocalName().equals("isoformenNumber")) {
				node.setIsoformenNumber(element.getText());
			} else if (element.getLocalName().equals("specifityNegative")) {
				node.setSpecificityNeg(element.getText());
			} else if (element.getLocalName().equals("specifityPositiv")) {
				node.setSpecificityPos(element.getText());
			} else if (element.getLocalName().equals("factorClass")) {
				node.setFactorClass(element.getText());
			} else if (element.getLocalName().equals("encodingGene")) {
				node.setEncodingGene(element.getText());
			} else if (element.getLocalName().equals("startPoint")) {
				node.setStartPoint(element.getText());
			} else if (element.getLocalName().equals("endPoint")) {
				node.setEndPoint(element.getText());
			} else if (element.getLocalName().equals("complexName")) {
				node.setComplexName(element.getText());
			} else if (element.getLocalName().equals("allSynonyms")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setSynonym(temp.getText());
				}
			} else if (element.getLocalName().equals("allDomains")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setDomain(temp.getText());
				}
			} else if (element.getLocalName().equals("allFeatures")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setFeature(temp.getText());
				}
			} else if (element.getLocalName().equals("allLocations")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setFeature(temp.getText());
				}
			} else if (element.getLocalName().equals("allGeneNames")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setGeneName(temp.getText());
				}
			} else if (element.getLocalName().equals("allPDBs")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setPDBs(temp.getText());
				}
			} else if (element.getLocalName().equals("allAccessionnumbers")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setAccessionnumber(temp.getText());
				}
			} else if (element.getLocalName().equals("allClassifications")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setClassification(temp.getText());
				}
			} else if (element.getLocalName().equals("allSubstrates")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setSubstrates(temp.getText());
				}
			} else if (element.getLocalName().equals("allSubstrateNames")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setSubstratesName(temp.getText());
				}
			} else if (element.getLocalName().equals("allProducts")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setProducts(temp.getText());
				}
			} else if (element.getLocalName().equals("allProductNames")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setProductsName(temp.getText());
				}
			} else if (element.getLocalName().equals("allCofactors")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setCofactors(temp.getText());
				}
			} else if (element.getLocalName().equals("allCofactorNames")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setCofactorsName(temp.getText());
				}
			} else if (element.getLocalName().equals("allInhibitors")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setInhibitors(temp.getText());
				}
			} else if (element.getLocalName().equals("allInhibitorNames")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setInhibitorsName(temp.getText());
				}
			} else if (element.getLocalName().equals("allEffectors")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setEffectors(temp.getText());
				}
			} else if (element.getLocalName().equals("allEffectorNames")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setEffectorsName(temp.getText());
				}
			} else if (element.getLocalName().equals("allOrthologys")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setOrthology(temp.getText());
				}
			} else if (element.getLocalName().equals("allDBLinks")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setDBLink(temp.getText());
				}
			} else if (element.getLocalName().equals("catalysts")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setCatalysts(temp.getText());
				}
			} else if (element.getLocalName().equals("catalystNames")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setCatalystsName(temp.getText());
				}
			} else if (element.getLocalName().equals("superfamilies")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setSuperfamily(temp.getText());
				}
			} else if (element.getLocalName().equals("subfamilies")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setSubfamily(temp.getText());
				}
			} else if (element.getLocalName().equals("expressions")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setExpression(temp.getText());
				}
			} else if (element.getLocalName().equals("prozesses")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setProzess(temp.getText());
				}
			} else if (element.getLocalName().equals("functions")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setFunction(temp.getText());
				}
			} else if (element.getLocalName().equals("references")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setReference(temp.getText());
				}
			} else if (element.getLocalName().equals("motifs")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setMotif(temp.getText());
				}
			} else if (element.getLocalName().equals("methods")) {
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					node.setMethod(temp.getText());
				}
			} else if (element.getLocalName().equals("idDBRelations")) {
				String key = "";
				String value = "";
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					if (temp.getLocalName().equals("key")) {
						key = temp.getText();
					} else if (temp.getLocalName().equals("value")) {
						value = temp.getText();
						node.addIDDBRelation(key, value);
					}
				}
			} else if (element.getLocalName().equals("idIDRelations")) {
				String key = "";
				String value = "";
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					if (temp.getLocalName().equals("key")) {
						key = temp.getText();
					} else if (temp.getLocalName().equals("value")) {
						value = temp.getText();
						node.addID(key, value);
					}
				}
			} else if (element.getLocalName().equals("allCollectorElements")) {
				String id = "";
				String name = "";
				boolean hasID = false;
				Iterator<OMElement> it2 = element.getChildren();
				while (it2.hasNext()) {
					OMElement temp = it2.next();
					if (temp.getLocalName().equals("collectorElementID")) {
						if (hasID) {
							id = temp.getText();
							String[] collectorElement = { id };
							node.setCollectorElements(collectorElement);
							hasID = false;
						}
						id = temp.getText();

						hasID = true;
					} else if (temp.getLocalName().equals(
							"collectorElementName")) {
						name = temp.getText();

						String[] collectorElement = { id, name };
						node.setCollectorElements(collectorElement);
						hasID = false;
					}
				}
			}
		}
		return node;
	}

	private void getData(Pathway pw) throws FileNotFoundException,
			XMLStreamException {

		InputStream in = new FileInputStream(file);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.TRUE);
		factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);

		javax.xml.stream.XMLStreamReader reader = factory
				.createXMLStreamReader(in);

		if (pw == null) {
			pw = new CreatePathway(file.getName()).getPathway();
		}
		pw.setFilename(file);
		pw.getGraph().lockVertices();
		pw.getGraph().stopVisualizationModel();

		StAXOMBuilder builder = new StAXOMBuilder(reader);

		OMDocument doc = builder.getDocument();
		OMElement docEl = doc.getOMDocumentElement();

		OMElement modelEl = docEl.getFirstChildWithName(new QName("model"));
		if (modelEl != null) {
			OMElement annotationEL = modelEl.getFirstChildWithName(new QName(
					"annotation"));
			boolean ownProgram = false;
			if (annotationEL != null) {
				OMElement layoutsEL = annotationEL
						.getFirstChildWithName(new QName(
								"NetworkEditorSettings"));
				if (layoutsEL != null) {
					ownProgram = true;
					OMElement projectEL = layoutsEL
							.getFirstChildWithName(new QName("project"));
					if (projectEL != null) {
						addProjectDetails(pw, projectEL);
					}

					Iterator it = layoutsEL.getChildrenWithName(new QName(
							"element"));
					if (it != null) {
						while (it.hasNext()) {
							OMElement element = (OMElement) it.next();
							addNetworkNode(pw, element);
						}
					}

					it = layoutsEL.getChildrenWithName(new QName("edge"));
					if (it != null) {
						while (it.hasNext()) {
							OMElement element = (OMElement) it.next();
							addEdge(pw, element);
						}
					}
					it = layoutsEL.getChildrenWithName(new QName("rangeInfo"));
					if (it != null) {
						while (it.hasNext()) {
							OMElement element = (OMElement) it.next();
							addRange(pw, element);
						}
					}
				}
			}
		}

		Iterator it = pw.getGraph().getAllEdges().iterator();
		while (it.hasNext()) {
			BiologicalEdgeAbstract b = (BiologicalEdgeAbstract) it.next();
			// System.out.println("E: "+b.getID());
		}

		Iterator it2 = pw.getGraph().getAllVertices().iterator();
		while (it2.hasNext()) {
			BiologicalNodeAbstract b = (BiologicalNodeAbstract) it2.next();
			// System.out.println("V "+b.getID());
		}
		pw.getGraph().unlockVertices();
		pw.getGraph().restartVisualizationModel();
		MainWindowSingelton.getInstance().updateProjectProperties();
		// MainWindowSingelton.getInstance().updateOptionPanel();

	}
}
