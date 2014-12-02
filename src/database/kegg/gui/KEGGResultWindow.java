/**
 * 
 */
package database.kegg.gui;

import gui.MainWindowSingleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;

import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.decorator.ColorHighlighter;

import pojos.DBColumn;

/**
 * @author Sebastian
 * 
 */
public class KEGGResultWindow extends JFrame {
	private static final long serialVersionUID = -4080155502963037035L;

	JPanel panel;

	JOptionPane pane;

	private ArrayList<String[]> map = new ArrayList<String[]>();

	private MyTable table;

	private JCheckBox checkBox;

	public JCheckBox getCheckBox() {
		//checkBox.setSelected(false);
		return checkBox;
	}

	/**
	 * 
	 */

	int values_count = 0;

	public KEGGResultWindow(ArrayList<DBColumn> result) {

		for (DBColumn column : result) {
			String[] resultDetails = column.getColumn();

			String pathway_name = new String();
			String pathway_title = new String("map");
			String pathway_name_long = new String("map");

			if (resultDetails[0] != null) {
				pathway_name = resultDetails[0];
			}

			if (resultDetails[1] != null) {
				pathway_title = resultDetails[1];
			}

			if (resultDetails[2] != null) {
				pathway_name_long = resultDetails[2];
			}

			String[] details = { pathway_name, pathway_title, pathway_name_long };
			map.add(details);
			values_count++;
		}

		Object[][] rows = new Object[values_count][3];
		int iterator_count = 0;

		for (String[] details : map) {

			rows[iterator_count][0] = false;
			rows[iterator_count][1] = details[1];
			rows[iterator_count][2] = details[2];

			iterator_count++;
		}

		String[] columNames = { "Selected", "Title", "Organism" };
		initTable(rows, columNames);

		JScrollPane sp = new JScrollPane(table);
		MigLayout layout = new MigLayout();
		JPanel mainPanel = new JPanel(layout);
		checkBox = new JCheckBox(
				"Search MirBase/TarBase for possibly connected microRNAs", false);

		mainPanel
				.add(new JLabel(
						"Following Pathways have been found. Please select the pathways of interest"),
						"");
		mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
		mainPanel.add(sp, "span 2, growx");
		mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
		mainPanel.add(checkBox);

		pane = new JOptionPane(mainPanel, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
	}

	// public KEGGResultWindow(Vector v) {
	// int values_count=0;
	//
	// Iterator it2 = v.iterator();
	// while (it2.hasNext()) {
	//
	// String[] resultDetails=(String[])it2.next();
	//
	// String pathway_name = "";
	// String pathway_title = "map";
	// String pathway_name_long="map";
	//
	// if(resultDetails[0] !=null ){
	// pathway_name = resultDetails[0];
	// }
	//
	// if(resultDetails[1] !=null ){
	// pathway_title = resultDetails[1];
	// }
	//
	// if(resultDetails[2] !=null ){
	// pathway_name_long = resultDetails[2];
	// }
	//
	// String[] details = {pathway_name,pathway_title,pathway_name_long};
	// map.add(details);
	// values_count ++;
	// }
	//
	// Container contentPane = getContentPane();
	// Object[][] rows = new Object[values_count][2];
	// int iterator_count=0;
	// Iterator it = map.iterator();
	//
	// while(it.hasNext()){
	// String[] details = (String[])it.next();
	// rows[iterator_count][0]= details[1];
	// rows[iterator_count][1]= details[2];
	// iterator_count ++;
	// }
	//
	//
	// String[] columNames = {"Title","Organism"};
	// initTable(rows, columNames);
	// JScrollPane sp = new JScrollPane(table);
	// MigLayout layout = new MigLayout();
	// JPanel mainPanel = new JPanel(layout);
	// mainPanel.add(new
	// JLabel("Following Pathways have been found. Please select the pathways of interest"),"");
	// mainPanel.add(new JSeparator(),"gap 10, wrap, growx");
	// mainPanel.add(sp,"span 2, growx");
	//
	// pane = new JOptionPane(mainPanel, JOptionPane.QUESTION_MESSAGE,
	// JOptionPane.OK_CANCEL_OPTION);
	// }

	public Vector<String[]> getAnswer() {

		JDialog dialog = pane.createDialog(MainWindowSingleton.getInstance(), "");
		dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

		Integer value = (Integer) pane.getValue();
		if (value == null || value.equals(JOptionPane.UNINITIALIZED_VALUE)
				|| value.equals(JOptionPane.CANCEL_OPTION))
			return null;

		Vector<String[]> v = new Vector<String[]>();
		String organism;
		String title;
		Iterator<String[]> it;
		String[] details;
		if (value != null)
			for (int i = 0; i < values_count; i++) {
				if ((Boolean) table.getValueAt(table.convertRowIndexToView(i),
						table.convertColumnIndexToView(0))) {
					organism = table.getValueAt(
							table.convertRowIndexToView(i),
							table.convertColumnIndexToView(2)).toString();
					title = table.getValueAt(
							table.convertRowIndexToView(i),
							table.convertColumnIndexToView(1)).toString();
					it = map.iterator();
					while (it.hasNext()) {
						details = (String[]) it.next();
						if (details[1].equals(title)
								&& details[2].equals(organism)) {
							v.add(details);
						}
					}
				}

				// if (value.intValue() == JOptionPane.OK_OPTION) {
				//
				// int[] selectedRows = table.getSelectedRows();
				// for(int i = 0; i< selectedRows.length;i++){
				//
				// String organism = table.getValueAt(selectedRows[i],
				// 1).toString();
				// String title = table.getValueAt(selectedRows[i],
				// 0).toString();
				//
				// Iterator it = map.iterator();
				// while(it.hasNext()){
				// String[] details = (String[])it.next();
				// if (details[1].equals(title) && details[2].equals(organism)){
				// v.add(details);
				// }
				// }
				// }
				// }
			}
		return v;
	}

	private void initTable(Object[][] rows, String[] columNames) {
		NodePropertyTableModel model = new NodePropertyTableModel(rows,
				columNames) {
			/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0)
					return Boolean.class;
				else
					return String.class;
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				if (col == 0)
					return true;
				else
					return false;
			}
		};

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		// table.setHighlighters(HighlighterFactory.createSimpleStriping());
		// table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter());
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.getColumn(0).setMaxWidth(50);
		table.getColumn(columNames[0]).setCellRenderer(
				table.getDefaultRenderer(Boolean.class));
		if (values_count==1) table.setValueAt(true, 0, 0);
		
