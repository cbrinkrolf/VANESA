package database.brenda.gui;

import api.payloads.dbBrenda.DBBrendaReaction;
import database.gui.SearchResultWindow;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BrendaSearchResultWindow extends SearchResultWindow<DBBrendaReaction> implements ActionListener {
    private JSpinner searchDepth;
    private JCheckBox organismSpecificBox;
    private JCheckBox disregard;
    private JCheckBox inhibitorBox;
    private JCheckBox cofactorBox;
    private JCheckBox autoCoarseDepth;
    private JCheckBox autoCoarseEnzymeNomenclature;
    private JTextField filterText;
    private TableRowSorter<TableModel> sorter;

    public BrendaSearchResultWindow(DBBrendaReaction[] result) {
        super(DBBrendaReaction.class, new String[]{"EC Number", "Name", "Educts", "Products", "Organism"}, result);
    }

    public Integer getSearchDepth() {
        try {
            searchDepth.commitEdit();
        } catch (java.text.ParseException ignored) {
        }
        return (Integer) searchDepth.getValue();
    }

    public boolean getOrganismSpecificDecision() {
        return organismSpecificBox.isSelected();
    }

    public boolean getInhibitorsDecision() {
        return inhibitorBox.isSelected();
    }

    public boolean getCoFactorsDecision() {
        return cofactorBox.isSelected();
    }

    public boolean getDisregarded() {
        return disregard.isSelected();
    }

    public boolean getAutoCoarseDepth() {
        return autoCoarseDepth.isSelected();
    }

    public boolean getAutoCoarseEnzymeNomenclature() {
        return autoCoarseEnzymeNomenclature.isSelected();
    }

    @Override
    protected boolean onOkClicked() {
        if (table.getSelectedRows().length == 0) {
            JOptionPane.showMessageDialog(null, "Please choose an enzyme.", "Message", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelClicked() {
    }

    public void actionPerformed(ActionEvent e) {
        String event = e.getActionCommand();
        if ("disregard".equals(event)) {
            if (disregard.isSelected()) {
                new BrendaPatternListWindow();
            }
        }
    }

    @Override
    protected Object getTableValueColumnAt(DBBrendaReaction value, int columnIndex) {
        if (columnIndex == 0)
            return value.ec;
        if (columnIndex == 1)
            return value.enzymeName;
        if (columnIndex == 2)
            return String.join(" + ", value.educts);
        if (columnIndex == 3)
            return String.join(" + ", value.products);
        // TODO: return value.organism;
        return null;
    }

    @Override
    protected void layoutMainPanelBeforeTable(JPanel mainPanel) {
        sorter = new TableRowSorter<>(table.getModel());
        sorter.setRowFilter(null);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mainPanel.add(new JLabel("Following enzymes have been found. Please select the enzymes of interest"), "span 2");
    }

    @Override
    protected void layoutMainPanelAfterTable(JPanel mainPanel) {
        mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
        mainPanel.add(new JLabel("What kind of settings do you wish to apply to the calculation?"), "span 2, wrap 15");

        mainPanel.add(new JLabel("Search Depth"), "span 1, gaptop 2");
        searchDepth = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        mainPanel.add(searchDepth, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Organism specific calculation"), "span 1, gaptop 2");
        organismSpecificBox = new JCheckBox();
        organismSpecificBox.setSelected(true);
        mainPanel.add(organismSpecificBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Include Inhibitors"), "span 1, gaptop 2");
        inhibitorBox = new JCheckBox();
        inhibitorBox.setSelected(false);
        mainPanel.add(inhibitorBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Include Cofactors"), "span 1, gaptop 2");
        cofactorBox = new JCheckBox();
        cofactorBox.setSelected(false);
        mainPanel.add(cofactorBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Disregard Currency Metabolites"), "span 1, gaptop 2");
        disregard = new JCheckBox();
        disregard.addActionListener(this);
        disregard.setActionCommand("disregard");
        mainPanel.add(disregard, "span 1,wrap,gaptop 2");

        ActionListener coarseListener = arg0 -> {
            if (autoCoarseEnzymeNomenclature.isSelected()) {
                autoCoarseDepth.setSelected(false);
                autoCoarseDepth.setEnabled(false);
            } else if (autoCoarseDepth.isSelected()) {
                autoCoarseEnzymeNomenclature.setSelected(false);
                autoCoarseEnzymeNomenclature.setEnabled(false);
            } else {
                autoCoarseEnzymeNomenclature.setEnabled(true);
                autoCoarseDepth.setEnabled(true);
            }
        };

        mainPanel.add(new JLabel("Coarse all results of the same query"), "span 1, gaptop 2");
        autoCoarseDepth = new JCheckBox();
        autoCoarseDepth.addActionListener(coarseListener);
        mainPanel.add(autoCoarseDepth, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Coarse enzyme due to their Enzyme nomenclature (EC-Number)"), "span 1, gaptop 2");
        autoCoarseEnzymeNomenclature = new JCheckBox();
        autoCoarseEnzymeNomenclature.addActionListener(coarseListener);
        mainPanel.add(autoCoarseEnzymeNomenclature, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Filter results"), "span 1, gaptop 2");
        filterText = new JTextField(10);
        filterText.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (filterText.getText().trim().length() < 1) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(filterText.getText().trim()));
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        mainPanel.add(filterText, "span 1,wrap,gaptop 2");
    }
}
