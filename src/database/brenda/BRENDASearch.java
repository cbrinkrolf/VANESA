package database.brenda;

import gui.MainWindow;
import gui.ProgressBar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;

import pojos.DBColumn;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import database.brenda.gui.BrendaSearchResultWindow;

public class BRENDASearch extends SwingWorker {

	String ec_number, name, substrat, product, organism;

	private DatabaseQueryValidator dqv = new DatabaseQueryValidator();

	MainWindow w;
	ProgressBar bar;
	// Vector results=new Vector();
	// ArrayList<String[]> results = new ArrayList<String[]>();
	String[][] results = null;

	BrendaSearchResultWindow bsrw;

	private Pathway mergePW=null;
	
	public BRENDASearch(String[] input, MainWindow w, ProgressBar bar, Pathway mergePW) {

		ec_number = input[0];
		name = input[1];
		substrat = input[2];
		product = input[3];
		organism = input[4];

		this.w = w;
		this.bar = bar;
		this.mergePW=mergePW;

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

	private Vector preparePattern(String patterns) {

		Vector v = new Vector();
		StringTokenizer st = new StringTokenizer(patterns);
		while (st.hasMoreTokens()) {

			String t = st.nextToken();
			boolean not = false;

			if (t.startsWith("!")) {

				String sub = t.substring(1, t.length());
				t = sub;
				not = true;

			}

			if (t.startsWith("*") && t.endsWith("*")) {

				String sub = t.substring(1, t.length() - 1);
				t = sub;

				if (not)
					v.add("!" + t);
				else
					v.add("=" + t);

			} else if (t.startsWith("*")) {

				String sub = t.substring(1, t.length());
				t = sub;

				if (not)
					v.add("!" + t);
				else
					v.add("=" + t);

			} else if (t.endsWith("*")) {

				String sub = t.substring(0, t.length() - 1);
				t = sub;

				if (not)
					v.add("!" + t);
				else
					v.add("=" + t);

			} else if (t.startsWith("&")) {
			} else if (t.startsWith("|")) {
				v.add("|");
			} else {

				if (not)
					v.add("!" + t);
				else
					v.add("=" + t);

			}
		}
		return v;
	}

	private boolean containsProduct(String products) {

		if (product.equals("") || products.equals(""))
			return true;

		Vector v = preparePattern(product.toUpperCase());
		Iterator it = v.iterator();

		boolean b = true;
		boolean connection = false;

		while (it.hasNext()) {
			String check = it.next().toString();

			if (products.contains(check.substring(1, check.length()))
					&& check.startsWith("=")) {

			} else if (!products.contains(check.substring(1, check.length()))
					&& check.startsWith("!")) {

			} else if (check.startsWith("|")) {
				connection = true;

			} else {

				if (connection && b) {
					b = true;
					connection = false;
				} else {
					b = false;
				}
			}
		}

		return b;
	}

	private boolean containsSubstrates(String substrates) {

		if (substrat.equals("") || substrates.equals(""))
			return true;

		Vector v = preparePattern(substrat.toUpperCase());
		Iterator it = v.iterator();

		boolean b = true;
		boolean connection = false;

		while (it.hasNext()) {
			String check = it.next().toString();

			if (substrates.contains(check.substring(1, check.length()))
					&& check.startsWith("=")) {

			} else if (!substrates.contains(check.substring(1, check.length()))
					&& check.startsWith("!")) {

			} else if (check.startsWith("|")) {
				connection = true;

			} else {

				if (connection && b) {
					b = true;
					connection = false;
				} else {
					b = false;
				}
			}
		}

		return b;
	}

	private boolean containsReactionElement(String reaction) {

		if (reaction.equals("") || reaction == null)
			return false;

		StringTokenizer st = new StringTokenizer(reaction, "=");

		String substrate = "";
		String product = "";

		int tokenCount = 0;
		while (st.hasMoreTokens()) {

			if (tokenCount == 0) {
				tokenCount++;
				substrate = st.nextToken().toUpperCase();
			} else {
				product = st.nextToken().toUpperCase();
			}
		}

		if (containsSubstrates(substrate) && containsProduct(product))
			return true;

		return false;
	}

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
		w.setLockedPane(true);
		results = getResults();
		w.setLockedPane(false);
		return null;
	}

	@Override
	public void done() {

		Boolean continueProgress = false;
		endSearch(w, bar);

		// if (results.size() > 0) {
		if (results.length > 0) {
			continueProgress = true;
			bsrw = new BrendaSearchResultWindow(results);
		} else {
			endSearch(w, bar);
			JOptionPane.showMessageDialog(w,
					"Sorry, no entries have been found.");
		}

		if (continueProgress) {
			Vector results = bsrw.getAnswer();
			if (results.size() != 0) {
				final Iterator it = results.iterator();
				while (it.hasNext()) {

					BrendaConnector bc = new BrendaConnector(bar, (String[]) it
							.next(), mergePW);
					bc.setDisregarded(bsrw.getDisregarded());
					bc.setOrganism_specific(bsrw.getOrganismSpecificDecision());
					bc.setSearchDepth(bsrw.getSerchDeapth());
				    bc.setCoFactors(bsrw.getCoFactorsDecision());
				    bc.setInhibitors(bsrw.getInhibitorsDecision());

					bc.execute();

				}
			}
		}
		endSearch(w, bar);
	}

	private void endSearch(final MainWindow w, final ProgressBar bar) {
		Runnable run = new Runnable() {
			public void run() {
				bar.closeWindow();
				w.setEnable(true);
			}
		};

		SwingUtilities.invokeLater(run);
	}

}
