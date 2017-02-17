package graph.algorithms.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import biologicalElements.Pathway;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.algorithms.CompareGraphs;
import graph.algorithms.HeatmapGraphs;
import graph.algorithms.MergeGraphs;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.algorithms.ScreenSize;
import net.miginfocom.swing.MigLayout;

public class CompareGraphsGUI extends JFrame implements ActionListener,
		ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel panel = new JPanel();

	private JPanel firstGraph = new JPanel();

	private JPanel secondGraph = new JPanel();

	private JComboBox<String> firstBox = new JComboBox<String>();

	private JComboBox<String> secondBox = new JComboBox<String>();

	private JOptionPane optionPane;

	private JDialog dialog;

	private JSplitPane splitPane;

	private GraphContainer con;

	private String mouseFunction;

	private GraphZoomScrollPane firstGraphPane;

	private GraphZoomScrollPane secondGraphPane;
	private JButton[] buttons;

	int splitWindowWith = 0;
	int splitWindowHeight = 0;

	public CompareGraphsGUI() {

		con = ContainerSingelton.getInstance();
		mouseFunction = con.getMouseFunction();
		con.changeMouseFunction("move");

		JButton newButton = new JButton("exit");
		JButton compare = new JButton("compare");
		JButton reset = new JButton("reset");
		JButton merge = new JButton("merge");
		JButton align = new JButton("align");
		JButton heatmap = new JButton("heatmap");
		JButton compare3d = new JButton("compare 3D");

		if (MainWindow.developer) {
			buttons = new JButton[] { newButton, compare, merge, heatmap,
					compare3d, align, reset };
		} else {
			buttons = new JButton[] { newButton, compare, merge, reset };
		}

		newButton.addActionListener(this);
		newButton.setActionCommand("exit");

		compare.addActionListener(this);
		compare.setActionCommand("compare");

		reset.addActionListener(this);
		reset.setActionCommand("reset");

		merge.addActionListener(this);
		merge.setActionCommand("merge");

		heatmap.addActionListener(this);
		heatmap.setActionCommand("heatmap");

		align.addActionListener(this);
		align.setActionCommand("align");

		compare3d.addActionListener(this);
		compare3d.setActionCommand("compare3d");

		optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Graph comparison", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		ScreenSize screen = new ScreenSize();
		int screenHeight = (int) screen.getheight();
		int screenWidth = (int) screen.getwidth();

		firstBox.setEditable(false);
		secondBox.setEditable(false);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, firstGraph,
				secondGraph);

		splitWindowWith = screenWidth - 150;
		splitWindowHeight = screenHeight - 200;

		splitPane.setPreferredSize(new Dimension(splitWindowWith,
				splitWindowHeight));

		splitPane.setOneTouchExpandable(true);

		fillGraphPane(firstGraph, firstBox);
		fillGraphPane(secondGraph, secondBox);

		String firstSelectedPathway = "";
		String secondSelectedPathway = "";

		Iterator<Pathway> it = con.getAllPathways().iterator();
		int i = 0;
		Pathway p;
		String pathwayName;
		
		while (it.hasNext()) {

			p = it.next();
			pathwayName = p.getName();

			if (i == 0) {

				firstGraphPane = new GraphZoomScrollPane(p.getGraph()
						.getVisualizationPaneCopy(
								new Dimension(splitWindowWith - 50,
										splitWindowHeight - 50)));
				firstGraphPane.removeAll();
				firstGraphPane.add(p.getGraph().getVisualizationPaneCopy(
						new Dimension(splitWindowWith - 50,
								splitWindowHeight - 50)));
				firstGraph.add(firstGraphPane, "wrap 5");
				firstSelectedPathway = pathwayName;

			} else if (i == 1) {

				secondGraphPane = new GraphZoomScrollPane(p.getGraph()
						.getVisualizationPaneCopy(
								new Dimension(splitWindowWith - 50,
										splitWindowHeight - 50)));
				secondGraphPane.removeAll();
				secondGraphPane.add(p.getGraph().getVisualizationPaneCopy(
						new Dimension(splitWindowWith - 50,
								splitWindowHeight - 50)));
				secondGraph.add(secondGraphPane, "wrap 5");
				secondSelectedPathway = pathwayName;
			}

			firstBox.addItem(pathwayName);
			secondBox.addItem(pathwayName);

			i++;
		}

		firstBox.setSelectedItem(firstSelectedPathway);
		secondBox.setSelectedItem(secondSelectedPathway);

		firstBox.addItemListener(this);
		firstBox.setName("first");
		secondBox.addItemListener(this);
		secondBox.setName("second");

		MigLayout layout = new MigLayout("", "[grow][grow]", "");
		panel.setLayout(layout);
		panel.add(splitPane, "growx, span,wrap 10");
		panel.add(new JSeparator(), "growx, span");

		dialog.pack();
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		splitPane.setDividerLocation(0.5);
		dialog.setVisible(true);

	}

	private void changeGraph(Boolean first, String pathway) {

		Pathway newPathway = con.getPathway(pathway);

		if (first) {

			firstGraphPane.removeAll();
			firstGraphPane.add(newPathway.getGraph()
					.getVisualizationPaneCopy(
							new Dimension(splitWindowWith - 50,
									splitWindowHeight - 50)));

		} else {

			secondGraphPane.removeAll();
			secondGraphPane.add(newPathway.getGraph()
					.getVisualizationPaneCopy(
							new Dimension(splitWindowWith - 50,
									splitWindowHeight - 50)));

		}
	}

	public void fillGraphPane(JPanel graphPanel, JComboBox<String> box) {
		MigLayout layout = new MigLayout("", "[grow,center]", "");
		graphPanel.setLayout(layout);
		graphPanel.add(box, "span, growx, wrap 5, align center");
		graphPanel.add(new JSeparator(), "span,growx,wrap 5");
	}

	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();
		if ("exit".equals(event)) {

			resetPanels();
			dialog.setVisible(false);
			con.changeMouseFunction(mouseFunction);

		} else if ("compare".equals(event)) {

			CompareGraphs.compareGraphs(
					con.getPathway(firstBox.getSelectedItem().toString()),
					con.getPathway(secondBox.getSelectedItem().toString()));
			firstGraph.updateUI();
			secondGraph.updateUI();

		} else if ("reset".equals(event)) {

			resetPanels();

		} else if ("merge".equals(event)) {

			new MergeGraphs(con.getPathway(firstBox.getSelectedItem()
					.toString()), con.getPathway(secondBox.getSelectedItem()
					.toString()), true);
			this.closeDialog();

		} else if ("heatmap".equals(event)) {

			new HeatmapGraphs(con.getPathway(firstBox.getSelectedItem()
					.toString()), con.getPathway(secondBox.getSelectedItem()
					.toString()));
			this.closeDialog();
		} else if ("align".equals(event)) {

			closeDialog();
			MainWindow.getInstance().openAlignmentGUI(
					con.getPathway(firstBox.getSelectedItem().toString()),
					con.getPathway(secondBox.getSelectedItem().toString()));

		} //else if ("compare3d".equals(event)) {
			//this.dispose();
			//new Compare3dChooseGraphsWindow();
		//}
	}

	private void resetPanels() {

		Iterator<Pathway> it = con.getAllPathways().iterator();
		MainWindow.getInstance().enableOptionPanelUpdate(false);
		Pathway p;
		MyGraph graph;
		
		while (it.hasNext()) {
			p = it.next();
			graph = p.getGraph();
			graph.disableGraphTheory();
			
			graph.clearPickedElements();

		}

		firstGraph.updateUI();
		secondGraph.updateUI();
		MainWindow.getInstance().enableOptionPanelUpdate(true);
	}

	public void itemStateChanged(ItemEvent e) {

		@SuppressWarnings("unchecked")
		JComboBox<String> box = (JComboBox<String>) e.getSource();

		if (box.getName().equals("first")) {
			changeGraph(true, e.getItem().toString());
		} else {
			changeGraph(false, e.getItem().toString());
		}
		resetPanels();
	}

	public void closeDialog() {
		dialog.setVisible(false);
	}

}
