package graph.gui;

import api.payloads.dbBrenda.DBBrendaEnzyme;
import api.payloads.dbBrenda.DBBrendaKMValue;
import api.payloads.dbBrenda.DBBrendaTurnoverNumberValue;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import database.brenda.BRENDASearch;
import graph.GraphInstance;
import gui.AsyncTaskExecutor;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.tables.GenericTableModel;
import gui.tables.MyTable;
import gui.visualization.RangeSlider;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import util.FormulaSafety;
import util.VanesaUtility;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ParameterSearcher extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JTextField ecNumber = new JTextField();
	private final JTextField name = new JTextField();
	private final JTextField syn = new JTextField();
	private final JTextField metabolite = new JTextField();
	private final JTextField org = new JTextField();

	private final JButton btnUpdateValues;

	private final JPanel mainPanel;
	private final JPanel valuesPanel;
	private final JPanel statisticPanel;

	private final JTextField enzymeFilter = new JTextField(30);
	private TableRowSorter<TableModel> enzymeSorter;

	private TableRowSorter<TableModel> valueSorter;

	private MyTable enzymeTable;
	private final JScrollPane enzymeTableScrollPane;

	private MyTable valueTable = null;
	private final String[] valueColumnNames = { "EC Number", "Organism", "Metabolite", "Value" };
	JScrollPane valueTableScrollPane;

	private TableModel valueModel;
	private final JLabel valueStatistics = new JLabel();
	private Parameter currentParameter = null;
	private final JLabel lblCurrentParameter = new JLabel();
	private final BiologicalNodeAbstract bna;
	private final Runnable onApplyCallback;

	private final JButton number = new JButton();
	private final JButton min = new JButton();
	private final JButton max = new JButton();
	private final JButton mean = new JButton();
	private final JButton median = new JButton();

	private final JRadioButton kmRadio = new JRadioButton("km");
	private final JRadioButton turnoverRadio = new JRadioButton("kcat");

	private final RangeSlider slider = new RangeSlider();

	public ParameterSearcher(BiologicalNodeAbstract bna, boolean initialSearch, Runnable onApplyCallback) {
		super("Parameter Browser");
		this.bna = bna;
		this.onApplyCallback = onApplyCallback;
		JButton btnUpdateEnzyme = new JButton("Update enzymes");
		btnUpdateEnzyme.addActionListener(e -> onUpdateEnzymesClicked());

		btnUpdateValues = new JButton("Update Values");
		btnUpdateValues.addActionListener(e -> onUpdateValuesClicked());

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

		initTable();

		enzymeTableScrollPane = new JScrollPane(enzymeTable);
		enzymeTableScrollPane.setMinimumSize(new Dimension(100, 100));

		MigLayout layout = new MigLayout();
		mainPanel = new JPanel(layout);
		valuesPanel = new JPanel(new MigLayout());
		statisticPanel = new JPanel(new MigLayout());
		valueStatistics.setForeground(Color.RED);

		updateValuesPanel();

		ButtonGroup groupType = new ButtonGroup();
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
		mainPanel.add(btnUpdateEnzyme, "span 1, gaptop 2");
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
		mainPanel.add(new JSeparator(), "span, growx, gaptop 7");

		JButton cancel = new JButton("cancel");
		cancel.addActionListener(e -> onCancelClicked());

		JButton okButton = new JButton("ok");
		okButton.addActionListener(e -> onOkClicked());

		JOptionPane optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(new JButton[] { okButton, cancel });

		setAlwaysOnTop(false);
		setContentPane(optionPane);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		pack();
		setLocationRelativeTo(MainWindow.getInstance().getFrame());
		requestFocus();
		setVisible(true);
		if (initialSearch) {
			onUpdateEnzymesClicked();
		}
	}

	private void initTable() {
		enzymeTable = new MyTable();
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
					ecNumber.setText(enzymeTable.getStringAt(enzymeTable.getSelectedRows()[0], 0));
					name.setText(enzymeTable.getStringAt(enzymeTable.getSelectedRows()[0], 1));
				}
			}
		});
	}

	public void updateEnzymeTable(DBBrendaEnzyme[] rows) {
		TableModel enzymeModel = new GenericTableModel<>(new String[] { "EC Number", "Recommended Name" }, rows) {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(DBBrendaEnzyme entry, int columnIndex) {
				if (columnIndex == 0)
					return entry.ec;
				return entry.name;
			}
		};
		enzymeSorter = new TableRowSorter<>(enzymeModel);
		enzymeTable.setModel(enzymeModel);
		enzymeSorter.setRowFilter(null);
		enzymeTable.setRowSorter(enzymeSorter);
		enzymeTable.setRowSelectionInterval(0, 0);
	}

	private void updateValueTable(String ec, DBBrendaTurnoverNumberValue[] rows) {
		updateValueTable(new GenericTableModel<>(valueColumnNames, rows) {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(DBBrendaTurnoverNumberValue entry, int columnIndex) {
				if (columnIndex == 0)
					return ec;
				if (columnIndex == 1)
					return entry.organismName;
				if (columnIndex == 2)
					return entry.metaboliteName;
				return entry.tn;
			}
		});
	}

	private void updateValueTable(String ec, DBBrendaKMValue[] rows) {
		updateValueTable(new GenericTableModel<>(valueColumnNames, rows) {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(DBBrendaKMValue entry, int columnIndex) {
				if (columnIndex == 0)
					return ec;
				if (columnIndex == 1)
					return entry.organismName;
				if (columnIndex == 2)
					return entry.metaboliteName;
				return entry.km;
			}
		});
	}

	private void updateValueTable(TableModel valueModel) {
		this.valueModel = valueModel;
		if (valueTable == null) {
			valueSorter = new TableRowSorter<>(valueModel);
			valueTable = new MyTable();
			valueTable.setModel(valueModel);
			valueSorter.setRowFilter(null);
			valueTable.setRowSorter(valueSorter);
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
			number.addActionListener(e -> onSetValueClicked(number));
			number.setToolTipText("set");

			min.setText("");
			min.setMinimumSize(new Dimension(70, min.getHeight()));
			min.addActionListener(e -> onSetValueClicked(min));
			min.setToolTipText("set");

			max.setText("");
			max.setMinimumSize(new Dimension(70, max.getHeight()));
			max.addActionListener(e -> onSetValueClicked(max));
			max.setToolTipText("set");

			mean.setText("");
			mean.setMinimumSize(new Dimension(70, mean.getHeight()));
			mean.addActionListener(e -> onSetValueClicked(mean));
			mean.setToolTipText("set");

			median.setText("");
			median.setMinimumSize(new Dimension(70, median.getHeight()));
			median.addActionListener(e -> onSetValueClicked(median));
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
			valueTableScrollPane = new JScrollPane(valueTable);
			valueTableScrollPane.setPreferredSize(new Dimension(100, 400));
			mainPanel.add(valueTableScrollPane, "growx, span 4");
			updateValueStatistics();
			mainPanel.revalidate();
		} else {
			valueSorter = new TableRowSorter<>(valueModel);
			valueTable.setModel(valueModel);
			valueSorter.setRowFilter(null);
			valueTable.setRowSorter(valueSorter);
			updateValueStatistics();
		}
	}

	private void onUpdateEnzymesClicked() {
		enzymeTableScrollPane.setVisible(true);
		AsyncTaskExecutor.runUIBlocking("BRENDA search", () -> {
			DBBrendaEnzyme[] results = BRENDASearch.searchEnzymes(getEcNumber(), getEcName(), getMetabolite(),
					getOrganism(), getSynonym());
			if (results != null && results.length > 0) {
				updateEnzymeTable(results);
			}
		});
	}

	private void onCancelClicked() {
		setVisible(false);
		MainWindow.getInstance().closeProgressBar();
	}

	private void onOkClicked() {
		if (enzymeTable.getSelectedRows().length == 0) {
			// JOptionPane.showMessageDialog(this, "Please choose an enzyme.", "Message",
			// 1);
		} else {
			setVisible(false);
			onApplyCallback.run();
		}
	}

	private void onUpdateValuesClicked() {
		if (enzymeTable.getSelectedRowCount() > 0) {
			if (enzymeTable.getSelectedRowCount() > 1) {
				PopUpDialog.getInstance().show("Multiple enzymes", "Multiselect on enzymes is not supported!");
			}
			final String ec = getEcNumber();
			AsyncTaskExecutor.runUIBlocking("BRENDA search", () -> {
				if (kmRadio.isSelected()) {
					updateValueTable(ec, BRENDASearch.requestKMValues(ec));
				} else {
					updateValueTable(ec, BRENDASearch.requestTurnoverNumberValues(ec));
				}
			});
		} else {
			PopUpDialog.getInstance().show("Empty enzyme", "Please select an enzyme first!");
		}
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		if (!event.startsWith("btn_")) {
			return;
		}
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
	}

	private void onSetValueClicked(JButton button) {
		if (currentParameter == null) {
			PopUpDialog.getInstance().show("Missing Parameter", "Select a Parameter first!");
			return;
		}
		String value = button.getText().trim();
		if (value.length() > 0) {
			try {
				currentParameter.setValue(new BigDecimal(value));
				updateValuesPanel();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
			mean.setText(String.valueOf(VanesaUtility.round(VanesaUtility.getMean(list), 4)));
			median.setText(String.valueOf(VanesaUtility.round(VanesaUtility.getMedian(list), 4)));
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
		return ecNumber.getText().trim();
	}

	public String getEcName() {
		return name.getText().trim();
	}

	public String getSynonym() {
		return syn.getText().trim();
	}

	public String getMetabolite() {
		return metabolite.getText().trim();
	}

	public String getOrganism() {
		return org.getText().trim();
	}

	private void setValueSorterListener() {
		if (valueSorter == null) {
			return;
		}
		if (metabolite.getText().trim().length() < 1 && org.getText().trim().length() < 1) {
			valueSorter.setRowFilter(null);
		} else {
			List<RowFilter<TableModel, Integer>> listOfFilters = new ArrayList<>();
			if (metabolite.getText().trim().length() > 0) {

				listOfFilters.add(RowFilter.regexFilter(metabolite.getText().trim(), 2));
			}
			if (org.getText().trim().length() > 0) {
				listOfFilters.add(RowFilter.regexFilter(org.getText().trim(), 1));
			}
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
			valuesPanel.add(new JLabel(p.getValue().toPlainString()));
		}
		valuesPanel.add(new JLabel(), "wrap");
		valuesPanel.add(new JSeparator(), "span, growx, gaptop 7");
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
			valueTable.setRowSelectionInterval(slider.getValue(), slider.getUpperValue());
		}
		updateValueStatistics();
	}
}
