package transformation.gui;

import java.awt.Dimension;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
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
import biologicalElements.PathwayType;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.BiologicalEdgeAbstractFactory;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphContainer;
import graph.VanesaGraph;
import graph.rendering.VanesaGraphRendererPanel;
import gui.ImagePath;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.ToolBarButton;
import net.miginfocom.swing.MigLayout;
import transformation.Rule;
import transformation.RuleEdge;
import transformation.RuleNode;
import transformation.graphElements.ANYTransition;

public class RuleEditingWindow implements ActionListener {
	private final JPanel nodeMappingPanel = new JPanel();
	private final JPanel parametersPanel = new JPanel();

	private final JFrame frame = new JFrame("Rule Editor");

	private final JTextField ruleName = new JTextField(50);

	private final Pathway bn = new Pathway("bn", true, PathwayType.BiologicalNetwork);
	private final Pathway pn = new Pathway("petriNet", true, PathwayType.PetriNet);

	private final JLabel elementTypeBN = new JLabel();
	private final JTextField elementNameBN = new JTextField(20);
	private final JLabel elementTypePN = new JLabel();
	private final JTextField elementNamePN = new JTextField(20);
	private final JCheckBox exactIncidenceChk = new JCheckBox();

	private final JCheckBox isDirected = new JCheckBox();

	private GraphElementAbstract gaBN = null;
	private GraphElementAbstract gaPN = null;

	private final RuleEditingWindowListener listener;

	private final JButton btnAddMapping = new JButton("add mapping");
	private final JButton btnHighlightEdges = new JButton("highlight edges");
	private final JButton btnSetEdges = new JButton("set selected edges");
	private final JLabel lblSetEdges = new JLabel("[none]");

	private final JButton chkAllEdgesSelected = new JButton("set all edges");

	private final Map<BiologicalNodeAbstract, BiologicalNodeAbstract> bnToPn = new HashMap<>();
	private final Map<GraphElementAbstract, HashMap<String, String>> parameterMapping = new HashMap<>();
	private final Set<BiologicalEdgeAbstract> consideredEdges = new HashSet<>();
	private final Set<BiologicalNodeAbstract> exactIncidence = new HashSet<>();

	private boolean pickLock = false;
	private Rule rule;
	private final Rule modRule;

