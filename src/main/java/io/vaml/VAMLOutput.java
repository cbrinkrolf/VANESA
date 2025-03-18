/*
 * Created on 23.04.2004
 */
package io.vaml;

import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;

/**
 * @author sebastian and olga
 */
public class VAMLOutput {
	private final OutputStream out;
	private final Pathway pw;
	private XMLStreamWriter writer;
	private final Hashtable<String, String> speciesTypeID = new Hashtable<>();
	private final Hashtable<BiologicalNodeAbstract, String> compartments = new Hashtable<>();

	public VAMLOutput(OutputStream out, Pathway pathway) throws IOException {
		this.out = out;
		this.pw = pathway;
		try {
			write();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	private void getCompartment() throws XMLStreamException {
		writer.writeStartElement("listOfCompartments");
		int i = 1;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			writer.writeStartElement("compartment");

			writer.writeAttribute("id", "com_" + i);
			writer.writeAttribute("compartmentType", "c_1");
			writer.writeAttribute("size", "190");

			writer.writeEndElement();
			compartments.put(bna, "com_" + i);
			i++;
		}
		writer.writeEndElement();
	}

	private void getCompartmentTypes() throws XMLStreamException {
		writer.writeStartElement("listOfCompartmentTypes");
		writer.writeStartElement("compartmentType");
		writer.writeAttribute("id", "c_1");
		writer.writeAttribute("name", "Cell");
		writer.writeEndElement();
		writer.writeEndElement();
	}

	private void writeProject() throws XMLStreamException {
		writer.writeStartElement("project");

		writer.writeStartElement("isPetriNet");
		writer.writeCData(((Boolean) pw.isPetriNet()).toString());
		writer.writeEndElement();

		writer.writeStartElement("title");
		writer.writeCData(pw.getTitle());
		writer.writeEndElement();

		writer.writeStartElement("organism");
		writer.writeCData(pw.getOrganism());
		writer.writeEndElement();

		writer.writeStartElement("organismSpecification");
		writer.writeCData(pw.getOrganismSpecificationAsString());
		writer.writeEndElement();

		writer.writeStartElement("author");
		writer.writeCData(pw.getAuthor());
		writer.writeEndElement();

		writer.writeStartElement("version");
		writer.writeCData(pw.getVersion());
		writer.writeEndElement();

		writer.writeStartElement("date");
		writer.writeCData(pw.getDate());
		writer.writeEndElement();

		writer.writeStartElement("description");
		writer.writeCData(pw.getDescription());
		writer.writeEndElement();

		writer.writeEndElement();
	}

	private void writeEdges() throws XMLStreamException {
		for (BiologicalEdgeAbstract bea : pw.getAllEdges()) {
			writer.writeStartElement("edge");

			writer.writeStartElement("elementSpecification");
			writer.writeCData(bea.getBiologicalElement());
			writer.writeEndElement();

			writer.writeStartElement("id");
			writer.writeCData(bea.getID() + "");
			writer.writeEndElement();

			writer.writeStartElement("from");
			writer.writeCData(bea.getFrom().getID() + "");
			writer.writeEndElement();

			writer.writeStartElement("to");
			writer.writeCData(bea.getTo().getID() + "");
			writer.writeEndElement();

			writer.writeStartElement("label");
			writer.writeCData(bea.getLabel());
			writer.writeEndElement();

			writer.writeStartElement("name");
			writer.writeCData(bea.getName());
			writer.writeEndElement();

			writer.writeStartElement("comment");
			writer.writeCData(bea.getComments());
			writer.writeEndElement();

			writer.writeStartElement("colour");
			writer.writeAttribute("r", bea.getColor().getRed() + "");
			writer.writeAttribute("g", bea.getColor().getGreen() + "");
			writer.writeAttribute("b", bea.getColor().getBlue() + "");
			writer.writeEndElement();

			writer.writeStartElement("isDirected");
			writer.writeCData(bea.isDirected() + "");
			writer.writeEndElement();

			if (bea instanceof PNArc) {
				PNArc e = (PNArc) bea;
				writer.writeStartElement("function");
				writer.writeCData(e.getFunction());
				writer.writeEndElement();

				writer.writeStartElement("activationProbability");
				writer.writeCData(e.getProbability() + "");
				writer.writeEndElement();
			}

			writer.writeEndElement();
		}
	}

	private void writeAnnotation() throws XMLStreamException {
		writer.writeStartElement("annotation");
		writer.writeStartElement("NetworkEditorSettings");
		writeProject();
		// DefaultSettableVertexLocationFunction loc =
		// pw.getGraph().getVertexLocations();
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			writer.writeStartElement("element");
			writer.writeAttribute("id", bna.getID() + "");
			writer.writeAttribute("ElementID", Integer.toString(bna.getID()));
			// writer.writeAttribute("class", bna.getClass().getName());
			if (bna instanceof Place) {
				Place p = (Place) bna;
				writer.writeAttribute("token", p.getToken() + "");
				writer.writeAttribute("tokenMin", p.getTokenMin() + "");
				writer.writeAttribute("tokenMax", p.getTokenMax() + "");
				writer.writeAttribute("tokenStart", p.getTokenStart() + "");
				// writer.writeAttribute("isDiscrete", p.isDiscrete() + "");
			}
			if (bna instanceof Transition) {
				if (bna instanceof DiscreteTransition) {
					DiscreteTransition t = (DiscreteTransition) bna;
					writer.writeAttribute("delay", t.getDelay() + "");
				} else if (bna instanceof ContinuousTransition) {
					ContinuousTransition t = (ContinuousTransition) bna;
					writer.writeAttribute("maximalSpeed", t.getMaximalSpeed());
				} else if (bna instanceof StochasticTransition) {
					StochasticTransition t = (StochasticTransition) bna;
					writer.writeAttribute("distribution", t.getDistribution());
				}
			}
			if (bna instanceof RNA) {
				RNA rna = (RNA) bna;
				writer.writeAttribute("NtSequence", rna.getNtSequence());
			}
			if (bna instanceof PathwayMap) {
				PathwayMap map = (PathwayMap) bna;
				if (map.getPathwayLink() != null) {
					try {
						File f = new File("Temp");
						f.delete();
						new VAMLOutput(new FileOutputStream(f), map.getPathwayLink());
						String s = fileToString("Temp");
						writer.writeStartElement("pathway");
						writer.writeCData(s);
						writer.writeEndElement();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			writer.writeStartElement("coordinates");
			final Point2D p = pw.getGraph2().getNodePosition(bna);
			// .getLocation(bna);
			writer.writeAttribute("x", p.getX() + "");
			writer.writeAttribute("y", p.getY() + "");
			writer.writeEndElement();

			writer.writeStartElement("elementSpecification");
			writer.writeCData(bna.getBiologicalElement());
			writer.writeEndElement();

			writer.writeStartElement("colour");
			writer.writeAttribute("r", bna.getColor().getRed() + "");
			writer.writeAttribute("g", bna.getColor().getGreen() + "");
			writer.writeAttribute("b", bna.getColor().getBlue() + "");
			writer.writeEndElement();

			writer.writeStartElement("label");
			writer.writeCData(bna.getLabel());
			writer.writeEndElement();

			writer.writeStartElement("name");
			writer.writeCData(bna.getName());
			writer.writeEndElement();

			writer.writeStartElement("location");
			writer.writeCData(pw.getCompartmentManager().getCompartment(bna) + "");
			writer.writeEndElement();

			writer.writeStartElement("comment");
			writer.writeCData(bna.getComments());
			writer.writeEndElement();

			// save protein sequence information
			if (bna instanceof Protein) {
				Protein protein = (Protein) bna;
				writer.writeStartElement("aaSequence");
				writer.writeCData(protein.getAaSequence());
				writer.writeEndElement();
			}
			// if (bna.hasKEGGNode()) {
			// writer.writeStartElement("keggProperties");
			// writeKEGGProperties(bna.getKEGGnode());
			// writer.writeEndElement();
			// }
			writer.writeEndElement();
		}
		writeEdges();
		writeRanges();
		writer.writeEndElement();
		writer.writeEndElement();
	}

	private void writeRanges() throws XMLStreamException {
		List<Map<String, String>> annotations = pw.getGraph().getAllAnnotations();
		if (annotations != null) {
			for (Map<String, String> annotation : annotations) {
				writer.writeStartElement("rangeInfo");
				for (String key : annotation.keySet()) {
					String value = annotation.get(key);
					writer.writeAttribute(key, value);
				}
				writer.writeEndElement();
			}
		}
	}

	private void getSpeciesType() throws XMLStreamException {
		writer.writeStartElement("listOfSpeciesTypes");
		int i = 1;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!speciesTypeID.containsKey(bna.getBiologicalElement())) {
				writer.writeStartElement("speciesType");
				writer.writeAttribute("id", "s_" + i);
				writer.writeAttribute("name", bna.getBiologicalElement());
				/*
				 * if (bna instanceof Place) { Place p = (Place) bna; //
				 * writer.writeAttribute("token", p.getToken() + "");
				 * writer.writeAttribute("tokenMin", p.getTokenMin() + "");
				 * writer.writeAttribute("tokenMax", p.getTokenMax() + "");
				 * writer.writeAttribute("tokenStart", p.getTokenStart() + "");
				 * writer.writeAttribute("isDiscrete", p.isDiscrete() + ""); }
				 */
				writer.writeEndElement();
				speciesTypeID.put(bna.getBiologicalElement(), "s_" + i);
				i++;
			}
		}
		writer.writeEndElement();
	}

	private void getSpecies() throws XMLStreamException {
		writer.writeStartElement("listOfSpecies");
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			writer.writeStartElement("species");
			writer.writeAttribute("compartment", compartments.get(bna) + "");
			writer.writeAttribute("id", bna.getID() + "");
			writer.writeAttribute("speciesType", speciesTypeID.get(bna.getBiologicalElement()) + "");
			writer.writeAttribute("initialAmount", "1.0");
			writer.writeAttribute("name", bna.getName());
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

	public void write() throws XMLStreamException, IOException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(out);
		writer.writeStartDocument();
		writer.writeStartElement("sbml");
		writer.writeNamespace("xmlns", "http://www.sbml.org/sbml/level2/version3");
		writer.writeAttribute("level", "2");
		writer.writeAttribute("version", "3");
		writer.writeStartElement("model");
		writeAnnotation();
		getCompartmentTypes();
		getSpeciesType();
		getCompartment();
		getSpecies();
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		writer.close();
		out.close();
	}

	public static String fileToString(String file) {
		String result;
		File f = new File(file);
		byte[] buffer = new byte[(int) f.length()];
		try (DataInputStream in = new DataInputStream(new FileInputStream(f))) {
			in.readFully(buffer);
			result = new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException("IO problem in fileToString", e);
		}
		return result;
	}
}
