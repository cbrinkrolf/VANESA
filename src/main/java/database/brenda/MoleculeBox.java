package database.brenda;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MoleculeBox {
    private static MoleculeBox instance = null;
    private final Hashtable<String, MoleculesPair> molecules = new Hashtable<>();
    private Vector<String> v = new Vector<>();

    private MoleculeBox() {
    }

    public static synchronized MoleculeBox getInstance() {
        if (MoleculeBox.instance == null) {
            MoleculeBox.instance = new MoleculeBox();
        }
        return MoleculeBox.instance;
    }

    public void fillTable(Vector<MoleculesPair> v) {
        for (MoleculesPair p : v) {
            molecules.put(p.getName().trim(), p);
        }
    }

    public void clear() {
        molecules.clear();
        v.clear();
    }

    public Vector<MoleculesPair> getAllValues() {
        Vector<MoleculesPair> v = new Vector<>();
        Enumeration<MoleculesPair> e = molecules.elements();
        while (e.hasMoreElements()) {
            v.add(e.nextElement());
        }
        Collections.sort(v);
        return v;
    }

    public Vector<String> getDisregardedValues() {
        Vector<String> results = new Vector<>();
        for (MoleculesPair p : molecules.values()) {
            if (p.isDisregard())
                results.add(p.getName());
        }
        v = results;
        return results;
    }

    public void changeValues(String name, boolean disregard) {
        molecules.get(name).setDisregard(disregard);
    }

    public boolean getElementValue(String name) {
        return v.contains(name);
    }
}
