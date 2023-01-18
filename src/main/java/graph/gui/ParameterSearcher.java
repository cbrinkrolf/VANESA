package graph.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import database.brenda2.BRENDA2Search;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MyPopUp;
import gui.visualization.RangeSlider;
import miscalleanous.tables.MyTable;
import miscalleanous.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import util.FormularSafety;
import util.VanesaUtility;

/**
 * @author cbrinkro
 * 
 */
public class ParameterSearcher extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JTextField ecNumber = new JTextField();
	private JTextField name = new JTextField();
	private JTextField syn = new JTextField();
	private JTextField metabolite = new JTextField();
	private JTextField org = new JTextField();

	private JButton btnUpdateEnzyme;
	private JButton btnUpdateValues;

	private JButton cancel = new JButton("cancel");
	private JButton newButton = new JButton("ok");
	private boolean ok = false;
	private JOptionPane optionPane;
	private JPanel mainPanel;
	private JPanel valuesPanel;
	private JPanel statisticPanel;

	private JTextField enzymeFilter = new JTextField(30);
	private TableRowSorter<NodePropertyTableModel> enzymeSorter;

	private TableRowSorter<NodePropertyTableModel> valueSorter;

	private NodePropertyTableModel enzymeModel;

	private MyTable enzymeTable;
	private String[] enzymeColumnNames = { "ID", "Ec number", "Recommended name" };
	private JScrollPane enzymeTableScrollPane;

	private MyTable valueTable = null;
	private String[] valueColumnNames = { "Ec number", "Organism", "Metabolite", "Value" };
	JScrollPane valueTableScrollPane;

	private NodePropertyTableModel valueModel;
	private JLabel valueStatistics = new JLabel();
	private Parameter currentParameter = null;
	private JLabel lblCurrentParameter = new JLabel();
	private BiologicalNodeAbstract bna;

	public static final String enzymeSearch = "enzymeSearch";
	public static final String kmSearch = "kmSearch";

	private JButton number = new JButton();
	private JButton min = new JButton();
	private JButton max = new JButton();
	private JButton mean = new JButton();
	private JButton median = new JButton();

	private ButtonGroup groupType = new ButtonGroup();
	private JRadioButton kmRadio = new JRadioButton("km");
	private JRadioButton turnoverRadio = new JRadioButton("kcat");

	private RangeSlider slider = new RangeSlider();

	public ParameterSearcher(BiologicalNodeAbstract bna, boolean initialSearch) {
		super("Brenda 2 Parameter");
		this.bna = bna;

		btnUpdateEnzyme = new JButton("Update enzymes");
		btnUpdateEnzyme.setActionCommand("updateEnzymes");
		btnUpdateEnzyme.addActionListener(this);

		btnUpdateValues = new JButton("Update Values");
		btnUpdateValues.setActionCommand("updateValues");
		btnUpdateValues.addActionListener(this);

		this.ecNumber.setText(bna.getLabel());
		this.name.setText(bna.getName());
		this.syn.setText("");
		this.metabolite.setText("");
		this.org.setText("");

		this.ecNumber.setColumns(8);
		this.name.setColumns(20);
		this.syn.setColumns(20);
		this.metabolite.setColumns(20);
		this.org.setColumns(20);

		initTable(null);

		enzymeTableScrollPane = new JScrollPane(enzymeTable);
		enzymeTableScrollPane.setMinimumSize(new Dimension(100, 100));

		MigLayout layout = new MigLayout();
		mainPanel = new JPanel(layout);
		valuesPanel = new JPanel(new MigLayout());
		statisticPanel = new JPanel(new MigLayout());
		valueStatistics.setForeground(Color.RED);

		this.updateValuesPanel();

		groupType.add(kmRadio);
		groupType.add(turnoverRadio);

		kmRadio.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					btnUpdateValues.setText("Update km values");
				}
			}
		});
		kmRadio.setSelected(true);
		turnoverRadio.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					btnUpdateValues.setText("Update kcat values");
				}
			}
		});

		lblCurrentParameter.setText("not selected");
		mainPanel.add(new JLabel("Current Paremter: "), "span 1");
		mainPanel.add(lblCurrentParameter, "wrap");
		mainPanel.add(new JSeparator(), "span, wrap 15, growx");
		mainPanel.add(valuesPanel, "span, wrap");
		mainPanel.add(new JLabel("Ec number:"), "span 1, gaptop 2 ");
		mainPanel.add(this.ecNumber, "span 1, wrap, gaptop 2");
		mainPanel.add(new JLabel("Name:"), "span 1, gaptop 2 ");
		mainPanel.add(this.name, "span 1, wrap, gaptop 2");
		mainPanel.add(new JLabel("Synonym:"), "span 1, gaptop 2 ");
		mainPanel.add(this.syn, "span 1,wrap,gaptop 2");
		mainPanel.add(new JLabel("Metabolite:"), "span 1, gaptop 2 ");
		mainPanel.add(this.metabolite, "span 1,wrap,gaptop 2");
		mainPanel.add(new JLabel("Organism:"), "span 1, gaptop 2 ");
		mainPanel.add(this.org, "span 1,gaptop 2, wrap");
		mainPanel.add(this.btnUpdateEnzyme, "span 1, gaptop 2");
		mainPanel.add(btnUpdateValues, "span 1,wrap,gaptop 2");
		mainPanel.add(new JLabel("Type:"));
		mainPanel.add(kmRadio);
		mainPanel.add(turnoverRadio, "span ,wrap,gaptop 2");
		mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");

		mainPanel.add(enzymeTableScrollPane, "span 4, growx, wrap");
		mainPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

		metabolite.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				setValueSorterListener();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// System.out.println("insert");
				setValueSorterListener();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				setValueSorterListener();
			}
		});

		org.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setValueSorterListener();
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

		mainPanel.add(new JLabel("Filter enzymes:"), "gaptop 2");
		mainPanel.add(enzymeFilter, "wrap");

		mainPanel.add(new JSeparator(), "span, growx, gaptop 7 ");

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		newButton.addActionListener(this);
		newButton.setActionCommand("new");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		JButton[] b = { newButton, cancel };
		optionPane.setOptions(b);

		this.setAlwaysOnTop(false);
		this.setContentPane(optionPane);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		this.pack();
		this.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		this.requestFocus();
		this.setVisible(true);
		if (initialSearch) {
			this.updateEnzymes();
		}
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
		enzymeTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (enzymeTable.getSelectedRowCount() > 0) {
					ecNumber.setText(enzymeTable.getStringAt(enzymeTable.getSelectedRows()[0], 1));
					name.setText(enzymeTable.getStringAt(enzymeTable.getSelectedRows()[0], 2));
				}
			}
		});
	}

	public void updateEnzymeTable(Object[][] rows) {
		enzymeModel = new NodePropertyTableModel(rows, enzymeColumnNames);
		enzymeSorter = new TableRowSorter<NodePropertyTableModel>(enzymeModel);
		enzymeTable.setModel(enzymeModel);
		enzymeSorter.setRowFilter(null);
		enzymeTable.setRowSorter(enzymeSorter);
		enzymeTable.setRowSelectionInterval(0, 0);
	}

	private void updateValueTable(Object[][] rows) {

		if (valueTable == null) {
			valueModel = new NodePropertyTableModel(rows, valueColumnNames);
			valueSorter = new TableRowSorter<NodePropertyTableModel>(valueModel);

			valueTable = new MyTable();
			// enzymeSorter = new
			// TableRowSorter<NodePropertyTableModel>(enzymeModel);
			valueTable.setModel(valueModel);
			valueSorter.setRowFilter(null);

			valueTable.setRowSorter(valueSorter);
			// organismSorter.setRowFilter(null);
			// kmTable.setRowSorter(organismSorter);
			// enzymeSorter.setRowFilter(null);
			// enzymeTable.setRowSorter(enzymeSorter);

			valueTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			valueTable.setColumnControlVisible(false);
			valueTable.setHighlighters(HighlighterFactory.createSimpleStriping());
			valueTable.setFillsViewportHeight(true);
			valueTable.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
			valueTable.setHorizontalScrollEnabled(true);
			valueTable.getTableHeader().setReorderingAllowed(false);
			valueTable.getTableHeader().setResizingAllowed(true);

			valueTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					// System.out.println(e.g);
					// avoid multiple events
					if (e.getValueIsAdjusting()) {
						updateValueStatistics();
					}
				}
			});
			valueTable.setRowSelectionInterval(0, 0);
			number.setText("");
			number.setMinimumSize(new Dimension(60, number.getHeight()));
			number.addActionListener(this);
			number.setActionCommand("setValue");
			number.setToolTipText("set");

			min.setText("");
			min.setMinimumSize(new Dimension(70, min.getHeight()));
			min.addActionListener(this);
			min.setActionCommand("setValue");
			min.setToolTipText("set");

			max.setText("");
			max.setMinimumSize(new Dimension(70, max.getHeight()));
			max.addActionListener(this);
			max.setActionCommand("setValue");
			max.setToolTipText("set");

			mean.setText("");
			mean.setMinimumSize(new Dimension(70, mean.getHeight()));
			mean.addActionListener(this);
			mean.setActionCommand("setValue");
			mean.setToolTipText("set");

			median.setText("");
			median.setMinimumSize(new Dimension(70, median.getHeight()));
			median.addActionListener(this);
			median.setActionCommand("setValue");
			median.setToolTipText("set");

			slider.setPreferredSize(new Dimension(500, slider.getPreferredSize().height));

			slider.setMinimum(0);
			slider.setMaximum(valueModel.getRowCount() - 1);

			slider.setValue(0);
			slider.setUpperValue(slider.getMaximum());

			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					sliderStateChanged();
				}
			});

			statisticPanel.add(slider, "span, wrap");
			statisticPanel.add(new JLabel("n:"));
			statisticPanel.add(number);
			statisticPanel.add(new JLabel("min:"));
			statisticPanel.add(min);
			statisticPanel.add(new JLabel("max:"));
			statisticPanel.add(max);
			statisticPanel.add(new JLabel("mean:"));
			statisticPanel.add(mean);
			statisticPanel.add(new JLabel("median:"));
			statisticPanel.add(median);
			statisticPanel.add(valueStatistics);
			mainPanel.add(statisticPanel, "span, wrap");

			// mainPanel.add(kmStatistics, "span 12, wrap");

			valueTableScrollPane = new JScrollPane(valueTable);
			valueTableScrollPane.setPreferredSize(new Dimension(100, 400));
			mainPanel.add(valueTableScrollPane, "growx, span 4");
			// mainPanel.repaint();
			this.updateValueStatistics();
			mainPanel.revalidate();
		} else {
			// System.out.println("update");

			valueModel = new NodePropertyTableModel(rows, valueColumnNames);
			valueSorter = new TableRowSorter<NodePropertyTableModel>(valueModel);
			valueTable.setModel(valueModel);
			valueSorter.setRowFilter(null);
			valueTable.setRowSorter(valueSorter);
			// valueTable.setModel(valueModel);
			// valueTable.updateUI();
			this.updateValueStatistics();
		}
	}

	private void updateEnzymes() {
		MainWindow.getInstance().showProgressBar("BRENDA 2 query");
		// BRENDA2Search brenda2Search = new BRENDA2Search(this,
		// BRENDA2Search.enzymeSearch);

		MainWindow.getInstance().showProgressBar("BRENDA 2 query");
		BRENDA2Search brenda2Search = new BRENDA2Search(BRENDA2Search.enzymeSearch);
		brenda2Search.setEcNumber(this.getEcNumber());
		brenda2Search.setName(this.getEcName());
		brenda2Search.setMetabolite(this.getMetabolite());
		brenda2Search.setOrg(this.getOrganism());
		brenda2Search.setSyn(this.getSynonym());
		brenda2Search.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue().toString().equals("DONE")) {
					if (brenda2Search.getResults().length > 0) {
						updateEnzymeTable(brenda2Search.getResults());
					} else {
						// System.out.println("clear");
						// enzymeModel;
					}
				}
			}
		});
		brenda2Search.execute();
	}

	private void updateValues(String searchType) {
		MainWindow.getInstance().showProgressBar("update values");
		MainWindow.getInstance().showProgressBar("BRENDA 2 query");
		BRENDA2Search brenda2Search = new BRENDA2Search(searchType);
		brenda2Search.setEcNumber(this.getEcNumber());
		brenda2Search.setName(this.getEcName());
		brenda2Search.setMetabolite(this.getMetabolite());
		brenda2Search.setOrg(this.getOrganism());
		brenda2Search.setSyn(this.getSynonym());
		brenda2Search.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue().toString().equals("DONE")) {
					// enzymeTableScrollPane.setVisible(false);
					// mainPanel.remove(enzymeTableScrollPane);
					updateValueTable(brenda2Search.getResults());

				}
			}
		});
		brenda2Search.execute();

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
			this.enzymeTableScrollPane.setVisible(true);
			this.updateEnzymes();
		} else if (event.equals("updateValues")) {
			// System.out.println(enzymeTable.getSelectedColumn());

			if (enzymeTable.getSelectedRowCount() > 0) {
				if (enzymeTable.getSelectedRowCount() > 1) {
					MyPopUp.getInstance().show("Multiple enzymes", "Multiselect on enzymes is not supported!");
				}
				if (kmRadio.isSelected()) {

					this.updateValues(BRENDA2Search.kmSearch);
				} else {
					this.updateValues(BRENDA2Search.turnoverSearch);
				}
			} else {
				MyPopUp.getInstance().show("Empty enzyme", "Please select an enzyme first!");
			}
			// BRENDA2Search brenda2Search = new BRENDA2Search(this,
			// BRENDA2Search.kmSearch);
			// brenda2Search.execute();
		} else if (event.startsWith("btn_")) {
			// System.out.println(event.substring(3));
			JButton b = (JButton) e.getSource();

			// System.out.println(b.getText());

			this.currentParameter = bna.getParameter(event.substring(4));
			String parameter = b.getText().trim();
			this.lblCurrentParameter.setText(parameter);
			if (valueTable != null) {
				valueTable.getSelectionModel().clearSelection();
			}
			if (parameter.equals("v_f") || parameter.equals("v_r")) {
				turnoverRadio.setSelected(true);
				this.metabolite.setText("");
			} else {
				if (!metabolite.getText().trim().equals(b.getText().trim())) {
					this.metabolite.setText(b.getText());
				}
				kmRadio.setSelected(true);
			}
		} else if (event.equals("setValue")) {

			if (e.getSource() instanceof JButton) {
				JButton button = (JButton) e.getSource();
				if (this.currentParameter == null) {
					MyPopUp.getInstance().show("Missing Parameter", "Select a Parameter first!");
					return;
				}
				if (button.getText().trim().length() > 0) {
					try {
						Double d = Double.parseDouble(button.getText().trim());
						this.currentParameter.setValue(d);
						this.updateValuesPanel();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} else if (event.equals("kmRadio")) {
			btnUpdateValues.setText("Update km values");
		} else if (event.equals("turnoverRadio")) {
			btnUpdateValues.setText("Update kcat values");
		}
	}

	private void updateValueStatistics() {
		// System.out.println("new km statistics");
		List<Double> list = new ArrayList<Double>();
		// System.out.println("count: "+valueTable.getRowCount());
		for (int i = 0; i < Math.min(valueTable.getRowCount(), valueModel.getRowCount()); i++) {
			// System.out.println("i: "+i);
			if (valueTable.getSelectedRowCount() == 0 || valueTable.isRowSelected(i)) {
				list.add(Double.parseDouble((String) valueTable.getValueAt(i, 3)));
			}
		}
		// System.out.println(list.size());
		list.sort(Double::compare);
		double mean = VanesaUtility.getMean(list);
		double median = VanesaUtility.getMedian(list);
		if (list.size() > 0) {
			this.number.setText(list.size() + "");
			this.min.setText(list.get(0) + "");
			this.max.setText(list.get(list.size() - 1) + "");
			this.mean.setText(Math.round(mean * 10000.0) / 10000.0 + "");
			this.median.setText(Math.round(median * 10000.0) / 10000.0 + "");
			// System.out.println(median);
			// this.kmStatistics.setText("n: " + list.size() + " min: " +
			// list.get(0) + " max: " + list.get(list.size() - 1) + " mean: "
			// + Math.round(mean * 10000.0) / 10000.0 + " meadian: " + median);
			this.valueStatistics.setText("");
		} else {
			this.number.setText("");
			this.min.setText("");
			this.max.setText("");
			this.mean.setText("");
			this.median.setText("");
			this.valueStatistics.setText(" No values selected!");
		}
	}

	public String getEcNumber() {
		return this.ecNumber.getText().trim();
	}

	public String getEcName() {
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

	private void setValueSorterListener() {

		if (valueSorter == null) {
			return;
		}
		if (metabolite.getText().trim().length() < 1 && org.getText().trim().length() < 1) {
			valueSorter.setRowFilter(null);
		} else {
			// System.out.println("setValueSorterListener");
			List<RowFilter<NodePropertyTableModel, Integer>> listOfFilters = new ArrayList<>();
			if (metabolite.getText().trim().length() > 0) {

				listOfFilters.add(RowFilter.regexFilter(metabolite.getText().trim(), 2));
			}
			if (org.getText().trim().length() > 0) {
				listOfFilters.add(RowFilter.regexFilter(org.getText().trim(), 1));
			}
			// kmSorter.setRowFilter(RowFilter.regexFilter(metabolite.getText().trim(),2));
			// kmSorter.setRowFilter(RowFilter.regexFilter(org.getText().trim(),1));
			if (listOfFilters.size() > 0) {
				// System.out.println("before set -----------------------");
				valueSorter.setRowFilter(RowFilter.andFilter(listOfFilters));
				// System.out.println("after set -----------------------");
			}
		}
		// System.out.println("update slider finally");
		this.updateSlider();
	}

	private void updateValuesPanel() {
		valuesPanel.removeAll();
		valuesPanel.add(new JLabel("Parameters:"));
		JButton button;

		Iterator<Parameter> it = bna.getParameters().iterator();
		Parameter p;
		String name;

		while (it.hasNext()) {
			p = it.next();

			name = p.getName();
			if (name.startsWith("km_")) {
				name = name.substring(3, name.length());
			}

			BiologicalNodeAbstract tmp;
			String lbl;
			Iterator<BiologicalNodeAbstract> it2 = GraphInstance.getMyGraph().getJungGraph().getNeighbors(bna)
					.iterator();
			while (it2.hasNext()) {
				tmp = it2.next();
				lbl = tmp.getLabel();
				if (FormularSafety.replace(lbl).equals(name)) {
					name = lbl;
				}
			}

			button = new JButton(name);
			button.addActionListener(this);
			button.setActionCommand("btn_" + p.getName());
			valuesPanel.add(button);

		}
		valuesPanel.add(new JLabel(), "wrap");
		valuesPanel.add(new JLabel("Values:"));

		it = bna.getParameters().iterator();
		while (it.hasNext()) {
			p = it.next();
			valuesPanel.add(new JLabel(p.getValue() + ""));
		}
		valuesPanel.add(new JLabel(), "wrap");
		valuesPanel.add(new JSeparator(), "span, growx, gaptop 7 ");
		/*
		 * button = new JButton("v_f"); button.addActionListener(this);
		 * button.setActionCommand("btn_" + "v_f"); valuesPanel.add(button);
		 * 
		 * Iterator<BiologicalEdgeAbstract> it =
		 * GraphInstance.getMyGraph().getJungGraph().getInEdges(bna).iterator();
		 * BiologicalEdgeAbstract bea;
		 * 
		 * 
		 * while (it.hasNext()) { bea = it.next(); button = new
		 * JButton(bea.getFrom().getLabel()); button.addActionListener(this);
		 * button.setActionCommand("btn_" + "km_" +
		 * FormularSafety.replace(bea.getFrom().getName())); valuesPanel.add(button); }
		 * valuesPanel.add(new JLabel(), "wrap"); valuesPanel.add(new
		 * JLabel("Values:")); p = bna.getParameter("v_f"); // TODO possible NPE in
		 * following line if (p != null) { valuesPanel.add(new JLabel(p.getValue() +
		 * "")); } it =
		 * GraphInstance.getMyGraph().getJungGraph().getInEdges(bna).iterator(); while
		 * (it.hasNext()) { bea = it.next(); name = bea.getFrom().getName(); name =
		 * FormularSafety.replace(name); p = bna.getParameter("km_" + name);
		 * valuesPanel.add(new JLabel(p.getValue() + "")); } valuesPanel.add(new
		 * JLabel(), "wrap"); valuesPanel.add(new JSeparator(),
		 * "span, growx, gaptop 7 "); valuesPanel.add(new JLabel("Products:")); button =
		 * new JButton("v_r"); button.addActionListener(this);
		 * button.setActionCommand("btn_" + "v_r"); valuesPanel.add(button);
		 * 
		 * it = GraphInstance.getMyGraph().getJungGraph().getOutEdges(bna).iterator() ;
		 * while (it.hasNext()) { bea = it.next(); button = new
		 * JButton(bea.getTo().getLabel()); button.addActionListener(this);
		 * button.setActionCommand("btn_" + "km_" +
		 * FormularSafety.replace(bea.getTo().getName())); valuesPanel.add(button); }
		 * valuesPanel.add(new JLabel(), "wrap"); valuesPanel.add(new
		 * JLabel("Values:")); p = bna.getParameter("v_r"); if (p != null) {
		 * valuesPanel.add(new JLabel(p.getValue() + "")); } it =
		 * GraphInstance.getMyGraph().getJungGraph().getOutEdges(bna).iterator() ; while
		 * (it.hasNext()) { bea = it.next(); name = bea.getTo().getName(); name =
		 * FormularSafety.replace(name); p = bna.getParameter("km_" + name);
		 * valuesPanel.add(new JLabel(p.getValue() + "")); }
		 */

		valuesPanel.revalidate();
	}

	private void updateSlider() {
		slider.setMinimum(0);
		slider.setMaximum(valueTable.getRowCount() - 1);
		slider.setValue(0);
		slider.setUpperValue(valueTable.getRowCount() - 1);
		// System.out.println("count: "+kmTable.getRowCount());
		// System.out.println(kmTable.filt);
		slider.getUpperValue();
	}

	private void sliderStateChanged() {
		// System.out.println(slider.getValue()+" - " + slider.getUpperValue());
		boolean all = false;
		// System.out.println("upper: "+slider.getUpperValue()+" vs.
		// "+valueTable.getRowCount());
		if (slider.getValue() == 0 && slider.getUpperValue() == valueTable.getRowCount() - 1) {
			// System.out.println("maximum");
			valueTable.getSelectionModel().clearSelection();
			all = true;
		}
		if (valueTable.getRowCount() > 0 && !all) {
			// valueTable.setUpdateSelectionOnSort(false);

			valueTable.setRowSelectionInterval(slider.getValue(), slider.getUpperValue());
		}
		// kmTable.select
		this.updateValueStatistics();
	}
}
