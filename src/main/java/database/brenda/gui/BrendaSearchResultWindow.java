package database.brenda.gui;

import api.payloads.dbBrenda.DBBrendaEnzyme;
import database.gui.SearchResultWindow;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BrendaSearchResultWindow extends SearchResultWindow<DBBrendaEnzyme> implements ActionListener {
    private final JSpinner searchDepth;
    private final JCheckBox organismSpecificBox;
    private final JCheckBox disregard;
    private final JCheckBox inhibitorBox;
    private final JCheckBox cofactorBox;
    private final JCheckBox autoCoarseDepth;
    private final JCheckBox autoCoarseEnzymeNomenclature;
    private final JTextField filterText;
    private TableRowSorter<TableModel> sorter;

    public BrendaSearchResultWindow(DBBrendaEnzyme[] result) {
        super(DBBrendaEnzyme.class, new String[]{"EC Number", "Name", "Reaction", "Organism"}, result);
        organismSpecificBox = new JCheckBox();
        organismSpecificBox.setSelected(true);
        disregard = new JCheckBox();
        disregard.addActionListener(this);
        disregard.setActionCommand("disregard");
        inhibitorBox = new JCheckBox();
        inhibitorBox.setSelected(false);
        cofactorBox = new JCheckBox();
        cofactorBox.setSelected(false);
        autoCoarseDepth = new JCheckBox();
        autoCoarseEnzymeNomenclature = new JCheckBox();
        searchDepth = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
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
        autoCoarseEnzymeNomenclature.addActionListener(coarseListener);
        autoCoarseDepth.addActionListener(coarseListener);
    }

    /*public Vector<String[]> getAnswer() {
        Vector<String[]> v = new Vector<>();
        if (ok) {
            String tempEnzyme = "";
            int[] selectedRows = table.getSelectedRows();
            for (int selectedRow : selectedRows) {
                String enzymes = table.getValueAt(selectedRow, 0).toString();
                String organism = table.getValueAt(selectedRow, 3).toString();
                String[] details = {enzymes, organism};
                if (organismSpecificBox.isSelected() || !tempEnzyme.equals(enzymes)) {
                    v.add(details);
                }
                tempEnzyme = enzymes;
            }
        }
        return v;
    }*/

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
    protected Object getTableValueColumnAt(DBBrendaEnzyme value, int columnIndex) {
        if (columnIndex == 0)
            return value.ec;
        if (columnIndex == 1)
            return value.name;
        // TODO:
        //  if (columnIndex == 1)
        //    return value.reaction;
        //  return value.organism;
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
        mainPanel.add(searchDepth, "span 1,wrap,gaptop 2");
        mainPanel.add(new JLabel("Organism specific calculation"), "span 1, gaptop 2");
        mainPanel.add(organismSpecificBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Include Inhibitors"), "span 1, gaptop 2");
        mainPanel.add(inhibitorBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Include Cofactors"), "span 1, gaptop 2");
        mainPanel.add(cofactorBox, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Disregard Currency Metabolites"), "span 1, gaptop 2");
        mainPanel.add(disregard, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Coarse all results of the same query"), "span 1, gaptop 2");
        mainPanel.add(autoCoarseDepth, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Coarse enzyme due to their Enzyme nomenclature (EC-Number)"), "span 1, gaptop 2");
        mainPanel.add(autoCoarseEnzymeNomenclature, "span 1,wrap,gaptop 2");

        mainPanel.add(new JLabel("Filter results"), "span 1, gaptop 2");
        mainPanel.add(filterText, "span 1,wrap,gaptop 2");
    }
}
