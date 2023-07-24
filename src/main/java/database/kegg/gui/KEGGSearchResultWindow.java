package database.kegg.gui;

import api.payloads.kegg.KeggPathway;
import database.gui.SearchResultWindow;

import javax.swing.*;

public class KEGGSearchResultWindow extends SearchResultWindow<KeggPathway> {
    public KEGGSearchResultWindow(KeggPathway[] tableValues) {
        super(KeggPathway.class, new String[]{"Id", "Name", "Organism"}, tableValues);
    }

    @Override
    protected Object getTableValueColumnAt(KeggPathway value, int columnIndex) {
        if (columnIndex == 0)
            return value.id;
        if (columnIndex == 1)
            return value.name;
        if (columnIndex == 2)
            return value.taxonomyName;
        return null;
    }

    @Override
    protected void layoutMainPanelBeforeTable(JPanel mainPanel) {
        mainPanel.add(new JLabel("Following Pathways have been found. Please select the pathways of interest"), "");
    }

    @Override
    protected void layoutMainPanelAfterTable(JPanel mainPanel) {
    }

    @Override
    protected boolean onOkClicked() {
        if (table.getSelectedRows().length == 0) {
            JOptionPane.showMessageDialog(null, "Please choose a pathway.", "Message", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelClicked() {
    }
}
