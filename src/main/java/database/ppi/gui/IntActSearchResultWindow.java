package database.ppi.gui;

import api.payloads.ppi.IntActEntry;
import database.gui.SearchResultWindow;

import javax.swing.*;

public class IntActSearchResultWindow extends SearchResultWindow<IntActEntry> {
    private JSpinner searchDepth;
    private JCheckBox autoCoarse;
    private JCheckBox binaryInteractions;
    private JCheckBox complexInteractions;

    public IntActSearchResultWindow(IntActEntry[] tableValues) {
        super(IntActEntry.class, new String[]{"Name", "Type", "Organism"}, tableValues);
    }

    @Override
    protected Object getTableValueColumnAt(IntActEntry value, int columnIndex) {
        if (columnIndex == 0)
            return value.name;
        if (columnIndex == 1)
            return value.type;
        if (columnIndex == 2)
            return value.org;
        return null;
    }

    @Override
    protected void layoutMainPanelBeforeTable(JPanel mainPanel) {
        mainPanel.add(new JLabel("Following proteins have been found. Please select the protein of interest"), "span 2");
    }

    @Override
    protected void layoutMainPanelAfterTable(JPanel mainPanel) {
        mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
        mainPanel.add(new JLabel("What kind of settings do you wish to apply to the calculation?"), "span 2, wrap 15");
        mainPanel.add(new JLabel("Search Depth"), "span 1, gaptop 2");
        searchDepth = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        mainPanel.add(searchDepth, "span 1,wrap,gaptop 2");
        binaryInteractions = new JCheckBox();
        binaryInteractions.setSelected(true);
        mainPanel.add(new JLabel("Include binary interactions"), "span 1, gaptop 2");
        mainPanel.add(binaryInteractions, "span 1,wrap,gaptop 2");
        mainPanel.add(new JLabel("Include complex interactions"), "span 1, gaptop 2");
        complexInteractions = new JCheckBox();
        mainPanel.add(complexInteractions, "span 1,wrap,gaptop 2");
        mainPanel.add(new JLabel("Coarse all results of the same query"), "span 1, gaptop 2");
        autoCoarse = new JCheckBox();
        mainPanel.add(autoCoarse, "span 1,wrap,gaptop 2");
        mainPanel.add(new JSeparator(), "span, growx, gaptop 7");
    }

    @Override
    protected boolean onOkClicked() {
        if (table.getSelectedRows().length == 0) {
            JOptionPane.showMessageDialog(null, "Please choose an enzyme.", "Message", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (!binaryInteractions.isSelected() && !complexInteractions.isSelected()) {
            JOptionPane.showMessageDialog(null, "Please include at least one type of interaction.", "Message",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelClicked() {
    }

    public Integer getSearchDepth() {
        try {
            searchDepth.commitEdit();
        } catch (java.text.ParseException ignored) {
        }
        return (Integer) searchDepth.getValue();
    }

    public boolean getAutoCoarse() {
        return autoCoarse.isSelected();
    }

    public boolean getBinaryInteractions() {
        return binaryInteractions.isSelected();
    }

    public boolean getComplexInteractions() {
        return complexInteractions.isSelected();
    }
}
