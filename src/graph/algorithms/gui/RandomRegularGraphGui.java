package graph.algorithms.gui;

import graph.algorithms.RandomRegularGraph;
import gui.MainWindowSingleton;
import gui.algorithms.ScreenSize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

public class RandomRegularGraphGui extends JFrame implements ActionListener {

	// Variables declaration
	JButton cancel = new JButton("cancel");
	JButton applyButton = new JButton("Generate Random Regular Graph");
	JButton[] buttons = { applyButton, cancel };

	JPanel panel;
	JOptionPane pane;

	JSpinner nodes;
	JSpinner degree;

	JOptionPane optionPane;
	JDialog dialog;

	public RandomRegularGraphGui() {

		MigLayout layout = new MigLayout();
		JPanel mainPanel = new JPanel(layout);

		mainPanel.add(new JLabel(
				"What kind of graph do you wish to be generated?"),
				"span 2, wrap 15 ");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx, span 2");

		SpinnerNumberModel model1 = new SpinnerNumberModel(10, 1, 1000, 1);
		nodes = new JSpinner(model1);

		mainPanel.add(new JLabel("Number of nodes"), "span 1, gaptop 2 ");
		mainPanel.add(nodes, "span 1,wrap,gaptop 2");

		SpinnerNumberModel model2 = new SpinnerNumberModel(4, 1,
				(1000 * (1000 - 1)) / 2, 1);
		degree = new JSpinner(model2);

		mainPanel.add(new JLabel("Degree of each Node"), "span 1, gaptop 2 ");
		mainPanel.add(degree, "span 1,wrap,gaptop 2");

		mainPanel.add(new JSeparator(), "gap 10, wrap, growx, span 2");

		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		applyButton.addActionListener(this);
		applyButton.setActionCommand("new");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Random Regular Graph Generation", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog.pack();
		dialog.setLocationRelativeTo(MainWindowSingleton.getInstance());
		dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			dialog.setVisible(false);
		} else if ("new".equals(event)) {
			dialog.setVisible(false);

			RandomRegularGraph random = new RandomRegularGraph();
			random.generateRandomRegularGraph((Integer) nodes.getValue(),
					(Integer) degree.getValue());

		}

	}

}
