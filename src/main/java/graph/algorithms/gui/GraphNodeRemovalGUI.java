package graph.algorithms.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import biologicalObjects.nodes.BiologicalNodeAbstract;
import graph.VanesaGraph;
import graph.algorithms.NetworkProperties;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class GraphNodeRemovalGUI implements ActionListener {
	private final JPanel p = new JPanel();
	private boolean emptyPane = true;

	private JComboBox<String> chooseAlgorithm;
	private JButton removeButton;
	private final String[] algorithmNames = { "Choose Algorithm", "Node Degree" };// , "Neighbor Degree", "Clique"
	private JPanel valuesMinMax;
	private JSpinner fromSpinner;
	private JSpinner toSpinner;
	private JLabel minValueLabel;
	private JLabel maxValueLabel;
	private double minvalue;
	private double maxvalue;
	private NetworkProperties c;
	private Hashtable<BiologicalNodeAbstract, Double> nodeValues;
	private SpinnerNumberModel modelRemoveNodesFrom;
	private SpinnerNumberModel modelRemoveNodesTo;

	private void updateWindow() {
		chooseAlgorithm = new JComboBox<>(algorithmNames);
		chooseAlgorithm.setActionCommand("algorithm");
		chooseAlgorithm.addActionListener(this);

		JPanel valuesFromTo = new JPanel(new MigLayout("", "[][][]", ""));

		modelRemoveNodesFrom = new SpinnerNumberModel(0.0d, 0.0d, 1.0d, 1.0d);
		modelRemoveNodesTo = new SpinnerNumberModel(0.0d, 0.0d, 1.0d, 1.0d);

		fromSpinner = new JSpinner(modelRemoveNodesFrom);
		toSpinner = new JSpinner(modelRemoveNodesTo);
		fromSpinner.setEnabled(false);
		toSpinner.setEnabled(false);

		valuesFromTo.add(fromSpinner);
		valuesFromTo.add(new JLabel(" to "));
		valuesFromTo.add(toSpinner);

		removeButton = new JButton("remove");
		removeButton.setActionCommand("remove");
		removeButton.addActionListener(this);
		removeButton.setEnabled(false);

		JButton trimbutton = new JButton("trim network");
		trimbutton.setActionCommand("trim");
		trimbutton.addActionListener(this);

		valuesMinMax = new JPanel(new MigLayout("", "[][grow]", ""));
		minValueLabel = new JLabel("NaN");
		maxValueLabel = new JLabel("NaN");
		valuesMinMax.add(new JLabel("Min:\t"));
		valuesMinMax.add(minValueLabel, "wrap");
		valuesMinMax.add(new JLabel("Max:\t"));
		valuesMinMax.add(maxValueLabel, "wrap");
		valuesMinMax.setVisible(false);

		p.setLayout(new MigLayout("", "[][grow]", ""));
		p.add(new JLabel("Algorithm"), "wrap");
		p.add(chooseAlgorithm, "wrap");
		p.add(new JLabel("Remove Nodes with values:"), "wrap");
		p.add(valuesFromTo, "span 2, wrap");
		p.add(removeButton, "span 2, align right, wrap");
		// p.add(valuesminmax,"span 2, wrap"); //displays already in Spinner
		p.add(trimbutton, "align left, wrap");
	}

	public void revalidateView() {
		if (emptyPane) {
			updateWindow();
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			p.removeAll();
			updateWindow();
			p.repaint();
			p.revalidate();
			p.setVisible(true);
		}
	}

	public void removeAllElements() {
		emptyPane = true;
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	private void resetRemovalInterface() {
		fromSpinner.setEnabled(false);
		toSpinner.setEnabled(false);
		valuesMinMax.setVisible(false);
		removeButton.setEnabled(false);
		chooseAlgorithm.setSelectedIndex(0);
	}

	private void getNodeDegreeRatings() {
		c = new NetworkProperties();
		nodeValues = new Hashtable<>();
		minvalue = Double.MAX_VALUE;
		maxvalue = Double.MIN_NORMAL;
		for (final BiologicalNodeAbstract bna : c.getPathway().getAllGraphNodes()) {
			final double degree = c.getNodeDegree(c.getNodeAssignment(bna));
			if (degree > maxvalue)
				maxvalue = degree;
			if (degree < minvalue)
				minvalue = degree;
			nodeValues.put(bna, degree);
		}
	}

	public void actionPerformed(ActionEvent e) {
		final String command = e.getActionCommand();
		final int currentAlgorithmIndex = chooseAlgorithm.getSelectedIndex();
		if (currentAlgorithmIndex > 0 && command.equals("algorithm")) {
			// do calculations
			if (currentAlgorithmIndex == 1) {
				// Node Degree rating
				getNodeDegreeRatings();
				// Post min/max values:
				modelRemoveNodesFrom = new SpinnerNumberModel(minvalue, minvalue, maxvalue, 1.0d);
				modelRemoveNodesTo = new SpinnerNumberModel(maxvalue, minvalue, maxvalue, 1.0d);
				fromSpinner.setModel(modelRemoveNodesFrom);
				toSpinner.setModel(modelRemoveNodesTo);
				minValueLabel.setText("" + minvalue);
				maxValueLabel.setText("" + maxvalue);
				toSpinner.setValue(maxvalue);
			}
			// Enable further Gui elements
			fromSpinner.setEnabled(true);
			toSpinner.setEnabled(true);
			removeButton.setEnabled(true);
			valuesMinMax.setVisible(true);
		} else if (command.equals("remove")) {
			// get Values from Text fields
			try {
				final double removeFrom = (double) fromSpinner.getValue();
				final double removeTo = (double) toSpinner.getValue();
				// remove specified Nodes
				Set<BiologicalNodeAbstract> removals = new HashSet<>();
				for (Map.Entry<BiologicalNodeAbstract, Double> entry : nodeValues.entrySet()) {
					final double value = entry.getValue();
					if (value >= removeFrom && value <= removeTo) {
						removals.add(entry.getKey());
					}
				}
				final VanesaGraph graph = c.getPathway().getGraph2();
				for (final BiologicalNodeAbstract removal : removals) {
					graph.remove(removal);
				}
				final MainWindow mw = MainWindow.getInstance();
				mw.updateElementTree();
				mw.updatePathwayTree();
				resetRemovalInterface();
				if (removals.size() > 0) {
					JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
							"Nodes removed from network: " + removals.size(), "Remove Success",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (NumberFormatException nfe) {
				System.out.println("Input format not allowed");
			}
		} else if (command.equals("trim")) {
			try {
				// remove all nodes with node degree of 0 and 1
				getNodeDegreeRatings();
				if (nodeValues.containsValue(0d) //ignore this command if there are no removable nodes
						|| nodeValues.containsValue(1d)) {
					// remove specified Nodes
					Set<BiologicalNodeAbstract> removals = new HashSet<>();
					for (Map.Entry<BiologicalNodeAbstract, Double> entry : nodeValues.entrySet()) {
						final double value = entry.getValue();
						if (value >= 0d && value <= 1d)
							removals.add(entry.getKey());
					}
					final VanesaGraph graph = c.getPathway().getGraph2();
					for (final BiologicalNodeAbstract removal : removals) {
						graph.remove(removal);
					}
					final MainWindow mw = MainWindow.getInstance();
					mw.updateElementTree();
					mw.updatePathwayTree();
					resetRemovalInterface();
				}
			} catch (NumberFormatException nfe) {
				// TODO BEHANDLUNG von Eingabe, bzw. Formatted text imput
				System.out.println("Input format not allowed");
			}
		}
	}
}
