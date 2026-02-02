package gui.simulation;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import configurations.Workspace;
import gui.*;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.gui.Parameter;
import petriNet.PetriNetSimulation;

public class SimMenu extends JFrame implements ItemListener {
	private static final long serialVersionUID = 7509509909627902082L;
	/**
	 * Solver for 1.26.0 stable release
	 * 
	 * To display supported solvers, either (1) run "omc --help=simulation"
	 * 
	 * or (2) check GitHub "OMCompiler/SimulationRuntime/c/util/simulation_options.c" variable "const char
	 * *SOLVER_METHOD_DESC[S_MAX]"
	 */
	private static final Map<String, String> SOLVER_TOOLTIPS = Map.ofEntries(Map.entry("dassl",
			"dassl (default) - BDF method - implicit (dense solver), variable step size control, adaptive order 1-5, event location"),
			Map.entry("ida",
					"ida - SUNDIALS IDA solver - BDF method - implicit (sparse/dense solver, default sparse) variable step size control, adaptive order 1-5, event location - additional simulation flags: -idaMaxErrorTestFails -idaMaxNonLinIters -idaMaxConvFails -idaNonLinConvCoef -idaLS -idaScaling -idaSensitivity"),
			Map.entry("cvode",
					"cvode - SUNDIALS CVODE solver - BDF or Adams-Moulton solver - implicit (dense solver), variable step-size control, adaptive order 1-12, event location - additional simulation flags -cvodeLinearMultistepMethod -cvodeNonlinearSolverIteration"),
			Map.entry("gbode",
					"gbode - generic Runge-Kutta ODE solver - implicit (sparse solver)/explicit, fixed/variable step size control, order 1-14, event location, optional bi-rate integration - additional simulation flags -gbm -gbctrl -gbratio - additional advanced flags -gbctrl_filter -gbctrl_fhr -gberr -gbint -gbnls -gbfm -gbfctrl -gbferr -gbfint -gbfnls"),
			Map.entry("euler", "euler - Euler - explicit, fixed step size, order 1"),
			Map.entry("rungekutta", "rungekutta - classical Runge-Kutta - explicit, fixed step, order 4"),
			Map.entry("symSolver",
					"symSolver - symbolic inline Solver [compiler flag '--symSolver' needed] - fixed step size, order 1"),
			Map.entry("symSolverSsc",
					"symSolverSsc - symbolic implicit Euler with step size control [compiler flag '--symSolver' needed] - step size control, order 1"),
			Map.entry("qss", "qss - A QSS solver [experimental]"),
			Map.entry("optimization", "optimization - Special solver for dynamic optimization"));
	private static final List<String> SUPPORTED_PNLIB_VERSIONS = PetriNetSimulation.SUPPORTED_PNLIB_VERSIONS;

	private final JButton start = new JButton("Start");
	private final JButton stop = new JButton("Stop");
	private final JLabel time = new JLabel("Time: -");
	private final JTextArea textArea = new JTextArea(20, 80);
	private final JPanel north = new JPanel();
	private final JPanel advancedOptionsPanel = new JPanel();
	private final JPanel parametrizedPanel = new JPanel();
	private final JPanel devOptionsPanel = new JPanel();

	private final JDecimalTextField startTxt;
	private final JDecimalTextField stopTxt;
	private final JIntTextField intervalsTxt;
	private final JLabel solversLbl = new JLabel("Solver:");

	private final JComboBox<String> solvers = new JComboBox<>();
	private final JLabel toleranceLbl = new JLabel("Tolerance:");
	private final JDecimalTextField tolerance;
	private final JLabel simLibLbl = new JLabel("Simulation library:");
	private final JComboBox<String> simLibs = new JComboBox<>();
	private int lastLibsIdx = 0;
	private final SimulationResultsListPanel simulationResultsList = new SimulationResultsListPanel();
	private final JCheckBox forceRebuild = new JCheckBox("force rebuild");

	private final JLabel seedLbl = new JLabel("Seed:");
	private final JIntTextField seedTxt;
	private final JCheckBox seedChk = new JCheckBox("random");

	private final JCheckBox advancedOptions = new JCheckBox("advanced options");
	private final JCheckBox parameterized = new JCheckBox("parameterized simulation");
	private final JCheckBox devOptions = new JCheckBox("developer options");

	private BiologicalNodeAbstract selectedNode = null;

	private final JRadioButton radioPlace = new JRadioButton("Place");
	private final JRadioButton radioTransition = new JRadioButton("Transition");

