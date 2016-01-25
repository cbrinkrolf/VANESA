package gui.visualization;

import graph.GraphInstance;
import gui.LocalBackboardPaintable;
import gui.MyAnnotation;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.table.DefaultTableModel;

public class PreRenderManagerTablemodel extends DefaultTableModel{

	private static final long serialVersionUID = -7174001672428120676L;
	private HashMap<Integer, Object> tablecontent;

	public PreRenderManagerTablemodel(Object[][] rows, String[] headers,
			HashMap<Integer, Object> tablecontent) {
		super(rows, headers);

		this.tablecontent = tablecontent;
	}


	@Override
	public Class<?> getColumnClass(int c) {

		if(super.getRowCount()>0)
			return getValueAt(0, c).getClass();
		else
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
			if(newsize < 300 && newsize >0){
				if (tablecontent.get(row) instanceof LocalBackboardPaintable) {
					LocalBackboardPaintable tosize = (LocalBackboardPaintable) tablecontent
							.get(row);
					tosize.setDrawsize(newsize);
				}
			}else{
				valid = false;
			}
		}
		
		if(valid){
			super.setValueAt(value, row, col);

		GraphInstance.getMyGraph().getVisualizationViewer().repaint();
		fireTableCellUpdated(row, col);
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		//general column permission
		if (col == 0 || col == 1 || col == 3 || col == 4 || col == 5){
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
	
	void addTablecontent(){
		
	}


	public void updateContent(HashMap<Integer, Object> contenttmp) {
		
		tablecontent = contenttmp;		
	}
}
