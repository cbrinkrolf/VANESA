package database.brenda;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import database.brenda.gui.BrendaSearchResultWindow;
import gui.MainWindow;
import gui.MainWindowSingleton;
import pojos.DBColumn;

public class BRENDASearch extends SwingWorker<Object, Object> {

	String ec_number, name, substrat, product, organism;

	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();

	private MainWindow w;
	private String[][] results = null;

	private BrendaSearchResultWindow bsrw;

	private Pathway mergePW = null;

	private boolean headless;

	public BRENDASearch(String[] input, MainWindow w,Pathway mergePW, boolean headless) {

		ec_number = input[0];
		name = input[1];
		substrat = input[2];
		product = input[3];
		organism = input[4];

		this.w = w;
		this.mergePW = mergePW;
		this.headless = headless;

	}

	private String getQuery() throws SQLException {

		// ResultSet rs = null;
		String queryStart = BRENDAQueries.searchBrendaEnzyms;

		boolean firstCriteria = false;

		if (!ec_number.equals("")) {
			String temp = dqv.replaceAndValidateString(ec_number);
			if (temp.length() > 0) {
				queryStart = queryStart
						+ dqv.prepareString(ec_number, "e.ec_number", null);
				firstCriteria = true;
			}
		}

		if (!name.equals("")) {
			String temp = dqv.replaceAndValidateString(name);
			if (temp.length() > 0) {
				if (firstCriteria) {
					queryStart = queryStart + " AND "
							+ dqv.prepareString(name, "e.recomment_name", null);
				} else {
					queryStart = queryStart
							+ dqv.prepareString(name, "e.recomment_name", null);
				}
				firstCriteria = true;
			}
		}

		if (!organism.equals("")) {
			String temp = dqv.replaceAndValidateString(organism);
			if (temp.length() > 0) {

				if (firstCriteria) {
					queryStart = queryStart + " AND "
							+ dqv.prepareString(organism, "org.org_name", null);
				} else {
					queryStart = queryStart
							+ dqv.prepareString(organism, "org.org_name", null);
				}
				firstCriteria = true;
			}
		}

		if (!product.equals("")) {

			String temp = dqv.replaceAndValidateString(product);
			if (temp.length() > 0) {

				if (firstCriteria) {
					queryStart = queryStart + " AND "
							+ dqv.prepareString(product, "r.reaction", null);
				} else {
					queryStart = queryStart
							+ dqv.prepareString(product, "r.reaction", null);
				}
				firstCriteria = true;
			}
		}

		if (!substrat.equals("")) {
			String temp = dqv.replaceAndValidateString(substrat);
			if (temp.length() > 0) {

				if (firstCriteria) {
					queryStart = queryStart + " AND "
							+ dqv.prepareString(substrat, "r.reaction", null);
				} else {
					queryStart = queryStart
							+ dqv.prepareString(substrat, "r.reaction", null);
				}
				firstCriteria = true;
			}
		}
		return queryStart + " order by e.ec_number;";
	}

	/*
	 * private Vector<String> preparePattern(String patterns) {
	 * 
	 * Vector<String> v = new Vector<String>(); StringTokenizer st = new
	 * StringTokenizer(patterns); String t; String sub; while
	 * (st.hasMoreTokens()) {
	 * 
	 * t = st.nextToken();
	 * 
	 * boolean not = false;
	 * 
	 * if (t.startsWith("!")) {
	 * 
	 * sub = t.substring(1, t.length()); t = sub; not = true;
	 * 
	 * }
	 * 
	 * if (t.startsWith("*") && t.endsWith("*")) {
	 * 
	 * sub = t.substring(1, t.length() - 1); t = sub;
	 * 
	 * if (not) v.add("!" + t); else v.add("=" + t);
	 * 
	 * } else if (t.startsWith("*")) {
	 * 
	 * sub = t.substring(1, t.length()); t = sub;
	 * 
	 * if (not) v.add("!" + t); else v.add("=" + t);
	 * 
	 * } else if (t.endsWith("*")) {
	 * 
	 * sub = t.substring(0, t.length() - 1); t = sub;
	 * 
	 * if (not) v.add("!" + t); else v.add("=" + t);
	 * 
	 * } else if (t.startsWith("&")) { } else if (t.startsWith("|")) {
	 * v.add("|"); } else {
	 * 
	 * if (not) v.add("!" + t); else v.add("=" + t);
	 * 
	 * } } return v; }
	 */

