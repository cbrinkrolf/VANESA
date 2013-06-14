package biologicalObjects.edges;

import java.util.Iterator;
import java.util.Vector;

public class KEGGEdge {

	private String entry1 = "";
	private String entry2 = "";
	private String type = "";
	private String description = "";

	private String name = "";
	private String remark = "";
	private String orthology = "";
	private String reference = "";
	private String comment = "";
	private String definition = "";
	private String equation = "";
	private String rpair = "";
	private String effect = "";
	
	private String reactionType="";
	
	private Vector products= new Vector();
	private Vector substrates= new Vector();
	private Vector enzymes = new Vector();
	private Vector catalysts = new Vector();
	private Vector catalystsNames = new Vector();
	private Vector inhibitors = new Vector();
	private Vector inhibitorsName = new Vector();
	
	private String involvedEnzyme = "";
	
	private Vector KEEGReactionID = new Vector();
	
	public void addProduct(String name){
		products.add(name);
	}
	
	public Vector getAllProducts(){	
		return products;
	}
	
	public void setInvolvedEnzyme(String name){
		involvedEnzyme = name;
	}
	
	public String getInvolvedEnzyme(){
		return involvedEnzyme;
	}
	
	public void addEnzyme(String name){
		enzymes.add(name);
	}
	
	public Vector getAllEnzymes(){	
		return enzymes;
	}
	
	
	public void addSubstrate(String name){
		substrates.add(name);
	}
	
	public Vector getAllSubstrates(){
		
		return substrates;
	}
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEntry1() {
		return entry1;
	}
	public void setEntry1(String entry1) {
		this.entry1 = entry1;
	}
	public String getEntry2() {
		return entry2;
	}
	public void setEntry2(String entry2) {
		this.entry2 = entry2;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String getEquation() {
		return equation;
	}
	public void setEquation(String equation) {
		this.equation = equation;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrthology() {
		return orthology;
	}
	public void setOrthology(String orthology) {
		this.orthology = orthology;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRpair() {
		return rpair;
	}
	public void setRpair(String rpair) {
		this.rpair = rpair;
	}

	public String getReactionType() {
		return reactionType;
	}

	public void setReactionType(String reactionType) {
		this.reactionType = reactionType;
	}
	
    public Object[][] getKeggDeatails(){
			
			Object[][] values = {
					 {"Name",getName()},
					 {"Reaction_ID", getKEEGReactionID()},
					 {"Definition",getDefinition()},
					 {"Equation",getEquation()},
					 {"Reaction Type",getReactionType()},
					 {"Orthology",getOrthology()},
					 {"Remark",getRemark()},
					 {"Comment",getComment()},
					 {"Reference",getReference()},
					 {"Effect", getEffect()},
					 {"Catalysts", getCatalysts()+": "+getCatalystsNames()},
					 {"Inhibitors", getInhibitors()+": "+getInhibitorsName()}
					 
			};
			return values;
    }

	public String getKEEGReactionID() {
		return createString(KEEGReactionID);
	}
	
	private String createString(Vector vec) {

		String result = "";

		Iterator it = vec.iterator();

		boolean first = true;
		while (it.hasNext()) {
			if (first) {
				result = result + it.next().toString();
				first = false;
			} else {
				result = result + " ; " + it.next().toString();
			}
		}
		return result;
	}

	public void setKEEGReactionID(String reactionID) {
		if (!KEEGReactionID.contains(reactionID)){
			KEEGReactionID.add(reactionID);
		}
	}

	public void setEffect(String e) {
		this.effect = e;
	}
	
	public String getEffect() {
		return this.effect;
	}
	
	public Object getCatalysts() {
		return createString(catalysts);
	}

	public void setCatalysts(String catalyst) {
		if (!catalysts.contains(catalyst)) {
			catalysts.add(catalyst);
		}
	}

	/**
	 * get catalysts as a vector
	 * 
	 * @return catalysts
	 */
	public Vector getCatalystsAsVector() {
		return catalysts;
	}
	
	public String getCatalystsNames() {
		return createString(catalystsNames);
	}

	public void setCatalystsName(String catalystName) {
		if (!catalystsNames.contains(catalystName)) {
			catalystsNames.add(catalystName);
		}
	}

	/**
	 * get catalysts names as a vector
	 * 
	 * @return catalysts names
	 */
	public Vector getCatalystNamesAsVector() {
		return catalystsNames;
	}
	
	public void setInhibitors(String inhibitor) {
		if (!inhibitors.contains(inhibitor)) {
			inhibitors.add(inhibitor);
		}
	}

	/**
	 * get inhibitors as a vector
	 * 
	 * @return inhibitors
	 */
	public Vector getInhibitorsAsVector() {
		return inhibitors;
	}

	public String getInhibitors() {
		return createString(inhibitors);
	}

	public void setInhibitorsName(String inhibitorName) {
		if (!inhibitorsName.contains(inhibitorName)) {
			inhibitorsName.add(inhibitorName);
		}
	}

	/**
	 * get inhibitor names as a vector
	 * 
	 * @return inhibitor names
	 */
	public Vector getInhibitorNamesAsVector() {
		return inhibitorsName;
	}

	public String getInhibitorsName() {
		return createString(inhibitorsName);
	}

	
}
