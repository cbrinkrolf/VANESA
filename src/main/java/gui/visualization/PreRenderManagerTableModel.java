package gui.visualization;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.table.DefaultTableModel;

import graph.GraphInstance;
import gui.LocalBackboardPaintable;
import gui.annotation.MyAnnotation;

public class PreRenderManagerTableModel extends DefaultTableModel {
    private static final long serialVersionUID = -7174001672428120676L;
    private HashMap<Integer, Object> tableContent;

    public PreRenderManagerTableModel(Object[][] rows, String[] headers, HashMap<Integer, Object> tableContent) {
        super(rows, headers);
        this.tableContent = tableContent;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (super.getRowCount() > 0)
            return getValueAt(0, c).getClass();
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        // determine action from column number
        // 0 : activate/deactivate visualization
        // 1 : name of visualization
        // 2 : type
        // 3 : change color of visualization
        // 4 : shape of visualization
        // 5 : size of visualization
        boolean valid = true;
        if (col == 0) {
            boolean active = (boolean) value;
            if (tableContent.get(row) instanceof LocalBackboardPaintable) {
                LocalBackboardPaintable lbp = (LocalBackboardPaintable) tableContent.get(row);
                lbp.setActive(active);
            } else if (tableContent.get(row) instanceof MyAnnotation) {
                MyAnnotation ma = (MyAnnotation) tableContent.get(row);
                GraphInstance.getPathway().getGraph().getAnnotationManager().setEnable(ma, active);
            }
        } else if (col == 1) {
            String name = (String) value;
            if (tableContent.get(row) instanceof LocalBackboardPaintable) {
                LocalBackboardPaintable toname = (LocalBackboardPaintable) tableContent.get(row);
                toname.setName(name);
            } else if (tableContent.get(row) instanceof MyAnnotation) {
                MyAnnotation ma = (MyAnnotation) tableContent.get(row);
                ma.setName(name);
            }
        } else if (col == 3) {
            Color c = (Color) value;
            if (tableContent.get(row) instanceof LocalBackboardPaintable) {
                LocalBackboardPaintable tocolor = (LocalBackboardPaintable) tableContent.get(row);
                tocolor.setBgcolor(c);
            } else if (tableContent.get(row) instanceof MyAnnotation) {
                MyAnnotation ma = (MyAnnotation) tableContent.get(row);
                ma.getAnnotation().setPaint(c);
            }
        } else if (col == 4) {
            String newshape = (String) value;
            if (tableContent.get(row) instanceof LocalBackboardPaintable) {
                LocalBackboardPaintable toshape = (LocalBackboardPaintable) tableContent.get(row);
                toshape.setShape(newshape);
            }
        } else if (col == 5) {
            int newsize = (int) value;
            if (newsize < 300 && newsize > 0) {
                if (tableContent.get(row) instanceof LocalBackboardPaintable) {
                    LocalBackboardPaintable tosize = (LocalBackboardPaintable) tableContent.get(row);
                    tosize.setDrawsize(newsize);
                }
            } else {
                valid = false;
            }
        }
        if (valid) {
            super.setValueAt(value, row, col);
            fireTableCellUpdated(row, col);
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        //general column permission
        if (col == 0 || col == 1 || col == 3 || col == 4 || col == 5) {
            return true;
            //partial column permission
        } else if (col == 4) {
            return tableContent.get(row) instanceof LocalBackboardPaintable;
        } else if (col == 5) {
            return tableContent.get(row) instanceof LocalBackboardPaintable;
        }
        return false;
    }

    public void updateContent(HashMap<Integer, Object> content) {
        tableContent = content;
    }
}
