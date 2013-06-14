package miscalleanous.tables;

import java.util.Hashtable;

import javax.swing.table.TableCellEditor;

public class CellEditors {

	private Hashtable data;
	
	public CellEditors() {
		data = new Hashtable();
	}

	public void addEditorForCell(int row,int column,TableCellEditor e ){
		String key = row + ","+ column;
		data.put(key, e);
	}
	
	public void removeEditorForCell(int row, int column){
		String key = row + ","+ column;
		data.remove(key);
	}
	
	public TableCellEditor getEditor(int row,int column){
		String key = row + ","+ column;
		return (TableCellEditor)data.get(key);
	}
}
