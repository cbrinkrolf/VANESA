package database.brenda;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import configurations.Wrapper;
import pojos.DBColumn;
import xmlInput.sbml.MoleculesInput;

public class MostWantedMolecules {

	private Hashtable<String, Integer> table = new Hashtable<String, Integer>();

	// private int elements = 0;

	// private String filename = "BrendaMolecules.xml";

	Vector<MoleculesPair> v = new Vector<MoleculesPair>();

	// File file;

	public MostWantedMolecules() {

		URL url = this.getClass().getClassLoader().getResource("MoleculesSBML.xml");
		System.out.println("DEBUG molecules file: " + url);

		if (url != null) {
			try {
				new MoleculesInput(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			generateFile();
			try {
				new MoleculesInput(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// private Vector tryToGetInfos() {
	// return new Wrapper().requestDbContent(1,
	// BRENDAQueries.getAllBRENDAenzymeDetails);
	//
	// }

	private void generateFile() {
		ArrayList<DBColumn> results = new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeDetails);

		while (results.size() == 0) {
			results = new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeDetails);
		}

		for (DBColumn column : results) {
			String[] details = column.getColumn();

			if (details[3] != null && details[3].length() > 0) {
				separateReaction(details[3]);
			}
		}

		sortElements();
		MoleculeBox.getInstance().fillTable(v);

		// TODO generate Molecules file, if not deprecated
		// try
		// {
		// new Moleculesoutput(true, file).write();
		// }
		// catch (XMLStreamException e)
		// {
		// e.printStackTrace();
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }
	}

	// private void generateFile() {
	//
	// Vector results = tryToGetInfos();
	//
	// while (results.size() == 0) {
	// results = tryToGetInfos();
	// }
	//
	// Iterator it = results.iterator();
	//
	// while (it.hasNext()) {
	// String[] details = (String[]) it.next();
	// if (details[3] != null && details[3].length() > 0) {
	// separateReaction(details[3]);
	//
	// }
	// }
	//
	// sortElements();
	// MoleculeBoxSingelton.getInstance().fillTable(v);
	//
	// try {
	// new Moleculesoutput(true, file).write();
	// } catch (XMLStreamException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// private void print() {
	// Iterator it = v.iterator();
	// while (it.hasNext()) {
	// MoleculesPair p = (MoleculesPair) it.next();
	// }
	// }

	private void sortElements() {
		Iterator<String> it = table.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			int amount = (Integer) table.get(key);
			if (amount > 29) {
				v.add(new MoleculesPair(key, (Integer) table.get(key), true));
			} else {
				v.add(new MoleculesPair(key, (Integer) table.get(key), false));
			}
		}
		Collections.sort(v);

	}

	public void fillTables() {
		MoleculeBox box = MoleculeBox.getInstance();
		box.clear();
		box.fillTable(v);
	}

	private void separateReaction(String reaction) {

		StringTokenizer tok = new StringTokenizer(reaction, "=");
		String temp;
		String element;
		String[] gesplittet;
		while (tok.hasMoreTokens()) {
			element = tok.nextToken();
			gesplittet = element.split("\\s\\+\\s");
			for (int j = 0; j < gesplittet.length; j++) {
				temp = gesplittet[j].trim();
				if (table.containsKey(temp)) {
					int i = (Integer) table.get(temp);
					table.remove(temp);
					table.put(temp, i + 1);
				} else {
					table.put(temp, 1);
				}
			}
		}
	}
}