		// TableColumn first = table.getColumn(0);
		// TableColumn second = table.getColumn(1);

		// int firstWidth = getPreferedWidthForColumn(first);
		// int secondWidth = getPreferedWidthForColumn(second);

		// int firstWidth = 100;
		// int secondWidth = 50;
		// first.setMinWidth(firstWidth);
		// first.setMaxWidth(firstWidth);
		// second.setMinWidth(secondWidth);

		// table.doLayout();

	}

	// private int getPreferedWidthForColumn(TableColumn col) {
	//
	// int hw = columnHeaderWidth(col);
	// int cw = widestCellInColumn(col);
	//
	// return hw > cw ? hw : cw;
	// }

	// private int columnHeaderWidth(TableColumn col) {
	//
	// return col.getPreferredWidth();
	// }
	//
	// private int widestCellInColumn(TableColumn col) {
	//
	// int c = col.getModelIndex(), width = 0, maxw = 0;
	//
	// for (int r = 0; r < table.getRowCount(); ++r) {
	//
	// TableCellRenderer renderer = table.getCellRenderer(r, c);
	// Component comp = renderer.getTableCellRendererComponent(table,
	// table.getValueAt(r, c), false, false, r, c);
	//
	// width = comp.getPreferredSize().width;
	//
	// maxw = width > maxw ? width : maxw;
	// }
	//
	// return maxw;
	// }
}
