package database.brenda2;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import configurations.Wrapper;
import database.Connection.DatabaseQueryValidator;
import gui.MainWindow;
import gui.MyPopUp;
import org.apache.commons.lang3.StringUtils;
import pojos.DBColumn;

public class BRENDA2Search extends SwingWorker<Object, Object> {
    public static final String enzymeSearch = "enzymeSearch";
    public static final String kmSearch = "kmSearch";
    public static final String turnoverSearch = "turnoverSearch";

    private final DatabaseQueryValidator dqv = new DatabaseQueryValidator();
    private final String search;
    private String ecNumber;
    private String name;
    private String syn;
    private String metabolite;
    private String org;
    private String[][] results = null;

    public BRENDA2Search(String search) {
        this.search = search;
    }

    private String getEnzymeQuery() {
        String start = "SELECT distinct e.* FROM brenda2_enzyme e";
        boolean firstCriteria = false;
        String where = " WHERE ";
        if (StringUtils.isNotEmpty(ecNumber)) {
            String temp = dqv.replaceAndValidateString(ecNumber);
            if (temp.length() > 0) {
                where += dqv.prepareString(ecNumber, "e.ec", null);
                firstCriteria = true;
            }
        }
        // only use the name if ecNumber is not given
        if (StringUtils.isNotEmpty(name) && ecNumber.length() < 7) {
            String temp = dqv.replaceAndValidateString(name);
            if (temp.length() > 0) {
                if (firstCriteria) {
                    where += " AND ";
                }
                where += dqv.prepareString(name, "e.recommendedName", null);
                firstCriteria = true;
            }
        }
        String join = "";
        if (StringUtils.isNotEmpty(metabolite)) {
            String temp = dqv.replaceAndValidateString(metabolite);
            if (temp.length() > 0) {
                join += " join brenda2_reaction on e.enzyme_id = brenda2_reaction.enzyme_id"
                        + " join brenda2_product2reaction on brenda2_reaction.id = brenda2_product2reaction.reaction_id"
                        + " join brenda2_subtrate2reaction on brenda2_reaction.id = brenda2_subtrate2reaction.reaction_id"
                        + " join brenda2_reactand as prod on brenda2_product2reaction.reactand_id = prod.id"
                        + " join brenda2_reactand as sub on brenda2_subtrate2reaction.reactand_id = sub.id"
                        + " join brenda2_metabolite as subM on sub.metabolite_id = subM.id"
                        + " join brenda2_metabolite as prodM on prod.metabolite_id = prodM.id";
                if (firstCriteria) {
                    where += " AND ";
                }
                where += "(prodM.name like '%" + metabolite + "%' or subM.name like '%" + metabolite + "%')";
                firstCriteria = true;
            }
        }
        if (StringUtils.isNotEmpty(org)) {
            String temp = dqv.replaceAndValidateString(org);
            if (temp.length() > 0) {
                if (metabolite.length() < 1 || dqv.replaceAndValidateString(metabolite).length() < 1) {
                    join += " join brenda2_reaction on e.enzyme_id = brenda2_reaction.enzyme_id";
                }
                join += " join brenda2_organism as org on org.id = brenda2_reaction.organism_id";
                if (firstCriteria) {
                    where += " AND ";
                }
                where += dqv.prepareString(org, "org.name", null);
                firstCriteria = true;
            }
        }
        if (StringUtils.isNotEmpty(syn)) {
            String temp = dqv.replaceAndValidateString(syn);
            if (temp.length() > 0) {
                join += " join brenda2_synonym as syn on e.enzyme_id = syn.enzyme_id";
                if (firstCriteria) {
                    where += " AND ";
                }
                where = where + dqv.prepareString(syn, "syn.name", null);
            }
        }
        return start + join + where;
    }

    private String getKmQuery() {
        return "select e.ec, org.name, met.name, km.value from brenda2_km as km"
                + " join brenda2_metabolite as met on km.metabolite_id = met.id" + " join brenda2_organism as org on km.organism_id = org.id"
                + " join brenda2_enzyme as e on km.enzyme_id = e.enzyme_id" + " where e.ec = '" + ecNumber + "'";
    }

    private String getTurnoverQuery() {
        return "SELECT e.ec, org.name, met.name, tn.min from brenda2_turnovernumber as tn"
                + " JOIN brenda2_metabolite as met on tn.metabolite_id = met.id"
                + " JOIN brenda2_organism as org on tn.organism_id = org.id"
                + " JOIN brenda2_enzyme as e on tn.enzyme_id = e.enzyme_id" + " where e.ec = '" + ecNumber + "'";
    }

    public String[][] getResults() {
        return this.results;
    }

    @Override
    protected Object doInBackground() throws Exception {
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
            case turnoverSearch:
                headers = 4;
                results = new Wrapper().requestDbContent(Wrapper.dbtype_BRENDA2, getTurnoverQuery());
        }
        String[][] container = new String[results.size()][headers];
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
        if (results == null || results.length < 1) {
            MyPopUp.getInstance().show("Brenda search", "Sorry, no entries have been found.");
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
