package database.brenda.gui;

import database.brenda.MostWantedMolecules;
import gui.MainWindow;
import gui.tables.GenericTableModel;
import gui.tables.MyTable;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class BrendaPatternListWindow {
    private final JDialog dialog;
    private MyTable table;

    public BrendaPatternListWindow() {
        initTable();
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(800, 400));
        MigLayout layout = new MigLayout();
        JPanel mainPanel = new JPanel(layout);
        mainPanel.add(new JLabel("Which elements supposed to be disregarded during the calculation?"), "span 2");
        mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
        mainPanel.add(sp, "span 4, growx");
        mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
        JPanel selectPanel = new JPanel();
        JButton select = new JButton("select");
        select.addActionListener(e -> onSelectClicked());
        selectPanel.add(select);
        JButton deselect = new JButton("deselect");
        deselect.addActionListener(e -> onDeselectClicked());
        selectPanel.add(deselect);
        JButton deselectAll = new JButton("deselect all");
        deselectAll.addActionListener(e -> onDeselectAllClicked());
        selectPanel.add(deselectAll);
        mainPanel.add(selectPanel, "span,gaptop 1,align right,wrap");
        mainPanel.add(new JSeparator(), "span, growx, gaptop 10");
        JButton okButton = new JButton("ok");
        okButton.addActionListener(e -> onOkClicked());
        JOptionPane optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
        JButton[] buttons = {okButton};
        optionPane.setOptions(buttons);
        dialog = new JDialog(new JFrame(), "Calculation Settings", true);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
        dialog.setVisible(true);
    }

    private void initTable() {
        MostWantedMolecules box = MostWantedMolecules.getInstance();
        TableModel model = new GenericTableModel<>(new String[]{"# Occurrences in Reactions", "Name", "Disregarded"},
                                                   box.getAllValues().toArray(new MostWantedMolecules.Entry[0])) {
            @Override
            public Object getValueAt(MostWantedMolecules.Entry entry, int columnIndex) {
                if (columnIndex == 0)
                    return entry.amount;
                if (columnIndex == 1)
                    return entry.name;
                return entry.disregard;
            }
        };
        table = new MyTable();
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnControlVisible(false);
        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setFillsViewportHeight(true);
        table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
        table.setHorizontalScrollEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(430);
        table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(20);
    }

    private void onOkClicked() {
        dialog.setVisible(false);
    }

    private void onSelectClicked() {
        for (int selectedRow : table.getSelectedRows()) {
            table.setValueAt(true, selectedRow, 2);
        }
    }

    private void onDeselectClicked() {
        for (int selectedRow : table.getSelectedRows()) {
            table.setValueAt(false, selectedRow, 2);
        }
    }

    private void onDeselectAllClicked() {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setValueAt(false, i, 2);
        }
    }
}
