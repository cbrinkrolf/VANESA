package transformation.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.BiologicalEdgeAbstractFactory;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphContainer;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.ToolBarButton;
import gui.ImagePath;
import net.miginfocom.swing.MigLayout;
import transformation.Rule;
import transformation.RuleEdge;
import transformation.RuleNode;
import transformation.graphElements.ANYTransition;

// TODO improve parameter matching (more than just 1 variable or literals)
public class RuleEditingWindow implements ActionListener {

	private JScrollPane scrollPane = new JScrollPane();
	private JPanel panel = new JPanel();
	private JPanel biologicalPanel = new JPanel();
	private JPanel petriPanel = new JPanel();
	private JPanel nodeMappingPanel = new JPanel();
	private JPanel parametersPanel = new JPanel();

	private JFrame frame = new JFrame("Rule Editor");;

	private JSplitPane splitPane;
	private GraphZoomScrollPane biologicalGraphPane;
	private GraphZoomScrollPane petriGraphPane;

	private int splitWindowWith = 0;
	private int splitWindowHeight = 0;

	private JTextField ruleName = new JTextField(50);

	private Pathway bn = null;
	private Pathway pn = null;

	private JLabel elementTypeBN = new JLabel();
	private JTextField elementNameBN = new JTextField(20);
	private JLabel elementTypePN = new JLabel();
	private JTextField elementNamePN = new JTextField(20);
	private JCheckBox exactIncidenceChk = new JCheckBox();

	private JCheckBox isDirected = new JCheckBox();

	private GraphElementAbstract gaBN = null;
	private GraphElementAbstract gaPN = null;

	private RuleEditingWindowListener listener;

	private JButton cancel = new JButton("cancel");
	private JButton okButton = new JButton("save");
	private JButton saveCopy = new JButton("save copy");
	private JButton[] buttons = { okButton, saveCopy, cancel };
	private JOptionPane optionPane;

	private JButton btnAddMapping = new JButton("add mapping");
	private JButton btnHighlightEdges = new JButton("highlight edges");
	private JButton btnSetEdges = new JButton("set selected edges");
	private JLabel lblSetEdges = new JLabel("[none]");

	private Map<BiologicalNodeAbstract, BiologicalNodeAbstract> bnToPn = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();
	private Map<GraphElementAbstract, HashMap<String, String>> parameterMapping = new HashMap<GraphElementAbstract, HashMap<String, String>>();
	private Rule rule = null;
	private Rule modRule = null;
	private Set<BiologicalEdgeAbstract> consideredEdges = new HashSet<BiologicalEdgeAbstract>();
	private Set<BiologicalNodeAbstract> exactIncidence = new HashSet<>();

	private JButton chkAllEdgesSelected = new JButton("set all edges");

	private boolean pickLock = false;

