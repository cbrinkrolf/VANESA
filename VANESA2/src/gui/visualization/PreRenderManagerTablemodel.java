package gui.visualization;

import graph.GraphInstance;
import gui.LocalBackboardPaintable;
import gui.MyAnnotation;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

public class PreRenderManagerTablemodel extends AbstractTableModel {

	private static final long serialVersionUID = -7174001672428120676L;

	private String[] columnNames;
	private HashMap<Integer, Object> tablecontent;
	public Object[][] data;

	public PreRenderManagerTablemodel(Object[][] rows, String[] headers,
			HashMap<Integer, Object> tablecontent) {
		this.columnNames = headers;
		this.data = rows;
		this.tablecontent = tablecontent;
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	@Override
	public Class<?> getColumnClass(int c) {

		return getValueAt(0, c).getClass();
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;

		// determine action from column number
		// 0 : activate/deactivate visualization
		// 1 : name of visualization
		// 2 : type
		// 3 : change color of visualization
		// 4 : shape of visualization
		// 5 : size of visualization
		
		if(col == 0){
			boolean active = (boolean) value;
			if (tablecontent.get(row) instanceof LocalBackboardPaintable) {
				LocalBackboardPaintable lbp = (LocalBackboardPaintable) tablecontent
						.get(row);
				lbp.setActive(active);
			} else if (tablecontent.get(row) instanceof MyAnnotation) {
				MyAnnotation ma = (MyAnnotation) tablecontent.get(row);
				GraphInstance.getMyGraph().getAnnotationManager().setEnable(ma, active);
			}
		}else if (col == 1) {
			String name = (String) value;
			if (tablecontent.get(row) instanceof LocalBackboardPaintable) {
				LocalBackboardPaintable toname = (LocalBackboardPaintable) tablecontent
						.get(row);
				toname.setName(name);
			} else if (tablecontent.get(row) instanceof MyAnnotation) {
				MyAnnotation ma = (MyAnnotation) tablecontent.get(row);
				ma.setName(name);
			}
		}else if (col == 3) {
			Color c = (Color) value;
			if (tablecontent.get(row) instanceof LocalBackboardPaintable) {
				LocalBackboardPaintable tocolor = (LocalBackboardPaintable) tablecontent
						.get(row);
				tocolor.setBgcolor(c);
			} else if (tablecontent.get(row) instanceof MyAnnotation) {
				MyAnnotation ma = (MyAnnotation) tablecontent.get(row);
				ma.getAnnotation().setPaint(c);
			}
			
		} else if (col == 4) {
			String newshape = (String) value;
			if (tablecontent.get(row) instanceof LocalBackboardPaintable) {
				LocalBackboardPaintable toshape = (LocalBackboardPaintable) tablecontent
						.get(row);
				toshape.setShape(newshape);
			} 
		} else if(col == 5){
			int newsize = (int) value;
			if (tablecontent.get(row) instanceof LocalBackboardPaintable) {
				LocalBackboardPaintable tosize = (LocalBackboardPaintable) tablecontent
						.get(row);
				tosize.setDrawsize(newsize);
			} 
		}

		GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		fireTableCellUpdated(row, col);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		//general column permission
		if (col == 0 || col == 1 || col == 3){
			return true;
			
		//partial column permission
		} else if (col == 4) {
			if (tablecontent.get(row) instanceof LocalBackboardPaintable)
				return true;
			else
				return false;
		} else if (col == 5){
			if (tablecontent.get(row) instanceof LocalBackboardPaintable)
				return true;
			else
				return false;
		} else
			return false;
	}
}
