package transformation.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import biologicalObjects.edges.BiologicalEdgeAbstractFactory;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstractFactory;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphContainer;
import gui.MainWindow;
import gui.ToolBarButton;
import gui.images.ImagePath;
import net.miginfocom.swing.MigLayout;
import transformation.Rule;
import transformation.RuleEdge;
import transformation.RuleNode;

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

	private JFrame frame;

	private JSplitPane splitPane;

	private GraphZoomScrollPane firstGraphPane;

	private GraphZoomScrollPane secondGraphPane;

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
	

	// TODO no empty graph in main window allowed, at least one instance must be
	// opened
	private Rule rule;

	public RuleEditingWindow(Rule rule, ActionListener al) {
		
		
		
		if(rule != null){
			this.rule = rule;
		}else{
			rule = new Rule();
		}
		
		this.createGraphs();
		populate();

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
		
		elementNamePN.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					pn.getGraph().getVisualizationViewer().repaint();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					pn.getGraph().getVisualizationViewer().repaint();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (gaPN != null) {
					gaPN.setName(elementNamePN.getText().trim());
					gaPN.setLabel(elementNamePN.getText().trim());
					pn.getGraph().getVisualizationViewer().repaint();
				}
			}
		});

		firstBox.setEditable(false);
		secondBox.setEditable(false);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, firstGraph, secondGraph);

		splitWindowWith = 600; // screenWidth - 150;
		splitWindowHeight = 400; // screenHeight - 200;

		splitPane.setPreferredSize(new Dimension(splitWindowWith, splitWindowHeight));

		splitPane.setOneTouchExpandable(true);

		fillGraphPane(firstGraph, firstBox, new JLabel("Biological graph pattern for matching"));
		fillGraphPane(secondGraph, secondBox, new JLabel("Petri net pattern"));

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
		secondGraph.add(elementInformationPN, "wrap 5");
		
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
		
		JPanel ruleNamePanel = new JPanel();
		ruleNamePanel.setLayout(new MigLayout("fillx", "[grow,fill]", ""));
		
		
		ruleNamePanel.add(new JLabel("Rule name: "));
		ruleNamePanel.add(ruleName, "align left, span, wrap");
		
		panel.add(ruleNamePanel, "wrap 10");
		panel.add(new JSeparator(), "growx, span");
		
		panel.add(splitPane, "growx, span,wrap 10");
		panel.add(new JSeparator(), "growx, span");

		
		cancel.addActionListener(al);
		cancel.addActionListener(this);
		cancel.setActionCommand("cancelRE");

		okButton.addActionListener(al);
		okButton.addActionListener(this);
		okButton.setActionCommand("okButtonRE");
		
		optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptions(buttons);
		
		frame = new JFrame("Edit or create a new Rule");
		frame.setAlwaysOnTop(false);
		frame.setContentPane(optionPane);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.revalidate();
		
		frame.pack();
		
		frame.setLocationRelativeTo(MainWindow.getInstance());
		
		frame.pack();
		splitPane.setDividerLocation(0.5);
		frame.setVisible(true);
		bn.getGraph().normalCentering();
		pn.getGraph().normalCentering();
	}

	public void fillGraphPane(JPanel graphPanel, JComboBox<String> box, JLabel label) {
		MigLayout layout = new MigLayout("", "[grow]", "");
		graphPanel.setLayout(layout);
		graphPanel.add(label, "wrap 5, align center");
		//graphPanel.add(box, "span, growx, wrap 5, align center");
		graphPanel.add(new JSeparator(), "span,growx,wrap 5");
	}

	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals("okButtonRE")){
			convertGraphToRule();
			frame.setVisible(false);
		} else if(e.getActionCommand().equals("cancelRE")){
			frame.setVisible(false);
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
				}else{
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
				}else{
					gaBN = null;
					elementTypeBN.setText("");
					elementNameBN.setText("");
				}
			}
		});
		
		
		PickedState<BiologicalNodeAbstract> vertexStatePN = pn.getGraph().getVisualizationViewer()
				.getPickedVertexState();
		PickedState<BiologicalEdgeAbstract> edgeStatePN = pn.getGraph().getVisualizationViewer().getPickedEdgeState();
		vertexStatePN.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (vertexStatePN.getPicked().size() == 1) {
					BiologicalNodeAbstract bna = vertexStatePN.getPicked().iterator().next();
					gaPN = bna;
					elementTypePN.setText(bna.getBiologicalElement());
					elementNamePN.setText(bna.getName());
				}else{
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
				}else{
					gaPN = null;
					elementTypePN.setText("");
					elementNamePN.setText("");
				}
			}
		});
	}
	
	private void populate(){
		
		// for BN
		// put nodes
		RuleNode rn;
		BiologicalNodeAbstract bna;
		Map<String, BiologicalNodeAbstract> nameToBN = new HashMap<String, BiologicalNodeAbstract>();
		for(int i = 0; i<rule.getAllBiologicalNodes().size(); i++){
			rn = rule.getAllBiologicalNodes().get(i);
			bna = BiologicalNodeAbstractFactory.create(rn.getType(), null);
			bna.setName(rn.getName());
			bna.setLabel(rn.getName());
			nameToBN.put(bna.getName(), bna);
			bn.addVertex(bna, new Point2D.Double(rn.getX(), rn.getY()));
		}
		
		// put edges
		BiologicalEdgeAbstract bea;
		RuleEdge re;
		for (int i = 0; i < rule.getAllBiologicalEdges().size(); i++) {
			re = rule.getAllBiologicalEdges().get(i);
			bea = BiologicalEdgeAbstractFactory.create(re.getType(), null);
			bea.setFrom(nameToBN.get(re.getFrom().getName()));
			bea.setTo(nameToBN.get(re.getTo().getName()));
			bea.setLabel(re.getName());
			bea.setName(re.getName());
			bea.setDirected(true);
			bn.addEdge(bea);
		}
		bn.updateMyGraph();
		
		
		// for PN
		Map<String, BiologicalNodeAbstract> nameToPN = new HashMap<String, BiologicalNodeAbstract>();
		for(int i = 0; i<rule.getAllPetriNodes().size(); i++){
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
	}
	
	private void convertGraphToRule(){
		rule.setName(this.ruleName.getText().trim());
		rule.getAllBiologicalNodes().clear();
		rule.getAllBiologicalEdges().clear();
		rule.getAllPetriNodes().clear();
		rule.getAllPetriEdges().clear();
		// forBN
		BiologicalNodeAbstract bna;
		RuleNode rn;
		Map<BiologicalNodeAbstract, RuleNode> bnaToRuleNode = new HashMap<BiologicalNodeAbstract, RuleNode>();
		for(int i = 0; i<bn.getAllGraphNodesSortedAlphabetically().size(); i++){
			bna = bn.getAllGraphNodesSortedAlphabetically().get(i);
			rn = new RuleNode();
			rn.setName(bna.getName());
			rn.setType(bna.getBiologicalElement());
			rn.setX(bn.getGraph().getVertexLocation(bna).getX());
			rn.setY(bn.getGraph().getVertexLocation(bna).getY());
			rule.addBiologicalNode(rn);
			bnaToRuleNode.put(bna, rn);
		}
		RuleEdge re;
		BiologicalEdgeAbstract bea;
		for(int i = 0; i<bn.getAllEdgesSorted().size(); i++){
			bea = bn.getAllEdgesSorted().get(i);
			re = new RuleEdge(bea.getName(), bea.getBiologicalElement(), bnaToRuleNode.get(bea.getFrom()), bnaToRuleNode.get(bea.getTo()));
			rule.addBiologicalEdge(re);
		}
		
		// for PN
		bnaToRuleNode = new HashMap<BiologicalNodeAbstract, RuleNode>();
		for(int i = 0; i<pn.getAllGraphNodesSortedAlphabetically().size(); i++){
			bna = pn.getAllGraphNodesSortedAlphabetically().get(i);
			rn = new RuleNode();
			rn.setName(bna.getName());
			rn.setType(bna.getBiologicalElement());
			rn.setX(pn.getGraph().getVertexLocation(bna).getX());
			rn.setY(pn.getGraph().getVertexLocation(bna).getY());
			rule.addPetriNode(rn);
			bnaToRuleNode.put(bna, rn);
		}
		for(int i = 0; i<pn.getAllEdgesSorted().size(); i++){
			bea = pn.getAllEdgesSorted().get(i);
			re = new RuleEdge(bea.getName(), bea.getBiologicalElement(), bnaToRuleNode.get(bea.getFrom()), bnaToRuleNode.get(bea.getTo()));
			rule.addPetriEdge(re);
		}
	}
	
	public Rule getResult(){
		convertGraphToRule();
		return rule;
	}
}
