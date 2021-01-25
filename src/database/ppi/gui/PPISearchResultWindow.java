/**
 * 
 */
package database.ppi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import gui.MainWindow;
import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import pojos.DBColumn;

public class PPISearchResultWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel panel;

	JOptionPane pane;

	JPanel panel2;

	JSpinner serchDeapth;

	JCheckBox finaliseGraph = new JCheckBox();
	JCheckBox autoCoarse = new JCheckBox();

	JCheckBox binaryInteractions = new JCheckBox();
	JCheckBox complexInteractions = new JCheckBox();

	JButton cancel = new JButton("cancel");
	JButton newButton = new JButton("ok");
	JButton[] buttons = { newButton, cancel };
	boolean ok = false;
	JOptionPane optionPane;
	JDialog dialog;
	//JCheckBox disregard = new JCheckBox();

	private MyTable table;

	private HashMap<Integer, String[]> tableContent;
	private String database;

	public PPISearchResultWindow(ArrayList<DBColumn> results, String db) {
		this.database = db;
		tableContent = new HashMap<Integer, String[]>();
		Object[][] rows = new Object[results.size()][6];
		int iterator_count = 0;

		for (DBColumn column : results) {
			String[] details = column.getColumn();

			rows[iterator_count][0] = details[0];
			rows[iterator_count][1] = details[1];
			rows[iterator_count][2] = details[2];
			rows[iterator_count][3] = details[3];

			tableContent.put(iterator_count, details);

			iterator_count++;
		}

		String[] columNames = { "Name", "Type", "Organism" };

		if (database.equals("HPRD")) {
			columNames[1] = "Gene symbol";
			columNames[2] = "Swissprot ID";
		}

		initTable(rows, columNames);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout = new MigLayout();

		JPanel mainPanel = new JPanel(layout);

		mainPanel
				.add(new JLabel(
						"Following proteins have been found. Please select the protein of interest"),
						"span 2");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		mainPanel.add(sp, "span 4, growx");

		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
		mainPanel
				.add(new JLabel(
						"What kind of settings do you wish to apply to the calculation?"),
						"span 2, wrap 15 ");

		SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1, 20, 1);
		serchDeapth = new JSpinner(model1);

		mainPanel.add(new JLabel("Search Depth"), "span 1, gaptop 2 ");
		mainPanel.add(serchDeapth, "span 1,wrap,gaptop 2");

		binaryInteractions.setSelected(true);

		if (!this.database.equals("HPRD")) {
			mainPanel.add(new JLabel("Include binary interactions"),
					"span 1, gaptop 2 ");
			mainPanel.add(binaryInteractions, "span 1,wrap,gaptop 2");
			mainPanel.add(new JLabel("Include complex interactions"),
					"span 1, gaptop 2 ");
			mainPanel.add(complexInteractions, "span 1,wrap,gaptop 2");
		}

		mainPanel.add(new JLabel("Connect nodes from the last iteration"),
				"span 1, gaptop 2 ");
		mainPanel.add(finaliseGraph, "span 1,wrap,gaptop 2");
		
		mainPanel.add(new JLabel("Coarse all results of the same query."), "span 1, gaptop 2 ");
		mainPanel.add(autoCoarse, "span 1,wrap,gaptop 2");

		// mainPanel.add(new JLabel("Disregard Elements"), "span 1, gaptop 2 ");
		// mainPanel.add(disregard, "span 1,wrap,gaptop 2");

		mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		// disregard.addActionListener(this);
		// disregard.setActionCommand("disregard");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Settings", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	// public PPISearchResultWindow(Vector v, String db) {
	//
	// this.database = db;
	// tableContent = new HashMap<Integer, String[]>();
	// Object[][] rows = new Object[v.size()][6];
	// int iterator_count = 0;
	// Iterator it = v.iterator();
	//
	// while (it.hasNext()) {
	// String[] details = (String[]) it.next();
	// rows[iterator_count][0] = details[0];
	// rows[iterator_count][1] = details[1];
	// rows[iterator_count][2] = details[2];
	// rows[iterator_count][3] = details[3];
	//
	// tableContent.put(new Integer(iterator_count), details);
	//
	// iterator_count++;
	// }
	//
	// String[] columNames = { "Name", "Type", "Organism" };
	//
	// if (database.equals("HPRD")) {
	// columNames[1] = "Gene symbol";
	// columNames[2] = "Swissprot ID";
	// }
	//
	// initTable(rows, columNames);
	// JScrollPane sp = new JScrollPane(table);
	// sp.setPreferredSize(new Dimension(800, 400));
	// MigLayout layout = new MigLayout();
	//
	// JPanel mainPanel = new JPanel(layout);
	//
	// mainPanel
	// .add(
	// new JLabel(
	// "Following proteins have been found. Please select the protein of interest"),
	// "span 2");
	// mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
	// mainPanel.add(sp, "span 4, growx");
	//
	// mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
	// mainPanel
	// .add(
	// new JLabel(
	// "What kind of settings do you wish to apply to the calculation?"),
	// "span 2, wrap 15 ");
	//
	// SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1, 20, 1);
	// serchDeapth = new JSpinner(model1);
	//
	// mainPanel.add(new JLabel("Search Depth"), "span 1, gaptop 2 ");
	// mainPanel.add(serchDeapth, "span 1,wrap,gaptop 2");
	//
	// binaryInteractions.setSelected(true);
	//
	// if (!this.database.equals("HPRD")) {
	// mainPanel.add(new JLabel("Include binary interactions"),
	// "span 1, gaptop 2 ");
	// mainPanel.add(binaryInteractions, "span 1,wrap,gaptop 2");
	// mainPanel.add(new JLabel("Include complex interactions"),
	// "span 1, gaptop 2 ");
	// mainPanel.add(complexInteractions, "span 1,wrap,gaptop 2");
	// }
	//
	// mainPanel.add(new JLabel("Connect nodes from the last iteration"),
	// "span 1, gaptop 2 ");
	// mainPanel.add(finaliseGraph, "span 1,wrap,gaptop 2");
	//
	// // mainPanel.add(new JLabel("Disregard Elements"), "span 1, gaptop 2 ");
	// // mainPanel.add(disregard, "span 1,wrap,gaptop 2");
	//
	// mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");
	//
	// cancel.addActionListener(this);
	// cancel.setActionCommand("cancel");
	//
	// newButton.addActionListener(this);
	// newButton.setActionCommand("new");
	//
	// // disregard.addActionListener(this);
	// // disregard.setActionCommand("disregard");
	//
	// optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
	// optionPane.setOptions(buttons);
	//
	// dialog = new JDialog(this, "Settings", true);
	//
	// dialog.setContentPane(optionPane);
	// dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	// }

	public Vector<String[]> getAnswer() {

		dialog.pack();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.setVisible(true);

		Vector<String[]> v = new Vector<String[]>();
		if (ok) {

			int[] selectedRows = table.getSelectedRows();
			String[] details;
			for (int i = 0; i < selectedRows.length; i++) {

				int pos = selectedRows[i];
				details = tableContent.get(pos);
				v.add(details);

			}
		}
		return v;
	}

	private void initTable(Object[][] rows, String[] columNames) {

		NodePropertyTableModel model = new NodePropertyTableModel(rows,
				columNames);

		table = new MyTable();
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnControlVisible(false);
		table.setHighlighters(HighlighterFactory.createSimpleStriping());
		table.setFillsViewportHeight(true);
		table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),
				Color.BLACK));
		table.setHorizontalScrollEnabled(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setRowSelectionInterval(0, 0);

	}

	public boolean continueProgress() {
		return ok;
	}

	public Integer getSerchDeapth() {
		return (Integer) serchDeapth.getValue();
	}

	public boolean getFinaliseGraph() {
		return finaliseGraph.isSelected();
	}
	
	public boolean getAutoCoarse() {
		return autoCoarse.isSelected();
	}

	public boolean getBinaryInteractions() {
		return binaryInteractions.isSelected();
	}

	public boolean getComplexInteractions() {
		return complexInteractions.isSelected();
	}

	// public boolean getDisregarded() {
	// return disregard.isSelected();
	// }

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			dialog.setVisible(false);
		} else if ("new".equals(event)) {
			if (table.getSelectedRows().length == 0) {
				JOptionPane.showMessageDialog(this, "Please choose an enzyme.",
						"Message", 1);
			} else if (!binaryInteractions.isSelected()
					&& !complexInteractions.isSelected()) {
				JOptionPane.showMessageDialog(this,
						"Please include at least one type of interaction.",
						"Message", 1);
			} else {
				ok = true;
				dialog.setVisible(false);
			}
		}
		// else if ("disregard".equals(event)) {
		// if (disregard.isSelected()) {
		// new BrendaPatternListWindow();
		// }
		// }
	}

}
