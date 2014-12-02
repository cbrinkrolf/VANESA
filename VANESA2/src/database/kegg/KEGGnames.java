package database.kegg;

import java.util.Vector;

public class KEGGnames {

	public KEGGnames() {
	}
	
	public Vector<String> getEnzymePossibilities(String pattern){
		
		Vector<String> results = new Vector<String>();
		//String[] entry = { pattern+"%", pattern+"%"};
		
		//TODO
		/*Vector v = wrapper.requestDbContent(2, BRENDAQueries.getPossibleBRENDAenzymeNames,entry);
		Iterator it = v.iterator();
		
		while(it.hasNext()){
			String[] details =(String[])it.next();
			results.add(details[0]);
			results.add(details[1]);
			
		}*/	
		return results;
	}

	public Vector<String> getCompoundPossibilities(String pattern){
		
		Vector<String> results = new Vector<String>();
		//String[] entry = { pattern+"%", pattern+"%"};
		
		//TODO
		/*Vector v = wrapper.requestDbContent(2, BRENDAQueries.getPossibleBRENDAenzymeNames,entry);
		Iterator it = v.iterator();
		
		while(it.hasNext()){
			String[] details =(String[])it.next();
			results.add(details[0]);
			results.add(details[1]);
			
		}*/	
		return results;
	}
	
	
	
}
