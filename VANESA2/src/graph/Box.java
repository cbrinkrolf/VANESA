package graph;

import java.util.Iterator;
import java.util.Vector;

public class Box {
	
	Vector v = new Vector();
	
	public Box(){
		
	}
	
	public boolean doEntriesExist(String entry1, String entry2){
		
		Iterator i = v.iterator();
		while (i.hasNext()){
			String[] details = (String[])i.next();
			if(details[0].equals(entry1)){
				if(details[1].equals(entry2)){
					return true;
				}
			}else if(details[0].equals(entry2)){
				if(details[1].equals(entry1)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void addElements(String entry1, String entry2){
		String[] relation = new String[2];
		relation[0]=entry1;
		relation[1]=entry2;
		v.add(relation);
	}

}