	private final JComboBox<String> selectedNodeBox = new JComboBox<>();
	private final JComboBox<String> parameterBox = new JComboBox<>();

	private final JDecimalTextField from;
	private final JDecimalTextField to;
	private final JDecimalTextField intervalSize;
	private final JTextField numbers;

	private String parameterName;

	private final JCheckBox useShortModelName = new JCheckBox("use short model name");
	private final JCheckBox useCustomExecutable = new JCheckBox("use executable:");
	private final JTextField executableTxt = new JTextField();
	private final JCheckBox useCustomEqPerFile = new JCheckBox("Eq. per file:");
	private final JIntTextField eqTxt;

	private List<File> customLibs;

	private final Pathway pw;

	public SimMenu(final Pathway pw, final ActionListener listener, final List<File> customLibs) {
		this.pw = pw;
		this.customLibs = customLibs;
		setTitle("VANESA - Simulation Setup");

		start.setActionCommand("start");
		start.addActionListener(listener);
		stop.setActionCommand("stop");
		stop.addActionListener(listener);

		startTxt = new JDecimalTextField(false);
		startTxt.setValue(new BigDecimal("0.0"));
		startTxt.setColumns(5);
		startTxt.setEnabled(false);

		stopTxt = new JDecimalTextField(false);
		stopTxt.setValue(new BigDecimal("1.0"));
		stopTxt.setColumns(5);

		intervalsTxt = new JIntTextField();
		intervalsTxt.setValue(500);
		intervalsTxt.setColumns(5);

		seedTxt = new JIntTextField();
		seedTxt.setValue(42);
		seedTxt.setColumns(5);

		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		simulationResultsList.setLogTextArea(textArea);

		solvers.setRenderer(new ToolTipListCellRenderer(SOLVER_TOOLTIPS));
		AutoCompleteDecorator.decorate(solvers);

		List<String> listSolver = new ArrayList<>();
		for (String k : SOLVER_TOOLTIPS.keySet()) {
			if (k.equals("dassl") || k.equals("cvode")) {
				continue;
			}
			listSolver.add(k);
		}
		Collections.sort(listSolver);
		solvers.addItem("dassl");
		solvers.addItem("cvode");
		for (final String s : listSolver) {
			solvers.addItem(s);
		}
		solvers.setSelectedItem("dassl");

		toleranceLbl.setToolTipText("numerical tolerance of solver, default: 1E-8");

		tolerance = new JDecimalTextField();
		tolerance.setValue(new BigDecimal("0.00000001"));
		tolerance.setColumns(10);
		tolerance.setEnabled(true);

		fillLibsComboBox();

		setLayout(new BorderLayout());
		stop.setEnabled(false);

		advancedOptions.setToolTipText("Show advanced simulation options");
		advancedOptions.addActionListener(e -> revalidateAdvancedPanel());

		if (Workspace.getCurrentSettings().isDeveloperMode()) {
			advancedOptions.setSelected(true);
		}

		devOptions.setToolTipText("Show further experimental developer simulation options");
		devOptions.addActionListener(e -> revalidateDevOptionsPanel());
		devOptions.setEnabled(advancedOptions.isSelected());

		parameterized.setToolTipText("Experimental parameterized simulation. Runs in single thread so far!");
		parameterized.addActionListener(e -> revalidateParametrizedPanel());

		seedLbl.setToolTipText("Seed for stochastic processes");

		seedChk.setToolTipText("Set random seed for stochastic processes");
		seedChk.addActionListener(e -> revalidateSeed());

		ButtonGroup selectedNodeGroup = new ButtonGroup();
		selectedNodeGroup.add(radioPlace);
		selectedNodeGroup.add(radioTransition);

		radioPlace.addActionListener(e -> fillNodeComboBox());
		radioTransition.addActionListener(e -> fillNodeComboBox());
		radioPlace.setSelected(true);

		selectedNodeBox.addItemListener(this);
		parameterBox.addItemListener(this);

		from = new JDecimalTextField();
		from.setValue(new BigDecimal("0.0"));
		from.setColumns(5);
		from.setEnabled(true);

		to = new JDecimalTextField();
		to.setValue(new BigDecimal("0.0"));
		to.setColumns(5);
		to.setEnabled(true);

		intervalSize = new JDecimalTextField();
		intervalSize.setValue(new BigDecimal("0.1"));
		intervalSize.setColumns(5);
		intervalSize.setEnabled(true);

		numbers = new JTextField();
		numbers.setColumns(5);
		numbers.setToolTipText("list of values, separated by semicolon");

		solversLbl.setToolTipText("numerical solver");
		JLabel intervalsLbl = new JLabel("Intervals:");
		intervalsLbl.setToolTipText("number of returned time steps");

		useShortModelName
				.setToolTipText("short model names lead to shorter generated file names for linking to executable");
		useCustomExecutable.setToolTipText(
				"use an existing simulation executable, all other compilation flags will be taken into account");
		useCustomExecutable.addActionListener(e -> revalidateDevOptionsPanel());
		executableTxt.setColumns(10);
		executableTxt.setToolTipText("name of the executable, including file ending");
		executableTxt.setText("_omcABC.exe");
		useCustomEqPerFile.setToolTipText(
				"override number of equations per generated file, larger number will result in less files generated");
		useCustomEqPerFile.addActionListener(e -> revalidateDevOptionsPanel());
		eqTxt = new JIntTextField();
		eqTxt.setValue(500);
		eqTxt.setColumns(5);

		JPanel controlsPanel = new JPanel();
		controlsPanel.add(start);
		controlsPanel.add(stop);
		controlsPanel.add(time);
		controlsPanel.add(new JLabel(""));
		JPanel basicOptionsPanel = new JPanel();
		basicOptionsPanel.add(new JLabel("Start:"));
		basicOptionsPanel.add(startTxt);
		basicOptionsPanel.add(new JLabel("Stop:"));
		basicOptionsPanel.add(stopTxt);
		basicOptionsPanel.add(intervalsLbl);
		basicOptionsPanel.add(intervalsTxt);

		basicOptionsPanel.add(forceRebuild);
		basicOptionsPanel.add(advancedOptions);
		basicOptionsPanel.add(devOptions);

		add(north, BorderLayout.NORTH);
		north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		north.add(controlsPanel);
		north.add(basicOptionsPanel);

		revalidateAdvancedPanel();
		updateSimulationResults();

		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
		setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(simulationResultsList, BorderLayout.WEST);
		pack();
		setLocationRelativeTo(MainWindow.getInstance().getFrame());
		setVisible(true);
	}

