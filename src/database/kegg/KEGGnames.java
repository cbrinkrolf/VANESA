package database.kegg;

import java.util.Iterator;
import java.util.Vector;

import configurations.Wrapper;
import database.brenda.BRENDAQueries;

public class KEGGnames {

private Wrapper wrapper = new Wrapper();
	
	public KEGGnames() {
	}
	
	public Vector getEnzymePossibilities(String pattern){
		
		Vector results = new Vector();
		String[] entry = { pattern+"%", pattern+"%"};
		
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

	public Vector getCompoundPossibilities(String pattern){
		
		Vector results = new Vector();
		String[] entry = { pattern+"%", pattern+"%"};
		
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