	/*
	 * private boolean containsProduct(String products) {
	 * 
	 * if (product.equals("") || products.equals("")) return true;
	 * 
	 * Vector v = preparePattern(product.toUpperCase()); Iterator it =
	 * v.iterator();
	 * 
	 * boolean b = true; boolean connection = false;
	 * 
	 * while (it.hasNext()) { String check = it.next().toString();
	 * 
	 * if (products.contains(check.substring(1, check.length())) &&
	 * check.startsWith("=")) {
	 * 
	 * } else if (!products.contains(check.substring(1, check.length())) &&
	 * check.startsWith("!")) {
	 * 
	 * } else if (check.startsWith("|")) { connection = true;
	 * 
	 * } else {
	 * 
	 * if (connection && b) { b = true; connection = false; } else { b = false;
	 * } } }
	 * 
	 * return b; }
	 */

	/*
	 * private boolean containsSubstrates(String substrates) {
	 * 
	 * if (substrat.equals("") || substrates.equals("")) return true;
	 * 
	 * Vector v = preparePattern(substrat.toUpperCase()); Iterator it =
	 * v.iterator();
	 * 
	 * boolean b = true; boolean connection = false;
	 * 
	 * while (it.hasNext()) { String check = it.next().toString();
	 * 
	 * if (substrates.contains(check.substring(1, check.length())) &&
	 * check.startsWith("=")) {
	 * 
	 * } else if (!substrates.contains(check.substring(1, check.length())) &&
	 * check.startsWith("!")) {
	 * 
	 * } else if (check.startsWith("|")) { connection = true;
	 * 
	 * } else {
	 * 
	 * if (connection && b) { b = true; connection = false; } else { b = false;
	 * } } }
	 * 
	 * return b; }
	 */

	/*
	 * private boolean containsReactionElement(String reaction) {
	 * 
	 * if (reaction.equals("") || reaction == null) return false;
	 * 
	 * StringTokenizer st = new StringTokenizer(reaction, "=");
	 * 
	 * String substrate = ""; String product = "";
	 * 
	 * int tokenCount = 0; while (st.hasMoreTokens()) {
	 * 
	 * if (tokenCount == 0) { tokenCount++; substrate =
	 * st.nextToken().toUpperCase(); } else { product =
	 * st.nextToken().toUpperCase(); } }
	 * 
	 * if (containsSubstrates(substrate) && containsProduct(product)) return
	 * true;
	 * 
	 * return false; }
	 */

	public String[][] getResults() throws SQLException {

		ArrayList<DBColumn> results = new Wrapper().requestDbContent(1,
				getQuery());
		String[][] container = new String[results.size()][4];

		for (int i = 0; i < results.size(); i++) {
			container[i][0] = results.get(i).getColumn()[0];
			container[i][1] = results.get(i).getColumn()[1];
			container[i][2] = results.get(i).getColumn()[3];
			container[i][3] = results.get(i).getColumn()[4];
		}

		return container;
	}

	@Override
	protected Object doInBackground() throws Exception {
		results = getResults();

		return null;
	}

	@Override
	public void done() {

		MainWindowSingleton.getInstance().closeProgressBar();
		Boolean continueProgress = false;

		if (results.length > 0) {
			continueProgress = true;
			bsrw = new BrendaSearchResultWindow(results);
		} else {
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}

		if (continueProgress) {
			Vector<String[]> results = bsrw.getAnswer();
			if (results.size() != 0) {
				MainWindowSingleton.getInstance().showProgressBar("Fetching Network.");
				final Iterator<String[]> it = results.iterator();
				String[] res;
		

				while (it.hasNext()) {
					res = it.next();
					BrendaConnector bc = new BrendaConnector(res, mergePW,
							headless);
					bc.setDisregarded(bsrw.getDisregarded());
					bc.setOrganism_specific(bsrw.getOrganismSpecificDecision());
					bc.setSearchDepth(bsrw.getSerchDeapth());
					bc.setCoFactors(bsrw.getCoFactorsDecision());
					bc.setInhibitors(bsrw.getInhibitorsDecision());
					bc.setAutoCoarseDepth(bsrw.getAutoCoarseDepth());
					bc.setAutoCoarseEnzymeNomenclature(bsrw.getAutoCoarseEnzymeNomenclature());
					bc.execute();

				}
			}
		}
	}
}