	public RuleEditingWindow(Rule rule, Rule modRule, ActionListener al) {

		this.rule = rule;
		this.modRule = modRule;

		this.createGraphs();
		populateGraph();

		listener = new RuleEditingWindowListener(bn, pn);

		ruleName.setText(rule.getName());

		elementNameBN.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
					bn.getGraph().getVisualizationViewer().repaint();
				}
				populateNodeMapping();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
					bn.getGraph().getVisualizationViewer().repaint();
				}
				populateNodeMapping();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
					bn.getGraph().getVisualizationViewer().repaint();
				}
				populateNodeMapping();
			}
		});

		elementNamePN.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					pn.getGraph().getVisualizationViewer().repaint();
					populateNodeMapping();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					pn.getGraph().getVisualizationViewer().repaint();
					populateNodeMapping();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					pn.getGraph().getVisualizationViewer().repaint();
					populateNodeMapping();
				}
			}
		});

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, biologicalPanel, petriPanel);

		splitWindowWith = 600; // screenWidth - 150;
		splitWindowHeight = 400; // screenHeight - 200;

		splitPane.setPreferredSize(new Dimension(splitWindowWith, splitWindowHeight));

		splitPane.setOneTouchExpandable(true);

		fillGraphPane(biologicalPanel, new JLabel("Biological graph pattern for matching"));
		fillGraphPane(petriPanel, new JLabel("Petri net pattern"));

		exactIncidenceChk.setToolTipText("Match exact number of incoming and outgoing edges");
		exactIncidenceChk.addActionListener(this);
		exactIncidenceChk.setActionCommand("exactIncidenceChk");

		isDirected.setToolTipText("directed edge");
		isDirected.addActionListener(this);
		isDirected.setActionCommand("isDirected");

		JPanel elementInformationBN = new JPanel();
		elementInformationBN.setLayout(new MigLayout("fillx", "[grow,fill]", ""));
		elementInformationBN.add(new JLabel("Selected element type:"));
		elementInformationBN.add(elementTypeBN, "wrap");
		elementInformationBN.add(new JLabel("Selected element name:"));
		elementInformationBN.add(elementNameBN, "wrap");
		elementInformationBN.add(new JLabel("Match exact incidence:"));
		elementInformationBN.add(exactIncidenceChk, "split 3");
		elementInformationBN.add(new JLabel("directed edge: "));
		elementInformationBN.add(isDirected);

		JPanel elementInformationPN = new JPanel();
		elementInformationPN.setLayout(new MigLayout("fillx", "[grow,fill]", ""));
		elementInformationPN.add(new JLabel("Selected element type: "));
		elementInformationPN.add(elementTypePN, "wrap");
		elementInformationPN.add(new JLabel("Selected element name: "));
		elementInformationPN.add(elementNamePN);

		JPanel buttonPanelBN = new JPanel();
		buttonPanelBN.setName("buttonBN");
		buttonPanelBN.setLayout(new MigLayout("fillx", "[grow,fill]", ""));
		this.createButtonPanel(buttonPanelBN, true);

		JPanel buttonPanelPN = new JPanel();
		buttonPanelPN.setName("buttonPN");
		buttonPanelPN.setLayout(new MigLayout("", "[grow,center]", ""));
		this.createButtonPanel(buttonPanelPN, false);

		// Iterator<Pathway> it = con.getAllPathways().iterator();
		// int i = 0;

		// bn = new CreatePathway("bn").getPathway();

		biologicalGraphPane = new GraphZoomScrollPane(bn.getGraph().getVisualizationViewer());
		biologicalGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));
		biologicalGraphPane.removeAll();
		biologicalGraphPane = new GraphZoomScrollPane(bn.getGraph().getVisualizationViewer());
		biologicalGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));
		// firstGraphPane.add(bn.getGraph()
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));

		biologicalPanel.add(biologicalGraphPane, "wrap 5");

		// firstGraphPane.setBackground(Color.WHITE);
		// bn.getGraph().getVisualizationPaneCopy(getSize())
		biologicalPanel.add(buttonPanelBN, "wrap 5");
		biologicalPanel.add(elementInformationBN, "wrap 5");
		biologicalPanel.add(nodeMappingPanel);
		petriGraphPane = new GraphZoomScrollPane(pn.getGraph().getVisualizationViewer());
		petriGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));

		// secondGraphPane = new GraphZoomScrollPane(pn.getGraph()
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));
		petriGraphPane.removeAll();
		petriGraphPane = new GraphZoomScrollPane(pn.getGraph().getVisualizationViewer());
		petriGraphPane.setMaximumSize(new Dimension(splitWindowWith - 50, splitWindowHeight - 50));
		// secondGraphPane.add(pn.getGraph()
		// .getVisualizationPaneCopy(new Dimension(splitWindowWith - 50,
		// splitWindowHeight - 50)));
		petriPanel.add(petriGraphPane, "wrap 5");
		petriPanel.add(buttonPanelPN, "wrap 5");
		petriPanel.add(elementInformationPN, "wrap 5");
		petriPanel.add(parametersPanel);

		MigLayout layout = new MigLayout("", "[grow][grow]", "");
		panel.setLayout(layout);

		JPanel ruleNamePanel = new JPanel();
		ruleNamePanel.setLayout(new MigLayout("fillx", "[grow,fill]", ""));

		ruleNamePanel.add(new JLabel("Rule name: "));
		ruleNamePanel.add(ruleName, "align left");

		panel.add(ruleNamePanel, "wrap 10");
		panel.add(new JSeparator(), "growx, span");

		panel.add(splitPane, "growx, span,wrap 10");
		panel.add(new JSeparator(), "growx, span");

		nodeMappingPanel.setLayout(new MigLayout("fillx", "[grow,fill]"));
		parametersPanel.setLayout(new MigLayout("fillx", "[grow,fill]"));

		btnAddMapping.setActionCommand("addMapping");
		btnAddMapping.addActionListener(this);
		btnAddMapping.setEnabled(false);
		btnAddMapping.setToolTipText("Select one biological node and one node from the Petri net!");

		chkAllEdgesSelected.setActionCommand("setAllEdges");
		chkAllEdgesSelected.addActionListener(this);
		chkAllEdgesSelected.setToolTipText("set all edges");

		btnSetEdges.setActionCommand("setEdges");
		btnSetEdges.addActionListener(this);
		btnSetEdges
				.setToolTipText("Set edges to be not considered for matching in rules anymore (default: all edges)!");

		btnHighlightEdges.setActionCommand("highlightEdges");
		btnHighlightEdges.addActionListener(this);
		btnHighlightEdges.setToolTipText(
				"highlights the set of edges which are not considered for further rule matching anymore");

		populateNodeMapping();

		// panel.add(this.nodeMappingPanel);

		cancel.addActionListener(al);
		cancel.addActionListener(this);
		cancel.setActionCommand("cancelRE");

		okButton.addActionListener(al);
		okButton.addActionListener(this);
		okButton.setActionCommand("okButtonRE");

		saveCopy.addActionListener(al);
		saveCopy.addActionListener(this);
		saveCopy.setActionCommand("saveCopy");

		scrollPane.add(panel);
		scrollPane.setPreferredSize(new Dimension(1200, 800));
		scrollPane.setViewportView(panel);
		scrollPane.setVisible(true);

		optionPane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		frame.setAlwaysOnTop(false);
		frame.setContentPane(optionPane);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		frame.revalidate();

		frame.pack();

		frame.setLocationRelativeTo(MainWindow.getInstance().getFrame());

		frame.pack();
		splitPane.setDividerLocation(0.5);
		frame.setVisible(true);
		bn.getGraph().normalCentering();
		pn.getGraph().normalCentering();
	}

	public void fillGraphPane(JPanel graphPanel, JLabel label) {
		MigLayout layout = new MigLayout("", "[grow]", "");
		graphPanel.setLayout(layout);
		graphPanel.add(label, "wrap 5, align center");
		graphPanel.add(new JSeparator(), "span,growx,wrap 5");
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("okButtonRE")) {
			convertGraphToRule(false);
		} else if (e.getActionCommand().equals("saveCopy")) {
			convertGraphToRule(true);
		} else if (e.getActionCommand().equals("cancelRE")) {
			frame.setVisible(false);
		} else if (e.getActionCommand().equals("addMapping")) {
			addNodeMapping();
		} else if (e.getActionCommand().equals("highlightEdges")) {
			PickedState<BiologicalEdgeAbstract> beaPickedState = bn.getGraph().getVisualizationViewer()
					.getPickedEdgeState();
			beaPickedState.clear();
			Iterator<BiologicalEdgeAbstract> it = consideredEdges.iterator();
			while (it.hasNext()) {
				beaPickedState.pick(it.next(), true);
			}
		} else if (e.getActionCommand().equals("setEdges")) {
			PickedState<BiologicalEdgeAbstract> beaPickedState = bn.getGraph().getVisualizationViewer()
					.getPickedEdgeState();
			PickedState<BiologicalNodeAbstract> bnaPickedState = bn.getGraph().getVisualizationViewer()
					.getPickedVertexState();

			Iterator<BiologicalEdgeAbstract> it = beaPickedState.getPicked().iterator();
			consideredEdges.clear();
			BiologicalEdgeAbstract bea;

			while (it.hasNext()) {
				consideredEdges.add(it.next());
			}
			// add also edges if both, from and to, are also picked
			it = bn.getAllEdges().iterator();
			while (it.hasNext()) {
				bea = it.next();
				if (bnaPickedState.isPicked(bea.getFrom()) && bnaPickedState.isPicked(bea.getTo())) {
					consideredEdges.add(bea);
				}
			}
			this.revalidateSelectedEdges();
		} else if (e.getActionCommand().equals("setAllEdges")) {
			Iterator<BiologicalEdgeAbstract> it = bn.getAllEdges().iterator();
			BiologicalEdgeAbstract bea;
			while (it.hasNext()) {
				bea = it.next();
				consideredEdges.add(bea);
				bn.getGraph().getVisualizationViewer().getPickedEdgeState().pick(bea, true);
			}
			revalidateSelectedEdges();
		} else if (e.getActionCommand().startsWith("del_")) {
			// System.out.println("del");
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			Iterator<BiologicalNodeAbstract> it = bnToPn.keySet().iterator();
			BiologicalNodeAbstract bna = null;
			while (it.hasNext()) {
				bna = it.next();
				if (bna.getID() == idx) {
					break;
				}
			}
			if (bna != null) {
				bnToPn.remove(bna);
				this.populateNodeMapping();
			}
			revalidateSelectedEdges();
		} else if (e.getActionCommand().equals("del")) {
			String panelName = ((ToolBarButton) e.getSource()).getParent().getName();
			if (panelName.equals("buttonBN")) {
				this.deleteBNSelection();
			} else if (panelName.equals("buttonPN")) {
				pn.removeSelection();
			}
		} else if (e.getActionCommand().equals("exactIncidenceChk")) {
			PickedState<BiologicalNodeAbstract> vertexState = bn.getGraph().getVisualizationViewer()
					.getPickedVertexState();
			if (vertexState.getPicked().size() == 1) {
				if (exactIncidenceChk.isSelected()) {
					exactIncidence.add(vertexState.getPicked().iterator().next());
				} else {
					exactIncidence.remove(vertexState.getPicked().iterator().next());
				}
			}
		} else if (e.getActionCommand().equals("isDirected")) {
			PickedState<BiologicalEdgeAbstract> edgeState = bn.getGraph().getVisualizationViewer().getPickedEdgeState();
			if (edgeState.getPicked().size() == 1) {
				if (isDirected.isSelected()) {
					edgeState.getPicked().iterator().next().setDirected(true);
				} else {
					edgeState.getPicked().iterator().next().setDirected(false);
				}
				bn.updateMyGraph();
				bn.getGraph().getVisualizationViewer().repaint();
			}
		}
	}

	public void closeDialog() {
		frame.setVisible(false);
	}

	private void createButtonPanel(JPanel panel, boolean bn) {
		ImagePath imagePath = ImagePath.getInstance();

		JButton pick = new ToolBarButton(ImagePath.getInstance().getImageIcon("newPick.png"));
		pick.setToolTipText("Pick element");
		pick.setActionCommand("pick");
		pick.addActionListener(listener);
		panel.add(pick);

		JButton move = new ToolBarButton(ImagePath.getInstance().getImageIcon("move.png"));
		move.setToolTipText("Move graph");
		move.setActionCommand("move");
		move.addActionListener(listener);
		panel.add(move);

		JButton trash = new ToolBarButton(ImagePath.getInstance().getImageIcon("Trash.png"));
		trash.setToolTipText("Delete selected items");
		this.bn.getGraph().getVisualizationViewer().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// nothing to do
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteBNSelection();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// nothing to do
			}
		});
		trash.setActionCommand("del");
		// trash.addActionListener(listener);
		trash.addActionListener(this);
		panel.add(trash);

		JButton center = new ToolBarButton(ImagePath.getInstance().getImageIcon("centerGraph.png"));
		center.setToolTipText("Center graph");
		center.setActionCommand("center");
		center.addActionListener(listener);
		panel.add(center);

		if (bn) {
			JButton edit = new ToolBarButton(ImagePath.getInstance().getImageIcon("TitleGraph.png"));
			edit.setSelectedIcon(ImagePath.getInstance().getImageIcon("editSelected.png"));
			edit.setToolTipText("Edit graph");
			edit.setActionCommand("edit");
			edit.addActionListener(listener);
			panel.add(edit);
		} else {

			JButton discretePlace = new ToolBarButton(ImagePath.getInstance().getImageIcon("discretePlace.png"));
			discretePlace.setToolTipText("Discrete Place");
			discretePlace.setActionCommand("discretePlace");
			discretePlace.addActionListener(listener);
			panel.add(discretePlace);

			JButton continiousPlace = new ToolBarButton(ImagePath.getInstance().getImageIcon("continiousPlace.png"));
			continiousPlace.setToolTipText("Continuouse Place");
			continiousPlace.setActionCommand("continuousPlace");
			continiousPlace.addActionListener(listener);
			panel.add(continiousPlace);

			JButton place = new ToolBarButton(ImagePath.getInstance().getImageIcon("discretePlace.png"));
			place.setToolTipText("Any place");
			place.setHorizontalTextPosition(JButton.CENTER);
			place.setVerticalTextPosition(JButton.CENTER);
			Font f = place.getFont();
			Font f2 = new Font(f.getName(), Font.PLAIN, 50);
			place.setFont(f2);
			place.setText("+");
			place.setActionCommand("place");
			place.addActionListener(listener);
			panel.add(place);

			JButton discreteTransition = new ToolBarButton(ImagePath.getInstance().getImageIcon("discreteTransition.png"));
			discreteTransition.setToolTipText("Discrete Transition");
			discreteTransition.setActionCommand("discreteTransition");
			discreteTransition.addActionListener(listener);
			panel.add(discreteTransition);

			JButton continiousTransition = new ToolBarButton(
					ImagePath.getInstance().getImageIcon("continiousTransition2.png"));
			continiousTransition.setToolTipText("Continuouse Transition");
			continiousTransition.setActionCommand("continiousTransition");
			continiousTransition.addActionListener(listener);
			panel.add(continiousTransition);

			JButton stochasticTransition = new ToolBarButton(
					ImagePath.getInstance().getImageIcon("stochasticTransition2.png"));
			stochasticTransition.setToolTipText("Stochastic Transition");
			stochasticTransition.setActionCommand("stochasticTransition");
			stochasticTransition.addActionListener(listener);
			panel.add(stochasticTransition);

			JButton transition = new ToolBarButton(ImagePath.getInstance().getImageIcon("discreteTransition.png"));
			transition.setToolTipText("transition");
			transition.setToolTipText("Any Transition");
			transition.setHorizontalTextPosition(JButton.CENTER);
			transition.setVerticalTextPosition(JButton.CENTER);
			f = transition.getFont();
			f2 = new Font(f.getName(), Font.PLAIN, 50);
			transition.setFont(f2);
			transition.setText("+");
			transition.setActionCommand("transition");
			transition.addActionListener(listener);
			panel.add(transition);
		}
		// panel.add(new JSeparator(JSeparator.VERTICAL), "growy, gap 5");
		panel.add(new JLabel(), "gap 10");

		JButton adjustDown = new ToolBarButton(imagePath.getImageIcon("adjustDown.png"));
		adjustDown.setToolTipText("Adjust Selected Nodes To Lowest Node");
		adjustDown.setActionCommand("adjustDown");
		adjustDown.addActionListener(listener);
		panel.add(adjustDown);

		JButton adjustLeft = new ToolBarButton(imagePath.getImageIcon("adjustLeft.png"));
		adjustLeft.setToolTipText("Adjust Selected Nodes To Left");
		adjustLeft.setActionCommand("adjustLeft");
		adjustLeft.addActionListener(listener);
		panel.add(adjustLeft);

		JButton adjustHorizontalSpace = new ToolBarButton(imagePath.getImageIcon("adjustHorizontalSpace.png"));
		adjustHorizontalSpace.setToolTipText("Adjust Horizontal Space of Selected Nodes");
		adjustHorizontalSpace.setActionCommand("adjustHorizontalSpace");
		adjustHorizontalSpace.addActionListener(listener);
		panel.add(adjustHorizontalSpace);

		JButton adjustVerticalSpace = new ToolBarButton(imagePath.getImageIcon("adjustVerticalSpace.png"));
		adjustVerticalSpace.setToolTipText("Adjust Vertical Space of Selected Nodes");
		adjustVerticalSpace.setActionCommand("adjustVerticalSpace");
		adjustVerticalSpace.addActionListener(listener);
		panel.add(adjustVerticalSpace);

	}

	private void addNodeMapping() {
		PickedState<BiologicalNodeAbstract> bnPickedState = bn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalNodeAbstract> pnPickedState = pn.getGraph().getVisualizationViewer()
				.getPickedVertexState();

		if (bnPickedState.getPicked().size() == 1 && pnPickedState.getPicked().size() == 1) {
			BiologicalNodeAbstract bnBNA = bnPickedState.getPicked().iterator().next();
			BiologicalNodeAbstract pnBNA = pnPickedState.getPicked().iterator().next();
			if (bnToPn.containsKey(bnBNA)) {
				PopUpDialog.getInstance().show("Error", "Biological node " + bnBNA.getName() + "is mapped already!");
				return;
			} else if (bnToPn.containsValue(pnBNA)) {
				PopUpDialog.getInstance().show("Error", "Petri net node " + pnBNA.getName() + "is mapped already!");
				return;
			} else if (bnBNA.getName().length() < 1) {
				PopUpDialog.getInstance().show("Error", "Biological node needs a valid name (not empty)!");
				return;
			} else if (pnBNA.getName().length() < 1) {
				PopUpDialog.getInstance().show("Error", "Petri net node needs a valid name (not empty)!");
				return;
			}
			bnToPn.put(bnBNA, pnBNA);
			addNodeMappingDefaultParameters(bnBNA, pnBNA);
			this.populateNodeMapping();
			this.populateTransformationParameterPanel();
		}
	}

	private void addNodeMappingDefaultParameters(BiologicalNodeAbstract bnBNA, BiologicalNodeAbstract pnBNA) {
		parameterMapping.get(pnBNA).put("name", bnBNA.getName() + ".name");
		if (pnBNA instanceof Place) {
			parameterMapping.get(pnBNA).put("tokenStart", bnBNA.getName() + ".concentrationStart");
			parameterMapping.get(pnBNA).put("tokenMin", bnBNA.getName() + ".concentrationMin");
			parameterMapping.get(pnBNA).put("tokenMax", bnBNA.getName() + ".concentrationMax");
		} else if (pnBNA instanceof Transition) {
			if (pnBNA instanceof DiscreteTransition || pnBNA instanceof ANYTransition) {

			} else if (pnBNA instanceof ContinuousTransition || pnBNA instanceof ANYTransition) {
				if (bnBNA instanceof DynamicNode) {
					parameterMapping.get(pnBNA).put("maximalSpeed", bnBNA.getName() + ".maximalSpeed");
				}
			}
		}
	}

	private void createGraphs() {

		bn = new Pathway("bn", true);
		if (rule.isBNEmpty()) {
			bn.getGraph().setMouseModeEditing();
			lblSetEdges.setText("[all edges]");
		} else {
			bn.getGraph().setMouseModePick();
		}
		String newPathwayName = GraphContainer.getInstance().addPathway(bn.getName(), bn);
		bn = GraphContainer.getInstance().getPathway(newPathwayName);

		pn = new Pathway("petriNet", true);
		pn.setIsPetriNet(true);
		if (rule.isPNEmpty()) {
			pn.getGraph().setMouseModeEditing();
		} else {
			pn.getGraph().setMouseModePick();
		}
		newPathwayName = GraphContainer.getInstance().addPathway(pn.getName(), pn);
		pn = GraphContainer.getInstance().getPathway(newPathwayName);

		PickedState<BiologicalNodeAbstract> vertexStateBN = bn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> edgeStateBN = bn.getGraph().getVisualizationViewer().getPickedEdgeState();
		PickedState<BiologicalNodeAbstract> vertexStatePN = pn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> edgeStatePN = pn.getGraph().getVisualizationViewer().getPickedEdgeState();

		vertexStateBN.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				if (vertexStateBN.getPicked().size() == 1 && vertexStatePN.getPicked().size() == 1) {
					if (!bnToPn.containsKey(vertexStateBN.getPicked().iterator().next())
							&& !bnToPn.containsValue(vertexStatePN.getPicked().iterator().next())) {
						btnAddMapping.setEnabled(true);
					} else {
						btnAddMapping.setEnabled(false);
					}
				} else {
					btnAddMapping.setEnabled(false);
				}
				if (!pickLock) {
					if (vertexStateBN.getPicked().size() == 1) {
						pickLock = true;
						exactIncidenceChk.setEnabled(true);
						BiologicalNodeAbstract bna = vertexStateBN.getPicked().iterator().next();
						if (bnToPn.containsKey(bna)) {
							vertexStatePN.clear();
							vertexStatePN.pick(bnToPn.get(bna), true);
							gaPN = bnToPn.get(bna);
							elementTypePN.setText(bnToPn.get(bna).getBiologicalElement());
							elementNamePN.setText(bnToPn.get(bna).getName());
						}
						pickLock = false;
						gaBN = bna;
						elementTypeBN.setText(bna.getBiologicalElement());
						elementNameBN.setText(bna.getName());
						exactIncidenceChk.setSelected(exactIncidence.contains(bna));
					} else {
						gaBN = null;
						elementTypeBN.setText("");
						elementNameBN.setText("");
						exactIncidenceChk.setSelected(false);
						exactIncidenceChk.setEnabled(false);
					}
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
					isDirected.setEnabled(true);
					isDirected.setSelected(edge.isDirected());
				} else {
					gaBN = null;
					elementTypeBN.setText("");
					elementNameBN.setText("");
					exactIncidenceChk.setSelected(false);
					exactIncidenceChk.setEnabled(false);
					isDirected.setEnabled(false);
				}
			}
		});

		vertexStatePN.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				if (vertexStateBN.getPicked().size() == 1 && vertexStatePN.getPicked().size() == 1) {
					if (!bnToPn.containsKey(vertexStateBN.getPicked().iterator().next())
							&& !bnToPn.containsValue(vertexStatePN.getPicked().iterator().next())) {
						btnAddMapping.setEnabled(true);
					} else {
						btnAddMapping.setEnabled(false);
					}
				} else {
					btnAddMapping.setEnabled(false);
				}
				if (!pickLock) {
					if (vertexStatePN.getPicked().size() == 1) {
						pickLock = true;
						BiologicalNodeAbstract bna = vertexStatePN.getPicked().iterator().next();
						if (bnToPn.containsValue(bna)) {
							vertexStateBN.clear();
							for (BiologicalNodeAbstract b : bnToPn.keySet()) {
								if (bnToPn.get(b) == bna) {
									vertexStateBN.pick(b, true);
									gaBN = b;
									elementTypeBN.setText(b.getBiologicalElement());
									elementNameBN.setText(b.getName());
									break;
								}
							}
						}
						pickLock = false;
						gaPN = bna;
						elementTypePN.setText(bna.getBiologicalElement());
						elementNamePN.setText(bna.getName());
						populateTransformationParameterPanel();
					} else {
						gaPN = null;
						elementTypePN.setText("");
						elementNamePN.setText("");
					}
				} else {
					if (vertexStatePN.getPicked().size() == 1) {
						populateTransformationParameterPanel();
					}
				}
			}
		});
		edgeStatePN.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (edgeStatePN.getPicked().size() == 1) {
					BiologicalEdgeAbstract edge = edgeStatePN.getPicked().iterator().next();
					gaPN = edge;
					elementTypePN.setText(edge.getBiologicalElement());
					elementNamePN.setText(edge.getName());
					populateTransformationParameterPanel();
				} else {
					gaPN = null;
					elementTypePN.setText("");
					elementNamePN.setText("");
				}
			}
		});
		for (KeyListener k : bn.getGraph().getVisualizationViewer().getKeyListeners()) {
			bn.getGraph().getVisualizationViewer().removeKeyListener(k);
		}

	}

	private void revalidateSelectedEdges() {
		String text = "[";
		Iterator<BiologicalEdgeAbstract> it = consideredEdges.iterator();
		BiologicalEdgeAbstract bea;
		while (it.hasNext()) {
			bea = it.next();
			if (text.length() > 1) {
				text += ",";
			}
			text += bea.getName();
		}
		text += "]";
		if (consideredEdges.size() == bn.getAllEdges().size()) {
			lblSetEdges.setText("[all edges]");
			chkAllEdgesSelected.setEnabled(false);
		} else if (consideredEdges.size() == 0) {
			lblSetEdges.setText("[none]");
			chkAllEdgesSelected.setEnabled(true);
		} else {
			lblSetEdges.setText(text);
			chkAllEdgesSelected.setEnabled(true);
		}
		// System.out.println("considered edges: " + consideredEdges.size());
		lblSetEdges.repaint();
	}

	private void populateGraph() {

		// for BN
		// put nodes
		RuleNode rn;
		BiologicalNodeAbstract bna;
		Map<String, BiologicalNodeAbstract> nameToBN = new HashMap<String, BiologicalNodeAbstract>();
		for (int i = 0; i < rule.getBiologicalNodes().size(); i++) {
			rn = rule.getBiologicalNodes().get(i);
			bna = BiologicalNodeAbstractFactory.create(rn.getType(), null);
			bna.setName(rn.getName());
			bna.setLabel(rn.getName());
			nameToBN.put(bna.getName(), bna);
			bn.addVertex(bna, new Point2D.Double(rn.getX(), rn.getY()));
			if (rn.isExactIncidence()) {
				exactIncidence.add(bna);
			}
			// biologicalNodeBox.addItem(bna.getName());
		}

		// put edges
		BiologicalEdgeAbstract bea;
		RuleEdge re;
		Map<RuleEdge, BiologicalEdgeAbstract> edgesMap = new HashMap<RuleEdge, BiologicalEdgeAbstract>();
		for (int i = 0; i < rule.getBiologicalEdges().size(); i++) {
			re = rule.getBiologicalEdges().get(i);
			bea = BiologicalEdgeAbstractFactory.create(re.getType(), null);
			bea.setFrom(nameToBN.get(re.getFrom().getName()));
			bea.setTo(nameToBN.get(re.getTo().getName()));
			bea.setLabel(re.getName());
			bea.setName(re.getName());
			bea.setDirected(re.isDirected());
			bn.addEdge(bea);
			edgesMap.put(re, bea);
		}
		bn.updateMyGraph();

		// for PN
		Map<String, BiologicalNodeAbstract> nameToPN = new HashMap<String, BiologicalNodeAbstract>();
		for (int i = 0; i < rule.getPetriNodes().size(); i++) {
			rn = rule.getPetriNodes().get(i);
			bna = BiologicalNodeAbstractFactory.create(rn.getType(), null);
			bna.setName(rn.getName());
			bna.setLabel(rn.getName());
			nameToPN.put(bna.getName(), bna);
			pn.addVertex(bna, new Point2D.Double(rn.getX(), rn.getY()));
			parameterMapping.put(bna, new HashMap<String, String>());
			for (String key : rn.getParameterMap().keySet()) {
				parameterMapping.get(bna).put(key, rn.getParameterMap().get(key));
			}
		}

		// put edges
		for (int i = 0; i < rule.getPetriEdges().size(); i++) {
			re = rule.getPetriEdges().get(i);
			bea = BiologicalEdgeAbstractFactory.create(re.getType(), null);
			bea.setFrom(nameToPN.get(re.getFrom().getName()));
			bea.setTo(nameToPN.get(re.getTo().getName()));
			bea.setLabel(re.getName());
			bea.setName(re.getName());
			bea.setDirected(true);
			bn.addEdge(bea);
			parameterMapping.put(bea, new HashMap<String, String>());
			for (String key : re.getParameterMap().keySet()) {
				parameterMapping.get(bea).put(key, re.getParameterMap().get(key));
			}
		}
		pn.updateMyGraph();

		String bn;
		String pn;
		Iterator<RuleNode> it = rule.getBnToPnMapping().keySet().iterator();
		while (it.hasNext()) {
			rn = it.next();
			bn = rn.getName();
			pn = rule.getBnToPnMapping().get(rn).getName();
			bnToPn.put(nameToBN.get(bn), nameToPN.get(pn));
		}

		for (int i = 0; i < rule.getConsideredEdges().size(); i++) {
			consideredEdges.add(edgesMap.get(rule.getConsideredEdges().get(i)));
		}
	}

	private void populateNodeMapping() {
		nodeMappingPanel.removeAll();

		nodeMappingPanel.add(new JLabel("Mapping of biological node to node of Petri net:"), "");
		nodeMappingPanel.add(new JSeparator(), "span, growx, wrap");

		nodeMappingPanel.add(btnAddMapping, "wrap");
		nodeMappingPanel.add(new JLabel("Biological node:"));
		nodeMappingPanel.add(new JLabel("Petri net node:"), "wrap");

		// print actual mapping
		Iterator<BiologicalNodeAbstract> it = bnToPn.keySet().iterator();
		BiologicalNodeAbstract bna;
		Set<BiologicalNodeAbstract> toDelete = new HashSet<BiologicalNodeAbstract>();
		JButton btnDelete;
		while (it.hasNext()) {
			bna = it.next();
			if (bn.containsVertex(bna) && pn.containsVertex(bnToPn.get(bna))) {
				nodeMappingPanel.add(new JLabel(bna.getName()));
				nodeMappingPanel.add(new JLabel(bnToPn.get(bna).getName()));
				btnDelete = new JButton("delete");
				btnDelete.addActionListener(this);
				btnDelete.setActionCommand("del_" + bna.getID());
				nodeMappingPanel.add(btnDelete, "wrap");
			} else {
				toDelete.add(bna);
			}
		}
		// two steps, otherwise concurrent transaction of iteration and deletion
		it = toDelete.iterator();
		while (it.hasNext()) {
			bnToPn.remove(it.next());
		}

		nodeMappingPanel.add(new JSeparator(), "span,growx,wrap 5");
		nodeMappingPanel.add(new JLabel("Considered edges:"), "grow");
		nodeMappingPanel.add(chkAllEdgesSelected, "grow");
		nodeMappingPanel.add(lblSetEdges, "grow, wrap");
		nodeMappingPanel.add(btnHighlightEdges);
		nodeMappingPanel.add(btnSetEdges, "wrap");

		this.revalidateSelectedEdges();
		nodeMappingPanel.repaint();
		frame.pack();
		frame.repaint();
	}

	private void populateTransformationParameterPanel() {
		parametersPanel.removeAll();
		parametersPanel.add(new JLabel("Set parameters for Petri net elements:"), "");
		parametersPanel.add(new JSeparator(), "span 2, growx, wrap");

		PickedState<BiologicalNodeAbstract> vertexStatePN = pn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> edgeStatePN = pn.getGraph().getVisualizationViewer().getPickedEdgeState();

		if (vertexStatePN.getPicked().size() == 1) {
			BiologicalNodeAbstract bna = vertexStatePN.getPicked().iterator().next();
			// System.out.println(bna.getTransformationParameters());
			List<String> params = bna.getTransformationParameters();
			// Collections.sort(params);
			// System.out.println("drin");
			// set default values for newly added nodes
			if (parameterMapping.get(bna) == null) {
				createDefaultParameterMap(bna);
			}
			for (String param : params) {
				JTextField field = new JTextField(20);
				field.setText(parameterMapping.get(bna).get(param));
				field.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent e) {
						if (bna != null) {
							parameterMapping.get(bna).put(param, field.getText().trim());
						}
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						if (bna != null) {
							parameterMapping.get(bna).put(param, field.getText().trim());
						}
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						if (bna != null) {
							parameterMapping.get(bna).put(param, field.getText().trim());
						}
					}
				});
				parametersPanel.add(new JLabel(param + ":"), "");
				parametersPanel.add(field, "wrap");
			}

		} else if (edgeStatePN.getPicked().size() == 1) {
			parametersPanel.removeAll();
			parametersPanel.add(new JLabel("Set parameters for Petri net elements:"), "");
			parametersPanel.add(new JSeparator(), "span 2, growx, wrap");

			BiologicalEdgeAbstract bea = edgeStatePN.getPicked().iterator().next();

			List<String> params = bea.getTransformationParameters();
			// Collections.sort(params);
			// System.out.println("drin");
			// set default values for newly added nodes
			if (parameterMapping.get(bea) == null) {
				createDefaultParameterMap(bea);
			}
			for (String param : params) {
				JTextField field = new JTextField(10);
				field.setText(parameterMapping.get(bea).get(param));
				field.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent e) {
						if (bea != null) {
							parameterMapping.get(bea).put(param, field.getText().trim());
						}
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						if (bea != null) {
							parameterMapping.get(bea).put(param, field.getText().trim());
							// System.out.println(parameterMapping.get(bea).get(param));
						}
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						if (bea != null) {
							parameterMapping.get(bea).put(param, field.getText().trim());
						}
					}
				});
				parametersPanel.add(new JLabel(param + ":"), "");
				parametersPanel.add(field, "wrap");
			}
		}
		parametersPanel.revalidate();
	}

	private void convertGraphToRule(boolean copyRule) {
		if (!checkGraphNames()) {
			return;
		}

		if (copyRule) {
			rule = modRule;
		}

		rule.setName(this.ruleName.getText().trim());
		rule.getBiologicalNodes().clear();
		rule.getBiologicalEdges().clear();
		rule.getPetriNodes().clear();
		rule.getPetriEdges().clear();
		rule.getBnToPnMapping().clear();
		rule.getConsideredEdges().clear();

		// forBN
		BiologicalNodeAbstract bna;
		RuleNode rn;
		Map<BiologicalNodeAbstract, RuleNode> bnaToBNRuleNode = new HashMap<BiologicalNodeAbstract, RuleNode>();
		for (int i = 0; i < bn.getAllGraphNodesSortedAlphabetically().size(); i++) {
			bna = bn.getAllGraphNodesSortedAlphabetically().get(i);
			rn = new RuleNode();
			rn.setName(bna.getName());
			rn.setType(bna.getBiologicalElement());
			rn.setX(bn.getGraph().getVertexLocation(bna).getX());
			rn.setY(bn.getGraph().getVertexLocation(bna).getY());
			rn.setExactIncidence(exactIncidence.contains(bna));
			rule.addBiologicalNode(rn);
			bnaToBNRuleNode.put(bna, rn);
		}
		RuleEdge re;
		BiologicalEdgeAbstract bea;
		Map<BiologicalEdgeAbstract, RuleEdge> beaToRuleEdge = new HashMap<BiologicalEdgeAbstract, RuleEdge>();
		for (int i = 0; i < bn.getAllEdgesSorted().size(); i++) {
			bea = bn.getAllEdgesSorted().get(i);
			re = new RuleEdge(bea.getName(), bea.getBiologicalElement(), bnaToBNRuleNode.get(bea.getFrom()),
					bnaToBNRuleNode.get(bea.getTo()));
			re.setDirected(bea.isDirected());
			rule.addBiologicalEdge(re);
			beaToRuleEdge.put(bea, re);
		}

		// for PN
		Map<BiologicalNodeAbstract, RuleNode> bnaToPNRuleNode = new HashMap<BiologicalNodeAbstract, RuleNode>();
		for (int i = 0; i < pn.getAllGraphNodesSortedAlphabetically().size(); i++) {
			bna = pn.getAllGraphNodesSortedAlphabetically().get(i);
			rn = new RuleNode();
			rn.setName(bna.getName());
			rn.setType(bna.getBiologicalElement());
			rn.setX(pn.getGraph().getVertexLocation(bna).getX());
			rn.setY(pn.getGraph().getVertexLocation(bna).getY());
			rule.addPetriNode(rn);
			bnaToPNRuleNode.put(bna, rn);
			if (parameterMapping.get(bna) == null) {
				createDefaultParameterMap(bna);
			}
			for (String param : parameterMapping.get(bna).keySet()) {
				rn.getParameterMap().put(param, parameterMapping.get(bna).get(param));
			}
		}
		for (int i = 0; i < pn.getAllEdgesSorted().size(); i++) {
			bea = pn.getAllEdgesSorted().get(i);
			re = new RuleEdge(bea.getName(), bea.getBiologicalElement(), bnaToPNRuleNode.get(bea.getFrom()),
					bnaToPNRuleNode.get(bea.getTo()));
			rule.addPetriEdge(re);
			if (parameterMapping.get(bea) == null) {
				createDefaultParameterMap(bea);
			}
			for (String param : parameterMapping.get(bea).keySet()) {
				re.getParameterMap().put(param, parameterMapping.get(bea).get(param));
				// System.out.println("set " + param + " to " +
				// parameterMapping.get(bea).get(param));
			}
		}

		// mapping BN nodes to PN nodes
		Iterator<BiologicalNodeAbstract> it = bnToPn.keySet().iterator();
		while (it.hasNext()) {
			bna = it.next();
			rule.addBNtoPNMapping(bnaToBNRuleNode.get(bna), bnaToPNRuleNode.get(bnToPn.get(bna)));

		}

		// considered edges
		if (chkAllEdgesSelected.isSelected()) {
			Iterator<BiologicalEdgeAbstract> it2 = bn.getAllEdges().iterator();
			while (it2.hasNext()) {
				rule.getConsideredEdges().add(beaToRuleEdge.get(it2.next()));
			}
		} else {
			Iterator<BiologicalEdgeAbstract> it2 = consideredEdges.iterator();
			while (it2.hasNext()) {
				rule.getConsideredEdges().add(beaToRuleEdge.get(it2.next()));
			}
		}

		for (RuleNode rn1 : rule.getBiologicalNodes()) {
			System.out.println(rn1.getName() + " In: " + rule.getIncomingDirectedEdgeCount(rn1) + " out: "
					+ rule.getOutgoingDirectedEdgeCount(rn1));
		}
		frame.setVisible(false);
	}

	private void deleteBNSelection() {
		Iterator<BiologicalEdgeAbstract> itBea = bn.getGraph().getVisualizationViewer().getPickedEdgeState().getPicked()
				.iterator();
		BiologicalEdgeAbstract bea;
		while (itBea.hasNext()) {
			bea = itBea.next();
			if (consideredEdges.contains(bea)) {
				consideredEdges.remove(bea);
			}
		}

		Iterator<BiologicalNodeAbstract> itBna = bn.getGraph().getVisualizationViewer().getPickedVertexState()
				.getPicked().iterator();
		BiologicalNodeAbstract bna;
		while (itBna.hasNext()) {
			bna = itBna.next();
			if (bnToPn.containsKey(bna)) {
				bnToPn.remove(bna);
			}
			for (BiologicalEdgeAbstract edge : bn.getGraph().getJungGraph().getIncidentEdges(bna)) {
				if (consideredEdges.contains(edge)) {
					consideredEdges.remove(edge);
				}
			}
		}
		bn.removeSelection();
		revalidateSelectedEdges();
		this.populateNodeMapping();
	}

	private void createDefaultParameterMap(GraphElementAbstract gea) {
		if (parameterMapping.get(gea) == null) {
			// for nodes
			if (gea instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) gea;
				parameterMapping.put(gea, new HashMap<String, String>());
				for (String key : bna.getTransformationParameters()) {
					switch (key) {
					case "name":
						parameterMapping.get(bna).put(key, "");
						break;
					case "tokenStart":
						parameterMapping.get(bna).put(key, ((Place) bna).getTokenStart() + "");
						break;
					case "tokenMin":
						parameterMapping.get(bna).put(key, ((Place) bna).getTokenMin() + "");
						break;
					case "tokenMax":
						parameterMapping.get(bna).put(key, ((Place) bna).getTokenMax() + "");
						break;
					case "firingCondition":
						parameterMapping.get(bna).put(key, ((Transition) bna).getFiringCondition());
						break;
					case "maximalSpeed":
						if (bna instanceof ContinuousTransition) {
							parameterMapping.get(bna).put(key, ((ContinuousTransition) bna).getMaximalSpeed());
						} else if (bna instanceof ANYTransition) {
							parameterMapping.get(bna).put(key, "1");
						}
						break;
					case "delay":
						if (bna instanceof ContinuousTransition) {
							parameterMapping.get(bna).put(key, ((DiscreteTransition) bna).getDelay() + "");
						} else if (bna instanceof ANYTransition) {
							parameterMapping.get(bna).put(key, "1");
						}
						break;
					}
				}
				// for edges
			} else if (gea instanceof BiologicalEdgeAbstract) {
				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) gea;
				parameterMapping.put(bea, new HashMap<String, String>());
				for (String key : bea.getTransformationParameters()) {
					switch (key) {
					case "function":
						parameterMapping.get(bea).put(key, ("1"));
						break;
					}
				}
			}
		}
	}

	private boolean checkGraphNames() {
		Set<String> set = new HashSet<String>();
		String errorMessage = "";
		for (BiologicalEdgeAbstract bea : bn.getAllEdges()) {
			if (bea.getName().length() == 0) {
				errorMessage += "- at least one edge in the biological pattern has an empty name\r\n";
			} else if (set.contains(bea.getName())) {
				errorMessage += "- name of edge +" + bea.getName() + " is not unique.\r\n";
			} else {
				set.add(bea.getName());
			}
		}
		for (BiologicalNodeAbstract bna : bn.getAllGraphNodes()) {
			if (bna.getName().length() == 0) {
				errorMessage += "- at least one node in the biological pattern has an empty name\r\n";
			} else if (set.contains(bna.getName())) {
				errorMessage += "- name of node " + bna.getName() + " is not unique.\r\n";
			} else {
				set.add(bna.getName());
			}
		}
		for (BiologicalEdgeAbstract bea : pn.getAllEdges()) {
			if (bea.getName().length() == 0) {
				errorMessage += "- at least one edge in the Petri net has an empty name\r\n";
			} else if (set.contains(bea.getName())) {
				errorMessage += "- name of edge " + bea.getName() + " is not unique.\r\n";
			} else {
				set.add(bea.getName());
			}
		}
		for (BiologicalNodeAbstract bna : pn.getAllGraphNodes()) {
			if (bna.getName().length() == 0) {
				errorMessage += "- at least one node in the Petri net has an empty name\r\n";
			} else if (set.contains(bna.getName())) {
				errorMessage += "- name of node " + bna.getName() + " is not unique.\r\n";
			} else {
				set.add(bna.getName());
			}
		}
		if (errorMessage.length() == 0) {
			return true;
		}
		errorMessage = "All names of edges and nodes in the biological pattern and in the Petri net must be non-emptyh and unique!\r\n\r\n"
				+ errorMessage;
		PopUpDialog.getInstance().show("Error saving rule!", errorMessage);
		return false;
	}

	public Rule getRule() {
		return this.rule;
	}
}
