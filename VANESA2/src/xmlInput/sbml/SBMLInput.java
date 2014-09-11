package xmlInput.sbml;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

//import edu.uci.ics.jung.exceptions.ConstraintViolationException;
//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.Vertex;
import xmlInput.util.XMLConstraints;
import xmlOutput.sbml.SBMLValidator;
import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.Complex;
import biologicalObjects.nodes.CompoundNode;
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
import biologicalObjects.nodes.SBMLNode;
import biologicalObjects.nodes.SRNA;
import biologicalObjects.nodes.Site;
import biologicalObjects.nodes.SmallMolecule;
import biologicalObjects.nodes.SolubleReceptor;
import biologicalObjects.nodes.TranscriptionFactor;
import graph.CreatePathway;
import gui.MainWindowSingleton;

/**
 * To read a SBML file and put the reasults on the graph. A SBML which has been
 * passed over to an instance of this class will be validated by a web service
 * and following parsed to the vanesa graph.
 * 
 * @author mwesterm
 * 
 */
public class SBMLInput {
	/**
	 * Maximum time to wait for a result from the adressed service. Default
	 * value is 10 seconds.
	 */
	public final static long MAX_CLIENT_WAIT_TIME = 1000000;

	// data from the graph
	private Pathway pathway = null;

	// location of the service
	private final EndpointReference ENDPOINT_REFERENCE = new EndpointReference(
			XMLConstraints.SBML_SERVICE_LOCATION.getXMLTag());

	private Hashtable<String, String> availableCompartmentTypes = null;

	private Hashtable<String, String> availableSpeciesTypes = null;

	private Hashtable<String, String> availableCompartments = null;

	private Hashtable<String, BiologicalNodeAbstract> availableReactions = null;

	private Hashtable<String, String> xCoordinates = null;

	private Hashtable<String, String> yCoordinates = null;

