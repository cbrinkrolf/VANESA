package miscalleanous.tables;

import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MyTable extends JXTable {
    private static final long serialVersionUID = 1L;

    public MyTable() {
        super();
    }

    public MyTable(final Object[][] rowData, final Object[] colNames) {
        super(rowData, colNames);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        Component comp = super.prepareRenderer(renderer, row, col);
        JComponent jcomp = (JComponent) comp;
        Object value = getValueAt(row, col);
        if (value instanceof Integer) {
        } else if (value instanceof Boolean) {
        } else if (value instanceof Double) {
        } else {
            jcomp.setToolTipText(value.toString());
        }
        return comp;
    }
}
