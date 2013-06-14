package miscalleanous.tables;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;

public class MyTable extends JXTable {

	protected CellEditors editors;

	public MyTable() {
		super();
		editors = null;
	}

	public MyTable(TableModel tm) {
		super(tm);
		editors = null;
	}

	public MyTable(TableModel tm, TableColumnModel cm) {
		super(tm, cm);
		editors = null;
	}

	public MyTable(TableModel tm, TableColumnModel cm, ListSelectionModel sm) {
		super(tm, cm, sm);
		editors = null;
	}

	public MyTable(final Object[][] rowData, final Object[] colNames)     {
	   super(rowData, colNames);
	   editors = null;
	}
	
	public void setCellEditors(CellEditors e) {
		editors = e;
	}

	public CellEditors getCellEditorModel() {
		return editors;
	}

	@Override
	public TableCellEditor getCellEditor(int row, int col) {
		
		TableCellEditor tmpEditor = null;
		if (editors != null)
			tmpEditor = editors.getEditor(row, col);
		if (tmpEditor != null)
			return tmpEditor;
		return super.getCellEditor(row, col);

	}
	
    @Override
	public Component prepareRenderer(TableCellRenderer renderer,int row, int col) {
      Component comp = super.prepareRenderer(renderer, row, col);
      JComponent jcomp = (JComponent)comp;
      if (comp == jcomp) {

    	  if(getValueAt(row, col) instanceof Integer ){
    		    
    	  }else if(getValueAt(row, col) instanceof Boolean ){
    		    
    	  } 
    	  else if(getValueAt(row, col) instanceof Double ){
    		    
    	  } 
    	  else{
    		 jcomp.setToolTipText(getValueAt(row, col).toString());
    	  }
      }
      return comp;
    }
}
