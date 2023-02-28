package database.mirna.gui;

import api.payloads.dbMirna.DBMirnaTargetGene;
import database.gui.SearchResultWindow;

import javax.swing.*;

public class MirnaTargetGeneSearchResultWindow extends SearchResultWindow<DBMirnaTargetGene> {
    public MirnaTargetGeneSearchResultWindow(DBMirnaTargetGene[] results) {
        super(DBMirnaTargetGene.class,
                new String[]{"Gene Name", "Database", "HGNC ID", "Ensembl Gene ID", "Entrez Gene ID"}, results);
    }

    @Override
    protected Object getTableValueColumnAt(DBMirnaTargetGene value, int columnIndex) {
        if (columnIndex == 0)
            return value.name;
        if (columnIndex == 1)
            return value.db;
        if (columnIndex == 2)
            return value.hgncId;
        if (columnIndex == 3)
            return value.ensemblGeneId;
        return value.entrezGeneId;
    }

    @Override
    protected void layoutMainPanelBeforeTable(JPanel mainPanel) {
        mainPanel.add(new JLabel("The following microRNAs have been found. Please select the microRNA of interest."), "");
    }

    @Override
    protected void layoutMainPanelAfterTable(JPanel mainPanel) {
    }

    @Override
    protected boolean onOkClicked() {
        return true;
    }

    @Override
    protected void onCancelClicked() {
    }
}
