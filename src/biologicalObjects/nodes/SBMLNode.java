package biologicalObjects.nodes;

public class SBMLNode {
	
	private String name, label, vertex;
	private String biologicalNodeDescription;
	private String shape;
	private String nucleotideSequence;
	private String abstractNode = "false";
	private String compartment = "Inner Cell";
	
	public String getCompartment() {
		return compartment;
	}
	
	
	public void setCompartment(String compartment) {
		this.compartment = compartment;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getVertex() {
		return vertex;
	}
	public void setVertex(String vertex) {
		this.vertex = vertex;
	}
	public String getBiologicalNodeDescription() {
		return biologicalNodeDescription;
	}
	public void setBiologicalNodeDescription(String biologicalNodeDescription) {
		this.biologicalNodeDescription = biologicalNodeDescription;
	}
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getNucleotideSequence() {
		return nucleotideSequence;
	}
	public void setNucleotideSequence(String nucleotideSequence) {
		this.nucleotideSequence = nucleotideSequence;
	}
	public String getAbstractNode() {
		return abstractNode;
	}
	public void setAbstractNode(String abstractNode) {
		this.abstractNode = abstractNode;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getMass() {
		return mass;
	}
	public void setMass(String mass) {
		this.mass = mass;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getAaSequence() {
		return aaSequence;
	}
	public void setAaSequence(String aaSequence) {
		this.aaSequence = aaSequence;
	}
	public String getUniprotProtein() {
		return uniprotProtein;
	}
	public void setUniprotProtein(String uniprotProtein) {
		this.uniprotProtein = uniprotProtein;
	}
	public String getEnzymeClass() {
		return enzymeClass;
	}
	public void setEnzymeClass(String enzymeClass) {
		this.enzymeClass = enzymeClass;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	public String getReaction() {
		return reaction;
	}
	public void setReaction(String reaction) {
		this.reaction = reaction;
	}
	public String getSubstrate() {
		return substrate;
	}
	public void setSubstrate(String substrate) {
		this.substrate = substrate;
	}
	public String getProdukt() {
		return produkt;
	}
	public void setProdukt(String produkt) {
		this.produkt = produkt;
	}
	public String getCofactor() {
		return cofactor;
	}
	public void setCofactor(String cofactor) {
		this.cofactor = cofactor;
	}
	public String getEffector() {
		return effector;
	}
	public void setEffector(String effector) {
		this.effector = effector;
	}
	public String getOrthology() {
		return orthology;
	}
	public void setOrthology(String orthology) {
		this.orthology = orthology;
	}
	private String formula;
	private String mass;
	private String colour;
	private String reference = "false";
	private String aaSequence;
	private String uniprotProtein = "";
	private String enzymeClass = "";
	private String sysName = "";
	private String reaction = "";
	private String substrate ="";
	private String produkt = "";
	private String cofactor = "";
	private String effector = "";
	private String orthology = "";	

}