	public RuleEditingWindow(Rule rule, Rule modRule, ActionListener al) {
		this.rule = rule;
		this.modRule = modRule;
		createGraphs();
		populateGraph();

		listener = new RuleEditingWindowListener(bn, pn);

		ruleName.setText(rule.getName());

		elementNameBN.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
				}
				populateNodeMapping();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
				}
				populateNodeMapping();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (gaBN != null) {
					gaBN.setName(elementNameBN.getText().trim());
					gaBN.setLabel(elementNameBN.getText().trim());
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
					populateNodeMapping();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					populateNodeMapping();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					populateNodeMapping();
				}
			}
		});

		JPanel biologicalPanel = new JPanel();
		JPanel petriPanel = new JPanel();
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, biologicalPanel, petriPanel);
		final int splitWindowWidth = 600;
		final int splitWindowHeight = 400;
		splitPane.setPreferredSize(new Dimension(splitWindowWidth, splitWindowHeight));
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
		createButtonPanel(buttonPanelBN, true);

		JPanel buttonPanelPN = new JPanel();
		buttonPanelPN.setName("buttonPN");
		buttonPanelPN.setLayout(new MigLayout("", "[grow,center]", ""));
		createButtonPanel(buttonPanelPN, false);
		final VanesaGraphRendererPanel biologicalGraphPane = bn.getGraphRenderer();
		biologicalGraphPane.setMaximumSize(new Dimension(splitWindowWidth - 50, splitWindowHeight - 50));
		biologicalPanel.add(biologicalGraphPane, "wrap 5");

		biologicalPanel.add(buttonPanelBN, "wrap 5");
		biologicalPanel.add(elementInformationBN, "wrap 5");
		biologicalPanel.add(nodeMappingPanel);
		final VanesaGraphRendererPanel petriGraphPane = pn.getGraphRenderer();
		petriGraphPane.setMaximumSize(new Dimension(splitWindowWidth - 50, splitWindowHeight - 50));
		petriPanel.add(petriGraphPane, "wrap 5");
		petriPanel.add(buttonPanelPN, "wrap 5");
		petriPanel.add(elementInformationPN, "wrap 5");
		petriPanel.add(parametersPanel);

		MigLayout layout = new MigLayout("", "[grow][grow]", "");
		JPanel panel = new JPanel();
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
		btnSetEdges.setToolTipText(
				"Set edges to be not considered for matching in rules anymore (default: all edges)!");

		btnHighlightEdges.setActionCommand("highlightEdges");
		btnHighlightEdges.addActionListener(this);
		btnHighlightEdges.setToolTipText(
				"highlights the set of edges which are not considered for further rule matching anymore");

		populateNodeMapping();

		final JButton cancel = new JButton("cancel");
		cancel.addActionListener(al);
		cancel.addActionListener(this);
		cancel.setActionCommand("cancelRE");

		final JButton okButton = new JButton("save");
		okButton.addActionListener(al);
		okButton.addActionListener(this);
		okButton.setActionCommand("okButtonRE");

		final JButton saveCopy = new JButton("save copy");
		saveCopy.addActionListener(al);
		saveCopy.addActionListener(this);
		saveCopy.setActionCommand("saveCopy");

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.add(panel);
		scrollPane.setPreferredSize(new Dimension(1200, 800));
		scrollPane.setViewportView(panel);
		scrollPane.setVisible(true);

		final JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(new JButton[] { okButton, saveCopy, cancel });

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
		bn.getGraphRenderer().zoomAndCenterGraph();
		pn.getGraphRenderer().zoomAndCenterGraph();
	}

	public void fillGraphPane(JPanel graphPanel, JLabel label) {
		MigLayout layout = new MigLayout("", "[grow]", "");
		graphPanel.setLayout(layout);
		graphPanel.add(label, "wrap 5, align center");
		graphPanel.add(new JSeparator(), "span,growx,wrap 5");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("okButtonRE")) {
			// CHRIS perform consistency check first
			if (bn.hasGotAtLeastOneElement()) {
				convertGraphToRule(false);
				frame.setVisible(false);
			} else {
				PopUpDialog.getInstance().show("Empty Graph", "The biological pattern of a rule must not be empty!");
			}
		} else if (e.getActionCommand().equals("saveCopy")) {
			convertGraphToRule(true);
		} else if (e.getActionCommand().equals("cancelRE")) {
			frame.setVisible(false);
		} else if (e.getActionCommand().equals("addMapping")) {
			addNodeMapping();
		} else if (e.getActionCommand().equals("highlightEdges")) {
			bn.getGraph2().selectEdges(consideredEdges);
		} else if (e.getActionCommand().equals("setEdges")) {
			final VanesaGraph bnGraph = bn.getGraph2();
			consideredEdges.clear();
			consideredEdges.addAll(bnGraph.getSelectedEdges());
			// add also edges if both, from and to, are also picked
			for (final BiologicalEdgeAbstract bea : bnGraph.getEdges()) {
				if (bnGraph.isSelected(bea.getFrom()) && bnGraph.isSelected(bea.getTo())) {
					consideredEdges.add(bea);
				}
			}
			revalidateSelectedEdges();
		} else if (e.getActionCommand().equals("setAllEdges")) {
			for (BiologicalEdgeAbstract bea : bn.getAllEdges()) {
				consideredEdges.add(bea);
				bn.getGraph2().selectEdges(true, bea);
			}
			revalidateSelectedEdges();
		} else if (e.getActionCommand().startsWith("del_")) {
			final int idx = Integer.parseInt(e.getActionCommand().substring(4));
			BiologicalNodeAbstract node = null;
			for (final BiologicalNodeAbstract bna : bnToPn.keySet()) {
				if (bna.getID() == idx) {
					node = bna;
					break;
				}
			}
			if (node != null) {
				bnToPn.remove(node);
				populateNodeMapping();
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
			final var selectedNodes = bn.getGraph2().getSelectedNodes();
			if (selectedNodes.size() == 1) {
				if (exactIncidenceChk.isSelected()) {
					exactIncidence.add(selectedNodes.iterator().next());
				} else {
					exactIncidence.remove(selectedNodes.iterator().next());
				}
			}
		} else if (e.getActionCommand().equals("isDirected")) {
			final var selectedEdges = bn.getGraph2().getSelectedEdges();
			if (selectedEdges.size() == 1) {
				selectedEdges.iterator().next().setDirected(isDirected.isSelected());
				bn.updateMyGraph();
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
		this.bn.getGraph().getVisualizationViewer().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteBNSelection();
				}
			}
		});
		trash.setActionCommand("del");
		trash.addActionListener(this);
		panel.add(trash);

		JButton center = new ToolBarButton(ImagePath.getInstance().getImageIcon("centerGraph.png"));
		center.setToolTipText("Center graph");
		center.setActionCommand("center");
		center.addActionListener(listener);
		panel.add(center);

		if (bn) {
			JButton edit = new ToolBarButton(ImagePath.getInstance().getImageIcon("TitleGraph.png"));
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

			JButton continuousPlace = new ToolBarButton(ImagePath.getInstance().getImageIcon("continuousPlace.png"));
			continuousPlace.setToolTipText("Continuous Place");
			continuousPlace.setActionCommand("continuousPlace");
			continuousPlace.addActionListener(listener);
			panel.add(continuousPlace);

			JButton place = new ToolBarButton(ImagePath.getInstance().getImageIcon("anyPlace.png"));
			place.setToolTipText("Any place");
			place.setActionCommand("place");
			place.addActionListener(listener);
			panel.add(place);

			JButton discreteTransition = new ToolBarButton(
					ImagePath.getInstance().getImageIcon("discreteTransition.png"));
			discreteTransition.setToolTipText("Discrete Transition");
			discreteTransition.setActionCommand("discreteTransition");
			discreteTransition.addActionListener(listener);
			panel.add(discreteTransition);

			JButton continuousTransition = new ToolBarButton(
					ImagePath.getInstance().getImageIcon("continuousTransition.png"));
			continuousTransition.setToolTipText("Continuous Transition");
			continuousTransition.setActionCommand("continuousTransition");
			continuousTransition.addActionListener(listener);
			panel.add(continuousTransition);

			JButton stochasticTransition = new ToolBarButton(
					ImagePath.getInstance().getImageIcon("stochasticTransition.png"));
			stochasticTransition.setToolTipText("Stochastic Transition");
			stochasticTransition.setActionCommand("stochasticTransition");
			stochasticTransition.addActionListener(listener);
			panel.add(stochasticTransition);

			JButton transition = new ToolBarButton(ImagePath.getInstance().getImageIcon("anyTransition.png"));
			transition.setToolTipText("transition");
			transition.setToolTipText("Any Transition");
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
		final var selectedBNNodes = bn.getGraph2().getSelectedNodes();
		final var selectedPNNodes = pn.getGraph2().getSelectedNodes();
		if (selectedBNNodes.size() == 1 && selectedPNNodes.size() == 1) {
			BiologicalNodeAbstract bnBNA = selectedBNNodes.iterator().next();
			BiologicalNodeAbstract pnBNA = selectedPNNodes.iterator().next();
			if (bnToPn.containsKey(bnBNA)) {
				PopUpDialog.getInstance().show("Error", "Biological node " + bnBNA.getName() + "is mapped already!");
				return;
			} else if (bnToPn.containsValue(pnBNA)) {
				PopUpDialog.getInstance().show("Error", "Petri net node " + pnBNA.getName() + "is mapped already!");
				return;
			} else if (bnBNA.getName().isEmpty()) {
				PopUpDialog.getInstance().show("Error", "Biological node needs a valid name (not empty)!");
				return;
			} else if (pnBNA.getName().isEmpty()) {
				PopUpDialog.getInstance().show("Error", "Petri net node needs a valid name (not empty)!");
				return;
			}
			bnToPn.put(bnBNA, pnBNA);
			addNodeMappingDefaultParameters(bnBNA, pnBNA);
			populateNodeMapping();
			populateTransformationParameterPanel();
		}
	}

	private void addNodeMappingDefaultParameters(BiologicalNodeAbstract bnBNA, BiologicalNodeAbstract pnBNA) {
		getParameterMapping(pnBNA).put("name", bnBNA.getName() + ".name");
		if (pnBNA instanceof Place) {
			getParameterMapping(pnBNA).put("tokenStart", bnBNA.getName() + ".concentrationStart");
			getParameterMapping(pnBNA).put("tokenMin", bnBNA.getName() + ".concentrationMin");
			getParameterMapping(pnBNA).put("tokenMax", bnBNA.getName() + ".concentrationMax");
			getParameterMapping(pnBNA).put("isConstant", bnBNA.getName() + ".isConstant");
		} else if (pnBNA instanceof Transition) {
			if (pnBNA instanceof DiscreteTransition || pnBNA instanceof ANYTransition) {
			} else if (pnBNA instanceof ContinuousTransition) {
				if (bnBNA instanceof DynamicNode) {
					getParameterMapping(pnBNA).put("maximalSpeed", bnBNA.getName() + ".maximalSpeed");
				}
			}
			if (bnBNA instanceof DynamicNode) {
				getParameterMapping(pnBNA).put("isKnockedOut", bnBNA.getName() + ".isKnockedOut");
			}
		}
		// put edge mapping if there is an edge between both mapped nodes
		BiologicalNodeAbstract oppositeBNode;
		BiologicalNodeAbstract oppositePNode;
		for (BiologicalEdgeAbstract bEdge : bn.getGraph2().getIncidentEdges(bnBNA)) {
			for (BiologicalEdgeAbstract pEdge : pn.getGraph2().getIncidentEdges(pnBNA)) {
				oppositeBNode = bn.getGraph2().getOpposite(bnBNA, bEdge);
				oppositePNode = pn.getGraph2().getOpposite(pnBNA, pEdge);
				if (bnToPn.get(oppositeBNode) == oppositePNode) {
					// System.out.println("match");
					getParameterMapping(pEdge).put("function", bEdge.getName() + ".function");
				}
			}
		}
	}

	private void createGraphs() {
		if (rule.isBNEmpty()) {
			bn.getGraph().setMouseModeEditing();
			lblSetEdges.setText("[all edges]");
		} else {
			bn.getGraph().setMouseModePick();
		}
		GraphContainer.getInstance().addPathway(bn.getName(), bn);
		if (rule.isPNEmpty()) {
			pn.getGraph().setMouseModeEditing();
		} else {
			pn.getGraph().setMouseModePick();
		}
		GraphContainer.getInstance().addPathway(pn.getName(), pn);
		PickedState<BiologicalNodeAbstract> vertexStateBN = bn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> edgeStateBN = bn.getGraph().getVisualizationViewer().getPickedEdgeState();
		PickedState<BiologicalNodeAbstract> vertexStatePN = pn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> edgeStatePN = pn.getGraph().getVisualizationViewer().getPickedEdgeState();

		vertexStateBN.addItemListener(e -> {
			if (vertexStateBN.getPicked().size() == 1 && vertexStatePN.getPicked().size() == 1) {
				btnAddMapping.setEnabled(
						!bnToPn.containsKey(vertexStateBN.getPicked().iterator().next()) && !bnToPn.containsValue(
								vertexStatePN.getPicked().iterator().next()));
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
		});
		edgeStateBN.addItemListener(e -> {
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
		});

		vertexStatePN.addItemListener(e -> {
			if (vertexStateBN.getPicked().size() == 1 && vertexStatePN.getPicked().size() == 1) {
				btnAddMapping.setEnabled(
						!bnToPn.containsKey(vertexStateBN.getPicked().iterator().next()) && !bnToPn.containsValue(
								vertexStatePN.getPicked().iterator().next()));
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
		});
		edgeStatePN.addItemListener(e -> {
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
		});
		for (KeyListener k : bn.getGraph().getVisualizationViewer().getKeyListeners()) {
			bn.getGraph().getVisualizationViewer().removeKeyListener(k);
		}
	}

	private void revalidateSelectedEdges() {
		final StringBuilder text = new StringBuilder("[");
		for (BiologicalEdgeAbstract bea : consideredEdges) {
			if (text.length() > 1) {
				text.append(",");
			}
			text.append(bea.getName());
		}
		text.append("]");
		if (consideredEdges.size() == bn.getAllEdges().size()) {
			lblSetEdges.setText("[all edges]");
			chkAllEdgesSelected.setEnabled(false);
		} else if (consideredEdges.isEmpty()) {
			lblSetEdges.setText("[none]");
			chkAllEdgesSelected.setEnabled(true);
		} else {
			lblSetEdges.setText(text.toString());
			chkAllEdgesSelected.setEnabled(true);
		}
		lblSetEdges.repaint();
	}

	private void populateGraph() {
		// for BN
		// put nodes
		Map<String, BiologicalNodeAbstract> nameToBN = new HashMap<>();
		for (int i = 0; i < rule.getBiologicalNodes().size(); i++) {
			RuleNode rn = rule.getBiologicalNodes().get(i);
			BiologicalNodeAbstract bna = BiologicalNodeAbstractFactory.create(rn.getType(), rn.getName(), rn.getName(),
					bn);
			nameToBN.put(bna.getName(), bna);
			bn.addVertex(bna, new Point2D.Double(rn.getX(), rn.getY()));
			if (rn.isExactIncidence()) {
				exactIncidence.add(bna);
			}
			// biologicalNodeBox.addItem(bna.getName());
		}
		// put edges
		Map<RuleEdge, BiologicalEdgeAbstract> edgesMap = new HashMap<>();
		for (int i = 0; i < rule.getBiologicalEdges().size(); i++) {
			RuleEdge re = rule.getBiologicalEdges().get(i);
			BiologicalEdgeAbstract bea = BiologicalEdgeAbstractFactory.create(re.getType(), re.getName(), re.getName(),
					nameToBN.get(re.getFrom().getName()), nameToBN.get(re.getTo().getName()));
			bea.setDirected(re.isDirected());
			bn.addEdge(bea);
			edgesMap.put(re, bea);
		}
		bn.updateMyGraph();

		// for PN
		Map<String, BiologicalNodeAbstract> nameToPN = new HashMap<>();
		for (int i = 0; i < rule.getPetriNodes().size(); i++) {
			RuleNode rn = rule.getPetriNodes().get(i);
			BiologicalNodeAbstract bna = BiologicalNodeAbstractFactory.create(rn.getType(), rn.getName(), rn.getName(),
					pn);
			nameToPN.put(bna.getName(), bna);
			pn.addVertex(bna, new Point2D.Double(rn.getX(), rn.getY()));
			for (String key : rn.getParameterMap().keySet()) {
				getParameterMapping(bna).put(key, rn.getParameterMap().get(key));
			}
		}

		// put edges
		for (int i = 0; i < rule.getPetriEdges().size(); i++) {
			RuleEdge re = rule.getPetriEdges().get(i);
			BiologicalEdgeAbstract bea = BiologicalEdgeAbstractFactory.create(re.getType(), re.getName(), re.getName(),
					nameToPN.get(re.getFrom().getName()), nameToPN.get(re.getTo().getName()));
			bea.setDirected(true);
			pn.addEdge(bea);
			for (String key : re.getParameterMap().keySet()) {
				getParameterMapping(bea).put(key, re.getParameterMap().get(key));
			}
		}
		pn.updateMyGraph();

		for (RuleNode rn : rule.getBnToPnMapping().keySet()) {
			String bn = rn.getName();
			String pn = rule.getBnToPnMapping().get(rn).getName();
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
		Set<BiologicalNodeAbstract> toDelete = new HashSet<>();
		for (BiologicalNodeAbstract bna : bnToPn.keySet()) {
			if (bn.contains(bna) && pn.contains(bnToPn.get(bna))) {
				nodeMappingPanel.add(new JLabel(bna.getName()));
				nodeMappingPanel.add(new JLabel(bnToPn.get(bna).getName()));
				JButton btnDelete = new JButton("delete");
				btnDelete.addActionListener(this);
				btnDelete.setActionCommand("del_" + bna.getID());
				nodeMappingPanel.add(btnDelete, "wrap");
			} else {
				toDelete.add(bna);
			}
		}
		for (BiologicalNodeAbstract biologicalNodeAbstract : toDelete) {
			bnToPn.remove(biologicalNodeAbstract);
		}
		nodeMappingPanel.add(new JSeparator(), "span,growx,wrap 5");
		nodeMappingPanel.add(new JLabel("Considered edges:"), "grow");
		nodeMappingPanel.add(chkAllEdgesSelected, "grow");
		nodeMappingPanel.add(lblSetEdges, "grow, wrap");
		nodeMappingPanel.add(btnHighlightEdges);
		nodeMappingPanel.add(btnSetEdges, "wrap");
		revalidateSelectedEdges();
		nodeMappingPanel.repaint();
		frame.pack();
		frame.repaint();
	}

	private void populateTransformationParameterPanel() {
		parametersPanel.removeAll();
		parametersPanel.add(new JLabel("Set parameters for Petri net elements:"), "");
		parametersPanel.add(new JSeparator(), "span 2, growx, wrap");

		final VanesaGraph pnGraph = pn.getGraph2();
		if (pnGraph.getSelectedNodes().size() == 1) {
			final BiologicalNodeAbstract bna = pnGraph.getSelectedNodes().iterator().next();
			final List<String> params = bna.getTransformationParameters();
			// set default values for newly added nodes
			for (final String param : params) {
				final JTextField field = new JTextField(20);
				field.setText(getParameterMapping(bna).get(param));
				field.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent e) {
						getParameterMapping(bna).put(param, field.getText().trim());
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						getParameterMapping(bna).put(param, field.getText().trim());
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						getParameterMapping(bna).put(param, field.getText().trim());
					}
				});
				addTooltip(field, param);
				parametersPanel.add(new JLabel(param + ":"), "");
				parametersPanel.add(field, "wrap");
			}
		} else if (pnGraph.getSelectedEdges().size() == 1) {
			final BiologicalEdgeAbstract bea = pnGraph.getSelectedEdges().iterator().next();
			final List<String> params = bea.getTransformationParameters();
			// set default values for newly added nodes
			for (String param : params) {
				JTextField field = new JTextField(20);
				field.setText(getParameterMapping(bea).get(param));
				field.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void removeUpdate(DocumentEvent e) {
						getParameterMapping(bea).put(param, field.getText().trim());
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						getParameterMapping(bea).put(param, field.getText().trim());
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						getParameterMapping(bea).put(param, field.getText().trim());
					}
				});
				parametersPanel.add(new JLabel(param + ":"), "");
				parametersPanel.add(field, "wrap");
			}
		}
		parametersPanel.revalidate();
	}

	private void addTooltip(JTextField tf, String parameter) {
		switch (parameter) {
		case "name":
			tf.setToolTipText("evaluated to string during transformation");
			break;
		case "tokenStart":
			tf.setToolTipText("evaluated to number during transformation, e.g. 1 + random(2,5)");
			break;
		case "tokenMin":
		case "tokenMax":
			tf.setToolTipText("evaluated to number during transformation");
			break;
		case "isConstant":
			tf.setToolTipText("evaluated to boolean during transformation, e.g. and(true,or(true,not(false))");
			break;
		case "isKnockedOut":
			tf.setToolTipText("evaluated to boolean during transformation");
			break;
		case "firingCondition":
		case "maximalSpeed":
		case "function":
			tf.setToolTipText("evaluated during simulation");
			break;
		}
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
		Map<BiologicalNodeAbstract, RuleNode> bnaToBNRuleNode = new HashMap<>();
		for (int i = 0; i < bn.getAllGraphNodesSortedAlphabetically().size(); i++) {
			BiologicalNodeAbstract bna = bn.getAllGraphNodesSortedAlphabetically().get(i);
			RuleNode rn = new RuleNode();
			rn.setName(bna.getName());
			rn.setType(bna.getBiologicalElement());
			rn.setX(bn.getGraph2().getNodePosition(bna).getX());
			rn.setY(bn.getGraph2().getNodePosition(bna).getY());
			rn.setExactIncidence(exactIncidence.contains(bna));
			rule.addBiologicalNode(rn);
			bnaToBNRuleNode.put(bna, rn);
		}
		Map<BiologicalEdgeAbstract, RuleEdge> beaToRuleEdge = new HashMap<>();
		for (int i = 0; i < bn.getAllEdgesSortedByID().size(); i++) {
			BiologicalEdgeAbstract bea = bn.getAllEdgesSortedByID().get(i);
			RuleEdge re = new RuleEdge(bea.getName(), bea.getBiologicalElement(), bnaToBNRuleNode.get(bea.getFrom()),
					bnaToBNRuleNode.get(bea.getTo()));
			re.setDirected(bea.isDirected());
			rule.addBiologicalEdge(re);
			beaToRuleEdge.put(bea, re);
		}

		// for PN
		Map<BiologicalNodeAbstract, RuleNode> bnaToPNRuleNode = new HashMap<>();
		for (int i = 0; i < pn.getAllGraphNodesSortedAlphabetically().size(); i++) {
			BiologicalNodeAbstract bna = pn.getAllGraphNodesSortedAlphabetically().get(i);
			RuleNode rn = new RuleNode();
			rn.setName(bna.getName());
			rn.setType(bna.getBiologicalElement());
			rn.setX(pn.getGraph2().getNodePosition(bna).getX());
			rn.setY(pn.getGraph2().getNodePosition(bna).getY());
			rule.addPetriNode(rn);
			bnaToPNRuleNode.put(bna, rn);
			for (String param : getParameterMapping(bna).keySet()) {
				rn.getParameterMap().put(param, getParameterMapping(bna).get(param));
			}
		}
		for (int i = 0; i < pn.getAllEdgesSortedByID().size(); i++) {
			BiologicalEdgeAbstract bea = pn.getAllEdgesSortedByID().get(i);
			RuleEdge re = new RuleEdge(bea.getName(), bea.getBiologicalElement(), bnaToPNRuleNode.get(bea.getFrom()),
					bnaToPNRuleNode.get(bea.getTo()));
			rule.addPetriEdge(re);
			for (String param : getParameterMapping(bea).keySet()) {
				re.getParameterMap().put(param, getParameterMapping(bea).get(param));
			}
		}

		// mapping BN nodes to PN nodes
		for (BiologicalNodeAbstract bna : bnToPn.keySet()) {
			rule.addBNtoPNMapping(bnaToBNRuleNode.get(bna), bnaToPNRuleNode.get(bnToPn.get(bna)));
		}

		// considered edges
		if (chkAllEdgesSelected.isSelected()) {
			for (BiologicalEdgeAbstract edge : bn.getAllEdges()) {
				rule.getConsideredEdges().add(beaToRuleEdge.get(edge));
			}
		} else {
			for (BiologicalEdgeAbstract edge : consideredEdges) {
				rule.getConsideredEdges().add(beaToRuleEdge.get(edge));
			}
		}
	}

	private void deleteBNSelection() {
		for (BiologicalEdgeAbstract bea : bn.getGraph2().getSelectedEdges()) {
			consideredEdges.remove(bea);
		}
		for (BiologicalNodeAbstract bna : bn.getGraph2().getSelectedNodes()) {
			bnToPn.remove(bna);
			for (BiologicalEdgeAbstract edge : bn.getGraph2().getIncidentEdges(bna)) {
				consideredEdges.remove(edge);
			}
		}
		bn.removeSelection();
		revalidateSelectedEdges();
		populateNodeMapping();
	}

	private void createDefaultParameterMap(GraphElementAbstract gea) {
		if (parameterMapping.get(gea) == null) {
			// for nodes
			if (gea instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) gea;
				parameterMapping.put(gea, new HashMap<>());
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
					case "isConstant":
						parameterMapping.get(bna).put(key, bna.isConstant() + "");
						break;
					case "firingCondition":
						parameterMapping.get(bna).put(key, ((Transition) bna).getFiringCondition());
						break;
					case "isKnockedOut":
						parameterMapping.get(bna).put(key, ((Transition) bna).isKnockedOut() + "");
						break;
					case "maximalSpeed":
						if (bna instanceof ContinuousTransition) {
							parameterMapping.get(bna).put(key, ((ContinuousTransition) bna).getMaximalSpeed());
						} else if (bna instanceof ANYTransition) {
							parameterMapping.get(bna).put(key, "1");
						}
						break;
					case "delay":
						if (bna instanceof DiscreteTransition) {
							parameterMapping.get(bna).put(key, ((DiscreteTransition) bna).getDelay());
						} else if (bna instanceof ANYTransition) {
							parameterMapping.get(bna).put(key, "1");
						}
						break;
					}
				}
				// for edges
			} else if (gea instanceof BiologicalEdgeAbstract) {
				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) gea;
				parameterMapping.put(bea, new HashMap<>());
				for (String key : bea.getTransformationParameters()) {
					switch (key) {
					case "function":
						parameterMapping.get(bea).put(key, "1");
						break;
					}
				}
			}
		}
	}

	private boolean checkGraphNames() {
		final Set<String> set = new HashSet<>();
		final StringBuilder errorMessage = new StringBuilder();
		for (final BiologicalEdgeAbstract bea : bn.getAllEdges()) {
			if (bea.getName().isEmpty()) {
				errorMessage.append("- at least one edge in the biological pattern has an empty name\r\n");
			} else if (set.contains(bea.getName())) {
				errorMessage.append("- name of edge +").append(bea.getName()).append(" is not unique.\r\n");
			} else {
				set.add(bea.getName());
			}
		}
		for (final BiologicalNodeAbstract bna : bn.getAllGraphNodes()) {
			if (bna.getName().isEmpty()) {
				errorMessage.append("- at least one node in the biological pattern has an empty name\r\n");
			} else if (set.contains(bna.getName())) {
				errorMessage.append("- name of node ").append(bna.getName()).append(" is not unique.\r\n");
			} else {
				set.add(bna.getName());
			}
		}
		for (final BiologicalEdgeAbstract bea : pn.getAllEdges()) {
			if (bea.getName().isEmpty()) {
				errorMessage.append("- at least one edge in the Petri net has an empty name\r\n");
			} else if (set.contains(bea.getName())) {
				errorMessage.append("- name of edge ").append(bea.getName()).append(" is not unique.\r\n");
			} else {
				set.add(bea.getName());
			}
		}
		for (final BiologicalNodeAbstract bna : pn.getAllGraphNodes()) {
			if (bna.getName().isEmpty()) {
				errorMessage.append("- at least one node in the Petri net has an empty name\r\n");
			} else if (set.contains(bna.getName())) {
				errorMessage.append("- name of node ").append(bna.getName()).append(" is not unique.\r\n");
			} else {
				set.add(bna.getName());
			}
		}
		if (errorMessage.length() == 0) {
			return true;
		}
		errorMessage.insert(0,
				"All names of edges and nodes in the biological pattern and in the Petri net must be non-empty and unique!\r\n\r\n");
		PopUpDialog.getInstance().show("Error saving rule!", errorMessage.toString());
		return false;
	}

	public Rule getRule() {
		return this.rule;
	}

	private Map<String, String> getParameterMapping(GraphElementAbstract gea) {
		if (parameterMapping.get(gea) == null) {
			createDefaultParameterMap(gea);
		}
		return parameterMapping.get(gea);
	}
}
