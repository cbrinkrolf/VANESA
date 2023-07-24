package database.kegg.gui;

import api.payloads.kegg.KeggPathway;
import database.gui.SearchResultWindow;

import javax.swing.*;

public class KEGGSearchResultWindow extends SearchResultWindow<KeggPathway> {
    private JCheckBox searchMirnas;
    private JCheckBox autoCoarse;

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
            return value.taxonomy_name;
        return null;
    }

    @Override
    protected void layoutMainPanelBeforeTable(JPanel mainPanel) {
        mainPanel.add(new JLabel("Following Pathways have been found. Please select the pathways of interest"), "");
    }

    @Override
    protected void layoutMainPanelAfterTable(JPanel mainPanel) {
        searchMirnas = new JCheckBox("Search MirBase/TarBase for possibly connected microRNAs", false);
        autoCoarse = new JCheckBox("Coarse MirBase/TarBase results to their associated node", false);
        autoCoarse.setEnabled(false);
        searchMirnas.addActionListener(arg0 -> {
            if (searchMirnas.isSelected()) {
                autoCoarse.setEnabled(true);
            } else {
                autoCoarse.setSelected(false);
                autoCoarse.setEnabled(false);
            }
        });
        mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
        mainPanel.add(searchMirnas);
        mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
        mainPanel.add(autoCoarse);
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

    public boolean getSearchMirnas() {
        return searchMirnas.isSelected();
    }

    public boolean getAutoCoarse() {
        return autoCoarse.isSelected();
    }
}
