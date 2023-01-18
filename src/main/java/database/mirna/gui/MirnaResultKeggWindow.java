package database.mirna.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.decorator.ColorHighlighter;

import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import pojos.DBColumn;

public class MirnaResultKeggWindow {

	private JOptionPane pane;
	private ArrayList<String[]> map = new ArrayList<String[]>();
	private MyTable table;

	/**
	 * 
	 */

	public MirnaResultKeggWindow(ArrayList<DBColumn> result) {
		int values_count = 0;
		for (DBColumn column : result) {
			String[] resultDetails = column.getColumn();

			String mirna_name = new String("Name");
			String mirna_accession = new String("Organism");
			String mirna_sequence = new String("Sequence");

			if (resultDetails[0] != null) {
				mirna_name = resultDetails[0];
			}

			if (resultDetails[2] != null) {
				mirna_accession = resultDetails[2];
			}

			if (resultDetails[4] != null) {
				mirna_sequence = resultDetails[4];
			}

			String[] details = { mirna_name, mirna_accession, mirna_sequence };
			map.add(details);
			values_count++;
		}

		Object[][] rows = new Object[values_count][2];
		int iterator_count = 0;

		for (String[] details : map) {
			rows[iterator_count][0] = details[0];
			rows[iterator_count][1] = details[2];

			iterator_count++;
		}

		String[] columNames = { "Pathway", "Gene" };
		initTable(rows, columNames);

		JScrollPane sp = new JScrollPane(table);
		MigLayout layout = new MigLayout();
		JPanel mainPanel = new JPanel(layout);

		mainPanel.add(new JLabel(
				"The microRNA of interest takes part in the following KEGG pathways. Please select a pathway to continue."),
				"");
		mainPanel.add(new JSeparator(), "gap 10, wrap, growx");
		mainPanel.add(sp, "span 2, growx");

		pane = new JOptionPane(mainPanel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	}

	public Vector<String[]> getAnswer() {

		JDialog dialog = pane.createDialog(null, "");
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

		Integer value = (Integer) pane.getValue();
		Vector<String[]> v = new Vector<String[]>();
		String title;
		Iterator<String[]> it;
		String[] details;
		if (value != null) {
			if (value.intValue() == JOptionPane.OK_OPTION) {

				int[] selectedRows = table.getSelectedRows();
				for (int i = 0; i < selectedRows.length; i++) {

					title = table.getValueAt(selectedRows[i], 0).toString();
					it = map.iterator();
					while (it.hasNext()) {
						details = (String[]) it.next();
						if (details[0].equals(title)) {
							v.add(details);
						}
					}
				}
			}
		}
		return v;
	}

	private void initTable(Object[][] rows, String[] columNames) {

		NodePropertyTableModel model = new NodePropertyTableModel(rows, columNames);

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		table.addHighlighter(new ColorHighlighter());
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setRowSelectionInterval(0, 0);

	}
}
