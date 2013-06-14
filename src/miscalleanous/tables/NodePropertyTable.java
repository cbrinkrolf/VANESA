package miscalleanous.tables;


import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;


public class NodePropertyTable {
	
	public MyTable table; 
	
	public NodePropertyTable(Object[][] rows,String[] columNames) {
		initTable(rows,columNames);	
	}
	
	private void initTable(Object[][] rows, String[] columNames) {
		
		NodePropertyTableModel model = new NodePropertyTableModel(rows,columNames);
		CellEditors editors = new CellEditors();
		
		JCheckBox box = new JCheckBox();
		box.setSelected(true);
		
		DefaultCellEditor defaultCellEditor = new DefaultCellEditor(box);
		editors.addEditorForCell(5, 1, defaultCellEditor);
		
		table = new MyTable();
		table.setModel(model);
		table.setCellEditors(editors);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter());
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		
		
		TableColumn first = table.getColumn(0);
		if(table.getColumnCount()>1){
			TableColumn second = table.getColumn(1);
		}
		
	/*	
		int firstWidth = getPreferedWidthForColumn(first); 
		int secondWidth = getPreferedWidthForColumn(second); 
		
		first.setMinWidth(firstWidth);
		first.setMaxWidth(firstWidth);
		second.setMinWidth(secondWidth);
		*/
		table.doLayout();
		
	}
	
	private int getPreferedWidthForColumn(TableColumn col){
		
		int hw =columnHeaderWidth(col);
		int cw = widestCellInColumn(col);
		
		return hw > cw ? hw : cw;
	}
	
	private int columnHeaderWidth(TableColumn col){
		
		return col.getPreferredWidth();
	}
	
	private int widestCellInColumn(TableColumn col){
		
		int c = col.getModelIndex(), width =0, maxw=0;
		
		for(int r=0;r<table.getRowCount();++r){
			
			TableCellRenderer renderer=table.getCellRenderer(r, c);
			Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, c), false, false, r, c);
			
			width = comp.getPreferredSize().width;
			
			maxw = width >maxw ?width : maxw;
		}
		
		return maxw;
	}
	
	public JScrollPane getTable(){
		JScrollPane pane = new JScrollPane(table);
		pane.setSize(300, 200);
		pane.setAutoscrolls(true);
		return pane;
	} 
	
	public MyTable getMyTable(){
		return table;
	}
	
}
