package graph.animations;

import javax.swing.table.DefaultTableModel;

public class RegulationTableModel extends DefaultTableModel {
    public RegulationTableModel(Object[][] rows, String[] headers) {
        super(rows, headers);
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c == 0)
            return String.class;
        return Integer.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col != 0;
    }
}
