package database.mirna.gui;

import api.payloads.dbMirna.DBMirnaTargetGene;
import database.gui.SearchResultWindow;

import javax.swing.*;

public class MirnaTargetGeneSearchResultWindow extends SearchResultWindow<DBMirnaTargetGene> {
    public MirnaTargetGeneSearchResultWindow(DBMirnaTargetGene[] results) {
        super(DBMirnaTargetGene.class, new String[]{"Gene Name", "Database"}, results);
    }

    @Override
    protected Object getTableValueColumnAt(DBMirnaTargetGene value, int columnIndex) {
        if (columnIndex == 0)
            return value.accession;
        if (columnIndex == 1)
            return value.db;
        return null;
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