	public JTextArea getLogTextArea() {
		return textArea;
	}

	private void revalidateParametrizedPanel() {
		parametrizedPanel.removeAll();
		if (parameterized.isSelected()) {
			north.add(parametrizedPanel);
			parametrizedPanel.add(new JLabel("Node:"));
			parametrizedPanel.add(radioPlace);
			parametrizedPanel.add(radioTransition);
			fillNodeComboBox();
			parametrizedPanel.add(selectedNodeBox);
			parametrizedPanel.add(parameterBox);
			parametrizedPanel.add(new JLabel("from:"));
			parametrizedPanel.add(from);
			parametrizedPanel.add(new JLabel("to:"));
			parametrizedPanel.add(to);
			parametrizedPanel.add(new JLabel("interval size:"));
			parametrizedPanel.add(intervalSize);
			parametrizedPanel.add(new JLabel("values:"));
			parametrizedPanel.add(numbers);
		} else {
			parametrizedPanel.setSize(1, 1);
			north.remove(parametrizedPanel);
		}
		north.revalidate();
		pack();
	}

	private void revalidateAdvancedPanel() {
		devOptions.setEnabled(advancedOptions.isSelected());
		advancedOptionsPanel.removeAll();
		if (advancedOptions.isSelected()) {
			north.add(advancedOptionsPanel);
			advancedOptionsPanel.add(solversLbl);
			advancedOptionsPanel.add(solvers);
			advancedOptionsPanel.add(toleranceLbl);
			advancedOptionsPanel.add(tolerance);
			advancedOptionsPanel.add(simLibLbl);
			advancedOptionsPanel.add(simLibs);
			advancedOptionsPanel.add(seedLbl);
			advancedOptionsPanel.add(seedTxt);
			advancedOptionsPanel.add(seedChk);
			advancedOptionsPanel.add(parameterized);
			revalidateParametrizedPanel();
			revalidateDevOptionsPanel();
		} else {
			advancedOptionsPanel.setSize(1, 1);
			north.remove(advancedOptionsPanel);
			north.remove(parametrizedPanel);
			north.remove(devOptionsPanel);
		}
		north.revalidate();
		pack();
	}

