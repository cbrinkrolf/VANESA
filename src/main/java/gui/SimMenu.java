package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import io.SuffixAwareFilter;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import configurations.SettingsManager;
import graph.gui.Parameter;
import io.SaveDialog;
import net.miginfocom.swing.MigLayout;
import petriNet.SimulationResult;
import util.MyJFormattedTextField;
import util.MyNumberFormat;

public class SimMenu extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JLabel status = new JLabel("");
	private JLabel time = new JLabel("Time: -");
	private JTextArea textArea = new JTextArea(20, 80);
	private JPanel north = new JPanel();
	private JPanel controlsPanel = new JPanel();
	private JPanel basicOptionsPanel = new JPanel();
	private JPanel advancedOptionsPanel = new JPanel();
	private JPanel parametrizedPanel = new JPanel();
	private JScrollPane scrollPane = new JScrollPane(textArea);
	private JLabel startLbl = new JLabel("Start:");
	private JLabel stopLbl = new JLabel("Stop:");
	private JLabel intervalsLbl = new JLabel("Intervals:");
	private MyJFormattedTextField startTxt;
	private MyJFormattedTextField stopTxt;
	private MyJFormattedTextField intervalsTxt;
	private JLabel solversLbl = new JLabel("Solver:");
	private JComboBox<String> solvers;
	private JLabel toleranceLbl = new JLabel("Tolerance:");
	private MyJFormattedTextField tolerance;
	private JLabel simLibLbl = new JLabel("Simlation library:");
	private JComboBox<String> simLibs;
	private int lastLibsIdx = 0;
	private JPanel west = new JPanel();
	private JCheckBox forceRebuild = new JCheckBox("force rebuild");

	private JLabel seedLbl = new JLabel("Seed:");
	private MyJFormattedTextField seedTxt;
	private JCheckBox seedChk = new JCheckBox("random");

	private JCheckBox advancedOptions = new JCheckBox("advanced options");
	private JCheckBox parameterized = new JCheckBox("parameterized simulation");

	private BiologicalNodeAbstract selectedNode = null;

	private JRadioButton radioPlace = new JRadioButton("Place");
	private JRadioButton radioTransition = new JRadioButton("Transition");

	private ButtonGroup selectedNodeGroup = new ButtonGroup();

	private JComboBox<String> selectedNodeBox = new JComboBox<String>();
	private JComboBox<String> parameterBox = new JComboBox<String>();

	private MyJFormattedTextField from;
	private MyJFormattedTextField to;
	private MyJFormattedTextField intervalSize;
	private JTextField numbers;

	private String parameterName;
	private String parameterNameShort;
	private List<Double> parameterValues;

	private HashMap<JTextField, SimulationResult> text2sim;

	// private ActionListener listener;

	private List<String> pnLibVersions;
	private List<File> customLibs;

	private Pathway pw;

	public SimMenu(Pathway pw, ActionListener listener, List<String> pnLibVersions, List<File> customLibs) {

		this.pw = pw;
		this.setTitle("VANESA - simulation setup");
		this.pnLibVersions = pnLibVersions;
		this.customLibs = customLibs;
		// this.listener = listener;
		start.setActionCommand("start");
		start.addActionListener(listener);
		stop.setActionCommand("stop");
		stop.addActionListener(listener);

		startTxt = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		startTxt.setText("0.0");
		startTxt.setColumns(5);
		startTxt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		startTxt.setEnabled(false);

		stopTxt = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		stopTxt.setText("1.0");
		stopTxt.setColumns(5);
		stopTxt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

		intervalsTxt = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
		intervalsTxt.setText("500");
		intervalsTxt.setColumns(5);
		intervalsTxt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		// intervalsTxt.get
		// nf.parse(source)

		seedTxt = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
		seedTxt.setText("42");
		seedTxt.setColumns(5);
		seedTxt.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

		// northUp.setLayout(new GridLayout(1,5));
		// northDown.setLayout();
		solvers = new JComboBox<String>();
		Map<String, String> solverMap = getSolverToolTips();
		ToolTipListCellRenderer toolTipsRenderer = new ToolTipListCellRenderer(solverMap);
		solvers.setRenderer(toolTipsRenderer);
		AutoCompleteDecorator.decorate(solvers);

		solvers.addItem("dassl");
		solvers.addItem("cvode");

		List<String> listSolver = new ArrayList<>();
		for (String k : solverMap.keySet()) {
			if (k.equals("dassl")) {
				continue;
			} else if (k.equals("cvode")) {
				continue;
			}
			listSolver.add(k);

		}
		Collections.sort(listSolver);
		for (String s : listSolver) {
			solvers.addItem(s);
		}

		solvers.setSelectedItem("dassl");

		toleranceLbl.setToolTipText("numerical tolerance of solver, default: 1E-8");

		tolerance = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		tolerance.setText("0.00000001");
		tolerance.setColumns(10);
		tolerance.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tolerance.setEnabled(true);

		simLibs = new JComboBox<String>();
		fillLibsComboBox();

		this.setLayout(new BorderLayout());
		this.stop.setEnabled(false);

		advancedOptions.setActionCommand("advancedOptions");
		advancedOptions.setToolTipText("Show advanced simulation options");
		advancedOptions.addActionListener(this);

		if (SettingsManager.getInstance().isDeveloperMode()) {
			advancedOptions.setSelected(true);
		}

		parameterized.setActionCommand("parameterized");
		parameterized.setToolTipText("Experimental parameterized simulation. Runs in single thread so far!");
		parameterized.addActionListener(this);

		seedLbl.setToolTipText("Seed for stochastic processes");

		seedChk.setActionCommand("seed");
		seedChk.setToolTipText("Set random seed for stochastic processes");
		seedChk.addActionListener(this);

		selectedNodeGroup.add(radioPlace);
		selectedNodeGroup.add(radioTransition);

		radioPlace.addActionListener(this);
		radioPlace.setActionCommand("nodeSelected");
		radioTransition.addActionListener(this);
		radioTransition.setActionCommand("nodeSelected");
		radioPlace.setSelected(true);

		selectedNodeBox.addItemListener(this);
		parameterBox.addItemListener(this);

		from = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		from.setText("0.0");
		from.setColumns(5);
		from.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		from.setEnabled(true);

		to = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		to.setText("0.0");
		to.setColumns(5);
		to.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		to.setEnabled(true);

		intervalSize = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
		intervalSize.setText("0.1");
		intervalSize.setColumns(5);
		intervalSize.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		intervalSize.setEnabled(true);

		numbers = new JTextField();
		numbers.setColumns(5);
		numbers.setToolTipText("list of values, seperated by semicolon");

		solversLbl.setToolTipText("numerical solver");
		intervalsLbl.setToolTipText("number of returned time steps");

		controlsPanel.add(start);
		controlsPanel.add(stop);
		controlsPanel.add(time);
		controlsPanel.add(status);
		basicOptionsPanel.add(startLbl);
		basicOptionsPanel.add(startTxt);
		basicOptionsPanel.add(stopLbl);
		basicOptionsPanel.add(stopTxt);
		basicOptionsPanel.add(intervalsLbl);
		basicOptionsPanel.add(intervalsTxt);

		basicOptionsPanel.add(forceRebuild);
		basicOptionsPanel.add(advancedOptions);

		// advancedOptionsPanel.add(parametrizedPanel);

		this.add(north, BorderLayout.NORTH);
		// north.setLayout(new GridLayout(4, 1));
		north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		north.add(controlsPanel);
		north.add(basicOptionsPanel);
		// north.add(northDown);
		// north.add(parametrizedPanel);
		west.setLayout(new MigLayout());

		this.revalidateAdvancedPanel();

		this.updateSimulationResults();

		// textArea.setAutoscrolls(true);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// this.add(textArea, BorderLayout.SOUTH);
		this.add(west, BorderLayout.WEST);
		this.pack();
		// this.setLocation(w.getLocation());
		this.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		// this.setLocationRelativeTo(null);
		this.setVisible(true);

	}

	private void revalidateParametrizedPanel() {
		parametrizedPanel.removeAll();

		if (this.parameterized.isSelected()) {
			north.add(parametrizedPanel);
			parametrizedPanel.add(new JLabel("Node:"));
			parametrizedPanel.add(radioPlace);
			parametrizedPanel.add(radioTransition);
			this.fillNodeComboBox();
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
		this.pack();
	}

	private void revalidateAdvancedPanel() {
		advancedOptionsPanel.removeAll();

		if (this.advancedOptions.isSelected()) {
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

		} else {
			advancedOptionsPanel.setSize(1, 1);
			north.remove(advancedOptionsPanel);
			north.remove(parametrizedPanel);
		}
		north.revalidate();
		this.pack();
	}

	private void revalidateSeed() {
		if (this.seedChk.isSelected()) {
			seedTxt.setEnabled(false);
		} else {
			seedTxt.setEnabled(true);
		}
	}

	private void fillNodeComboBox() {

		selectedNodeBox.removeAllItems();
		List<BiologicalNodeAbstract> l = pw.getAllGraphNodesSortedAlphabetically();
		BiologicalNodeAbstract bna;
		for (int i = 0; i < l.size(); i++) {
			bna = l.get(i);

			if (bna instanceof Place && radioPlace.isSelected()) {
				selectedNodeBox.addItem(bna.getName());
			} else if (bna instanceof Transition && radioTransition.isSelected()) {
				selectedNodeBox.addItem(bna.getName());
			}
		}
		selectedNodeBox.setSelectedIndex(0);
	}

	private void fillParameterComboBox() {
		if (selectedNode == null) {
			return;
		}
		this.parameterBox.removeAllItems();
		// System.out.println(selectedNode.getName());
		if (selectedNode instanceof Place) {
			parameterBox.addItem("token start");
			parameterBox.addItem("token min");
			parameterBox.addItem("token max");

		} else if (selectedNode instanceof Transition) {
			// System.out.println(selectedNode.getParameters().size());
			if (selectedNode.getParameters().isEmpty()) {
				// System.out.println("return");
				return;
			}
			parameterBox.addItem("speed");
			for (int i = 0; i < selectedNode.getParameters().size(); i++) {
				parameterBox.addItem(selectedNode.getParameters().get(i).getName());
			}
		}
		this.parameterBox.setSelectedIndex(0);
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
			from.setText(p.getValue() + "");
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
		this.setTime("-");
	}

	public void stopped() {
		this.updateSimulationResults();
		start.setEnabled(true);
		stop.setEnabled(false);
	}

	public void setTime(String time) {
		this.time.setText(time);
		this.time.repaint();
	}

	public void addText(String text) {
		this.textArea.setText(textArea.getText() + text);
		this.pack();
	}

	public void clearText() {
		this.textArea.setText("");
		this.pack();
	}

	public double getStartValue() {
		Number number = (Number) startTxt.getValue();
		if (number != null) {
			return number.doubleValue();
		}
		return 0.0;
	}

	public double getStopValue() {
		Number number = (Number) stopTxt.getValue();
		if (number != null) {
			return number.doubleValue();
		}
		return 1.0;
	}

	public int getIntervals() {
		Number number = (Number) intervalsTxt.getValue();
		if (number != null) {
			return number.intValue();
		}
		return 500;
	}

	public String getSolver() {
		return (String) this.solvers.getSelectedItem();
	}

	public double getTolerance() {
		Number number = (Number) tolerance.getValue();
		if (number != null) {
			return number.doubleValue();
		}
		return 0.00000001;
	}

	public void setCustomLibs(List<File> libs) {
		this.customLibs = libs;
		fillLibsComboBox();
	}

	private void fillLibsComboBox() {
		simLibs.removeAllItems();
		if (!SettingsManager.getInstance().isOverridePNlibPath()) {
			String item;
			for (int i = 0; i < pnLibVersions.size(); i++) {
				if (i == 0) {
					item = "PNlib " + pnLibVersions.get(i)+" (default, built-in)";
				}else{
					item = "PNlib " + pnLibVersions.get(i)+" (built-in)";
				}
				simLibs.addItem(item);
			}
		}
		String name;
		for (File f : customLibs) {
			name = f.getName() + " - " + f.getParentFile().getName();
			simLibs.addItem(name);
		}
		if (simLibs.getItemCount() > lastLibsIdx) {
			simLibs.setSelectedIndex(lastLibsIdx);
		} else {
			simLibs.setSelectedIndex(0);
		}
	}

	public boolean isBuiltInPNlibSelected() {
		return simLibs.getSelectedIndex() < pnLibVersions.size()
				&& !SettingsManager.getInstance().isOverridePNlibPath();
	}
	
	public String getSelectedBuiltInPNLibVersion(){
		if(simLibs.getSelectedIndex() < pnLibVersions.size() && !SettingsManager.getInstance().isOverridePNlibPath()){
			return pnLibVersions.get(simLibs.getSelectedIndex());
		}
		return null;
	}

	public File getCustomPNLib() {
		if (simLibs.getSelectedIndex() < pnLibVersions.size() && !SettingsManager.getInstance().isOverridePNlibPath()) {
			return null;
		}
		// System.out.println(libs.size());
		// System.out.println(simLibs.);
		// System.out.println("lib index: "+simLibs.getSelectedIndex());
		lastLibsIdx = simLibs.getSelectedIndex();
		if (SettingsManager.getInstance().isOverridePNlibPath()) {
			return this.customLibs.get(this.simLibs.getSelectedIndex());
		} else {
			return this.customLibs.get(this.simLibs.getSelectedIndex() - pnLibVersions.size());
		}
	}

	public void updateSimulationResults() {
		west.removeAll();
		text2sim = new HashMap<JTextField, SimulationResult>();
		west.add(new JSeparator(), "growx, span, wrap");

		JCheckBox all = new JCheckBox("active");
		all.setToolTipText("de/select all");
		all.setActionCommand("-1");
		all.addItemListener(this);

		west.add(all);
		// CHRIS implement delete all action
		west.add(new JLabel("simulation name"));
		west.add(new JLabel(""));
		west.add(new JLabel(""));
		west.add(new JLabel(""));
		west.add(new JLabel(""));
		west.add(new JLabel(""), "wrap");

		List<SimulationResult> results = pw.getPetriPropertiesNet().getSimResController().getAll();

		for (int i = 0; i < results.size(); i++) {
			JCheckBox box = new JCheckBox();
			box.setActionCommand(i + "");
			box.addItemListener(this);
			box.setSelected(results.get(i).isActive());
			west.add(box);
			JButton del = new JButton("del");
			del.setToolTipText("delete result");
			del.addActionListener(this);
			del.setActionCommand("del_" + i);

			JButton log = new JButton("log");
			log.setToolTipText("show log");
			log.addActionListener(this);
			log.setActionCommand("log_" + i);

			JButton detail = new JButton("detailed");
			detail.setToolTipText("show detailed result");
			detail.addActionListener(this);
			detail.setActionCommand("detail_" + i);

			JButton export = new JButton("export");
			export.setToolTipText("export result");
			export.addActionListener(this);
			export.setActionCommand("export_" + i);
			// System.out.println("name: "+results.get(i).getName());

			JTextField simName = new JTextField(10);
			simName.setText(results.get(i).getName());
			text2sim.put(simName, results.get(i));
			simName.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					if (!simName.getText().trim().equals(text2sim.get(e.getSource()).getName())) {
						text2sim.get(e.getSource()).setName(simName.getText().trim());
					}
				}

				@Override
				public void focusGained(FocusEvent e) {
				}
			});
			west.add(simName);
			west.add(log);
			west.add(detail);
			west.add(export);
			west.add(del, "wrap");
		}
		this.pack();
		west.repaint();
		// this.setVisible(true);

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() instanceof JCheckBox) {
			JCheckBox box = (JCheckBox) e.getItem();
			int i = Integer.parseInt(box.getActionCommand());
			// System.out.println(i);
			if (i >= 0) {
				pw.getPetriPropertiesNet().getSimResController().getAll().get(i).setActive(box.isSelected());
				MainWindow.getInstance().updateSimulationResultView();
			} else {
				Component[] components = west.getComponents();
				for (int j = 0; j < components.length; j++) {
					if (components[j] instanceof JCheckBox) {
						((JCheckBox) components[j]).setSelected(box.isSelected());
					}
				}
				List<SimulationResult> resList = pw.getPetriPropertiesNet().getSimResController().getAll();
				for (int j = 0; j < resList.size(); j++) {
					resList.get(j).setActive(box.isSelected());
				}
				// this.updateSimulationResults();
			}

		} else if (e.getSource() == this.selectedNodeBox) {
			// System.out.println(e.getSource());
			// System.out.println("select");
			// System.out.println(this.selectedNodeBox.getSelectedItem());
			this.selectedNode = pw.getNodeByName(this.selectedNodeBox.getSelectedItem() + "");
			this.fillParameterComboBox();
		} else if (e.getSource() == this.parameterBox) {
			this.fillTextFields();
			this.parameterName = parameterBox.getSelectedItem() + "";
		}
		// System.out.println(((JCheckBox)e.getItem()).getActionCommand());

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e);
		if (e.getActionCommand().startsWith("del_")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			pw.getPetriPropertiesNet().getSimResController().remove(idx);
			this.updateSimulationResults();
		} else if (e.getActionCommand().startsWith("log_")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			this.textArea.setText(
					pw.getPetriPropertiesNet().getSimResController().getAll().get(idx).getLogMessage().toString());
		} else if (e.getActionCommand().startsWith("detail_")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(7));
			String simId = pw.getPetriPropertiesNet().getSimResController().getAll().get(idx).getId();
			new DetailedSimRes(pw, simId);
		} else if (e.getActionCommand().startsWith("export_")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(7));
			String simId = pw.getPetriPropertiesNet().getSimResController().getAll().get(idx).getId();
			new SaveDialog(new SuffixAwareFilter[] { SuffixAwareFilter.CSV_RESULT },
					SaveDialog.DATA_TYPE_SIMULATION_RESULTS, null, this, simId);
		} else if ("advancedOptions".equals(e.getActionCommand())) {
			revalidateAdvancedPanel();
		} else if ("parameterized".equals(e.getActionCommand())) {
			revalidateParametrizedPanel();
		} else if ("seed".equals(e.getActionCommand())) {
			revalidateSeed();
		} else if ("nodeSelected".equals(e.getActionCommand())) {
			this.fillNodeComboBox();
		}
	}

	public boolean isForceRebuild() {
		return forceRebuild.isSelected();
	}

	public boolean isParameterized() {
		return this.parameterized.isSelected();
	}

	public String getParameterName() {
		return this.parameterName;
	}

	public String getParameterNameShort() {
		return this.parameterNameShort;
	}

	public List<Double> getParameterValues() {
		// compute values
		this.parameterValues = new ArrayList<Double>();
		if (numbers.getText().trim().length() > 0) {
			String[] num = numbers.getText().split(";");
			if (num.length > 0) {
				for (int i = 0; i < num.length; i++) {
					try {
						// System.out.println(num[i]);
						parameterValues.add(Double.parseDouble(num[i]));
					} catch (Exception e) {
						PopUpDialog.getInstance().show("Number error", "Given number is not valid: " + num[i]);
					}
				}
			}
		} else {
			double start = Double.parseDouble(from.getText().trim());
			double stop = Double.parseDouble(to.getText().trim());
			double stepsize = Double.parseDouble(intervalSize.getText().trim());

			if (stop < start) {
				return parameterValues;
			}
			parameterValues.add(start);

			double sum = start;
			while (sum + stepsize < stop) {
				sum += stepsize;
				parameterValues.add(sum);
			}
			parameterValues.add(stop);
		}
		return this.parameterValues;
	}

	public BiologicalNodeAbstract getSelectedNode() {
		return this.selectedNode;
	}

	public boolean isRandomGlobalSeed() {
		return seedChk.isSelected();
	}

	public int getGlobalSeed() {
		Number number = (Number) seedTxt.getValue();
		if (number != null) {
			return number.intValue();
		}
		return 42;
	}

	public Map<String, String> getSolverToolTips() {
		// Solver for 1.20.0 stable release
		Map<String, String> map = new HashMap<>();
		map.put("euler", "euler - Euler - explicit, fixed step size, order 1");
		map.put("heun", "heun - Heun's method - explicit, fixed step, order 2");
		map.put("rungekutta", "rungekutta - classical Runge-Kutta - explicit, fixed step, order 4");
		map.put("impeuler", "impeuler - Euler - implicit, fixed step size, order 1");
		map.put("trapezoid", "trapezoid - trapezoidal rule - implicit, fixed step size, order 2");
		map.put("imprungekutta",
				"imprungekutta - Runge-Kutta methods based on Radau and Lobatto IIA - implicit, fixed step size, order 1-6(selected manually by flag -impRKOrder)");
		map.put("gbode", "gbode - generic bi-rate ODE solver - implicit, explicit, step size control, arbitrary order");
		map.put("irksco", "irksco - own developed Runge-Kutta solver - implicit, step size control, order 1-2");
		map.put("dassl", "dassl - default solver - BDF method - implicit, step size control, order 1-5");
		map.put("ida",
				"ida - SUNDIALS IDA solver - BDF method with sparse linear solver - implicit, step size control, order 1-5");
		map.put("cvode",
				"cvode - experimental implementation of SUNDIALS CVODE solver - BDF or Adams-Moulton method - step size control, order 1-12");
		map.put("rungekuttaSsc",
				"rungekuttaSsc - Runge-Kutta based on Novikov (2016) - explicit, step size control, order 4-5 [experimental]");
		map.put("qss", "qss - A QSS solver [experimental]");
		map.put("optimization", "optimization - Special solver for dynamic optimization");
		return map;
	}
}
