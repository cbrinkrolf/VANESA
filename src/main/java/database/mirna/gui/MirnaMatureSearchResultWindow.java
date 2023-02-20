package database.mirna.gui;

import api.payloads.dbMirna.DBMirnaMature;
import database.gui.SearchResultWindow;

import javax.swing.*;

public class MirnaMatureSearchResultWindow extends SearchResultWindow<DBMirnaMature> {
    public MirnaMatureSearchResultWindow(DBMirnaMature[] tableValues) {
        super(DBMirnaMature.class, new String[]{"Name", "Accession", "Sequence"}, tableValues);
    }

    @Override
    protected Object getTableValueColumnAt(DBMirnaMature value, int columnIndex) {
        if (columnIndex == 0)
            return value.name;
        if (columnIndex == 1)
            return value.accession;
        if (columnIndex == 2)
            return value.sequence;
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