	/**
	 * Excepts a sbml file to be loaded.
	 * 
	 * @param file
	 *            File, the sbml file
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public SBMLInput() {

	}

	/**
	 * Adresses the web sevice and exprects a objective representation of the
	 * SBML file which will be send to the service.
	 * 
	 * @return OMElement the objective representation of SBML
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	public String loadSBMLFile(File file) throws FileNotFoundException,
			XMLStreamException {
		// set up some hash tables
		this.availableCompartmentTypes = new Hashtable<String, String>();
		this.availableSpeciesTypes = new Hashtable<String, String>();
		this.availableCompartments = new Hashtable<String, String>();
		this.availableReactions = new Hashtable<String, BiologicalNodeAbstract>();
		this.xCoordinates = new Hashtable<String, String>();
		this.yCoordinates = new Hashtable<String, String>();

		// new pathway
		this.pathway = new CreatePathway().getPathway();
		this.pathway.setFilename(file);
		this.pathway.getGraph().lockVertices();
		this.pathway.getGraph().stopVisualizationModel();

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));

			String row = "";

			while ((row = br.readLine()) != null) {
				sb.append(row);
			}

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		String xml = sb.toString();
		int errors = -1;
		String msg = "";
		// System.out.println(xml);
		SBMLValidator validator = new SBMLValidator();
		try {
			errors = validator.validateSBML(xml, file.getName(), new HashMap());

			System.out.println("SBML Errors: " + errors);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Fail to connect to sbml Validator (sbml.org)");
			msg += "File could not be validated.";
		}

		OMElement validatedSBML = null;
		// get the sbml as OMElement
		OMElement potentialSBML = this.createOMElementFromSBML(file);
		validatedSBML = potentialSBML;

		// if (potentialSBML != null) {
		// validatedSBML = this.validateSBMLViaWebService(potentialSBML);
		// }
		// the sbml is correct, load it
		if (validatedSBML != null && errors < 1) {
			try {
				OMElement modelElement = validatedSBML.getFirstElement();
				if (modelElement != null) {

					// get compartment type list
					QName compartmentTypeQName = new QName(
							XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
							XMLConstraints.LIST_OF_COMPARTMENT_TYPES
									.getXMLTag());
					OMElement listOfCompartmentTypes = modelElement
							.getFirstChildWithName(compartmentTypeQName);
					if (listOfCompartmentTypes != null) {
						QName childQName = new QName(
								XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
								XMLConstraints.COMPARTMENT_TYPE.getXMLTag());
						Iterator<OMElement> iterator = listOfCompartmentTypes
								.getChildrenWithName(childQName);
						this.loadListOfCompartmentTypes(iterator);
					}

					// get species type list
					QName speciesTypeQName = new QName(
							XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
							XMLConstraints.LIST_OF_SPECIES_TYPES.getXMLTag());
					OMElement listOfSpeciesTypes = modelElement
							.getFirstChildWithName(speciesTypeQName);
					if (listOfSpeciesTypes != null) {
						QName childQName = new QName(
								XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
								XMLConstraints.SPECIES_TYPE.getXMLTag());
						Iterator<OMElement> it = listOfSpeciesTypes
								.getChildrenWithName(childQName);
						this.loadListOfSpeciesTypes(it);
					}

					// get compartment list
					QName compartmentQNamen = new QName(
							XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
							XMLConstraints.LIST_OF_COMPARTMENTS.getXMLTag());
					OMElement listOfCompartments = modelElement
							.getFirstChildWithName(compartmentQNamen);
					if (listOfCompartments != null) {
						QName childQName = new QName(
								XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
								XMLConstraints.COMPARTMENT.getXMLTag());
						Iterator<OMElement> compIterator = listOfCompartments
								.getChildrenWithName(childQName);
						this.loadListOfCompartments(compIterator);
					}

					// get Coordinates of Vertices
					QName coordinates = new QName(
							XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
							XMLConstraints.LIST_OF_PARAMETERS.getXMLTag());
					OMElement coordinateElements = modelElement
							.getFirstChildWithName(coordinates);
					if (coordinateElements != null) {
						QName childQName = new QName(
								XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
								XMLConstraints.PARAMETER.getXMLTag());
						Iterator<OMElement> paramIterator = coordinateElements
								.getChildrenWithName(childQName);
						this.loadParameterList(paramIterator);
					}

					// get species list
					QName speciesQName = new QName(
							XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
							XMLConstraints.LIST_OF_SPECIES.getXMLTag());
					OMElement listOfSpecies = modelElement
							.getFirstChildWithName(speciesQName);
					if (listOfSpecies != null) {
						QName childQName = new QName(
								XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
								XMLConstraints.SPECIES.getXMLTag());
						Iterator<OMElement> specIterator = listOfSpecies
								.getChildrenWithName(childQName);
						this.loadListOfSpecies(specIterator);
					}

					// get reactants list
					QName listOfReactionQName = new QName(
							XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
							XMLConstraints.LIST_OF_REACTIONS.getXMLTag());
					OMElement listOfReactants = modelElement
							.getFirstChildWithName(listOfReactionQName);
					if (listOfReactants != null) {
						Iterator<OMElement> iterator = listOfReactants
								.getChildren();
						;
						this.createEdge(iterator);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				msg += " File could not be loaded.";
			}
		} else {
			msg += "File contains " + errors + " error(s)!";
		}

		// refresh view
		try {
			this.pathway.getGraph().unlockVertices();
			this.pathway.getGraph().restartVisualizationModel();
			MainWindowSingleton.getInstance().updateProjectProperties();
			MainWindowSingleton.getInstance().updateOptionPanel();

		} catch (Exception ex) {
			System.err.println(" Exception in class SBMLInput.loadSBML() "
					+ "Jung.Graph.ConstraintVialation: " + ex.getMessage());
		}
		System.out.println(msg);
		return msg;
	}

	/**
	 * Reads a sbml file and returns a OMElement representation of the given
	 * SBML file.
	 * 
	 * @param file
	 *            File, the SBML file
	 * @return OMElement, the OMElement representation of SBML file.
	 */
	private OMElement createOMElementFromSBML(File file) {

		// create a buffered reader
		BufferedReader bufferedReader = null;

		// create a xml reader
		XMLStreamReader reader = null;
		OMElement sbmlOMElement = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			reader = XMLInputFactory.newInstance().createXMLStreamReader(
					bufferedReader);
			
			// create a OMElement from SBML file
			StAXOMBuilder axiomBuilder = new StAXOMBuilder(reader);
			sbmlOMElement = axiomBuilder.getDocumentElement();
			//String content = sbmlOMElement.toString();
			
			// clean up
			bufferedReader.close();
			reader.close();
			return sbmlOMElement;
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sbmlOMElement;
	}

