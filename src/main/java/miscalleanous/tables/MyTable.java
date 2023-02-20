package miscalleanous.tables;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;

public class MyTable extends JXTable {
    private static final long serialVersionUID = 1L;

    public MyTable() {
        super();
    }

    public MyTable(TableModel tm) {
        super(tm);
    }

    public MyTable(TableModel tm, TableColumnModel cm) {
        super(tm, cm);
    }

    public MyTable(TableModel tm, TableColumnModel cm, ListSelectionModel sm) {
        super(tm, cm, sm);
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
