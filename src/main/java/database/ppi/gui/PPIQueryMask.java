package database.ppi.gui;

import database.gui.QueryMask;
import database.ppi.PPISearch;
import gui.MainWindow;
import gui.eventhandlers.TextFieldColorChanger;

import javax.swing.*;

public class PPIQueryMask extends QueryMask {
    private static final String DB_NAME_HPRD = "HPRD";
    private static final String DB_NAME_MINT = "MINT";
    private static final String DB_NAME_INTACT = "IntAct";
    private static final String[] DB_NAMES = {DB_NAME_HPRD, DB_NAME_MINT, DB_NAME_INTACT};
    private final JComboBox<String> choosePPIDatabase;
    private final JTextField fullName;
    private final JTextField alias;
    private final JTextField acNumber;

    public PPIQueryMask() {
        choosePPIDatabase = new JComboBox<>(DB_NAMES);
        choosePPIDatabase.setSelectedItem(DB_NAMES[0]);
        fullName = new JTextField(20);
        fullName.setText("");
        fullName.addFocusListener(new TextFieldColorChanger());

        alias = new JTextField(20);
        alias.setText("HMG");
        alias.addFocusListener(new TextFieldColorChanger());

        acNumber = new JTextField(20);
        acNumber.setText("");
        acNumber.addFocusListener(new TextFieldColorChanger());

        panel.add(new JLabel("Database"), "span 2, gap 5");
        panel.add(choosePPIDatabase, "span,wrap,growx ,gap 10");
        panel.add(new JLabel("Name"), "span 2, gap 5");
        panel.add(fullName, "span,wrap,growx ,gap 10");
        panel.add(new JLabel("Alias"), "span 2, gap 5");
        panel.add(alias, "span,wrap,growx ,gap 10");
        panel.add(new JLabel("AC number"), "span 2, gap 5");
        panel.add(acNumber, "span, wrap, growx, gap 10");
        addControlButtons();
    }

    @Override
    public String getMaskName() {
        return "PPI";
    }

    @Override
    protected void reset() {
        fullName.setText("");
        alias.setText("");
        acNumber.setText("");
    }

    @Override
    protected String search() {
        String selectedDatabase = (String) choosePPIDatabase.getSelectedItem();
        if (selectedDatabase != null) {
            switch (selectedDatabase) {
                case "HPRD":
                    PPISearch.requestHPRDEntries(fullName.getText(), alias.getText(), acNumber.getText());
                    break;
                case "MINT":
                    PPISearch.requestMintEntries(fullName.getText(), alias.getText(), acNumber.getText());
                    break;
                case "IntAct":
                    PPISearch.requestIntActEntries(fullName.getText(), alias.getText(), acNumber.getText());
                    break;
            }
        }
        return null;
    }

    @Override
    protected boolean doSearchCriteriaExist() {
        return fullName.getText().length() > 0 || alias.getText().length() > 0 || acNumber.getText().length() > 0;
    }

    @Override
    protected void showInfoWindow() {
        String instructions =
                "<html>" + "<h3>The PPI search window</h3>" + "<ul>" + "<li>Supports search in HPRD, Mint, IntAct.<p>" +
                "<li>Select a database and search for a name, alias or AC number.<p>" + "</ul>" + "</html>";
        JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(), instructions, "PPI Information",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
}
