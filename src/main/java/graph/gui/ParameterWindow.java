package graph.gui;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import graph.ChangedFlags;
import graph.GraphInstance;
import gui.JDecimalTextField;
import gui.MainWindow;
import gui.PopUpDialog;
import net.miginfocom.swing.MigLayout;
import util.KineticBuilder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.math.BigDecimal;

public class ParameterWindow implements DocumentListener {
	private final JFrame frame = new JFrame("Parameters");
	private final JPanel panel;
	private final JTextField name = new JTextField("");
	private final Pathway pw = GraphInstance.getPathway();
	private final JDecimalTextField value = new JDecimalTextField(true);
	private final JTextField unit = new JTextField("");
	private final JButton add;
	private final GraphElementAbstract gea;
	private FormulaPanel fp;
	private boolean editMode = false;

	private JComboBox<String> kineticsComboBox;
	private JRadioButton buttonRev;
	private JRadioButton buttonIrrev;
	private boolean recentRev = false;
	private int recentComboBoxIndex = 0;
	private final boolean isFunctionBuilder;

	private static final String KINETIC_CONVENIENCE = "Convenience kinetic";
	private static final String KINETIC_LAW_OF_MASS_ACTION = "Law of mass action";

	public ParameterWindow(final GraphElementAbstract gea) {
		isFunctionBuilder = gea instanceof DynamicNode || gea instanceof BiologicalEdgeAbstract
				|| gea instanceof ContinuousTransition || gea instanceof DiscreteTransition;
		if (isFunctionBuilder) {
			frame.setTitle("Function Builder");
		}
		this.gea = gea;
		panel = new JPanel(new MigLayout("fill, wrap", "[grow]", "[][]12[]12[]12[]12[grow, top]"));
		if (isFunctionBuilder) {
			String function = "";
			if (gea instanceof DynamicNode) {
				function = ((DynamicNode) gea).getMaximalSpeed();
			} else if (gea instanceof BiologicalEdgeAbstract) {
				function = ((BiologicalEdgeAbstract) gea).getFunction();
			} else if (gea instanceof ContinuousTransition) {
				function = ((ContinuousTransition) gea).getMaximalSpeed();
			} else if (gea instanceof DiscreteTransition) {
				function = ((DiscreteTransition) gea).getDelay();
			}
			fp = new FormulaPanel(function, gea, pw, frame);
			fp.addChangedListener(this::repaintPanel);
		}
		name.getDocument().addDocumentListener(this);
		add = new JButton("add");
		add.addActionListener(e -> onAddClicked());
		JButton cancel = new JButton("cancel");
		cancel.addActionListener(e -> onCancelClicked());
		JButton okButton = new JButton("ok");
		okButton.addActionListener(e -> onOkClicked());
		repaintPanel();
		JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setPreferredSize(new Dimension(600, 600));
		optionPane.setOptions(new JButton[] { okButton, cancel });
		frame.setAlwaysOnTop(false);
		frame.setContentPane(optionPane);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		frame.revalidate();
		frame.pack();
		frame.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		frame.requestFocus();
		frame.setVisible(true);
		frame.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				frame.pack();
			}
		});
	}

	private void onOkClicked() {
		if (isFunctionBuilder) {
			String formula = fp.getFormula();
			String formulaClean = formula.replaceAll("\\s", "");
			boolean changed = false;
			if (gea instanceof DynamicNode) {
				DynamicNode dn = (DynamicNode) gea;
				String orgClean = dn.getMaximalSpeed().replaceAll("\\s", "");
				if (!orgClean.equals(formulaClean)) {
					dn.setMaximalSpeed(formula);
					changed = true;
				}
			} else if (gea instanceof BiologicalEdgeAbstract) {
				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) gea;
				String orgClean = bea.getFunction().replaceAll("\\s", "");
				if (!orgClean.equals(formulaClean)) {
					bea.setFunction(formula);
					changed = true;
				}
			} else if (gea instanceof ContinuousTransition) {
				ContinuousTransition t = (ContinuousTransition) gea;
				String orgClean = t.getMaximalSpeed().replaceAll("\\s", "");
				if (!orgClean.equals(formulaClean)) {
					t.setMaximalSpeed(formula);
					changed = true;
				}
			} else if (gea instanceof DiscreteTransition) {
				DiscreteTransition t = (DiscreteTransition) gea;
				String orgClean = t.getDelay().replaceAll("\\s", "");
				if (!orgClean.equals(formulaClean)) {
					t.setDelay(formula);
					changed = true;
				}
			}
			if (changed) {
				pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				MainWindow.getInstance().updateElementProperties();
			}
		}
		frame.setVisible(false);
	}

	private void onCancelClicked() {
		frame.setVisible(false);
	}

	private void onAddClicked() {
		// override old parameter
		for (int i = 0; i < gea.getParameters().size(); i++) {
			Parameter p = gea.getParameters().get(i);
			if (p.getName().equals(name.getText())) {
				if (editMode && name.getText().trim().length() > 0) {
					try {
						// unit did not change
						if (p.getUnit().equals(unit.getText().trim())) {
							if (p.getValue().compareTo(value.getBigDecimalValue(BigDecimal.ZERO)) == 0) {
								return;
							} else {
								p.setValue(value.getBigDecimalValue(BigDecimal.ZERO));
								pw.handleChangeFlags(ChangedFlags.PARAMETER_CHANGED);
								pw.getChangedParameters().put(p, gea);
							}
						} else {
							// unit changed
							p.setValue(value.getBigDecimalValue(BigDecimal.ZERO));
							p.setUnit(unit.getText().trim());
							pw.handleChangeFlags(ChangedFlags.NODE_CHANGED);
						}
						editMode = false;
						add.setText("add");
						repaintPanel();
					} catch (NumberFormatException nfe) {
						PopUpDialog.getInstance().show("Parameter",
								"Parameter not correct. Value not a number or empty?");
					}
				} else {
					PopUpDialog.getInstance().show("Parameter",
							"Parameter with same name already exists! Use edit button to edit parameter");
				}
				return;
			}
		}
		// add new parameter
		if (name.getText().trim().length() > 0) {
			try {
				Parameter p = new Parameter(name.getText().trim(), value.getBigDecimalValue(BigDecimal.ZERO),
						unit.getText().trim());
				gea.getParameters().add(p);
				pw.handleChangeFlags(ChangedFlags.NODE_CHANGED);
				panel.add(new JLabel(name.getText()), "span 1, gaptop 2 ");
				panel.add(new JLabel(value.getText()), "span 1, gapright 4");
				panel.add(new JLabel(unit.getText()), "span 1, gapright 4, wrap");
				repaintPanel();
			} catch (NumberFormatException nfx) {
				PopUpDialog.getInstance().show("Parameter", "Parameter not correct. Value not a number or empty?");
			}
		} else {
			PopUpDialog.getInstance().show("Parameter", "Name is empty!");
		}
	}

	private void onGenerateKineticClicked() {
		generateKineticEquation();
		repaintPanel();
	}

	private void onSetValuesClicked() {
		if (isFunctionBuilder) {
			new ParameterSearcher((BiologicalNodeAbstract) gea, true, this::repaintPanel);
		}
	}

	private void listParameters() {
		final JPanel parametersListPanel = new JPanel(new MigLayout("ins 0, fill, wrap 7", "[][grow][][][][][]"));
		parametersListPanel.add(new JLabel("Name"));
		parametersListPanel.add(new JLabel("Value"));
		parametersListPanel.add(new JLabel("Unit"), "wrap");
		for (int i = 0; i < gea.getParameters().size(); i++) {
			final int parameterIdx = i;
			Parameter p = gea.getParameters().get(i);
			parametersListPanel.add(new JLabel(p.getName()));
			parametersListPanel.add(new JLabel(p.getValue().toPlainString()));
			parametersListPanel.add(new JLabel(p.getUnit()));

			JButton edit = new JButton("✎");
			edit.addActionListener(e -> onParameterEditClicked(parameterIdx));
			edit.setToolTipText("edit entry");
			edit.setMaximumSize(edit.getMinimumSize());

			JButton del = new JButton("✖");
			del.setBackground(Color.RED);
			del.addActionListener(e -> onParameterDeleteClicked(parameterIdx));
			del.setToolTipText("delete entry");

			JButton up = new JButton("↑");
			up.addActionListener(e -> onParameterUpClicked(parameterIdx));
			up.setToolTipText("move up");

			JButton down = new JButton("↓");
			down.addActionListener(e -> onParameterDownClicked(parameterIdx));
			down.setToolTipText("move down");
			up.setEnabled(i > 0);
			down.setEnabled(i < gea.getParameters().size() - 1);
			parametersListPanel.add(up);
			parametersListPanel.add(down);
			parametersListPanel.add(edit);
			parametersListPanel.add(del);
		}
		panel.add(new JScrollPane(parametersListPanel), "growx");
	}

	private void onParameterEditClicked(final int idx) {
		Parameter p = gea.getParameters().get(idx);
		name.setText(p.getName());
		value.setValue(p.getValue());
		unit.setText(p.getUnit());
		add.setText("Override");
		editMode = true;
		repaintPanel();
	}

	private void onParameterDeleteClicked(final int idx) {
		pw.handleChangeFlags(ChangedFlags.PARAMETER_CHANGED);
		pw.getChangedParameters().remove(gea.getParameters().get(idx));
		gea.getParameters().remove(idx);
		repaintPanel();
	}

	private void onParameterUpClicked(final int idx) {
		Parameter p = gea.getParameters().get(idx);
		gea.getParameters().set(idx, gea.getParameters().get(idx - 1));
		gea.getParameters().set(idx - 1, p);
		repaintPanel();
	}

	private void onParameterDownClicked(final int idx) {
		Parameter p = gea.getParameters().get(idx);
		gea.getParameters().set(idx, gea.getParameters().get(idx + 1));
		gea.getParameters().set(idx + 1, p);
		repaintPanel();
	}

	private void repaintPanel() {
		panel.removeAll();
		if (isFunctionBuilder) {
			panel.add(fp, "growx");
		}
		if (gea instanceof DynamicNode || gea instanceof ContinuousTransition) {
			JPanel kineticsPanel = new JPanel(new MigLayout("ins 0, fill, wrap 4", "[grow][][][]"));
			kineticsComboBox = new JComboBox<>();
			kineticsComboBox.addItem(KINETIC_CONVENIENCE);
			kineticsComboBox.addItem(KINETIC_LAW_OF_MASS_ACTION);

			AutoCompleteDecorator.decorate(kineticsComboBox);
			kineticsComboBox.setSelectedIndex(recentComboBoxIndex);
			kineticsPanel.add(kineticsComboBox, "");

			ButtonGroup group = new ButtonGroup();
			buttonRev = new JRadioButton("reversible");
			buttonRev.setSelected(recentRev);
			buttonIrrev = new JRadioButton("irreversible");
			buttonIrrev.setSelected(!recentRev);
			group.add(buttonRev);
			group.add(buttonIrrev);
			kineticsPanel.add(buttonRev);
			kineticsPanel.add(buttonIrrev);

			JButton generateKinetic = new JButton("generate");
			generateKinetic.setToolTipText("Generates equation for selected kinetic");
			generateKinetic.addActionListener(e -> onGenerateKineticClicked());
			kineticsPanel.add(generateKinetic);
			JButton setValues = new JButton("Open kinetic parameters browser");
			setValues.setToolTipText("Browse kinetic parameters (km and kcat), currently based on BRENDA");
			setValues.addActionListener(e -> onSetValuesClicked());
			kineticsPanel.add(setValues);
			panel.add(kineticsPanel, "growx");
		}
		panel.add(new JSeparator(), "growx");

		final JPanel newParameterPanel = new JPanel(new MigLayout("ins 0, fill, wrap 2", "[][grow]"));
		newParameterPanel.add(new JLabel("Name:"));
		newParameterPanel.add(name, "growx");
		newParameterPanel.add(new JLabel("Value:"));
		newParameterPanel.add(value, "growx");
		newParameterPanel.add(new JLabel("Unit:"));
		newParameterPanel.add(unit, "growx");
		newParameterPanel.add(add, "span 2");
		panel.add(newParameterPanel, "growx");
		panel.add(new JSeparator(), "growx");
		listParameters();
		panel.repaint();
		frame.pack();
	}

	@Override
	public void insertUpdate(final DocumentEvent e) {
		handleChangedName();
	}

	@Override
	public void removeUpdate(final DocumentEvent e) {
		handleChangedName();
	}

	@Override
	public void changedUpdate(final DocumentEvent e) {
	}

	private void handleChangedName() {
		if (editMode) {
			editMode = false;
			add.setText("add");
		}
	}

	private void generateKineticEquation() {
		if (gea instanceof DynamicNode || gea instanceof ContinuousTransition) {
			String kinetic = kineticsComboBox.getSelectedItem().toString();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) gea;
			String equation = "";
			switch (kinetic) {
			case KINETIC_CONVENIENCE:
				if (buttonRev.isSelected()) {
					equation = KineticBuilder.createConvenienceKineticReversible(bna, null);
				} else if (buttonIrrev.isSelected()) {
					equation = KineticBuilder.createConvenienceKineticIrreversible(bna, null);
				}
				break;
			case KINETIC_LAW_OF_MASS_ACTION:
				if (buttonRev.isSelected()) {
					equation = KineticBuilder.createLawOfMassActionKineticReversible(bna, null);
				} else if (buttonIrrev.isSelected()) {
					equation = KineticBuilder.createLawOfMassActionKineticIrreversible(bna, null);
				}
				break;
			}
			recentRev = buttonRev.isSelected();
			recentComboBoxIndex = kineticsComboBox.getSelectedIndex();
			if (fp != null) {
				fp.setFormula(equation);
			}
		}
	}
}