	private void revalidateDevOptionsPanel() {
		devOptionsPanel.removeAll();
		if (advancedOptions.isSelected() && devOptions.isSelected()) {
			north.add(devOptionsPanel);
			if (parameterized.isSelected()) {
				north.remove(parametrizedPanel);
				north.add(parametrizedPanel);
			}
			devOptionsPanel.add(useShortModelName);
			devOptionsPanel.add(useCustomExecutable);
			executableTxt.setEnabled(useCustomExecutable.isSelected());
			devOptionsPanel.add(executableTxt);
			devOptionsPanel.add(useCustomEqPerFile);
			eqTxt.setEnabled(useCustomEqPerFile.isSelected());
			devOptionsPanel.add(eqTxt);
		} else {
			devOptionsPanel.setSize(1, 1);
			north.remove(devOptionsPanel);
		}
		north.revalidate();
		north.repaint();
		pack();
	}

	private void revalidateSeed() {
		seedTxt.setEnabled(!seedChk.isSelected());
	}

	private void fillNodeComboBox() {
		selectedNodeBox.removeAllItems();
		final List<BiologicalNodeAbstract> l = pw.getAllGraphNodesSortedAlphabetically();
		for (final BiologicalNodeAbstract bna : l) {
			if (bna instanceof Place && radioPlace.isSelected()) {
				selectedNodeBox.addItem(bna.getName());
			} else if (bna instanceof Transition && radioTransition.isSelected()) {
				selectedNodeBox.addItem(bna.getName());
			}
		}
		// otherwise crash, if PN only contains transitions, because radio place is preselected
		if (selectedNodeBox.getItemCount() > 0) {
			selectedNodeBox.setSelectedIndex(0);
		}
	}

	private void fillParameterComboBox() {
		if (selectedNode == null) {
			return;
		}
		parameterBox.removeAllItems();
		if (selectedNode instanceof Place) {
			parameterBox.addItem("token start");
			parameterBox.addItem("token min");
			parameterBox.addItem("token max");
		} else if (selectedNode instanceof Transition) {
			if (selectedNode.getParameters().isEmpty()) {
				return;
			}
			parameterBox.addItem("speed");
			for (int i = 0; i < selectedNode.getParameters().size(); i++) {
				parameterBox.addItem(selectedNode.getParameters().get(i).getName());
			}
		}
		parameterBox.setSelectedIndex(0);
	}

	private void fillTextFields() {
		if (parameterBox.getItemCount() < 1) {
			from.setEnabled(false);
			to.setEnabled(false);
			intervalSize.setEnabled(false);
			numbers.setEnabled(false);
			return;
		}
		from.setEnabled(true);
		to.setEnabled(true);
		intervalSize.setEnabled(true);
		numbers.setEnabled(true);
		String param = parameterBox.getSelectedItem() + "";
		if (selectedNode.getParameter(param) != null) {
			Parameter p = selectedNode.getParameter(param);
			from.setText(p.getValue().toPlainString());
		} else {
			if (selectedNode instanceof Place) {
				Place p = (Place) selectedNode;
				switch (param) {
				case "token start":
					from.setText(p.getTokenStart() + "");
					break;
				case "token min":
					from.setText(p.getTokenMin() + "");
					break;
				case "token max":
					from.setText(p.getTokenMax() + "");
					break;
				}
			} else if (selectedNode instanceof Transition) {
				// if (param.equals(speed))
			}
		}
	}

	public void started() {
		start.setEnabled(false);
		stop.setEnabled(true);
		setTime("-");
	}

	public void stopped() {
		updateSimulationResults();
		start.setEnabled(true);
		stop.setEnabled(false);
	}

	public void setTime(final String time) {
		this.time.setText(time);
		this.time.repaint();
	}

	public void addText(final String text) {
		textArea.setText(textArea.getText() + text);
		pack();
	}

	public void clearText() {
		textArea.setText("");
		pack();
	}

	public BigDecimal getStartValue() {
		return startTxt.getBigDecimalValue(BigDecimal.ZERO);
	}

	public BigDecimal getStopValue() {
		return stopTxt.getBigDecimalValue(BigDecimal.ONE);
	}

	public int getIntervals() {
		return intervalsTxt.getValue(500);
	}

	public String getSolver() {
		return (String) solvers.getSelectedItem();
	}

	public BigDecimal getTolerance() {
		return tolerance.getBigDecimalValue(new BigDecimal("0.00000001"));
	}

	public void setCustomLibs(final List<File> libs) {
		customLibs = libs;
		fillLibsComboBox();
	}

