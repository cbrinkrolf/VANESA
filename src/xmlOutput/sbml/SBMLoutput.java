
package xmlOutput.sbml;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import xmlInput.util.XMLConstraints;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * This class represents a writer from graph data to a SBML file.
 * The actual version supports SBML Level 2 Version 3 only!
 * Please read the documentation while changing the internal methods 
 * of this class.
 * 
 * @author mwesterm
 *
 */
public class SBMLoutput {

    /**
     * Maximum time to wait for a result from the adressed service.
     * Default value is 10 seconds.
     */
    public final static long MAX_CLIENT_WAIT_TIME = 1000000;
    
    // the sbml document which has to be filled
	private File file = null;
	
	// data from the graph
	private Pathway pathway = null;
	
	// location of the service
    private final EndpointReference ENDPOINT_REFERENCE = new EndpointReference(
            XMLConstraints.SBML_SERVICE_LOCATION.getXMLTag());
    
    // all necessary data for the service will be stored in here.
    // this element will be send to the sbml service.
    // It represents a sbml tag (see SBML specification).
    private OMElement payload = null;
    
    private OMElement sbmlTag = null;
    
    // the model element 
    private OMElement model = null;
    
    // the listOfCompartments
    private OMElement listOfCompartmentTypes = null;
    
    // list of compartments
    private OMElement listOfCompartments = null;
    
    // list of species types
    private OMElement listOfSpeciesType = null;
    
    // list of reactionspayload
    private OMElement listOfReactions = null;

    // list of species
    private OMElement listOfSpcies = null;
    
    // list of parameter
    private OMElement listOfParameter = null;
    
    // factory for creating new OMElements
    private OMFactory factory = null;
    
    private Hashtable<String,String> availableCompartmentTypes = null;
    
    private Hashtable<String,String> availableSpeciesTypes = null;
    
    private Hashtable<String,String> availableCompartments = null;
    
    private Hashtable<String,String> availableReactions = null;

    private int parameterCount = 0;
	/**
	 * This constructor needs a file to which the output can be written
	 * and a Pathway to get all information out of the visualized graph.
	 * 
	 * @param file  File, the new SBML file.
	 * @param pathway  Pathway, the graph information are received from 
	 * here.
	 */
	public SBMLoutput(File file, Pathway pathway) {
		this.file = file;

		this.pathway = pathway;
		this.availableCompartmentTypes = new Hashtable<String,String>();
		this.availableSpeciesTypes = new Hashtable<String,String>();
		this.availableCompartments = new Hashtable<String,String>();
		this.availableReactions = new Hashtable<String, String>();
	}
	

	/**
	 * Generates a SBML document via a web service.
	 * This may take a few seconds.
	 * @return boolean  true if a document has been written 
	 * to the specified diretory.
	 */
	public boolean generateSBMLDocument(){
		
		// initiates the settings for the service data.
		this.setUpPayload();
		
        // further data will be added to the payload here
		this.listOfCompartmentTypes = this.factory.createOMElement(new QName(
				XMLConstraints.LIST_OF_COMPARTMENT_TYPES.getXMLTag()), this.model);
		this.listOfSpeciesType = this.factory.createOMElement(new QName(
				XMLConstraints.LIST_OF_SPECIES_TYPES.getXMLTag()), this.model);
		this.listOfCompartments = this.factory.createOMElement(new QName(
				XMLConstraints.LIST_OF_COMPARTMENTS.getXMLTag()), this.model);
		this.listOfSpcies = this.factory.createOMElement(new QName(
				XMLConstraints.LIST_OF_SPECIES.getXMLTag()), this.model);
		this.listOfParameter = this.factory.createOMElement(new QName(
				XMLConstraints.LIST_OF_PARAMETERS.getXMLTag()), this.model);
		this.listOfReactions = this.factory.createOMElement(new QName(
				XMLConstraints.LIST_OF_REACTIONS.getXMLTag()), this.model);

		// read all nodes from graph
		Iterator<BiologicalNodeAbstract> it = this.pathway.getAllNodes().iterator();
		
		while (it.hasNext()) {
			BiologicalNodeAbstract oneNode = it.next();
			
			// creates all data for the service
			this.createListOfCompartmentTypes(oneNode);
			this.createListOfCompartments(oneNode);
			this.createListOfSpeciesTypes(oneNode);
			this.createListOfParameter(oneNode);
			this.createListOfSpecies(oneNode);
		}
		
		// reactions to sbml
		Iterator<BiologicalEdgeAbstract> edgeIterator = this.pathway.getAllEdges().iterator();
		
		while(edgeIterator.hasNext()){
			BiologicalEdgeAbstract oneEdge = edgeIterator.next();
			this.createListOfReactions(oneEdge);
		}
        
        // send the data to the service
		OMElement result = this.sendPayLoad();	
		// write a sbml file
		if(result != null){	
        	return this.writeSBMLDocument(result);
		}
		return false;
	}
	
