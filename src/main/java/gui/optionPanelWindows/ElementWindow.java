package gui.optionPanelWindows;

import biologicalElements.GraphElementAbstract;
import biologicalElements.GraphicalElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.*;
import biologicalObjects.nodes.petriNet.*;
import configurations.Workspace;
import graph.ChangedFlags;
import graph.GraphInstance;
import graph.compartment.Compartment;
import graph.gui.LabelsWindow;
import graph.gui.ParameterWindow;
import graph.gui.ReferenceDialog;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.PopUpDialog;
import gui.eventhandlers.PropertyWindowListener;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import simulation.ConflictHandling;
import util.MyColorChooser;
import util.MyJFormattedTextField;
import util.MyNumberFormat;
import util.StochasticDistribution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class ElementWindow implements ActionListener, ItemListener {
	private final JPanel p = new JPanel();
	private GraphElementAbstract ab;
	boolean emptyPane = true;

	private JCheckBox constCheck;

	private BiologicalNodeAbstract ref = null;
	private GraphElementAbstract original;

	private JCheckBox knockedOut;
	private JLabel token;
	private MyJFormattedTextField tokenMin;
	private MyJFormattedTextField tokenMax;
	private MyJFormattedTextField concentrationMin;
	private MyJFormattedTextField concentrationMax;

	private JLabel concentration;

	private JCheckBox isDirected;

	private void updateWindow(final GraphicalElementAbstract element) {
		p.removeAll();
		final Pathway pw = GraphInstance.getPathway();
		if (pw == null) {
			return;
		}
		if (element instanceof GraphElementAbstract) {
			original = (GraphElementAbstract) element;
			PropertyWindowListener pwl = new PropertyWindowListener(original);

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

			JButton hideNeighbours = new JButton("Hide neighbors");
			JButton showNeighbours = new JButton("Show neighbors");
			JButton parametersButton = new JButton("Parameters");
			JButton showLabels = new JButton("Show labels");

			JButton fillColorButton = new JButton("Fill color");
			fillColorButton.setBackground(ab.getColor());
			fillColorButton.setToolTipText("Select fill color");
			fillColorButton.setActionCommand("colour");
			fillColorButton.addActionListener(this);

			JButton plotColorButton = new JButton("Plot color");
			if (ab instanceof BiologicalNodeAbstract) {
				plotColorButton.setBackground(((BiologicalNodeAbstract) ab).getPlotColor());
			}
			plotColorButton.setToolTipText("Select plot color");
			plotColorButton.setActionCommand("plotColour");
			plotColorButton.addActionListener(this);
			label.setName("label");
			name.setName("name");

			label.addFocusListener(pwl);
			name.addFocusListener(pwl);

			// MigLayout headerlayout = new MigLayout("fillx", "[right]rel[grow,fill]", "");
			// JPanel headerPanel = new JPanel(headerlayout);
			// headerPanel.setBackground(new Color(192, 215, 227));
			// headerPanel.add(new JLabel(ab.getBiologicalElement()), "");
			// headerPanel.add(new JSeparator(), "gap 10");

			MigLayout layout = new MigLayout("fillx", "[grow,fill]", "");
			p.setLayout(layout);
			p.add(new JLabel("Element"), "gap 5 ");
			if (ref != null) {
				p.add(new JLabel(ab.getBiologicalElement() + " (logical node)"), "wrap,span 1");
			} else {
				p.add(new JLabel(ab.getBiologicalElement()), "wrap,span 1");
			}
			if (Workspace.getCurrentSettings().isDeveloperMode()) {
				p.add(new JLabel("ID"), "gap 5 ");
				JLabel id = new JLabel(String.valueOf(ab.getID()));
				if (ref != null) {
					id.setText(String.valueOf(original.getID()));
				}
				p.add(id, "wrap ,span 1");
			}

			if (!(ab instanceof PNArc)) {
				p.add(new JLabel("Label"), "gap 5 ");
				p.add(label, "span 1, wrap");
				p.add(new JLabel("Name"), "gap 5 ");
				p.add(name, "span 1, wrap");
			}
			// JCheckBox transitionfire = new JCheckBox("Should transition fire:", true);
			// JTextField transitionStatement = new JTextField("true");
			if (ab.isVertex()) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
				// final List<String> experimentEntries = new ArrayList<>();
				// final List<String> databaseIdEntries = new ArrayList<>();
				// final List<String> annotationEntries = new ArrayList<>();
				// final List<String> graphPropertyEntries = new ArrayList<>();
				// for (NodeAttribute att : bna.getNodeAttributes()) {
				// switch (att.getType()) {
				// case NodeAttributeTypes.EXPERIMENT:
				// experimentEntries.add(att.getName() + ":\t" + att.getDoublevalue() + "\n");
				// break;
				// case NodeAttributeTypes.DATABASE_ID:
				// databaseIdEntries.add(att.getName() + ":\t" + att.getStringvalue() + "\n");
				// break;
				// case NodeAttributeTypes.ANNOTATION:
				// annotationEntries.add(att.getName() + ":\t" + att.getStringvalue() + "\n");
				// break;
				// case NodeAttributeTypes.GRAPH_PROPERTY:
				// graphPropertyEntries.add(att.getName() + ":\t" + att.getDoublevalue() +
				// "\n");
				// break;
				// }
				// }
				//
				// // Sort for more convenient display
				// Collections.sort(experimentEntries);
				// Collections.sort(databaseIdEntries);
				// Collections.sort(annotationEntries);
				// Collections.sort(graphPropertyEntries);

				String lbl = "-";
				if (bna.isLogical()) {
					lbl = "ID: " + ref.getID() + ", label: " + ref.getLabel();
				}
				p.add(new JLabel("Reference to:"), "gap 5 ");
				p.add(new JLabel(lbl), "wrap ,span 3");

				if (bna.isLogical()) {
					JButton deleteRef = new JButton("Delete reference");
					deleteRef.setToolTipText("Delete reference (this node will not be a logical node anymore)");
					deleteRef.setActionCommand("deleteRef");
					deleteRef.addActionListener(this);
					p.add(deleteRef, "gap 5");
					JButton pickOrigin = new JButton("Highlight origin");
					pickOrigin.setToolTipText("Highlight node which this node refers to");
					pickOrigin.setActionCommand("pickOrigin");
					pickOrigin.addActionListener(this);
					p.add(pickOrigin, "split 2, gap 5");

				} else {
					JButton chooseRef = new JButton("Choose reference");
					chooseRef.setToolTipText("Choose reference node (makes this node a logical node)");
					chooseRef.setActionCommand("chooseRef");
					chooseRef.addActionListener(this);
					p.add(chooseRef);
					if (bna.getRefs().size() > 0) {
						JButton pickRefs = new JButton("Highlight references");
						pickRefs.setToolTipText("Highlights all logical nodes which refer to this node");
						pickRefs.setActionCommand("pickRefs");
						pickRefs.addActionListener(this);
						p.add(pickRefs);
					}
				}

				showLabels.setToolTipText("Show all labels");
				showLabels.setActionCommand("showLabels");
				showLabels.addActionListener(this);
				p.add(showLabels, "span 1, wrap");

				JComboBox<String> compartment = new JComboBox<>();
				addCompartmentItems(compartment);
				AutoCompleteDecorator.decorate(compartment);

				compartment.setSelectedItem(pw.getCompartmentManager().getCompartment(((BiologicalNodeAbstract) ab)));
				compartment.addItemListener(this);

				p.add(new JLabel("Compartment"), "gap 5 ");
				p.add(compartment, "wrap ,span 1");

				// ADDing Attributes (for all nodes)

				// Show Database IDs
				// JTextArea dbids = new JTextArea();
				// dbids.setEditable(false);
				// dbids.setFont(dbids.getFont().deriveFont(Font.BOLD));
				// dbids.setBackground(Color.WHITE);
				// dbids.setText(String.join("", databaseIdEntries));
				// p.add(new JLabel("IDs known:"), "gap 5");
				// p.add(dbids, "wrap, span 3");

				// Show Experiment names and values
				// JTextArea experiments = new JTextArea();
				// experiments.setEditable(false);
				// experiments.setBackground(Color.WHITE);
				// experiments.setText(String.join("", experimentEntries));
				// p.add(new JLabel("Dataset:"), "gap 5");
				// p.add(experiments, "wrap, span 3");

				// Show GO annotations
				// JTextArea goannoations = new JTextArea();
				// goannoations.setEditable(false);
				// goannoations.setForeground(Color.BLUE);
				// goannoations.setBackground(Color.WHITE);
				// goannoations.setText(String.join("", annotationEntries));
				// p.add(new JLabel("Gene Ontology:"), "gap 5");
				// p.add(goannoations, "wrap, span 3");

				// Show graph properties (local property)
				// JTextArea graphproperties = new JTextArea();
				// graphproperties.setEditable(false);
				// graphproperties.setForeground(new Color(255, 55, 55));
				// graphproperties.setBackground(Color.WHITE);
				// graphproperties.setText(String.join("", graphPropertyEntries));
				// p.add(new JLabel("Graph properties:"), "gap 5");
				// p.add(graphproperties, "wrap, span 3");

				constCheck = new JCheckBox("");
				constCheck.setActionCommand("constCheck");
				constCheck.addActionListener(this);
				constCheck.setSelected(((BiologicalNodeAbstract) ab).isConstant());

				if (!(ab instanceof PNNode)) {
					concentration = new JLabel(String.valueOf(((BiologicalNodeAbstract) ab).getConcentration()));
					MyJFormattedTextField concentrationStart = new MyJFormattedTextField(
							MyNumberFormat.getDecimalFormat());
					concentrationMin = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					concentrationMax = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					concentrationStart.setText(String.valueOf(((BiologicalNodeAbstract) ab).getConcentrationStart()));
					concentrationMin.setText(String.valueOf(((BiologicalNodeAbstract) ab).getConcentrationMin()));
					concentrationMax.setText(String.valueOf(((BiologicalNodeAbstract) ab).getConcentrationMax()));

					concentrationStart.setName("concentrationStart");
					concentrationStart.setFocusLostBehavior(JFormattedTextField.COMMIT);
					concentrationStart.addFocusListener(pwl);

					concentrationMin.setName("concentrationMin");
					concentrationMin.setFocusLostBehavior(JFormattedTextField.COMMIT);
					concentrationMin.addFocusListener(pwl);

					concentrationMax.setName("concentrationMax");
					concentrationMax.setFocusLostBehavior(JFormattedTextField.COMMIT);
					concentrationMax.addFocusListener(pwl);
					p.add(new JLabel("Concentration"), "gap 5");
					p.add(concentration, "wrap");

					p.add(new JLabel("Start concentration"), "gap 5 ");
					p.add(concentrationStart, "wrap");

					p.add(new JLabel("Min concentration"), "gap 5 ");
					p.add(concentrationMin, "wrap");
					p.add(new JLabel("Max concentration"), "gap 5");
					p.add(concentrationMax, "wrap");
					p.add(new JLabel("Constant concentration"), "gap 5");
					p.add(constCheck, "wrap");

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
				} else if (ab instanceof DNA) {
					DNA dna = (DNA) ab;
					JTextField ntSequence = new JTextField(20);
					ntSequence.setText(dna.getNtSequence());
					ntSequence.setName("ntSequence");
					ntSequence.addFocusListener(pwl);
					p.add(new JLabel("NT-Sequence"), "gap 5 ");
					p.add(ntSequence, "wrap, span 3");
					MyJFormattedTextField logFc = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat(), true);
					logFc.setName("logFC");
					logFc.setText(String.valueOf(dna.getLogFC()));
					logFc.addFocusListener(pwl);
					p.add(new JLabel("logFC"), "gap 5 ");
					p.add(logFc, "wrap, span 3");
				} else if (ab instanceof Gene) {
					Gene dna = (Gene) ab;
					JTextField ntSequence = new JTextField(20);
					ntSequence.setText(dna.getNtSequence());
					ntSequence.setName("ntSequence");
					ntSequence.addFocusListener(pwl);
					p.add(new JLabel("NT-Sequence"), "gap 5 ");
					p.add(ntSequence, "wrap, span 3");
				} else if (ab instanceof RNA) {
					RNA rna = (RNA) ab;
					JTextField ntSequence = new JTextField(20);
					ntSequence.setText(rna.getNtSequence());
					ntSequence.setName("ntSequence");
					ntSequence.addFocusListener(pwl);
					p.add(new JLabel("NT-Sequence"), "gap 5 ");
					p.add(ntSequence, "wrap, span 3");
					MyJFormattedTextField logFc = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat(), true);
					logFc.setName("logFC");
					logFc.setText(String.valueOf(rna.getLogFC()));
					logFc.addFocusListener(pwl);
					p.add(new JLabel("logFC"), "gap 5 ");
					p.add(logFc, "wrap, span 3");
				} else if (ab instanceof Place) {
					Place place = (Place) ab;
					JLabel lswitchPlace = new JLabel("Place type");
					JComboBox<String> placeList = new JComboBox<>(new String[] { "discrete", "continuous" });
					if (place.isDiscrete()) {
						placeList.setSelectedItem("discrete");
					} else {
						placeList.setSelectedItem("continuous");
					}
					placeList.setName("placeList");
					placeList.addFocusListener(pwl);
					p.add(lswitchPlace, "gap 5 ");
					p.add(placeList, "wrap");

					MyJFormattedTextField tokenStart;

					if (place.isDiscrete()) {
						token = new JLabel();
						tokenStart = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
						tokenMin = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
						tokenMax = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
						token.setText(String.valueOf((int) place.getToken()));
						tokenStart.setText(String.valueOf((int) place.getTokenStart()));
						tokenMin.setText(String.valueOf((int) place.getTokenMin()));
						tokenMax.setText(String.valueOf((int) place.getTokenMax()));
					} else {
						token = new JLabel();
						tokenStart = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						tokenMin = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						tokenMax = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						token.setText(String.valueOf(place.getToken()));
						tokenStart.setText(String.valueOf(place.getTokenStart()));
						tokenMin.setText(String.valueOf(place.getTokenMin()));
						tokenMax.setText(String.valueOf(place.getTokenMax()));
					}

					tokenStart.setName("tokenStart");
					tokenStart.setFocusLostBehavior(JFormattedTextField.COMMIT);
					tokenStart.addFocusListener(pwl);

					tokenMin.setName("tokenMin");
					tokenMin.setFocusLostBehavior(JFormattedTextField.COMMIT);
					tokenMin.addFocusListener(pwl);

					tokenMax.setName("tokenMax");
					tokenMax.setFocusLostBehavior(JFormattedTextField.COMMIT);
					tokenMax.addFocusListener(pwl);
					p.add(new JLabel("Tokens"), "gap 5 ");
					p.add(token, "span 1, wrap");

					p.add(new JLabel("Start tokens"), "gap 5 ");
					p.add(tokenStart, "wrap");

					// p.add(constCheck, "wrap");
					p.add(new JLabel("Min tokens"), "gap 5 ");
					p.add(tokenMin, "span 1, wrap");
					p.add(new JLabel("Max tokens"), "gap 5");
					p.add(tokenMax, "span 1, wrap");
					p.add(new JLabel("Constant tokens"), "gap 5");
					p.add(constCheck, "wrap");

					if (place.getConflictingInEdges().size() > 1 || place.getConflictingOutEdges().size() > 1) {
						ButtonGroup group = new ButtonGroup();
						JRadioButton prio = new JRadioButton("prio");
						prio.setActionCommand("conflict_prio");
						prio.addActionListener(this);
						JRadioButton prob = new JRadioButton("prob");
						prob.setActionCommand("conflict_prob");
						prob.addActionListener(this);

						group.add(prio);
						group.add(prob);

						JButton solve = new JButton("Solve conflict properties");
						solve.setToolTipText("Solve priorities and normalize probabilities");
						solve.setActionCommand("solve");
						solve.addActionListener(this);

						JButton check = new JButton("Check all");
						check.setToolTipText("Check all conflict properties");
						check.setActionCommand("check");
						check.addActionListener(this);

						if (place.getConflictStrategy() == ConflictHandling.Priority) {
							prio.setSelected(true);
						} else if (place.getConflictStrategy() == ConflictHandling.Probability) {
							prob.setSelected(true);
						}

						p.add(new JLabel("Conflict solving:"), "gap 5");
						p.add(prio, "flowx, split 2");
						p.add(prob, "wrap");
						p.add(check, "skip, split 2");
						p.add(solve, "wrap");
					}

				} else if (ab instanceof Transition) {
					JComboBox<String> transList = new JComboBox<>(
							new String[] { DiscreteTransition.class.getSimpleName(),
									ContinuousTransition.class.getSimpleName(),
									StochasticTransition.class.getSimpleName() });
					transList.setSelectedItem(ab.getClass().getSimpleName());
					transList.setName("transList");
					transList.addFocusListener(pwl);
					p.add(new JLabel("Transition type"), "gap 5");
					p.add(transList, "wrap");

					JTextField firingCondition = new JTextField(4);
					firingCondition.setText(((Transition) ab).getFiringCondition());
					firingCondition.setName("firingCondition");
					firingCondition.addFocusListener(pwl);

					p.add(new JLabel("Firing condition"), "gap 5");
					p.add(firingCondition, "wrap");

					if (ab instanceof DiscreteTransition) {
						DiscreteTransition trans = (DiscreteTransition) ab;
						JTextField delay = new JTextField(4);
						delay.setText(String.valueOf(trans.getDelay()));
						delay.setName("delay");
						delay.addFocusListener(pwl);
						if (trans.isKnockedOut()) {
							delay.setEnabled(false);
						}
						p.add(new JLabel("Delay"), "gap 5");
						p.add(delay, "wrap");
					} else if (ab instanceof ContinuousTransition) {
						ContinuousTransition node = (ContinuousTransition) ab;
						JTextField maxSpeed = new JTextField(4);
						maxSpeed.setText(node.getMaximalSpeed());
						maxSpeed.setName("maximalSpeed");
						maxSpeed.addFocusListener(pwl);
						if (node.isKnockedOut()) {
							maxSpeed.setEnabled(false);
						}
						p.add(new JLabel("Maximal speed"), "gap 5");
						p.add(maxSpeed, "wrap");

					} else if (ab instanceof StochasticTransition) {
						StochasticTransition trans = (StochasticTransition) ab;
						// Create the combo box, select item at index 4.
						// Indices start at 0, so 4 specifies the pig.
						final var distributionList = new JComboBox<>(StochasticDistribution.DISTRIBUTIONS);
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
						h.setText(String.valueOf(trans.getH()));
						h.addFocusListener(pwl);
						h.setFocusLostBehavior(JFormattedTextField.COMMIT);
						h.setName("h");

						MyJFormattedTextField a = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						JLabel lblA = new JLabel("a: minimum value");
						a.setText(String.valueOf(trans.getA()));
						a.addFocusListener(pwl);
						a.setFocusLostBehavior(JFormattedTextField.COMMIT);
						a.setName("a");

						MyJFormattedTextField b = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						JLabel lblB = new JLabel("b: maximum value");
						b.setText(String.valueOf(trans.getB()));
						b.addFocusListener(pwl);
						b.setFocusLostBehavior(JFormattedTextField.COMMIT);
						b.setName("b");

						MyJFormattedTextField c = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						JLabel lblC = new JLabel("c: most likely value");
						c.setText(String.valueOf(trans.getC()));
						c.addFocusListener(pwl);
						c.setFocusLostBehavior(JFormattedTextField.COMMIT);
						c.setName("c");

						MyJFormattedTextField mu = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						JLabel lblMu = new JLabel("mu: expected value");
						mu.setText(String.valueOf(trans.getMu()));
						mu.addFocusListener(pwl);
						mu.setFocusLostBehavior(JFormattedTextField.COMMIT);
						mu.setName("mu");

						MyJFormattedTextField sigma = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
						JLabel lblSigma = new JLabel("sigma: std. deviation");
						sigma.setText(String.valueOf(trans.getSigma()));
						sigma.addFocusListener(pwl);
						sigma.setFocusLostBehavior(JFormattedTextField.COMMIT);
						sigma.setName("sigma");

						JTextField events = new JTextField(4);
						JLabel lblEvents = new JLabel("discrete events");
						events.setText(String.valueOf(trans.getEvents()));
						events.setToolTipText("List of discrete events [1,2,...,n]");
						events.addFocusListener(pwl);
						events.setName("events");

						JTextField probabilities = new JTextField(4);
						JLabel lblProbabilities = new JLabel("probabilities");
						probabilities.setText(String.valueOf(trans.getProbabilities()));
						probabilities.setToolTipText("List of probabilities [1/n,1/n,...,1/n]");
						probabilities.addFocusListener(pwl);
						probabilities.setName("probabilities");

						switch (trans.getDistribution()) {
						case Exponential:
							p.add(lblH, "gap 5");
							p.add(h, "wrap");
							break;
						case Triangular:
							p.add(lblA, "gap 5");
							p.add(a, "wrap");
							p.add(lblB, "gap 5");
							p.add(b, "wrap");
							p.add(lblC, "gap 5");
							p.add(c, "wrap");
							break;
						case TruncatedNormal:
							p.add(lblA, "gap 5");
							p.add(a, "wrap");
							p.add(lblB, "gap 5");
							p.add(b, "wrap");
							p.add(lblMu, "gap 5");
							p.add(mu, "wrap");
							p.add(lblSigma, "gap 5");
							p.add(sigma, "wrap");
							break;
						case Uniform:
							p.add(lblA, "gap 5");
							p.add(a, "wrap");
							p.add(lblB, "gap 5");
							p.add(b, "wrap");
							break;
						case DiscreteProbability:
							p.add(lblEvents, "gap 5");
							p.add(events, "wrap");
							p.add(lblProbabilities, "gap 5");
							p.add(probabilities, "wrap");
							break;
						}
						if (trans.isKnockedOut()) {
							distributionList.setEnabled(false);
							h.setEnabled(false);
							a.setEnabled(false);
							b.setEnabled(false);
							c.setEnabled(false);
							mu.setEnabled(false);
							sigma.setEnabled(false);
						}
					}
					Transition t = (Transition) ab;

					if (t.isKnockedOut()) {
						firingCondition.setEnabled(false);
					}

					knockedOut.setSelected(t.isKnockedOut());
					knockedOut.setToolTipText("Knock out");
					knockedOut.setActionCommand("knockedOut");
					knockedOut.addActionListener(this);
					p.add(new JLabel("Knocked out"), "gap 5");
					p.add(knockedOut, "wrap, span 1");
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

				JTextField function = new JTextField(5);
				function.setText(((BiologicalEdgeAbstract) ab).getFunction());
				function.setName("function");
				function.addFocusListener(pwl);
				String labelText = "Edge weight";
				if (ab instanceof PNArc) {
					labelText = "Arc weight";
				}
				JLabel lblpassingTokens = new JLabel(labelText);
				lblpassingTokens.setToolTipText(labelText + " / function");
				p.add(lblpassingTokens, "gap 5");
				p.add(function, "wrap");

				// String[] types = { "discrete", "continuous", "inhibition" };
				// Create the combo box, select item at index 4.
				// Indices start at 0, so 4 specifies the pig.
				// JLabel typeList = new JComboBox(types);

				JButton changeEdgeDirection = new JButton("Change direction");
				changeEdgeDirection.setActionCommand("changeEdgeDirection");
				changeEdgeDirection.addActionListener(this);
				if (((BiologicalEdgeAbstract) ab).isDirected()) {
					if (ab instanceof PNArc) {
						PNArc pnedge = (PNArc) ab;
						if (!pnedge.isInhibitorArc() && !pnedge.isTestArc()) {
							p.add(changeEdgeDirection, "wrap");
						}
					} else {
						p.add(changeEdgeDirection, "wrap");
					}
				}

				if (ab instanceof PNArc) {
					final PNArc e = (PNArc) ab;
					final var activationProb = new MyJFormattedTextField(MyNumberFormat.getDecimalFormat());
					activationProb.setText(String.valueOf(e.getProbability()));
					activationProb.setName("activationProb");
					activationProb.addFocusListener(pwl);
					activationProb.setEnabled(e.isRegularArc());
					final var activationPrio = new MyJFormattedTextField(MyNumberFormat.getIntegerFormat());
					activationPrio.setText(String.valueOf(e.getPriority()));
					activationPrio.setName("activationPrio");
					activationPrio.addFocusListener(pwl);
					if (e.getFrom() instanceof Place) {
						final Place place = (Place) e.getFrom();
						if (place.getConflictingOutEdges().size() > 1) {
							p.add(new JLabel("Conflict solving strategy:"), "gap 5");
							if (place.getConflictStrategy() == ConflictHandling.Priority) {
								p.add(new JLabel("priorities"), "gap 5, wrap");
								p.add(new JLabel("Activation priority"), "gap 5");
								p.add(activationPrio, "wrap");
							} else if (place.getConflictStrategy() == ConflictHandling.Probability) {
								p.add(new JLabel("probabilities"), "gap 5, wrap");
								p.add(new JLabel("Activation probability"), "gap 5");
								p.add(activationProb, "wrap");
							}
						}
					} else if (e.getTo() instanceof Place) {
						final Place place = (Place) e.getTo();
						if (place.getConflictingInEdges().size() > 1) {
							p.add(new JLabel("Conflict solving strategy:"), "gap 5");
							if (place.getConflictStrategy() == ConflictHandling.Priority) {
								p.add(new JLabel("priorities"), "gap 5, wrap");
								p.add(new JLabel("Activation priority"), "gap 5");
								p.add(activationPrio, "wrap");
							} else if (place.getConflictStrategy() == ConflictHandling.Probability) {
								p.add(new JLabel("probabilities"), "gap 5, wrap");
								p.add(new JLabel("Activation probability"), "gap 5");
								p.add(activationProb, "wrap");
							}
						}
					}
				} else {
					isDirected.setSelected(((BiologicalEdgeAbstract) ab).isDirected());
					isDirected.setToolTipText("Is directed");
					isDirected.setActionCommand("isDirected");
					isDirected.addActionListener(this);
					p.add(new JLabel("Is directed"), "gap 5, split 2");
					p.add(isDirected, "wrap");

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
				}
			}

			if (ab instanceof DynamicNode) {
				DynamicNode node = (DynamicNode) ab;
				JTextField maxSpeed = new JTextField(4);
				JLabel lblMaxSpeed = new JLabel("Maximal speed");
				maxSpeed.setText(node.getMaximalSpeed());
				maxSpeed.setName("maximalSpeed");
				maxSpeed.addFocusListener(pwl);

				p.add(lblMaxSpeed, "gap 5");
				p.add(maxSpeed, "wrap");

				if (node.isKnockedOut()) {
					maxSpeed.setEnabled(false);
				}

				knockedOut.setSelected(((DynamicNode) ab).isKnockedOut());
				knockedOut.setToolTipText("Knock out");
				knockedOut.setActionCommand("knockedOut");
				knockedOut.addActionListener(this);
				p.add(new JLabel("Knocked out"), "gap 5");
				p.add(knockedOut, "wrap, span 1");
			}

			if (ab.isVertex()) {
				hideNeighbours.setToolTipText("Hide all neighboring nodes");
				hideNeighbours.setActionCommand("hideNeighbours");
				hideNeighbours.addActionListener(this);
				hideNeighbours.setMaximumSize(new Dimension(120, 30));
				hideNeighbours.setEnabled(false);
				showNeighbours.setToolTipText("Show all neighboring nodes");
				showNeighbours.setActionCommand("showNeighbours");
				showNeighbours.addActionListener(this);
				showNeighbours.setMaximumSize(new Dimension(120, 30));
				showNeighbours.setEnabled(false);
				p.add(showNeighbours, "flowx");
				p.add(hideNeighbours, "flowx, split 2");
			}

			if (ab instanceof DynamicNode || ab instanceof BiologicalEdgeAbstract || ab instanceof ContinuousTransition
					|| ab instanceof DiscreteTransition) {
				parametersButton.setText("Function Builder");
				parametersButton.setToolTipText("Build function and edit parameters");
			} else {
				parametersButton.setText("Parameters");
				parametersButton.setToolTipText("Edit parameters");
			}

			parametersButton.setActionCommand("showParameters");
			parametersButton.addActionListener(this);
			p.add(parametersButton, "wrap");

			if (ab instanceof BiologicalNodeAbstract) {
				p.add(plotColorButton, "");
			}
			p.add(fillColorButton, "wrap");
		} else if (element instanceof GraphicalElementAbstract) {
			// TODO show menu
			// System.out.println("annotation selected");
		}
	}

	public JPanel getPanel() {
		p.setVisible(false);
		return p;
	}

	public void revalidateView() {
		final GraphicalElementAbstract selectedElement = GraphInstance.getSelectedObject();
		if (selectedElement instanceof BiologicalNodeAbstract
				&& ((BiologicalNodeAbstract) selectedElement).isLogical()) {
			this.ref = ((BiologicalNodeAbstract) selectedElement).getLogicalReference();
		} else {
			this.ref = null;
		}
		// dirty hack that pane is always empty
		this.removeAllElements();

		if (emptyPane) {
			updateWindow(selectedElement);
			p.setVisible(true);
			p.repaint();
			p.revalidate();
			emptyPane = false;
		} else {
			final Thread worker = new Thread(() -> {
				// dirty hack
				try {
					p.removeAll();
					// removeAllElements();
				} catch (Exception e) {
					e.printStackTrace();
					revalidateView();
				}
				SwingUtilities.invokeLater(() -> {
					updateWindow(selectedElement);
					p.setVisible(true);
					p.repaint();
					p.revalidate();
				});
			});
			worker.start();
		}
	}

	private void addCompartmentItems(JComboBox<String> compartment) {
		Pathway pw = GraphInstance.getPathway();
		if (pw == null) {
			return;
		}
		List<Compartment> compartmentList = pw.getCompartmentManager().getAllCompartmentsAlphabetically();
		compartment.addItem(" ");
		for (Compartment value : compartmentList) {
			compartment.addItem(value.getName());
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
		Pathway pw = GraphInstance.getPathway();
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
				// System.out.println(bna.getDefaultColor());
				// CHRIS plot color = null if BN with loaded PN
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
					"Delete the Sub-Pathway...", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
					== JOptionPane.YES_OPTION && ab instanceof PathwayMap) {
				((PathwayMap) ab).setPathwayLink(null);
				w.updateElementTree();
				w.updatePathwayTree();
				ab.setColor(Color.white);
			}
			w.updateElementProperties();
		} else if (("hideNeighbours".equals(event) || ("showNeighbours".equals(event)))
				&& ab instanceof BiologicalNodeAbstract) {
			// CHRIS visible wird noch nicht gehandelt in transformators
			boolean hide = "hideNeighbours".equals(event);
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ab;
			for (BiologicalEdgeAbstract bea : pw.getGraph().getJungGraph().getIncidentEdges(bna)) {
				bea.setVisible(!hide);
				// bea.setLabel(!hide+"");
			}
			for (BiologicalNodeAbstract node : pw.getGraph().getJungGraph().getNeighbors(bna)) {
				node.setVisible(!hide);
			}
		} else if ("changeEdgeDirection".equals(event) && ab.isEdge()) {
			PNArc edge = (PNArc) ab;
			PNArc newEdge = new PNArc(edge.getTo(), edge.getFrom(), edge.getLabel(), edge.getName(),
					edge.getBiologicalElement(), edge.getFunction());
			newEdge.setPriority(edge.getPriority());
			newEdge.setProbability(edge.getProbability());
			pw.removeElement(edge);
			pw.addEdge(newEdge);
			pw.updateMyGraph();
			pw.getGraph().getVisualizationViewer().getPickedEdgeState().clear();
			pw.getGraph().getVisualizationViewer().getPickedEdgeState().pick(newEdge, true);
			GraphInstance.setSelectedObject(newEdge);
			ab = newEdge;
		} else if ("chooseRef".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			ReferenceDialog dialog = new ReferenceDialog(bna);
			BiologicalNodeAbstract node = dialog.getAnswer();
			if (node != null) {
				bna.setLogicalReference(node);
				revalidateView();
				w.updateElementTree();
			}
		} else if ("deleteRef".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			bna.deleteLogicalReference();
			revalidateView();
			w.updateElementTree();
		} else if ("pickOrigin".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			MyGraph g = pw.getGraph();
			g.getVisualizationViewer().getPickedVertexState().pick(bna.getLogicalReference(), true);
			revalidateView();
		} else if ("pickRefs".equals(event)) {
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) original;
			MyGraph g = pw.getGraph();
			for (BiologicalNodeAbstract pick : bna.getRefs()) {
				g.getVisualizationViewer().getPickedVertexState().pick(pick, true);
			}
			revalidateView();
		} else if ("showParameters".equals(event)) {
			new ParameterWindow(ab);
			updateWindow(ab);
			p.revalidate();
		} else if ("showLabels".equals(event)) {
			new LabelsWindow(ab);
		} else if ("knockedOut".equals(event)) {
			if (ab instanceof DynamicNode) {
				((DynamicNode) ab).setKnockedOut(knockedOut.isSelected());
			} else if (ab instanceof Transition) {
				((Transition) ab).setKnockedOut(knockedOut.isSelected());
			}
			updateWindow(ab);
			p.revalidate();
			pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
		} else if ("isDirected".equals(event)) {
			if (ab instanceof BiologicalEdgeAbstract) {
				((BiologicalEdgeAbstract) ab).setDirected(isDirected.isSelected());
				pw.updateMyGraph();
				pw.getGraph().getVisualizationViewer().repaint();
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
				pw.handleChangeFlags(ChangedFlags.EDGEWEIGHT_CHANGED);
			}
		} else if ("conflict_prio".equals(event)) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				if (place.getConflictStrategy() != ConflictHandling.Priority) {
					place.setConflictStrategy(ConflictHandling.Priority);
					revalidateView();
				}
			}
		} else if ("conflict_prob".equals(event)) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				if (place.getConflictStrategy() != ConflictHandling.Probability) {
					place.setConflictStrategy(ConflictHandling.Probability);
					revalidateView();
				}
			}
		} else if ("solve".equals(event)) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				place.solveConflictProperties();
			}
		} else if ("check".equals(event)) {
			String result = "";
			for (BiologicalNodeAbstract bna : GraphInstance.getMyGraph().getAllVertices()) {
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
			PopUpDialog.getInstance().show("Checking conflicts", message);
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
		if (ab.isVertex()) {
			JComboBox<String> compartment = (JComboBox<String>) event.getSource();
			Pathway pw = GraphInstance.getPathway();
			pw.getCompartmentManager().setCompartment((BiologicalNodeAbstract) ab,
					pw.getCompartmentManager().getCompartment(compartment.getSelectedItem().toString()));
		}
	}

	public void redrawTokens() {
		if (ab instanceof BiologicalNodeAbstract) {
			if (ab instanceof Place) {
				Place place = (Place) ab;
				if (place.isDiscrete()) {
					token.setText(String.valueOf((int) place.getToken()));
				} else {
					token.setText(String.valueOf(place.getToken()));
				}
				// token.repaint();
			} else {
				Pathway pw = GraphInstance.getPathway();
				if (pw.getTransformationInformation() != null) {
					if (pw.getTransformationInformation().getBnToPnMapping().containsKey(ab)) {
						BiologicalNodeAbstract node = pw.getTransformationInformation().getBnToPnMapping().get(ab);
						if (node instanceof Place) {
							if (concentration != null) {
								concentration.setText(String.valueOf(((Place) node).getToken()));
							}
						}
					}
				}
			}
		}
	}
}
