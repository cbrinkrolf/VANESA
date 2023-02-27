package database.brenda;

import api.payloads.dbBrenda.DBBrendaEnzyme;
import biologicalElements.Pathway;
import database.brenda.gui.BrendaSearchResultWindow;
import gui.MainWindow;

import javax.swing.*;

public class BRENDASearch extends SwingWorker<Object, Object> {
    private final String ecNumber;
    private final String name;
    private final String metabolite;
    private final String organism;
    private final Pathway mergePW;
    private DBBrendaEnzyme[] results = null;

    public BRENDASearch(String ecNumber, String ecName, String metabolite, String organism, Pathway mergePW) {
        this.ecNumber = ecNumber;
        this.name = ecName;
        this.metabolite = metabolite;
        this.organism = organism;
        this.mergePW = mergePW;
    }

    @Override
    protected Object doInBackground() {
        results = BRENDA2Search.requestEnzymes(ecNumber, name, metabolite, organism, null);
        return null;
    }

    @Override
    public void done() {
        MainWindow.getInstance().closeProgressBar();
        if (results.length > 0) {
            BrendaSearchResultWindow bsrw = new BrendaSearchResultWindow(results);
            DBBrendaEnzyme[] selectedResults = bsrw.getSelectedValues();
            if (selectedResults.length != 0) {
                MainWindow.getInstance().showProgressBar("Fetching Network.");
                for (DBBrendaEnzyme res : selectedResults) {
                    BrendaConnector bc = new BrendaConnector(res, mergePW);
                    bc.setDisregarded(bsrw.getDisregarded());
                    bc.setOrganism_specific(bsrw.getOrganismSpecificDecision());
                    bc.setSearchDepth(bsrw.getSearchDepth());
                    bc.setCofactors(bsrw.getCoFactorsDecision());
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
