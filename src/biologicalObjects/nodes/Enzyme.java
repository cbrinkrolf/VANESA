package biologicalObjects.nodes;

import graph.jung.graphDrawing.VertexShapes;

import java.awt.Color;

import biologicalElements.Elementdeclerations;

public class Enzyme extends Protein {
	
	private String enzymeClass = "";
	private String sysName = "";
	private String reaction = "";
	private String substrate ="";
	private String produkt = "";
	private String cofactor = "";
	private String reference = "";
	private String effector = "";
	private String orthology = "";		
	
	public Enzyme(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.enzyme);
		shapes = new VertexShapes();	
		setDefaultShape(shapes.getRegularPolygon(3));
		setDefaultColor(Color.green);
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


	public String getEnzymeClass() {
		return enzymeClass;
	}


	public void setEnzymeClass(String enzymeClass) {
		this.enzymeClass = enzymeClass;
	}


	public String getOrthology() {
		return orthology;
	}


	public void setOrthology(String orthology) {
		this.orthology = orthology;
	}


	public String getProdukt() {
		return produkt;
	}


	public void setProdukt(String produkt) {
		this.produkt = produkt;
	}


	public String getReaction() {
		return reaction;
	}


	public void setReaction(String reaction) {
		this.reaction = reaction;
	}


	public String getReference() {
		return reference;
	}


	public void setReference(String reference) {
		this.reference = reference;
	}


	public String getSubstrate() {
		return substrate;
	}


	public void setSubstrate(String substrate) {
		this.substrate = substrate;
	}


	public String getSysName() {
		return sysName;
	}


	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	@Override
	public void rebuildShape(VertexShapes vs){
		//setShape(vs.getRegularPolygon(3));
	}
	
	
//	@Override
//	public void lookUpAtAllDatabases() {
//		
//		DAWISNode node = getDAWISNode();
//		String db = getDB();
//		
//		String[] det = { getLabel() };
//		Vector<String[]> results;
//		Iterator<String[]> it;
//
//		if (db.equalsIgnoreCase("KEGG")) {
//
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getTPEnzymeFromKEGGEnzyme, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("Transpath", id);
//
//			}
//		} else if (db.equalsIgnoreCase("Transpath")){
//			results = new Wrapper().requestDbContent(3,
//					DAWISQueries.getKEGGEnzymeFromTPEnzyme, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("KEGG", id);
//			}
//		}
//
//	}
	
	
	
}