	/**
	 * Some configuration work will take place here.
	 */
	private void setUpPayload(){
		
		// get a OM factory
        try {
            factory = OMAbstractFactory.getOMFactory();
        } catch (OMException ex) {
            ex.printStackTrace();
        }
        
        // create payload
        this.payload =  this.factory.createOMElement(new QName(
        		"PAYLOAD"));
        
        // creates some elements for setting up a valid SBML order
        this.sbmlTag = this.factory.createOMElement(new QName(
        		XMLConstraints.SBML.getXMLTag()),this.payload);
        
        // xmlns attribute for sbml tag
        this.sbmlTag.addAttribute(this.factory.createOMAttribute(
        		XMLConstraints.SBML_ID_XMLNS.getXMLTag(), null,
        		XMLConstraints.SBML_XMLNS_VALUE.getXMLTag()));
        
        // level attribute for sbml tag
       this.sbmlTag.addAttribute(this.factory.createOMAttribute(
    		   XMLConstraints.SBML_ID_LEVEL.getXMLTag(),
    		   null, XMLConstraints.SBML_LEVEL_VALUE.getXMLTag()));
       
       // version attribute for sbml
       this.sbmlTag.addAttribute(this.factory.createOMAttribute(
    		   XMLConstraints.SBML_ID_VERSION.getXMLTag(),
    		   null, XMLConstraints.SBML_VERSION_VALUE.getXMLTag()));
       
       // model tag is set here
        this.model = this.factory.createOMElement(new QName(
        		XMLConstraints.MODEL.getXMLTag()),this.sbmlTag);
        this.model.addAttribute(this.factory.createOMAttribute(
        		XMLConstraints.MODEL_ID.getXMLTag(),
        		null, XMLConstraints.MODEL_ID_VALUE.getXMLTag()));  
        
        // set up some additional configurations
        this.payload.setLocalName(XMLConstraints.TRANSFROM_OBJECT_TO_SBML.getXMLTag());

	}
	
	/**
	 * The graph data will be send to the SBML Service.
	 * 
	 * @return the SBML File as a OMElement
	 */
	private OMElement sendPayLoad(){
		
		//set up the service-communication-information
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
			//System.out.println("Payload: " + this.payload.toStringWithConsume());
			
            // send the sbml data to web service
            result = sender.sendReceive(this.payload);
            //System.out.println(result);
            sender.cleanup();
        }
        catch(AxisFault exception){
        	System.err.println(exception.getMessage());
        }
        
