package graph.gui;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import database.brenda2.BRENDA2Search;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MyPopUp;
import gui.visualization.RangeSlider;
import gui.tables.MyTable;
import gui.tables.NodePropertyTableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import util.FormulaSafety;
import util.VanesaUtility;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cbrinkro
 */
public class ParameterSearcher extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JTextField ecNumber = new JTextField();
	private final JTextField name = new JTextField();
	private final JTextField syn = new JTextField();
	private final JTextField metabolite = new JTextField();
	private final JTextField org = new JTextField();

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

		ecNumber.setText(bna.getLabel());
		name.setText(bna.getName());
		syn.setText("");
		metabolite.setText("");
		org.setText("");

		ecNumber.setColumns(8);
		name.setColumns(20);
		syn.setColumns(20);
		metabolite.setColumns(20);
		org.setColumns(20);

		initTable(null);

		enzymeTableScrollPane = new JScrollPane(enzymeTable);
		enzymeTableScrollPane.setMinimumSize(new Dimension(100, 100));

		MigLayout layout = new MigLayout();
		mainPanel = new JPanel(layout);
		valuesPanel = new JPanel(new MigLayout());
		statisticPanel = new JPanel(new MigLayout());
		valueStatistics.setForeground(Color.RED);

		updateValuesPanel();

		groupType.add(kmRadio);
		groupType.add(turnoverRadio);

		kmRadio.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				btnUpdateValues.setText("Update km values");
			}
		});
		kmRadio.setSelected(true);
		turnoverRadio.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				btnUpdateValues.setText("Update kcat values");
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
					enzymeSorter.setRowFilter(null);
				} else {
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
		// enzymeSorter = new TableRowSorter<NodePropertyTableModel>(enzymeModel);
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
		enzymeSorter = new TableRowSorter<>(enzymeModel);
		enzymeTable.setModel(enzymeModel);
		enzymeSorter.setRowFilter(null);
		enzymeTable.setRowSorter(enzymeSorter);
		enzymeTable.setRowSelectionInterval(0, 0);
	}

	private void updateValueTable(Object[][] rows) {

		if (valueTable == null) {
			valueModel = new NodePropertyTableModel(rows, valueColumnNames);
			valueSorter = new TableRowSorter<>(valueModel);

			valueTable = new MyTable();
			// enzymeSorter = new TableRowSorter<>(enzymeModel);
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

			valueTable.getSelectionModel().addListSelectionListener(e -> {
				// avoid multiple events
				if (e.getValueIsAdjusting()) {
					updateValueStatistics();
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

			slider.addChangeListener(e -> sliderStateChanged());

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
			valueModel = new NodePropertyTableModel(rows, valueColumnNames);
			valueSorter = new TableRowSorter<>(valueModel);
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
		BRENDA2Search brenda2Search = new BRENDA2Search(BRENDA2Search.enzymeSearch);
		brenda2Search.setEcNumber(this.getEcNumber());
		brenda2Search.setName(this.getEcName());
		brenda2Search.setMetabolite(this.getMetabolite());
		brenda2Search.setOrg(this.getOrganism());
		brenda2Search.setSyn(this.getSynonym());
		brenda2Search.addPropertyChangeListener(evt -> {
			if (evt.getNewValue().toString().equals("DONE")) {
				if (brenda2Search.getResults().length > 0) {
					updateEnzymeTable(brenda2Search.getResults());
				} else {
					// enzymeModel;
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
		brenda2Search.addPropertyChangeListener(evt -> {
			if (evt.getNewValue().toString().equals("DONE")) {
				// enzymeTableScrollPane.setVisible(false);
				// mainPanel.remove(enzymeTableScrollPane);
				updateValueTable(brenda2Search.getResults());
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
				// JOptionPane.showMessageDialog(this, "Please choose an enzyme.", "Message", 1);
			} else {
				ok = true;
				this.setVisible(false);
			}
		} else if (event.equals("updateEnzymes")) {
			enzymeTableScrollPane.setVisible(true);
			updateEnzymes();
		} else if (event.equals("updateValues")) {
			if (enzymeTable.getSelectedRowCount() > 0) {
				if (enzymeTable.getSelectedRowCount() > 1) {
					MyPopUp.getInstance().show("Multiple enzymes", "Multiselect on enzymes is not supported!");
				}
				if (kmRadio.isSelected()) {
					updateValues(BRENDA2Search.kmSearch);
				} else {
					updateValues(BRENDA2Search.turnoverSearch);
				}
			} else {
				MyPopUp.getInstance().show("Empty enzyme", "Please select an enzyme first!");
			}
		} else if (event.startsWith("btn_")) {
			JButton b = (JButton) e.getSource();
			currentParameter = bna.getParameter(event.substring(4));
			String parameter = b.getText().trim();
			lblCurrentParameter.setText(parameter);
			if (valueTable != null) {
				valueTable.getSelectionModel().clearSelection();
			}
			if (parameter.equals("v_f") || parameter.equals("v_r")) {
				turnoverRadio.setSelected(true);
				metabolite.setText("");
			} else {
				if (!metabolite.getText().trim().equals(b.getText().trim())) {
					metabolite.setText(b.getText());
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
						double d = Double.parseDouble(button.getText().trim());
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
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < Math.min(valueTable.getRowCount(), valueModel.getRowCount()); i++) {
			if (valueTable.getSelectedRowCount() == 0 || valueTable.isRowSelected(i)) {
				list.add(Double.parseDouble((String) valueTable.getValueAt(i, 3)));
			}
		}
		list.sort(Double::compare);
		if (list.size() > 0) {
			number.setText(String.valueOf(list.size()));
			min.setText(String.valueOf(list.get(0)));
			max.setText(String.valueOf(list.get(list.size() - 1)));
			double mean = VanesaUtility.getMean(list);
			this.mean.setText(String.valueOf(Math.round(mean * 10000.0) / 10000.0));
			double median = VanesaUtility.getMedian(list);
			this.median.setText(String.valueOf(Math.round(median * 10000.0) / 10000.0));
			// kmStatistics.setText("n: " + list.size() + " min: " +
			// list.get(0) + " max: " + list.get(list.size() - 1) + " mean: "
			// + Math.round(mean * 10000.0) / 10000.0 + " meadian: " + median);
			valueStatistics.setText("");
		} else {
			number.setText("");
			min.setText("");
			max.setText("");
			mean.setText("");
			median.setText("");
			valueStatistics.setText(" No values selected!");
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
				valueSorter.setRowFilter(RowFilter.andFilter(listOfFilters));
			}
		}
		updateSlider();
	}

	private void updateValuesPanel() {
		valuesPanel.removeAll();
		valuesPanel.add(new JLabel("Parameters:"));
		for (Parameter p : bna.getParameters()) {
			String name = p.getName();
			if (name.startsWith("km_")) {
				name = name.substring(3);
			}
			for (BiologicalNodeAbstract tmp : GraphInstance.getMyGraph().getJungGraph().getNeighbors(bna)) {
				String lbl = tmp.getLabel();
				if (FormulaSafety.replace(lbl).equals(name)) {
					name = lbl;
				}
			}
			JButton button = new JButton(name);
			button.addActionListener(this);
			button.setActionCommand("btn_" + p.getName());
			valuesPanel.add(button);
		}
		valuesPanel.add(new JLabel(), "wrap");
		valuesPanel.add(new JLabel("Values:"));
		for (Parameter p : bna.getParameters()) {
			valuesPanel.add(new JLabel(p.getValue() + ""));
		}
		valuesPanel.add(new JLabel(), "wrap");
		valuesPanel.add(new JSeparator(), "span, growx, gaptop 7 ");
		valuesPanel.revalidate();
	}

	private void updateSlider() {
		slider.setMinimum(0);
		slider.setMaximum(valueTable.getRowCount() - 1);
		slider.setValue(0);
		slider.setUpperValue(valueTable.getRowCount() - 1);
		slider.getUpperValue();
	}

	private void sliderStateChanged() {
		boolean all = false;
		if (slider.getValue() == 0 && slider.getUpperValue() == valueTable.getRowCount() - 1) {
			valueTable.getSelectionModel().clearSelection();
			all = true;
		}
		if (valueTable.getRowCount() > 0 && !all) {
			// valueTable.setUpdateSelectionOnSort(false);
			valueTable.setRowSelectionInterval(slider.getValue(), slider.getUpperValue());
		}
		// kmTable.select
		updateValueStatistics();
	}
}
