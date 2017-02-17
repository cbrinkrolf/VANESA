package graph.algorithms.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import graph.algorithms.RandomConnectedGraph;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class RandomConnectedGraphGui extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Variables declaration
	JButton cancel = new JButton("cancel");
	JButton applyButton = new JButton("generate Random Graph");
	JButton[] buttons = { applyButton, cancel };

	JPanel panel;
	JOptionPane pane;

	JSpinner nodes;
	JSpinner edges;

	JOptionPane optionPane;
	JDialog dialog;

	JCheckBox weighted = new JCheckBox();

	JSpinner minWeight;
	JSpinner maxWeight;

	public RandomConnectedGraphGui() {

		MigLayout layout = new MigLayout();
		JPanel mainPanel = new JPanel(layout);

		mainPanel.add(new JLabel(
				"What kind of graph do you wish to be generated?"),
				"span 2, wrap 15 ");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx, span 2");

		SpinnerNumberModel model1 = new SpinnerNumberModel(10, 1, 10000, 1);
		nodes = new JSpinner(model1);

		mainPanel.add(new JLabel("Number of nodes"), "span 1, gaptop 2 ");
		mainPanel.add(nodes, "span 1,wrap,gaptop 2");

		SpinnerNumberModel model2 = new SpinnerNumberModel(10, 1,
				(10000 * (10000 - 1)) / 2, 1);
		edges = new JSpinner(model2);

		mainPanel.add(new JLabel("Number of edges"), "span 1, gaptop 2 ");
		mainPanel.add(edges, "span 1,wrap,gaptop 2");

		weighted.setSelected(false);
		mainPanel.add(new JLabel("Weighted Graph"), "span 1, gaptop 2 ");
		mainPanel.add(weighted, "span 1,wrap,gaptop 2");

		SpinnerNumberModel model3 = new SpinnerNumberModel(1, 1, 100, 1);
		minWeight = new JSpinner(model3);

		mainPanel.add(new JLabel("Minimum weight of edges"),
				"span 1, gaptop 2 ");
		mainPanel.add(minWeight, "span 1,wrap,gaptop 2");

		SpinnerNumberModel model4 = new SpinnerNumberModel(100, 1, 100, 1);
		maxWeight = new JSpinner(model4);

		mainPanel.add(new JLabel("Maximum weight of edges"),
				"span 1, gaptop 2 ");
		mainPanel.add(maxWeight, "span 1,wrap,gaptop 2");

		mainPanel.add(new JSeparator(), "gap 10, wrap, growx, span 2");

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		applyButton.addActionListener(this);
		applyButton.setActionCommand("new");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Random Graph Generation", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog.pack();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			dialog.setVisible(false);
		} else if ("new".equals(event)) {
			dialog.setVisible(false);

			RandomConnectedGraph.generateRandomGraph((Integer) nodes.getValue(),
					(Integer) edges.getValue(), weighted.isSelected(),
					(Integer) minWeight.getValue(),
					(Integer) maxWeight.getValue());

		}

	}

}
