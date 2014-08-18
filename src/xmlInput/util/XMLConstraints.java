package xmlInput.util;

/**
 * This enumeration class holds all necessary string constants 
 * for XML processing like generate a SBML or retrieve SBML data.
 * 
 * This enumeration contains all tags and constraints for the SBML 
 * version <b>2.3</b>
 * @author mwesterm
 *
 */
public enum XMLConstraints {

	SBML("sbml"),
	SBML_ID_XMLNS("xmlns"),
	SBML_XMLNS_VALUE("http://www.sbml.org/sbml/level2/version3"),
	SBML_ID_LEVEL("level"),
	SBML_LEVEL_VALUE("2"),
	SBML_ID_VERSION("version"),
	SBML_VERSION_VALUE("3"),
	SMBL_ID_COMP("xmlns:comp"),
	SMBL_COMP_VALUE("http://www.sbml.org/sbml/level3/version1/comp/version1"),
	SMBL_COMP_REQUIRED("comp:required"),
	
	MODEL("model"),
	MODEL_ID("id"),
	MODEL_ID_VALUE("VanesaModel"),
	
	LIST_OF_SUBMODELS("comp:listOfSubmodels"),
	SUBMODEL("comp:submodel"),
	COMP_ID("comp:id"),
	COMP_ID_REF("comp_idRef"),
	MODEL_REF("comp:modelRef"),
	
	LIST_OF_MODEL_DEFINITIONS("comp:listOfModelDefinitions"),
	MODEL_DEFINITION("comp:modelDefinition"),
	
	LIST_OF_EXTERNAL_MODEL_DEFINITIONS("comp:listOfExternalModelDefinitions"),
	EXTERNAL_MODEL_DEFINITION("comp:externalModelDefinition"),
	
	LIST_OF_REPLACED_ELEMENTS("comp:listOfReplacedElements"),
	REPLACED_ELEMENT("comp:replacedElement"),
	REPLACED_BY("comp:replacedBy"),
	
	LIST_OF_DELETIONS("comp:listOfDeletions"),
	DELETION("comp:deletion"),
	
	LIST_OF_PORTS("comp:listOfPorts"),
	PORT("comp:port"),

	
	PORT_REF("comp:portRef"),
	SUBMODEL_REF("comp:submodelRef"),	
	
	LIST_OF_FUNCION_DEFINITIONS("listOfFunctionDefinitions"),
	FUNCTION_DEFINITION("functionDefinition"),
	
	LIST_OF_UNIT_DEFINITIONS("listOfUnitDefinitions"),
	UNIT_DEFINITION("unitDefinition"),
	
	LIST_OF_COMPARTMENT_TYPES("listOfCompartmentTypes"),
	COMPARTMENT_TYPE("compartmentType"),
	
	LIST_OF_SPECIES_TYPES("listOfSpeciesTypes"),
	SPECIES_TYPE("speciesType"),
	
	LIST_OF_COMPARTMENTS("listOfCompartments"),
	COMPARTMENT("compartment"),
	COMPARTMENT_SIZE("size"),
	
	LIST_OF_SPECIES("listOfSpecies"),
	SPECIES("species"),
	
	
	LIST_OF_PARAMETERS("listOfParameters"),
	PARAMETER_SID("paramSiD"),
	PARAMETER("parameter"),
	PARAMETER_ID("id"),
	PARAMETER_NAME("name"),
	PARAMETER_COORDINATE("value"),
	PARAMETER_UNIT("units"),
	PARAMETER_EMPTY_VALUE("second"),

	
	LIST_OF_INITIAL_ASSIGNMENTS("listOfInitialAssignments"),
	INITIAL_ASSIGNMENT("initialAssignment"),
	
	LIST_OF_RULES("listOfRules"),
	RULE(""),
	
	LIST_OF_CONSTRAINTS("listOfConstraints"),
	CONSTRAINT("constraint"),
	
	LIST_OF_REACTIONS("listOfReactions"),
	REACTION("reaction"),
	
	LIST_OF_EVENTS("listOfEvents"),
	EVENT("event"),
	
	NAME("name"),
	COMPARTMENT_TYPE_NAME("c_"),
	COMARTMENT_NAME("com_"),
	REACTION_NAME("react_"),
	SPECIES_ID("s_"),
	ID("id"),
	
	SPECIES_REFERENCE("speciesReference"),
	LIST_OF_REACTANTS("listOfReactants"),
	LIST_OF_PRODUCTS("listOfProducts"),
	
	TRANSFROM_OBJECT_TO_SBML("validateSBML"),
	SBML_SERVICE_LOCATION("http://tunicata.techfak.uni-bielefeld.de/axis2/services/SBMLGenerator"),
	XML_TAG("<?xml version=\"1.0\" encoding =\"UTF-8\"?>");
	
	// data
	private final String xmlTag;
	
	/**
	 * Needs a xml tag as parameter.
	 * 
	 * @param tag  String, the xml tag for the specified 
	 * XML constraint. (see also the SBML specification on
	 * <b>www.sbml.org/Documents/Specifications/</b>).
	 */
	XMLConstraints(String tag){
		this.xmlTag = tag;
	}
	
	/**
	 * Returns the xmlTag of a enumeration. This tag can
	 * be used to write a xml.
	 * 
	 * @return String  the xmlTag of the given enum.
	 */
	public String getXMLTag(){
		return xmlTag;
	}
}
