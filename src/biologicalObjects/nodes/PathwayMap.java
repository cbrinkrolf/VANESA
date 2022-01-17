package biologicalObjects.nodes;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathwayMap extends BiologicalNodeAbstract {

	private boolean specification;
	// private String[] databases = { "KEGG", "Transpath" };

	private Pathway pathwayLink = null;

	public PathwayMap(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.pathwayMap);
		attributeSetter(this.getClass().getSimpleName(), this);
	}

//	@SuppressWarnings("unchecked")
//	public void lookUpAtAllDatabases() {
//
////		String db = getDB();
////		addID(db, getLabel());
//		
//		String dbOfInterest = "";
//		DAWISNode node = getDAWISNode();
//
//		for (int i = 0; i < databases.length; i++) {
//			if (!databases[i].equalsIgnoreCase(db)) {
//				
//				dbOfInterest = databases[i];
//
//				String query = getQuery(dbOfInterest);
//				
//				String[] det = new String [1];
//				if (dbOfInterest.equalsIgnoreCase("KEGG")){
//					det[0] = getLabel();
//				} else {
//					det[0] = getNumber(getLabel());
//				}
//				
//
//				Vector <String []> results = new Wrapper().requestDbContent(3, query, det);
//				Iterator <String []> it = results.iterator();
//				while (it.hasNext()) {
//					String[] res = it.next();
//					String id = res[0];
//					node.addID(id, getLabel());
//					node.addIDDBRelation(dbOfInterest, id);
//				}
//			}
//		}
//	}

//	@SuppressWarnings("unchecked")
//	private String getNumber(String label) {
//		String number = "";
//		String [] det = {label};
//		Vector <String []> v = new Wrapper().requestDbContent(3, DAWISQueries.getPathwayNumber, det);
//		Iterator <String []> it = v.iterator();
//		while (it.hasNext()){
//			String [] s = it.next();
//			number = s[0];
//		}
//		return number;
//	}
}
