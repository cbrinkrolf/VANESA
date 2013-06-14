package database.brenda;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class MoleculeBox {

	private Hashtable molecules = new Hashtable();
	private Vector v = new Vector();
	
	public MoleculeBox() {

	}

	public void fillTable(Vector v) {
		Iterator it = v.iterator();
		while (it.hasNext()) {
			MoleculesPair p = (MoleculesPair) it.next();
			molecules.put(p.getName().trim(), p);
		}
	}

	public void clear() {
		molecules.clear();
		v.clear();
	}

	public Vector getAllValues() {
		Vector v = new Vector();
		Enumeration e = molecules.elements();
		while (e.hasMoreElements()) {
			v.add(e.nextElement());
		}
		Collections.sort(v);
		return v;
	}

	public Vector getDisregardedValues() {

		Vector results = new Vector();
		Iterator it = molecules.values().iterator();
		while (it.hasNext()) {
			MoleculesPair p = (MoleculesPair) it.next();
			if (p.isDisregard())
				results.add(p.getName());
		}
		v=results;
		return results;
	}


	public void changeValues(String name, boolean disregard) {
		((MoleculesPair) molecules.get(name)).setDisregard(disregard);
	}

	public void printElements() {
		Enumeration e = molecules.elements();
		while (e.hasMoreElements()) {
			
//				System.out.println(((MoleculesPair) e.nextElement()).getName());

		}
	}

	public boolean getElementValue(String name) {
		
		if(v.contains(name)) {
		
			return true;
		} else {
		
			return false;
		}
	}
}
