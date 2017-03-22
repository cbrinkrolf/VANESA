package graph.gui;

/**
 * 
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import database.brenda2.BRENDA2Search;
import gui.MainWindow;
import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import util.MyDoubleComparable;
import util.VanesaUtility;

/**
 * @author cbrinkro
 * 
 */
public class ParameterSearcher extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField ecNumber = new JTextField();
	private JTextField name = new JTextField();
	private JTextField syn = new JTextField();
	private JTextField metabolite = new JTextField();
	private JTextField org = new JTextField();

	private JButton btnUpdateEnzyme;
	private JButton btnUpdateKm;

	private JButton cancel = new JButton("cancel");
	private JButton newButton = new JButton("ok");
	private JButton[] buttons = { newButton, cancel };
	private boolean ok = false;
	private JOptionPane optionPane;
	private JPanel mainPanel;

	private JTextField enzymeFilter = new JTextField(20);
	private TableRowSorter<NodePropertyTableModel> enzymeSorter;

	private TableRowSorter<NodePropertyTableModel> kmSorter;

	private NodePropertyTableModel enzymeModel;

	private MyTable enzymeTable;
	private String[] enzymeColumnNames = { "ID", "Ec number", "Recommended name" };

	private MyTable kmTable = null;
	private String[] kmColumnNames = { "Ec number", "Organism", "Metabolite", "Value" };
	private NodePropertyTableModel kmModel;
	private JLabel kmStatistics = new JLabel();

	public static final String enzymeSearch = "enzymeSearch";
	public static final String kmSearch = "kmSearch";

	public ParameterSearcher(BiologicalNodeAbstract bna) {
		super("Brenda 2 Parameter");
		btnUpdateEnzyme = new JButton("Update possible Enzymes");
		btnUpdateEnzyme.setActionCommand("updateEnzymes");
		btnUpdateEnzyme.addActionListener(this);

		btnUpdateKm = new JButton("Update Km Values");
		btnUpdateKm.setActionCommand("updateKm");
		btnUpdateKm.addActionListener(this);

		this.ecNumber.setText(bna.getLabel());
		this.name.setText(bna.getName());
		this.syn.setText("");
		this.metabolite.setText("");
		this.org.setText("");

		this.ecNumber.setColumns(5);
		this.name.setColumns(20);
		this.syn.setColumns(20);
		this.metabolite.setColumns(20);
		this.org.setColumns(20);

		initTable(null);

		JScrollPane sp = new JScrollPane(enzymeTable);
		sp.setMinimumSize(new Dimension(100, 100));

		MigLayout layout = new MigLayout();
		mainPanel = new JPanel(layout);

		mainPanel.add(new JLabel("Following enzymes have been found. Please select the enzymes of interest"), "span 2");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
		mainPanel.add(new JLabel("Ec number:"), "span 1, gaptop 2 ");
		mainPanel.add(this.ecNumber, "span 1,wrap,gaptop 2");
		mainPanel.add(new JLabel("Name:"), "span 1, gaptop 2 ");
		mainPanel.add(this.name, "span 1,wrap,gaptop 2");
		mainPanel.add(new JLabel("Synonym:"), "span 1, gaptop 2 ");
		mainPanel.add(this.syn, "span 1,wrap,gaptop 2");
		mainPanel.add(new JLabel("Metabolite:"), "span 1, gaptop 2 ");
		mainPanel.add(this.metabolite, "span 1,wrap,gaptop 2");
		mainPanel.add(new JLabel("Organism:"), "span 1, gaptop 2 ");
		mainPanel.add(this.org, "span 1,gaptop 2");
		mainPanel.add(this.btnUpdateEnzyme, "span 1,wrap,gaptop 2");

		mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");

		mainPanel.add(sp, "span 4, growx");
		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

		metabolite.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setKmSorterListener();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		org.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setKmSorterListener();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		enzymeFilter.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (enzymeFilter.getText().trim().length() < 1) {
					// System.out.println("null");
					enzymeSorter.setRowFilter(null);
				} else {
					// System.out.println("filter: "+field.getText());
					enzymeSorter.setRowFilter(RowFilter.regexFilter(enzymeFilter.getText().trim()));
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		mainPanel.add(btnUpdateKm, "span 1, gaptop 2");
		mainPanel.add(new JLabel("Filter enzymes:"), "span 1, gaptop 2");
		mainPanel.add(enzymeFilter, "span 1, wrap");

		mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		this.setAlwaysOnTop(false);
		this.setContentPane(optionPane);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(MainWindow.getInstance());
		this.requestFocus();
		this.setVisible(true);
	}

	public Vector<String[]> getAnswer() {

		Vector<String[]> v = new Vector<String[]>();
		if (ok) {

			String tempEnzyme = "";

			int[] selectedRows = enzymeTable.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {

				String enzymes = enzymeTable.getValueAt(selectedRows[i], 0).toString();
				String organism = enzymeTable.getValueAt(selectedRows[i], 3).toString();
				String[] details = { enzymes, organism };

				if (!tempEnzyme.equals(enzymes)) {
					v.add(details);
				}
				tempEnzyme = enzymes;
			}
		}
		return v;
	}

	private void initTable(Object[][] rows) {
		// enzymeModel = new NodePropertyTableModel(rows, enzymeColumnNames);

		enzymeTable = new MyTable();
		// enzymeSorter = new
		// TableRowSorter<NodePropertyTableModel>(enzymeModel);
		// enzymeTable.setModel(enzymeModel);
		// enzymeSorter.setRowFilter(null);
		// enzymeTable.setRowSorter(enzymeSorter);

		enzymeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		enzymeTable.setColumnControlVisible(false);
		enzymeTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		enzymeTable.setFillsViewportHeight(true);
		enzymeTable.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
		enzymeTable.setHorizontalScrollEnabled(true);
		enzymeTable.getTableHeader().setReorderingAllowed(false);
		enzymeTable.getTableHeader().setResizingAllowed(true);
		// enzymeTable.setRowSelectionInterval(0, 0);

	}

	public void updateEnzymeTable(Object[][] rows) {
		enzymeModel = new NodePropertyTableModel(rows, enzymeColumnNames);
		enzymeSorter = new TableRowSorter<NodePropertyTableModel>(enzymeModel);
		enzymeTable.setModel(enzymeModel);
		enzymeSorter.setRowFilter(null);
		enzymeTable.setRowSorter(enzymeSorter);
		enzymeTable.setRowSelectionInterval(0, 0);
	}

	public void updateKmTable(Object[][] rows) {

		if (kmTable == null) {
			kmModel = new NodePropertyTableModel(rows, kmColumnNames);
			kmSorter = new TableRowSorter<NodePropertyTableModel>(kmModel);
			kmTable = new MyTable();
			// enzymeSorter = new
			// TableRowSorter<NodePropertyTableModel>(enzymeModel);
			kmTable.setModel(kmModel);
			kmSorter.setRowFilter(null);
			kmTable.setRowSorter(kmSorter);
			// organismSorter.setRowFilter(null);
			// kmTable.setRowSorter(organismSorter);
			// enzymeSorter.setRowFilter(null);
			// enzymeTable.setRowSorter(enzymeSorter);

			kmTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			kmTable.setColumnControlVisible(false);
			kmTable.setHighlighters(HighlighterFactory.createSimpleStriping());
			kmTable.setFillsViewportHeight(true);
			kmTable.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
			kmTable.setHorizontalScrollEnabled(true);
			kmTable.getTableHeader().setReorderingAllowed(false);
			kmTable.getTableHeader().setResizingAllowed(true);
			// kmTable.setRowSelectionInterval(0, 0);
			kmTable.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// System.out.println("clicked");
					setKmStatistics();
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
				}
			});

			mainPanel.add(kmStatistics, "span 12, wrap");

			JScrollPane sp = new JScrollPane(kmTable);
			sp.setPreferredSize(new Dimension(100, 400));
			mainPanel.add(sp, "growx, span 4");
			// mainPanel.repaint();
			this.setKmStatistics();
			mainPanel.revalidate();
		} else {
			// System.out.println("update");

			kmModel = new NodePropertyTableModel(rows, kmColumnNames);

			// enzymeSorter = new
			// TableRowSorter<NodePropertyTableModel>(enzymeModel);
			kmTable.setModel(kmModel);
			this.setKmStatistics();
		}
	}

	public boolean continueProgress() {
		return ok;
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			this.setVisible(false);
			MainWindow.getInstance().closeProgressBar();
		} else if ("new".equals(event)) {
			if (enzymeTable.getSelectedRows().length == 0) {
				// JOptionPane.showMessageDialog(this, "Please choose an
				// enzyme.", "Message", 1);
			} else {
				ok = true;
				this.setVisible(false);
			}
		} else if (event.equals("updateEnzymes")) {
			MainWindow.getInstance().showProgressBar("BRENDA 2 query");
			// BRENDA2Search brenda2Search = new BRENDA2Search(this,
			// BRENDA2Search.enzymeSearch);

			MainWindow.getInstance().showProgressBar("BRENDA 2 query");
			BRENDA2Search brenda2Search = new BRENDA2Search(BRENDA2Search.enzymeSearch);
			brenda2Search.setEcNumber(this.getEcNumber());
			brenda2Search.setName(this.getName());
			brenda2Search.setMetabolite(this.getMetabolite());
			brenda2Search.setOrg(this.getOrganism());
			brenda2Search.setSyn(this.getSynonym());
			brenda2Search.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getNewValue().toString().equals("DONE")) {
						updateEnzymeTable(brenda2Search.getResults());
					}
				}
			});
			brenda2Search.execute();

			// brenda2Search.execute();
		} else if (event.equals("updateKm")) {
			// System.out.println(enzymeTable.getSelectedColumn());
			MainWindow.getInstance().showProgressBar("update Km Values");
			this.ecNumber.setText(enzymeTable.getStringAt(enzymeTable.getSelectedRows()[0], 1));
			MainWindow.getInstance().showProgressBar("BRENDA 2 query");
			BRENDA2Search brenda2Search = new BRENDA2Search(BRENDA2Search.kmSearch);
			brenda2Search.setEcNumber(this.getEcNumber());
			brenda2Search.setName(this.getName());
			brenda2Search.setMetabolite(this.getMetabolite());
			brenda2Search.setOrg(this.getOrganism());
			brenda2Search.setSyn(this.getSynonym());
			brenda2Search.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getNewValue().toString().equals("DONE")) {
						updateKmTable(brenda2Search.getResults());
					}
				}
			});
			brenda2Search.execute();
			// BRENDA2Search brenda2Search = new BRENDA2Search(this,
			// BRENDA2Search.kmSearch);
			// brenda2Search.execute();
		}
	}

	private void setKmStatistics() {
		//System.out.println("new km statistics");
		List<Double> list = new ArrayList<Double>();
		// System.out.println("count: "+kmTable.getSelectedRowCount());
		for (int i = 0; i < kmTable.getRowCount(); i++) {
			if (kmTable.getSelectedRowCount() == 0 || kmTable.isRowSelected(i)) {
				list.add(Double.parseDouble((String) kmTable.getValueAt(i, 3)));
			}
		}
		// System.out.println(list.size());
		Collections.sort(list, new MyDoubleComparable());
		double mean = VanesaUtility.getMean(list);
		double median = VanesaUtility.getMedian(list);
		if (list.size() > 0) {
			this.kmStatistics.setText("n: " + list.size() + " min: " + list.get(0) + " max: " + list.get(list.size() - 1) + " mean: "
					+ Math.round(mean * 10000.0) / 10000.0 + " meadian: " + median);
		} else{
			this.kmStatistics.setText("no values");
		}
	}

	public String getEcNumber() {
		return this.ecNumber.getText().trim();
	}

	public String getName() {
		return this.name.getText().trim();
	}

	public String getSynonym() {
		return this.syn.getText().trim();
	}

	public String getMetabolite() {
		return this.metabolite.getText().trim();
	}

	public String getOrganism() {
		return this.org.getText().trim();
	}

	private void setKmSorterListener() {

		if (metabolite.getText().trim().length() < 1 && org.getText().trim().length() < 1) {
			kmSorter.setRowFilter(null);
		} else {
			List<RowFilter<NodePropertyTableModel, Integer>> listOfFilters = new ArrayList<>();
			if (metabolite.getText().trim().length() > 0) {

				listOfFilters.add(RowFilter.regexFilter(metabolite.getText().trim(), 2));
			}
			if (org.getText().trim().length() > 0) {
				listOfFilters.add(RowFilter.regexFilter(org.getText().trim(), 1));
			}
			// kmSorter.setRowFilter(RowFilter.regexFilter(metabolite.getText().trim(),2));
			// kmSorter.setRowFilter(RowFilter.regexFilter(org.getText().trim(),1));
			kmSorter.setRowFilter(RowFilter.andFilter(listOfFilters));
		}
		setKmStatistics();
	}
}
