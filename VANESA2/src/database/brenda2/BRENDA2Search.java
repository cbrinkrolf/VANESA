package database.brenda2;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import database.brenda2.gui.Brenda2SearchResultWindow;
import gui.MainWindow;
import pojos.DBColumn;

public class BRENDA2Search extends SwingWorker<Object, Object> {

	private String ecNumber, name, syn, metabolite, org = "";

	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();

	private String[][] results = null;

	//private Brenda2SearchResultWindow bsrw = null;

	public static final String enzymeSearch = "enzymeSearch";
	public static final String kmSearch = "kmSearch";

	private String search = BRENDA2Search.enzymeSearch;

	public BRENDA2Search(String[] input, Pathway mergePW, boolean headless) {

		ecNumber = input[0].trim();
		name = input[1].trim();
		syn = input[2].trim();
		metabolite = input[3].trim();
		org = input[4].trim();
	}

	public BRENDA2Search(String search) {
		this.search = search;
	}

	private String getEnzymeQuery() throws SQLException {
		String start = BRENDA2Queries.getEnzymes;
		String where = " where ";
		String join = "";
		String query = "";
		boolean firstCriteria = false;
		if (!ecNumber.equals("")) {
			String temp = dqv.replaceAndValidateString(ecNumber);
			if (temp.length() > 0) {
				where = where + dqv.prepareString(ecNumber, "e.ec", null);
				firstCriteria = true;
			}
		}

		if (!name.equals("")) {
			String temp = dqv.replaceAndValidateString(name);
			if (temp.length() > 0) {
				if (firstCriteria) {
					where = where + " AND " + dqv.prepareString(name, "e.recommendedName", null);
				} else {
					where = where + dqv.prepareString(name, "e.recommendedName", null);
				}
				firstCriteria = true;
			}
		}

		if (!metabolite.equals("")) {

			String temp = dqv.replaceAndValidateString(metabolite);
			if (temp.length() > 0) {
				join = join + " join brenda2_reaction on e.enzyme_id = brenda2_reaction.enzyme_id"
						+ " join brenda2_product2reaction on brenda2_reaction.id = brenda2_product2reaction.reaction_id"
						+ " join brenda2_subtrate2reaction on brenda2_reaction.id = brenda2_subtrate2reaction.reaction_id"
						+ " join brenda2_reactand as prod on brenda2_product2reaction.reactand_id = prod.id"
						+ " join brenda2_reactand as sub on brenda2_subtrate2reaction.reactand_id = sub.id"
						+ " join brenda2_metabolite as subM on sub.metabolite_id = subM.id"
						+ " join brenda2_metabolite as prodM on prod.metabolite_id = prodM.id";

				if (firstCriteria) {
					where = where + " AND "
					// + dqv.prepareString(metabolite, "r.reaction", null);
							+ "(prodM.name like '%" + metabolite + "%' or subM.name like '%" + metabolite + "%')";
				} else {
					where = where
							// + dqv.prepareString(metabolite, "r.reaction",
							// null);
							+ "(prodM.name like '%" + metabolite + "%' or subM.name like '%" + metabolite + "%')";
				}
				firstCriteria = true;
			}
		}

		if (!org.equals("")) {
			String temp = dqv.replaceAndValidateString(org);
			if (temp.length() > 0) {
				if (metabolite.length() < 1 || dqv.replaceAndValidateString(metabolite).length() < 1) {
					join = join + " join brenda2_reaction on e.enzyme_id = brenda2_reaction.enzyme_id";
				}
				join = join + " join brenda2_organism as org on org.id = brenda2_reaction.organism_id";

				if (firstCriteria) {
					where = where + " AND " + dqv.prepareString(org, "org.name", null);
				} else {
					where = where + dqv.prepareString(org, "org.name", null);
				}
				firstCriteria = true;
			}
		}

		if (!syn.equals("")) {
			String temp = dqv.replaceAndValidateString(syn);
			if (temp.length() > 0) {
				join = join + " join brenda2_synonym as syn on e.enzyme_id = syn.enzyme_id";

				if (firstCriteria) {
					where = where + " AND " + dqv.prepareString(syn, "syn.name", null);
				} else {
					where = where + dqv.prepareString(syn, "syn.name", null);
				}
				firstCriteria = true;
			}
		}
		query = start + join + where;
		return query;
	}

	private String getKmQuery() {

		String query = "select e.ec, org.name, met.name, km.value from brenda2_km as km"
				+ " join brenda2_metabolite as met on km.metabolite_id = met.id" + " join brenda2_organism as org on km.organism_id = org.id"
				+ " join brenda2_enzyme as e on km.enzyme_id = e.enzyme_id" + " where e.ec = '" + ecNumber + "'";
		//System.out.println(query);
		// = 3713"
		// select km.value, met.name, org.name from brenda2_km as km join
		// brenda2_metabolite as met on km.metabolite_id = met.id join
		// brenda2_organism as org on km.organism_id = org.id where km.enzyme_id
		// = 3713 LIMIT 0, 1000
		return query;
	}

	public String[][] getResults(){

		return this.results;
	}

	@Override
	protected Object doInBackground() throws Exception {
		//results = getResults();
		ArrayList<DBColumn> results = null;

		int headers = 0;

		switch (this.search) {
		case enzymeSearch:
			headers = 3;
			results = new Wrapper().requestDbContent(Wrapper.dbtype_BRENDA2, getEnzymeQuery());
			break;
		case kmSearch:
			headers = 4;
			results = new Wrapper().requestDbContent(Wrapper.dbtype_BRENDA2, getKmQuery());
			break;
		}

		String[][] container = new String[results.size()][headers];

		// System.out.println(results.size());
		for (int i = 0; i < results.size(); i++) {
			for (int j = 0; j < headers; j++) {
				container[i][j] = results.get(i).getColumn()[j];
			}
		}
		this.results = container;
		return null;
	}

	@Override
	public void done() {
		MainWindow.getInstance().closeProgressBar();
		
		if (results != null && results.length > 0) {
			/*if (bsrw == null) {
				bsrw = new Brenda2SearchResultWindow(results, ecNumber, name, syn, metabolite, org);
			} else {
				switch(this.search){
				case enzymeSearch:
					bsrw.updateEnzymeTable(results);
					break;
				case kmSearch:
					bsrw.updateKmTable(results);
					break;
				}
			}*/
		} else {
			JOptionPane.showMessageDialog(MainWindow.getInstance(), "Sorry, no entries have been found.");
		}
	}

	public String getEcNumber() {
		return ecNumber;
	}

	public void setEcNumber(String ecNumber) {
		this.ecNumber = ecNumber.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getSyn() {
		return syn;
	}

	public void setSyn(String syn) {
		this.syn = syn.trim();
	}

	public String getMetabolite() {
		return metabolite;
	}

	public void setMetabolite(String metabolite) {
		this.metabolite = metabolite.trim();
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org.trim();
	}
}
