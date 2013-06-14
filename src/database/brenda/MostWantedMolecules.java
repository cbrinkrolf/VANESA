package database.brenda;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import pojos.DBColumn;

import xmlInput.sbml.MoleculesInput;
import xmlOutput.sbml.Moleculesoutput;
import configurations.Wrapper;
import database.brenda.Files.BrendaDataPath;


public class MostWantedMolecules {

	private Hashtable table = new Hashtable();

	// private int elements = 0;

	// private String filename = "BrendaMolecules.xml";

	Vector v = new Vector();

	File file;

	public MostWantedMolecules() {
		
		try {
			file = new File(new BrendaDataPath().getPath("MoleculesSBML.xml").toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (file.exists()) {
			try {
				new MoleculesInput(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			generateFile();
			try {
				new MoleculesInput(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
//	private Vector tryToGetInfos() {
//		return new Wrapper().requestDbContent(1,
//				BRENDAQueries.getAllBRENDAenzymeDetails);
//
//	}
	
	private void generateFile()
	{
		ArrayList<DBColumn> results=new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeDetails);

		while (results.size()==0)
		{
			results=new Wrapper().requestDbContent(1, BRENDAQueries.getAllBRENDAenzymeDetails);
		}

		for (DBColumn column : results)
		{
			String[] details=column.getColumn();
			
			if (details[3]!=null&&details[3].length()>0)
			{
				separateReaction(details[3]);
			}
		}

		sortElements();
		MoleculeBoxSingelton.getInstance().fillTable(v);

		try
		{
			new Moleculesoutput(true, file).write();
		}
		catch (XMLStreamException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
//	private void generateFile() {
//
//		Vector results = tryToGetInfos();
//
//		while (results.size() == 0) {
//			results = tryToGetInfos();
//		}
//
//		Iterator it = results.iterator();
//
//		while (it.hasNext()) {
//			String[] details = (String[]) it.next();
//			if (details[3] != null && details[3].length() > 0) {
//				separateReaction(details[3]);
//
//			}
//		}
//
//		sortElements();
//		MoleculeBoxSingelton.getInstance().fillTable(v);
//
//		try {
//			new Moleculesoutput(true, file).write();
//		} catch (XMLStreamException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

//	private void print() {
//		Iterator it = v.iterator();
//		while (it.hasNext()) {
//			MoleculesPair p = (MoleculesPair) it.next();
//		}
//	}

	private void sortElements() {
		Set set = table.keySet();
		Iterator it = set.iterator();
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
		MoleculeBox box = MoleculeBoxSingelton.getInstance();
		box.clear();
		box.fillTable(v);
	}

	private void separateReaction(String reaction) {

		StringTokenizer tok = new StringTokenizer(reaction, "=");
		while (tok.hasMoreTokens()) {
			String element = tok.nextToken();
			String[] gesplittet = element.split("\\s\\+\\s");
			for (int j = 0; j < gesplittet.length; j++) {
				String temp = gesplittet[j].trim();
				if (table.containsKey(temp)) {
					int i = (Integer) table.get(temp);
					table.remove(temp);
					table.put(temp, i + 1);
				} else {
					table.put(temp, new Integer(1));
				}
			}
		}

	}

}
