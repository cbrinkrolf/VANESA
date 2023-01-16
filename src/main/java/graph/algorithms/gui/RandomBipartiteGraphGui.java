package graph.algorithms.gui;

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

import graph.algorithms.RandomBipartiteGraph;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class RandomBipartiteGraphGui implements ActionListener {

	// Variables declaration
	private JButton cancel = new JButton("cancel");
	private JButton applyButton = new JButton("generate Random Bipartite Graph");
	private JButton[] buttons = { applyButton, cancel };
	
	private JSpinner nodes;
	private JSpinner nodes2;
	
	private JSpinner edges;
	
	private JOptionPane optionPane;
	private JDialog dialog;
	
	public RandomBipartiteGraphGui(){
		
		MigLayout layout = new MigLayout();
		JPanel mainPanel = new JPanel(layout);

		mainPanel.add(new JLabel("What kind of bipartite graph do you wish to be generated?"),
						"span 2, wrap 15 ");
		mainPanel.add(new JSeparator(), "gap 10, wrap 15, growx, span 2");
		
		
		SpinnerNumberModel model1 = new SpinnerNumberModel(3, 1, 1000, 1);
		nodes = new JSpinner(model1);

		mainPanel.add(new JLabel("Number of nodes in the first set"), "span 1, gaptop 2 ");
		mainPanel.add(nodes, "span 1,wrap,gaptop 2");
		
		SpinnerNumberModel model3 = new SpinnerNumberModel(4, 1, 1000, 1);
		nodes2 = new JSpinner(model3);

		mainPanel.add(new JLabel("Number of nodes in the second set"), "span 1, gaptop 2 ");
		mainPanel.add(nodes2, "span 1,wrap,gaptop 2");
		
		SpinnerNumberModel model2 = new SpinnerNumberModel(6, 1, (1000*(1000-1))/2, 1);
		edges = new JSpinner(model2);

		mainPanel.add(new JLabel("Number of edges"), "span 1, gaptop 2 ");
		mainPanel.add(edges, "span 1,wrap,gaptop 2");
		
		mainPanel.add(new JSeparator(), "gap 10, wrap, growx, span 2");
		
		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");

		applyButton.addActionListener(this);
		applyButton.setActionCommand("new");

		optionPane = new JOptionPane(mainPanel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(new JFrame(), "Random Bipartite Graph Generation", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		dialog.pack();
		dialog.setLocationRelativeTo(MainWindow.getInstance().getFrame());
		dialog.setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			dialog.setVisible(false);
		} else if ("new".equals(event)) {
			dialog.setVisible(false);
			
			RandomBipartiteGraph.generateRandomGraph((Integer) nodes.getValue(), (Integer) nodes2.getValue(), (Integer) edges.getValue());
		}
	} 
}
