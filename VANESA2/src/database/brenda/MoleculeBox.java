package database.brenda;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class MoleculeBox {

	private Hashtable<String, MoleculesPair> molecules = new Hashtable<String, MoleculesPair>();
	private Vector<String> v = new Vector<String>();
	private static MoleculeBox instance = null;
	
	private MoleculeBox() {

	}
	
	public static synchronized MoleculeBox getInstance(){
		if(MoleculeBox.instance == null){
			MoleculeBox.instance = new MoleculeBox();
		}
		return MoleculeBox.instance;
	}

	public void fillTable(Vector<MoleculesPair> v) {
		Iterator<MoleculesPair> it = v.iterator();
		while (it.hasNext()) {
			MoleculesPair p = (MoleculesPair) it.next();
			molecules.put(p.getName().trim(), p);
		}
	}

	public void clear() {
		molecules.clear();
		v.clear();
	}

	public Vector<MoleculesPair> getAllValues() {
		Vector<MoleculesPair> v = new Vector<MoleculesPair>();
		Enumeration<MoleculesPair> e = molecules.elements();
		while (e.hasMoreElements()) {
			v.add(e.nextElement());
		}
		Collections.sort(v);
		return v;
	}

	public Vector<String> getDisregardedValues() {

		Vector<String> results = new Vector<String>();
		Iterator<MoleculesPair> it = molecules.values().iterator();
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
		Enumeration<MoleculesPair> e = molecules.elements();
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