	private void fillLibsComboBox() {
		simLibs.removeAllItems();
		if (!Workspace.getCurrentSettings().isOverridePNlibPath()) {
			for (int i = 0; i < SUPPORTED_PNLIB_VERSIONS.size(); i++) {
				simLibs.addItem(
						"PNlib " + SUPPORTED_PNLIB_VERSIONS.get(i) + " (" + (i == 0 ? "default, " : "") + "built-in)");
			}
		}
		for (final File f : customLibs) {
			simLibs.addItem(f.getName() + " - " + f.getParentFile().getName());
		}
		simLibs.setSelectedIndex(simLibs.getItemCount() > lastLibsIdx ? lastLibsIdx : 0);
	}

	public boolean isBuiltInPNlibSelected() {
		return simLibs.getSelectedIndex() < SUPPORTED_PNLIB_VERSIONS.size()
				&& !Workspace.getCurrentSettings().isOverridePNlibPath();
	}

	public String getSelectedBuiltInPNLibVersion() {
		if (simLibs.getSelectedIndex() < SUPPORTED_PNLIB_VERSIONS.size()
				&& !Workspace.getCurrentSettings().isOverridePNlibPath()) {
			return SUPPORTED_PNLIB_VERSIONS.get(simLibs.getSelectedIndex());
		}
		return null;
	}

	public File getCustomPNLib() {
		if (simLibs.getSelectedIndex() < SUPPORTED_PNLIB_VERSIONS.size()
				&& !Workspace.getCurrentSettings().isOverridePNlibPath()) {
			return null;
		}
		lastLibsIdx = simLibs.getSelectedIndex();
		if (Workspace.getCurrentSettings().isOverridePNlibPath()) {
			return customLibs.get(simLibs.getSelectedIndex());
		} else {
			return customLibs.get(simLibs.getSelectedIndex() - SUPPORTED_PNLIB_VERSIONS.size());
		}
	}

	public void updateSimulationResults() {
		simulationResultsList.updateSimulationResults(pw);
		pack();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == selectedNodeBox) {
			selectedNode = pw.getNodeByName(selectedNodeBox.getSelectedItem() + "");
			fillParameterComboBox();
		} else if (e.getSource() == parameterBox) {
			fillTextFields();
			parameterName = parameterBox.getSelectedItem() + "";
		}
	}

	public boolean isForceRebuild() {
		return forceRebuild.isSelected();
	}

	public boolean isParameterized() {
		return parameterized.isSelected();
	}

	public String getParameterName() {
		return parameterName;
	}

	public List<BigDecimal> getParameterValues() {
		final List<BigDecimal> parameterValues = new ArrayList<>();
		if (numbers.getText().trim().length() > 0) {
			final String[] num = numbers.getText().split(";");
			if (num.length > 0) {
				for (final String s : num) {
					try {
						parameterValues.add(new BigDecimal(s));
					} catch (Exception e) {
						PopUpDialog.getInstance().show("Number error", "Given number is not valid: " + s);
					}
				}
			}
		} else {
			final BigDecimal start = from.getBigDecimalValue(BigDecimal.ZERO);
			final BigDecimal stop = to.getBigDecimalValue(BigDecimal.ZERO);
			if (stop.compareTo(start) < 0) {
				return parameterValues;
			}
			parameterValues.add(start);
			final BigDecimal stepSize = intervalSize.getBigDecimalValue(new BigDecimal("0.1"));
			BigDecimal sum = start;
			while (sum.add(stepSize).compareTo(stop) < 0) {
				sum = sum.add(stepSize);
				parameterValues.add(sum);
			}
			parameterValues.add(stop);
		}
		return parameterValues;
	}

	public BiologicalNodeAbstract getSelectedNode() {
		return selectedNode;
	}

	public boolean isRandomGlobalSeed() {
		return seedChk.isSelected();
	}

	public int getGlobalSeed() {
		return seedTxt.getValue(42);
	}

	public boolean isUseShortNamesSelected() {
		return advancedOptions.isSelected() && devOptions.isSelected() && useShortModelName.isSelected();
	}

	public boolean isUseCustomExecutableSelected() {
		return advancedOptions.isSelected() && devOptions.isSelected() && useCustomExecutable.isSelected();
	}

	public String getCustomExecutableName() {
		return executableTxt.getText().strip();
	}

	// TODO not in use, yet
	public boolean isEquationsPerFileSelected() {
		return advancedOptions.isSelected() && devOptions.isSelected() && useCustomEqPerFile.isSelected();
	}

	// TODO not in use, yet
	public int getCustomEquationsPerFile() {
		return eqTxt.getValue(0);
	}
}
