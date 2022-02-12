package gui.optionPanelWindows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract.NodeAttribute;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.DynamicNode;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.RNA;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.PNNode;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
/*import edu.uci.ics.jung.graph.Edge;
 import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.utils.Pair;*/
import graph.ChangedFlags;
import graph.GraphInstance;
import graph.Compartment.Compartment;
import graph.algorithms.NodeAttributeTypes;
import graph.gui.LabelsWindow;
import graph.gui.ParameterWindow;
import graph.gui.ReferenceDialog;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MyPopUp;
import gui.eventhandlers.PropertyWindowListener;
import net.miginfocom.swing.MigLayout;
import util.MyColorChooser;
import util.MyJFormattedTextField;
import util.MyNumberFormat;
import util.StochasticDistribution;

public class ElementWindow implements ActionListener, ItemListener {

	private JPanel p = new JPanel();
	private GraphElementAbstract ab;
	private GraphInstance graphInstance;
	boolean emptyPane = true;

	private JButton chooseRef;
	private JButton deleteRef;
	private JButton pickOrigin;
	private JButton pickRefs;
	private JButton fillColorButton;
	private JButton plotColorButton;
	private JButton hideNeighbours;
	private JButton showNeighbours;
	private JButton parametersButton;
	private JButton showLabels;

	private JCheckBox constCheck;

	private BiologicalNodeAbstract ref = null;
	private GraphElementAbstract original;

	private JCheckBox knockedOut;
	private MyJFormattedTextField tokenMin;
	private MyJFormattedTextField tokenMax;
	private MyJFormattedTextField concentrationMin;
	private MyJFormattedTextField concentrationMax;

	private JCheckBox isDirected;
	// private Object element;

	public ElementWindow() {

	}

