package transformation.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
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
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphContainer;
import gui.MainWindow;
import gui.MyPopUp;
import gui.ToolBarButton;
import gui.images.ImagePath;
import net.miginfocom.swing.MigLayout;
import transformation.Rule;
import transformation.RuleEdge;
import transformation.RuleNode;

// TODO handle parameter matching
public class RuleEditingWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane = new JScrollPane();
	private JPanel panel = new JPanel();
	private JPanel biologicalPanel = new JPanel();
	private JPanel petriPanel = new JPanel();
	private JPanel nodeMappingPanel = new JPanel();

	private JFrame frame = new JFrame("Edit or create a new Rule");;

	private JSplitPane splitPane;
	private GraphZoomScrollPane biologicalGraphPane;
	private GraphZoomScrollPane petriGraphPane;

	int splitWindowWith = 0;
	int splitWindowHeight = 0;

	private JTextField ruleName = new JTextField(50);

	private Pathway bn = null;
	private Pathway pn = null;

	private JLabel elementTypeBN = new JLabel();
	private JTextField elementNameBN = new JTextField(20);
	private JLabel elementTypePN = new JLabel();
	private JTextField elementNamePN = new JTextField(20);
	private GraphElementAbstract gaBN = null;
	private GraphElementAbstract gaPN = null;

	private RuleEditingWindowListener listener;

	private JButton cancel = new JButton("cancel");
	private JButton okButton = new JButton("ok");
	private JButton[] buttons = { okButton, cancel };
	private JOptionPane optionPane;

	private JButton btnAddMapping = new JButton("add mapping");
	private JButton btnHighlightEdges = new JButton("highlight edges");
	private JButton btnSetEdges = new JButton("set selected edges");
	private JLabel lblSetEdges = new JLabel("[none]");

	private Map<BiologicalNodeAbstract, BiologicalNodeAbstract> bnToPn = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();
	private Rule rule = null;
	private Set<BiologicalEdgeAbstract> consideredEdges = new HashSet<BiologicalEdgeAbstract>();

	private JCheckBox chkAllEdgesSelected = new JCheckBox("all edges");
	private boolean newRule = false;

	public RuleEditingWindow(Rule rule, ActionListener al) {

		if (rule != null) {
			this.rule = rule;
		} else {
			rule = new Rule();
			newRule = true;
		}

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

		JPanel elementInformationBN = new JPanel();
		elementInformationBN.setLayout(new MigLayout("fillx", "[grow,fill]", ""));
		elementInformationBN.add(new JLabel("Selected element type: "));
		elementInformationBN.add(elementTypeBN, "wrap");
		elementInformationBN.add(new JLabel("Selected element name: "));
		elementInformationBN.add(elementNameBN);

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

		MigLayout layout = new MigLayout("", "[grow][grow]", "");
		panel.setLayout(layout);

		JPanel ruleNamePanel = new JPanel();
		ruleNamePanel.setLayout(new MigLayout("fillx", "[grow,fill]", ""));

		ruleNamePanel.add(new JLabel("Rule name: "));
		ruleNamePanel.add(ruleName, "align left, span, wrap");

		panel.add(ruleNamePanel, "wrap 10");
		panel.add(new JSeparator(), "growx, span");

		panel.add(splitPane, "growx, span,wrap 10");
		panel.add(new JSeparator(), "growx, span");

		nodeMappingPanel.setLayout(new MigLayout("fillx", "[grow,fill]"));

		btnAddMapping.setActionCommand("addMapping");
		btnAddMapping.addActionListener(this);
		btnAddMapping.setEnabled(false);
		btnAddMapping.setToolTipText("Select one biological node and one node from the Petri net!");

		chkAllEdgesSelected.setActionCommand("setAllEdges");
		chkAllEdgesSelected.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getItem() instanceof JCheckBox) {
					JCheckBox box = (JCheckBox) e.getItem();
					if (box.isSelected()) {
						Iterator<BiologicalEdgeAbstract> it = bn.getAllEdges().iterator();
						BiologicalEdgeAbstract bea;
						while (it.hasNext()) {
							bea = it.next();
							consideredEdges.add(bea);
							bn.getGraph().getVisualizationViewer().getPickedEdgeState().pick(bea, true);
						}
					} else {
						consideredEdges.clear();
						bn.getGraph().getVisualizationViewer().getPickedEdgeState().clear();
					}
					// nodeMappingPanel.repaint();
					revalidateSelectedEdges();
				}
			}
		});
		chkAllEdgesSelected.setToolTipText("select all edges");
		chkAllEdgesSelected.setSelected(true);

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

		scrollPane.add(panel);
		scrollPane.setPreferredSize(new Dimension(1200, 800));
		scrollPane.setViewportView(panel);
		scrollPane.setVisible(true);

		optionPane = new JOptionPane(scrollPane, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);

		frame.setAlwaysOnTop(false);
		frame.setContentPane(optionPane);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.revalidate();

		frame.pack();

		frame.setLocationRelativeTo(MainWindow.getInstance());

		frame.pack();
		splitPane.setDividerLocation(0.5);
		frame.setVisible(true);
		if (bn.hasGotAtLeastOneElement()) {
			bn.getGraph().normalCentering();
		}
		if (pn.hasGotAtLeastOneElement()) {
			pn.getGraph().normalCentering();
		}
	}

	public void fillGraphPane(JPanel graphPanel, JLabel label) {
		MigLayout layout = new MigLayout("", "[grow]", "");
		graphPanel.setLayout(layout);
		graphPanel.add(label, "wrap 5, align center");
		graphPanel.add(new JSeparator(), "span,growx,wrap 5");
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("okButtonRE")) {
			convertGraphToRule();
			frame.setVisible(false);
		} else if (e.getActionCommand().equals("cancelRE")) {
			frame.setVisible(false);
		} else if (e.getActionCommand().equals("addMapping")) {
			PickedState<BiologicalNodeAbstract> bnPickedState = bn.getGraph().getVisualizationViewer()
					.getPickedVertexState();
			PickedState<BiologicalNodeAbstract> pnPickedState = pn.getGraph().getVisualizationViewer()
					.getPickedVertexState();

			if (bnPickedState.getPicked().size() == 1 && pnPickedState.getPicked().size() == 1) {
				BiologicalNodeAbstract bnBNA = bnPickedState.getPicked().iterator().next();
				BiologicalNodeAbstract pnBNA = pnPickedState.getPicked().iterator().next();
				if (bnToPn.containsKey(bnBNA)) {
					MyPopUp.getInstance().show("Error", "Biological node " + bnBNA.getName() + "is mapped already!");
					return;
				} else if (bnToPn.containsValue(pnBNA)) {
					MyPopUp.getInstance().show("Error", "Petri net node " + pnBNA.getName() + "is mapped already!");
					return;
				} else if (bnBNA.getName().length() < 1) {
					MyPopUp.getInstance().show("Error", "Biological node needs a valid name (not empty)!");
					return;
				} else if (pnBNA.getName().length() < 1) {
					MyPopUp.getInstance().show("Error", "Petri net node needs a valid name (not empty)!");
					return;
				}
				bnToPn.put(bnBNA, pnBNA);
				this.populateNodeMapping();
			}
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
		}
	}

	public void closeDialog() {
		frame.setVisible(false);
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

			JButton place = new ToolBarButton(new ImageIcon(imagePath.getPath("discretePlace.png")));
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

			JButton transition = new ToolBarButton(new ImageIcon(imagePath.getPath("discreteTransition.png")));
			transition.setToolTipText("transition");
			transition.setToolTipText("Any transition");
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
	}

	private void createGraphs() {

		bn = new Pathway("bn", true);
		if (newRule) {
			bn.getGraph().setMouseModeEditing();
			lblSetEdges.setText("[all edges]");
		} else {
			bn.getGraph().setMouseModePick();
		}
		String newPathwayName = GraphContainer.getInstance().addPathway(bn.getName(), bn);
		bn = GraphContainer.getInstance().getPathway(newPathwayName);

		pn = new Pathway("petriNet", true);
		pn.setPetriNet(true);
		if (newRule) {
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
					btnAddMapping.setEnabled(true);
				} else {
					btnAddMapping.setEnabled(false);
				}
				if (vertexStateBN.getPicked().size() == 1) {
					BiologicalNodeAbstract bna = vertexStateBN.getPicked().iterator().next();
					gaBN = bna;
					elementTypeBN.setText(bna.getBiologicalElement());
					elementNameBN.setText(bna.getName());
				} else {
					gaBN = null;
					elementTypeBN.setText("");
					elementNameBN.setText("");
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
				} else {
					gaBN = null;
					elementTypeBN.setText("");
					elementNameBN.setText("");
				}
			}
		});

		vertexStatePN.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (vertexStateBN.getPicked().size() == 1 && vertexStatePN.getPicked().size() == 1) {
					btnAddMapping.setEnabled(true);
				} else {
					btnAddMapping.setEnabled(false);
				}
				if (vertexStatePN.getPicked().size() == 1) {
					BiologicalNodeAbstract bna = vertexStatePN.getPicked().iterator().next();
					gaPN = bna;
					elementTypePN.setText(bna.getBiologicalElement());
					elementNamePN.setText(bna.getName());
				} else {
					gaPN = null;
					elementTypePN.setText("");
					elementNamePN.setText("");
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
				} else {
					gaPN = null;
					elementTypePN.setText("");
					elementNamePN.setText("");
				}
			}
		});
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
		if (chkAllEdgesSelected.isSelected() || consideredEdges.size() == bn.getAllEdges().size()) {
			lblSetEdges.setText("[all edges]");
		} else if (consideredEdges.size() == 0) {
			lblSetEdges.setText("[none]");
		} else {
			lblSetEdges.setText(text);
		}
		lblSetEdges.repaint();

	}

	private void populateGraph() {

		// for BN
		// put nodes
		RuleNode rn;
		BiologicalNodeAbstract bna;
		Map<String, BiologicalNodeAbstract> nameToBN = new HashMap<String, BiologicalNodeAbstract>();
		for (int i = 0; i < rule.getAllBiologicalNodes().size(); i++) {
			rn = rule.getAllBiologicalNodes().get(i);
			bna = BiologicalNodeAbstractFactory.create(rn.getType(), null);
			bna.setName(rn.getName());
			bna.setLabel(rn.getName());
			nameToBN.put(bna.getName(), bna);
			bn.addVertex(bna, new Point2D.Double(rn.getX(), rn.getY()));
			// biologicalNodeBox.addItem(bna.getName());
		}

		// put edges
		BiologicalEdgeAbstract bea;
		RuleEdge re;
		Map<RuleEdge, BiologicalEdgeAbstract> edgesMap = new HashMap<RuleEdge, BiologicalEdgeAbstract>();
		for (int i = 0; i < rule.getAllBiologicalEdges().size(); i++) {
			re = rule.getAllBiologicalEdges().get(i);
			bea = BiologicalEdgeAbstractFactory.create(re.getType(), null);
			bea.setFrom(nameToBN.get(re.getFrom().getName()));
			bea.setTo(nameToBN.get(re.getTo().getName()));
			bea.setLabel(re.getName());
			bea.setName(re.getName());
			bea.setDirected(true);
			bn.addEdge(bea);
			edgesMap.put(re, bea);
		}
		bn.updateMyGraph();

		// for PN
		Map<String, BiologicalNodeAbstract> nameToPN = new HashMap<String, BiologicalNodeAbstract>();
		for (int i = 0; i < rule.getAllPetriNodes().size(); i++) {
			rn = rule.getAllPetriNodes().get(i);
			bna = BiologicalNodeAbstractFactory.create(rn.getType(), null);
			bna.setName(rn.getName());
			bna.setLabel(rn.getName());
			nameToPN.put(bna.getName(), bna);
			pn.addVertex(bna, new Point2D.Double(rn.getX(), rn.getY()));
		}

		// put edges
		for (int i = 0; i < rule.getAllPetriEdges().size(); i++) {
			re = rule.getAllPetriEdges().get(i);
			bea = BiologicalEdgeAbstractFactory.create(re.getType(), null);
			bea.setFrom(nameToPN.get(re.getFrom().getName()));
			bea.setTo(nameToPN.get(re.getTo().getName()));
			bea.setLabel(re.getName());
			bea.setName(re.getName());
			bea.setDirected(true);
			bn.addEdge(bea);
		}
		pn.updateMyGraph();

		String bn;
		String pn;
		Iterator<RuleNode> it = rule.getBNtoPNMapping().keySet().iterator();
		while (it.hasNext()) {
			rn = it.next();
			bn = rn.getName();
			pn = rule.getBNtoPNMapping().get(rn).getName();
			bnToPn.put(nameToBN.get(bn), nameToPN.get(pn));
		}

		for (int i = 0; i < rule.getConsideredEdges().size(); i++) {
			consideredEdges.add(edgesMap.get(rule.getConsideredEdges().get(i)));
		}
	}

	private void populateNodeMapping() {
		nodeMappingPanel.removeAll();

		nodeMappingPanel.add(new JLabel("Mapping of biological node to node of Petri net:"), "span 2,wrap");

		nodeMappingPanel.add(btnAddMapping, "wrap");
		nodeMappingPanel.add(new JLabel("Biological Node:"));
		nodeMappingPanel.add(new JLabel("Petri net Node:"), "wrap");

		// print actual mapping
		Iterator<BiologicalNodeAbstract> it = bnToPn.keySet().iterator();
		BiologicalNodeAbstract bna;
		Set<BiologicalNodeAbstract> toDelete = new HashSet<BiologicalNodeAbstract>();
		while (it.hasNext()) {
			bna = it.next();
			if (bn.containsVertex(bna) && pn.containsVertex(bnToPn.get(bna))) {
				nodeMappingPanel.add(new JLabel(bna.getName()));
				nodeMappingPanel.add(new JLabel(bnToPn.get(bna).getName()), "wrap");
			} else {
				toDelete.add(bna);
			}
		}
		// two steps, otherwise concurrent transaction of iteration and deletion
		it = toDelete.iterator();
		while(it.hasNext()){
			bnToPn.remove(it.next());
		}
		
		nodeMappingPanel.add(new JSeparator(), "span,growx,wrap 5");
		nodeMappingPanel.add(new JLabel("Considered Edges:"), "grow");
		nodeMappingPanel.add(chkAllEdgesSelected, "grow");
		nodeMappingPanel.add(lblSetEdges, "grow, wrap");
		nodeMappingPanel.add(btnHighlightEdges);
		nodeMappingPanel.add(btnSetEdges, "wrap");

		this.revalidateSelectedEdges();
		nodeMappingPanel.repaint();
		frame.pack();
		frame.repaint();
	}

	private void convertGraphToRule() {
		rule.setName(this.ruleName.getText().trim());
		rule.getAllBiologicalNodes().clear();
		rule.getAllBiologicalEdges().clear();
		rule.getAllPetriNodes().clear();
		rule.getAllPetriEdges().clear();
		rule.getBNtoPNMapping().clear();
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
		}
		for (int i = 0; i < pn.getAllEdgesSorted().size(); i++) {
			bea = pn.getAllEdgesSorted().get(i);
			re = new RuleEdge(bea.getName(), bea.getBiologicalElement(), bnaToPNRuleNode.get(bea.getFrom()),
					bnaToPNRuleNode.get(bea.getTo()));
			rule.addPetriEdge(re);
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
	}

	public Rule getResult() {
		convertGraphToRule();
		return rule;
	}
}
