/*
 * Created on 23.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package xmlOutput.sbml;

import gui.RangeSelector;

import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
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
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class VAMLoutput {

	private OutputStream out = null;
	private Pathway pw = null;
	private XMLStreamWriter writer;
	private Hashtable<String, String> speciesTypeID = new Hashtable<String, String>();
	private Hashtable<BiologicalNodeAbstract, String> compartments = new Hashtable<BiologicalNodeAbstract, String>();

	public VAMLoutput(OutputStream out, Pathway pathway) throws IOException {

		this.out = out;
		this.pw = pathway;

		try {
			write();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getCompartment() throws XMLStreamException {

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		writer.writeStartElement("listOfCompartments");
		int i = 1;
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {

			bna = it.next();
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
		writer.writeCData(pw.getSpecificationAsString());
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

		Iterator<BiologicalEdgeAbstract> it = pw.getAllEdges().iterator();

		BiologicalEdgeAbstract bea;

		while (it.hasNext()) {
			bea = it.next();
			// System.out.println("Eo: "+bea.getID());
			// System.out.println(bea.getBiologicalElement());
			// System.out.println("edge zum speichern " + bna);

			writer.writeStartElement("edge");

			writer.writeStartElement("elementSpecification");
			writer.writeCData(bea.getBiologicalElement());
			// System.out.println("elementSpecification "
			// + bna.getBiologicalElement());
			writer.writeEndElement();

			writer.writeStartElement("id");
			writer.writeCData(bea.getID() + "");
			writer.writeEndElement();

			writer.writeStartElement("from");
			writer.writeCData(bea.getFrom().getID() + "");
			writer.writeEndElement();

			writer.writeStartElement("to");
			writer.writeCData(bea.getTo().getID() + "");
			// System.out.println("to "
			// + bna.getEdge().getEndpoints().getSecond().toString());
			writer.writeEndElement();

			writer.writeStartElement("label");
			writer.writeCData(bea.getLabel());
			// System.out.println("label " + bna.getLabel());
			writer.writeEndElement();

			writer.writeStartElement("name");
			writer.writeCData(bea.getName());
			// System.out.println("name " + bna.getName());
			writer.writeEndElement();

			writer.writeStartElement("comment");
			writer.writeCData(bea.getComments());
			// System.out.println("comments " + bna.getComments());
			writer.writeEndElement();

			writer.writeStartElement("colour");
			writer.writeAttribute("r", bea.getColor().getRed() + "");
			writer.writeAttribute("g", bea.getColor().getGreen() + "");
			writer.writeAttribute("b", bea.getColor().getBlue() + "");
			// System.out.println("color: " + bna.getColor().getRed() + ", "
			// + bna.getColor().getBlue() + ", "
			// + bna.getColor().getGreen());
			writer.writeEndElement();

			writer.writeStartElement("isDirected");
			writer.writeCData(bea.isDirected() + "");
			// System.out.println("isDirected " + bna.isDirected());
			writer.writeEndElement();

			if (bea instanceof PNEdge) {
				PNEdge e = (PNEdge) bea;
				writer.writeStartElement("function");
				writer.writeCData(e.getFunction());
				writer.writeEndElement();

				writer.writeStartElement("activationProbability");
				writer.writeCData(e.getActivationProbability() + "");
				writer.writeEndElement();
			}

			writer.writeEndElement();
		}
	}

	private void writeAnnotation() throws XMLStreamException {

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		writer.writeStartElement("annotation");
		writer.writeStartElement("NetworkEditorSettings");

		writeProject();

		// DefaultSettableVertexLocationFunction loc =
		// pw.getGraph().getVertexLocations();

		BiologicalNodeAbstract bna;
		while (it.hasNext()) {

			bna = it.next();

			writer.writeStartElement("element");
			writer.writeAttribute("id", bna.getID() + "");
			writer.writeAttribute("ElementID", Integer.toString(bna.getID()));
			// writer.writeAttribute("class", bna.getClass().getName());
			// System.out.println(bna.getClass().getName().);
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
					writer.writeAttribute("maximumSpeed", t.getMaximumSpeed());
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
						new File("Temp").delete();
						new VAMLoutput(new FileOutputStream(new File("Temp")), map.getPathwayLink());
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
			Point2D p = pw.getGraph().getVertexLocation(bna);
			// .getLocation(bna);
			// System.out.println(pw.getGraph().getVertexLocation(bna));
			// System.out.println(bna.getID()+" "+p);
			writer.writeAttribute("x", p.getX() + "");
			writer.writeAttribute("y", p.getY() + "");
			writer.writeEndElement();

			writer.writeStartElement("elementSpecification");
			// System.out.println(bna.getBiologicalElement());
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
			writer.writeCData(bna.getCompartment() + "");
			writer.writeEndElement();

			writer.writeStartElement("comment");
			writer.writeCData(bna.getComments());
			writer.writeEndElement();

			// save protein sequence informations
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
		List<Map<String, String>> rangeInfos = RangeSelector.getInstance()
				.getRangesInMyGraph(pw.getGraph());
		if (rangeInfos != null) {
			for (Map<String, String> range : rangeInfos) {
				writer.writeStartElement("rangeInfo");
				for (String key : range.keySet()) {
					String value = range.get(key);
					writer.writeAttribute(key, value);
				}
				writer.writeEndElement();
			}
		}
	}

	private void getSpeciesType() throws XMLStreamException {

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		writer.writeStartElement("listOfSpeciesTypes");
		int i = 1;
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {

			bna = it.next();

			if (!speciesTypeID.containsKey(bna.getBiologicalElement())) {

				writer.writeStartElement("speciesType");
				writer.writeAttribute("id", "s_" + i);
				writer.writeAttribute("name", bna.getBiologicalElement());
				/*
				 * if (bna instanceof Place) { Place p = (Place) bna; //
				 * System.out.println("ist nen place");
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

		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		writer.writeStartElement("listOfSpecies");

		BiologicalNodeAbstract bna;
		while (it.hasNext()) {

			bna = it.next();
			// System.out.println("Vo: "+bna.getID());
			writer.writeStartElement("species");

			writer.writeAttribute("compartment", compartments.get(bna) + "");
			writer.writeAttribute("id", bna.getID() + "");
			writer.writeAttribute("speciesType",
					speciesTypeID.get(bna.getBiologicalElement()) + "");
			writer.writeAttribute("initialAmount", "1.0");
			writer.writeAttribute("name", bna.getName());
			writer.writeEndElement();

		}
		writer.writeEndElement();
	}

	public void write() throws XMLStreamException, IOException {

		//OutputStream out = new FileOutputStream(file);

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		writer = factory.createXMLStreamWriter(out);

		writer.writeStartDocument();
		writer.writeStartElement("sbml");
		writer.writeNamespace("xmlns",
				"http://www.sbml.org/sbml/level2/version3");
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
		String result = null;
		DataInputStream in = null;
		try {
			File f = new File(file);
			byte[] buffer = new byte[(int) f.length()];
			in = new DataInputStream(new FileInputStream(f));
			in.readFully(buffer);
			result = new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException("IO problem in fileToString", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

}
