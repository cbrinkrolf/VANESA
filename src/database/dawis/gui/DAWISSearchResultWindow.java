package database.dawis.gui;

import gui.algorithms.ScreenSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
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
import javax.swing.border.Border;

import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * @author Olga
 * 
 */

/**
 * create the window to show the search result
 */
@SuppressWarnings("serial")
public class DAWISSearchResultWindow extends JFrame implements ActionListener {

	JDialog dialog;

	DynamicPanel dPanel;

	private MyTable table;

	JSeparator specialSeparator = new JSeparator();

	private String db, mode;

	JPanel panel, panel2, mainPanel;

	JOptionPane pane, optionPane;

	JSpinner searchDepth;

	JButton cancel = new JButton("cancel");

	JButton newButton = new JButton("ok");

	JButton[] buttons = { newButton, cancel };

	JCheckBox disregard = new JCheckBox();

	HashMap<String, Integer> map = new HashMap<String, Integer>();

	boolean ok = false;

	boolean lastSettings[] = null;

	int spinnerValueNew = 1;

	String[] details = null;

	boolean[] settings = null;
	boolean organismSpecification = false;
	
	String obj = "";

	/**
	 * create the window to show the search result for the object
	 */
	public DAWISSearchResultWindow(Vector <String[]> result, String object, String mode, boolean specification) {

		this.mode = mode;
		this.organismSpecification = specification;
		this.obj = object;
		Object[][] rows = null;
		String[] columNames = null;
			columNames = new String [4];
			columNames[0] = object+"-ID";
			columNames[1] = "Name";
			columNames[2] = "Organism";
			columNames[3] = "Database";
			rows = new Object[result.size()][4];
		if (object.equals("Disease")) {
			columNames[0] = "MIM";
		} else if (object.equals("Enzyme")) {
			columNames[0] = "EC Number";
		}

		int iterator_count = 0;
		Iterator <String[]> it = result.iterator();

		while (it.hasNext()) {

			details = it.next();
			rows[iterator_count][0] = details[0];
			rows[iterator_count][1] = details[1];
			rows[iterator_count][2] = details[2];
			rows[iterator_count][3] = details[3];

			iterator_count++;

		}

		initTable(rows, columNames);
		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(800, 400));
		MigLayout layout = new MigLayout();

		mainPanel = new JPanel(layout);

		Border border = BorderFactory.createEtchedBorder();
		mainPanel.setBorder(border);

		mainPanel.add(new JLabel("Following " + object
				+ "s have been found. Please select the " + object
				+ " of interest"), "wrap, growx, span 6");
		mainPanel.add(new JSeparator(), "span, wrap 15, growx");
		mainPanel.add(sp, "span, growx, wrap");
		
		SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1, 5, 1);
		searchDepth = new JSpinner(model1);

		if (mode.equals("Basic")) {
			mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
			mainPanel
					.add(
							new JLabel(
									"Please choose the depth of search:"),
							"span, wrap 15 ");
	
			JFormattedTextField tf = ((JSpinner.DefaultEditor) searchDepth
					.getEditor()).getTextField();
			tf.setEditable(false);

			mainPanel.add(new JLabel("Search Depth"), "span 2, gaptop 2 ");

			mainPanel.add(searchDepth, "wrap, span 2, gaptop 2");
		}

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.setBorder(border);
		
		if (mode.equals("Expert")){
			dPanel = new DynamicPanel(object, searchDepth);
			panel.add(dPanel.getPanel(object), "growy, gaptop 0");
		}
		
		panel.add(mainPanel);

		optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, object + " Settings", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	

	/**
	 * get search result
	 * 
	 * @return result
	 */
	public Vector <String[]> getAnswer() {

		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth = (int) screen.getwidth();

		dialog.pack();
		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
				(screenHeight / 2) - dialog.getSize().height / 2);
		dialog.setVisible(true);

		Vector <String[]> v = new Vector <String[]> ();
		if (ok) {

			int[] selectedRows = table.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {

				String id = table.getValueAt(selectedRows[i], 0).toString();
				String name = table.getValueAt(selectedRows[i], 1).toString();
				String[] details;
					String organism = "";
					if (table.getValueAt(selectedRows[i], 2)!=null){
						organism = table.getValueAt(selectedRows[i], 2).toString();
					} 
					details = new String [4];
					details [0] = id;
					details [1] = name; 
					details [2] = organism;
					db = table.getValueAt(selectedRows[i], 3).toString();
					details [3] = db;
			
				v.add(details);
				
			}
		}
		return v;
	}

	/**
	 * create table
	 * 
	 * @param rows
	 * @param columNames
	 */
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

	}

	// should the progress be continued?
	public boolean continueProgress() {
		return ok;
	}

	/**
	 * get the search depth
	 * 
	 * @return depth
	 */
	public Integer getSerchDeapth() {
		return (Integer) searchDepth.getValue();
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {

			dialog.setVisible(false);

		} else if ("new".equals(event)) {

			if (table.getSelectedRows().length == 0) {
				JOptionPane.showMessageDialog(this, "Please choose an entry.",
						"Message", 1);
			} else {
				if (mode.equals("Expert")){
					String query = dPanel.allOk();
					if (!query.equals("")) {
						// show dialog;
						JOptionPane.showMessageDialog(this, query, "Message", 1);
						mainPanel.repaint();
					} else {
						mainPanel.repaint();
						ok = true;
						dialog.setVisible(false);
					}
					
				} else {
					mainPanel.repaint();
					ok = true;
					dialog.setVisible(false);
				}
				
			}

		} 

	}

	public boolean[] getSettings() {
		settings = dPanel.getSettings();
		return this.settings;
	}
	
	public String getDB() {
		return db;
	}

}