		return result;
	}

	/**writeSBMLDocument
	 * The SBML file will be written in this method.
	 * Further file handling like already existing files etc.
	 * is <b>still not implemented</b>.
	 * 
	 * @param sbmlDoc  OMElement, which contains the SBML file
	 * data.
	 */
	private boolean writeSBMLDocument(OMElement sbmlDoc){
		
        // generate a Writer for sbml file
        PrintWriter writer = null;
        try {
        	this.file.setExecutable(true);
        	this.file.setReadable(true);
        	this.file.setWritable(true);
            writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(this.file)));
            String content = null;
            try {
            	content = XMLConstraints.XML_TAG.getXMLTag() + 
            	sbmlDoc.toStringWithConsume();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
			writer.write(content);
            writer.flush();
            writer.close();
            System.err.println("Content written: " + content);
            
        } catch (IOException ex) {
        	ex.printStackTrace();
         }
        // write the sbml file
        return true;
	}
	
	/**
	 * If compartment type information are available, they will be 
	 * returned in a web service understandable manner (OMElement).
	 * 
	 * @param oneNode  one Node in the application�s graph.
	 * 
	 * Adds a OMElement, a XML tag with given exactly  to the 
	 * payload (which will be send as a package to the web service).
	 * The OMElement contains one attribute: "id". 
	 * This attribute represents a relationship between 
	 * multiple compartments. @see getListOfCompartmentTypes().
	 */
	private void createListOfCompartmentTypes(
			BiologicalNodeAbstract oneNode){
		if(oneNode != null){
			String compType = 
				oneNode.getSbml().getCompartment();
			if(compType != null){
				// if no compartment type of this name already exists
				if(!this.availableCompartmentTypes.keySet().contains(compType)){
					int compCount = (this.availableCompartmentTypes.size()+1);
					String compID = XMLConstraints.COMPARTMENT_TYPE_NAME.getXMLTag()+ compCount;
					this.availableCompartmentTypes.put(compType,compID);

					// create compartment type
					OMElement compTypeOm = this.factory.createOMElement(
							new QName(
							XMLConstraints.COMPARTMENT_TYPE.getXMLTag()), 
							this.listOfCompartmentTypes);
					
					// add id attribute
					compTypeOm.addAttribute(this.createOMAttribute(
							XMLConstraints.ID.getXMLTag(),
							compID));
				
					// add name attribute 
					compTypeOm.addAttribute(this.createOMAttribute(
							XMLConstraints.NAME.getXMLTag(),
							compType));
				
				}
			}
		}
	}
	
	private void createListOfSpeciesTypes(
			BiologicalNodeAbstract oneNode){

		if(oneNode != null){
			String speciesType = oneNode.getSbml().getBiologicalNodeDescription();
			if(speciesType != null && (!this.
					availableSpeciesTypes.keySet().contains(speciesType))){
				int speciesCount = (this.availableSpeciesTypes.size()+1);
				String id = XMLConstraints.SPECIES_ID.getXMLTag()+speciesCount;
				this.availableSpeciesTypes.put(speciesType, id);
				
				// create species type list
				OMElement species = this.factory.createOMElement(
						new QName(
						XMLConstraints.SPECIES_TYPE.getXMLTag()), 
						this.listOfSpeciesType);
				
				// add id attribute
				species.addAttribute(this.createOMAttribute(
						XMLConstraints.ID.getXMLTag(),
						id));
			
				// add name attribute 
				species.addAttribute(this.createOMAttribute(
						XMLConstraints.NAME.getXMLTag(),
						speciesType));
			}
		}
	}
	
	/**
	 * Generates service understandable compartments.
	 * 
	 * @param oneNode  one Node in the application�s graph.
	 * 
	 * Adds an OMElement, a XML tag with given exactly  to the 
	 * payload (which will be send as a package to the web service).
	 * The OMElement contains two attributes: "id" and "compartmentType".
	 */
	private void createListOfCompartments(
			BiologicalNodeAbstract oneNode){
		
		if(oneNode != null){
			// the compartment type id
			String compType = this.availableCompartmentTypes.get
			(oneNode.getSbml().getCompartment());
			
			String vertex = oneNode.getSbml().getVertex();
			if(compType != null && vertex != null && (!this.
					availableCompartments.keySet().contains(vertex))){
				int compCount = (this.availableCompartments.size()+1);
				String id = XMLConstraints.COMARTMENT_NAME.getXMLTag()+compCount;
				this.availableCompartments.put(vertex,id);
				
				// generate compartment and add it to compartment list
				OMElement compartment = this.factory.createOMElement(
						new QName(XMLConstraints.COMPARTMENT.getXMLTag()),
								this.listOfCompartments);
				
				// attribute compartment tag 
				compartment.addAttribute(this.createOMAttribute(
						XMLConstraints.COMPARTMENT_TYPE.getXMLTag(),
						compType));
				
				// attribute id
				compartment.addAttribute(this.createOMAttribute(
						XMLConstraints.ID.getXMLTag(),
						id));
				
				// add size tag, this tag is optional but will be set to 0 here
				compartment.addAttribute(this.createOMAttribute(
						XMLConstraints.COMPARTMENT_SIZE.getXMLTag(), "0"));
			}
		}
	}
	
	/**
	 * Creates service understandable list of species xml tags.
	 * 
	 * @param oneNode  one Node in the application�s graph.
	 * 
	 * Adds a OMElement, a XML tag with given exactly  to the 
	 * payload (which will be send as a package to the web service).
	 * The OMElement contains one attribute: "id". 
	 */
	private void createListOfSpecies (
			BiologicalNodeAbstract oneNode){
		if(oneNode != null){
			String vertexID = oneNode.getSbml().getVertex();
			
			if(vertexID != null){
			String compartment = this.availableCompartments.get(vertexID);
			String speciesType = this.availableSpeciesTypes.get(
					oneNode.getSbml().getBiologicalNodeDescription());
			String name = oneNode.getSbml().getLabel();
			
			// if all necessary attributes are available, set the attributes
			if(compartment != null && speciesType != null && name != null){
				// generate species and add it to compartment list
				OMElement species = this.factory.createOMElement(
						new QName(XMLConstraints.SPECIES.getXMLTag()),
								this.listOfSpcies);
				
				// attribute compartment tag 
				species.addAttribute(this.createOMAttribute(
						XMLConstraints.COMPARTMENT.getXMLTag(),
						compartment));
				
				// attribute id
				species.addAttribute(this.createOMAttribute(
						XMLConstraints.ID.getXMLTag(),
						vertexID));
				
				// attribute species type
				species.addAttribute(this.createOMAttribute(
						XMLConstraints.SPECIES_TYPE.getXMLTag(),
						speciesType));
				
				// attribute name
				species.addAttribute(this.createOMAttribute(
						XMLConstraints.NAME.getXMLTag(),
						name));
				}
			}
		}
	}
	
	/**
	 * Creates service understandable parameter type.
	 * bna
	 * @param oneEdge  one reaction in the applications graph
	 * 
	 * Adds a OMElement.
	 */
	private void createListOfParameter(BiologicalNodeAbstract oneNode){
		if(oneNode != null){
			this.parameterCount++;
			String vertexID = oneNode.getSbml().getVertex();
			
			if(vertexID != null){
				Point2D point = this.pathway.getGraph().getVertexLocation(oneNode);
			
			// parameter data 
			String name = vertexID;
			String xCoord = ""+point.getX();
			String yCoord = ""+point.getY();
			
			// create X-Parameter tag
			OMElement xParameter = this.factory.createOMElement(
					new QName(XMLConstraints.PARAMETER.getXMLTag()),this.listOfParameter);
			// set id
			String xId = XMLConstraints.PARAMETER_SID.getXMLTag() + ""+this.parameterCount;
			xParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_ID.getXMLTag(),
					xId));
			// set name
			xParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_NAME.getXMLTag(),
					name));
			// set x-coordinate
			xParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_COORDINATE.getXMLTag(),
					xCoord));
			// units
			xParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_UNIT.getXMLTag(),
					XMLConstraints.PARAMETER_EMPTY_VALUE.getXMLTag()));
			this.parameterCount++;
			
			// create Y-Parameter
			OMElement yParameter = this.factory.createOMElement(
					new QName(XMLConstraints.PARAMETER.getXMLTag()),this.listOfParameter);
			// set id
			String yId = XMLConstraints.PARAMETER_SID.getXMLTag() + ""+this.parameterCount;
			yParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_ID.getXMLTag(),
					yId));
			// set name
			yParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_NAME.getXMLTag(),
					name));
			// set y-coordinate
			yParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_COORDINATE.getXMLTag(),
					yCoord));
			// units
			yParameter.addAttribute(this.createOMAttribute(
					XMLConstraints.PARAMETER_UNIT.getXMLTag(),
					XMLConstraints.PARAMETER_EMPTY_VALUE.getXMLTag()));
			}
		}
	}
	
	/**
	 * Creates service understandable reaction type.
	 * 
	 * @param oneEdge  one reaction in the applications graph
	 * 
	 * Adds a OMElement.
	 * The OMElement contains one attribute: "id". 
	 */
	private void createListOfReactions(
			BiologicalEdgeAbstract oneEdge){
		
		if(oneEdge != null){
			String from = oneEdge.getSbml().getFrom();
			String to = oneEdge.getSbml().getTo();
			String name = oneEdge.getSbml().getEdge();
			
			if(from != null && to != null && name != null && (!this.availableReactions.contains(name))){
				String id = XMLConstraints.REACTION_NAME.getXMLTag()+(this.availableReactions.size()+1);
				this.availableReactions.put(name, id);
				// generate species and add it to compartment list
				OMElement reaction = this.factory.createOMElement(
						new QName(XMLConstraints.REACTION.getXMLTag()),
								this.listOfReactions);
				
				// attribut id for reaction
				reaction.addAttribute(this.createOMAttribute(
						XMLConstraints.ID.getXMLTag(),
						id));
				
				// generate list of reactants 
				OMElement listIfReactants = this.factory.createOMElement(new QName(
						XMLConstraints.LIST_OF_REACTANTS.getXMLTag()), reaction);
				
				// species reference for reactants 
				OMElement speciesReference1 = this.factory.createOMElement(new QName(
						XMLConstraints.SPECIES_REFERENCE.getXMLTag()), listIfReactants);
				
				// attribute species
				speciesReference1.addAttribute(this.createOMAttribute(
						XMLConstraints.SPECIES.getXMLTag(), from ));
				
				// generate list of product
				OMElement listOfProduct = this.factory.createOMElement(new QName(
						XMLConstraints.LIST_OF_PRODUCTS.getXMLTag()), reaction);
				
				// species reference for product 
				OMElement speciesReference2 = this.factory.createOMElement(new QName(
						XMLConstraints.SPECIES_REFERENCE.getXMLTag()), listOfProduct);
				
				// attribute species
				speciesReference2.addAttribute(this.createOMAttribute(
						XMLConstraints.SPECIES.getXMLTag(), to ));	
			}
		}
	}
	


	/**
	 * Creates a OMAttribute via a <code>OMFactory</code>.
	 * 
	 * @param tag  the tag name
	 * @param value  the value of the new attribute
	 * @return  OMAttribute, the attribute. Null if no attribute has 
	 * been generated.
	 */
	private OMAttribute createOMAttribute(String tag, String value){
		
		if(tag != null && value != null){
		OMAttribute attribute = this.factory.createOMAttribute( tag,
				null, value);
		return attribute;
		}	
		return null;
		
	}
}