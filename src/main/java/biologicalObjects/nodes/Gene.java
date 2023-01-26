package biologicalObjects.nodes;

import java.util.Vector;

import biologicalElements.Elementdeclerations;

public class Gene extends BiologicalNodeAbstract {
	private String ntSequence = "";
	private Vector<String[]> proteins = new Vector<>();
	private Vector<String[]> enzymes = new Vector<>();
	private boolean hasProteins = false;
	private boolean hasEnzymes = false;

	public Gene(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.gene);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

	public String getNtSequence() {
		return ntSequence;
	}

	public void setNtSequence(String ntSequence) {
		this.ntSequence = ntSequence;
	}

	public Vector<String[]> getProteins() {
		return proteins;
	}

	public Vector<String[]> getEnzymes() {
		return enzymes;
	}

	public boolean isHasProteins() {
		return hasProteins;
	}

	public boolean isHasEnzymes() {
		return hasEnzymes;
	}

//	public void lookUpAtAllDatabases() {
//		DAWISNode node = getDAWISNode();
//		String db = getDB();
//		String[] det = { getLabel() };
//		Vector<String[]> results;
//		Iterator<String[]> it;
//		if (db.equalsIgnoreCase("KEGG")) {
//			results = new Wrapper().requestDbContent(3, DAWISQueries.getTPGeneFromKEGGGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("Transpath", id);
//			}
//			results = new Wrapper().requestDbContent(3, DAWISQueries.getTFGeneFromKEGGGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("Transfac", id);
//			}
//		} else if (db.equalsIgnoreCase("Transpath")) {
//			results = new Wrapper().requestDbContent(3, DAWISQueries.getKEGGGeneFromTPGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("KEGG", id);
//			}
//			results = new Wrapper().requestDbContent(3, DAWISQueries.getTFGeneFromTPGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res2 = it.next();
//				String id2 = res2[0];
//				node.addID(id2, getLabel());
//				node.addIDDBRelation("Transfac", id2);
//			}
//		} else if (db.equalsIgnoreCase("Transfac")) {
//			results = new Wrapper().requestDbContent(3, DAWISQueries.getKEGGGeneFromTFGene, det);
//			it = results.iterator();
//			while (it.hasNext()) {
//				String[] res = it.next();
//				String id = res[0];
//				node.addID(id, getLabel());
//				node.addIDDBRelation("KEGG", id);
//				if (!id.equals("")) {
//					String[] det2 = { id };
//					results = new Wrapper().requestDbContent(3, DAWISQueries.getTPGeneFromKEGGGene, det2);
//					it = results.iterator();
//					while (it.hasNext()) {
//						String[] res2 = it.next();
//						String id2 = res2[0];
//						node.addID(id2, getLabel());
//						node.addIDDBRelation("Transpath", id2);
//					}
//				}
//			}
//		}
//	}

	public void addProtein(String[] proteinID) {
		if (!proteins.contains(proteinID)) {
			proteins.add(proteinID);
		}
		hasProteins = true;
	}

	public void addEnzyme(String[] enzymeID) {
		if (!enzymes.contains(enzymeID)) {
			enzymes.add(enzymeID);
		}
		hasEnzymes = true;
	}
}
