package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;

public class Enzyme extends Protein implements DynamicNode {

	private String maximumSpeed = "1";
	private boolean knockedOut = false;

	public Enzyme(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.enzyme);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	@Override
	public String getMaximumSpeed() {
		return this.maximumSpeed;
	}

	@Override
	public void setMaximumSpeed(String maximumSpeed) {
		this.maximumSpeed = maximumSpeed;
		
	}

	@Override
	public boolean isKnockedOut() {
		return this.knockedOut;
	}

	@Override
	public void setKnockedOut(Boolean knockedOut) {
		this.knockedOut = knockedOut;
	}

	// @Override
	// public void lookUpAtAllDatabases() {
	//
	// DAWISNode node = getDAWISNode();
	// String db = getDB();
	//
	// String[] det = { getLabel() };
	// Vector<String[]> results;
	// Iterator<String[]> it;
	//
	// if (db.equalsIgnoreCase("KEGG")) {
	//
	// results = new Wrapper().requestDbContent(3,
	// DAWISQueries.getTPEnzymeFromKEGGEnzyme, det);
	// it = results.iterator();
	// while (it.hasNext()) {
	// String[] res = it.next();
	// String id = res[0];
	// node.addID(id, getLabel());
	// node.addIDDBRelation("Transpath", id);
	//
	// }
	// } else if (db.equalsIgnoreCase("Transpath")){
	// results = new Wrapper().requestDbContent(3,
	// DAWISQueries.getKEGGEnzymeFromTPEnzyme, det);
	// it = results.iterator();
	// while (it.hasNext()) {
	// String[] res = it.next();
	// String id = res[0];
	// node.addID(id, getLabel());
	// node.addIDDBRelation("KEGG", id);
	// }
	// }
	//
	// }

}