	private void updateWindow(GraphElementAbstract element) {
		p.removeAll();
		// this.element = element;
		// this.ab = (GraphElementAbstract) graphInstance
		// .getPathwayElement(element);
		original = element;
		PropertyWindowListener pwl = new PropertyWindowListener(element);

		JTextField label = new JTextField(20);
		JTextField name = new JTextField(20);

		if (ref != null) {
			this.ab = ref;
			label.setText(ref.getLabel());
			name.setText(ref.getName());
		} else {
			this.ab = original;
			label.setText(ab.getLabel());
			name.setText(ab.getName());
		}

		knockedOut = new JCheckBox();
		isDirected = new JCheckBox();

		hideNeighbours = new JButton("Hide all Neighbours");
		showNeighbours = new JButton("Show all Neighbours");
		parametersButton = new JButton("Parameters");
		showLabels = new JButton("Show Labels");

		fillColorButton = new JButton("Fill color");
		fillColorButton.setBackground(ab.getColor());
		fillColorButton.setToolTipText("Select fill color");
		fillColorButton.setActionCommand("colour");
		fillColorButton.addActionListener(this);

		plotColorButton = new JButton("Plot color");
		if (ab instanceof BiologicalNodeAbstract) {
			plotColorButton.setBackground(((BiologicalNodeAbstract) ab).getPlotColor());
		}
		plotColorButton.setToolTipText("Select plot color");
		plotColorButton.setActionCommand("plotColour");
		plotColorButton.addActionListener(this);

		// System.out.println("label: "+ab.getLabel());
		// System.out.println("name: "+ab.getName());
		label.setName("label");
		name.setName("name");

		label.addFocusListener(pwl);
		name.addFocusListener(pwl);

		MigLayout headerlayout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
		JPanel headerPanel = new JPanel(headerlayout);
		// headerPanel.setBackground(new Color(192, 215, 227));
		headerPanel.add(new JLabel(ab.getBiologicalElement()), "");
		headerPanel.add(new JSeparator(), "gap 10");

		MigLayout layout = new MigLayout("fillx", "[grow,fill]", "");
		p.setLayout(layout);
		p.add(new JLabel("Element"), "gap 5 ");
		p.add(new JLabel(ab.getBiologicalElement()), "wrap,span 1");

		if (MainWindow.developer) {
			p.add(new JLabel("ID"), "gap 5 ");
			JLabel id = new JLabel(ab.getID() + "");
			p.add(id, "wrap ,span 1");
		}

		if (!(ab instanceof PNEdge)) {
			p.add(new JLabel("Label"), "gap 5 ");
			p.add(label, "span 1, wrap");
			p.add(new JLabel("Name"), "gap 5 ");
			p.add(name, "span 1, wrap");
		}
		// JCheckBox transitionfire = new JCheckBox("Should transition fire:",
		// true);
		// JTextField transitionStatement = new JTextField("true");

		if (ab.isVertex()) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			String lbl = "";
			if (bna.hasRef()) {
				lbl = bna.getID() + "_" + bna.getRef().getLabel();
			}
			p.add(new JLabel("Reference:"), "gap 5 ");
			p.add(new JLabel(lbl), "wrap ,span 3");

			if (bna.hasRef()) {
				this.deleteRef = new JButton("Delete Reference");
				deleteRef.setToolTipText("Delete Reference");
				deleteRef.setActionCommand("deleteRef");
				deleteRef.addActionListener(this);
				p.add(deleteRef);
				this.pickOrigin = new JButton("Highlight Origin");
				pickOrigin.setToolTipText("Highlight Origin");
				pickOrigin.setActionCommand("pickOrigin");
				pickOrigin.addActionListener(this);
				p.add(pickOrigin);

			} else {
				this.chooseRef = new JButton("Choose Reference");
				chooseRef.setToolTipText("Choose Reference");
				chooseRef.setActionCommand("chooseRef");
				chooseRef.addActionListener(this);
				p.add(chooseRef);
				if (bna.getRefs().size() > 0) {
					this.pickRefs = new JButton("Highlight References");
					pickRefs.setToolTipText("Highlight References");
					pickRefs.setActionCommand("pickRefs");
					pickRefs.addActionListener(this);
					p.add(pickRefs);
				}
			}

			showLabels.setToolTipText("Show all Labels");
			showLabels.setActionCommand("showLabels");
			showLabels.addActionListener(this);
			p.add(showLabels, "span 1, wrap");

			JComboBox<String> compartment = new JComboBox<String>();
			addCompartmentItems(compartment);
			AutoCompleteDecorator.decorate(compartment);

			Pathway pw = graphInstance.getPathway();

			compartment.setSelectedItem(pw.getCompartmentManager().getCompartment(((BiologicalNodeAbstract) ab)));
			compartment.addItemListener(this);

			p.add(new JLabel("Compartment"), "gap 5 ");
			p.add(compartment, "wrap ,span 1");

			// ADDing Attributes (for all nodes)

			// Show Database IDs
			JTextArea dbids = new JTextArea();
			String dbidstring = new String();
			dbids.setEditable(false);
			dbids.setFont(dbids.getFont().deriveFont(Font.BOLD));
			dbids.setBackground(Color.WHITE);

			// p.add(new JLabel("IDs known:"), "gap 5");
			// p.add(dbids, "wrap, span 3");

			// Show Experiment names and values
			JTextArea experiments = new JTextArea();
			String experimentstring = new String();
			experiments.setEditable(false);
			experiments.setBackground(Color.WHITE);

			// p.add(new JLabel("Dataset:"), "gap 5");
			// p.add(experiments, "wrap, span 3");

			// Show GO annotations
			JTextArea goannoations = new JTextArea();
			String annotationstring = new String();
			goannoations.setEditable(false);
			goannoations.setForeground(Color.BLUE);
			goannoations.setBackground(Color.WHITE);

			// p.add(new JLabel("Gene Ontology:"), "gap 5");
			// p.add(goannoations, "wrap, span 3");

			// Show graph properties (local property)
			JTextArea graphproperties = new JTextArea();
			String propertiesstring = new String();
			graphproperties.setEditable(false);
			graphproperties.setForeground(new Color(255, 55, 55));
			graphproperties.setBackground(Color.WHITE);

			// p.add(new JLabel("Graph properties:"), "gap 5");
			// p.add(graphproperties, "wrap, span 3");

			constCheck = new JCheckBox("constant");
			constCheck.setActionCommand("constCheck");
			constCheck.addActionListener(this);
			constCheck.setSelected(((BiologicalNodeAbstract) ab).isConstant());

			// JTextField aaSequence = new JTextField(20);
			// aaSequence.setText(protein.getAaSequence());
			// aaSequence.setName("protein");
			// aaSequence.addFocusListener(pwl);
			// p.add(new JLabel("AA-Sequence"), "gap 5 ");
			// p.add(aaSequence, "wrap, span 3");

			String atname, atsvalue;
			double atdvalue;

			ArrayList<String> experimententries = new ArrayList<>(), databaseidentries = new ArrayList<>(),
					annotationentries = new ArrayList<>(), graphpropertiesentries = new ArrayList<>();

			for (NodeAttribute att : bna.getNodeAttributes()) {
				atname = att.getName();
				atsvalue = att.getStringvalue();
				atdvalue = att.getDoublevalue();

				switch (att.getType()) {
				case NodeAttributeTypes.EXPERIMENT:
					experimententries.add(atname + ":\t" + atdvalue + "\n");
					break;

				case NodeAttributeTypes.DATABASE_ID:
					databaseidentries.add(atname + ":\t" + atsvalue + "\n");
					break;

				case NodeAttributeTypes.ANNOTATION:
					annotationentries.add(atname + ":\t" + atsvalue + "\n");
					break;

				case NodeAttributeTypes.GRAPH_PROPERTY:
					graphpropertiesentries.add(atname + ":\t" + atdvalue + "\n");
					break;

				default:
					break;
				}
			}

			// Sort for more convenient display
			Collections.sort(experimententries);
			for (String exp : experimententries)
				experimentstring += exp;

			Collections.sort(databaseidentries);
			for (String dbid : databaseidentries)
				dbidstring += dbid;

			Collections.sort(annotationentries);
			for (String ann : annotationentries)
				annotationstring += ann;

			Collections.sort(graphpropertiesentries);
			for (String gprop : graphpropertiesentries)
				propertiesstring += gprop;

			experiments.setText(experimentstring);
			dbids.setText(dbidstring);
			goannoations.setText(annotationstring);
			graphproperties.setText(propertiesstring);

			if (!(ab instanceof PNNode)) {
				MyJFormattedTextField concentration;
				MyJFormattedTextField concentrationStart;

				JLabel lblTokenStart = new JLabel("Concentration Start");
				concentration = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
				concentrationStart = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
				concentrationMin = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
				concentrationMax = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
				concentration.setText(((BiologicalNodeAbstract) ab).getConcentration() + "");
				concentrationStart.setText(((BiologicalNodeAbstract) ab).getConcentrationStart() + "");
				concentrationMin.setText(((BiologicalNodeAbstract) ab).getConcentrationMin() + "");
				concentrationMax.setText(((BiologicalNodeAbstract) ab).getConcentrationMax() + "");

				JLabel lblConcentration = new JLabel("Concentration");

				concentration.setName("concentration");
				// token.addFocusListener(pwl);
				concentration.setEditable(false);
				concentration.setFocusLostBehavior(JFormattedTextField.COMMIT);

				concentrationStart.setName("concentrationStart");
				concentrationStart.setFocusLostBehavior(JFormattedTextField.COMMIT);
				concentrationStart.addFocusListener(pwl);

				concentrationMin.setName("concentrationMin");
				concentrationMin.setFocusLostBehavior(JFormattedTextField.COMMIT);
				concentrationMin.addFocusListener(pwl);
				JLabel lblTokenMin = new JLabel("min Conc.");

				concentrationMax.setName("concentrationMax");
				concentrationMax.setFocusLostBehavior(JFormattedTextField.COMMIT);
				concentrationMax.addFocusListener(pwl);
				JLabel lblTokenMax = new JLabel("max Conc.");
				p.add(lblConcentration, "gap 5 ");
				p.add(concentration, "wrap");

				p.add(lblTokenStart, "gap 5 ");
				p.add(concentrationStart, "");

				p.add(constCheck, "wrap");
				p.add(lblTokenMin, "gap 5 ");
				p.add(concentrationMin, "wrap");
				p.add(lblTokenMax, "gap 5");
				p.add(concentrationMax, "wrap");

				bna = (BiologicalNodeAbstract) ab;
				ButtonGroup group = new ButtonGroup();
				JRadioButton discrete = new JRadioButton("discrete");
				discrete.setActionCommand("nodeDiscrete");
				discrete.addActionListener(this);
				JRadioButton cont = new JRadioButton("continuous");
				cont.setActionCommand("nodeCont");
				cont.addActionListener(this);
				group.add(discrete);
				group.add(cont);

				if (bna.isDiscrete()) {
					discrete.setSelected(true);
				} else {
					cont.setSelected(true);
				}

				p.add(new JLabel("Type"), "flowx, gap 5");
				p.add(discrete, "flowx, split 2, gap 5");
				p.add(cont, "gap 5, wrap");
			}

			if (ab instanceof PathwayMap) {
				p.add(new JLabel("Linked to Pathway"), "gap 5 ");
				boolean b = ((PathwayMap) ab).getPathwayLink() == null;
				JCheckBox linked = new JCheckBox("", !b);
				linked.setToolTipText(
						"Shows whether there is a connected Pathway in Memory to this Map (uncheck the Box to delete that Pathway)");
				linked.setActionCommand("pathwayLink");
				linked.addActionListener(this);
				linked.setEnabled(!b);
				p.add(linked, "wrap ,span 3");
			} else if (ab instanceof Protein) {
				Protein protein = (Protein) ab;
				JTextField aaSequence = new JTextField(20);
				aaSequence.setText(protein.getAaSequence());
				aaSequence.setName("protein");
				aaSequence.addFocusListener(pwl);
				p.add(new JLabel("AA-Sequence"), "gap 5 ");
				p.add(aaSequence, "wrap, span 3");
			}

			else if (ab instanceof DNA) {
				DNA dna = (DNA) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(dna.getNtSequence());
				ntSequence.setName("dna");
				ntSequence.addFocusListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			} else if (ab instanceof Gene) {
				Gene dna = (Gene) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(dna.getNtSequence());
				ntSequence.setName("gene");
				ntSequence.addFocusListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			}

			else if (ab instanceof RNA) {
				RNA rna = (RNA) ab;
				JTextField ntSequence = new JTextField(20);
				ntSequence.setText(rna.getNtSequence());
				ntSequence.setName("rna");
				ntSequence.addFocusListener(pwl);
				p.add(new JLabel("NT-Sequence"), "gap 5 ");
				p.add(ntSequence, "wrap, span 3");
			} else if (ab instanceof Place) {
				Place place = (Place) ab;

				JLabel lswitchPlace = new JLabel("Place Type");
				JComboBox<String> placeList = new JComboBox<String>(new String[] { "discrete", "continuous" });
				if (place.isDiscrete()) {
					placeList.setSelectedItem("discrete");
				} else {
					placeList.setSelectedItem("continuous");
				}
				placeList.setName("placeList");
				placeList.addFocusListener(pwl);
				p.add(lswitchPlace, "gap 5 ");
				p.add(placeList, "wrap");

				MyJFormattedTextField token;
				MyJFormattedTextField tokenStart;

				JLabel lblTokenStart = new JLabel("Token Start");
				if (place.isDiscrete()) {
					token = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
					tokenStart = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
					tokenMin = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
					tokenMax = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
					token.setText((int) place.getToken() + "");
					tokenStart.setText((int) place.getTokenStart() + "");
					tokenMin.setText((int) place.getTokenMin() + "");
					tokenMax.setText((int) place.getTokenMax() + "");
				} else {
					token = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					tokenStart = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					tokenMin = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					tokenMax = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					token.setText(place.getToken() + "");
					tokenStart.setText(place.getTokenStart() + "");
					tokenMin.setText(place.getTokenMin() + "");
					tokenMax.setText(place.getTokenMax() + "");
				}
				JLabel lblToken = new JLabel("Token");

				token.setName("token");
				// token.addFocusListener(pwl);
				token.setEditable(false);
				token.setFocusLostBehavior(JFormattedTextField.COMMIT);

				tokenStart.setName("tokenStart");
				tokenStart.setFocusLostBehavior(JFormattedTextField.COMMIT);
				tokenStart.addFocusListener(pwl);

				tokenMin.setName("tokenMin");
				tokenMin.setFocusLostBehavior(JFormattedTextField.COMMIT);
				tokenMin.addFocusListener(pwl);
				JLabel lblTokenMin = new JLabel("min Tokens");

				tokenMax.setName("tokenMax");
				tokenMax.setFocusLostBehavior(JFormattedTextField.COMMIT);
				tokenMax.addFocusListener(pwl);
				JLabel lblTokenMax = new JLabel("max Tokens");
				p.add(lblToken, "gap 5 ");
				p.add(token, "span 1, wrap");

				p.add(lblTokenStart, "gap 5 ");
				p.add(tokenStart, "span 1");

				p.add(constCheck, "wrap");
				p.add(lblTokenMin, "gap 5 ");
				p.add(tokenMin, "span 1, wrap");
				p.add(lblTokenMax, "gap 5");
				p.add(tokenMax, "span 1, wrap");

				if (place.getConflictingOutEdges().size() > 1) {

					ButtonGroup group = new ButtonGroup();
					JRadioButton none = new JRadioButton("none");
					none.setActionCommand("conflict_none");
					none.addActionListener(this);
					JRadioButton prio = new JRadioButton("prio");
					prio.setActionCommand("conflict_prio");
					prio.addActionListener(this);
					JRadioButton prob = new JRadioButton("prob");
					prob.setActionCommand("conflict_prob");
					prob.addActionListener(this);

					group.add(none);
					group.add(prio);
					group.add(prob);

					JButton solve = new JButton("solve conflict properties");
					solve.setToolTipText("solve priorities and normalize probabilites");
					solve.setActionCommand("solve");
					solve.addActionListener(this);

					JButton check = new JButton("check all");
					check.setToolTipText("check all conflict properties");
					check.setActionCommand("check");
					check.addActionListener(this);

					p.add(new JLabel("conflict solving:"), "gap 5 ");
					p.add(none, "flowx, split 3");
					p.add(prio);
					p.add(prob, "wrap");

					if (place.getConflictStrategy() == Place.CONFLICTHANDLING_NONE) {
						none.setSelected(true);
					} else if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PRIO) {
						prio.setSelected(true);
						p.add(check, "skip,split 2");
						p.add(solve, "wrap");
					} else if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PROB) {
						prob.setSelected(true);
						p.add(check, "skip,split 2");
						p.add(solve, "wrap");
					}
				}

			} else if (ab instanceof Transition) {
				JLabel lswitchTrans = new JLabel("Transition Type");
				JComboBox<String> transList = new JComboBox<String>(new String[] {
						DiscreteTransition.class.getSimpleName(), ContinuousTransition.class.getSimpleName(),
						StochasticTransition.class.getSimpleName() });
				transList.setSelectedItem(ab.getClass().getSimpleName());
				transList.setName("transList");
				transList.addFocusListener(pwl);
				p.add(lswitchTrans, "gap 5");
				p.add(transList, "wrap");

				JTextField firingCondition = new JTextField(4);
				JLabel lblFiringCondition = new JLabel("Firing Condition");
				firingCondition.setText(((Transition) ab).getFiringCondition());
				firingCondition.setName("firingCondition");
				firingCondition.addFocusListener(pwl);

				p.add(lblFiringCondition, "gap 5");
				p.add(firingCondition, "wrap");

				if (ab instanceof DiscreteTransition) {
					DiscreteTransition trans = (DiscreteTransition) ab;
					JTextField delay = new JTextField(4);
					JLabel lbldelay = new JLabel("Delay");
					delay.setText(trans.getDelay() + "");
					delay.setName("delay");
					delay.addFocusListener(pwl);

					p.add(lbldelay, "gap 5");
					p.add(delay, "wrap");
				}

				else if (ab instanceof StochasticTransition) {
					StochasticTransition trans = (StochasticTransition) ab;
					// Create the combo box, select item at index 4.
					// Indices start at 0, so 4 specifies the pig.
					JComboBox<String> distributionList = new JComboBox<String>(
							StochasticDistribution.distributionList.toArray(new String[0]));
					distributionList.setSelectedItem(trans.getDistribution());
					distributionList.setName("distributionList");
					distributionList.addItemListener(pwl);
					// distributionList.addFocusListener(pwl);
					p.add(new JLabel("Distribution"), "gap 5");
					p.add(distributionList, "wrap");

					p.add(new JLabel("Distribution properties"), "gap 5");
					p.add(new JSeparator(), "span, growx, gaptop 7 ");
					MyJFormattedTextField h = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					JLabel lblH = new JLabel("h: probability density");
					// lblH.setToolTipText("probability density");
					h.setText(trans.getH() + "");
					h.addFocusListener(pwl);
					h.setFocusLostBehavior(JFormattedTextField.COMMIT);
					h.setName("h");

					MyJFormattedTextField a = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					JLabel lblA = new JLabel("a: minimum value");
					a.setText(trans.getA() + "");
					a.addFocusListener(pwl);
					a.setFocusLostBehavior(JFormattedTextField.COMMIT);
					a.setName("a");

					MyJFormattedTextField b = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					JLabel lblB = new JLabel("b: maximum value");
					b.setText(trans.getB() + "");
					b.addFocusListener(pwl);
					b.setFocusLostBehavior(JFormattedTextField.COMMIT);
					b.setName("b");

					MyJFormattedTextField c = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					JLabel lblC = new JLabel("c: most likely value");
					c.setText(trans.getC() + "");
					c.addFocusListener(pwl);
					c.setFocusLostBehavior(JFormattedTextField.COMMIT);
					c.setName("c");

					MyJFormattedTextField mu = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					JLabel lblMu = new JLabel("mu: expected value");
					mu.setText(trans.getMu() + "");
					mu.addFocusListener(pwl);
					mu.setFocusLostBehavior(JFormattedTextField.COMMIT);
					mu.setName("mu");

					MyJFormattedTextField sigma = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					JLabel lblSigma = new JLabel("sigma: standard deviation");
					sigma.setText(trans.getSigma() + "");
					sigma.addFocusListener(pwl);
					sigma.setFocusLostBehavior(JFormattedTextField.COMMIT);
					sigma.setName("sigma");

					JTextField events = new JTextField(4);
					JLabel lblEvents = new JLabel("discrete events");
					events.setText(trans.getEvents() + "");
					events.setToolTipText("List of discrete events [1,2,...,n]");
					events.addFocusListener(pwl);
					events.setName("events");

					JTextField probabilities = new JTextField(4);
					JLabel lblProbabilities = new JLabel("probabilities");
					probabilities.setText(trans.getProbabilities() + "");
					probabilities.setToolTipText("List of probabilities [1/n,1/n,...,1/n]");
					probabilities.addFocusListener(pwl);
					probabilities.setName("probabilities");

					// System.out.println("distr: "+trans.getDistribution());
					switch (trans.getDistribution()) {
					case StochasticDistribution.distributionExponential:
						p.add(lblH, "gap 5");
						p.add(h, "wrap");
						break;
					case StochasticDistribution.distributionTriangular:
						p.add(lblA, "gap 5");
						p.add(a, "wrap");
						p.add(lblB, "gap 5");
						p.add(b, "wrap");
						p.add(lblC, "gap 5");
						p.add(c, "wrap");
						break;
					case StochasticDistribution.distributionTruncatedNormal:
						p.add(lblA, "gap 5");
						p.add(a, "wrap");
						p.add(lblB, "gap 5");
						p.add(b, "wrap");
						p.add(lblMu, "gap 5");
						p.add(mu, "wrap");
						p.add(lblSigma, "gap 5");
						p.add(sigma, "wrap");
						break;
					case StochasticDistribution.distributionUniform:
						p.add(lblA, "gap 5");
						p.add(a, "wrap");
						p.add(lblB, "gap 5");
						p.add(b, "wrap");
						break;
					case StochasticDistribution.distributionDiscreteProbability:
						p.add(lblEvents, "gap 5");
						p.add(events, "wrap");
						p.add(lblProbabilities, "gap 5");
						p.add(probabilities, "wrap");
						break;
					}
				}

				else if (ab instanceof DynamicNode) {

					DynamicNode trans = (DynamicNode) ab;
					JTextField maxSpeed = new JTextField(4);
					JLabel lblMaxSpeed = new JLabel("Maximal Speed");
					maxSpeed.setText(trans.getMaximalSpeed());
					maxSpeed.setName("maximalSpeed");
					maxSpeed.addFocusListener(pwl);

					p.add(lblMaxSpeed, "gap 5");
					p.add(maxSpeed, "wrap");

					if (trans.isKnockedOut()) {
						maxSpeed.setEnabled(false);
					}
				}
			}
			if (constCheck.isSelected()) {
				if (tokenMin != null && tokenMax != null) {
					tokenMin.setEnabled(false);
					tokenMax.setEnabled(false);
				}
				if (concentrationMin != null && concentrationMax != null) {
					concentrationMin.setEnabled(false);
					concentrationMax.setEnabled(false);
				}
			}

		} else if (ab.isEdge()) {
			// System.out.println("edge");
			if (ab instanceof PNEdge) {

				PNEdge e = (PNEdge) ab;

				JTextField function = new JTextField(5);
				function.setText(e.getFunction());
				function.setName("function");
				function.addFocusListener(pwl);
				JLabel lblpassingTokens = new JLabel("Edge Function");

				// String[] types = { "discrete", "continuous", "inhibition" };
				// Create the combo box, select item at index 4.
				// Indices start at 0, so 4 specifies the pig.
				// JLabel typeList = new JComboBox(types);

				JButton changeEdgeDirection = new JButton("Change Direction");
				changeEdgeDirection.setActionCommand("changeEdgeDirection");
				changeEdgeDirection.addActionListener(this);
				p.add(changeEdgeDirection, "wrap");

				MyJFormattedTextField activationProb = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
				activationProb.setText(e.getProbability() + "");
				activationProb.setName("activationProb");
				activationProb.addFocusListener(pwl);
				JLabel lblProb = new JLabel("activation Probability");

				MyJFormattedTextField activationPrio = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
				activationPrio.setText(e.getPriority() + "");
				activationPrio.setName("activationPrio");
				activationPrio.addFocusListener(pwl);
				JLabel lblPrio = new JLabel("activation Priority");

				p.add(lblpassingTokens, "gap 5");
				p.add(function, "wrap");

				if (e.getFrom() instanceof Place) {
					Place place = (Place) e.getFrom();
					if (place.getConflictingOutEdges().size() > 0) {
						p.add(new JLabel("Conflict solving strategy:"), "gap 5");
						if (place.getConflictStrategy() == Place.CONFLICTHANDLING_NONE) {
							p.add(new JLabel("none"), "gap 5, wrap");
						} else if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PRIO) {
							p.add(new JLabel("priorities"), "gap 5, wrap");
							p.add(lblPrio, "gap 5");
							p.add(activationPrio, "wrap");
						} else if (place.getConflictStrategy() == Place.CONFLICTHANDLING_PROB) {
							p.add(new JLabel("probabilites"), "gap 5, wrap");
							p.add(lblProb, "gap 5");
							p.add(activationProb, "wrap");
						}
					}
				}

			} else {

				if (ab instanceof Inhibition) {
					Inhibition inhib = (Inhibition) ab;
					ButtonGroup group = new ButtonGroup();
					JRadioButton absolute = new JRadioButton("absolute");
					absolute.setActionCommand("inhibition_absolute");
					absolute.addActionListener(this);
					JRadioButton relative = new JRadioButton("relative");
					relative.setActionCommand("inhibition_relative");
					relative.addActionListener(this);
					group.add(absolute);
					group.add(relative);

					if (inhib.isAbsoluteInhibition()) {
						absolute.setSelected(true);
					} else {
						relative.setSelected(true);
					}

					p.add(new JLabel("Inhib. behaviour"), "flowx, gap 5");
					p.add(absolute, "flowx, split 2, gap 5");
					p.add(relative, "gap 5, wrap");
				}

				isDirected.setSelected(((BiologicalEdgeAbstract) ab).isDirected());
				isDirected.setToolTipText("is directed");
				isDirected.setActionCommand("isDirected");
				isDirected.addActionListener(this);
				p.add(new JLabel("is directed"), "gap 5 ");
				p.add(isDirected, "wrap ,span 1");
			}

		}

		if (ab instanceof DynamicNode) {
			knockedOut.setSelected(((DynamicNode) ab).isKnockedOut());
			knockedOut.setToolTipText("Knock out");
			knockedOut.setActionCommand("knockedOut");
			knockedOut.addActionListener(this);
			p.add(new JLabel("Knocked out"), "gap 5 ");
			p.add(knockedOut, "wrap ,span 1");
		}

		if (ab.isVertex()) {
			hideNeighbours.setToolTipText("Sets all Neighbors of the selected Node to Reference");
			hideNeighbours.setActionCommand("hideNeighbours");
			hideNeighbours.addActionListener(this);
			hideNeighbours.setMaximumSize(new Dimension(120, 30));
			showNeighbours.setToolTipText("Delete Reference flag of all Neighbours of the current Node");
			showNeighbours.setActionCommand("showNeighbours");
			showNeighbours.addActionListener(this);
			showNeighbours.setMaximumSize(new Dimension(120, 30));
			p.add(showNeighbours, "flowx");
			p.add(hideNeighbours, "flowx, split 2");
		}
		parametersButton.setToolTipText("Show all Parameters");
		parametersButton.setActionCommand("showParameters");
		parametersButton.addActionListener(this);
		p.add(parametersButton, "wrap");

		if (ab instanceof BiologicalNodeAbstract) {
			p.add(plotColorButton, "gap 5");
		}
		p.add(fillColorButton, "gap 5, wrap");

	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void revalidateView() {
		// System.out.println("revalidate");
		graphInstance = new GraphInstance();

		if (graphInstance.getSelectedObject() instanceof BiologicalNodeAbstract
				&& ((BiologicalNodeAbstract) graphInstance.getSelectedObject()).hasRef()) {
			this.ref = ((BiologicalNodeAbstract) graphInstance.getSelectedObject()).getRef();
		} else {
			this.ref = null;
		}
		// dirty hack that pane is always empty
		this.removeAllElements();

		if (emptyPane) {
			// System.out.println("empty");
			updateWindow(graphInstance.getSelectedObject());
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane = false;
			// System.out.println("done");
		} else {
			// System.out.println("else");
			// System.out.println("begin");
			Thread worker = new Thread() {
				public void run() {
					// dirty hack
					try {
						p.removeAll();
						// removeAllElements();
						// System.out.println("begin");
					} catch (Exception e) {
						e.printStackTrace();
						revalidateView();
					}
					// System.out.println("swing");
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							updateWindow(graphInstance.getSelectedObject());
							p.setVisible(true);
							p.repaint();
							p.revalidate();
						}
					});
				}
			};
			worker.start();
		}
	}

	private void addCompartmentItems(JComboBox<String> compartment) {
		Pathway pw = graphInstance.getPathway();
		List<Compartment> compartmentList = pw.getCompartmentManager().getAllCompartmentsAlphabetically();

		compartment.addItem(" ");

		Iterator<Compartment> it = compartmentList.iterator();
		while (it.hasNext()) {
			String element = it.next().getName();
			compartment.addItem(element);
		}
	}

	public void removeAllElements() {
		emptyPane = true;
		p.removeAll();
		p.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MainWindow w = MainWindow.getInstance();
		String event = e.getActionCommand();

		if ("colour".equals(event)) {

			// JColorChooser cc = new JColorChooser(ab.getColor());
			// JDialog dialog = JColorChooser.createDialog(w.getFrame(), "Choose fill
			// color", false, cc, null, null);
			// dialog.setVisible(true);
			// Color newColor = cc.getColor();
			// Color newColor = JColorChooser.showDialog(w.getFrame(), "Choose fill color",
			// ab.getColor());
			// JColorChooser cc = new JColorChooser(ab.getColor());
			MyColorChooser mc = new MyColorChooser(w.getFrame(), "Choose fill color", true, ab.getColor());
			if (mc.isOkAction()) {

				Color newColor = mc.getColor();

				JButton b = ((JButton) e.getSource());
				b.setBackground(newColor);

				ab.setColor(newColor);
				ab.setVisible(true);
				// reference.setSelected(false);
				// updateReferences(false);
			}

		} else if ("plotColour".equals(event)) {
			if (ab instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
				// Color newColor = JColorChooser.showDialog(w.getFrame(), "Choose plot colour",
				// bna.getPlotColor());
				MyColorChooser mc = new MyColorChooser(w.getFrame(), "Choose plot color", true, bna.getPlotColor());
				if (mc.isOkAction()) {
					Color newColor = mc.getColor();
					JButton b = ((JButton) e.getSource());
					b.setBackground(newColor);
					bna.setPlotColor(newColor);
				}
			}
		} else if ("pathwayLink".equals(event)) {
			if (JOptionPane.showConfirmDialog(w.getFrame(),
					"If you delete the PathwayLink the Sub-Pathway (with all eventually made changes within it) will be lost. Do you want to do this?",
					"Delete the Sub-Pathway...", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION && ab instanceof PathwayMap) {
				((PathwayMap) ab).setPathwayLink(null);
				w.updateElementTree();
				w.updatePathwayTree();
				ab.setColor(Color.white);
			}
			w.updateElementProperties();
		} else if (("hideNeighbours".equals(event) || ("showNeighbours".equals(event)))
				&& ab instanceof BiologicalNodeAbstract) {
			// TODO visible wird noch nicht gehandelt in transformators
			Pathway pw = graphInstance.getPathway();
			boolean hide = "hideNeighbours".equals(event);
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;

			Iterator<BiologicalEdgeAbstract> it = pw.getGraph().getJungGraph().getIncidentEdges(bna).iterator();
			BiologicalEdgeAbstract bea;

			while (it.hasNext()) {
				// System.out.println(!hide);
				bea = it.next();
				bea.setVisible(!hide);
				// bea.setLabel(!hide+"");
			}

			Iterator<BiologicalNodeAbstract> it2 = pw.getGraph().getJungGraph().getNeighbors(bna).iterator();

			BiologicalNodeAbstract node;
			while (it2.hasNext()) {
				// System.out.println("drin");
				node = it2.next();
				node.setVisible(!hide);
			}

		} else if ("changeEdgeDirection".equals(event) && ab.isEdge()) {
			Pathway pw = graphInstance.getPathway();
			PNEdge edge = (PNEdge) ab;

			PNEdge newEdge = new PNEdge(edge.getTo(), edge.getFrom(), edge.getLabel(), edge.getName(),
					edge.getBiologicalElement(), edge.getFunction());
			newEdge.setPriority(edge.getPriority());
			newEdge.setProbability(edge.getProbability());
			newEdge.setDirected(true);

			pw.removeElement(edge);
			pw.addEdge(newEdge);
			pw.updateMyGraph();
			pw.getGraph().getVisualizationViewer().getPickedEdgeState().clear();
			pw.getGraph().getVisualizationViewer().getPickedEdgeState().pick(newEdge, true);
			graphInstance.setSelectedObject(newEdge);

			ab = newEdge;
		} else if ("chooseRef".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			ReferenceDialog dialog = new ReferenceDialog(bna);
			BiologicalNodeAbstract node = dialog.getAnswer();
			if (node != null) {
				bna.setRef(node);
				this.revalidateView();
				w.updateElementTree();
				// System.out.println("node: "+node.getID());
			}
		} else if ("deleteRef".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			bna.deleteRef();

			this.revalidateView();
			w.updateElementTree();

		} else if ("pickOrigin".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;

			Pathway pw = graphInstance.getPathway();
			pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			g.getVisualizationViewer().getPickedVertexState().pick(bna.getRef(), true);

			this.revalidateView();

		} else if ("pickRefs".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;

			Pathway pw = graphInstance.getPathway();
			pw = graphInstance.getPathway();
			MyGraph g = pw.getGraph();
			// System.out.println("c: "+g.getJungGraph().getVertexCount());
			BiologicalNodeAbstract pick;
			Iterator<BiologicalNodeAbstract> it = bna.getRefs().iterator();
			// System.out.println("size: "+bna.getRefs().size());
			while (it.hasNext()) {
				pick = it.next();
				// System.out.println(pick.getLabel());
				// System.out.println(pick);
				g.getVisualizationViewer().getPickedVertexState().pick(pick, true);

			}
			// System.out.println(g.getVisualizationViewer().getPickedVertexState().getPicked().size());
			this.revalidateView();
		}

		else if ("showParameters".equals(event)) {
			// System.out.println("show parameters");
			new ParameterWindow(ab);
			this.updateWindow(ab);
			p.revalidate();
		} else if ("showLabels".equals(event)) {
			// System.out.println("click");
			new LabelsWindow(ab);
		} else if ("knockedOut".equals(event)) {

			((DynamicNode) ab).setKnockedOut(knockedOut.isSelected());
			this.updateWindow(ab);
			p.revalidate();
			// p.repaint();
			Pathway pw = new GraphInstance().getPathway();
			pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);

			// System.out.println("knocked out");
		} else if ("isDirected".equals(event)) {
			if (ab instanceof BiologicalEdgeAbstract) {
				((BiologicalEdgeAbstract) ab).setDirected(isDirected.isSelected());
				Pathway pw = new GraphInstance().getPathway();
				pw.updateMyGraph();
			}
		} else if ("constCheck".equals(event)) {
			if (ab instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
				if (constCheck.isSelected()) {
					if (tokenMin != null && tokenMax != null) {
						this.tokenMin.setEnabled(false);
						this.tokenMax.setEnabled(false);
					}
					if (concentrationMin != null && concentrationMax != null) {
						this.concentrationMin.setEnabled(false);
						this.concentrationMax.setEnabled(false);
					}
					bna.setConstant(true);
				} else {
					if (tokenMin != null && tokenMax != null) {
						this.tokenMin.setEnabled(true);
						this.tokenMax.setEnabled(true);
					}
					if (concentrationMin != null && concentrationMax != null) {
						this.concentrationMin.setEnabled(true);
						this.concentrationMax.setEnabled(true);
					}
					bna.setConstant(false);
				}
				Pathway pw = new GraphInstance().getPathway();
				pw.handleChangeFlags(ChangedFlags.EDGEWEIGHT_CHANGED);
			}
			// System.out.println(this.constCheck.isSelected());
		} else if ("conflict_none".equals(event)) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				if (place.getConflictStrategy() != Place.CONFLICTHANDLING_NONE) {
					place.setConflictStrategy(Place.CONFLICTHANDLING_NONE);
					this.revalidateView();
				}
			}
		} else if ("conflict_prio".equals(event)) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				if (place.getConflictStrategy() != Place.CONFLICTHANDLING_PRIO) {
					place.setConflictStrategy(Place.CONFLICTHANDLING_PRIO);
					this.revalidateView();
				}
			}
		} else if ("conflict_prob".equals(event)) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				if (place.getConflictStrategy() != Place.CONFLICTHANDLING_PROB) {
					place.setConflictStrategy(Place.CONFLICTHANDLING_PROB);
					this.revalidateView();
					// p.repaint();
					// System.out.println("durch");
				}
			}
		} else if ("solve".equals(event)) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				place.solveConflictProperties();
			}
		} else if ("check".equals(event)) {
			Iterator<BiologicalNodeAbstract> it = GraphInstance.getMyGraph().getAllVertices().iterator();
			String result = "";
			while (it.hasNext()) {
				BiologicalNodeAbstract bna = it.next();

				if (bna instanceof Place) {
					Place place = (Place) bna;
					if (place.hasConflictProperties()) {
						result += place.getName() + "\n";
					}
					// place.solveConflictProperties();
				}
			}
			String message = "No conflicts found!";
			if (result.length() > 0) {
				message = "Following conflicting places found: " + result;
			}
			MyPopUp.getInstance().show("Checking conflicts", message);
		} else if ("inhibition_absolute".equals(event)) {
			if (ab instanceof Inhibition) {
				((Inhibition) ab).setAbsoluteInhibition(true);
			}
		} else if ("inhibition_relative".equals(event)) {
			if (ab instanceof Inhibition) {
				((Inhibition) ab).setAbsoluteInhibition(false);
			}
		} else if ("nodeDiscrete".equals(event)) {
			if (!(ab instanceof PNNode) && ab instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
				bna.setDiscrete(true);
			}
		} else if ("nodeCont".equals(event)) {
			if (!(ab instanceof PNNode) && ab instanceof BiologicalNodeAbstract) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
				bna.setDiscrete(false);
			}
		}
		GraphInstance.getMyGraph().updateGraph();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		// String item = (String) event.getItem();
		if (ab.isVertex()) {

			JComboBox<String> compartment = (JComboBox<String>) event.getSource();
			// System.out.println("new: "+compartment.getSelectedItem());
			Pathway pw = graphInstance.getPathway();
			// System.out.println(pw.getCompartmentManager().getCompartment(compartment.getSelectedItem().toString()));
			pw.getCompartmentManager().setCompartment((BiologicalNodeAbstract) ab,
					pw.getCompartmentManager().getCompartment(compartment.getSelectedItem().toString()));
		}
	}
}
