package database.brenda;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import biologicalElements.Pathway;
import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import database.brenda.gui.BrendaSearchResultWindow;
import gui.MainWindow;
import org.apache.commons.lang3.StringUtils;
import pojos.DBColumn;

public class BRENDASearch extends SwingWorker<Object, Object> {
    private final DatabaseQueryValidator dqv = new DatabaseQueryValidator();
    private final String ecNumber;
    private final String name;
    private final String substrate;
    private final String product;
    private final String organism;
    private final Pathway mergePW;
    private final boolean headless;
    private BrendaSearchResultWindow bsrw;
    private String[][] results = null;

    public BRENDASearch(String[] input, Pathway mergePW, boolean headless) {
        ecNumber = input[0];
        name = input[1];
        substrate = input[2];
        product = input[3];
        organism = input[4];
        this.mergePW = mergePW;
        this.headless = headless;
    }

    private String getQuery() {
        String queryStart = BRENDAQueries.searchBrendaEnzyms;
        boolean firstCriteria = false;
        if (StringUtils.isNotEmpty(ecNumber)) {
            String temp = dqv.replaceAndValidateString(ecNumber);
            if (temp.length() > 0) {
                queryStart += dqv.prepareString(ecNumber, "e.ec_number", null);
                firstCriteria = true;
            }
        }
        if (StringUtils.isNotEmpty(name)) {
            String temp = dqv.replaceAndValidateString(name);
            if (temp.length() > 0) {
                if (firstCriteria) {
                    queryStart += " AND ";
                }
                queryStart += dqv.prepareString(name, "e.recomment_name", null);
                firstCriteria = true;
            }
        }
        if (StringUtils.isNotEmpty(organism)) {
            String temp = dqv.replaceAndValidateString(organism);
            if (temp.length() > 0) {
                if (firstCriteria) {
                    queryStart += " AND ";
                }
                queryStart += dqv.prepareString(organism, "org.org_name", null);
                firstCriteria = true;
            }
        }
        if (StringUtils.isNotEmpty(product)) {
            String temp = dqv.replaceAndValidateString(product);
            if (temp.length() > 0) {
                if (firstCriteria) {
                    queryStart += " AND ";
                }
                queryStart += dqv.prepareString(product, "r.reaction", null);
                firstCriteria = true;
            }
        }
        if (StringUtils.isNotEmpty(substrate)) {
            String temp = dqv.replaceAndValidateString(substrate);
            if (temp.length() > 0) {
                if (firstCriteria) {
                    queryStart += " AND ";
                }
                queryStart += dqv.prepareString(substrate, "r.reaction", null);
            }
        }
        return queryStart + " ORDER BY e.ec_number;";
    }

    public String[][] getResults() {
        ArrayList<DBColumn> results = new Wrapper().requestDbContent(1, getQuery());
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
        MainWindow.getInstance().closeProgressBar();
        if (results.length > 0) {
            bsrw = new BrendaSearchResultWindow(results);
            Vector<String[]> results = bsrw.getAnswer();
            if (results.size() != 0) {
                MainWindow.getInstance().showProgressBar("Fetching Network.");
                for (String[] res : results) {
                    BrendaConnector bc = new BrendaConnector(res, mergePW, headless);
                    bc.setDisregarded(bsrw.getDisregarded());
                    bc.setOrganism_specific(bsrw.getOrganismSpecificDecision());
                    bc.setSearchDepth(bsrw.getSearchDepth());
                    bc.setCoFactors(bsrw.getCoFactorsDecision());
                    bc.setInhibitors(bsrw.getInhibitorsDecision());
                    bc.setAutoCoarseDepth(bsrw.getAutoCoarseDepth());
                    bc.setAutoCoarseEnzymeNomenclature(bsrw.getAutoCoarseEnzymeNomenclature());
                    bc.execute();
                }
            }
        } else {
            JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), "Sorry, no entries have been found.");
        }
    }
}
