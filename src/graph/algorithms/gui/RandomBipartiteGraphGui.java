package graph.algorithms.gui;

import graph.algorithms.RandomBipartiteGraph;
import graph.algorithms.RandomGraph;
import gui.algorithms.ScreenSize;

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

import database.brenda.gui.BrendaPatternListWindow;

import net.miginfocom.swing.MigLayout;

public class RandomBipartiteGraphGui extends JFrame implements ActionListener {

	// Variables declaration
	JButton cancel = new JButton("cancel");
	JButton applyButton = new JButton("generate Random Bipartite Graph");
	JButton[] buttons = { applyButton, cancel };
	
	JPanel panel;
	JOptionPane pane;
	
	JSpinner nodes;
	JSpinner nodes2;
	
	JSpinner edges;
	
	JOptionPane optionPane;
	JDialog dialog;
	
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

		dialog = new JDialog(this, "Random Bipartite Graph Generation", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth = (int) screen.getwidth();

		dialog.pack();
		dialog.setLocation((screenWidth / 2) - dialog.getSize().width / 2,
				(screenHeight / 2) - dialog.getSize().height / 2);
		dialog.setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();

		if ("cancel".equals(event)) {
			dialog.setVisible(false);
		} else if ("new".equals(event)) {
			dialog.setVisible(false);
			
			RandomBipartiteGraph random = new RandomBipartiteGraph(); 
			random.generateRandomGraph((Integer) nodes.getValue(), (Integer) nodes2.getValue(), (Integer) edges.getValue());
		}
		
	} 

}
