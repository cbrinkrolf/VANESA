package transformation.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphContainer;
import graph.algorithms.CompareGraphs;
import graph.algorithms.MergeGraphs;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.ToolBarButton;
import gui.images.ImagePath;
import net.miginfocom.swing.MigLayout;

public class RuleEditingWindow extends JFrame implements ActionListener {

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

	private Pathway bn = null;
	private Pathway pn = null;

	private JLabel elementTypeBN = new JLabel();

	private JTextField elementNameBN = new JTextField(20);

	private RuleEditingWindowListener listener;

	private GraphElementAbstract gaBN = null;

	// TODO no empty graph in main window allowed, at least one instance must be
	// opened
	public RuleEditingWindow() {

		this.createGraphs();

		listener = new RuleEditingWindowListener(bn, pn);
		// con = GraphContainer.getInstance();
		// mouseFunction = con.getMouseFunction();
		// con.changeMouseFunction("move");

		JButton exit = new JButton("exit");
		JButton compare = new JButton("compare");
		JButton reset = new JButton("reset");
		JButton merge = new JButton("merge");
		// JButton align = new JButton("align");
		// JButton heatmap = new JButton("heatmap");
		// JButton compare3d = new JButton("compare 3D");

		if (MainWindow.developer) {
			buttons = new JButton[] { exit, compare, merge, reset };
		} else {
			buttons = new JButton[] { exit, compare, merge, reset };
		}

		exit.addActionListener(this);
		exit.setActionCommand("exit");

		compare.addActionListener(this);
		compare.setActionCommand("compare");

		reset.addActionListener(this);
		reset.setActionCommand("reset");

		merge.addActionListener(this);
		merge.setActionCommand("merge");

		// heatmap.addActionListener(this);
		// heatmap.setActionCommand("heatmap");

		// align.addActionListener(this);
		// align.setActionCommand("align");

		// compare3d.addActionListener(this);
		// compare3d.setActionCommand("compare3d");
		elementNameBN.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
					bn.getGraph().getVisualizationViewer().repaint();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
					bn.getGraph().getVisualizationViewer().repaint();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
					bn.getGraph().getVisualizationViewer().repaint();
				}
			}
		});

		optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		dialog = new JDialog(this, "Edit or create a new Rule", true);

		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		firstBox.setEditable(false);
		secondBox.setEditable(false);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, firstGraph, secondGraph);

		splitWindowWith = 600; // screenWidth - 150;
		splitWindowHeight = 400; // screenHeight - 200;

		splitPane.setPreferredSize(new Dimension(splitWindowWith, splitWindowHeight));

		splitPane.setOneTouchExpandable(true);

		fillGraphPane(firstGraph, firstBox);
		fillGraphPane(secondGraph, secondBox);

		JPanel elementInformationBN = new JPanel();
		elementInformationBN.setLayout(new MigLayout("fillx", "[grow,fill]", ""));
		elementInformationBN.add(new JLabel("Element type: "));
		elementInformationBN.add(elementTypeBN, "wrap");
		elementInformationBN.add(new JLabel("Element name: "));
		elementInformationBN.add(elementNameBN);

		JPanel buttonPanelBN = new JPanel();
		buttonPanelBN.setName("buttonBN");
		buttonPanelBN.setLayout(new MigLayout("fillx", "[grow,fill]", ""));
		this.createButtonPanel(buttonPanelBN, true);

		JPanel buttonPanelPN = new JPanel();
		buttonPanelPN.setName("buttonPN");
		buttonPanelPN.setLayout(new MigLayout("", "[grow,center]", ""));
		this.createButtonPanel(buttonPanelPN, false);

		String firstSelectedPathway = "";
		String secondSelectedPathway = "";

		// Iterator<Pathway> it = con.getAllPathways().iterator();
		// int i = 0;

		// bn = new CreatePathway("bn").getPathway();

		firstGraphPane = new GraphZoomScrollPane(bn.getGraph().getVisualizationViewer());
		firstGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));
		firstGraphPane.removeAll();
		firstGraphPane = new GraphZoomScrollPane(bn.getGraph().getVisualizationViewer());
		firstGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));
		// firstGraphPane.add(bn.getGraph()
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));
		firstGraph.add(firstGraphPane, "wrap 5");
		firstSelectedPathway = bn.getName();
		// firstGraphPane.setBackground(Color.WHITE);
		// bn.getGraph().getVisualizationPaneCopy(getSize())
		firstGraph.add(buttonPanelBN, "wrap 5");
		firstGraph.add(elementInformationBN, "wrap 5");
		secondGraphPane = new GraphZoomScrollPane(pn.getGraph().getVisualizationViewer());
		secondGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));

		// secondGraphPane = new GraphZoomScrollPane(pn.getGraph()
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));
		secondGraphPane.removeAll();
		secondGraphPane = new GraphZoomScrollPane(pn.getGraph().getVisualizationViewer());
		secondGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));
		// secondGraphPane.add(pn.getGraph()
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));
		secondGraph.add(secondGraphPane, "wrap 5");
		secondGraph.add(buttonPanelPN, "wrap 5");

		secondSelectedPathway = pn.getName();

		firstBox.addItem(bn.getName());
		firstBox.addItem(pn.getName());
		secondBox.addItem(pn.getName());
		secondBox.addItem(bn.getName());

		firstBox.setSelectedItem(firstSelectedPathway);
		secondBox.setSelectedItem(secondSelectedPathway);

		// firstBox.addItemListener(this);
		firstBox.setName("first");
		// secondBox.addItemListener(this);
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

	public void fillGraphPane(JPanel graphPanel, JComboBox<String> box) {
		MigLayout layout = new MigLayout("", "[grow]", "");
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

			CompareGraphs.compareGraphs(con.getPathway(firstBox.getSelectedItem().toString()),
					con.getPathway(secondBox.getSelectedItem().toString()));
			firstGraph.updateUI();
			secondGraph.updateUI();

		} else if ("reset".equals(event)) {

			resetPanels();

		} else if ("merge".equals(event)) {

			new MergeGraphs(con.getPathway(firstBox.getSelectedItem().toString()),
					con.getPathway(secondBox.getSelectedItem().toString()), true);
			this.closeDialog();

		}
	}

	private void resetPanels() {
		System.out.println("reset");
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

	public void closeDialog() {
		dialog.setVisible(false);
	}

	private void createButtonPanel(JPanel panel, boolean bn) {
		ImagePath imagePath = ImagePath.getInstance();

		JButton pick = new ToolBarButton(new ImageIcon(imagePath.getPath("newPick.png")));
		pick.setToolTipText("Pick element");
		pick.setActionCommand("pick");
		pick.addActionListener(listener);
		panel.add(pick);

		JButton move = new ToolBarButton(new ImageIcon(imagePath.getPath("move.png")));
		move.setToolTipText("Move graph");
		move.setActionCommand("move");
		move.addActionListener(listener);
		panel.add(move);

		JButton trash = new ToolBarButton(new ImageIcon(imagePath.getPath("Trash.png")));
		trash.setToolTipText("Delete selected items");
		trash.setMnemonic(KeyEvent.VK_DELETE);
		trash.setActionCommand("del");
		trash.addActionListener(listener);
		panel.add(trash);

		JButton center = new ToolBarButton(new ImageIcon(imagePath.getPath("centerGraph.png")));
		center.setToolTipText("Center graph");
		center.setActionCommand("center");
		center.addActionListener(listener);
		panel.add(center);

		if (bn) {
			JButton edit = new ToolBarButton(new ImageIcon(imagePath.getPath("TitleGraph.png")));
			edit.setSelectedIcon(new ImageIcon(imagePath.getPath("editSelected.png")));
			edit.setToolTipText("Edit graph");
			edit.setActionCommand("edit");
			edit.addActionListener(listener);
			panel.add(edit);
		} else {
			JButton discretePlace = new ToolBarButton(new ImageIcon(imagePath.getPath("discretePlace.png")));
			discretePlace.setToolTipText("Discrete Place");
			discretePlace.setActionCommand("discretePlace");
			discretePlace.addActionListener(listener);
			panel.add(discretePlace);

			JButton continiousPlace = new ToolBarButton(new ImageIcon(imagePath.getPath("continiousPlace.png")));
			continiousPlace.setToolTipText("Continuouse Place");
			continiousPlace.setActionCommand("continuousPlace");
			continiousPlace.addActionListener(listener);
			panel.add(continiousPlace);

			JButton discreteTransition = new ToolBarButton(new ImageIcon(imagePath.getPath("discreteTransition.png")));
			discreteTransition.setToolTipText("Discrete Transition");
			discreteTransition.setActionCommand("discreteTransition");
			discreteTransition.addActionListener(listener);
			panel.add(discreteTransition);

			JButton continiousTransition = new ToolBarButton(
					new ImageIcon(imagePath.getPath("continiousTransition2.png")));
			continiousTransition.setToolTipText("Continuouse Transition");
			continiousTransition.setActionCommand("continiousTransition");
			continiousTransition.addActionListener(listener);
			panel.add(continiousTransition);

			JButton stochasticTransition = new ToolBarButton(
					new ImageIcon(imagePath.getPath("stochasticTransition2.png")));
			stochasticTransition.setToolTipText("Stochastic Transition");
			stochasticTransition.setActionCommand("stochasticTransition");
			stochasticTransition.addActionListener(listener);
			panel.add(stochasticTransition);
		}
	}

	private void createGraphs() {
		bn = new Pathway("bn", true);
		bn.getGraph().setMouseModeEditing();
		String newPathwayName = GraphContainer.getInstance().addPathway(bn.getName(), bn);
		bn = GraphContainer.getInstance().getPathway(newPathwayName);

		pn = new Pathway("petriNet", true);
		pn.setPetriNet(true);
		pn.getGraph().setMouseModeEditing();
		newPathwayName = GraphContainer.getInstance().addPathway(pn.getName(), pn);
		pn = GraphContainer.getInstance().getPathway(newPathwayName);

		PickedState<BiologicalNodeAbstract> vertexStateBN = bn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> edgeStateBN = bn.getGraph().getVisualizationViewer().getPickedEdgeState();
		vertexStateBN.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (vertexStateBN.getPicked().size() == 1) {
					BiologicalNodeAbstract bna = vertexStateBN.getPicked().iterator().next();
					gaBN = bna;
					elementTypeBN.setText(bna.getBiologicalElement());
					elementNameBN.setText(bna.getName());
				}

			}
		});
		edgeStateBN.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (edgeStateBN.getPicked().size() == 1) {
					BiologicalEdgeAbstract edge = edgeStateBN.getPicked().iterator().next();
					gaBN = edge;
					elementTypeBN.setText(edge.getBiologicalElement());
					elementNameBN.setText(edge.getName());
				}
			}
		});
	}
}