	/**
	 * Validates the SBML file via a web service
	 * 
	 * @param sbmlOMElement
	 *            OMElement, the sbml OM representation
	 * @return the validated sbml OM representation, null if the validation has
	 *         not been successfull.
	 */
	private OMElement validateSBMLViaWebService(OMElement sbmlOMElement) {
		if (sbmlOMElement != null) {

			// get a OM factory
			OMFactory factory = null;
			try {
				factory = OMAbstractFactory.getOMFactory();
			} catch (OMException ex) {
				ex.printStackTrace();
			}

			OMElement payload = factory.createOMElement(new QName(
					XMLConstraints.TRANSFROM_OBJECT_TO_SBML.getXMLTag()));

			payload.addChild(sbmlOMElement);

			// communication data
			Options options = new Options();
			options.setTo(ENDPOINT_REFERENCE);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setTimeOutInMilliSeconds(MAX_CLIENT_WAIT_TIME);

			OMElement result = null;
			try {
				// communicate with service
				ServiceClient sender = new ServiceClient();
				sender.setOptions(options);

				// get result from service
				// send the sbml data to web service
				System.out.println("drin");
				result = sender.sendReceive(payload);
				System.out.println("drin");
				sender.cleanup();
				return result;

			} catch (AxisFault exception) {
				System.err.println(exception.getMessage());
			}
			return null;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * Loads the component types.
	 * 
	 * @param iterator
	 *            Iterator<OMElements>, the iterator over all component types.
	 * @throws XMLStreamException
	 */
	private void loadParameterList(Iterator<OMElement> iterator) {
		String tmpName = null;

		while (iterator.hasNext()) {
			OMElement oneCoordinate = iterator.next();
			QName nameQName = new QName(
					XMLConstraints.PARAMETER_NAME.getXMLTag());
			QName valueQName = new QName(
					XMLConstraints.PARAMETER_COORDINATE.getXMLTag());
			// get the vertex id ofthis parameter
			String newName = oneCoordinate.getAttributeValue(nameQName);
			// if there is already the x coordinate available
			if (tmpName != null && newName.equals(tmpName)) {
				String yCoordinate = oneCoordinate
						.getAttributeValue(valueQName);
				this.yCoordinates.put(newName, yCoordinate);
			}
			// a new coordinate found
			else {
				String xCoordinate = oneCoordinate
						.getAttributeValue(valueQName);
				this.xCoordinates.put(newName, xCoordinate);
				// set a new xcoord
				tmpName = newName;
			}
		}

	}

	/**
	 * Loads the component types.
	 * 
	 * @param iterator
	 *            Iterator<OMElements>, the iterator over all component types.
	 * @throws XMLStreamException
	 */
	private void loadListOfCompartmentTypes(Iterator<OMElement> iterator)
			throws XMLStreamException {
		String compTypeValue = null;
		String name = null;
		while (iterator.hasNext()) {

			OMElement compartmentType = iterator.next();
			compTypeValue = compartmentType.getAttributeValue(new QName(
					XMLConstraints.ID.getXMLTag()));
			name = compartmentType.getAttributeValue(new QName(
					XMLConstraints.NAME.getXMLTag()));
			this.availableCompartmentTypes.put(compTypeValue, name);
		}
	}

	/**
	 * Loads all species types in a given SBML file.
	 * 
	 * @param iterator
	 *            Iterator<OMElement> , the iterator over all species types.
	 */
	private void loadListOfSpeciesTypes(Iterator<OMElement> iterator) {
		String name = null;
		String id = null;

		while (iterator.hasNext()) {
			OMElement oneSpeciesType = iterator.next();
			id = oneSpeciesType.getAttributeValue(new QName(XMLConstraints.ID
					.getXMLTag()));
			name = oneSpeciesType.getAttributeValue(new QName(
					XMLConstraints.NAME.getXMLTag()));
			this.availableSpeciesTypes.put(id, name);
		}
	}

	/**
	 * Loads all compartments out of a given SBML
	 * 
	 * @param iterator
	 *            Iterator<OMElement>
	 */
	private void loadListOfCompartments(Iterator<OMElement> iterator) {
		String compTyp = null;
		String id = null;

		while (iterator.hasNext()) {
			OMElement oneSpeciesType = iterator.next();
			compTyp = oneSpeciesType.getAttributeValue(new QName(
					XMLConstraints.COMPARTMENT_TYPE.getXMLTag()));
			id = oneSpeciesType.getAttributeValue(new QName(XMLConstraints.ID
					.getXMLTag()));
			this.availableCompartments.put(id, compTyp);
		}
	}

	/**
	 * Loads all species out of a given SBML
	 * 
	 * @param iterator
	 *            Iterator<OMElement>
	 */
	private void loadListOfSpecies(Iterator<OMElement> iterator) {

		String compartment = null;
		String id = null;
		String speciesType = null;
		String name = null;

		while (iterator.hasNext()) {
			OMElement oneSpecies = iterator.next();
			id = oneSpecies.getAttributeValue(new QName(XMLConstraints.ID
					.getXMLTag()));
			String speciesTypeId = oneSpecies.getAttributeValue(new QName(
					XMLConstraints.SPECIES_TYPE.getXMLTag()));
			speciesType = this.availableSpeciesTypes.get(speciesTypeId);
			name = oneSpecies.getAttributeValue(new QName(XMLConstraints.NAME
					.getXMLTag()));

			// fill vertex with data
			SBMLNode sbmlNode = new SBMLNode();
			sbmlNode.setVertex(id);
			sbmlNode.setBiologicalNodeDescription(speciesType);
			sbmlNode.setLabel(name);
			sbmlNode.setShape(null);

			// add this vertex to the pathway
			BiologicalNodeAbstract node = this.createVertex(speciesType, name,
					id);

			// add node to pathway
			if (node != null) {
				double xCoord = 0;
				double yCoord = 0;

				// put node on right possition
				// Vertex ver = node.getVertex();
				xCoord = Double.parseDouble(this.xCoordinates.get(id));
				yCoord = Double.parseDouble(this.yCoordinates.get(id));
				this.pathway
						.addVertex(node, new Point2D.Double(xCoord, yCoord));
				// this.pathway.getGraph().moveVertex(node.getVertex(), xCoord,
				// yCoord);
			}
		}
	}

	/**
	 * Loads all edges to the graph
	 * 
	 * @param iterator
	 *            Iterator<OMElement>
	 */
	private void createEdge(Iterator<OMElement> iterator) {

		String name = null;
		String label = null;
		String from = null;
		String to = null;
		String directed = null;

		while (iterator.hasNext()) {
			OMElement oneReaction = iterator.next();

			// get the internal information
			QName reactantQName = new QName(
					XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
					XMLConstraints.LIST_OF_REACTANTS.getXMLTag());
			QName productQName = new QName(
					XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
					XMLConstraints.LIST_OF_PRODUCTS.getXMLTag());
			QName speciesReferenceQName = new QName(
					XMLConstraints.SBML_XMLNS_VALUE.getXMLTag(),
					XMLConstraints.SPECIES_REFERENCE.getXMLTag());
			QName speciesQName = new QName(XMLConstraints.SPECIES.getXMLTag());

			// the elements where all necessary information for sbml is set
			OMElement reactant = oneReaction
					.getFirstChildWithName(reactantQName);
			OMElement product = oneReaction.getFirstChildWithName(productQName);
			OMElement omFrom = reactant
					.getFirstChildWithName(speciesReferenceQName);
			OMElement omTo = product
					.getFirstChildWithName(speciesReferenceQName);
			from = omFrom.getAttributeValue(speciesQName);
			to = omTo.getAttributeValue(speciesQName);

			BiologicalNodeAbstract ver1 = this.availableReactions.get(from);
			BiologicalNodeAbstract ver2 = this.availableReactions.get(to);

			ReactionEdge edge = new ReactionEdge("", "",
					this.availableReactions.get(from),
					this.availableReactions.get(to));
			// ReactionEdge graphElement = new ReactionEdge(edge,);
			this.pathway.addEdge(edge);
		}
	}

	/**
	 * Takes care of different biological nodes. This method decides which kind
	 * of biological node will be created. @see
	 * <code>BiologicalNodeAbstract</node>.
	 * 
	 * @param biologicalElement
	 *            String, the kind of biological node
	 * @param label
	 *            String, the labe of the new vertex
	 * @param id
	 *            String, the unique id of this vertex
	 * @return biologicalNode, the created node. Null if no node has been
	 *         created
	 */
	private BiologicalNodeAbstract createVertex(String biologicalElement,
			String label, String id) {
		BiologicalNodeAbstract biologicalNode = null;

		if (biologicalElement.equals(Elementdeclerations.enzyme)) {
			Enzyme e = new Enzyme(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.others)) {
			Other e = new Other(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.complex)) {
			Complex e = new Complex(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.degraded)) {
			Degraded e = new Degraded(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.dna)) {
			DNA e = new DNA(label, "");
			biologicalNode = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.homodimerFormation)) {
			HomodimerFormation e = new HomodimerFormation(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.ligandBinding)) {
			LigandBinding e = new LigandBinding(label, "");
			biologicalNode = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.membraneChannel)) {
			MembraneChannel e = new MembraneChannel(label, "");
			biologicalNode = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.membraneReceptor)) {
			Receptor e = new Receptor(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.mRNA)) {
			MRNA e = new MRNA(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.orthologGroup)) {
			OrthologGroup e = new OrthologGroup(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.pathwayMap)) {
			PathwayMap e = new PathwayMap(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.protein)) {
			Protein e = new Protein(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.receptor)) {
			Receptor e = new Receptor(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.sRNA)) {
			SRNA e = new SRNA(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.smallMolecule)) {
			SmallMolecule e = new SmallMolecule(label, "");
			biologicalNode = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.solubleReceptor)) {
			SolubleReceptor e = new SolubleReceptor(label, "");
			biologicalNode = e;

		} else if (biologicalElement
				.equals(Elementdeclerations.transcriptionFactor)) {
			TranscriptionFactor e = new TranscriptionFactor(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.glycan)) {
			Glycan e = new Glycan(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.collector)) {
			CollectorNode e = new CollectorNode(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.compound)) {
			CompoundNode e = new CompoundNode(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.disease)) {
			Disease e = new Disease(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.drug)) {
			Drug e = new Drug(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.gene)) {
			Gene e = new Gene(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.go)) {
			GeneOntology e = new GeneOntology(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.reaction)) {
			Reaction e = new Reaction(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.matrix)) {
			Matrix e = new Matrix(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.factor)) {
			Factor e = new Factor(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.fragment)) {
			Fragment e = new Fragment(label, "");
			biologicalNode = e;

		} else if (biologicalElement.equals(Elementdeclerations.site)) {
			Site e = new Site(label, "");
			biologicalNode = e;
		}

		if (biologicalNode != null) {
			//BiologicalNodeAbstract bna = (BiologicalNodeAbstract) biologicalNode;
			this.availableReactions.put(id, biologicalNode);
			//return bna;
		}
		return biologicalNode;
	}
}
